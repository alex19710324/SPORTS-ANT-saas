<template>
  <div class="manager-dashboard" v-loading="loading">
    <h2>Manager Overview</h2>
    <div class="kpi-grid" v-if="overview">
      <el-card shadow="hover">
        <template #header>Revenue</template>
        <h3>¥{{ overview.revenue }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Visitors</template>
        <h3>{{ overview.visitors }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Alerts</template>
        <h3 class="text-danger">{{ overview.alertCount }}</h3>
      </el-card>
    </div>
    
    <!-- Charts would go here -->
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useWorkbenchStore } from '../../../stores/workbench.store';

const store = useWorkbenchStore();
const overview = computed(() => store.managerOverview);
const loading = computed(() => store.loading);

onMounted(() => {
  store.fetchManagerOverview(1); // Default Store ID 1
});
</script>

<style scoped>
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 20px;
}
.text-danger {
  color: #f56c6c;
}
</style>
