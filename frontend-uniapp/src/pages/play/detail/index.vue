<template>
  <view class="play-detail">
    <!-- Cover -->
    <view class="cover-section">
      <view class="gradient-overlay"></view>
      <view class="cover-content">
        <view class="i-carbon-virtual-private-cloud cover-icon"></view>
      </view>
      <view class="back-btn" @click="goBack">
        <view class="i-carbon-arrow-left text-white text-lg"></view>
      </view>
      
      <!-- Live Status -->
      <view class="live-status">
        <view class="status-dot"></view>
        <text class="status-text">当前排队: 3人</text>
        <text class="divider">|</text>
        <text class="status-text">等待约10分钟</text>
      </view>
    </view>

    <!-- Header Info -->
    <view class="info-section">
      <view class="title-row">
        <text class="project-title">{{ detail.name }}</text>
        <view class="price-box">
          <text class="price">¥{{ detail.price }}</text>
          <text class="unit">/小时/人</text>
        </view>
      </view>
      <view class="tags-row">
        <text class="tag" v-for="tag in detail.tags" :key="tag">{{ tag }}</text>
      </view>
    </view>

    <!-- Tabs -->
    <view class="tabs-header">
      <view 
        class="tab-item"
        :class="{ active: activeTab === 0 }"
        @click="activeTab = 0"
      >
        项目介绍
        <view v-if="activeTab === 0" class="active-line"></view>
      </view>
      <view 
        class="tab-item"
        :class="{ active: activeTab === 1 }"
        @click="activeTab = 1"
      >
        安全须知
        <view v-if="activeTab === 1" class="active-line"></view>
      </view>
    </view>

    <!-- Content -->
    <view class="content-body">
      <view v-if="activeTab === 0" class="animate-fade-in">
        <view class="content-block">
          <text class="block-title">【玩法说明】</text>
          <text class="block-text">{{ detail.description }}</text>
        </view>
        <view class="content-block">
          <text class="block-title">【设备配置】</text>
          <text class="block-text">最新设备配置，提供顶级体验。</text>
        </view>
      </view>
      <view v-else class="animate-fade-in">
        <view class="warning-item">
          <view class="i-carbon-warning warning-icon"></view>
          <text class="warning-text">患有高血压、心脏病、眩晕症及孕妇请勿体验。</text>
        </view>
        <view class="warning-item">
          <view class="i-carbon-warning warning-icon"></view>
          <text class="warning-text">体验过程中请勿奔跑，以免发生碰撞。</text>
        </view>
        <view class="warning-item">
          <view class="i-carbon-warning warning-icon"></view>
          <text class="warning-text">12岁以下儿童需在家长陪同下体验。</text>
        </view>
      </view>
    </view>

    <!-- Action Bar -->
    <view class="action-bar">
      <button class="action-btn" @click="handleBooking">
        <view class="i-carbon-ticket"></view>
        立即预约取号
      </button>
    </view>

    <!-- Booking Modal -->
    <view v-if="showBooking" class="modal-mask" @click="closeBooking">
      <view class="modal-content" @click.stop>
        <text class="modal-title">预约信息</text>

        <!-- Booking Mode Toggle -->
        <view class="flex bg-gray-800 rounded-lg p-1 mb-4">
          <view class="flex-1 text-center py-2 text-sm rounded-md transition-all" :class="!isTeamBooking ? 'bg-blue-600 text-white font-bold' : 'text-gray-400'" @click="isTeamBooking = false">单人/普通预约</view>
          <view class="flex-1 text-center py-2 text-sm rounded-md transition-all" :class="isTeamBooking ? 'bg-purple-600 text-white font-bold' : 'text-gray-400'" @click="isTeamBooking = true">组队预约</view>
        </view>
        
        <!-- Date Selection -->
        <view class="form-section">
          <text class="section-label">选择日期</text>
          <view class="date-tabs">
            <view 
              v-for="(date, index) in dates" 
              :key="index"
              class="date-tab"
              :class="{ active: selectedDateIndex === index }"
              @click="selectedDateIndex = index"
            >
              <text class="date-day">{{ date.day }}</text>
              <text class="date-val">{{ date.date }}</text>
            </view>
          </view>
        </view>

        <!-- Time Selection -->
        <view class="form-section">
          <text class="section-label">选择场次</text>
          <view class="time-grid">
            <view 
              v-for="t in timeSlots" 
              :key="t" 
              class="time-slot" 
              :class="{ active: selectedTime === t }"
              @click="selectedTime = t"
            >
              {{ t }}
            </view>
          </view>
        </view>

        <!-- People Count -->
        <view class="form-section" v-if="!isTeamBooking || (isTeamBooking && !teamCode)">
          <view class="people-row">
            <text class="section-label" style="margin-bottom:0">{{ isTeamBooking ? '发起组队 (队伍人数)' : '游玩人数' }}</text>
            <view class="stepper">
              <view class="step-btn" @click="updatePeople(-1)">-</view>
              <text class="step-val">{{ peopleCount }}</text>
              <view class="step-btn" @click="updatePeople(1)">+</view>
            </view>
          </view>
        </view>

        <!-- Team Booking Extra -->
        <view class="form-section bg-purple-900/20 border border-purple-500/30 p-3 rounded-lg" v-if="isTeamBooking">
          <text class="section-label text-purple-400 mb-2">加入已有队伍</text>
          <input type="text" v-model="teamCode" placeholder="输入队伍邀请码 (选填)" class="w-full bg-black/50 border border-gray-700 rounded-md px-3 py-2 text-sm text-white focus:border-purple-500 placeholder-gray-600" />
          <text class="text-[10px] text-gray-500 mt-2 block">提示: 发起组队后，系统将生成专属邀请链接发送给好友。</text>
        </view>

        <!-- Confirm Button -->
        <button class="confirm-btn" @click="confirmBooking" :disabled="!selectedTime || submitting">
          {{ submitting ? '提交中...' : `确认预约 (¥${totalPrice})` }}
        </button>
      </view>
    </view>

  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { getPlayDetail, createBooking } from '@/api/play'
