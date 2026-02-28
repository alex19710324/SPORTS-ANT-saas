<template>
  <div class="saas-dashboard">
    <div class="header">
        <h2>SaaS Super Admin</h2>
        <el-button type="primary" @click="showCreateModal = true">Onboard New Tenant</el-button>
    </div>

    <div class="stats-row">
        <el-card shadow="hover">
            <template #header>Total Tenants</template>
            <h3>{{ tenants.length }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Active Subscriptions</template>
            <h3 class="text-success">{{ activeCount }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Monthly Recurring Revenue</template>
            <h3>¥{{ mrr }}</h3>
        </el-card>
    </div>

    <el-card>
        <el-table :data="tenants" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="Franchise Name" />
            <el-table-column prop="domain" label="Domain" />
            <el-table-column prop="subscriptionPlan" label="Plan">
                <template #default="scope">
                    <el-tag :type="getPlanType(scope.row.subscriptionPlan)">{{ scope.row.subscriptionPlan }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="status" label="Status">
                <template #default="scope">
                    <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'">{{ scope.row.status }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="Actions" width="200">
                <template #default="scope">
                    <el-button 
                        size="small" 
                        :type="scope.row.status === 'ACTIVE' ? 'danger' : 'success'"
                        @click="toggleStatus(scope.row)"
                    >
                        {{ scope.row.status === 'ACTIVE' ? 'Suspend' : 'Activate' }}
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>

    <!-- Create Tenant Modal -->
    <el-dialog v-model="showCreateModal" title="Onboard New Tenant">
        <el-form :model="newTenant" label-width="120px">
            <el-form-item label="Franchise Name">
                <el-input v-model="newTenant.name" />
            </el-form-item>
            <el-form-item label="Domain">
                <el-input v-model="newTenant.domain" placeholder="e.g. gym-one.sportsant.com" />
            </el-form-item>
            <el-form-item label="Plan">
                <el-select v-model="newTenant.plan">
                    <el-option label="Free Trial" value="FREE" />
                    <el-option label="Pro (¥999/mo)" value="PRO" />
                    <el-option label="Enterprise (¥2999/mo)" value="ENTERPRISE" />
                </el-select>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showCreateModal = false">Cancel</el-button>
            <el-button type="primary" @click="createTenant">Create</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';

const tenants = ref<any[]>([]);
const showCreateModal = ref(false);
const newTenant = ref({ name: '', domain: '', plan: 'PRO' });

const activeCount = computed(() => tenants.value.filter(t => t.status === 'ACTIVE').length);

const mrr = computed(() => {
    return tenants.value.reduce((sum, t) => {
        if (t.status !== 'ACTIVE') return sum;
        if (t.subscriptionPlan === 'PRO') return sum + 999;
        if (t.subscriptionPlan === 'ENTERPRISE') return sum + 2999;
        return sum;
    }, 0);
});

const fetchTenants = async () => {
    try {
        const res = await apiClient.get('/saas/admin/tenants');
        tenants.value = res.data;
    } catch (e) {
        ElMessage.error('Failed to load tenants');
    }
};

const createTenant = async () => {
    try {
        await apiClient.post('/saas/admin/tenants', newTenant.value);
        ElMessage.success('Tenant created successfully');
        showCreateModal.value = false;
        fetchTenants();
    } catch (e) {
        ElMessage.error('Failed to create tenant');
    }
};

const toggleStatus = async (tenant: any) => {
    const newStatus = tenant.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
    try {
        await apiClient.put(`/saas/admin/tenants/${tenant.id}/status`, { status: newStatus });
        ElMessage.success(`Tenant ${newStatus.toLowerCase()}`);
        fetchTenants();
    } catch (e) {
        ElMessage.error('Failed to update status');
    }
};

const getPlanType = (plan: string) => {
    if (plan === 'ENTERPRISE') return 'warning';
    if (plan === 'PRO') return 'primary';
    return 'info';
};

onMounted(() => {
    fetchTenants();
});
</script>

<style scoped>
.saas-dashboard {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.stats-row {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin-bottom: 20px;
}
.text-success { color: #67c23a; }
</style>
