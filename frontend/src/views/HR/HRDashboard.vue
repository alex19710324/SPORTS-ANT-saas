<template>
  <div class="hr-dashboard">
    <div class="header">
        <h2>Human Resources</h2>
        <el-button type="primary">Add Staff</el-button>
    </div>

    <div class="kpi-row">
        <el-card shadow="hover">
            <template #header>Total Staff</template>
            <h3>{{ staffList.length }}</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Avg Attendance</template>
            <h3 class="text-success">96%</h3>
        </el-card>
        <el-card shadow="hover">
            <template #header>Est. Payroll</template>
            <h3>¥{{ payroll.totalPayroll }}</h3>
        </el-card>
    </div>

    <el-tabs type="border-card">
        <el-tab-pane label="Staff Management">
            <el-table :data="staffList" style="width: 100%">
                <el-table-column prop="username" label="Name" />
                <el-table-column prop="email" label="Email" />
                <el-table-column label="Roles">
                    <template #default="scope">
                        <el-tag v-for="role in scope.row.roles" :key="role" size="small" style="margin-right: 5px">{{ role }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="rating" label="Performance" width="180">
                    <template #default="scope">
                        <el-rate v-model="scope.row.rating" disabled show-score text-color="#ff9900" />
                    </template>
                </el-table-column>
                <el-table-column label="Actions">
                    <template #default="scope">
                        <el-button size="small" @click="viewProfile(scope.row)">View</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="Payroll & Performance">
            <el-table :data="payroll.breakdown" style="width: 100%">
                <el-table-column prop="name" label="Employee" />
                <el-table-column prop="base" label="Base Salary">
                    <template #default="scope">¥{{ scope.row.base }}</template>
                </el-table-column>
                <el-table-column prop="bonus" label="Bonus (Commission)">
                    <template #default="scope">¥{{ scope.row.bonus }}</template>
                </el-table-column>
                <el-table-column prop="total" label="Total Payout" sortable>
                    <template #default="scope"><strong>¥{{ scope.row.total }}</strong></template>
                </el-table-column>
            </el-table>
        </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import apiClient from '../../services/api';

const staffList = ref<any[]>([]);
const payroll = ref<any>({ totalPayroll: 0, breakdown: [] });

const fetchData = async () => {
    try {
        const staffRes = await apiClient.get('/hr/staff');
        staffList.value = staffRes.data;

        const payRes = await apiClient.get('/hr/payroll');
        payroll.value = payRes.data;
    } catch (error) {
        ElMessage.error('Failed to load HR data');
    }
};

const viewProfile = (staff: any) => {
    ElMessage.info(`View profile for ${staff.username}`);
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped>
.hr-dashboard {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.kpi-row {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin-bottom: 20px;
}
.text-success { color: #67c23a; }
</style>
