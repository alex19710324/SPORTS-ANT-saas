<template>
  <div class="hr-dashboard">
    <div class="header">
        <h2>Human Resources & Scheduling</h2>
        <el-button type="primary" @click="showAddScheduleDialog = true">Add Schedule</el-button>
    </div>

    <div class="hr-grid">
        <!-- Employee List -->
        <el-card class="employee-card">
            <template #header>Employees</template>
            <el-table :data="employees" style="width: 100%" height="400">
                <el-table-column prop="user.username" label="Name" />
                <el-table-column prop="position" label="Position">
                    <template #default="scope">
                        <el-tag size="small">{{ scope.row.position }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="Status" />
            </el-table>
        </el-card>

        <!-- Calendar -->
        <el-card class="calendar-card">
            <template #header>Shift Calendar</template>
            <el-calendar v-model="currentDate">
                <template #date-cell="{ data }">
                    <p :class="data.isSelected ? 'is-selected' : ''">
                        {{ data.day.split('-').slice(1).join('-') }}
                        <div v-for="shift in getShiftsForDate(data.day)" :key="shift.id" class="shift-item">
                            {{ shift.employee.user.username }} ({{ formatTime(shift.startTime) }})
                        </div>
                    </p>
                </template>
            </el-calendar>
        </el-card>
    </div>

    <!-- Add Schedule Dialog -->
    <el-dialog v-model="showAddScheduleDialog" title="Add Schedule">
        <el-form :model="newSchedule">
            <el-form-item label="Employee">
                <el-select v-model="newSchedule.employeeId">
                    <el-option v-for="emp in employees" :key="emp.id" :label="emp.user.username" :value="emp.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="Time Range">
                <el-date-picker
                    v-model="newSchedule.timeRange"
                    type="datetimerange"
                    start-placeholder="Start Date"
                    end-placeholder="End Date"
                />
            </el-form-item>
            <el-form-item label="Type">
                <el-select v-model="newSchedule.type">
                    <el-option label="Regular" value="REGULAR" />
                    <el-option label="Overtime" value="OVERTIME" />
                </el-select>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showAddScheduleDialog = false">Cancel</el-button>
            <el-button type="primary" @click="submitSchedule">Create</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';

const currentDate = ref(new Date());
const employees = ref<any[]>([]);
const schedules = ref<any[]>([]);
const showAddScheduleDialog = ref(false);

const newSchedule = ref({
    employeeId: null,
    timeRange: [],
    type: 'REGULAR'
});

const fetchEmployees = async () => {
    // Mock for now if backend empty
    employees.value = [
        { id: 1, user: { username: 'Alice' }, position: 'RECEPTIONIST', status: 'ACTIVE' },
        { id: 2, user: { username: 'Bob' }, position: 'TRAINER', status: 'ACTIVE' }
    ];
    // Real call: const res = await apiClient.get('/hr/employees', { params: { storeId: 1 } });
};

const fetchSchedules = async () => {
    // Mock
    schedules.value = [
        { id: 1, employee: { user: { username: 'Alice' } }, startTime: dayjs().hour(9).minute(0).toISOString(), endTime: dayjs().hour(17).minute(0).toISOString() }
    ];
};

const getShiftsForDate = (dateStr: string) => {
    return schedules.value.filter(s => s.startTime.startsWith(dateStr));
};

const formatTime = (timeStr: string) => {
    return dayjs(timeStr).format('HH:mm');
};

const submitSchedule = async () => {
    // Mock submission
    schedules.value.push({
        id: Date.now(),
        employee: employees.value.find(e => e.id === newSchedule.value.employeeId),
        startTime: dayjs(newSchedule.value.timeRange[0]).toISOString(),
        endTime: dayjs(newSchedule.value.timeRange[1]).toISOString()
    });
    showAddScheduleDialog.value = false;
    ElMessage.success('Schedule created');
};

onMounted(() => {
    fetchEmployees();
    fetchSchedules();
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
.hr-grid {
    display: grid;
    grid-template-columns: 1fr 3fr;
    gap: 20px;
}
.shift-item {
    font-size: 0.8em;
    background: #e1f3d8;
    border-radius: 2px;
    padding: 2px;
    margin-top: 2px;
}
</style>
