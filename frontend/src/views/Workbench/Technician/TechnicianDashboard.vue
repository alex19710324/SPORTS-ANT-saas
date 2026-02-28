<template>
  <div class="technician-dashboard" v-loading="loading">
    <h2>{{ $t('technician.title') }}</h2>
    <div class="task-grid" v-if="tasks">
      <el-card shadow="hover">
        <template #header>{{ $t('technician.workOrders') }}</template>
        <h3>{{ tasks.pendingOrders ? tasks.pendingOrders.length : 0 }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('technician.offlineDevices') }}</template>
        <h3 class="text-warning">{{ tasks.offlineDevices ? tasks.offlineDevices.length : 0 }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('technician.inspection') }}</template>
        <h3>{{ tasks.todayInspectionsTotal > 0 ? ((tasks.todayInspectionsCompleted / tasks.todayInspectionsTotal) * 100).toFixed(0) : 0 }}%</h3>
      </el-card>
    </div>
    
    <div class="action-list">
      <el-button type="primary">{{ $t('technician.scanQr') }}</el-button>
      <el-button type="info">{{ $t('technician.reportFault') }}</el-button>
    </div>
    
    <div class="dashboard-split" v-if="tasks">
        <!-- T01: Pending Work Orders -->
        <el-card class="maintenance-card">
            <template #header>
                <div class="card-header">
                    <span>{{ $t('technician.workOrders') }}</span>
                    <el-tag type="danger" v-if="tasks.pendingOrders">{{ tasks.pendingOrders.length }}</el-tag>
                </div>
            </template>
            <el-table :data="tasks.pendingOrders" style="width: 100%" size="small">
                <el-table-column prop="deviceId" label="Device" width="80" />
                <el-table-column prop="description" label="Issue" />
                <el-table-column prop="priority" label="Priority" width="80">
                    <template #default="scope">
                        <el-tag :type="scope.row.priority === 'HIGH' ? 'danger' : 'warning'" size="small">{{ scope.row.priority }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="Action" width="180">
                    <template #default="scope">
                        <el-button v-if="scope.row.status === 'OPEN'" link type="primary" size="small" @click="updateStatus(scope.row.id, 'IN_PROGRESS')">Start</el-button>
                        <el-button v-if="scope.row.status === 'IN_PROGRESS'" link type="success" size="small" @click="openCompleteDialog(scope.row)">Complete</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- NEW: Preventive Maintenance -->
        <el-card class="maintenance-card">
            <template #header>
                <div class="card-header">
                    <span>Preventive Maintenance</span>
                    <el-tag type="success" v-if="preventiveTasks">{{ preventiveTasks.length }}</el-tag>
                </div>
            </template>
            <el-table :data="preventiveTasks" style="width: 100%" size="small">
                <el-table-column prop="device.name" label="Device" />
                <el-table-column prop="description" label="Task" />
                <el-table-column prop="dueDate" label="Due" width="100" />
                <el-table-column label="Action" width="80">
                    <template #default="scope">
                        <el-button link type="primary" size="small" @click="completePreventiveTask(scope.row.id)">Done</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- T03: Offline Devices -->
        <el-card class="inspection-card">
            <template #header>{{ $t('technician.offlineDevices') }}</template>
            <div class="inspection-list" v-if="tasks.offlineDevices">
                <div class="inspection-item" v-for="device in tasks.offlineDevices" :key="device.id">
                    <div class="plan-info">
                        <span class="plan-area">{{ device.name }}</span>
                        <span class="plan-count">{{ device.location }}</span>
                    </div>
                    <el-tag type="info" size="small">Offline</el-tag>
                </div>
            </div>
        </el-card>
    </div>

    <!-- Complete Order Dialog -->
    <el-dialog v-model="showCompleteDialog" title="Complete Work Order">
        <el-form>
            <el-form-item label="Resolution Notes">
                <el-input type="textarea" v-model="completionNotes" />
            </el-form-item>
            
            <el-divider>Spare Parts Used</el-divider>
            
            <div v-for="(part, index) in selectedParts" :key="index" class="part-row">
                <el-select v-model="part.sku" placeholder="Select Part" filterable>
                    <el-option v-for="item in spareParts" :key="item.sku" :label="item.name + ' (' + item.quantity + ')'" :value="item.sku" :disabled="item.quantity <= 0" />
                </el-select>
                <el-input-number v-model="part.quantity" :min="1" size="small" />
                <el-button type="danger" icon="Delete" circle size="small" @click="removePartRow(index)" />
            </div>
            <el-button type="primary" link @click="addPartRow">+ Add Part</el-button>
        </el-form>
        <template #footer>
            <el-button @click="showCompleteDialog = false">Cancel</el-button>
            <el-button type="primary" @click="submitCompletion">Complete Order</el-button>
        </template>
    </el-dialog>

    <DeviceMonitor />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useWorkbenchStore } from '../../../stores/workbench.store';
