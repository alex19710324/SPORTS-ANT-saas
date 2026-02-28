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
                <el-table-column label="Action" width="120">
                    <template #default="scope">
                        <el-button v-if="scope.row.status === 'OPEN'" link type="primary" size="small" @click="updateStatus(scope.row.id, 'IN_PROGRESS')">Start</el-button>
                        <el-button v-if="scope.row.status === 'IN_PROGRESS'" link type="success" size="small" @click="updateStatus(scope.row.id, 'CLOSED')">Complete</el-button>
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

    <DeviceMonitor />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useWorkbenchStore } from '../../../stores/workbench.store';
import DeviceMonitor from './DeviceMonitor.vue';
import { ElMessage } from 'element-plus';

const store = useWorkbenchStore();
const tasks = computed(() => store.technicianTasks);
const loading = computed(() => store.loading);

onMounted(() => {
  store.fetchTechnicianTasks();
});

const updateStatus = async (orderId: number, status: string) => {
    try {
        await store.updateWorkOrderStatus(orderId, status);
        ElMessage.success('Status updated successfully');
    } catch (error) {
        ElMessage.error('Failed to update status');
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
</style>
