<template>
  <div class="finance-dashboard">
    <div class="header">
        <h2>Financial Management</h2>
        <div class="actions">
            <el-date-picker 
                v-model="dateRange" 
                type="daterange" 
                range-separator="To" 
                start-placeholder="Start date" 
                end-placeholder="End date"
                @change="fetchData"
            />
            <el-button type="success" @click="fetchData">Generate Report</el-button>
        </div>
    </div>

    <div class="stats-cards">
        <el-card shadow="hover">
            <template #header>Total Income</template>
            <h3 class="text-success">¥{{ formatMoney(report.totalIncome) }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Total Expense</template>
            <h3 class="text-danger">¥{{ formatMoney(report.totalExpense) }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Net Profit</template>
            <h3 :class="report.netProfit >= 0 ? 'text-success' : 'text-danger'">
                ¥{{ formatMoney(report.netProfit) }}
                <small>({{ report.margin.toFixed(1) }}%)</small>
            </h3>
        </el-card>
    </div>

    <div class="transaction-section">
        <el-card>
            <template #header>Transaction Audit</template>
            <el-table :data="transactions" style="width: 100%" stripe>
                <el-table-column prop="transactionDate" label="Date" width="180">
                    <template #default="scope">{{ formatDate(scope.row.transactionDate) }}</template>
                </el-table-column>
                <el-table-column prop="type" label="Type" width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.type === 'INCOME' ? 'success' : 'danger'">{{ scope.row.type }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="category" label="Category" width="120" />
                <el-table-column prop="description" label="Description" />
                <el-table-column prop="amount" label="Amount" align="right">
                    <template #default="scope">
                        <span :class="scope.row.type === 'INCOME' ? 'text-success' : 'text-danger'">
                            {{ scope.row.type === 'INCOME' ? '+' : '-' }}¥{{ formatMoney(scope.row.amount) }}
                        </span>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import apiClient from '../../../services/api';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';

const dateRange = ref([
    dayjs().startOf('month').toDate(),
    dayjs().endOf('month').toDate()
]);

const report = ref({
    totalIncome: 0,
    totalExpense: 0,
    netProfit: 0,
    margin: 0
});

const transactions = ref<any[]>([]);

const fetchData = async () => {
    if (!dateRange.value) return;
    
    const start = dayjs(dateRange.value[0]).format('YYYY-MM-DD');
    const end = dayjs(dateRange.value[1]).format('YYYY-MM-DD');

    try {
        const reportRes = await apiClient.get('/finance/statement', { params: { start, end } });
        report.value = reportRes.data;

        const transRes = await apiClient.get('/finance/transactions', { params: { start, end } });
        transactions.value = transRes.data;
    } catch (error) {
        ElMessage.error('Failed to load finance data');
    }
};

const formatDate = (date: string) => {
    return dayjs(date).format('YYYY-MM-DD HH:mm');
};

const formatMoney = (val: number) => {
    return val.toFixed(2);
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped>
.finance-dashboard {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.stats-cards {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin-bottom: 20px;
}
.text-success { color: #67c23a; }
.text-danger { color: #f56c6c; }
.text-warning { color: #e6a23c; }
small { font-size: 0.6em; color: #909399; margin-left: 5px; }
</style>
