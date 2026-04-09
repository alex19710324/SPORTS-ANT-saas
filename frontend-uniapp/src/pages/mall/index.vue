<template>
  <view class="mall-container">
    <view class="cyber-header">
      <text class="title">CYBER MALL</text>
      <view class="flex gap-2">
        <view class="coins-display border-yellow-400/50 bg-yellow-900/20">
          <text class="i-carbon-star text-yellow-400 text-sm mr-1"></text>
          <text class="amount text-yellow-400">{{ userAssets.points }}</text>
        </view>
        <view class="coins-display border-cyber-blue/50 bg-blue-900/20">
          <text class="i-carbon-wallet text-cyber-blue text-sm mr-1"></text>
          <text class="amount text-cyber-blue">¥ {{ userAssets.balance }}</text>
        </view>
      </view>
    </view>

    <!-- Search Bar -->
    <view class="px-4 mt-2 mb-4">
      <view class="bg-gray-900/60 border border-gray-700/50 rounded-full flex items-center px-4 py-2">
        <view class="i-carbon-search text-gray-400 mr-2"></view>
        <input type="text" placeholder="搜索盲盒、手办、门票" class="flex-1 text-sm text-white placeholder-gray-500 bg-transparent outline-none" />
      </view>
    </view>

    <!-- Rage Click Mock Button -->
    <view class="px-4 mb-4">
      <button class="w-full bg-red-900/20 border border-red-500/50 text-red-400 py-2 rounded-xl text-xs flex justify-center items-center gap-2" @click="mockRageClick">
        <text class="i-carbon-warning text-lg"></text>
        模拟结账卡顿 (触发 Rage Click AI 补偿)
      </button>
    </view>

    <!-- Categories -->
    <view class="flex overflow-x-auto gap-4 px-4 mb-6 scrollbar-hide">
      <view
        v-for="category in displayCategories"
        :key="category.id"
        class="whitespace-nowrap px-4 py-1 rounded-full text-xs font-bold border"
        :class="activeCategory === category.id ? 'bg-cyber-blue/20 text-cyber-blue border-cyber-blue' : 'bg-gray-800/50 text-gray-400 border-gray-700'"
        @click="activeCategory = category.id"
      >{{ category.name }}</view>
    </view>

    <!-- Gacha Section -->
    <view class="gacha-section relative overflow-hidden" v-show="activeCategory === 'blindbox' || activeCategory === 'all'">
      <!-- Compliance Banner -->
      <view class="absolute top-0 left-0 w-full bg-black/80 text-[10px] text-gray-400 py-1 px-3 flex justify-between items-center border-b border-gray-800 z-20">
        <text>【合规公示】本盲盒已备案。概率: N(70%) SR(25%) SSR(5%)</text>
        <text class="text-cyber-blue underline" @click="showCostModal = true">成本透明化公示</text>
      </view>

      <view class="flex justify-between items-center mt-6 mb-4">
        <view class="flex flex-col">
          <text class="neon-text">NEON BLIND BOX</text>
          <text class="subtitle">Drop Rate UP: SSR Skullpanda</text>
        </view>
        <button class="ar-preview-btn" @click="openARPreview">
          <text class="i-carbon-view text-xl"></text>
          <text class="text-xs ml-1 font-bold">AR 预览</text>
        </button>
      </view>
      
      <!-- 3D Gacha Machine Mock -->
      <view class="machine-container">
        <view class="machine" :class="{ 'is-shaking': isDrawing }">
          <view class="glass-dome">
            <view class="ball-container" :class="{ 'is-spinning': isDrawing }">
              <view class="ball red" style="top: 20%; left: 30%;"></view>
              <view class="ball blue" style="top: 50%; left: 20%;"></view>
              <view class="ball gold" style="top: 70%; left: 60%;"></view>
              <view class="ball purple" style="top: 30%; left: 70%;"></view>
              <view class="ball green" style="top: 60%; left: 40%;"></view>
            </view>
            <!-- Draw Result Overlay -->
            <view class="result-overlay" v-if="drawResult" :class="{ 'show': showResult }">
              <view class="glow-effect" :class="drawResult.rarity.toLowerCase()"></view>
              <text class="rarity-badge" :class="drawResult.rarity.toLowerCase()">{{ drawResult.rarity }}</text>
              <text class="item-name">{{ drawResult.name }}</text>
              <image class="item-img" :src="drawResult.image" mode="aspectFit"></image>
            </view>
          </view>
          <view class="machine-base">
            <view class="slot"></view>
          </view>
        </view>
      </view>

      <view class="draw-actions mt-4">
        <view class="w-full flex justify-center gap-4 mb-4">
          <button class="bg-gray-800 text-xs px-4 py-1 rounded-full text-gray-300 border border-gray-600" :class="{'border-cyber-blue text-cyber-blue bg-cyber-blue/20': payMethod === 'cash'}" @click="payMethod = 'cash'">
            RMB 支付 (¥{{ dynamicPriceCash }}/次)
          </button>
          <button class="bg-gray-800 text-xs px-4 py-1 rounded-full text-gray-300 border border-gray-600" :class="{'border-yellow-400 text-yellow-400 bg-yellow-400/20': payMethod === 'points'}" @click="payMethod = 'points'">
            积分全额抵扣 ({{ dynamicPricePoints }} pt)
          </button>
        </view>
        <view class="flex gap-4 w-full">
          <button class="cyber-btn draw-btn" @click="startDraw(1)" :disabled="isDrawing">
            <text>DRAW x1</text>
            <text class="cost">{{ payMethod === 'cash' ? `¥ ${dynamicPriceCash}.00` : `${dynamicPricePoints} Points` }}</text>
          </button>
          <button class="cyber-btn draw-btn gold-btn" @click="startDraw(10)" :disabled="isDrawing">
            <text>DRAW x10</text>
            <text class="cost">{{ payMethod === 'cash' ? `¥ ${dynamicPriceCash * 10}.00` : `${dynamicPricePoints * 10} Points` }}</text>
          </button>
        </view>
      </view>
    </view>

    <!-- Result Modal -->
    <view class="modal-mask" v-if="showResultModal" @click="closeResult">
      <view class="result-panel" @click.stop>
        <view class="panel-header">
          <text>UPLINK COMPLETE</text>
        </view>
        <view class="panel-body">
          <view class="item-display" :class="drawResult.rarity.toLowerCase()">
            <view class="rarity-bg"></view>
            <text class="item-rarity">{{ drawResult.rarity }}</text>
            <text class="item-title">{{ drawResult.name }}</text>
          </view>
          <view class="sync-status">
            <text class="iconfont i-carbon-data-check"></text>
            <text>Item synced to Store #sz-01 inventory</text>
          </view>
          <button class="cyber-btn claim-btn" @click="closeResult">CLAIM & NOTIFY STORE</button>
        </view>
      </view>
    </view>

    <!-- Retail Section -->
    <view class="retail-section">
      <view class="flex justify-between items-center mb-4">
        <text class="section-title mb-0">商城商品精选</text>
        <text class="text-xs text-pink-400" @click="goToGiftHistory">操作记录 ></text>
      </view>
      <view class="goods-grid">
        <view class="goods-card" v-for="item in filteredProducts" :key="item.id" @click="goProductDetail(item)">
          <image class="goods-cover" :src="item.image" mode="aspectFill"></image>
          <view class="goods-info">
            <text class="name">{{ item.name }}</text>
            <text class="goods-status" :class="item.saleStatus === 'ON_SHELF' && Number(item.stock) > 0 ? 'status-sale' : 'status-off'">{{ item.statusText }}</text>
            <view class="price-row mt-2">
              <view class="price-col">
                <text class="price text-pink-400 text-sm">¥{{ item.cashPrice }}</text>
                <text class="origin-price">¥{{ item.originalPrice }}</text>
              </view>
              <button class="buy-btn" :class="item.saleStatus === 'ON_SHELF' && Number(item.stock) > 0 ? 'buy-btn-sale' : 'buy-btn-off'" @click.stop="goProductDetail(item)">
                {{ item.saleStatus === 'ON_SHELF' && Number(item.stock) > 0 ? '查看详情' : item.statusText }}
              </button>
            </view>
          </view>
        </view>
      </view>
      <view v-if="!filteredProducts.length" class="empty-products">当前分类暂无可展示商品</view>
    </view>
    <!-- AR Preview Mock -->
    <view class="ar-overlay" v-if="showAR" @click="showAR = false">
      <view class="ar-camera-bg"></view>
      <view class="ar-content" @click.stop>
        <view class="ar-header">
          <text class="font-bold text-white tracking-widest">AR 潮玩互动预览</text>
          <text class="i-carbon-close text-2xl text-white" @click="showAR = false"></text>
        </view>
        
        <view class="ar-view-area">
          <view class="ar-target-box">
            <view class="ar-corner tl"></view>
            <view class="ar-corner tr"></view>
            <view class="ar-corner bl"></view>
            <view class="ar-corner br"></view>
            
            <image src="http://localhost:8080/images/asset_c62355a48fdc7534f71e9b081a3c17ca.jpg" class="ar-model animate-float" mode="aspectFit"></image>
            
            <view class="ar-info-tag">
              <text class="text-[10px] text-cyber-blue border border-cyber-blue/30 bg-black/60 px-2 py-0.5 rounded">SSR - 赛博拓荒者</text>
            </view>
          </view>
        </view>

        <view class="ar-controls">
          <button class="ar-btn" @click="showAR = false">放置于现实空间</button>
        </view>
      </view>
    </view>

    <!-- Cost Transparency Modal (PRD-013) -->
    <view class="modal-mask" v-if="showCostModal" @click="showCostModal = false">
      <view class="result-panel bg-gray-900 border-gray-600" @click.stop>
        <view class="panel-header bg-gray-800 text-gray-300">
          <text class="text-white">成本透明化公示</text>
        </view>
        <view class="p-6 text-sm text-gray-400 space-y-4">
          <view class="border-b border-gray-700 pb-2">
            <text class="block font-bold text-white mb-1">抽赏资金去向公示：</text>
            <text class="block">· 实体商品采购成本：35%</text>
            <text class="block">· 平台运营与技术维护：15%</text>
            <text class="block">· NFT 版权及上链 Gas 费：10%</text>
            <text class="block">· 剩余 40% 将注入「赛博奖池」，用于定期空投回馈玩家。</text>
          </view>
          <view>
            <text class="block font-bold text-white mb-1">保底机制：</text>
            <text class="block text-yellow-400">连续 50 抽未出 SSR，将自动触发「天命保底」，必得 SSR 并赠送隐藏款数字藏品。</text>
          </view>
          <button class="w-full mt-4 py-2 border border-gray-600 rounded text-gray-300 hover:bg-gray-800" @click="showCostModal = false">我已了解</button>
        </view>
      </view>
    </view>

  </view>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { io } from 'socket.io-client'