import { onLoad } from '@dcloudio/uni-app'

const activeTab = ref(0)
const showBooking = ref(false)
const submitting = ref(false)
const detail = ref<any>({
  id: '',
  name: '未来战场 VR',
  price: 88,
  tags: ['沉浸式', '多人联机', '适合团建'],
  description: '佩戴最新的VR头显，手持体感控制器，进入完全拟真的未来战场。支持4-8人组队对抗或合作闯关。包含“丧尸围城”、“星际穿越”、“反恐精英”等多个剧本。'
})

onLoad(async (options) => {
  if (options && options.id) {
    try {
      const res: any = await getPlayDetail(options.id)
      const data = res?.data || res
      if (data) {
        detail.value = {
          id: data.id,
          name: data.name || detail.value.name,
          price: data.price || detail.value.price,
          tags: data.tags || detail.value.tags,
          description: data.description || detail.value.description
        }
      }
    } catch (e) {
      console.error(e)
    }
  }
})

// Booking State
const selectedDateIndex = ref(0)
const selectedTime = ref('')
const peopleCount = ref(1)
const isTeamBooking = ref(false)
const teamCode = ref('')

// Date Logic (Mock)
const dates = [
  { day: '今天', date: '03-17' },
  { day: '明天', date: '03-18' },
  { day: '后天', date: '03-19' }
]

const timeSlots = [
  '10:00', '11:00', '14:00', '15:00', '16:00', '17:00', '19:00', '20:00'
]

const totalPrice = computed(() => peopleCount.value * detail.value.price)

const goBack = () => {
  uni.navigateBack()
}

const handleBooking = () => {
  showBooking.value = true
}

const closeBooking = () => {
  showBooking.value = false
}

const updatePeople = (delta: number) => {
  const newVal = peopleCount.value + delta
  if (newVal >= 1 && newVal <= 10) {
    peopleCount.value = newVal
  }
}

