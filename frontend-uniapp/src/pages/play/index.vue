<template>
  <view class="play-container">
    <view class="play-shell">
    <view class="cyber-header px-4 pt-5 pb-4 sticky top-0 z-50 bg-black/90 backdrop-blur-md border-b border-cyber-blue/20">
      <view class="header-badge">沉浸式场馆节点</view>
      <view class="flex justify-between items-center mb-4">
        <view class="header-copy">
          <text class="header-title">CYBER<text class="text-cyber-blue">PLAY</text></text>
          <text class="header-subtitle">NEURAL LINK ACTIVE // HANGZHOU</text>
        </view>
        <view class="header-radar w-10 h-10 rounded-full border border-cyber-blue/30 flex items-center justify-center bg-cyber-blue/10">
          <view class="i-carbon-radar text-xl text-cyber-blue"></view>
        </view>
      </view>
      
      <view class="search-bar bg-gray-900/80 border border-gray-700/50 rounded-xl flex items-center px-4 py-3">
        <view class="i-carbon-search text-gray-400 mr-2"></view>
        <input type="text" placeholder="搜索元宇宙节点、VR实景..." class="flex-1 text-sm text-white placeholder-gray-500 bg-transparent outline-none" />
      </view>
      <view class="header-summary">
        <view class="summary-chip">
          <text class="summary-value">{{ games.length }}</text>
          <text class="summary-label">在线节点</text>
        </view>
        <view class="summary-chip">
          <text class="summary-value">{{ games.filter((item: any) => item.hot).length }}</text>
          <text class="summary-label">推荐热场</text>
        </view>
        <view class="summary-chip">
          <text class="summary-value">IoT</text>
          <text class="summary-label">实时联动</text>
        </view>
      </view>
    </view>

    <view class="section-block category-panel">
      <view class="section-head">
        <text class="section-title">探索专区</text>
        <text class="section-note">按体验类型快速切换推荐场景</text>
      </view>
      <scroll-view scroll-x class="category-scroll whitespace-nowrap py-1" :show-scrollbar="false">
      <view class="category-row">
        <view 
          v-for="(cat, index) in categories" 
          :key="index"
          class="category-chip px-5 py-2 rounded-full text-sm font-bold border transition-all"
          :class="activeCategory === index ? 'bg-cyber-blue/20 text-cyber-blue border-cyber-blue shadow-[0_0_10px_rgba(0,240,255,0.3)]' : 'bg-gray-900 text-gray-400 border-gray-800'"
          @click="activeCategory = index"
        >
          {{ cat }}
        </view>
      </view>
    </scroll-view>
    </view>

    <view class="section-block ar-prompt bg-gradient-to-r from-purple-900/40 to-blue-900/40 border border-purple-500/30 rounded-2xl p-4 flex items-center justify-between" @click="openARMap">
      <view class="flex items-center gap-3 ar-copy">
        <view class="ar-icon i-carbon-virtual-reality text-2xl text-purple-400"></view>
        <view>
          <text class="text-base font-bold text-white block">启动 AR 场馆导航</text>
          <text class="text-xs text-purple-300/70 block mt-1">寻找隐藏的赛博彩蛋，解锁即时入场加速奖励</text>
        </view>
      </view>
      <view class="ar-arrow i-carbon-chevron-right text-gray-400"></view>
    </view>

    <view class="section-head game-section-head">
      <text class="section-title">热门体验</text>
      <text class="section-note">按热度、空闲状态与体验类型实时排序</text>
    </view>

    <view class="game-list flex flex-col gap-5 pb-6">
      <view v-for="(item, index) in filteredGames" :key="item.id || index" class="game-card group" @click="goDetail(item)">
        <view class="game-card-media relative h-44 rounded-t-2xl overflow-hidden">
          <image :src="item.image" mode="aspectFill" class="game-card-cover w-full h-full opacity-80 group-hover:scale-105 transition-transform duration-500" />
          <view class="absolute inset-0 bg-gradient-to-t from-black via-black/20 to-transparent"></view>
          
          <view class="absolute top-3 left-3 px-2 py-1 rounded bg-black/60 border backdrop-blur-sm flex items-center gap-1" :class="item.status === 'AVAILABLE' ? 'border-green-500/50 text-green-400' : 'border-red-500/50 text-red-400'">
            <view class="w-1.5 h-1.5 rounded-full" :class="item.status === 'AVAILABLE' ? 'bg-green-400' : 'bg-red-400'"></view>
            <text class="text-[10px] font-bold">{{ item.status === 'AVAILABLE' ? '空闲中' : '爆满排队' }}</text>
          </view>

          <view v-if="item.hot" class="absolute top-3 right-3 px-2 py-1 rounded bg-gradient-to-r from-orange-500 to-red-500 text-white text-[10px] font-bold shadow-[0_0_10px_rgba(239,68,68,0.5)]">
            HOT 🔥
          </view>
          <view class="game-card-glow"></view>
        </view>
        
        <view class="game-card-body bg-gray-900 border border-gray-800 border-t-0 rounded-b-2xl p-3 relative overflow-hidden">
          <view class="absolute bottom-0 right-0 w-20 h-20 bg-cyber-blue/5 blur-xl rounded-full"></view>
          
          <view class="flex justify-between items-start gap-4 mb-2 relative z-10">
            <view class="game-main-copy">
              <text class="game-name text-base font-bold text-white block">{{ item.name }}</text>
              <text class="game-desc text-xs text-gray-400 block mt-2">适合想快速沉浸、即时开局的玩家，支持移动端下单与 IoT 实时授权入场。</text>
              <view class="flex gap-2 mt-3 flex-wrap">
                <text v-for="tag in item.tags" :key="tag" class="text-[10px] text-gray-400 border border-gray-700 px-2 py-1 rounded-full">{{ tag }}</text>
              </view>
            </view>
            <view class="text-right price-panel">
              <text class="text-[10px] text-gray-500 block tracking-[0.2em]">PRICE</text>
              <text class="text-cyber-blue font-mono font-bold text-xl mt-1 block">¥{{ item.price }}</text>
              <text class="text-[10px] text-gray-500 block">/ 局</text>
            </view>
          </view>
          
          <view class="flex justify-between items-center mt-4 pt-4 border-t border-gray-800/50 relative z-10">
            <view class="flex items-center gap-1 status-group">
              <view class="flex -space-x-2">
                <view class="w-5 h-5 rounded-full bg-gray-700 border border-black"></view>
                <view class="w-5 h-5 rounded-full bg-gray-600 border border-black"></view>
                <view class="w-5 h-5 rounded-full bg-gray-500 border border-black flex items-center justify-center text-[8px]">+8</view>
              </view>
              <text class="text-[10px] text-gray-500 ml-2">正在游玩</text>
            </view>
            <button class="game-card-cta m-0 px-6 py-1.5 rounded-full text-xs font-bold shadow-[0_0_10px_rgba(0,240,255,0.2)] active:scale-95 transition-transform" 
                    :class="item.status === 'AVAILABLE' ? 'bg-cyber-blue text-black' : 'bg-gray-800 text-gray-400'"
                    @click.stop="handleBook(item)">
              {{ item.status === 'AVAILABLE' ? '立即跃迁' : '加入队列' }}
            </button>
          </view>
        </view>
      </view>
    </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { io } from 'socket.io-client'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request'