import request from '@/utils/request'
import { useActorContext } from '@/composables/useActorContext'
import { getMallCategories, getProductList } from '@/api/mall'

const activeCategory = ref('all')
const userAssets = ref({
  points: 1250,
  balance: 500
})
const giftHistory = ref<string[]>([])
const { userId } = useActorContext()
const mallCategories = ref<Array<{ id: string; name: string }>>([])
const mallProducts = ref<any[]>([])
const displayCategories = computed(() => [{ id: 'all', name: '全部商品' }, ...mallCategories.value])
const filteredProducts = computed(() => {
  if (activeCategory.value === 'all') return mallProducts.value
  return mallProducts.value.filter((item) => item.categoryId === activeCategory.value)
})

const buyVIP = async (item: any) => {
  uni.showLoading({ title: '开通中...' })
  try {
    // 1. Create order
    await request({
      url: '/orders',
      method: 'POST',
      data: {
        userId: userId.value,
        amount: item.price,
        items: [{ name: item.name }]
      }
    })
    
    // 2. Open Gate
    await request({
      url: '/iot/gate/open',
      method: 'POST',
      data: {
        userId: userId.value,
        zone: 'VIP Lounge'
      }
    })
    
    uni.hideLoading()
    uni.showToast({ title: '开通成功！闸机已为您开启。', icon: 'none', duration: 3000 })
  } catch (e) {
    uni.hideLoading()
  }
}