const confirmBooking = async () => {
  if (!selectedTime.value) return
  
  submitting.value = true
  try {
    const dateStr = dates[selectedDateIndex.value].date
    const fullTime = `${dateStr} ${selectedTime.value}`
    
    await createBooking({ 
      resourceId: detail.value.id || '1',
      // Note: time and people are not directly accepted by createBooking backend API currently, but passed in payload.
      // We will adjust backend later if needed, for now send as extra payload
      ...({ time: fullTime, people: peopleCount.value })
    })
    
    uni.showToast({ title: '预约成功', icon: 'success' })
    setTimeout(() => {
      showBooking.value = false
      // Reset state
      selectedTime.value = ''
      peopleCount.value = 1
    }, 1500)
  } catch (e) {
    uni.showToast({ title: '预约失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
/* Base Styles */
.play-detail {
  min-height: 100vh;
  padding-bottom: 6rem;
  background-color: #0B0C10;
  color: white;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}
.cover-section {
  height: 16rem;
  width: 100%;
  background-color: #1f2937;
  position: relative;
}
.gradient-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, rgba(88, 28, 135, 0.8), rgba(30, 58, 138, 0.8));
}
.cover-content {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cover-icon {
  font-size: 6rem;
  color: rgba(255, 255, 255, 0.3);
}
.back-btn {
  position: absolute;
  top: 1rem;
  left: 1rem;
  z-index: 50;
  width: 2rem;
  height: 2rem;
  border-radius: 9999px;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: center;
}
.live-status {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(12px);
  padding: 0.375rem 0.75rem;
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.status-dot {
  width: 0.5rem;
  height: 0.5rem;
  border-radius: 9999px;
  background-color: #22c55e;
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: .5; }
}
.status-text {
  font-size: 0.75rem;
  color: white;
}
.divider {
  font-size: 0.75rem;
  color: #9ca3af;
}

.info-section {
  padding: 1rem;
}
.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}
.project-title {
  font-size: 1.5rem;
  font-weight: 900;
  color: white;
}
.price-box {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}
.price {
  font-size: 1.25rem;
  font-weight: 700;
  color: #00F0FF;
}
.unit {
  font-size: 0.75rem;
  color: #9ca3af;
}
.tags-row {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}
.tag {
  font-size: 0.75rem;
  padding: 0.125rem 0.5rem;
  background-color: rgba(255, 255, 255, 0.1);
  color: #d1d5db;
  border-radius: 0.25rem;
}

.tabs-header {
  display: flex;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0 1rem;
  margin-bottom: 1rem;
}
.tab-item {
  padding-bottom: 0.5rem;
  margin-right: 1.5rem;
  font-size: 0.875rem;
  font-weight: 700;
  position: relative;
  transition: color 0.2s;
  color: #6b7280;
}
.tab-item.active {
  color: white;
}
.active-line {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #B026FF;
}

.content-body {
  padding: 0 1rem;
  font-size: 0.875rem;
  color: #d1d5db;
  line-height: 1.6;
  min-height: 200px;
}
.content-block {
  margin-bottom: 1rem;
}
.block-title {
  font-weight: 700;
  color: white;
  display: block;
  margin-bottom: 0.25rem;
}
.warning-item {
  margin-bottom: 0.5rem;
  display: flex;
  gap: 0.5rem;
}
.warning-icon {
  color: #eab308;
  flex-shrink: 0;
  margin-top: 0.125rem;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: rgba(11, 12, 16, 0.9);
  backdrop-filter: blur(12px);
  padding: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  box-sizing: border-box;
  z-index: 90;
}
.action-btn {
  width: 100%;
  background: linear-gradient(to right, #B026FF, #00F0FF);
  color: white;
  font-weight: 700;
  padding: 0.75rem 0;
  border-radius: 0.75rem;
  box-shadow: 0 10px 15px -3px rgba(176, 38, 255, 0.3);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

/* Modal Styles */
.modal-mask {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.7);
  z-index: 100;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.2s ease-out;
}
.modal-content {
  width: 100%;
  background-color: #1f2937;
  border-top-left-radius: 1.5rem;
  border-top-right-radius: 1.5rem;
  padding: 1.5rem;
  padding-bottom: 2.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.modal-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: white;
  display: block;
  margin-bottom: 1.5rem;
  text-align: center;
}

.form-section {
  margin-bottom: 1.5rem;
}
.section-label {
  font-size: 0.875rem;
  color: #9ca3af;
  margin-bottom: 0.75rem;
  display: block;
}

.date-tabs {
  display: flex;
  gap: 0.75rem;
  overflow-x: auto;
}
.date-tab {
  flex: 1;
  background-color: rgba(255, 255, 255, 0.05);
  padding: 0.75rem;
  border-radius: 0.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.2s;
}
.date-tab.active {
  background-color: rgba(0, 240, 255, 0.1);
  border-color: #00F0FF;
}
.date-day {
  font-size: 0.875rem;
  font-weight: 700;
  color: white;
}
.date-val {
  font-size: 0.75rem;
  color: #9ca3af;
  margin-top: 0.125rem;
}
.date-tab.active .date-day,
.date-tab.active .date-val {
  color: #00F0FF;
}

.time-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.75rem;
}
.time-slot {
  background-color: rgba(255, 255, 255, 0.05);
  padding: 0.5rem 0;
  border-radius: 0.375rem;
  text-align: center;
  color: #d1d5db;
  font-size: 0.875rem;
  border: 1px solid transparent;
}
.time-slot.active {
  background-color: #B026FF;
  color: white;
  box-shadow: 0 0 10px rgba(176, 38, 255, 0.4);
}

.people-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stepper {
  display: flex;
  align-items: center;
  gap: 1rem;
  background-color: rgba(255, 255, 255, 0.05);
  padding: 0.25rem 0.5rem;
  border-radius: 9999px;
}
.step-btn {
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 9999px;
  background-color: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.25rem;
  font-weight: 300;
}
.step-val {
  font-size: 1rem;
  font-weight: 700;
  color: white;
  min-width: 1.5rem;
  text-align: center;
}

.confirm-btn {
  width: 100%;
  background: linear-gradient(to right, #B026FF, #00F0FF);
  color: white;
  font-weight: 700;
  padding: 0.875rem 0;
  border-radius: 0.75rem;
  margin-top: 1rem;
  border: none;
}
.confirm-btn[disabled] {
  opacity: 0.5;
  background: #374151;
  color: #9ca3af;
}

/* Utilities */
.flex { display: flex; }
.flex-1 { flex: 1; }
.text-center { text-align: center; }
.py-2 { padding-top: 0.5rem; padding-bottom: 0.5rem; }
.text-sm { font-size: 0.875rem; }
.rounded-md { border-radius: 0.375rem; }
.rounded-lg { border-radius: 0.5rem; }
.bg-gray-800 { background-color: #1f2937; }
.bg-blue-600 { background-color: #2563eb; }
.bg-purple-600 { background-color: #9333ea; }
.text-white { color: white; }
.font-bold { font-weight: 700; }
.text-gray-400 { color: #9ca3af; }
.text-purple-400 { color: #c084fc; }
.bg-purple-900\/20 { background-color: rgba(88, 28, 135, 0.2); }
.border-purple-500\/30 { border-color: rgba(168, 85, 247, 0.3); }
.mb-2 { margin-bottom: 0.5rem; }
.mb-4 { margin-bottom: 1rem; }
.p-1 { padding: 0.25rem; }
.p-3 { padding: 0.75rem; }
.px-3 { padding-left: 0.75rem; padding-right: 0.75rem; }
.w-full { width: 100%; }
.bg-black\/50 { background-color: rgba(0, 0, 0, 0.5); }
.border-gray-700 { border-color: #374151; }
.text-\[10px\] { font-size: 10px; }
.text-gray-500 { color: #6b7280; }
.mt-2 { margin-top: 0.5rem; }
.block { display: block; }
.transition-all { transition: all 0.3s; }

.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}
</style>
