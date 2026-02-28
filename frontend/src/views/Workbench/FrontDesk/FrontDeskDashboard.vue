<template>
  <div class="frontdesk-dashboard" v-loading="loading">
    <h2>{{ $t('frontdesk.title') }}</h2>
    <div class="task-grid" v-if="tasks">
      <el-card shadow="hover">
        <template #header>{{ $t('frontdesk.target') }}</template>
        <h3>¥{{ tasks.todayTarget }}</h3>
        <el-progress :percentage="tasks.todayTarget > 0 ? Math.min((tasks.currentSales / tasks.todayTarget) * 100, 100) : 0" 
                     :status="getProgressStatus(tasks.currentSales / tasks.todayTarget)"></el-progress>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('frontdesk.sales') }}</template>
        <h3>¥{{ tasks.currentSales }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('frontdesk.checkins') }}</template>
        <h3>{{ tasks.pendingCheckins }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('frontdesk.verifications') }}</template>
        <h3>{{ tasks.pendingVerifications }}</h3>
      </el-card>
    </div>
    
    <div class="quick-actions">
      <el-button type="primary" size="large" @click="handleQuickCheckin">{{ $t('frontdesk.quickCheckin') }}</el-button>
      <el-button type="success" size="large" @click="handleNewSale">{{ $t('frontdesk.newSale') }}</el-button>
      <el-button type="warning" size="large" @click="handleRegister">{{ $t('frontdesk.memberReg') }}</el-button>
    </div>

    <!-- Quick Checkin Modal -->
    <el-dialog v-model="showCheckinModal" :title="$t('frontdesk.quickCheckin')">
        <el-input v-model="checkinCode" placeholder="Scan Ticket / QR Code / Phone" autofocus @keyup.enter="performCheckin"></el-input>
        <template #footer>
            <el-button @click="showCheckinModal = false">Cancel</el-button>
            <el-button type="primary" :loading="checkinLoading" @click="performCheckin">Verify</el-button>
        </template>
    </el-dialog>

    <!-- New Sale Modal (Smart POS) -->
    <el-dialog v-model="showSaleModal" :title="$t('frontdesk.newSale')" width="800px" @open="initPOS">
        <div class="pos-container">
            <div class="product-list">
                <el-input v-model="productSearch" placeholder="Search Product" prefix-icon="Search" style="margin-bottom: 10px" />
                <el-scrollbar height="400px">
                    <div class="product-grid">
                        <div v-for="prod in filteredProducts" :key="prod.id" class="product-card" @click="addToCart(prod)">
                            <div class="prod-name">{{ prod.name }}</div>
                            <div class="prod-price">¥{{ prod.sellPrice || 0 }}</div>
                            <el-tag size="small" :type="prod.quantity > 0 ? 'success' : 'danger'">{{ prod.quantity > 0 ? 'In Stock' : 'Out' }}</el-tag>
                        </div>
                    </div>
                </el-scrollbar>
            </div>
            
            <div class="cart-panel">
                <div class="cart-header">
                    <span>Current Cart</span>
                    <el-button link type="danger" @click="clearCart">Clear</el-button>
                </div>
                <el-scrollbar height="300px">
                    <div v-for="(item, idx) in cart" :key="idx" class="cart-item">
                        <div class="item-info">
                            <div>{{ item.name }}</div>
                            <small>¥{{ item.sellPrice }} x {{ item.qty }}</small>
                        </div>
                        <div class="item-total">¥{{ (item.sellPrice * item.qty).toFixed(2) }}</div>
                        <el-button icon="Delete" circle size="small" type="danger" @click="removeFromCart(idx)" />
                    </div>
                </el-scrollbar>
                <div class="cart-footer">
                    <div class="total-row">
                        <span>Total:</span>
                        <span class="total-amount">¥{{ cartTotal }}</span>
                    </div>
                    <el-input v-model="saleForm.code" placeholder="Scan Member Code" style="margin-bottom: 10px" />
                    <el-select v-model="saleForm.method" placeholder="Payment Method" style="width: 100%; margin-bottom: 10px">
                        <el-option label="Wallet Balance" value="WALLET" />
                        <el-option label="WeChat Pay" value="WECHAT" />
                        <el-option label="Alipay" value="ALIPAY" />
                        <el-option label="Cash" value="CASH" />
                    </el-select>
                    <el-button type="success" style="width: 100%" :loading="saleLoading" :disabled="cart.length === 0" @click="performCartSale">Checkout</el-button>
                </div>
            </div>
        </div>
    </el-dialog>

    <!-- Member Registration Modal -->
    <el-dialog v-model="showRegisterModal" :title="$t('frontdesk.memberReg')">
        <el-form :model="regForm" label-width="100px">
            <el-form-item label="Name" required>
                <el-input v-model="regForm.name" placeholder="Member Name"></el-input>
            </el-form-item>
            <el-form-item label="Phone" required>
                <el-input v-model="regForm.phone" placeholder="Phone Number"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showRegisterModal = false">Cancel</el-button>
            <el-button type="primary" :loading="regLoading" @click="performRegister">Register</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useWorkbenchStore } from '../../../stores/workbench.store';
