<template>
  <view class="container">
    <view class="header">
      <text class="title">活动日历</text>
      <text class="subtitle">提前预约，锁定席位</text>
    </view>

    <!-- Calendar Mock -->
    <scroll-view scroll-x class="calendar-scroll" :show-scrollbar="false">
      <view class="calendar-strip">
        <view 
          v-for="day in 7" 
          :key="day" 
          class="day-card"
          :class="{ active: activeDay === day }"
          @click="activeDay = day"
        >
          <text class="day-name">周{{ ['一','二','三','四','五','六','日'][day-1] }}</text>
          <text class="day-num">{{ 14 + day }}</text>
          <view v-if="[1, 4, 6].includes(day)" class="event-dot"></view>
        </view>
      </view>
    </scroll-view>

    <!-- Filters -->
    <view class="filters">
      <view class="filter-item">全部门店 <view class="i-carbon-chevron-down text-gray-500"></view></view>
      <view class="filter-item">全部类型 <view class="i-carbon-chevron-down text-gray-500"></view></view>
    </view>

    <!-- Event List -->
    <view class="event-list">
      <view class="event-card" v-for="event in events" :key="event.id">
        <view class="event-image-wrap">
          <image :src="event.image" mode="aspectFill" class="event-image"></image>
          <view class="event-tag" :class="event.tagClass">{{ event.type }}</view>
          <view v-if="event.isHot" class="hot-badge">
            <view class="hot-dot"></view> HOT
          </view>
        </view>
        <view class="event-content">
          <view class="event-meta">
            <text class="event-time"><text class="i-carbon-time"></text> {{ event.time }}</text>
            <text class="event-store"><text class="i-carbon-location"></text> {{ event.store }}</text>
          </view>
          <text class="event-title">{{ event.title }}</text>
          <text class="event-desc">{{ event.desc }}</text>
          
          <view class="event-footer">
            <view class="price-wrap">
              <text class="price">{{ event.price === 0 ? '免费' : `¥${event.price}` }}</text>
              <text class="quota">已报 {{ event.enrolled }}/{{ event.capacity }}</text>
            </view>
            <button class="join-btn" :class="{ disabled: event.enrolled >= event.capacity }" @click="handleJoin(event)">
              {{ event.enrolled >= event.capacity ? '名额已满' : (event.checkedIn ? '已入场' : (event.joined ? '查看入场券' : '立即报名')) }}
            </button>
          </view>
          <view class="cross-links">
            <text class="cross-link" @click="openPlayerEvent(event)">去 5176 活动日历</text>
            <text class="cross-link" @click="openBusinessEvent(event)">去 5178 报名运营</text>
            <text v-if="event.workflowTaskId || event.ticketCode" class="cross-link" @click="openAdminWorkflow(event)">去 5174 协同流</text>
          </view>
        </view>
      </view>
    </view>

    <!-- Registration / Ticket Modal -->
    <view class="modal-mask" v-if="showModal" @click="showModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ selectedEvent?.joined ? '电子入场券' : '活动报名' }}</text>
          <text class="i-carbon-close text-xl text-gray-400" @click="showModal = false"></text>
        </view>
        
        <view class="modal-body" v-if="!selectedEvent?.joined">
          <text class="modal-event-title">{{ selectedEvent?.title }}</text>
          <view class="input-group">
            <text class="label">参与人数</text>
            <view class="stepper">
              <button class="step-btn" @click="participants > 1 && participants--">-</button>
              <text class="step-val">{{ participants }}</text>
              <button class="step-btn" @click="participants++">+</button>
            </view>
          </view>
          <view class="input-group">
            <text class="label">联系电话</text>
            <input class="input-box" v-model="phone" type="number" placeholder="请输入手机号" />
          </view>
          <button class="confirm-btn mt-6" @click="confirmJoin">确认报名</button>
        </view>

        <view class="modal-body ticket-body" v-else>
          <text class="modal-event-title text-center">{{ selectedEvent?.title }}</text>
          <text class="text-gray-400 text-sm text-center mb-4">{{ selectedEvent?.time }}</text>
          <view class="qr-box">
            <view class="qr-placeholder animate-pulse">
              <text class="i-carbon-qr-code text-6xl text-acid-green"></text>
            </view>
            <text class="qr-code-text">核销码：{{ selectedEvent?.ticketCode }}</text>
          </view>
          <text class="text-xs text-center mt-4" :class="selectedEvent?.checkedIn ? 'text-emerald-400' : 'text-gray-500'">
            {{ selectedEvent?.checkedIn ? '该门票已完成核销，祝您活动愉快' : '请在活动现场向工作人员出示此码' }}
          </text>
          <view v-if="selectedEvent?.ticketCode" class="ops-panel">
            <text class="ops-title">跨端协同</text>
            <text class="ops-line">入场券：{{ selectedEvent?.ticketCode }}</text>
            <text v-if="selectedEvent?.workflowTaskId" class="ops-line">流程单：{{ selectedEvent?.workflowTaskId }}</text>
            <view class="ops-actions">
              <button class="ops-btn" @click="selectedEvent && openPlayerEvent(selectedEvent)">5176 玩家活动面</button>
              <button class="ops-btn" @click="selectedEvent && openBusinessEvent(selectedEvent)">5178 报名运营</button>
              <button v-if="selectedEvent?.workflowTaskId" class="ops-btn" @click="selectedEvent && openAdminWorkflow(selectedEvent)">5174 流程协同</button>
            </view>
          </view>
        </view>
      </view>
    </view>

  </view>
