<template>
  <div class="report-dashboard">
    <div class="header">
        <h2>Report Center</h2>
        <div class="actions">
            <el-select v-model="reportType" placeholder="Select Report Type" style="width: 200px; margin-right: 10px">
                <el-option label="Financial Report" value="FINANCE" />
                <el-option label="Inventory Report" value="INVENTORY" />
                <el-option label="Operations Report" value="OPERATIONS" />
            </el-select>
            <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="To"
                start-placeholder="Start date"
                end-placeholder="End date"
                style="margin-right: 10px"
            />
            <el-button type="primary" :loading="loading" @click="generateReport">Generate & Export</el-button>
        </div>
    </div>

    <div class="preview-section" v-if="previewData.length > 0">
        <el-card>
            <template #header>Preview: {{ reportType }} Report</template>
            <el-table :data="previewData" style="width: 100%" height="500" border>
                <el-table-column v-for="col in columns" :key="col" :prop="col" :label="formatHeader(col)" />
            </el-table>
        </el-card>
    </div>
    
    <el-empty v-else description="Select criteria and click Generate to view report" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';
import * as XLSX from 'xlsx';

const reportType = ref('FINANCE');
const dateRange = ref([]);
const loading = ref(false);
const previewData = ref<any[]>([]);
const columns = ref<string[]>([]);

const generateReport = async () => {
    loading.value = true;
    try {
        let endpoint = '';
        if (reportType.value === 'FINANCE') endpoint = '/finance/transactions';
        else if (reportType.value === 'INVENTORY') endpoint = '/inventory';
        else endpoint = '/data/store-leaderboard'; // Mock for Operations

        const res = await apiClient.get(endpoint, { 
            params: { storeId: 1, limit: 1000 } // Fetch ample data for report
        });
        
        previewData.value = Array.isArray(res.data) ? res.data : [res.data];
        
        if (previewData.value.length > 0) {
            columns.value = Object.keys(previewData.value[0]);
            exportToExcel(previewData.value, `${reportType.value}_Report_${new Date().toISOString().slice(0,10)}`);
        } else {
            ElMessage.info('No data found for the selected criteria');
        }
    } catch (error) {
        ElMessage.error('Failed to generate report');
    } finally {
        loading.value = false;
    }
};

const exportToExcel = (data: any[], filename: string) => {
    const ws = XLSX.utils.json_to_sheet(data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Report");
    XLSX.writeFile(wb, `${filename}.xlsx`);
    ElMessage.success('Report downloaded successfully');
};

const formatHeader = (key: string) => {
    return key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, ' $1');
};
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
</style>
