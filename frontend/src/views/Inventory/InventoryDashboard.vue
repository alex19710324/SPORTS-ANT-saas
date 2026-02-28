<template>
  <div class="inventory-dashboard">
    <div class="header">
        <h2>Inventory Management</h2>
        <div class="actions">
            <el-button type="primary" @click="showAddItemDialog = true">Add New Item</el-button>
        </div>
    </div>

    <div class="stats-cards">
        <el-card shadow="hover">
            <template #header>Total Items</template>
            <h3>{{ inventory.length }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Low Stock Alerts</template>
            <h3 class="text-danger">{{ lowStockCount }}</h3>
        </el-card>
    </div>

    <el-card class="inventory-list">
        <el-table :data="inventory" style="width: 100%" stripe>
            <el-table-column prop="sku" label="SKU" width="120" />
            <el-table-column prop="name" label="Name" />
            <el-table-column prop="category" label="Category" width="120">
                <template #default="scope">
                    <el-tag :type="getCategoryType(scope.row.category)">{{ scope.row.category }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="quantity" label="Quantity">
                <template #default="scope">
                    <span :class="{ 'text-danger': scope.row.quantity <= scope.row.threshold }">
                        {{ scope.row.quantity }}
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="unitPrice" label="Unit Price">
                <template #default="scope">¥{{ scope.row.unitPrice }}</template>
            </el-table-column>
            <el-table-column label="Actions" width="200">
                <template #default="scope">
                    <el-button size="small" @click="handleStock(scope.row, 1)">+1</el-button>
                    <el-button size="small" type="danger" @click="handleStock(scope.row, -1)">-1</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>

    <!-- Add Item Dialog -->
    <el-dialog v-model="showAddItemDialog" title="Add New Item">
        <el-form :model="newItem" label-width="100px">
            <el-form-item label="Name">
                <el-input v-model="newItem.name" />
            </el-form-item>
            <el-form-item label="SKU">
                <el-input v-model="newItem.sku" />
            </el-form-item>
            <el-form-item label="Category">
                <el-select v-model="newItem.category">
                    <el-option label="Retail" value="RETAIL" />
                    <el-option label="Spare Part" value="SPARE_PART" />
                    <el-option label="Asset" value="ASSET" />
                </el-select>
            </el-form-item>
            <el-form-item label="Quantity">
                <el-input-number v-model="newItem.quantity" :min="0" />
            </el-form-item>
            <el-form-item label="Threshold">
                <el-input-number v-model="newItem.threshold" :min="0" />
            </el-form-item>
            <el-form-item label="Unit Price">
                <el-input-number v-model="newItem.unitPrice" :min="0" :precision="2" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showAddItemDialog = false">Cancel</el-button>
            <el-button type="primary" @click="submitNewItem">Create</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';

const inventory = ref<any[]>([]);
const showAddItemDialog = ref(false);

const newItem = ref({
    store: { id: 1 }, // Mock Store ID
    name: '',
    sku: '',
    category: 'RETAIL',
    quantity: 0,
    threshold: 5,
    unitPrice: 0.0
});

const lowStockCount = computed(() => {
    return inventory.value.filter(item => item.quantity <= item.threshold).length;
});

const fetchInventory = async () => {
    try {
        const res = await apiClient.get('/inventory', { params: { storeId: 1 } });
        inventory.value = res.data;
    } catch (error) {
        ElMessage.error('Failed to load inventory');
    }
};

const handleStock = async (item: any, change: number) => {
    try {
        await apiClient.post('/inventory/adjust', {
            storeId: 1,
            sku: item.sku,
            quantity: change
        });
        item.quantity += change;
        ElMessage.success('Stock updated');
    } catch (error) {
        ElMessage.error('Failed to update stock');
    }
};

const submitNewItem = async () => {
    try {
        await apiClient.post('/inventory/add', newItem.value);
        ElMessage.success('Item added successfully');
        showAddItemDialog.value = false;
        fetchInventory();
    } catch (error) {
        ElMessage.error('Failed to add item');
    }
};

const getCategoryType = (cat: string) => {
    if (cat === 'RETAIL') return 'success';
    if (cat === 'SPARE_PART') return 'warning';
    return 'info';
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
.stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 20px;
}
.text-danger {
    color: #f56c6c;
    font-weight: bold;
}
</style>
