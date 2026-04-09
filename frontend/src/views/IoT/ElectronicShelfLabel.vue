<template>
  <div class="esl-dashboard p-6 h-full flex flex-col bg-gray-50">
    <div class="flex justify-between items-center mb-6">
      <div>
        <h1 class="text-2xl font-bold flex items-center gap-2 text-gray-800">
          <el-icon class="text-cyan-600"><PriceTag /></el-icon>
          电子价签管理网络 (ESL Gateway)
        </h1>
        <p class="text-gray-500 text-sm mt-1">PRD-103: 基于 NB-IoT 的门店商品数字价签实时同步系统</p>
      </div>
    </div>

    <!-- Status Overview -->
    <el-row :gutter="20" class="mb-6">
      <el-col :span="6">
        <el-card shadow="hover" class="border-l-4 border-cyan-500">
          <div class="text-sm text-gray-500 mb-1">在线价签数</div>
          <div class="text-2xl font-bold text-gray-800">{{ eslData.online }} / {{ eslData.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="border-l-4 border-green-500">
          <div class="text-sm text-gray-500 mb-1">今日同步成功率</div>
          <div class="text-2xl font-bold text-gray-800">99.8%</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="border-l-4 border-red-500">
          <div class="text-sm text-gray-500 mb-1">低电量告警</div>
          <div class="text-2xl font-bold text-red-600">{{ eslData.lowBattery }} 台</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="border-l-4 border-purple-500">
          <div class="text-sm text-gray-500 mb-1">待执行的促销改价</div>
          <div class="text-2xl font-bold text-purple-600">2 项计划</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ESL List -->
    <el-card shadow="never" class="flex-1 overflow-hidden flex flex-col">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="font-bold">价签硬件节点列表</span>
          <div class="flex gap-2">
            <el-button type="warning" plain @click="simulatePriceChange">
              <el-icon class="mr-1"><Lightning /></el-icon> 模拟下发【节假日盲盒降价大促】指令
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="tags" style="width: 100%" height="100%">
        <el-table-column prop="mac" label="MAC 地址" width="160" />
        <el-table-column prop="location" label="部署位置" width="150" />
        <el-table-column prop="product" label="绑定商品" min-width="200">
          <template #default="{ row }">
            <span class="font-bold">{{ row.product }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="当前显示价格" width="120">
          <template #default="{ row }">
            <span class="font-mono text-cyan-600 font-bold">¥{{ row.price.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="battery" label="电量" width="100">
          <template #default="{ row }">
            <el-progress :percentage="row.battery" :status="row.battery < 20 ? 'exception' : 'success'" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="通信状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ONLINE' ? 'success' : 'danger'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { PriceTag, Lightning } from '@element-plus/icons-vue'
import { io } from 'socket.io-client'
import { ElMessage } from 'element-plus'

const eslData = ref({ total: 1240, online: 1238, lowBattery: 3 })

const tags = ref([
  { mac: '00:1A:2B:3C:4D:5E', location: '潮玩区-A架-01', product: '赛博纪元系列盲盒 (基础款)', price: 59.00, battery: 85, status: 'ONLINE' },
  { mac: '00:1A:2B:3C:4D:5F', location: '潮玩区-A架-02', product: '赛博纪元系列盲盒 (典藏款)', price: 99.00, battery: 92, status: 'ONLINE' },
  { mac: '00:1B:2C:3D:4E:5A', location: '装备区-B架-05', product: 'VR电竞专业防滑手套', price: 129.00, battery: 15, status: 'ONLINE' }
])

let socket: any = null

onMounted(() => {
  socket = io('http://localhost:8080')
  socket.on('data_sync', (data: any) => {
    if (data.type === 'ESL_PRICE_UPDATED') {
      const tag = tags.value.find(t => t.product.includes(data.payload.productNameKeyword))
      if (tag) {
        tag.price = data.payload.newPrice
        ElMessage.success(`【物联网指令】价签 ${tag.mac} 墨水屏刷新完成，新价格: ¥${data.payload.newPrice}`)
      }
    }
  })
})

onUnmounted(() => {
  if (socket) socket.disconnect()
})

const simulatePriceChange = async () => {
  try {
    await fetch('http://localhost:8080/api/esl/update', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        productNameKeyword: '盲盒',
        newPrice: 39.00, // Discounted from 59
        reason: '节假日大促'
      })
    })
    ElMessage.info('已向 MQTT 网关下发改价指令，等待价签心跳回传...')
  } catch (e) {
    ElMessage.error('网关连接失败')
  }
}
</script>
