<template>
  <div class="order-manager">
    <div class="page-header">
      <h2>商城订单管理</h2>
      <div class="header-actions">
        <el-button type="success">导出订单</el-button>
      </div>
    </div>

    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filters">
        <el-form-item label="订单号">
          <el-input v-model="filters.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="用户手机号">
          <el-input v-model="filters.userPhone" placeholder="请输入用户手机号" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="待支付" value="PENDING_PAYMENT" />
            <el-option label="待发货" value="PENDING_SHIPMENT" />
            <el-option label="已发货" value="SHIPPED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchOrders">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 订单列表 -->
    <el-card class="data-card">
      <el-table :data="orderList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="订单号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="130" />
        <el-table-column label="商品信息" min-width="250">
          <template #default="{ row }">
            <div class="product-info" v-for="item in row.items" :key="item.productId">
              <el-image :src="item.imageUrl" class="product-img" />
              <div class="product-details">
                <div class="product-name">{{ item.productName }}</div>
                <div class="product-meta">¥{{ item.price }} x {{ item.quantity }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单总额" width="120">
          <template #default="{ row }">
            <span class="text-price">¥ {{ row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="下单时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.date).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewOrderDetails(row)">详情</el-button>
            <el-button 
              v-if="row.status === 'PENDING_SHIPMENT'" 
              link 
              type="success" 
              @click="handleShip(row)"
            >
              发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchOrders"
          @current-change="fetchOrders"
        />
      </div>
    </el-card>

    <!-- 发货弹窗 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="物流公司" required>
          <el-select v-model="shipForm.courierCompany" placeholder="请选择物流公司">
            <el-option label="顺丰速运" value="SF" />
            <el-option label="中通快递" value="ZTO" />
            <el-option label="圆通速递" value="YTO" />
            <el-option label="京东物流" value="JD" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" required>
          <el-input v-model="shipForm.trackingNumber" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="shipDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitShipment">确认发货</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';
import { io } from 'socket.io-client';

// Types
interface OrderItem {
  productId: number;
  productName: string;
  price: number;
  quantity: number;
  imageUrl: string;
}

interface Order {
  id: number;
  orderNo: string;
  userPhone: string;
  totalAmount: number;
  status: string;
  createdAt: string;
  items: OrderItem[];
}

// State
const loading = ref(false);
const orderList = ref<Order[]>([]);
const filters = reactive({
  orderNo: '',
  userPhone: '',
  status: '',
  dateRange: []
});
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
});

import { apiClient } from '@sportsant/utils';

// Mock Data
const MOCK_ORDERS: Order[] = [
  {
    id: 1,
    orderNo: 'ORD202603210001',
    userPhone: '13800138000',
    totalAmount: 599.00,
    status: 'PENDING_SHIPMENT',
    createdAt: '2026-03-21 10:30:00',
    items: [
      { productId: 101, productName: '赛博朋克发光夹克', price: 599.00, quantity: 1, imageUrl: 'http://localhost:8080/images/asset_d1a1323301151b15d3facde9e14f607b.jpg' }
    ]
  },
  {
    id: 2,
    orderNo: 'ORD202603210002',
    userPhone: '13900139000',
    totalAmount: 1299.00,
    status: 'COMPLETED',
    createdAt: '2026-03-20 15:45:00',
    items: [
      { productId: 102, productName: '初号机限量版滑板', price: 1299.00, quantity: 1, imageUrl: 'http://localhost:8080/images/asset_6873a963ac3ed4981146b83dc0caef39.jpg' }
    ]
  },
  {
    id: 3,
    orderNo: 'ORD202603210003',
    userPhone: '13700137000',
    totalAmount: 299.00,
    status: 'PENDING_PAYMENT',
    createdAt: '2026-03-21 11:20:00',
    items: [
      { productId: 301, productName: '杭州电竞中心全天通票', price: 299.00, quantity: 1, imageUrl: 'https://via.placeholder.com/100' }
    ]
  }
];

// Dialog State
const shipDialogVisible = ref(false);
const currentShipOrder = ref<Order | null>(null);
const shipForm = reactive({
  courierCompany: '',
  trackingNumber: ''
});

// Methods
const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await apiClient.get('/orders');
    let filtered = res.data;
    
    if (filters.orderNo) {
      filtered = filtered.filter((o: any) => o.id.includes(filters.orderNo));
    }
    if (filters.status) {
      filtered = filtered.filter((o: any) => o.status === filters.status);
    }
    
    orderList.value = filtered;
    pagination.total = filtered.length;
  } catch (err) {
    // Fallback to local
    let filtered = [...MOCK_ORDERS];
    if (filters.orderNo) {
      filtered = filtered.filter(o => o.orderNo.includes(filters.orderNo));
    }
    orderList.value = filtered;
    pagination.total = filtered.length;
  } finally {
    loading.value = false;
  }
};

