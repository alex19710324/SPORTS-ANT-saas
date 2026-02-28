<template>
  <div class="member-portal" v-loading="loading">
    <h2>My Member Portal</h2>
    
    <div class="profile-header" v-if="profile">
        <el-avatar :size="80" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <div class="profile-info">
            <h3>{{ profile.name }}</h3>
            <p>Level: <el-tag>{{ profile.currentLevel.name }}</el-tag></p>
            <p>Points: {{ profile.points }} | Growth: {{ profile.growthValue }}</p>
        </div>
        <el-button type="primary" @click="handleCheckIn">Daily Check-in</el-button>
    </div>

    <el-tabs v-model="activeTab" class="portal-tabs">
        <el-tab-pane label="My Activities" name="activities">
            <el-empty v-if="!activities || activities.length === 0" description="No upcoming activities" />
            <!-- Book Activity -->
            <h3>Book Team Building</h3>
            <div class="activity-grid" v-if="availableActivities">
                <el-card v-for="act in availableActivities" :key="act.id" :body-style="{ padding: '0px' }">
                    <img :src="act.image" class="image" />
                    <div style="padding: 14px">
                        <span>{{ act.title }}</span>
                        <div class="bottom">
                            <span class="price">¥{{ act.price }}</span>
                            <el-button text class="button" @click="handleBook(act.id)">Book Now</el-button>
                        </div>
                    </div>
                </el-card>
            </div>
        </el-tab-pane>
        
        <el-tab-pane label="Transaction History" name="transactions">
            <!-- Mock Transaction History -->
            <el-timeline>
                <el-timeline-item timestamp="2025-03-01" placement="top">
                    <el-card>
                        <h4>Purchase</h4>
                        <p>Spent ¥100.00</p>
                    </el-card>
                </el-timeline-item>
                <el-timeline-item timestamp="2025-02-28" placement="top">
                    <el-card>
                        <h4>Check-in Reward</h4>
                        <p>+10 Points</p>
                    </el-card>
                </el-timeline-item>
            </el-timeline>
        </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import apiClient from '../../services/api';

const loading = ref(false);
const profile = ref<any>(null);
const activeTab = ref('activities');
const activities = ref([]);
const availableActivities = ref<any[]>([]);

const fetchProfile = async () => {
    loading.value = true;
    try {
        const res = await apiClient.get('/membership/me');
        profile.value = res.data;
    } catch (error) {
        ElMessage.error('Failed to load profile');
    } finally {
        loading.value = false;
    }
};

const fetchActivities = async () => {
    try {
        const res = await apiClient.get('/teambuilding/activities');
        availableActivities.value = res.data;
    } catch (error) {
        console.error('Failed to load activities');
    }
};

const handleCheckIn = async () => {
    try {
        await apiClient.post('/membership/checkin');
        ElMessage.success('Checked in successfully!');
        fetchProfile();
    } catch (error) {
        ElMessage.error('Check-in failed');
    }
};

const handleBook = (activityId: number) => {
    ElMessageBox.prompt('Select a date (YYYY-MM-DD)', 'Book Activity', {
        confirmButtonText: 'Book',
        cancelButtonText: 'Cancel',
        inputPattern: /\d{4}-\d{2}-\d{2}/,
        inputErrorMessage: 'Invalid Date Format'
    }).then(async ({ value }) => {
        try {
            await apiClient.post('/teambuilding/book', { activityId, date: value });
            ElMessage.success('Booking Confirmed!');
        } catch (error) {
            ElMessage.error('Booking Failed');
        }
    });
};

onMounted(() => {
    fetchProfile();
    fetchActivities();
});
</script>

<style scoped>
.member-portal {
    padding: 20px;
}
.profile-header {
    display: flex;
    align-items: center;
    gap: 20px;
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.activity-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
    margin-top: 20px;
}
.image {
    width: 100%;
    height: 150px;
    object-fit: cover;
}
.bottom {
    margin-top: 13px;
    line-height: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.price {
    font-size: 18px;
    font-weight: bold;
    color: #f56c6c;
}
</style>
