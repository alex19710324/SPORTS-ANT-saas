<template>
  <div class="inventory-dashboard">
    <div class="header">
        <h2>Inventory Management</h2>
        <div class="actions">
            <el-button type="warning" @click="fetchLowStock">Check Low Stock</el-button>
            <el-button type="primary" @click="showAddModal = true">Add Item</el-button>
        </div>
    </div>

    <div class="stats-row">
        <el-card shadow="hover">
            <template #header>Total Items</template>
            <h3>{{ items.length }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Low Stock Alerts</template>
            <h3 class="text-danger">{{ lowStockCount }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Total Value</template>
            <h3>¥{{ totalValue }}</h3>
        </el-card>
    </div>

    <el-card>
        <el-table :data="items" style="width: 100%" stripe>
            <el-table-column prop="sku" label="SKU" width="120" />
            <el-table-column prop="name" label="Item Name" />
            <el-table-column prop="category" label="Category" />
            <el-table-column prop="quantity" label="Stock" sortable>
                <template #default="scope">
                    <el-tag :type="getStockStatus(scope.row)">{{ scope.row.quantity }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="threshold" label="Threshold" width="100" />
            <el-table-column prop="unitPrice" label="Unit Price">
                <template #default="scope">¥{{ scope.row.unitPrice }}</template>
            </el-table-column>
            <el-table-column label="Actions" width="200">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="handleRestock(scope.row)">Restock</el-button>
                    <el-button size="small" @click="handleAdjust(scope.row)">Adjust</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>

    <!-- Restock Modal -->
    <el-dialog v-model="showRestockModal" title="Restock Item">
        <p>Restocking: <strong>{{ selectedItem?.name }}</strong></p>
        <el-input-number v-model="restockAmount" :min="1" />
        <template #footer>
            <el-button @click="showRestockModal = false">Cancel</el-button>
            <el-button type="primary" :loading="loading" @click="submitRestock">Confirm Order</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';

const items = ref<any[]>([]);
const loading = ref(false);
const showRestockModal = ref(false);
const selectedItem = ref<any>(null);
const restockAmount = ref(10);
const showAddModal = ref(false);

const lowStockCount = computed(() => items.value.filter(i => i.quantity <= i.threshold).length);
const totalValue = computed(() => items.value.reduce((sum, i) => sum + (i.quantity * i.unitPrice), 0).toFixed(2));

const fetchInventory = async () => {
    try {
        const res = await apiClient.get('/inventory', { params: { storeId: 1 } });
        items.value = res.data;
    } catch (error) {
        ElMessage.error('Failed to load inventory');
    }
};

const fetchLowStock = async () => {
    try {
        const res = await apiClient.get('/inventory/low-stock', { params: { storeId: 1 } });
        const lowItems = res.data;
        ElMessage.warning(`Found ${lowItems.length} low stock items!`);
        // Highlight logic could go here
    } catch (error) {
        ElMessage.error('Failed to check low stock');
    }
};

const getStockStatus = (item: any) => {
    if (item.quantity <= 0) return 'danger';
    if (item.quantity <= item.threshold) return 'warning';
    return 'success';
};

const handleRestock = (item: any) => {
    selectedItem.value = item;
    restockAmount.value = item.threshold * 2; // Suggest 2x threshold
    showRestockModal.value = true;
};

const handleAdjust = (item: any) => {
    // Implement manual adjustment logic
    ElMessage.info('Manual adjustment feature coming soon');
};

const submitRestock = async () => {
    if (!selectedItem.value) return;
    loading.value = true;
    try {
        await apiClient.post('/inventory/adjust', {
            storeId: 1,
            sku: selectedItem.value.sku,
            quantity: restockAmount.value
        });
        ElMessage.success('Restock successful');
        showRestockModal.value = false;
        fetchInventory();
    } catch (error) {
        ElMessage.error('Restock failed');
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchInventory();
});
</script>

<style scoped>
.inventory-dashboard {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.stats-row {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin-bottom: 20px;
}
.text-danger { color: #f56c6c; }
</style>
