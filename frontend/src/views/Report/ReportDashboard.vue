<template>
  <div class="report-dashboard">
    <div class="header">
        <h2>Data Analytics & Reporting</h2>
        <div class="filters">
            <el-select v-model="period" placeholder="Select Period" @change="fetchData">
                <el-option label="Weekly" value="WEEKLY" />
                <el-option label="Monthly" value="MONTHLY" />
            </el-select>
            <el-button type="primary" @click="exportReport">Export PDF</el-button>
        </div>
    </div>

    <div class="charts-grid">
        <el-card shadow="hover" class="chart-card">
            <template #header>Revenue Trend</template>
            <div class="chart-container">
                <Line v-if="revenueData" :data="revenueData" :options="chartOptions" />
            </div>
        </el-card>

        <el-card shadow="hover" class="chart-card">
            <template #header>Visitor Peak Hours</template>
            <div class="chart-container">
                <Bar v-if="visitorData" :data="visitorData" :options="chartOptions" />
            </div>
        </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';
import { Line, Bar } from 'vue-chartjs';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend
);

const period = ref('WEEKLY');
const revenueData = ref<any>(null);
const visitorData = ref<any>(null);

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false
};

const fetchData = async () => {
    try {
        const revRes = await apiClient.get('/reports/revenue', { params: { period: period.value } });
        revenueData.value = {
            labels: revRes.data.labels,
            datasets: [{
                label: 'Revenue (CNY)',
                backgroundColor: '#f87979',
                borderColor: '#f87979',
                data: revRes.data.data
            }]
        };

        const visRes = await apiClient.get('/reports/visitors');
        visitorData.value = {
            labels: visRes.data.labels,
            datasets: [{
                label: 'Visitors',
                backgroundColor: '#409eff',
                data: visRes.data.data
            }]
        };
    } catch (error) {
        ElMessage.error('Failed to load report data');
    }
};

const exportReport = () => {
    ElMessage.success('Report exported to PDF (Mock)');
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped>
.report-dashboard {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.filters {
    display: flex;
    gap: 10px;
}
.charts-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
}
.chart-container {
    height: 300px;
}
</style>