const dynamicPriceCash = ref(59)
const dynamicPricePoints = ref(500)

let socket: any = null

onMounted(() => {
  socket = io('http://localhost:8080')
  Promise.allSettled([getMallCategories(), getProductList()]).then(([categoriesRes, productsRes]) => {
    if (categoriesRes.status === 'fulfilled') {
      const categories = categoriesRes.value?.data || categoriesRes.value || []
      mallCategories.value = Array.isArray(categories) ? categories : []
    }
    if (productsRes.status === 'fulfilled') {
      const products = productsRes.value?.data || productsRes.value || []
      mallProducts.value = Array.isArray(products) ? products : []
      const featuredBlindbox = mallProducts.value.find((item) => item.categoryId === 'blindbox' && item.saleStatus === 'ON_SHELF')
      if (featuredBlindbox) {
        dynamicPriceCash.value = Number(featuredBlindbox.cashPrice || dynamicPriceCash.value)
        dynamicPricePoints.value = Number(featuredBlindbox.pointsPrice || dynamicPricePoints.value)
      }
    }
  })
  request({
    url: '/api/mobile/profile',
    method: 'GET'
  }).then((res: any) => {
    if (res?.data) {
      userAssets.value.points = res.data.pts ?? userAssets.value.points
      userAssets.value.balance = res.data.balance ?? userAssets.value.balance
    }
  }).catch(() => {})
  socket.on('data_sync', (data: any) => {
    if (data.type === 'ESL_PRICE_UPDATED') {
      if (data.payload.productNameKeyword === '盲盒') {
        dynamicPriceCash.value = data.payload.newPrice
        uni.showToast({ title: `商城价格已同步更新为: ¥${data.payload.newPrice}`, icon: 'none' })
      }
    }
    if (data.type === 'DYNAMIC_PRICING_APPLIED') {
      // Apply 15% surge
      dynamicPriceCash.value = Math.round(dynamicPriceCash.value * 1.15)
      uni.showModal({
        title: '📊 实时价格波动提示',
        content: `系统检测到当前场馆客流激增。根据智能定价策略，部分热门商品/门票价格已上调。`,
        showCancel: false,
        confirmText: '我知道了'
      })
    }
    if (data.type === 'GIFT_REDEEMED' && data.payload.member === userId.value) {
      giftHistory.value.unshift(`${data.payload.giftName} · ${data.payload.points} pt · 刚刚`)
    }
  })
})