</template>

<script setup lang="ts">
import request from '@/utils/request'
import { ref } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { io } from 'socket.io-client'
import { useActorContext } from '@/composables/useActorContext'
import { openWorkspace } from '@/utils/crossApp'
import { getHotActivities } from '@/api/home'

type EventItem = {
  id: number
  time: string
  title: string
  desc: string
  type: string
  tagClass: string
  store: string
  price: number
  capacity: number
  enrolled: number
  isHot: boolean
  joined: boolean
  checkedIn?: boolean
  ticketCode?: string
  participants?: number
  workflowTaskId?: string
  image: string
}

const activeDay = ref(1)

const events = ref<EventItem[]>([])

const showModal = ref(false)
const selectedEvent = ref<EventItem | null>(null)
const participants = ref(1)
const { userId } = useActorContext()
const phone = ref('')

const fallbackImages = [
  'http://localhost:8080/images/asset_80f7c8b077ea38bf5b715e9c9c84614a.jpg',
  'http://localhost:8080/images/asset_38a3facbeabbf8a5e5eb0d09bcd584e0.jpg',
  'http://localhost:8080/images/asset_3253dd52e47cf71ad733f7b478422c51.jpg'
]

const inferEventType = (title: string) => {
  if (title.includes('Tournament') || title.includes('对抗') || title.includes('赛')) return '电竞赛事'
  if (title.includes('Night') || title.includes('派对')) return '主题派对'
  return '主题活动'
}

const inferTagClass = (index: number) => ['bg-purple', 'bg-blue', 'bg-green'][index % 3]

const mapApiEvent = (event: any, index: number): EventItem => {
  const existing = events.value.find((item) => String(item.id) === String(event.id) || item.title === event.title)
  const enrolled = Number(event.enrolled ?? event.attendees ?? 0)
  const capacity = Number(event.capacity ?? Math.max(enrolled + 20, 50))
  return {
    id: Number(String(event.id).replace(/\D/g, '')) || index + 1,
    time: event.time || `${event.date || '即将开始'} · ${event.status || 'OPEN'}`,
    title: event.title,
    desc: event.desc || event.description || `${event.title} 正在开放报名，支持 5171 报名与跨端协同核销。`,
    type: event.type || inferEventType(event.title || ''),
    tagClass: event.tagClass || inferTagClass(index),
    store: event.store || event.storeName || event.location || '活动中心',
    price: Number(event.price || 0),
    capacity,
    enrolled,
    isHot: Boolean(event.isHot ?? enrolled >= Math.max(capacity * 0.6, 30)),
    joined: existing?.joined || false,
    checkedIn: existing?.checkedIn || false,
    ticketCode: existing?.ticketCode || event.ticketCode,
    participants: existing?.participants,
    workflowTaskId: existing?.workflowTaskId || event.workflowTaskId,
    image: event.image || fallbackImages[index % fallbackImages.length]
  }
}

