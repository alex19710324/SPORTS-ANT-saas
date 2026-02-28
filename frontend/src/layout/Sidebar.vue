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
    >
      <el-menu-item index="/">
        <el-icon><home /></el-icon>
        <template #title>Home</template>
      </el-menu-item>
      
      <el-sub-menu index="/hq">
        <template #title>
          <el-icon><office-building /></el-icon>
          <span>Headquarters</span>
        </template>
        <el-menu-item index="/hq/dashboard">Dashboard</el-menu-item>
        <el-menu-item index="/hq/franchise">Franchise</el-menu-item>
      </el-sub-menu>

      <el-menu-item index="/store/manager">
        <el-icon><setting /></el-icon>
        <template #title>Store Manager</template>
      </el-menu-item>
      
      <el-menu-item index="/store/frontdesk">
        <el-icon><service /></el-icon>
        <template #title>Front Desk</template>
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
  Service
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
