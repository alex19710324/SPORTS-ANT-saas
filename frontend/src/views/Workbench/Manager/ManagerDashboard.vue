<template>
  <div class="manager-dashboard" v-loading="loading">
    <h2>{{ $t('manager.title') }}</h2>
    
    <!-- M01: Business Overview -->
    <div class="kpi-grid" v-if="overview">
      <el-card shadow="hover">
        <template #header>{{ $t('manager.revenue') }}</template>
        <h3>¥{{ overview.revenue }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('manager.visitors') }}</template>
        <h3>{{ overview.visitors }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('manager.redemptions') }}</template>
        <h3>{{ overview.redemptions }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('manager.blindbox') }}</template>
        <h3>¥{{ overview.blindboxSales }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('manager.koc') }}</template>
        <h3>¥{{ overview.kocContribution }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('manager.deviceRate') }}</template>
        <h3 class="text-success">{{ overview.deviceOnlineRate }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('manager.alerts') }}</template>
        <h3 class="text-danger">{{ overview.alertCount }}</h3>
      </el-card>
    </div>
    
    <div class="dashboard-split" v-if="overview">
        <!-- M02: Pending Approvals -->
        <el-card class="approval-card">
            <template #header>
                <div class="card-header">
                    <span>{{ $t('manager.approvals') }}</span>
                    <el-tag type="danger" v-if="overview.pendingApprovals">{{ overview.pendingApprovals.length }}</el-tag>
                </div>
            </template>
            <el-table :data="overview.pendingApprovals" style="width: 100%" size="small">
                <el-table-column prop="type" label="Type" width="100" />
                <el-table-column prop="applicant" label="Applicant" width="100" />
                <el-table-column prop="summary" label="Summary" />
                <el-table-column label="Action" width="80">
                    <template #default>
                        <el-button link type="primary" size="small">Review</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- M04: Cost Breakdown -->
        <el-card class="cost-card">
            <template #header>{{ $t('manager.cost') }}</template>
            <div class="cost-list" v-if="overview.costBreakdown">
                <div class="cost-item" v-for="(value, key) in overview.costBreakdown" :key="key">
                    <span>{{ key }}</span>
                    <span>¥{{ value }}</span>
                </div>
            </div>
        </el-card>
    </div>
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
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 20px;
  margin-bottom: 20px;
}
.text-danger {
  color: #f56c6c;
}
.text-success {
  color: #67c23a;
}
.dashboard-split {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.cost-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}
.cost-item {
    display: flex;
    justify-content: space-between;
    padding: 10px;
    background: #f5f7fa;
    border-radius: 4px;
}
</style>
