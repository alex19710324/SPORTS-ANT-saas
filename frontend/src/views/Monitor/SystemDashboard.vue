<template>
  <div class="system-dashboard">
    <div class="header">
        <h2>System Monitor</h2>
        <el-tag type="success" effect="dark">HEALTHY</el-tag>
    </div>

    <div class="monitor-grid">
        <el-card>
            <template #header>CPU Usage</template>
            <el-progress type="dashboard" :percentage="cpuUsage" :color="cpuColor" />
            <div class="detail">System: {{ (metrics.systemCpu * 100).toFixed(1) }}%</div>
        </el-card>

        <el-card>
            <template #header>Memory Usage</template>
            <el-progress type="dashboard" :percentage="memUsage" :color="memColor" />
            <div class="detail">
                {{ (metrics.jvmMemoryUsed / 1024 / 1024).toFixed(0) }}MB / 
                {{ (metrics.jvmMemoryMax / 1024 / 1024).toFixed(0) }}MB
            </div>
        </el-card>

        <el-card>
            <template #header>System Uptime</template>
            <div class="stat-value">{{ formatUptime(metrics.uptime) }}</div>
            <div class="detail">Live Threads: {{ metrics.liveThreads }}</div>
        </el-card>

        <el-card>
            <template #header>HTTP Requests</template>
            <div class="stat-value">{{ metrics.httpRequests }}</div>
            <div class="detail">Total Since Boot</div>
        </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import apiClient from '../../services/api';

const metrics = ref<any>({
    systemCpu: 0,
    processCpu: 0,
    jvmMemoryUsed: 0,
    jvmMemoryMax: 100, // avoid div by zero
    httpRequests: 0,
    liveThreads: 0,
    uptime: 0
});

const timer = ref<any>(null);

const cpuUsage = computed(() => Math.round(metrics.value.systemCpu * 100));
const memUsage = computed(() => Math.round((metrics.value.jvmMemoryUsed / metrics.value.jvmMemoryMax) * 100));

const cpuColor = computed(() => {
    if (cpuUsage.value > 80) return '#f56c6c';
    if (cpuUsage.value > 60) return '#e6a23c';
    return '#67c23a';
});

const memColor = computed(() => {
    if (memUsage.value > 85) return '#f56c6c';
    if (memUsage.value > 70) return '#e6a23c';
    return '#409eff';
});

const fetchMetrics = async () => {
    try {
        const res = await apiClient.get('/monitor/dashboard');
        metrics.value = res.data;
    } catch (e) {
        console.error("Failed to fetch system metrics");
    }
};

const formatUptime = (seconds: number) => {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    return `${h}h ${m}m`;
};

onMounted(() => {
    fetchMetrics();
    timer.value = setInterval(fetchMetrics, 5000); // Poll every 5s
});

onUnmounted(() => {
    if (timer.value) clearInterval(timer.value);
});
</script>

<style scoped>
.system-dashboard {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.monitor-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
}
.stat-value {
    font-size: 32px;
    font-weight: bold;
    text-align: center;
    margin: 20px 0;
    color: #303133;
}
.detail {
    text-align: center;
    color: #909399;
    font-size: 14px;
}
</style>
