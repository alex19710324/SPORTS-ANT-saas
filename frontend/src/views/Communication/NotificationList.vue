<template>
  <div class="notification-list">
    <div class="header">
        <el-button link type="primary" @click="markAllRead">Mark all read</el-button>
    </div>
    
    <el-scrollbar height="calc(100vh - 100px)">
        <el-empty v-if="notifications.length === 0" description="No notifications" />
        
        <div v-else class="notif-container">
            <div 
                v-for="notif in notifications" 
                :key="notif.id" 
                class="notif-item" 
                :class="{ 'unread': !notif.isRead }"
                @click="markRead(notif)"
            >
                <div class="notif-icon">
                    <el-icon v-if="notif.type === 'SYSTEM'"><Setting /></el-icon>
                    <el-icon v-else-if="notif.type === 'MARKETING'"><Promotion /></el-icon>
                    <el-icon v-else-if="notif.type === 'ALERT'"><Warning /></el-icon>
                    <el-icon v-else><Bell /></el-icon>
                </div>
                <div class="notif-content">
                    <div class="notif-title">{{ notif.title }}</div>
                    <div class="notif-msg">{{ notif.message }}</div>
                    <div class="notif-time">{{ formatTime(notif.createdAt) }}</div>
                </div>
                <div class="notif-status" v-if="!notif.isRead"></div>
            </div>
        </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import apiClient from '../../services/api';
import { useAuthStore } from '../../stores/auth.store';
import { Setting, Promotion, Warning, Bell } from '@element-plus/icons-vue';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
dayjs.extend(relativeTime);

const authStore = useAuthStore();
const notifications = ref<any[]>([]);

const fetchNotifications = async () => {
    try {
        const user = authStore.user;
        const userId = user ? user.id : 101; // Mock
        const res = await apiClient.get('/communication/notifications', { params: { userId } });
        notifications.value = res.data;
    } catch (e) {
        console.error("Failed to load notifications");
    }
};

const markRead = async (notif: any) => {
    if (notif.isRead) return;
    try {
        await apiClient.put(`/communication/notifications/${notif.id}/read`);
        notif.isRead = true;
    } catch (e) {
        // Silent fail
    }
};

const markAllRead = async () => {
    // For MVP, loop through (not efficient but simple)
    notifications.value.forEach(n => markRead(n));
};

const formatTime = (time: string) => {
    return dayjs(time).fromNow();
};

onMounted(() => {
    fetchNotifications();
});
</script>

<style scoped>
.notification-list {
    padding: 10px;
}
.header {
    text-align: right;
    margin-bottom: 10px;
}
.notif-item {
    display: flex;
    gap: 15px;
    padding: 15px;
    border-bottom: 1px solid #f0f2f5;
    cursor: pointer;
    transition: background 0.3s;
    position: relative;
}
.notif-item:hover {
    background: #f5f7fa;
}
.notif-item.unread {
    background: #ecf5ff;
}
.notif-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    background: #fff;
    border-radius: 50%;
    color: #409eff;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.notif-content {
    flex: 1;
}
.notif-title {
    font-weight: bold;
    font-size: 14px;
    margin-bottom: 5px;
}
.notif-msg {
    font-size: 13px;
    color: #606266;
    margin-bottom: 5px;
    line-height: 1.4;
}
.notif-time {
    font-size: 12px;
    color: #909399;
}
.notif-status {
    position: absolute;
    top: 15px;
    right: 15px;
    width: 8px;
    height: 8px;
    background: #f56c6c;
    border-radius: 50%;
}
</style>
