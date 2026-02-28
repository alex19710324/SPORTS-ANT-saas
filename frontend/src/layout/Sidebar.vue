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
      
      <el-sub-menu index="/hq">
        <template #title>
          <el-icon><office-building /></el-icon>
          <span>{{ $t('home.modules.hq.title') }}</span>
        </template>
        <el-menu-item index="/hq/dashboard">{{ $t('home.modules.hq.desc') }}</el-menu-item>
      </el-sub-menu>

      <el-menu-item index="/workbench/manager">
        <el-icon><setting /></el-icon>
        <template #title>{{ $t('home.modules.manager.title') }}</template>
      </el-menu-item>
      
      <el-menu-item index="/workbench/frontdesk">
        <el-icon><service /></el-icon>
        <template #title>{{ $t('home.modules.frontdesk.title') }}</template>
      </el-menu-item>

      <el-menu-item index="/workbench/technician">
        <el-icon><tools /></el-icon>
        <template #title>{{ $t('home.modules.technician.title') }}</template>
      </el-menu-item>

      <el-menu-item index="/data/dashboard">
        <el-icon><data-line /></el-icon>
        <template #title>{{ $t('home.modules.data.title') }}</template>
      </el-menu-item>

      <el-menu-item index="/marketing/dashboard">
        <el-icon><promotion /></el-icon>
        <template #title>{{ $t('home.modules.marketing.title') }}</template>
      </el-menu-item>

      <el-menu-item index="/finance/dashboard">
        <el-icon><money /></el-icon>
        <template #title>{{ $t('home.modules.finance.title') }}</template>
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
import {
  HomeFilled as Home,
  OfficeBuilding,
  Setting,
  Service,
  Tools,
  DataLine,
  Promotion,
  Money,
  ChatDotRound
} from '@element-plus/icons-vue';

const isCollapsed = ref(false);
const route = useRoute();

const activeMenu = computed(() => route.path);

const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
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
