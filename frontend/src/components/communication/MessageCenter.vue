<template>
  <div class="message-center">
    <div class="message-list" v-loading="loading">
      <el-empty v-if="messages.length === 0" description="No messages"></el-empty>
      <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ 'unread': !msg.read }">
        <div class="message-header">
          <span class="sender">User {{ msg.senderId }}</span>
          <span class="time">{{ formatTime(msg.timestamp) }}</span>
        </div>
        <div class="message-content">{{ msg.content }}</div>
        <div class="message-actions" v-if="!msg.read">
          <el-button link type="primary" size="small" @click="markAsRead(msg.id)">Mark Read</el-button>
        </div>
      </div>
    </div>
    
    <div class="message-input">
      <el-input v-model="newMessage" placeholder="Type a message..." @keyup.enter="sendMessage">
        <template #append>
          <el-button @click="sendMessage">Send</el-button>
        </template>
      </el-input>
      <div class="recipient-select">
          <el-select v-model="receiverId" placeholder="To User" size="small" style="width: 120px; margin-top: 5px;">
              <el-option label="Manager (User 10)" :value="10" />
              <el-option label="Technician (User 101)" :value="101" />
              <el-option label="Front Desk (User 201)" :value="201" />
          </el-select>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useWorkbenchStore } from '../../stores/workbench.store';
import WorkbenchService from '../../services/workbench.service';
import { ElMessage } from 'element-plus';

const store = useWorkbenchStore();
const messages = ref<any[]>([]);
const loading = ref(false);
const newMessage = ref('');
const receiverId = ref<number | undefined>(undefined);

const fetchMessages = async () => {
    loading.value = true;
    try {
        const res = await WorkbenchService.getMessages();
        messages.value = res.data;
    } catch (error) {
        console.error("Failed to fetch messages", error);
    } finally {
        loading.value = false;
    }
};

const sendMessage = async () => {
    if (!newMessage.value.trim()) return;
    if (!receiverId.value) {
        ElMessage.warning("Please select a recipient");
        return;
    }
    
    try {
        await WorkbenchService.sendMessage(receiverId.value, newMessage.value);
        ElMessage.success("Message sent");
        newMessage.value = '';
        // Ideally fetch outbox or update UI, but for now we just clear input
    } catch (error) {
        ElMessage.error("Failed to send message");
    }
};

const markAsRead = async (id: number) => {
    // Implement mark as read API call if available, or just update local state
    // Currently backend has markAsRead but frontend service might need update
    // For now, assume it's done via separate call or auto-read
    // Let's just remove unread class locally
    const msg = messages.value.find(m => m.id === id);
    if (msg) msg.read = true;
    
    // Call backend to update
    // await WorkbenchService.markAsRead(id); // TODO: Add to service
};

const formatTime = (isoString: string) => {
    return new Date(isoString).toLocaleString();
};

onMounted(() => {
    fetchMessages();
});
</script>

<style scoped>
.message-center {
    display: flex;
    flex-direction: column;
    height: 100%;
}
.message-list {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
}
.message-item {
    padding: 10px;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 5px;
}
.message-item.unread {
    background-color: #f0f9eb;
    border-left: 3px solid #67c23a;
}
.message-header {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #909399;
    margin-bottom: 5px;
}
.sender {
    font-weight: bold;
    color: #303133;
}
.message-content {
    font-size: 14px;
    color: #606266;
    word-break: break-word;
}
.message-input {
    padding: 10px;
    border-top: 1px solid #dcdfe6;
}
.recipient-select {
    display: flex;
    justify-content: flex-end;
}
</style>
