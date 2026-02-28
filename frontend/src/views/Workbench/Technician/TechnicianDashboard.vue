<template>
  <div class="technician-dashboard" v-loading="loading">
    <h2>Technician Workbench</h2>
    <div class="task-grid" v-if="tasks">
      <el-card shadow="hover">
        <template #header>Work Orders</template>
        <h3>{{ tasks.pendingWorkOrders }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Offline Devices</template>
        <h3 class="text-danger">{{ tasks.offlineDevices }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Inspection</template>
        <h3>{{ (tasks.inspectionProgress * 100).toFixed(0) }}%</h3>
      </el-card>
    </div>
    
    <div class="action-list">
      <el-button type="primary">Scan Device QR</el-button>
      <el-button type="info">Report Fault</el-button>
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
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 20px;
}
.text-danger {
  color: #f56c6c;
}
.action-list {
  margin-top: 20px;
}
</style>
