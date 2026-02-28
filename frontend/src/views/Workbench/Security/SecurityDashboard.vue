<template>
  <div class="security-dashboard" v-loading="loading">
    <h2>{{ $t('security.title') }}</h2>
    
    <div class="alert-banner" v-if="tasks && tasks.incidents && tasks.incidents.length > 0">
        <el-alert
            v-for="incident in tasks.incidents"
            :key="incident.id"
            :title="`[${incident.type}] ${incident.location} - ${incident.time}`"
            type="error"
            effect="dark"
            show-icon
            :closable="false"
            class="mb-2"
        >
            <template #default>
                <div class="alert-content">
                    <span>{{ incident.description }}</span>
                    <el-button type="danger" size="small" link v-if="incident.status !== 'RESOLVED'" @click="handleResolve(incident.id)">Resolve</el-button>
                    <el-tag v-else type="success" size="small">Resolved</el-tag>
                </div>
            </template>
        </el-alert>
    </div>

    <div class="task-grid" v-if="tasks">
      <el-card shadow="hover">
        <template #header>{{ $t('security.inspections') }}</template>
        <h3>{{ tasks.todayInspections ? tasks.todayInspections.length : 0 }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('security.incidents') }}</template>
        <h3 class="text-danger">{{ tasks.incidents ? tasks.incidents.filter((i:any) => i.status !== 'RESOLVED').length : 0 }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('security.equipment') }}</template>
        <h3 class="text-warning">{{ tasks.expiringEquipment }} Expiring</h3>
      </el-card>
    </div>
    
    <div class="action-list">
      <el-button type="primary" size="large">{{ $t('security.checkEquipment') }}</el-button>
      <el-button type="danger" size="large" @click="handleEmergencyAlert">{{ $t('security.reportIncident') }}</el-button>
    </div>
    
    <div class="dashboard-split" v-if="tasks">
        <!-- S01: Inspection List -->
        <el-card class="inspection-card">
            <template #header>{{ $t('security.inspections') }}</template>
            <el-table :data="tasks.todayInspections" style="width: 100%" size="small">
                <el-table-column prop="area" label="Area" />
                <el-table-column prop="items" label="Check Items" width="100" />
                <el-table-column prop="status" label="Status" width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 'Completed' ? 'success' : 'info'" size="small">{{ scope.row.status }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="Action" width="80">
                    <template #default>
                        <el-button link type="primary" size="small">Start</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useWorkbenchStore } from '../../../stores/workbench.store';
import { ElMessage, ElMessageBox } from 'element-plus';

const store = useWorkbenchStore();
const tasks = computed(() => store.securityTasks);
const loading = computed(() => store.loading);

onMounted(() => {
  store.fetchSecurityTasks();
});

const handleEmergencyAlert = () => {
    ElMessageBox.prompt('Describe the emergency situation:', 'Emergency Alert', {
        confirmButtonText: 'Report',
        cancelButtonText: 'Cancel',
        inputPattern: /.+/,
        inputErrorMessage: 'Description is required'
    }).then(({ value }) => {
        store.createIncident('EMERGENCY', 'Main Hall', value)
            .then(() => ElMessage.success('Emergency reported!'))
            .catch(() => ElMessage.error('Failed to report'));
    }).catch(() => {});
};

const handleResolve = (id: number) => {
    store.resolveIncident(id)
        .then(() => ElMessage.success('Incident resolved'))
        .catch(() => ElMessage.error('Failed to resolve'));
};
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
.text-warning {
  color: #e6a23c;
}
.action-list {
  margin-top: 20px;
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
}
.dashboard-split {
    margin-bottom: 20px;
}
.alert-banner {
    margin-bottom: 20px;
}
.mb-2 {
    margin-bottom: 10px;
}
.alert-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
}
</style>