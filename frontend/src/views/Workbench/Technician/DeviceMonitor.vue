<template>
  <div class="device-monitor">
    <h2>Device Monitoring</h2>
    
    <div class="monitor-grid">
      <!-- Device Card -->
      <el-card v-for="device in devices" :key="device.id" class="device-card" :class="device.status.toLowerCase()">
        <template #header>
          <div class="card-header">
            <span>{{ device.name }}</span>
            <el-tag :type="getStatusType(device.status)">{{ device.status }}</el-tag>
          </div>
        </template>
        <div class="device-info">
          <p>Serial: {{ device.serialNumber }}</p>
          <p>Loc: {{ device.location }}</p>
          <div class="params" v-if="device.parametersJson">
            <pre>{{ JSON.parse(device.parametersJson) }}</pre>
          </div>
        </div>
        <div class="actions">
          <el-button size="small" type="primary" @click="handleAction(device, 'RESTART')">Restart</el-button>
          <el-button size="small" type="danger" v-if="device.status === 'FAULT'" @click="handleAction(device, 'REPAIR')">Repair</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
// import { useWorkbenchStore } from '../../../stores/workbench.store'; // Reuse for now or create new

// Mock Data for now as we don't have a dedicated Device API exposed to frontend yet
// Ideally, fetch from backend
const devices = ref([
    { id: 1, name: 'VR Headset A', serialNumber: 'VR-001', location: 'Zone A', status: 'ONLINE', parametersJson: '{"temp": 45, "fps": 90}' },
    { id: 2, name: 'Entrance Gate', serialNumber: 'GATE-001', location: 'Entrance', status: 'FAULT', parametersJson: '{"error": "E01"}' },
    { id: 3, name: 'Arcade Machine', serialNumber: 'ARC-005', location: 'Zone B', status: 'OFFLINE', parametersJson: '{}' }
]);

const getStatusType = (status: string) => {
    switch(status) {
        case 'ONLINE': return 'success';
        case 'FAULT': return 'danger';
        case 'OFFLINE': return 'info';
        default: return 'warning';
    }
};

const handleAction = (device: any, action: string) => {
    console.log(`Action ${action} on ${device.name}`);
    // Call backend API
};
</script>

<style scoped>
.monitor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.device-card.fault {
    border: 1px solid #f56c6c;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.params pre {
    background: #f4f4f5;
    padding: 5px;
    border-radius: 4px;
    font-size: 0.8em;
}

.actions {
    margin-top: 15px;
    display: flex;
    justify-content: flex-end;
}
</style>
