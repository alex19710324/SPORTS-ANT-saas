<template>
  <div class="hq-dashboard" v-loading="loading">
    <h2>{{ $t('hq.title') }}</h2>
    
    <div class="overview-grid" v-if="overview">
      <el-card shadow="hover">
        <template #header>{{ $t('hq.revenue') }}</template>
        <h3>¥{{ overview.totalRevenue }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('hq.visitors') }}</template>
        <h3>{{ overview.totalVisitors }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('hq.stores') }}</template>
        <h3>{{ overview.storeCount }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>{{ $t('hq.koc') }}</template>
        <h3>{{ overview.activeKoc }}</h3>
      </el-card>
    </div>

    <el-card class="map-card">
      <template #header>{{ $t('hq.map') }}</template>
      <div class="map-placeholder">
        <!-- In real app, integrate Baidu Map or Google Maps here -->
        <div class="map-visual">
             <div class="store-dot" v-for="store in storeMapData" :key="store.id" 
                  :style="{ left: (store.longitude + 180) / 3.6 + '%', top: (90 - store.latitude) / 1.8 + '%' }"
                  :title="store.name">
             </div>
        </div>
        <ul class="store-list">
          <li v-for="store in storeMapData" :key="store.id">
            <span class="status-dot" :class="store.status.toLowerCase()"></span>
            {{ store.name }} - {{ store.city }} (Rev: ¥{{ store.todayRevenue }})
          </li>
        </ul>
      </div>
    </el-card>

    <el-card class="franchise-card">
      <template #header>{{ $t('hq.franchise') }}</template>
      <el-table :data="franchiseApplications" style="width: 100%">
        <el-table-column prop="applicantName" :label="$t('hq.applicant')" />
        <el-table-column prop="proposedCity" :label="$t('hq.city')" />
        <el-table-column prop="contactInfo" :label="$t('hq.contact')" />
        <el-table-column prop="status" :label="$t('hq.status')">
             <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
             </template>
        </el-table-column>
        <el-table-column label="Action" width="200">
          <template #default="scope">
            <div v-if="scope.row.status === 'PENDING'">
                <el-button size="small" type="success" @click="handleApprove(scope.row, true)">{{ $t('hq.approve') }}</el-button>
                <el-button size="small" type="danger" @click="handleApprove(scope.row, false)">{{ $t('hq.reject') }}</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Approval Dialog -->
    <el-dialog v-model="showApprovalDialog" title="Application Review">
        <el-form>
            <el-form-item label="Decision">
                <el-tag :type="currentApprovalType ? 'success' : 'danger'">
                    {{ currentApprovalType ? 'Approve' : 'Reject' }}
                </el-tag>
            </el-form-item>
            <el-form-item label="Comments">
                <el-input type="textarea" v-model="approvalComments"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showApprovalDialog = false">Cancel</el-button>
            <el-button type="primary" @click="submitApproval">Confirm</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useHQStore } from '../../stores/hq.store';
import { ElMessage } from 'element-plus';

const store = useHQStore();
const overview = computed(() => store.overview);
const storeMapData = computed(() => store.storeMapData);
const franchiseApplications = computed(() => store.franchiseApplications);
const loading = computed(() => store.loading);

const showApprovalDialog = ref(false);
const currentApp = ref<any>(null);
const currentApprovalType = ref(true);
const approvalComments = ref('');

onMounted(() => {
  store.fetchGlobalOverview();
  store.fetchStoreMapData();
  store.fetchFranchiseApplications();
});

const getStatusType = (status: string) => {
    if (status === 'APPROVED') return 'success';
    if (status === 'REJECTED') return 'danger';
    return 'warning';
};

const handleApprove = (app: any, approve: boolean) => {
    currentApp.value = app;
    currentApprovalType.value = approve;
    approvalComments.value = approve ? 'Approved. Welcome to SPORTS ANT!' : 'Application rejected due to incomplete documents.';
    showApprovalDialog.value = true;
};

const submitApproval = async () => {
    if (!currentApp.value) return;
    try {
        await store.approveApplication(currentApp.value.id);
        showApprovalDialog.value = false;
        ElMessage.success('Application processed successfully');
    } catch (error) {
        ElMessage.error('Failed to process application');
    }
};
</script>

<style scoped>
.hq-dashboard {
  padding: 20px;
}
.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.map-card {
  margin-bottom: 20px;
}
.franchise-card {
  margin-bottom: 20px;
}
.map-placeholder {
  background: #f0f2f5;
  height: 400px;
  position: relative;
  overflow: hidden;
  display: flex;
}
.map-visual {
    flex: 1;
    background-color: #e6e6e6;
    background-image: radial-gradient(#d1d1d1 1px, transparent 1px);
    background-size: 20px 20px;
    position: relative;
}
.store-list {
    width: 300px;
    background: white;
    overflow-y: auto;
    padding: 10px;
    border-left: 1px solid #eee;
    list-style: none;
    margin: 0;
}
.store-list li {
    padding: 10px;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
    align-items: center;
}
.store-dot {
    position: absolute;
    width: 12px;
    height: 12px;
    background: #f56c6c;
    border-radius: 50%;
    transform: translate(-50%, -50%);
    box-shadow: 0 0 10px rgba(245, 108, 108, 0.5);
    cursor: pointer;
}
.status-dot {
    display: inline-block;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    margin-right: 8px;
}
.status-dot.normal { background: #67C23A; }
.status-dot.warning { background: #E6A23C; }
.status-dot.closed { background: #F56C6C; }
</style>