onUnmounted(() => {
  if (socket) socket.disconnect()
})

const payMethod = ref('cash')
const showCostModal = ref(false)

const showAR = ref(false)
const openARPreview = () => {
  uni.showLoading({ title: '加载AR引擎...' })
  setTimeout(() => {
    uni.hideLoading()
    showAR.value = true
  }, 800)
}

const isDrawing = ref(false)
const drawResult = ref<any>(null)
const showResult = ref(false)
const showResultModal = ref(false)

let clickCount = 0;
let clickTimer: any = null;

const mockRageClick = async () => {
  clickCount++;
  if (!clickTimer) {
    clickTimer = setTimeout(() => {
      clickCount = 0;
      clickTimer = null;
    }, 2000);
  }
  
  if (clickCount >= 5) {
    // Trigger rage click
    try {
      await request({
        url: '/ux/rage-click',
        method: 'POST',
        data: { userId: userId.value, page: 'Mall Checkout', element: 'Pay Button' }
      })
      clickCount = 0;
    } catch(e) {}
  } else {
    uni.showToast({ title: '网络请求中，请稍候...', icon: 'none' })
  }
}

const goProductDetail = (item: any) => {
  uni.navigateTo({ url: `/pages/mall/detail/index?id=${encodeURIComponent(item.id)}` })
}

const redeemGift = async (item: any) => {
  goProductDetail(item)
}

const goToGiftHistory = () => {
  uni.showModal({
    title: '最近兑换记录',
    content: giftHistory.value.length ? giftHistory.value.join('\n') : '暂无兑换记录',
    showCancel: false
  })
}

const startDraw = async (times: number) => {
  if (isDrawing.value) return
  isDrawing.value = true
  drawResult.value = null
  showResult.value = false
  
  // Simulate order creation for Gacha
  try {
    await request({
      url: '/orders',
      method: 'POST',
      data: {
        userId: 'Player_1',
        amount: times * 39,
        items: [{ name: `Gacha Draw x${times}` }]
      }
    })
  } catch(e) {}

  // Simulate 3D Shake and Spin
  setTimeout(() => {
    // Generate Result
    const isSSR = Math.random() > 0.8
    drawResult.value = {
      rarity: isSSR ? 'SSR' : 'SR',
      name: isSSR ? 'Neon Skullpanda - Phantom' : 'Cyber Trooper - Delta',
      image: '/static/logo.svg' // Placeholder
    }
    showResult.value = true
    
    // Show Modal
    setTimeout(() => {
      showResultModal.value = true
      isDrawing.value = false
      
      // SEND MOCK SYNC DATA TO STORE
      console.log(`[SYNC] Gacha pulled: ${drawResult.value.name}. Notifying Backend...`);
      const socket = io('http://localhost:8080')
      socket.emit('data_sync', {
        type: 'GACHA_PULL',
        payload: {
          item: drawResult.value.name,
          rarity: drawResult.value.rarity
        }
      })
      setTimeout(() => socket.disconnect(), 1000)
    }, 1500)
  }, 2000)
}

