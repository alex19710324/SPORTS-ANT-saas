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

    <!-- New Sale Modal -->
    <el-dialog v-model="showSaleModal" :title="$t('frontdesk.newSale')">
        <el-form :model="saleForm" label-width="100px">
            <el-form-item label="Member" required>
                <el-input v-model="saleForm.code" placeholder="Scan Member Code / Phone"></el-input>
            </el-form-item>
            <el-form-item label="Amount" required>
                <el-input-number v-model="saleForm.amount" :min="0" :precision="2" :step="10"></el-input-number>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showSaleModal = false">Cancel</el-button>
            <el-button type="success" :loading="saleLoading" @click="performSale">Confirm Sale</el-button>
        </template>
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
import { computed, onMounted, reactive, ref } from 'vue';
import { useWorkbenchStore } from '../../../stores/workbench.store';
import { ElMessage, ElNotification } from 'element-plus';

const store = useWorkbenchStore();
const tasks = computed(() => store.frontDeskTasks);
const loading = computed(() => store.loading);

// Check-in
const showCheckinModal = ref(false);
const checkinCode = ref('');
const checkinLoading = ref(false);

// Registration
const showRegisterModal = ref(false);
const regLoading = ref(false);
const regForm = reactive({
    name: '',
    phone: ''
});

// Sale
const showSaleModal = ref(false);
const saleLoading = ref(false);
const saleForm = reactive({
    code: '',
    amount: 0
});

onMounted(() => {
  store.fetchFrontDeskTasks();
});

const handleQuickCheckin = () => {
    checkinCode.value = '';
    showCheckinModal.value = true;
};

const handleNewSale = () => {
    saleForm.code = '';
    saleForm.amount = 0;
    showSaleModal.value = true;
};

const handleRegister = () => {
    regForm.name = '';
    regForm.phone = '';
    showRegisterModal.value = true;
};

const performRegister = async () => {
    if (!regForm.name || !regForm.phone) {
        ElMessage.warning('Name and Phone are required.');
        return;
    }
    regLoading.value = true;
    try {
        const res = await store.registerMember(regForm.name, regForm.phone);
        const member = res.data;
        ElNotification({
            title: 'Registration Successful',
            message: `Member ${member.name} registered. Code: ${member.memberCode}`,
            type: 'success',
            duration: 5000
        });
        showRegisterModal.value = false;
    } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Registration failed.');
    } finally {
        regLoading.value = false;
    }
};

const performSale = async () => {
    if (!saleForm.code || !saleForm.amount) {
        ElMessage.warning('Member Code and Amount are required.');
        return;
    }
    saleLoading.value = true;
    try {
        const res = await store.processSale(saleForm.code, saleForm.amount);
        const member = res.data;
        ElNotification({
            title: 'Sale Successful',
            message: `Sale recorded for ${member.name}. Growth: ${member.growthValue}`,
            type: 'success',
            duration: 5000
        });
        showSaleModal.value = false;
        // Refresh tasks
        store.fetchFrontDeskTasks();
    } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Sale failed.');
    } finally {
        saleLoading.value = false;
    }
};

const performCheckin = async () => {
    if (!checkinCode.value) return;
    checkinLoading.value = true;
    try {
        const res = await store.checkInMember(checkinCode.value);
        const member = res.data;
        ElNotification({
            title: 'Check-in Successful',
            message: `Member: ${member.name} (Level: ${member.currentLevel.name}) - Growth +10`,
            type: 'success',
            duration: 5000
        });
        showCheckinModal.value = false;
        // Refresh tasks
        store.fetchFrontDeskTasks();
    } catch (error) {
        ElMessage.error('Check-in failed. Member not found or invalid code.');
    } finally {
        checkinLoading.value = false;
    }
};

const getProgressStatus = (val: number) => {
    if (val >= 1) return 'success';
    if (val >= 0.8) return 'warning';
    return 'exception';
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
