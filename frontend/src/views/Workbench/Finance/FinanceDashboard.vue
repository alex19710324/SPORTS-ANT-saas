<template>
  <div class="finance-dashboard">
    <div class="header">
        <h2>Financial Management</h2>
        <div class="actions">
            <el-button type="primary" @click="fetchData">Refresh</el-button>
        </div>
    </div>

    <div class="stats-cards">
        <el-card shadow="hover">
            <template #header>Today's Revenue</template>
            <h3 class="text-success">¥{{ todayRevenue }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Store Balance</template>
            <h3>¥{{ storeBalance }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Pending Vouchers</template>
            <h3 class="text-warning">5</h3>
        </el-card>
    </div>

    <div class="transaction-section">
        <el-card>
            <template #header>Recent Transactions</template>
            <el-table :data="transactions" style="width: 100%" stripe>
                <el-table-column prop="createdAt" label="Time" width="180">
                    <template #default="scope">{{ formatDate(scope.row.createdAt) }}</template>
                </el-table-column>
                <el-table-column prop="type" label="Type">
                    <template #default="scope">
                        <el-tag :type="getTypeColor(scope.row.type)">{{ scope.row.type }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="amount" label="Amount">
                    <template #default="scope">
                        <span :class="scope.row.amount > 0 ? 'text-success' : 'text-danger'">
                            {{ scope.row.amount > 0 ? '+' : '' }}{{ scope.row.amount }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="description" label="Description" />
                <el-table-column prop="balanceAfter" label="Balance After" width="120" />
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

const todayRevenue = ref(0);
const storeBalance = ref(0);
const transactions = ref<any[]>([]);

const fetchData = async () => {
    try {
        const revRes = await apiClient.get('/finance/revenue/today');
        todayRevenue.value = revRes.data;

        // Assuming Store Wallet ID = 1 for MVP
        const transRes = await apiClient.get('/finance/transactions', { params: { walletId: 1 } });
        transactions.value = transRes.data;
        
        if (transactions.value.length > 0) {
            storeBalance.value = transactions.value[0].balanceAfter;
        }
    } catch (error) {
        ElMessage.error('Failed to load finance data');
    }
};

const formatDate = (date: string) => {
    return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
};

const getTypeColor = (type: string) => {
    if (type === 'PAYMENT_RECEIVED') return 'success';
    if (type === 'PAYMENT_SENT') return 'danger';
    return 'info';
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
</style>
