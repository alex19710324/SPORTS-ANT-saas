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
            <el-table-column prop="reorderPoint" label="Reorder Point" width="120" />
            <el-table-column prop="sellPrice" label="Sell Price">
                <template #default="scope">¥{{ scope.row.sellPrice }}</template>
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
            <el-button type="primary" :loading="loading" @click="submitRestock">Confirm Stock In</el-button>
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

const lowStockCount = computed(() => items.value.filter(i => i.quantity <= i.reorderPoint).length);
// Mock total value calculation
const totalValue = computed(() => items.value.reduce((sum, i) => sum + (i.quantity * (i.costPrice || 0)), 0).toFixed(2));

const fetchInventory = async () => {
    try {
        const res = await apiClient.get('/inventory');
        items.value = res.data;
    } catch (error) {
        ElMessage.error('Failed to load inventory');
    }
};

const fetchLowStock = async () => {
    // Client-side filter for MVP
    const lowItems = items.value.filter(i => i.quantity <= i.reorderPoint);
    if (lowItems.length > 0) {
        ElMessage.warning(`Found ${lowItems.length} low stock items!`);
    } else {
        ElMessage.success('All stock levels healthy');
    }
};

const getStockStatus = (item: any) => {
    if (item.quantity <= 0) return 'danger';
    if (item.quantity <= item.reorderPoint) return 'warning';
    return 'success';
};

const handleRestock = (item: any) => {
    selectedItem.value = item;
    restockAmount.value = 10;
    showRestockModal.value = true;
};

const handleAdjust = (item: any) => {
    ElMessage.info('Manual adjustment feature coming soon for: ' + item.name);
};

const submitRestock = async () => {
    if (!selectedItem.value) return;
    loading.value = true;
    try {
        await apiClient.post('/inventory/adjust', {
            sku: selectedItem.value.sku,
            quantityChange: restockAmount.value,
            reason: 'MANUAL_RESTOCK'
        });
        ElMessage.success('Stock updated');
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
