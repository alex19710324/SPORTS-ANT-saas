<template>
  <div class="frontdesk-dashboard" v-loading="loading">
    <h2>{{ $t('frontdesk.title') }}</h2>
    <div class="task-grid" v-if="tasks">
      <el-card shadow="hover">
        <template #header>{{ $t('frontdesk.target') }}</template>
        <h3>¥{{ tasks.todayTarget }}</h3>
        <el-progress :percentage="tasks.targetCompletion * 100" :status="getProgressStatus(tasks.targetCompletion)"></el-progress>
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
      <el-button type="success" size="large">{{ $t('frontdesk.newSale') }}</el-button>
      <el-button type="warning" size="large">{{ $t('frontdesk.memberReg') }}</el-button>
    </div>

    <!-- Quick Checkin Modal Placeholder -->
    <el-dialog v-model="showCheckinModal" :title="$t('frontdesk.quickCheckin')">
        <el-input placeholder="Scan Ticket / QR Code" autofocus></el-input>
        <template #footer>
            <el-button @click="showCheckinModal = false">Cancel</el-button>
            <el-button type="primary" @click="showCheckinModal = false">Verify</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useWorkbenchStore } from '../../../stores/workbench.store';

const store = useWorkbenchStore();
const tasks = computed(() => store.frontDeskTasks);
const loading = computed(() => store.loading);
const showCheckinModal = ref(false);

onMounted(() => {
  store.fetchFrontDeskTasks();
});

const handleQuickCheckin = () => {
    showCheckinModal.value = true;
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