const resetFilters = () => {
  filters.orderNo = '';
  filters.userPhone = '';
  filters.status = '';
  filters.dateRange = [];
  fetchOrders();
};

const getStatusName = (status: string) => {
  const map: Record<string, string> = {
    'PENDING_PAYMENT': '待支付',
    'PENDING_SHIPMENT': '待发货',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  };
  return map[status] || status;
};

const getStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    'PENDING_PAYMENT': 'warning',
    'PENDING_SHIPMENT': 'primary',
    'SHIPPED': 'info',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  };
  return map[status] || 'info';
};

const viewOrderDetails = (row: Order) => {
  ElMessage.info(`查看订单详情：${row.orderNo} (功能开发中)`);
};

const handleShip = (row: Order) => {
  currentShipOrder.value = row;
  shipForm.courierCompany = '';
  shipForm.trackingNumber = '';
  shipDialogVisible.value = true;
};

const submitShipment = async () => {
  if (!shipForm.courierCompany || !shipForm.trackingNumber) {
    ElMessage.warning('请填写完整的物流信息');
    return;
  }
  
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 500));
    if (currentShipOrder.value) {
      currentShipOrder.value.status = 'SHIPPED';
      
      // Notify Player via WebSocket
      if (socket) {
        socket.emit('data_sync', {
          type: 'ORDER_SHIPPED',
          payload: {
            orderId: currentShipOrder.value.id,
            trackingNumber: shipForm.trackingNumber,
            userId: currentShipOrder.value.userId
          }
        });
      }
    }
    ElMessage.success('发货成功');
    shipDialogVisible.value = false;
  } catch (e) {
    ElMessage.error('发货失败');
  }
};

let socket: any = null;

onMounted(() => {
  fetchOrders();

  socket = io('http://localhost:8080');
  socket.on('data_sync', (data: any) => {
    if (data.type === 'NEW_MALL_ORDER') {
      ElMessage.success(`收到新订单: ${data.payload.productName}`);
      orderList.value.unshift({
        id: data.payload.id,
        orderNo: data.payload.id,
        userId: data.payload.userId,
        userPhone: '13800000000',
        totalAmount: data.payload.price,
        status: data.payload.status,
        createdAt: data.payload.createdAt,
        items: [
          { 
            productId: data.payload.productId || 999, 
            productName: data.payload.productName, 
            price: data.payload.price, 
            quantity: 1, 
            imageUrl: 'http://localhost:8080/images/asset_d1a1323301151b15d3facde9e14f607b.jpg' 
          }
        ]
      });
    }
  });
});

onUnmounted(() => {
  if (socket) socket.disconnect();
});
</script>

<style scoped>
.order-manager {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.filter-card {
  margin-bottom: 20px;
}
.product-info {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.product-info:last-child {
  margin-bottom: 0;
}
.product-img {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  margin-right: 10px;
  flex-shrink: 0;
}
.product-details {
  display: flex;
  flex-direction: column;
}
.product-name {
  font-size: 13px;
  color: #333;
  margin-bottom: 4px;
}
.product-meta {
  font-size: 12px;
  color: #999;
}
.text-price {
  color: #f56c6c;
  font-weight: bold;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
