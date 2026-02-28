<template>
  <div class="hq-dashboard" v-loading="loading">
    <div class="header">
        <h2>{{ $t('hq.title') }}</h2>
        <div class="actions">
            <el-button type="primary" @click="fetchData">Refresh Global Data</el-button>
        </div>
    </div>
    
    <div class="overview-grid" v-if="overview">
      <el-card shadow="hover">
        <template #header>{{ $t('hq.revenue') }}</template>
        <h3>¥{{ overview.totalRevenue }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Total Stores</template>
        <h3>{{ overview.activeStores }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Total Members</template>
        <h3>{{ overview.totalMembers }}</h3>
      </el-card>
      <el-card shadow="hover">
        <template #header>Connected Devices</template>
        <h3>{{ overview.totalDevices }}</h3>
      </el-card>
    </div>

    <div class="dashboard-split">
        <el-card class="map-card">
            <template #header>Global Store Map</template>
            <div class="map-container">
                <v-chart class="chart" :option="mapOption" autoresize />
            </div>
        </el-card>

        <el-card class="leaderboard-card">
            <template #header>Top Performing Stores</template>
            <el-table :data="leaderboard" style="width: 100%">
                <el-table-column type="index" width="50" />
                <el-table-column prop="name" label="Store" />
                <el-table-column prop="revenue" label="Revenue" align="right">
                    <template #default="scope">¥{{ scope.row.revenue }}</template>
                </el-table-column>
                <el-table-column prop="score" label="Score" width="80">
                    <template #default="scope">
                        <el-tag :type="scope.row.score >= 90 ? 'success' : 'warning'">{{ scope.row.score }}</el-tag>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { ScatterChart, EffectScatterChart } from 'echarts/charts';
import { GeoComponent, TooltipComponent, TitleComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import apiClient from '../../services/api';

// Register ECharts components
use([CanvasRenderer, ScatterChart, EffectScatterChart, GeoComponent, TooltipComponent, TitleComponent]);

const loading = ref(false);
const overview = ref<any>({});
const leaderboard = ref<any[]>([]);

// Mock GeoJSON for China Map (simplified) or just use coordinates on a blank canvas for MVP
// For MVP without downloading large map files, we simulate a "Coordinate System" or use a simple scatter plot
const mapOption = computed(() => ({
    backgroundColor: '#f3f3f3',
    title: { text: 'Store Locations', left: 'center' },
    tooltip: { trigger: 'item' },
    geo: {
        map: 'china', // Requires registering map data, skipping for MVP, using coordinate system instead
        roam: true,
        label: { show: false },
        itemStyle: {
            areaColor: '#323c48',
            borderColor: '#111'
        }
    },
    // Fallback: Simple Scatter if map not loaded
    xAxis: { show: false, min: 70, max: 140 }, // Longitude range for China roughly
    yAxis: { show: false, min: 15, max: 55 },  // Latitude range for China roughly
    series: [
        {
            name: 'Stores',
            type: 'effectScatter',
            coordinateSystem: 'cartesian2d', // Using cartesian as mock map
            data: [
                [116.40, 39.90, 'Beijing Store'], // Beijing
                [121.47, 31.23, 'Shanghai Store'], // Shanghai
                [113.26, 23.12, 'Guangzhou Store'], // Guangzhou
                [104.06, 30.67, 'Chengdu Store'], // Chengdu
                [114.30, 30.59, 'Wuhan Store']   // Wuhan
            ],
            symbolSize: 20,
            label: {
                formatter: '{@[2]}',
                position: 'right',
                show: true
            },
            itemStyle: {
                color: '#ddb926'
            }
        }
    ]
}));

const fetchData = async () => {
    loading.value = true;
    try {
        const kpiRes = await apiClient.get('/data/kpi');
        overview.value = kpiRes.data;

        const leadRes = await apiClient.get('/data/store-leaderboard');
        leaderboard.value = leadRes.data;
    } catch (error) {
        console.error("Failed to load HQ data");
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped>
.hq-dashboard {
  padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.dashboard-split {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;
}
.map-container {
    height: 500px;
    background: #eef;
    border-radius: 4px;
    overflow: hidden;
}
.chart {
    height: 100%;
    width: 100%;
}
</style>
