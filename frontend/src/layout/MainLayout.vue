<template>
  <el-container class="layout-container">
    <Sidebar />
    
    <el-container>
      <Navbar />
      
      <el-main>
        <router-view />
      </el-main>
      
      <el-footer class="footer">
        © 2025 SPORTS ANT. All Rights Reserved.
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import Sidebar from './Sidebar.vue';
import Navbar from './Navbar.vue';
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.footer {
  text-align: center;
  padding: 20px;
  background-color: #f5f7fa;
  color: #909399;
}
</style>