const closeResult = () => {
  showResultModal.value = false
  showResult.value = false
  drawResult.value = null
  uni.showToast({ title: 'Order sent to store!', icon: 'none' })
}


</script>

<style scoped>
.mall-container {
  padding: 20rpx;
  padding-bottom: 120rpx;
  min-height: 100vh;
}

.cyber-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
  margin-top: 20rpx;
}

.title {
  font-size: 40rpx;
  font-weight: 900;
  color: #fff;
  text-shadow: 0 0 10px rgba(0, 240, 255, 0.5);
  letter-spacing: 2rpx;
}

.coins-display {
  display: flex;
  align-items: center;
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(0, 240, 255, 0.3);
  border-radius: 30rpx;
  padding: 5rpx 10rpx 5rpx 20rpx;
}

.coins-display .iconfont {
  color: #00F0FF;
  font-size: 28rpx;
  margin-right: 10rpx;
}

.coins-display .amount {
  color: #fff;
  font-size: 28rpx;
  font-weight: bold;
  font-family: monospace;
  margin-right: 20rpx;
}

.add-btn {
  width: 40rpx;
  height: 40rpx;
  background: #00F0FF;
  color: #000;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 24rpx;
}

/* Gacha Machine Styles */
.gacha-section {
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(168, 85, 247, 0.3);
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 40rpx;
  box-shadow: inset 0 0 30rpx rgba(168, 85, 247, 0.1);
}

.section-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30rpx;
}

.neon-text {
  font-size: 36rpx;
  font-weight: 900;
  color: #fff;
  text-shadow: 0 0 10px #a855f7, 0 0 20px #a855f7;
  letter-spacing: 4rpx;
}

.subtitle {
  font-size: 20rpx;
  color: #d8b4fe;
  margin-top: 10rpx;
}

.machine-container {
  height: 400rpx;
  display: flex;
  justify-content: center;
  margin-bottom: 40rpx;
  perspective: 800px;
}

.machine {
  width: 300rpx;
  height: 100%;
  position: relative;
  transform-style: preserve-3d;
}

.is-shaking {
  animation: shake 0.5s ease-in-out infinite;
}

@keyframes shake {
  0% { transform: translate(1px, 1px) rotate(0deg); }
  10% { transform: translate(-1px, -2px) rotate(-1deg); }
  20% { transform: translate(-3px, 0px) rotate(1deg); }
  30% { transform: translate(3px, 2px) rotate(0deg); }
  40% { transform: translate(1px, -1px) rotate(1deg); }
  50% { transform: translate(-1px, 2px) rotate(-1deg); }
  60% { transform: translate(-3px, 1px) rotate(0deg); }
  70% { transform: translate(3px, 1px) rotate(-1deg); }
  80% { transform: translate(-1px, -1px) rotate(1deg); }
  90% { transform: translate(1px, 2px) rotate(0deg); }
  100% { transform: translate(1px, -2px) rotate(-1deg); }
}

.glass-dome {
  width: 100%;
  height: 250rpx;
  background: radial-gradient(circle at 30% 30%, rgba(255,255,255,0.2), rgba(255,255,255,0.05));
  border: 2px solid rgba(0, 240, 255, 0.4);
  border-radius: 50% 50% 0 0;
  position: relative;
  overflow: hidden;
  box-shadow: inset 0 0 30rpx rgba(0, 240, 255, 0.2);
}

.ball-container {
  position: absolute;
  width: 100%;
  height: 100%;
}

