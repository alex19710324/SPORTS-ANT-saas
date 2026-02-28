<template>
  <div class="marketing-dashboard">
    <div class="header">
        <h2>Marketing & Growth</h2>
        <el-button type="primary" @click="showWizard = true">Create Campaign</el-button>
    </div>

    <div class="stats-grid">
        <el-card shadow="hover">
            <template #header>Active Campaigns</template>
            <h3>{{ campaigns.filter(c => c.status === 'ACTIVE').length }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Total Reach</template>
            <h3>15,204</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Conversion Rate</template>
            <h3 class="text-success">3.2%</h3>
        </el-card>
    </div>

    <el-card class="campaign-list">
        <template #header>Campaigns</template>
        <el-table :data="campaigns" style="width: 100%">
            <el-table-column prop="name" label="Name" />
            <el-table-column prop="type" label="Type">
                <template #default="scope">
                    <el-tag>{{ scope.row.type }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="status" label="Status">
                <template #default="scope">
                    <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="Actions" width="150">
                <template #default="scope">
                    <el-button size="small" @click="viewDetails(scope.row)">View</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>

    <CampaignWizard v-model="showWizard" @created="fetchCampaigns" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import CampaignWizard from './CampaignWizard.vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';

const campaigns = ref<any[]>([]);
const showWizard = ref(false);

const fetchCampaigns = async () => {
    try {
        const res = await apiClient.get('/marketing/campaigns');
        campaigns.value = res.data;
    } catch (error) {
        ElMessage.error('Failed to load campaigns');
    }
};

const getStatusType = (status: string) => {
    if (status === 'ACTIVE') return 'success';
    if (status === 'COMPLETED') return 'info';
    return 'warning';
};

const viewDetails = (campaign: any) => {
    ElMessage.info('Campaign details view coming soon');
};

onMounted(() => {
    fetchCampaigns();
});
</script>

<style scoped>
.marketing-dashboard {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin-bottom: 20px;
}
.text-success {
    color: #67c23a;
}
</style>
