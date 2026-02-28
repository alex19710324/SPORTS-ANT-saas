<template>
  <el-aside class="sidebar" :width="isCollapsed ? '64px' : '200px'">
    <div class="sidebar-header" v-if="!isCollapsed">
      SPORTS ANT
    </div>
    
    <el-menu
      :default-active="activeMenu"
      class="el-menu-vertical-demo"
      :collapse="isCollapsed"
      @open="handleOpen"
      @close="handleClose"
      router
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
    >
      <el-menu-item index="/">
        <el-icon><home /></el-icon>
        <template #title>{{ $t('home.modules.applications.title') || 'Home' }}</template>
      </el-menu-item>
      
      <el-sub-menu index="/hq" v-if="hasRole(['ADMIN', 'HQ'])">
        <template #title>
          <el-icon><office-building /></el-icon>
          <span>{{ $t('home.modules.hq.title') }}</span>
        </template>
        <el-menu-item index="/hq/dashboard">{{ $t('home.modules.hq.desc') }}</el-menu-item>
      </el-sub-menu>

      <el-menu-item index="/workbench/manager" v-if="hasRole(['ADMIN', 'STORE_MANAGER'])">
        <el-icon><setting /></el-icon>
        <template #title>{{ $t('home.modules.manager.title') }}</template>
      </el-menu-item>
      
      <el-menu-item index="/workbench/frontdesk" v-if="hasRole(['ADMIN', 'FRONT_DESK', 'STORE_MANAGER'])">
        <el-icon><service /></el-icon>
        <template #title>{{ $t('home.modules.frontdesk.title') }}</template>
      </el-menu-item>

      <el-menu-item index="/workbench/technician" v-if="hasRole(['ADMIN', 'TECHNICIAN', 'STORE_MANAGER'])">
        <el-icon><tools /></el-icon>
        <template #title>{{ $t('home.modules.technician.title') }}</template>
      </el-menu-item>

      <el-menu-item index="/data/dashboard" v-if="hasRole(['ADMIN', 'HQ', 'STORE_MANAGER'])">
        <el-icon><data-line /></el-icon>
        <template #title>{{ $t('home.modules.data.title') }}</template>
      </el-menu-item>

      <el-menu-item index="/marketing/dashboard" v-if="hasRole(['ADMIN', 'MARKETING', 'STORE_MANAGER'])">
        <el-icon><promotion /></el-icon>
        <template #title>{{ $t('home.modules.marketing.title') }}</template>
      </el-menu-item>

      <el-menu-item index="/finance/dashboard" v-if="hasRole(['ADMIN', 'FINANCE', 'STORE_MANAGER'])">
        <el-icon><money /></el-icon>
        <template #title>{{ $t('home.modules.finance.title') }}</template>
      </el-menu-item>

      <el-menu-item index="/inventory" v-if="hasRole(['ADMIN', 'STORE_MANAGER', 'TECHNICIAN'])">
        <el-icon><box /></el-icon>
        <template #title>Inventory</template>
      </el-menu-item>

      <el-menu-item index="/hr" v-if="hasRole(['ADMIN', 'HR', 'STORE_MANAGER'])">
        <el-icon><user-filled /></el-icon>
        <template #title>HR</template>
      </el-menu-item>

      <el-menu-item index="/report" v-if="hasRole(['ADMIN', 'STORE_MANAGER', 'HQ'])">
        <el-icon><document /></el-icon>
        <template #title>Reports</template>
      </el-menu-item>

      <el-menu-item index="/iot/venue" v-if="hasRole(['ADMIN', 'STORE_MANAGER', 'TECHNICIAN'])">
        <el-icon><connection /></el-icon>
        <template #title>Smart Venue (IoT)</template>
      </el-menu-item>

      <el-menu-item index="/monitor" v-if="hasRole(['ADMIN'])">
        <el-icon><monitor /></el-icon>
        <template #title>System Monitor</template>
      </el-menu-item>

      <el-menu-item index="/communication/dashboard">
        <el-icon><chat-dot-round /></el-icon>
        <template #title>{{ $t('home.modules.communication.title') }}</template>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '../stores/auth.store';
import {
  HomeFilled as Home,
  OfficeBuilding,
  Setting,
  Service,
  Tools,
  DataLine,
  Promotion,
  Money,
  ChatDotRound,
  Box,
  UserFilled,
  Document,
  Monitor,
  Connection
} from '@element-plus/icons-vue';

const isCollapsed = ref(false);
const route = useRoute();
const authStore = useAuthStore();

const activeMenu = computed(() => route.path);

const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};

const hasRole = (roles: string[]) => {
    // If no user or no roles, assume guest (only public pages)
    // For MVP, if authStore.user is null, hide everything except Home?
    // Actually, MainLayout is protected, so user must be logged in.
    if (!authStore.user || !authStore.user.roles) return false;
    
    // Check if user has ANY of the required roles
    // Mock: user.roles is array of strings like ['ROLE_ADMIN']
    const userRoles = authStore.user.roles;
    return roles.some(role => userRoles.includes('ROLE_' + role) || userRoles.includes(role));
};
</script>

<style scoped>
.sidebar {
  height: 100vh;
  background-color: #545c64;
  color: #fff;
  transition: width 0.3s;
  overflow-x: hidden;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
  background-color: #434a50;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
</style>
