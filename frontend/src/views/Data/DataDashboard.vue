<template>
  <div class="data-dashboard" v-loading="loading">
    <h2>Data Intelligence Center</h2>
    
    <!-- Real-time Metrics -->
    <div class="metrics-section">
      <h3>Real-time Indicators</h3>
      <div class="metric-grid">
        <el-card shadow="hover" v-for="(data, name) in metrics" :key="name">
          <template #header>{{ name.toUpperCase() }}</template>
          <div class="metric-value">
            <span class="value">{{ data.value }}</span>
            <span class="trend" :class="data.trend.startsWith('+') ? 'up' : 'down'">{{ data.trend }}</span>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Tag Query Tool -->
    <div class="tag-query-section">
      <h3>Audience Segmentation (Tag Query)</h3>
      <div class="query-box">
        <el-select v-model="selectedTags" multiple placeholder="Select Tags" style="width: 300px">
          <el-option label="High Value KOC" value="High Value KOC" />
          <el-option label="Zone Asia" value="Zone Asia" />
          <el-option label="Frequent Visitor" value="Frequent Visitor" />
        </el-select>
        <el-button type="primary" @click="handleQueryTags">Query Audience</el-button>
      </div>
      
      <div class="query-result" v-if="tagResults">
        <el-alert type="success" :closable="false">
          Found {{ tagResults.total }} users matching criteria.
        </el-alert>
        <p>Sample User IDs: {{ tagResults.userIds.join(', ') }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useDataStore } from '../../stores/data.store';

const store = useDataStore();
const metrics = computed(() => store.metrics);
const tagResults = computed(() => store.tagResults);
const loading = computed(() => store.loading);

const selectedTags = ref([]);

const handleQueryTags = () => {
  if (selectedTags.value.length > 0) {
    store.queryTags(selectedTags.value);
  }
};

onMounted(() => {
  store.fetchMetric('revenue');
  store.fetchMetric('visitors');
});
</script>

<style scoped>
.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.metric-value {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.value {
  font-size: 2em;
  font-weight: bold;
}

.trend.up { color: #67c23a; }
.trend.down { color: #f56c6c; }

.query-box {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}
</style>
