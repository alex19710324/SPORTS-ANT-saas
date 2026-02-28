<template>
  <el-header class="navbar">
    <div class="navbar-left">
      <div class="logo">
        <img src="../assets/vue.svg" alt="Logo" class="logo-img" v-if="hasLogo" />
        <span class="logo-text">SPORTS ANT SaaS</span>
      </div>
    </div>
    
    <div class="navbar-right">
      <div class="message-badge" @click="showMessageCenter = true">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="item">
              <el-icon :size="20"><Message /></el-icon>
          </el-badge>
      </div>

      <el-dropdown trigger="click" @command="handleCommand">
        <span class="user-profile">
          <el-avatar :size="32" :src="userAvatar">{{ userInitials }}</el-avatar>
          <span class="username">{{ username }}</span>
          <el-icon class="el-icon--right"><arrow-down /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">Profile</el-dropdown-item>
            <el-dropdown-item command="settings">Settings</el-dropdown-item>
            <el-dropdown-item divided command="logout">Logout</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- Message Center Drawer -->
    <el-drawer v-model="showMessageCenter" title="Message Center" size="400px" direction="rtl">
        <MessageCenter />
    </el-drawer>
  </el-header>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth.store';
import { ArrowDown, Message } from '@element-plus/icons-vue';
import { useWorkbenchStore } from '../stores/workbench.store';
import MessageCenter from '../components/communication/MessageCenter.vue';

const router = useRouter();
const authStore = useAuthStore();
const workbenchStore = useWorkbenchStore();

const hasLogo = true; // Set to true if logo exists
const userAvatar = ''; // URL to user avatar
const showMessageCenter = ref(false);
const unreadCount = ref(0);

const username = computed(() => {
  const user = authStore.user;
  return user ? user.username : 'Guest';
});

const userInitials = computed(() => {
  return username.value.substring(0, 2).toUpperCase();
});

onMounted(async () => {
    // Poll for unread messages every 30 seconds
    await checkUnread();
    setInterval(checkUnread, 30000);
});

const checkUnread = async () => {
    try {
        unreadCount.value = await workbenchStore.getUnreadMessageCount();
    } catch (e) {
        console.error("Failed to check unread messages");
    }
};

const handleCommand = (command: string) => {
  if (command === 'logout') {
    authStore.logout();
    router.push('/login');
  } else if (command === 'profile') {
    // Navigate to profile
  }
};
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 20px;
  color: #303133;
}

.logo-img {
  height: 40px;
  margin-right: 10px;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.message-badge {
    cursor: pointer;
    display: flex;
    align-items: center;
    color: #606266;
}

.message-badge:hover {
    color: #409eff;
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}

.username {
  margin: 0 8px;
  font-weight: 500;
}
</style>
