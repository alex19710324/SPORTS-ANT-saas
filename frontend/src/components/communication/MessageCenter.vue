<template>
  <div class="message-center">
    <div class="message-list" v-loading="loading">
      <el-empty v-if="notifications.length === 0" description="No new notifications"></el-empty>
      <div v-for="notif in notifications" :key="notif.id" class="notification-item" :class="{ 'unread': !notif.read }">
        <div class="notif-header">
            <el-tag size="small" :type="getTypeColor(notif.type)">{{ notif.type }}</el-tag>
            <span class="time">{{ formatTime(notif.createdAt) }}</span>
        </div>
        <h4 class="notif-title">{{ notif.title }}</h4>
        <p class="notif-message">{{ notif.message }}</p>
        <div class="notif-actions" v-if="notif.link">
            <el-button link type="primary" size="small" @click="handleAction(notif)">View Details</el-button>
        </div>
        <div class="notif-actions" v-if="!notif.read">
            <el-button link type="info" size="small" @click="markAsRead(notif.id)">Mark Read</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import apiClient from '../../services/api';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';

const router = useRouter();
const notifications = ref<any[]>([]);
const loading = ref(false);

const fetchNotifications = async () => {
    loading.value = true;
    try {
        const res = await apiClient.get('/notifications');
        notifications.value = res.data;
    } catch (error) {
        console.error("Failed to fetch notifications");
    } finally {
        loading.value = false;
    }
};

const markAsRead = async (id: number) => {
    try {
        await apiClient.post(`/notifications/${id}/read`);
        const notif = notifications.value.find(n => n.id === id);
        if (notif) notif.read = true;
    } catch (error) {
        ElMessage.error('Failed to mark as read');
    }
};

const handleAction = (notif: any) => {
    if (notif.link) {
        router.push(notif.link);
        if (!notif.read) markAsRead(notif.id);
    }
};

const getTypeColor = (type: string) => {
    if (type === 'ERROR' || type === 'CRITICAL') return 'danger';
    if (type === 'WARNING') return 'warning';
    if (type === 'SUCCESS') return 'success';
    return 'info';
};

const formatTime = (isoString: string) => {
    return dayjs(isoString).format('MM-DD HH:mm');
};

onMounted(() => {
    fetchNotifications();
});
</script>

<style scoped>
.message-center {
    height: 100%;
    display: flex;
    flex-direction: column;
}
.message-list {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
}
.notification-item {
    padding: 12px;
    border-bottom: 1px solid #eee;
    margin-bottom: 8px;
    border-radius: 4px;
    background: #fff;
    transition: background 0.3s;
}
.notification-item.unread {
    background-color: #f0f9eb;
    border-left: 3px solid #67c23a;
}
.notif-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 5px;
}
.time {
    font-size: 12px;
    color: #999;
}
.notif-title {
    margin: 0 0 5px 0;
    font-size: 14px;
    font-weight: 600;
}
.notif-message {
    margin: 0;
    font-size: 13px;
    color: #666;
    line-height: 1.4;
}
.notif-actions {
    margin-top: 8px;
    text-align: right;
}
</style>
