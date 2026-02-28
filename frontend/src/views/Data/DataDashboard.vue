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
            <template #header>Member Growth Trend</template>
            <v-chart class="chart" :option="growthOption" autoresize />
        </el-card>
        
        <el-card class="chart-card">
            <template #header>Revenue Composition</template>
            <v-chart class="chart" :option="pieOption" autoresize />
        </el-card>
    </div>
    
    <div class="charts-row" style="margin-top: 20px">
         <el-card class="chart-card">
            <template #header>Peak Hours (Occupancy)</template>
            <v-chart class="chart" :option="peakOption" autoresize />
        </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { LineChart, BarChart, PieChart } from 'echarts/charts';
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import apiClient from '../../services/api';

use([CanvasRenderer, LineChart, BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent]);

const dateRange = ref([]);
const kpis = ref<any>({
    totalRevenue: 0,
    activeStores: 0, // Mock
    totalMembers: 0,
    totalDevices: 0 // Mock
});

const memberGrowth = ref<number[]>([]);
const peakHours = ref<any>({});
const categorySales = ref<any>({});

const growthOption = computed(() => ({
    title: { text: 'New Members (Last 7 Days)' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'] },
    yAxis: { type: 'value' },
    series: [{
        data: memberGrowth.value,
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 },
        itemStyle: { color: '#409EFF' }
    }]
}));

const pieOption = computed(() => ({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: Object.keys(categorySales.value).map(key => ({
            name: key,
            value: categorySales.value[key]
        }))
    }]
}));

const peakOption = computed(() => ({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: Object.keys(peakHours.value).sort() },
    yAxis: { type: 'value' },
    series: [{
        data: Object.keys(peakHours.value).sort().map(k => peakHours.value[k]),
        type: 'bar',
        itemStyle: { color: '#67c23a' }
    }]
}));

const fetchData = async () => {
    try {
        const res = await apiClient.get('/analytics/dashboard');
        const data = res.data;
        
        kpis.value.totalRevenue = data.totalRevenue;
        kpis.value.totalMembers = 1205; // Mock total
        
        memberGrowth.value = data.memberGrowth;
        peakHours.value = data.peakHours;
        categorySales.value = data.categorySales;
        
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
