<template>
  <div class="home-container">
    <div class="header">
      <h1>{{ $t('home.welcome') }}</h1>
      <el-button type="danger" @click="handleLogout">{{ $t('home.logout') }}</el-button>
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

const authStore = useAuthStore();
const router = useRouter();

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

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.membership-card-comp {
  margin-top: 20px;
}
</style>