const loadEvents = async () => {
  try {
    const res: any = await getHotActivities()
    const list = Array.isArray(res) ? res : res?.data || []
    events.value = Array.isArray(list) ? list.map(mapApiEvent) : []
  } catch (e) {
    events.value = []
  }
}

const openPlayerEvent = (event: EventItem) => {
  openWorkspace('player', `/events?eventId=${encodeURIComponent(event.id)}&eventName=${encodeURIComponent(event.title)}&storeName=${encodeURIComponent(event.store)}&ticketCode=${encodeURIComponent(event.ticketCode || '')}`)
}

const openBusinessEvent = (event: EventItem) => {
  openWorkspace('business', `/event-registration?eventId=${encodeURIComponent(event.id)}&eventName=${encodeURIComponent(event.title)}&ticketCode=${encodeURIComponent(event.ticketCode || '')}`)
}

const openAdminWorkflow = (event: EventItem) => {
  openWorkspace('admin', `/workflow/list?relatedId=${encodeURIComponent(event.ticketCode || event.id)}&type=EVENT_BOOKING`)
}

const handleJoin = (event: EventItem) => {
  if (event.enrolled >= event.capacity && !event.joined) return
  selectedEvent.value = event
  showModal.value = true
}

const confirmJoin = async () => {
  if (!phone.value) {
    uni.showToast({ title: '请输入联系电话', icon: 'none' })
    return
  }
  
  uni.showLoading({ title: '处理中...' })
  try {
    const currentEvent = selectedEvent.value
    if (!currentEvent) {
      uni.hideLoading()
      return
    }
    const res: any = await request({
      url: '/api/mobile/play/book',
      method: 'POST',
      data: {
        eventId: currentEvent.id,
        eventName: currentEvent.title,
        phone: phone.value,
        participants: participants.value,
        user: userId.value,
        storeName: currentEvent.store
      }
    })
    currentEvent.joined = true
    currentEvent.enrolled += participants.value
    currentEvent.participants = participants.value
    currentEvent.ticketCode = res?.data?.eventTicket?.ticketCode || 'EV' + Date.now().toString().slice(-6)
    currentEvent.workflowTaskId = res?.data?.workflowTaskId || res?.data?.eventTicket?.workflowTaskId || ''
    uni.showToast({ title: `报名成功 ${currentEvent.ticketCode}`, icon: 'success' })
  } catch(e) {
    uni.showToast({ title: '报名失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

let socket: any = null

const appendPublishedEvent = (payload: any) => {
  if (events.value.some(event => event.title === payload.title)) {
    return
  }
  events.value.unshift({
    id: Date.now(),
    time: '即将开始',
    title: payload.title,
    desc: '新发布的神秘活动，快来抢先报名！',
    type: payload.type,
    tagClass: 'bg-purple',
    store: payload.storeName || '全城首发',
    price: 0,
    capacity: payload.capacity || 50,
    enrolled: 0,
    isHot: true,
    joined: false,
    image: 'http://localhost:8080/images/asset_cf1ecdf74cbfa7e729fcd21cb4ff3349.jpg'
  })
  uni.showToast({
    title: '收到新活动推送！',
    icon: 'none',
    duration: 3000
  })
}

onLoad(() => {
  loadEvents()
  socket = io('http://localhost:8080')
  const handleRealtimeEvent = (data: any) => {
    if (data.type === 'NEW_EVENT_PUBLISHED') {
      appendPublishedEvent(data.payload)
    }
    if (data.type === 'EVENT_BOOKED') {
      const target = events.value.find(event => event.title === data.payload.eventName || event.id === data.payload.eventId)
      if (target && !target.joined && data.payload.user === userId.value) {
        target.joined = true
        target.participants = data.payload.participants
        target.ticketCode = data.payload.ticketCode
        target.workflowTaskId = data.payload.workflowTaskId
      }
    }
    if (data.type === 'EVENT_TICKET_VERIFIED' || data.type === 'EVENT_AUTO_CHECKIN') {
      const target = events.value.find(event => event.ticketCode === data.payload.ticketCode || event.title === data.payload.eventName)
      if (target) {
        target.checkedIn = true
        uni.showToast({ title: `${target.title} 已核销`, icon: 'success' })
      }
    }
  }
  socket.on('data_sync', handleRealtimeEvent)
  socket.on('state_update', handleRealtimeEvent)
})

onUnload(() => {
  if (socket) {
    socket.disconnect()
  }
})
</script>

<style>
.container {
  min-height: 100vh;
  background-color: #0B0C10;
  padding-bottom: 40rpx;
}
.header {
  padding: 100rpx 40rpx 40rpx;
  background-color: #1f2937;
}
.title {
  font-size: 48rpx;
  font-weight: bold;
  color: white;
  display: block;
}
.subtitle {
  font-size: 28rpx;
  color: #a0aec0;
}

.calendar-scroll {
  background-color: #1f2937;
  padding-bottom: 30rpx;
}
.calendar-strip {
  display: inline-flex;
  padding: 0 40rpx;
  gap: 20rpx;
}
.day-card {
  width: 100rpx;
  height: 130rpx;
  background-color: rgba(255,255,255,0.05);
  border-radius: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  border: 2rpx solid transparent;
}
.day-card.active {
  background-color: rgba(34, 197, 94, 0.2);
  border-color: #22c55e;
}
.day-name {
  font-size: 22rpx;
  color: #9ca3af;
  margin-bottom: 6rpx;
}
.day-num {
  font-size: 32rpx;
  font-weight: bold;
  color: white;
}
.active .day-name, .active .day-num {
  color: #4ade80;
}
.event-dot {
  position: absolute;
  bottom: 12rpx;
  width: 8rpx;
  height: 8rpx;
  background-color: #22c55e;
  border-radius: 50%;
}

.filters {
  display: flex;
  gap: 20rpx;
  padding: 30rpx 40rpx;
}
.filter-item {
  background-color: #1f2937;
  color: white;
  font-size: 26rpx;
  padding: 16rpx 30rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.event-list {
  padding: 0 40rpx;
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}
.event-card {
  background-color: #1f2937;
  border-radius: 24rpx;
  overflow: hidden;
  border: 2rpx solid #374151;
}
.event-image-wrap {
  width: 100%;
  height: 240rpx;
  position: relative;
}
.event-image {
  width: 100%;
  height: 100%;
}
.event-tag {
  position: absolute;
  top: 20rpx;
  left: 20rpx;
  padding: 6rpx 16rpx;
  border-radius: 10rpx;
  font-size: 20rpx;
  font-weight: bold;
  color: white;
}
.bg-purple { background-color: rgba(168, 85, 247, 0.9); }
.bg-blue { background-color: rgba(59, 130, 246, 0.9); }
.bg-green { background-color: rgba(34, 197, 94, 0.9); }

.hot-badge {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  background-color: rgba(239, 68, 68, 0.9);
  color: white;
  padding: 6rpx 16rpx;
  border-radius: 10rpx;
  font-size: 20rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8rpx;
}
.hot-dot {
  width: 10rpx;
  height: 10rpx;
  background-color: white;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.event-content {
  padding: 30rpx;
}
.event-meta {
  display: flex;
  gap: 20rpx;
  margin-bottom: 16rpx;
}
.event-time, .event-store {
  font-size: 24rpx;
  color: #9ca3af;
  display: flex;
  align-items: center;
  gap: 6rpx;
}
.event-title {
  font-size: 32rpx;
  font-weight: bold;
  color: white;
  display: block;
  margin-bottom: 12rpx;
}
.event-desc {
  font-size: 26rpx;
  color: #d1d5db;
  display: block;
  margin-bottom: 30rpx;
  line-height: 1.5;
}
.event-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 2rpx solid #374151;
  padding-top: 20rpx;
}
.cross-links {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 20rpx;
}
.cross-link {
  font-size: 22rpx;
  color: #67e8f9;
}
.price-wrap {
  display: flex;
  flex-direction: column;
}
.price {
  color: #22c55e;
  font-size: 36rpx;
  font-weight: bold;
}
.quota {
  color: #6b7280;
  font-size: 22rpx;
}
.join-btn {
  margin: 0;
  background: linear-gradient(to right, #22c55e, #10b981);
  color: white;
  font-size: 28rpx;
  font-weight: bold;
  padding: 0 40rpx;
  height: 70rpx;
  line-height: 70rpx;
  border-radius: 35rpx;
}
.join-btn.disabled {
  background: #374151;
  color: #9ca3af;
}

/* Modal Styles */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  width: 600rpx;
  background: #1f2937;
  border-radius: 30rpx;
  border: 2rpx solid #374151;
  overflow: hidden;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 40rpx;
  border-bottom: 2rpx solid #374151;
}
.modal-title {
  color: white;
  font-size: 32rpx;
  font-weight: bold;
}
.modal-body {
  padding: 40rpx;
}
.modal-event-title {
  color: #4ade80;
  font-size: 28rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 40rpx;
}
.input-group {
  margin-bottom: 30rpx;
}
.label {
  color: #9ca3af;
  font-size: 24rpx;
  display: block;
  margin-bottom: 12rpx;
}
.input-box {
  width: 100%;
  height: 80rpx;
  background: rgba(255,255,255,0.05);
  border: 2rpx solid #374151;
  border-radius: 16rpx;
  padding: 0 20rpx;
  color: white;
  font-size: 28rpx;
  box-sizing: border-box;
}
.stepper {
  display: flex;
  align-items: center;
  gap: 20rpx;
}
.step-btn {
  width: 60rpx;
  height: 60rpx;
  background: rgba(255,255,255,0.1);
  color: white;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  padding: 0;
  line-height: 60rpx;
}
.step-val {
  color: white;
  font-size: 32rpx;
  font-weight: bold;
  width: 60rpx;
  text-align: center;
}
.confirm-btn {
  background: linear-gradient(to right, #22c55e, #10b981);
  color: white;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 40rpx;
  height: 80rpx;
  line-height: 80rpx;
}
.ticket-body {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.ops-panel {
  width: 100%;
  margin-top: 28rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  background: rgba(17,255,255,0.06);
  border: 2rpx solid rgba(103, 232, 249, 0.18);
  box-sizing: border-box;
}
.ops-title {
  display: block;
  color: #fff;
  font-size: 26rpx;
  font-weight: bold;
  margin-bottom: 12rpx;
}
.ops-line {
  display: block;
  font-size: 22rpx;
  color: #9ca3af;
  margin-top: 8rpx;
}
.ops-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 20rpx;
}
.ops-btn {
  margin: 0;
  padding: 0 24rpx;
  line-height: 72rpx;
  border-radius: 999rpx;
  background: rgba(34, 211, 238, 0.12);
  color: #67e8f9;
  font-size: 22rpx;
  border: 2rpx solid rgba(103, 232, 249, 0.28);
}
.qr-box {
  background: white;
  padding: 20rpx;
  border-radius: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.qr-placeholder {
  width: 300rpx;
  height: 300rpx;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;
}
.qr-code-text {
  color: #1f2937;
  font-size: 28rpx;
  font-weight: bold;
  font-family: monospace;
}
</style>