import { ElMessage } from 'element-plus';
import { Search, Delete } from '@element-plus/icons-vue';
import apiClient from '../../../services/api';

const store = useWorkbenchStore();
const tasks = computed(() => store.frontDeskTasks);
const loading = ref(false);
const showCheckinModal = ref(false);
const showRegisterModal = ref(false);
const showSaleModal = ref(false);
const saleLoading = ref(false);
const regLoading = ref(false);
const checkinLoading = ref(false);

const productSearch = ref('');
const products = ref<any[]>([]);
const cart = ref<any[]>([]);

const checkinCode = ref('');
const regForm = reactive({
    name: '',
    phone: ''
});
const saleForm = reactive({
    code: '',
    amount: 0,
    method: 'WALLET'
});

const filteredProducts = computed(() => {
    if (!productSearch.value) return products.value;
    return products.value.filter(p => p.name.toLowerCase().includes(productSearch.value.toLowerCase()));
});

const cartTotal = computed(() => {
    return cart.value.reduce((sum, item) => sum + (item.sellPrice * item.qty), 0).toFixed(2);
});

onMounted(async () => {
    loading.value = true;
    await store.fetchFrontDeskTasks();
    loading.value = false;
});

const getProgressStatus = (val: number) => {
    if (val >= 1) return 'success';
    if (val >= 0.8) return 'warning';
    return 'exception';
};

const handleQuickCheckin = () => {
    showCheckinModal.value = true;
};

const handleNewSale = () => {
    showSaleModal.value = true;
};

const handleRegister = () => {
    showRegisterModal.value = true;
};

const performCheckin = async () => {
    if (!checkinCode.value) return;
    checkinLoading.value = true;
    try {
        await store.checkInMember(checkinCode.value);
        ElMessage.success('Check-in Successful');
        showCheckinModal.value = false;
        checkinCode.value = '';
        store.fetchFrontDeskTasks();
    } catch (e) {
        ElMessage.error('Check-in Failed');
    } finally {
        checkinLoading.value = false;
    }
};

const performRegister = async () => {
    regLoading.value = true;
    try {
        await store.registerMember(regForm.name, regForm.phone);
        ElMessage.success('Member Registered');
        showRegisterModal.value = false;
    } catch (e) {
        ElMessage.error('Registration Failed');
    } finally {
        regLoading.value = false;
    }
};

const initPOS = async () => {
    try {
        const res = await apiClient.get('/inventory');
        products.value = res.data;
    } catch (e) {
        ElMessage.error('Failed to load products');
    }
};

const addToCart = (prod: any) => {
    if (prod.quantity <= 0) {
        ElMessage.warning('Out of stock');
        return;
    }
    const existing = cart.value.find(i => i.id === prod.id);
    if (existing) {
        if (existing.qty < prod.quantity) {
            existing.qty++;
        } else {
            ElMessage.warning('Max stock reached');
        }
    } else {
        // Create a copy to avoid reactive issues with original product list
        cart.value.push({ ...prod, qty: 1 });
    }
};

const removeFromCart = (idx: number) => {
    cart.value.splice(idx, 1);
};

const clearCart = () => {
    cart.value = [];
};

const performCartSale = async () => {
    if (!saleForm.code) {
        ElMessage.warning('Please scan member code');
        return;
    }
    saleLoading.value = true;
    try {
        await store.processCartSale(saleForm.code, cart.value, saleForm.method);
        ElMessage.success('Sale Completed!');
        showSaleModal.value = false;
        cart.value = [];
        saleForm.code = '';
        store.fetchFrontDeskTasks();
    } catch (e) {
        ElMessage.error('Sale Failed');
    } finally {
        saleLoading.value = false;
    }
};

</script>

<style scoped>
.task-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 20px;
}
.quick-actions {
  margin-top: 30px;
  display: flex;
  gap: 15px;
}
</style>
