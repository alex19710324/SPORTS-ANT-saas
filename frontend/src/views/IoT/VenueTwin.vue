<template>
  <div class="venue-twin">
    <div class="header">
        <h2>Digital Twin: Venue Status</h2>
        <div class="legend">
            <span class="dot occupied"></span> Occupied
            <span class="dot empty"></span> Empty
            <span class="dot light-on"></span> Light On
        </div>
    </div>

    <div class="zone-grid">
        <el-card v-for="zone in zones" :key="zone.id" class="zone-card" :class="{ 'is-occupied': zone.occupancy > 0 }">
            <template #header>
                <div class="zone-header">
                    <span>{{ zone.name }}</span>
                    <el-tag size="small" :type="zone.occupancy > 0 ? 'success' : 'info'">
                        {{ zone.occupancy > 0 ? 'Occupied' : 'Empty' }}
                    </el-tag>
                </div>
            </template>
            
            <div class="zone-stats">
                <div class="stat">
                    <el-icon><user /></el-icon>
                    <span>{{ zone.occupancy }} people</span>
                </div>
                <div class="stat">
                    <el-icon><sunny /></el-icon>
                    <span>{{ zone.temperature.toFixed(1) }}°C</span>
                </div>
                <div class="stat">
                    <el-icon><lightning /></el-icon>
                    <el-switch v-model="zone.lightsOn" disabled active-text="Lights" />
                </div>
            </div>

            <div class="zone-actions">
                <el-button size="small" @click="overrideZone(zone)">Manual Override</el-button>
            </div>
        </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import apiClient from '../../services/api';
import { User, Sunny, Lightning } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const zones = ref<any[]>([]);
const timer = ref<any>(null);

const fetchZones = async () => {
    try {
        const res = await apiClient.get('/iot/venue/zones');
        zones.value = res.data;
    } catch (e) {
        console.error("Failed to load venue data");
    }
};

const overrideZone = (zone: any) => {
    ElMessage.info(`Manual override for ${zone.name} requested. Not implemented in MVP.`);
};

onMounted(() => {
    fetchZones();
    timer.value = setInterval(fetchZones, 3000); // Live update every 3s
});

onUnmounted(() => {
    if (timer.value) clearInterval(timer.value);
});
</script>

<style scoped>
.venue-twin {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.zone-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
}
.zone-card {
    transition: all 0.3s;
    border: 1px solid #ebeef5;
}
.zone-card.is-occupied {
    border-color: #67c23a;
    box-shadow: 0 0 10px rgba(103, 194, 58, 0.2);
}
.zone-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
}
.zone-stats {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-bottom: 15px;
}
.stat {
    display: flex;
    align-items: center;
    gap: 10px;
    color: #606266;
}
.legend {
    display: flex;
    gap: 15px;
    font-size: 14px;
    color: #606266;
}
.dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    display: inline-block;
}
.dot.occupied { background: #67c23a; }
.dot.empty { background: #909399; }
.dot.light-on { background: #409eff; }
</style>
