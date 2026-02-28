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
            <h3>{{ totalReach }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Conversion Rate</template>
            <h3 class="text-success">{{ conversionRate }}</h3>
        </el-card>
    </div>

    <el-card class="campaign-list">
        <template #header>Campaigns</template>
        <el-table :data="campaigns" style="width: 100%">
            <el-table-column prop="name" label="Name" />
            <el-table-column prop="targetSegment" label="Target" />
            <el-table-column prop="status" label="Status">
                <template #default="scope">
                    <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="Performance">
                <template #default="scope">
                    <div v-if="scope.row.sentCount > 0">
                        <small>Sent: {{ scope.row.sentCount }}</small><br/>
                        <small>Conv: {{ scope.row.convertedCount }} ({{ ((scope.row.convertedCount/scope.row.sentCount)*100).toFixed(1) }}%)</small>
                    </div>
                    <span v-else>-</span>
                </template>
            </el-table-column>
            <el-table-column label="Actions" width="150">
                <template #default="scope">
                    <el-button v-if="scope.row.status === 'DRAFT'" size="small" type="success" @click="launchCampaign(scope.row)">Launch</el-button>
                    <el-button v-else size="small" @click="viewDetails(scope.row)">View</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>

    <CampaignWizard v-model="showWizard" @created="fetchCampaigns" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import CampaignWizard from './CampaignWizard.vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';

const campaigns = ref<any[]>([]);
const showWizard = ref(false);

const totalReach = computed(() => campaigns.value.reduce((sum, c) => sum + (c.sentCount || 0), 0));
const conversionRate = computed(() => {
    const totalSent = totalReach.value;
    if (totalSent === 0) return '0.0%';
    const totalConv = campaigns.value.reduce((sum, c) => sum + (c.convertedCount || 0), 0);
    return ((totalConv / totalSent) * 100).toFixed(1) + '%';
});

const fetchCampaigns = async () => {
    try {
        const res = await apiClient.get('/marketing/campaigns');
        campaigns.value = res.data;
    } catch (error) {
        ElMessage.error('Failed to load campaigns');
    }
};

const launchCampaign = async (campaign: any) => {
    try {
        await apiClient.post(`/marketing/campaigns/${campaign.id}/launch`);
        ElMessage.success('Campaign Launched!');
        fetchCampaigns();
    } catch (e) {
        ElMessage.error('Failed to launch');
    }
};

const getStatusType = (status: string) => {
    if (status === 'ACTIVE') return 'success';
    if (status === 'COMPLETED') return 'info';
    return 'warning';
};

const viewDetails = (campaign: any) => {
    ElMessage.info(`Campaign details view for ${campaign.name} coming soon`);
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
