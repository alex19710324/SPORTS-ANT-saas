<template>
  <div class="home-container">
    <div class="header">
      <h1>{{ $t('home.welcome') }}</h1>
      <el-button type="danger" @click="handleLogout">{{ $t('home.logout') }}</el-button>
    </div>

    <!-- Navigation Modules -->
    <div class="module-nav">
      <h2 class="section-title">Applications</h2>
      <el-row :gutter="20">
        <el-col :span="6" v-for="mod in modules" :key="mod.path">
          <el-card class="module-card" shadow="hover" @click="router.push(mod.path)">
            <div class="module-icon">
              <el-icon :size="32" :color="mod.color">
                <component :is="mod.icon" />
              </el-icon>
            </div>
            <div class="module-info">
              <h3>{{ mod.title }}</h3>
              <p>{{ mod.desc }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <div class="dashboard-grid">
      <div class="left-panel">
        <AiGuardian />
        <MembershipCard class="membership-card-comp" />
      </div>
      <div class="right-panel">
        <AiSuggestions />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../stores/auth.store';
import { useRouter } from 'vue-router';
import AiGuardian from '../components/ai/AiGuardian.vue';
import AiSuggestions from '../components/ai/AiSuggestions.vue';
import MembershipCard from '../components/MembershipCard.vue';
import { 
  OfficeBuilding, 
  User, 
  Service, 
  DataLine, 
  Money, 
  ChatDotRound, 
  Promotion, 
  Setting 
} from '@element-plus/icons-vue';

const authStore = useAuthStore();
const router = useRouter();

const modules = [
  { title: 'HQ Center', desc: 'Franchise & Global Management', path: '/hq', icon: OfficeBuilding, color: '#409EFF' },
  { title: 'Store Manager', desc: 'Daily Operations & Staff', path: '/workbench/manager', icon: User, color: '#67C23A' },
  { title: 'Front Desk', desc: 'Check-in & POS', path: '/workbench/frontdesk', icon: Service, color: '#E6A23C' },
  { title: 'Technician', desc: 'Device Monitoring & Repair', path: '/workbench/technician', icon: Setting, color: '#F56C6C' },
  { title: 'Data Center', desc: 'Analytics & Reports', path: '/data', icon: DataLine, color: '#909399' },
  { title: 'Marketing', desc: 'Campaigns & Members', path: '/marketing', icon: Promotion, color: '#F56C6C' },
  { title: 'Finance', desc: 'Invoices & Payroll', path: '/finance', icon: Money, color: '#E6A23C' },
  { title: 'Communication', desc: 'Messages & Notifications', path: '/communication', icon: ChatDotRound, color: '#409EFF' },
];

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.home-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-title {
  margin-bottom: 20px;
  font-weight: 600;
  color: #303133;
}

.module-nav {
  margin-bottom: 40px;
}

.module-card {
  cursor: pointer;
  transition: transform 0.2s;
  height: 120px;
  display: flex;
  align-items: center;
}

.module-card:hover {
  transform: translateY(-5px);
}

.module-icon {
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.module-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
}

.module-info p {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.membership-card-comp {
  margin-top: 20px;
}
</style>
