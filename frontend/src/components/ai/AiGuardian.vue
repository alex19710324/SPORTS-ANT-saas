<template>
  <div class="guardian-status" :class="statusClass">
    <div class="status-header">
      <h3>🛡️ System Guardian</h3>
      <el-tag :type="statusType" effect="dark">{{ health.status }}</el-tag>
    </div>
    <div class="status-body">
      <div class="message-bubble">
        <p>{{ health.ai_guardian_message }}</p>
      </div>
      <div class="metrics">
        <div class="metric-item">
          <span class="label">Memory</span>
          <el-progress :percentage="Number(health.memory_usage_percent)" :status="progressStatus"></el-progress>
        </div>
        <div class="metric-item">
          <span class="label">CPU</span>
          <span class="value">{{ health.cpu_load }}</span>
        </div>
         <div class="metric-item">
          <span class="label">Threats</span>
          <span class="value">{{ health.active_threats }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useAiStore } from '../../stores/ai.store';

const aiStore = useAiStore();

onMounted(() => {
  aiStore.fetchHealth();
  // Poll every 30 seconds
  setInterval(() => {
    aiStore.fetchHealth();
  }, 30000);
});

const health = computed(() => aiStore.health);

const statusClass = computed(() => {
  return {
    'status-healthy': health.value.status === 'HEALTHY',
    'status-warning': health.value.status === 'WARNING',
    'status-critical': health.value.status === 'CRITICAL',
  };
});

const statusType = computed(() => {
  switch (health.value.status) {
    case 'HEALTHY': return 'success';
    case 'WARNING': return 'warning';
    case 'CRITICAL': return 'danger';
    default: return 'info';
  }
});

const progressStatus = computed(() => {
  const usage = Number(health.value.memory_usage_percent);
  if (usage > 90) return 'exception';
  if (usage > 70) return 'warning';
  return 'success';
});
</script>

<style scoped>
.guardian-status {
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.status-healthy {
  border-left: 5px solid #67c23a;
}
.status-warning {
  border-left: 5px solid #e6a23c;
}
.status-critical {
  border-left: 5px solid #f56c6c;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.message-bubble {
  background-color: #f4f4f5;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
  font-style: italic;
  color: #606266;
}

.metrics {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.metric-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.metric-item .label {
  width: 60px;
  font-weight: bold;
}
.metric-item .el-progress {
    width: 200px;
}
</style>
