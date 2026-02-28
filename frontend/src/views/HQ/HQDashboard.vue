<template>
  <div class="hq-dashboard" v-loading="loading">
    <h2>Global Headquarters</h2>
    
    <div class="overview-grid" v-if="overview">
      <el-card shadow="hover">
        <template #header>Total Revenue</template>
        <h3>¥{{ overview.totalRevenue }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Total Visitors</template>
        <h3>{{ overview.totalVisitors }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Active Stores</template>
        <h3>{{ overview.storeCount }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Active KOCs</template>
        <h3>{{ overview.activeKoc }}</h3>
      </el-card>
    </div>

    <el-card class="map-card">
      <template #header>Global Store Map</template>
      <div class="map-placeholder">
        <!-- In real app, integrate Baidu Map or Google Maps here -->
        <p>Map Visualization Placeholder</p>
        <ul>
          <li v-for="store in storeMapData" :key="store.id">
            {{ store.name }} - {{ store.city }} (Rev: ¥{{ store.todayRevenue }})
          </li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useHQStore } from '../../stores/hq.store';

const store = useHQStore();
const overview = computed(() => store.overview);
const storeMapData = computed(() => store.storeMapData);
const loading = computed(() => store.loading);

onMounted(() => {
  store.fetchGlobalOverview();
  store.fetchStoreMapData();
});
</script>

<style scoped>
.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.map-card {
  height: 400px;
}

.map-placeholder {
  background: #f0f2f5;
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}
</style>
