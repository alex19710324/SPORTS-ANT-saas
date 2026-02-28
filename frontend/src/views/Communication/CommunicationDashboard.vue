<template>
  <div class="communication-dashboard">
    <h2>Communication Center</h2>
    
    <div class="send-panel">
      <el-card>
        <template #header>Send Notification</template>
        <el-form :model="form" label-width="120px">
          <el-form-item label="Channel">
            <el-radio-group v-model="form.channel">
              <el-radio label="SMS">SMS</el-radio>
              <el-radio label="EMAIL">Email</el-radio>
              <el-radio label="WECHAT">WeChat</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="Recipient">
            <el-input v-model="form.recipient" placeholder="Phone / Email / OpenID" />
          </el-form-item>
          <el-form-item label="Subject" v-if="form.channel !== 'SMS'">
            <el-input v-model="form.subject" />
          </el-form-item>
          <el-form-item label="Content">
            <el-input type="textarea" v-model="form.content" :rows="4" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSend" :loading="sending">Send</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <div class="history-panel">
      <h3>History</h3>
      <el-table :data="history" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="channel" label="Channel" width="100">
          <template #default="scope">
            <el-tag>{{ scope.row.channel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recipient" label="Recipient" />
        <el-table-column prop="status" label="Status" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'SENT' ? 'success' : 'danger'">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Time" />
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import axios from 'axios'; // Quick hack, should use service

// Mock service for now, or use real one if I created it
const api = axios.create({ baseURL: 'http://localhost:8080/api' });

const form = reactive({
  channel: 'SMS',
  recipient: '',
  subject: '',
  content: ''
});

const sending = ref(false);
const history = ref([]);

const handleSend = async () => {
  sending.value = true;
  try {
    // Need to implement Auth token logic or disable security for dev
    // Assuming simple post for now
    await api.post('/communication/send', form);
    alert('Sent!');
    fetchHistory();
  } catch (e) {
    console.error(e);
    alert('Failed to send');
  } finally {
    sending.value = false;
  }
};

const fetchHistory = async () => {
  try {
    const res = await api.get('/communication/history');
    history.value = res.data;
  } catch (e) {
    console.error(e);
  }
};

onMounted(() => {
  // Mock data for UI if backend not ready
  history.value = [
    { id: 1, channel: 'SMS', recipient: '13800138000', status: 'SENT', createdAt: '2025-03-02 10:00:00' }
  ];
  // Uncomment when backend is ready and auth is handled
  // fetchHistory();
});
</script>

<style scoped>
.send-panel {
  margin-bottom: 30px;
}
</style>
