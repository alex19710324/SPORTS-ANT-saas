<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { Link, Picture, Setting, Plus, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { io } from 'socket.io-client'
import { useRoute } from 'vue-router'

const nfts = ref<any[]>([])
const loading = ref(false)
const route = useRoute()
const routeUserId = computed(() => String(route.query.userId || '').trim())
let socket: any = null

const fetchNFTs = async () => {
  loading.value = true
  try {
    const res = await fetch('http://localhost:8080/api/trendy/nfts')
    nfts.value = await res.json()
  } catch (e) {
    console.error('Failed to load NFTs')
  } finally {
    loading.value = false
  }
}

const mintMockNFT = async () => {
  try {
    await fetch('http://localhost:8080/api/trendy/nfts', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: `Mystery Cyber Gear #${Math.floor(Math.random() * 1000)}`,
        rarity: Math.random() > 0.8 ? 'LEGENDARY' : 'RARE',
        owner: 'System Mint',
        image: 'http://localhost:8080/images/asset_cd3585c06d80ce9e62f4c080246b166c.jpg'
      })
    })
    ElMessage.success('Mock NFT Minted')
    fetchNFTs()
  } catch (e) {
    ElMessage.error('Mint failed')
  }
}

const getRarityTag = (rarity: string) => {
    switch(rarity) {
        case 'LEGENDARY': return 'warning';
        case 'RARE': return 'primary';
        case 'COMMON': return 'info';
        default: return 'info';
    }
}

onMounted(() => {
  fetchNFTs()
  
  socket = io('http://localhost:8080')
  socket.on('data_sync', (data: any) => {
    if (data.type === 'NFT_EQUIPPED') {
      ElMessage.success(`玩家 ${data.payload.userId} 在链上装备了 NFT: ${data.payload.nftName}`)
      fetchNFTs() // Optionally refresh to show 'EQUIPPED' status
    }
  })
})

onUnmounted(() => {
  if (socket) socket.disconnect()
})
</script>

<template>
  <div class="p-6 h-full flex flex-col">
    <div class="header mb-6 flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-bold flex items-center gap-2">
            <el-icon class="text-indigo-500"><Link /></el-icon>
            Digital Collectibles (NFT)
        </h2>
        <p class="text-gray-500">Manage blockchain-based digital assets and权益.</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="mintMockNFT">Mint New NFT</el-button>
    </div>

    <el-alert
      v-if="routeUserId"
      type="info"
      :closable="false"
      show-icon
      class="mb-6"
      title="5171 Web3 页面已带入用户上下文"
      :description="`当前为用户 ${routeUserId} 展示链上资产与 NFT 管理。`"
    />

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div v-for="nft in nfts" :key="nft.id" class="bg-white rounded-xl shadow overflow-hidden group hover:shadow-lg transition-all border border-gray-100">
            <div class="h-48 bg-gray-100 relative overflow-hidden">
                <img :src="nft.image" class="w-full h-full object-cover transform group-hover:scale-110 transition-transform duration-500" />
                <div class="absolute top-2 right-2">
                    <el-tag :type="getRarityTag(nft.rarity)" effect="dark">{{ nft.rarity }}</el-tag>
                </div>
            </div>
            <div class="p-4">
                <div class="text-xs text-gray-400 mb-1 font-mono">SERIES: {{ nft.series }}</div>
                <h3 class="font-bold text-lg mb-2">{{ nft.name }}</h3>
                <div class="flex justify-between items-center text-sm text-gray-500">
                    <div class="flex items-center gap-1">
                        <el-icon><User /></el-icon> Owner: {{ nft.owner }}
                    </div>
                    <el-tag size="small" type="success" effect="plain">{{ nft.status }}</el-tag>
                </div>
                <div class="mt-4 pt-4 border-t border-gray-50 flex justify-between">
                    <el-button size="small" text>History</el-button>
                    <el-button size="small" type="primary" plain>Transfer</el-button>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>
