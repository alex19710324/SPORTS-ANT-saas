<template>
  <div class="booking-view">
    <div class="header">
        <h2>Book a Resource</h2>
        <el-button type="primary" @click="showMyBookings = true">My Bookings</el-button>
    </div>

    <div class="resource-grid">
        <el-card v-for="res in resources" :key="res.id" class="resource-card">
            <template #header>
                <div class="res-header">
                    <span>{{ res.name }}</span>
                    <el-tag>{{ res.type }}</el-tag>
                </div>
            </template>
            <div class="price">¥{{ res.hourlyRate }}/hr</div>
            <div class="actions">
                <el-date-picker 
                    v-model="bookingForm.date" 
                    type="date" 
                    placeholder="Pick a day" 
                    :disabled-date="disabledDate"
                />
                <el-time-select
                    v-model="bookingForm.time"
                    start="08:00"
                    step="01:00"
                    end="22:00"
                    placeholder="Select time"
                />
                <el-button type="success" @click="bookResource(res)">Book Now</el-button>
            </div>
        </el-card>
    </div>

    <!-- My Bookings Modal -->
    <el-dialog v-model="showMyBookings" title="My Bookings & Access Codes">
        <el-table :data="myBookings" style="width: 100%">
            <el-table-column prop="resource.name" label="Resource" />
            <el-table-column label="Time">
                <template #default="scope">
                    {{ formatTime(scope.row.startTime) }} - {{ formatTime(scope.row.endTime) }}
                </template>
            </el-table-column>
            <el-table-column label="Access QR">
                <template #default="scope">
                    <div class="qr-code">
                        <!-- Mock QR Display -->
                        <img :src="`https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=${scope.row.accessCode}`" alt="QR" />
                        <div class="code-text">{{ scope.row.accessCode.substring(0,8) }}...</div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="status" label="Status">
                 <template #default="scope">
                    <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
                </template>
            </el-table-column>
        </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';

const resources = ref<any[]>([]);
const myBookings = ref<any[]>([]);
const showMyBookings = ref(false);

const bookingForm = ref({
    date: '',
    time: ''
});

const fetchResources = async () => {
    try {
        // Mock resources if backend empty
        const res = await apiClient.get('/booking/resources');
        if (res.data.length === 0) {
            resources.value = [
                { id: 1, name: 'Badminton Court 1', type: 'COURT', hourlyRate: 50 },
                { id: 2, name: 'Tennis Court A', type: 'COURT', hourlyRate: 100 },
                { id: 3, name: 'Yoga Studio', type: 'STUDIO', hourlyRate: 80 },
            ];
        } else {
            resources.value = res.data;
        }
    } catch (e) {
        console.error("Failed to load resources");
    }
};

const fetchMyBookings = async () => {
    // Mock user ID 101
    try {
        const res = await apiClient.get('/booking/my?memberId=101');
        myBookings.value = res.data;
    } catch (e) {
        // Silent fail or mock
    }
};

const bookResource = async (resource: any) => {
    if (!bookingForm.value.date || !bookingForm.value.time) {
        ElMessage.warning('Please select date and time');
        return;
    }

    const start = dayjs(bookingForm.value.date).format('YYYY-MM-DD') + 'T' + bookingForm.value.time;
    const end = dayjs(start).add(1, 'hour').format('YYYY-MM-DDTHH:mm');

    try {
        await apiClient.post('/booking/create', {
            memberId: 101, // Mock
            resourceId: resource.id,
            startTime: start,
            endTime: end
        });
        ElMessage.success('Booking Confirmed!');
        fetchMyBookings();
        showMyBookings.value = true;
    } catch (e) {
        ElMessage.error('Booking Failed: Slot likely taken');
    }
};

const disabledDate = (time: Date) => {
    return time.getTime() < Date.now() - 8.64e7;
};

const formatTime = (time: string) => {
    return dayjs(time).format('MM-DD HH:mm');
};

const getStatusType = (status: string) => {
    if (status === 'CONFIRMED') return 'success';
    if (status === 'COMPLETED') return 'info';
    return 'danger';
};

onMounted(() => {
    fetchResources();
    fetchMyBookings();
});
</script>

<style scoped>
.booking-view {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.resource-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
}
.res-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
}
.price {
    font-size: 24px;
    color: #f56c6c;
    margin: 10px 0;
    font-weight: bold;
}
.actions {
    display: flex;
    flex-direction: column;
    gap: 10px;
}
.qr-code {
    text-align: center;
}
.code-text {
    font-size: 10px;
    color: #999;
}
</style>
