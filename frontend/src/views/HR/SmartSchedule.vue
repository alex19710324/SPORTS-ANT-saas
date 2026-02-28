<template>
  <div class="smart-schedule">
    <div class="header">
        <h2>Workforce Management</h2>
        <div class="actions">
            <el-date-picker v-model="currentWeek" type="week" format="ww [Week]" placeholder="Select Week" @change="fetchShifts" />
            <el-button type="primary" :loading="loading" @click="generateSchedule">
                <el-icon><magic-stick /></el-icon> AI Auto-Schedule
            </el-button>
        </div>
    </div>

    <div class="schedule-grid">
        <div class="day-col" v-for="(day, index) in weekDays" :key="index">
            <div class="day-header">
                {{ day.format('ddd') }}
                <span>{{ day.format('MM/DD') }}</span>
            </div>
            <div class="shifts-container">
                <div v-for="shift in getShiftsForDay(day)" :key="shift.id" class="shift-card" :class="shift.role.toLowerCase()">
                    <div class="shift-time">{{ formatTime(shift.startTime) }} - {{ formatTime(shift.endTime) }}</div>
                    <div class="shift-name">{{ shift.employeeName }}</div>
                    <div class="shift-role">{{ shift.role }}</div>
                </div>
                <el-empty v-if="getShiftsForDay(day).length === 0" description="No shifts" :image-size="40" />
            </div>
        </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import apiClient from '../../services/api';
import { MagicStick } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';
import weekOfYear from 'dayjs/plugin/weekOfYear';
dayjs.extend(weekOfYear);

const currentWeek = ref(dayjs().startOf('week').add(1, 'day')); // Start Monday
const shifts = ref<any[]>([]);
const loading = ref(false);

const weekDays = computed(() => {
    const start = dayjs(currentWeek.value).startOf('week').add(1, 'day'); // Assume Monday start
    return Array.from({ length: 7 }, (_, i) => start.add(i, 'day'));
});

const fetchShifts = async () => {
    if (!currentWeek.value) return;
    const start = dayjs(currentWeek.value).startOf('week').add(1, 'day').format('YYYY-MM-DD');
    const end = dayjs(currentWeek.value).endOf('week').add(1, 'day').format('YYYY-MM-DD');
    
    try {
        const res = await apiClient.get('/hr/workforce/shifts', { params: { start, end } });
        shifts.value = res.data;
    } catch (e) {
        ElMessage.error('Failed to load shifts');
    }
};

const generateSchedule = async () => {
    loading.value = true;
    const weekStart = dayjs(currentWeek.value).startOf('week').add(1, 'day').format('YYYY-MM-DD');
    try {
        const res = await apiClient.post('/hr/workforce/schedule/generate', null, { params: { weekStart } });
        shifts.value = res.data;
        ElMessage.success('AI Schedule Generated!');
    } catch (e) {
        ElMessage.error('Failed to generate schedule');
    } finally {
        loading.value = false;
    }
};

const getShiftsForDay = (date: dayjs.Dayjs) => {
    return shifts.value.filter(s => dayjs(s.startTime).isSame(date, 'day'));
};

const formatTime = (time: string) => {
    return dayjs(time).format('HH:mm');
};

onMounted(() => {
    fetchShifts();
});
</script>

<style scoped>
.smart-schedule {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.schedule-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 10px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;
}
.day-col {
    background: #fff;
    min-height: 500px;
    border-right: 1px solid #ebeef5;
}
.day-col:last-child {
    border-right: none;
}
.day-header {
    background: #f5f7fa;
    padding: 10px;
    text-align: center;
    font-weight: bold;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    flex-direction: column;
}
.day-header span {
    font-size: 12px;
    color: #909399;
    font-weight: normal;
}
.shifts-container {
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 10px;
}
.shift-card {
    padding: 8px;
    border-radius: 4px;
    font-size: 12px;
    border-left: 3px solid;
    background: #f9f9f9;
}
.shift-card.trainer {
    border-left-color: #409eff;
    background: #ecf5ff;
}
.shift-card.front_desk {
    border-left-color: #67c23a;
    background: #f0f9eb;
}
.shift-time {
    font-weight: bold;
    margin-bottom: 4px;
}
.shift-role {
    color: #909399;
    font-size: 10px;
    margin-top: 4px;
    text-transform: capitalize;
}
</style>
