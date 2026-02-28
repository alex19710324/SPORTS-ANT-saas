<template>
  <div class="technician-dashboard" v-loading="loading">
    <h2>{{ $t('technician.title') }}</h2>
    <div class="task-grid" v-if="tasks">
      <el-card shadow="hover">
        <template #header>{{ $t('technician.workOrders') }}</template>
        <h3>{{ tasks.pendingWorkOrders }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('technician.faultyDevices') }}</template>
        <h3 class="text-danger">{{ tasks.faultyDevices }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('technician.offlineDevices') }}</template>
        <h3 class="text-warning">{{ tasks.offlineDevices }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('technician.inspection') }}</template>
        <h3>{{ (tasks.inspectionProgress * 100).toFixed(0) }}%</h3>
      </el-card>
    </div>
    
    <div class="action-list">
      <el-button type="primary">{{ $t('technician.scanQr') }}</el-button>
      <el-button type="info">{{ $t('technician.reportFault') }}</el-button>
    </div>
    
    <div class="dashboard-split" v-if="tasks">
        <!-- T06: Preventive Maintenance Suggestions -->
        <el-card class="maintenance-card">
            <template #header>
                <div class="card-header">
                    <span>{{ $t('technician.preventive') }}</span>
                    <el-tag type="warning">AI</el-tag>
                </div>
            </template>
            <el-table :data="tasks.maintenanceSuggestions" style="width: 100%" size="small">
                <el-table-column prop="deviceId" label="Device" width="100" />
                <el-table-column prop="reason" label="Reason" />
                <el-table-column prop="urgency" label="Urgency" width="80">
                    <template #default="scope">
                        <el-tag :type="scope.row.urgency === 'High' ? 'danger' : 'warning'" size="small">{{ scope.row.urgency }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="Action" width="80">
                    <template #default>
                        <el-button link type="primary" size="small">Create WO</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- T02: Inspection Plan -->
        <el-card class="inspection-card">
            <template #header>{{ $t('technician.maintenance') }}</template>
            <div class="inspection-list" v-if="tasks.todayInspections">
                <div class="inspection-item" v-for="plan in tasks.todayInspections" :key="plan.id">
                    <div class="plan-info">
                        <span class="plan-area">{{ plan.area }}</span>
                        <span class="plan-count">{{ plan.deviceCount }} Devices</span>
                    </div>
                    <el-tag :type="plan.status === 'Completed' ? 'success' : 'info'" size="small">{{ plan.status }}</el-tag>
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

const store = useWorkbenchStore();
const tasks = computed(() => store.technicianTasks);
const loading = computed(() => store.loading);

onMounted(() => {
  store.fetchTechnicianTasks();
});
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