import { useActorContext } from '@/composables/useActorContext'
import { getPlayList } from '@/api/play'

const activeCategory = ref(0)
const categories = ['全部节点', '大空间VR', '全息剧本杀', '赛博射箭', '硬核电竞', '主机包厢']

const games = ref<any[]>([])
const { userId } = useActorContext()
const filteredGames = computed(() => {
  const current = categories[activeCategory.value]
  if (!current || current === '全部节点') return games.value
  return games.value.filter((item) => Array.isArray(item.tags) && item.tags.some((tag: string) => String(tag).includes(current)))
})

const buildPoster = (title: string, accent: string) =>
  `data:image/svg+xml;utf8,${encodeURIComponent(`
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 480">
      <defs>
        <linearGradient id="bg" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0%" stop-color="#0B0C10" />
          <stop offset="100%" stop-color="${accent}" />
        </linearGradient>
      </defs>
      <rect width="800" height="480" fill="url(#bg)" />
      <circle cx="650" cy="120" r="120" fill="rgba(255,255,255,0.08)" />
      <circle cx="180" cy="360" r="140" fill="rgba(255,255,255,0.05)" />
      <text x="60" y="210" fill="#ffffff" font-size="52" font-family="Arial, sans-serif" font-weight="700">${title}</text>
      <text x="60" y="270" fill="#cbd5e1" font-size="26" font-family="Arial, sans-serif">SPORTS ANT CYBER PLAY</text>
    </svg>
  `)}`

const fetchGames = async () => {
  try {
    const res: any = await getPlayList()
    games.value = Array.isArray(res?.data)
      ? res.data.map((item: any, index: number) => ({
          ...item,
          id: item.id || `play-${index + 1}`,
          name: item.name || item.title || `体验项目 ${index + 1}`,
          image: item.image || buildPoster(item.name || item.title || `体验项目 ${index + 1}`, ['#2563EB', '#7C3AED', '#EA580C'][index % 3]),
          price: Number(item.price || 88),
          status: item.status || 'AVAILABLE',
          hot: !!item.hot,
          tags: Array.isArray(item.tags) ? item.tags : []
        }))
      : []
  } catch (e) {
    games.value = []
  }
}

const openARMap = () => {
  uni.showToast({ title: 'AR 导航建设中', icon: 'none' })
}

const goDetail = (item: any) => {
  uni.navigateTo({ url: `/pages/play/detail/index?id=${encodeURIComponent(item.id)}` })
}

