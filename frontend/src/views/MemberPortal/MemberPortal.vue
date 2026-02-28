<template>
  <div class="member-portal">
    <div class="header">
        <h2>My Dashboard</h2>
        <div class="balance-card">
            <span>Balance:</span>
            <span class="amount">¥{{ wallet.balance }}</span>
            <el-button type="primary" size="small" @click="showTopUp = true">Top Up</el-button>
        </div>
    </div>

    <div class="portal-grid">
        <el-card class="schedule-card">
            <template #header>My Schedule</template>
            <el-empty v-if="bookings.length === 0" description="No upcoming classes" />
            <div v-else class="booking-list">
                <div v-for="book in bookings" :key="book.id" class="booking-item">
                    <div class="booking-info">
                        <h4>{{ book.className }}</h4>
                        <p>{{ book.time }}</p>
                    </div>
                    <el-tag type="success">Confirmed</el-tag>
                </div>
            </div>
        </el-card>

        <el-card class="history-card">
            <template #header>Transaction History</template>
            <el-table :data="transactions" style="width: 100%" max-height="400">
                <el-table-column prop="createdAt" label="Date" width="120">
                    <template #default="scope">{{ formatDate(scope.row.createdAt) }}</template>
                </el-table-column>
                <el-table-column prop="type" label="Type" />
                <el-table-column prop="amount" label="Amount">
                    <template #default="scope">
                        <span :class="scope.row.amount > 0 ? 'text-success' : 'text-danger'">
                            {{ scope.row.amount }}
                        </span>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>

    <!-- Top Up Modal -->
    <el-dialog v-model="showTopUp" title="Top Up Wallet">
        <el-input-number v-model="topUpAmount" :min="100" :step="100" />
        <template #footer>
            <el-button @click="showTopUp = false">Cancel</el-button>
            <el-button type="primary" @click="handleTopUp">Pay with WeChat</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';

const wallet = ref({ balance: 0, id: 0 });
const bookings = ref<any[]>([]);
const transactions = ref<any[]>([]);
const showTopUp = ref(false);
const topUpAmount = ref(100);

const fetchData = async () => {
    try {
        // Mock member fetching logic (usually via /api/member/me)
        // For MVP, we need an endpoint. Assuming auth user is linked.
        // We'll use a specific endpoint or just mock for now if backend isn't ready for "me" context
        
        // Let's assume we can get wallet via a new endpoint or reusing existing with user context
        // Since we don't have /api/member/me/wallet yet, let's mock the data structure 
        // but try to fetch if possible.
        
        // For demonstration of "Closed Loop", we need to see the balance change after a sale.
        // We need the user's wallet ID.
        // Let's fetch current user info first
        const userStr = localStorage.getItem('user');
        if (userStr) {
            const user = JSON.parse(userStr);
            // Fetch wallet for this user
            // We need a backend endpoint: GET /api/finance/wallet/my
            // Since it doesn't exist, we will mock the display for now, 
            // BUT to show "Closed Loop", we really should implement that endpoint.
            
            // Temporary Mock to allow UI development
            wallet.value = { balance: 1250.00, id: 101 };
            transactions.value = [
                { createdAt: '2025-03-01T10:00:00', type: 'PAYMENT_SENT', amount: -100.00 },
                { createdAt: '2025-02-28T14:00:00', type: 'TOP_UP', amount: 500.00 }
            ];
        }
    } catch (error) {
        console.error("Failed to load member data");
    }
};

const handleTopUp = () => {
    ElMessage.success(`Top up of ¥${topUpAmount.value} successful!`);
    wallet.value.balance += topUpAmount.value;
    transactions.value.unshift({
        createdAt: new Date().toISOString(),
        type: 'TOP_UP',
        amount: topUpAmount.value
    });
    showTopUp.value = false;
};

const formatDate = (date: string) => {
    return dayjs(date).format('MM-DD HH:mm');
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped>
.member-portal {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}
.balance-card {
    display: flex;
    align-items: center;
    gap: 15px;
    font-size: 18px;
}
.amount {
    font-weight: bold;
    color: #409EFF;
    font-size: 24px;
}
.portal-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
}
.text-success { color: #67c23a; }
.text-danger { color: #f56c6c; }
.booking-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    border-bottom: 1px solid #eee;
}
.booking-info h4 { margin: 0 0 5px 0; }
.booking-info p { margin: 0; color: #999; font-size: 14px; }
</style>
