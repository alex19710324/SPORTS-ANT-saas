<template>
  <div class="manager-dashboard" v-loading="loading">
    <h2>{{ $t('manager.title') }}</h2>
    
    <!-- M01: Business Overview -->
    <div class="kpi-grid" v-if="overview">
      <!-- AI Brain Widget -->
      <el-card shadow="hover" class="ai-card">
        <template #header>
            <div class="card-header">
                <span>🤖 AI Assistant</span>
                <el-tag type="warning" effect="dark" v-if="aiSuggestions.length > 0">{{ aiSuggestions.length }}</el-tag>
            </div>
        </template>
        <div class="suggestion-list" v-if="aiSuggestions.length > 0">
            <div v-for="s in aiSuggestions" :key="s.id" class="suggestion-item">
                <div class="suggestion-title">
                    <strong>{{ s.title }}</strong>
                    <el-tag size="small" :type="getPriorityType(s.priority)">{{ s.priority }}</el-tag>
                </div>
                <p class="suggestion-content">{{ s.content }}</p>
                <div class="suggestion-actions" v-if="s.actionableApi">
                    <el-button type="primary" link size="small">Execute Action</el-button>
                </div>
            </div>
        </div>
        <div v-else class="empty-state">
            No active suggestions. System is running optimally.
        </div>
      </el-card>

      <el-card shadow="hover">
        <template #header>
            <div class="card-header">
                <span>{{ $t('manager.revenue') }}</span>
                <el-button type="primary" link size="small" @click="$router.push('/report')">View Report</el-button>
            </div>
        </template>
        <h3>¥{{ overview.todayRevenue }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('manager.visitors') }}</template>
        <h3>{{ overview.todayVisitors }}</h3>
      </el-card>
    </div>
    
    <div class="dashboard-split" v-if="overview">
      <el-card shadow="hover">
        <template #header>{{ $t('manager.koc') }}</template>
        <h3>¥{{ overview.kocContribution }}</h3>
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
                    <template #default="scope">
                        <el-button link type="primary" size="small" @click="handleApprove(scope.row.id)">Approve</el-button>
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
import { ElMessage } from 'element-plus';

const store = useWorkbenchStore();
const overview = computed(() => store.managerOverview);
const loading = computed(() => store.loading);
const aiSuggestions = computed(() => store.aiSuggestions || []);

onMounted(() => {
  store.fetchManagerOverview(1); // Default Store ID 1
  store.fetchAiSuggestions();
});

const handleApprove = async (id: number) => {
    try {
        await store.approveRequest(id);
        ElMessage.success('Request approved successfully');
    } catch (error) {
        ElMessage.error('Failed to approve request');
    }
};

const getPriorityType = (priority: string) => {
    switch (priority) {
        case 'HIGH': return 'danger';
        case 'MEDIUM': return 'warning';
        case 'LOW': return 'info';
        default: return 'info';
    }
};
</script>

<style scoped>
.ai-card {
    grid-column: span 4;
    border-left: 4px solid #409eff;
}
.suggestion-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}
.suggestion-item {
    background: #f0f9eb;
    padding: 10px;
    border-radius: 4px;
}
.suggestion-title {
    display: flex;
    justify-content: space-between;
    margin-bottom: 5px;
}
.suggestion-content {
    margin: 0;
    font-size: 0.9em;
    color: #606266;
}
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
