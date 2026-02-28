<template>
  <div class="communication-dashboard">
    <div class="header">
        <h2>Communication Center</h2>
    </div>
    
    <div class="send-panel">
      <el-card>
        <template #header>
            <div class="card-header">
                <span>Broadcast Message</span>
                <el-tag type="warning">Marketing</el-tag>
            </div>
        </template>
        <el-form :model="form" label-width="120px">
          <el-form-item label="Title">
            <el-input v-model="form.title" placeholder="e.g. Summer Sale!" />
          </el-form-item>
          <el-form-item label="Message">
            <el-input type="textarea" v-model="form.message" :rows="4" placeholder="Enter your message here..." />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleBroadcast" :loading="sending">
                <el-icon><promotion /></el-icon> Send to All Members
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <div class="history-panel">
      <h3>Recent Broadcasts</h3>
      <el-table :data="history" style="width: 100%">
        <el-table-column prop="title" label="Title" />
        <el-table-column prop="message" label="Message" show-overflow-tooltip />
        <el-table-column prop="type" label="Type">
          <template #default="scope">
            <el-tag>{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Time">
            <template #default="scope">{{ formatDate(scope.row.createdAt) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';
import { Promotion } from '@element-plus/icons-vue';
import dayjs from 'dayjs';

const form = reactive({
  title: '',
  message: ''
});

const sending = ref(false);
const history = ref<any[]>([]);

const handleBroadcast = async () => {
  if (!form.title || !form.message) {
      ElMessage.warning('Please fill in title and message');
      return;
  }
  
  sending.value = true;
  try {
    await apiClient.post('/communication/broadcast', form);
    ElMessage.success('Broadcast sent successfully!');
    // Add to local history for MVP feedback
    history.value.unshift({
        title: form.title,
        message: form.message,
        type: 'MARKETING',
        createdAt: new Date().toISOString()
    });
    form.title = '';
    form.message = '';
  } catch (e) {
    ElMessage.error('Failed to send broadcast');
  } finally {
    sending.value = false;
  }
};

const formatDate = (date: string) => {
    return dayjs(date).format('YYYY-MM-DD HH:mm');
};
</script>

<style scoped>
.communication-dashboard {
    padding: 20px;
}
.header {
    margin-bottom: 20px;
}
.send-panel {
  margin-bottom: 30px;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