import DeviceMonitor from './DeviceMonitor.vue';
import { ElMessage } from 'element-plus';
import apiClient from '../../../services/api';

const store = useWorkbenchStore();
const tasks = computed(() => store.technicianTasks);
const loading = computed(() => store.loading);

const showCompleteDialog = ref(false);
const currentOrderId = ref<number | null>(null);
const completionNotes = ref('');
const selectedParts = ref<{sku: string, quantity: number}[]>([]);
const spareParts = ref<any[]>([]);
const preventiveTasks = ref<any[]>([]);

onMounted(() => {
  store.fetchTechnicianTasks();
  fetchSpareParts();
  fetchPreventiveTasks();
});

const fetchPreventiveTasks = async () => {
    try {
        const res = await apiClient.get('/maintenance/tasks/pending');
        preventiveTasks.value = res.data;
    } catch (e) {
        console.error('Failed to load maintenance tasks');
    }
};

const completePreventiveTask = async (taskId: number) => {
    try {
        await apiClient.put(`/maintenance/tasks/${taskId}/complete`);
        ElMessage.success('Maintenance Task Completed');
        fetchPreventiveTasks();
    } catch (e) {
        ElMessage.error('Failed to complete task');
    }
};

const fetchSpareParts = async () => {
    try {
        const res = await apiClient.get('/inventory', { params: { storeId: 1 } }); // Default store
        spareParts.value = res.data.filter((i: any) => i.category === 'SPARE_PART' || i.category === 'ASSET');
    } catch (error) {
        console.error('Failed to load spare parts');
    }
};

const updateStatus = async (orderId: number, status: string) => {
    try {
        await store.updateWorkOrderStatus(orderId, status);
        ElMessage.success('Status updated successfully');
    } catch (error) {
        ElMessage.error('Failed to update status');
    }
};

const openCompleteDialog = (order: any) => {
    currentOrderId.value = order.id;
    completionNotes.value = '';
    selectedParts.value = [];
    showCompleteDialog.value = true;
};

const addPartRow = () => {
    selectedParts.value.push({ sku: '', quantity: 1 });
};

const removePartRow = (index: number) => {
    selectedParts.value.splice(index, 1);
};

const submitCompletion = async () => {
    if (!currentOrderId.value) return;
    
    // Filter out empty rows
    const partsUsed = selectedParts.value.filter(p => p.sku && p.quantity > 0);
    
    try {
        // We need to update the store action to support payload
        // For now, assume store.updateWorkOrderStatus handles the API call structure or we call API directly here for simplicity if store is rigid
        // Actually, let's call API directly to bypass store limitation if needed, or update store.
        // Let's call API directly for this specific complex action
        await apiClient.put(`/workbench/technician/work-orders/${currentOrderId.value}/status`, {
            status: 'CLOSED',
            notes: completionNotes.value,
            partsUsed: partsUsed
        });
        
        ElMessage.success('Work Order Completed');
        showCompleteDialog.value = false;
        store.fetchTechnicianTasks(); // Refresh
    } catch (error) {
        ElMessage.error('Failed to complete order');
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
.text-danger {
  color: #f56c6c;
}
.text-warning {
  color: #e6a23c;
}
.action-list {
  margin-top: 20px;
  margin-bottom: 20px;
}
.dashboard-split {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;
    margin-bottom: 20px;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.inspection-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}
.inspection-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    background: #f5f7fa;
    border-radius: 4px;
}
.plan-info {
    display: flex;
    flex-direction: column;
}
.plan-area {
    font-weight: bold;
    font-size: 14px;
}
.plan-count {
    font-size: 12px;
    color: #909399;
}
.part-row {
    display: flex;
    gap: 10px;
    margin-bottom: 10px;
    align-items: center;
}
</style>
