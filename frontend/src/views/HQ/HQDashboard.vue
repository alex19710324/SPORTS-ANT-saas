<template>
  <div class="hq-dashboard" v-loading="loading">
    <div class="header">
        <h2>{{ $t('hq.title') }}</h2>
        <div class="actions">
            <el-button type="primary" @click="fetchData">Refresh Dashboard</el-button>
        </div>
    </div>
    
    <div class="overview-grid" v-if="dashboardData">
      <el-card shadow="hover">
        <template #header>Monthly Revenue</template>
        <h3>¥{{ formatMoney(dashboardData.finance?.totalIncome || 0) }}</h3>
        <small class="text-success">Net Profit: ¥{{ formatMoney(dashboardData.finance?.netProfit || 0) }}</small>
      </el-card>
      <el-card shadow="hover">
        <template #header>Members</template>
        <h3>{{ dashboardData.members?.total || 0 }}</h3>
        <small class="text-info">Active: {{ dashboardData.members?.active || 0 }}</small>
      </el-card>
      <el-card shadow="hover">
        <template #header>Operations Health</template>
        <h3 :class="getOpsStatusColor(dashboardData.ops?.systemStatus)">{{ dashboardData.ops?.systemStatus || 'UNKNOWN' }}</h3>
        <small>{{ dashboardData.ops?.pendingMaintenance || 0 }} Pending Tasks</small>
      </el-card>
      <el-card shadow="hover">
        <template #header>Active Campaigns</template>
        <h3>{{ dashboardData.marketing?.activeCampaigns || 0 }}</h3>
      </el-card>
    </div>

    <div class="dashboard-split">
        <el-card class="live-feed-card">
            <template #header>Live Events Feed</template>
            <el-scrollbar height="400px">
                <div v-for="(event, idx) in dashboardData.liveFeed" :key="idx" class="feed-item">
                    <span class="feed-time">{{ event.time }}</span>
                    <el-tag size="small" :type="event.type">{{ event.type }}</el-tag>
                    <span class="feed-msg">{{ event.msg }}</span>
                </div>
            </el-scrollbar>
        </el-card>

        <el-card class="status-card">
            <template #header>Department Status</template>
            <div class="dept-row">
                <span>Inventory</span>
                <el-tag :type="(dashboardData.inventory?.lowStockItems || 0) > 0 ? 'warning' : 'success'">
                    {{ (dashboardData.inventory?.lowStockItems || 0) }} Low Stock
                </el-tag>
            </div>
            <el-divider />
            <div class="dept-row">
                <span>Finance</span>
                <el-tag type="success">Books Balanced</el-tag>
            </div>
            <el-divider />
            <div class="dept-row">
                <span>Marketing</span>
                <el-tag type="primary">Running</el-tag>
            </div>
        </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';

const loading = ref(false);
const dashboardData = ref<any>({});

const fetchData = async () => {
    loading.value = true;
    try {
        const res = await apiClient.get('/hq/dashboard');
        dashboardData.value = res.data;
    } catch (error) {
        ElMessage.error("Failed to load HQ data");
    } finally {
        loading.value = false;
    }
};

const formatMoney = (val: number) => {
    return val ? val.toFixed(2) : '0.00';
};

const getOpsStatusColor = (status: string) => {
    if (status === 'HEALTHY') return 'text-success';
    if (status === 'WARNING') return 'text-warning';
    return 'text-danger';
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped>
.hq-dashboard {
  padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.dashboard-split {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;
}
.feed-item {
    padding: 10px;
    border-bottom: 1px solid #f0f2f5;
    display: flex;
    align-items: center;
    gap: 10px;
}
.feed-time {
    color: #909399;
    font-size: 12px;
    width: 50px;
}
.dept-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
}
.text-success { color: #67c23a; }
.text-warning { color: #e6a23c; }
.text-danger { color: #f56c6c; }
.text-info { color: #909399; }
</style>