.is-spinning {
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.ball {
  position: absolute;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  box-shadow: inset -5rpx -5rpx 10rpx rgba(0,0,0,0.5), inset 5rpx 5rpx 10rpx rgba(255,255,255,0.8);
}

.ball.red { background: radial-gradient(circle at 30% 30%, #ff4b4b, #991b1b); }
.ball.blue { background: radial-gradient(circle at 30% 30%, #60a5fa, #1e3a8a); }
.ball.gold { background: radial-gradient(circle at 30% 30%, #fbbf24, #b45309); }
.ball.purple { background: radial-gradient(circle at 30% 30%, #c084fc, #6b21a8); }
.ball.green { background: radial-gradient(circle at 30% 30%, #34d399, #065f46); }

.machine-base {
  width: 110%;
  height: 150rpx;
  margin-left: -5%;
  background: linear-gradient(180deg, #1e293b, #0f172a);
  border: 2px solid #334155;
  border-top: 4px solid #a855f7;
  border-radius: 0 0 20rpx 20rpx;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

.slot {
  width: 80rpx;
  height: 80rpx;
  background: #000;
  border: 2px solid #475569;
  border-radius: 10rpx;
  box-shadow: inset 0 0 20rpx #000;
}

.result-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.5s;
  z-index: 10;
}

.result-overlay.show {
  opacity: 1;
}

.glow-effect {
  position: absolute;
  width: 150rpx;
  height: 150rpx;
  border-radius: 50%;
  filter: blur(20px);
  z-index: -1;
}

.glow-effect.ssr { background: rgba(251, 191, 36, 0.6); animation: pulse-glow 2s infinite; }
.glow-effect.sr { background: rgba(168, 85, 247, 0.6); }

@keyframes pulse-glow {
  0% { transform: scale(1); opacity: 0.6; }
  50% { transform: scale(1.5); opacity: 0.2; }
  100% { transform: scale(1); opacity: 0.6; }
}

.rarity-badge {
  font-size: 40rpx;
  font-weight: 900;
  font-style: italic;
  margin-bottom: 10rpx;
}

.rarity-badge.ssr { color: #fbbf24; text-shadow: 0 0 10px #fbbf24; }
.rarity-badge.sr { color: #c084fc; text-shadow: 0 0 10px #c084fc; }

.draw-actions {
  display: flex;
  gap: 20rpx;
}

.draw-btn {
  flex: 1;
  height: 100rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  line-height: 1.2;
  background: linear-gradient(180deg, rgba(30, 41, 59, 0.8), rgba(15, 23, 42, 0.9));
  border: 1px solid rgba(0, 240, 255, 0.4);
  border-radius: 12rpx;
  color: #fff;
  font-weight: bold;
}

.draw-btn .cost {
  font-size: 20rpx;
  color: #00F0FF;
  font-weight: normal;
}

.gold-btn {
  border-color: rgba(251, 191, 36, 0.6);
}
.gold-btn .cost {
  color: #fbbf24;
}

/* Modal */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(5px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-panel {
  width: 80%;
  background: #0B0C10;
  border: 2px solid #00F0FF;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 0 50rpx rgba(0, 240, 255, 0.3);
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from { transform: translateY(50rpx); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.panel-header {
  background: #00F0FF;
  padding: 20rpx;
  text-align: center;
}

.panel-header text {
  color: #000;
  font-weight: 900;
  font-size: 32rpx;
  letter-spacing: 2rpx;
}

.panel-body {
  padding: 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.item-display {
  width: 200rpx;
  height: 200rpx;
  background: #1e293b;
  border-radius: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 40rpx;
  position: relative;
}

.item-display.ssr { border: 2px solid #fbbf24; box-shadow: 0 0 30rpx rgba(251, 191, 36, 0.4); }
.item-display.sr { border: 2px solid #c084fc; box-shadow: 0 0 30rpx rgba(192, 132, 252, 0.4); }

.item-rarity {
  font-size: 48rpx;
  font-weight: 900;
  font-style: italic;
  margin-bottom: 10rpx;
}
.ssr .item-rarity { color: #fbbf24; }
.sr .item-rarity { color: #c084fc; }

.item-title {
  font-size: 24rpx;
  color: #fff;
  text-align: center;
  padding: 0 10rpx;
}

.sync-status {
  display: flex;
  align-items: center;
  gap: 10rpx;
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.3);
  padding: 10rpx 20rpx;
  border-radius: 10rpx;
  margin-bottom: 40rpx;
}

.sync-status text {
  color: #10b981;
  font-size: 24rpx;
}

.claim-btn {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background: #00F0FF;
  color: #000;
  font-weight: bold;
  border-radius: 10rpx;
}

/* Retail Section */
.retail-section {
  margin-top: 40rpx;
}

.retail-section .section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
  border-left: 8rpx solid #00F0FF;
  padding-left: 20rpx;
  margin-bottom: 30rpx;
}

.goods-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
}

.goods-card {
  background: rgba(30, 41, 59, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16rpx;
  overflow: hidden;
}

.goods-cover {
  height: 200rpx;
  width: 100%;
  display: block;
  background: rgba(0, 0, 0, 0.5);
}

.goods-info {
  padding: 20rpx;
}

.goods-info .name {
  font-size: 26rpx;
  color: #e2e8f0;
  display: block;
  margin-bottom: 10rpx;
  height: 72rpx;
  line-height: 36rpx;
}

.goods-status {
  display: inline-block;
  font-size: 20rpx;
  border-radius: 20rpx;
  padding: 4rpx 12rpx;
  margin-bottom: 12rpx;
}

.status-sale {
  color: #34d399;
  background: rgba(16, 185, 129, 0.12);
}

.status-off {
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.12);
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-col {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.price {
  color: #00F0FF;
  font-weight: bold;
  font-size: 32rpx;
}

.origin-price {
  color: #64748b;
  font-size: 22rpx;
  text-decoration: line-through;
}

.buy-btn {
  margin: 0;
  padding: 0 20rpx;
  height: 50rpx;
  line-height: 50rpx;
  font-size: 22rpx;
  background: transparent;
  color: #00F0FF;
  border: 1px solid #00F0FF;
  border-radius: 25rpx;
}

.buy-btn-sale {
  color: #f472b6;
  border-color: #f472b6;
}

.buy-btn-off {
  color: #94a3b8;
  border-color: #475569;
}

.empty-products {
  text-align: center;
  color: #94a3b8;
  font-size: 24rpx;
  padding: 32rpx 0 8rpx;
}

.ar-preview-btn {
  background: rgba(0, 240, 255, 0.1);
  border: 1px solid rgba(0, 240, 255, 0.4);
  color: #00F0FF;
  padding: 8rpx 20rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  line-height: 1;
}

/* AR Mock Styles */
.ar-overlay {
  position: fixed;
  inset: 0;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ar-camera-bg {
  position: absolute;
  inset: 0;
  background: url('http://localhost:8080/images/asset_d6b9b95e2b8fb193157832614cadf5f7.jpg') center/cover;
  filter: grayscale(0.5) contrast(1.2);
  opacity: 0.8;
}

.ar-content {
  position: relative;
  z-index: 10;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.ar-header {
  padding: 100rpx 40rpx 40rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(to bottom, rgba(0,0,0,0.8), transparent);
}

.ar-view-area {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ar-target-box {
  width: 500rpx;
  height: 600rpx;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed rgba(0, 240, 255, 0.3);
}

.ar-corner {
  position: absolute;
  width: 40rpx;
  height: 40rpx;
  border-color: #00F0FF;
  border-style: solid;
}

.tl { top: -2rpx; left: -2rpx; border-width: 4rpx 0 0 4rpx; }
.tr { top: -2rpx; right: -2rpx; border-width: 4rpx 4rpx 0 0; }
.bl { bottom: -2rpx; left: -2rpx; border-width: 0 0 4rpx 4rpx; }
.br { bottom: -2rpx; right: -2rpx; border-width: 0 4rpx 4rpx 0; }

.ar-model {
  width: 300rpx;
  height: 400rpx;
  filter: drop-shadow(0 20rpx 30rpx rgba(0, 0, 0, 0.8));
}

.ar-info-tag {
  position: absolute;
  bottom: -40rpx;
  left: 50%;
  transform: translateX(-50%);
}

.animate-float {
  animation: float 4s ease-in-out infinite;
}

@keyframes float {
  0% { transform: translateY(0); }
  50% { transform: translateY(-20rpx); }
  100% { transform: translateY(0); }
}

.ar-controls {
  padding: 60rpx 40rpx;
  background: linear-gradient(to top, rgba(0,0,0,0.9), transparent);
}

.ar-btn {
  background: linear-gradient(90deg, #00F0FF, #0055FF);
  color: #fff;
  font-weight: bold;
  border-radius: 40rpx;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  box-shadow: 0 0 20rpx rgba(0, 240, 255, 0.4);
}
</style>
