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
}
</style>
