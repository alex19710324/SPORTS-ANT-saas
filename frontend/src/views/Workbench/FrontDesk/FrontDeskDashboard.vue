<template>
  <div class="frontdesk-dashboard" v-loading="loading">
    <h2>Front Desk Workbench</h2>
    <div class="task-grid" v-if="tasks">
      <el-card shadow="hover">
        <template #header>Today's Target</template>
        <h3>¥{{ tasks.todayTarget }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Current Sales</template>
        <h3>¥{{ tasks.currentSales }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Pending Check-ins</template>
        <h3>{{ tasks.pendingCheckins }}</h3>
      </el-card>
    </div>
    
    <div class="quick-actions">
      <el-button type="primary" size="large">Quick Check-in</el-button>
      <el-button type="success" size="large">New Sale</el-button>
      <el-button type="warning" size="large">Member Reg</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useWorkbenchStore } from '../../../stores/workbench.store';

const store = useWorkbenchStore();
const tasks = computed(() => store.frontDeskTasks);
const loading = computed(() => store.loading);

onMounted(() => {
  store.fetchFrontDeskTasks();
});
</script>

<style scoped>
.task-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 20px;
}
.quick-actions {
  margin-top: 30px;
  display: flex;
  gap: 15px;
}
</style>