const handleBook = async (item: any) => {
  if (item.status !== 'AVAILABLE') {
    uni.navigateTo({ url: '/pages/queue/index' })
    return
  }

  uni.showLoading({ title: '跃迁中...' })
  try {
    await request({
      url: '/api/mobile/orders/pos',
      method: 'POST',
      timeout: 5000,
      data: {
        userId: userId.value,
        amount: item.price,
        items: [{ name: item.name }],
        source: 'MOBILE_APP_BOOKING'
      }
    })

    await request({
      url: '/api/mobile/iot/gate/open',
      method: 'POST',
      timeout: 5000,
      data: {
        userId: userId.value,
        zone: item.name
      }
    })

    const socket = io('http://localhost:8080')
    socket.emit('data_sync', { type: 'GATE_OPENED', payload: { zone: item.name, user: userId.value } })
    socket.emit('state_update', { type: 'PAYMENT_SUCCESS', payload: { amount: item.price, source: 'Play Booking (5171)' } })
    setTimeout(() => socket.disconnect(), 1000)

    uni.hideLoading()
    uni.showToast({ title: '跃迁成功！IoT闸机已授权', icon: 'success' })
  } catch (e) {
    uni.hideLoading()
    uni.showToast({ title: '网络异常', icon: 'none' })
  }
}

onLoad(() => {
  fetchGames()
})
</script>

<style scoped>
.play-container {
  min-height: 100vh;
  background-color: #0B0C10;
  width: 100%;
  overflow-x: hidden;
}

.play-shell {
  width: 100%;
  max-width: 760px;
  margin: 0 auto;
  padding: 0 16px 28px;
  box-sizing: border-box;
}

.cyber-header,
.game-list,
scroll-view,
.section-block {
  box-sizing: border-box;
}

.cyber-header {
  margin: 0 -16px;
  padding-left: 16px;
  padding-right: 16px;
  border-radius: 0 0 24px 24px;
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.4);
}

.header-badge {
  display: inline-flex;
  align-items: center;
  margin-bottom: 14px;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.18em;
  color: rgba(103, 232, 249, 0.92);
  background: rgba(8, 47, 73, 0.6);
  border: 1px solid rgba(34, 211, 238, 0.22);
}

.header-copy {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.header-title {
  display: block;
  font-size: 34rpx;
  font-weight: 900;
  color: #fff;
  font-style: italic;
  letter-spacing: 0.08em;
}

.header-subtitle {
  display: block;
  font-size: 11px;
  color: rgba(103, 232, 249, 0.72);
  letter-spacing: 0.16em;
}

.header-radar {
  box-shadow: 0 0 20px rgba(34, 211, 238, 0.14);
}

.search-bar {
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);
}

.header-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-top: 14px;
}

.summary-chip {
  padding: 12px 10px;
  border-radius: 16px;
  background: rgba(17, 24, 39, 0.72);
  border: 1px solid rgba(55, 65, 81, 0.55);
}

.summary-value {
  display: block;
  color: #f8fafc;
  font-size: 16px;
  font-weight: 800;
}

.summary-label {
  display: block;
  margin-top: 4px;
  color: #64748b;
  font-size: 10px;
}

.section-block {
  margin-top: 18px;
}

.section-head {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

.section-title {
  font-size: 15px;
  font-weight: 800;
  color: #f8fafc;
}

.section-note {
  font-size: 11px;
  color: #64748b;
}

.category-panel {
  padding: 14px 14px 12px;
  border-radius: 20px;
  background: rgba(15, 23, 42, 0.5);
  border: 1px solid rgba(30, 41, 59, 0.8);
}

.category-scroll {
  margin-right: -14px;
}

.category-row {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding-right: 14px;
}

.category-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
  flex-shrink: 0;
}

.ar-prompt {
  box-shadow: 0 16px 28px rgba(76, 29, 149, 0.18);
}

.ar-copy {
  flex: 1;
}

.ar-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: rgba(76, 29, 149, 0.24);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ar-arrow {
  flex-shrink: 0;
  margin-left: 12px;
}

.game-section-head {
  margin-top: 22px;
  margin-bottom: 14px;
}

.game-list {
  padding-bottom: 48rpx;
}

.game-card {
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 20px 34px rgba(2, 6, 23, 0.42);
}

.game-card-media {
  width: 100%;
  height: 220px;
}

.game-card-cover {
  display: block;
  width: 100%;
  height: 100%;
}

.game-card-glow {
  position: absolute;
  inset: auto 0 0 auto;
  width: 140px;
  height: 140px;
  background: rgba(34, 211, 238, 0.16);
  filter: blur(48px);
  border-radius: 999px;
  transform: translate(22%, 28%);
}

.game-card-body {
  padding: 18px 18px 16px;
}

.game-main-copy {
  flex: 1;
  min-width: 0;
}

.game-name {
  line-height: 1.35;
}

.game-desc {
  line-height: 1.6;
}

.price-panel {
  min-width: 82px;
}

.game-card-cta {
  width: auto;
  min-width: 112px;
  padding: 10px 18px;
  flex-shrink: 0;
  border: 0;
  line-height: 1;
}

.status-group {
  min-width: 0;
}

@media screen and (min-width: 768px) {
  .play-shell {
    max-width: 760px;
  }

  .cyber-header {
    border-left: 1px solid rgba(34, 211, 238, 0.12);
    border-right: 1px solid rgba(34, 211, 238, 0.12);
  }
}
/* Hiding scrollbar for webkit */
::-webkit-scrollbar {
  display: none;
}
</style>
