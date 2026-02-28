<template>
  <div class="data-dashboard">
    <div class="header">
        <h2>Data Intelligence</h2>
        <div class="actions">
            <el-date-picker v-model="dateRange" type="daterange" range-separator="To" start-placeholder="Start date" end-placeholder="End date" />
        </div>
    </div>

    <div class="kpi-grid">
        <el-card shadow="hover">
            <template #header>Total Revenue</template>
            <h3>¥{{ kpis.totalRevenue }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Active Stores</template>
            <h3>{{ kpis.activeStores }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Total Members</template>
            <h3>{{ kpis.totalMembers }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Connected Devices</template>
            <h3>{{ kpis.totalDevices }}</h3>
        </el-card>
    </div>

    <div class="charts-row">
        <el-card class="chart-card">
            <template #header>Revenue Trend (Last 7 Days)</template>
            <v-chart class="chart" :option="revenueOption" autoresize />
        </el-card>
        
        <el-card class="chart-card">
            <template #header>Store Leaderboard (Top 5)</template>
            <el-table :data="leaderboard" style="width: 100%" :show-header="false">
                <el-table-column type="index" width="50" />
                <el-table-column prop="name" />
                <el-table-column prop="revenue" align="right">
                    <template #default="scope">¥{{ scope.row.revenue }}</template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { LineChart, BarChart } from 'echarts/charts';
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import apiClient from '../../services/api';

use([CanvasRenderer, LineChart, BarChart, GridComponent, TooltipComponent, LegendComponent]);

const dateRange = ref([]);
const kpis = ref({
    totalRevenue: 0,
    activeStores: 0,
    totalMembers: 0,
    totalDevices: 0
});
const revenueData = ref({ dates: [], values: [] });
const leaderboard = ref([]);

const revenueOption = computed(() => ({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: revenueData.value.dates },
    yAxis: { type: 'value' },
    series: [{
        data: revenueData.value.values,
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 },
        itemStyle: { color: '#409EFF' }
    }]
}));

const fetchData = async () => {
    try {
        const kpiRes = await apiClient.get('/data/kpi');
        kpis.value = kpiRes.data;

        const trendRes = await apiClient.get('/data/revenue-trend');
        revenueData.value = trendRes.data;

        const leadRes = await apiClient.get('/data/store-leaderboard');
        leaderboard.value = leadRes.data.slice(0, 5);
    } catch (error) {
        console.error("Failed to load data");
    }
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped>
.data-dashboard {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.kpi-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 20px;
}
.charts-row {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;
}
.chart {
    height: 300px;
}
</style>
