<template>
  <div class="product-manager">
    <div class="page-header">
      <h2>商品与库存管理</h2>
      <div class="flex gap-2">
        <el-button type="warning" @click="triggerDynamicPricing">
          <el-icon><DataLine /></el-icon> AI 动态调价 (沙盘应用)
        </el-button>
        <el-button type="primary" @click="handleAddProduct">
          <el-icon><Plus /></el-icon> 新增商品
        </el-button>
      </div>
    </div>

    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filters">
        <el-form-item label="商品名称">
          <el-input v-model="filters.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="商品类型">
          <el-select v-model="filters.type" placeholder="全部类型" clearable>
            <el-option label="实物周边" value="PHYSICAL" />
            <el-option label="数字藏品(NFT)" value="NFT" />
            <el-option label="盲盒资产" value="BLINDBOX_ITEM" />
            <el-option label="场馆门票" value="TICKET" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="上架中" value="ACTIVE" />
            <el-option label="已下架" value="INACTIVE" />
            <el-option label="已售罄" value="SOLD_OUT" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchProducts">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="data-card">
      <el-table :data="productList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image 
              :src="row.imageUrl || 'https://via.placeholder.com/60x60'" 
              class="product-img"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="售价" width="120">
          <template #default="{ row }">
            ¥ {{ row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100">
          <template #default="{ row }">
            <span :class="{'text-danger': row.stock <= 10}">{{ row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="ACTIVE"
              inactive-value="INACTIVE"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEditProduct(row)">编辑</el-button>
            <el-button link type="primary" @click="handleManageStock(row)">补库存</el-button>
            <el-button link type="danger" @click="handleDeleteProduct(row)">删除</el-button>
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
          @size-change="fetchProducts"
          @current-change="fetchProducts"
        />
      </div>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingProduct ? '编辑商品' : '新增商品'"
      width="600px"
    >
      <el-form :model="productForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" />
        </el-form-item>
        <el-form-item label="商品类型" prop="type">
          <el-select v-model="productForm.type" style="width: 100%">
            <el-option label="实物周边" value="PHYSICAL" />
            <el-option label="数字藏品(NFT)" value="NFT" />
            <el-option label="盲盒资产" value="BLINDBOX_ITEM" />
            <el-option label="场馆门票" value="TICKET" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input type="textarea" v-model="productForm.description" :rows="3" />
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="productForm.price" :precision="2" :step="1" :min="0" />
        </el-form-item>
        <el-form-item label="初始库存" prop="stock" v-if="!editingProduct">
          <el-input-number v-model="productForm.stock" :step="1" :min="0" />
        </el-form-item>
        <el-form-item label="商品主图" prop="imageUrl">
          <el-input v-model="productForm.imageUrl" placeholder="输入图片URL或上传（暂用URL）" />
          <el-image 
            v-if="productForm.imageUrl" 
            :src="productForm.imageUrl" 
            class="preview-img mt-2"
          />
        </el-form-item>
        
        <!-- 门票专属配置 -->
        <template v-if="productForm.type === 'TICKET'">
          <el-divider>门票配置</el-divider>
          <el-form-item label="适用场馆">
            <el-select v-model="productForm.venueId" placeholder="选择适用场馆">
              <el-option label="杭州电竞中心" value="VENUE_1" />
              <el-option label="上海超级旗舰店" value="VENUE_2" />
            </el-select>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProduct">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, DataLine } from '@element-plus/icons-vue';

// Types
interface Product {
  id: number;
  name: string;
  type: string;
  price: number;
  stock: number;
  status: string;
  imageUrl?: string;
  description?: string;
  venueId?: string;
}

// State
const loading = ref(false);
const productList = ref<Product[]>([]);
const filters = reactive({
  name: '',
  type: '',
  status: ''
});
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
});

// Use real mock server data if available
import { apiClient } from '@sportsant/utils';
import { io } from 'socket.io-client';

const MOCK_PRODUCTS: Product[] = [
  { id: 101, name: '赛博朋克发光夹克', type: 'PHYSICAL', price: 599.00, stock: 45, status: 'ACTIVE', imageUrl: 'http://localhost:8080/images/asset_ec56175d8fd630c09146c5da25f7d82d.jpg' },
  { id: 102, name: '初号机限量版滑板', type: 'PHYSICAL', price: 1299.00, stock: 5, status: 'ACTIVE', imageUrl: 'http://localhost:8080/images/asset_ad83633098d1fed8da797c24c6fa2ce2.jpg' },
  { id: 201, name: '【创始纪】数字徽章', type: 'NFT', price: 99.00, stock: 999, status: 'ACTIVE', imageUrl: 'http://localhost:8080/images/asset_ae9d8876d1bade52328cdd9f9abd0c7d.jpg' },
  { id: 301, name: '杭州电竞中心全天通票', type: 'TICKET', price: 299.00, stock: 200, status: 'ACTIVE', venueId: 'VENUE_1' },
];

// Dialog State
const dialogVisible = ref(false);
const editingProduct = ref<Product | null>(null);
const formRef = ref();
const productForm = reactive({
  name: '',
  type: 'PHYSICAL',
  description: '',
  price: 0,
  stock: 0,
  imageUrl: '',
  venueId: ''
});

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择商品类型', trigger: 'change' }],
  price: [{ required: true, message: '请设置售价', trigger: 'blur' }]
};

// Methods
const fetchProducts = async () => {
  loading.value = true;
  try {
    const response = await apiClient.get('/products');
    let data = response.data;
    
    // Apply local filters since mock server might not handle queries yet
    if (filters.name) {
      data = data.filter((p: any) => p.name.includes(filters.name));
    }
    if (filters.type) {
      data = data.filter((p: any) => p.type === filters.type);
    }
    if (filters.status) {
      data = data.filter((p: any) => p.status === filters.status);
    }
    
    productList.value = data;
    pagination.total = data.length;
  } catch (err) {
    console.error('Failed to fetch products from backend', err);
    // Fallback to local mock if server is down
    setTimeout(() => {
      let result = [...MOCK_PRODUCTS];
      if (filters.name) result = result.filter(p => p.name.includes(filters.name));
      if (filters.type) result = result.filter(p => p.type === filters.type);
      if (filters.status) result = result.filter(p => p.status === filters.status);
      
      productList.value = result;
      pagination.total = result.length;
    }, 500);
  } finally {
    loading.value = false;
  }
};

const resetFilters = () => {
  filters.name = '';
  filters.type = '';
  filters.status = '';
  fetchProducts();
};

const getTypeName = (type: string) => {
  const map: Record<string, string> = {
    'PHYSICAL': '实物周边',
    'NFT': '数字藏品',
    'BLINDBOX_ITEM': '盲盒资产',
    'TICKET': '门票'
  };
  return map[type] || type;
};

const getTypeTagType = (type: string) => {
  const map: Record<string, string> = {
    'PHYSICAL': 'success',
    'NFT': 'warning',
    'BLINDBOX_ITEM': 'danger',
    'TICKET': 'info'
  };
  return map[type] || 'info';
};

const handleStatusChange = async (row: Product, val: string | number | boolean) => {
  try {
    ElMessage.success(`已${val === 'ACTIVE' ? '上架' : '下架'}商品：${row.name}`);
    
    // Simulate smart pricing / dynamic pricing update trigger
    await fetch('http://localhost:8080/api/mall/products/status', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id: row.id, name: row.name, status: val })
    });
  } catch (e) {
    row.status = val === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'; // Revert
    ElMessage.error('操作失败');
  }
};

const triggerDynamicPricing = async () => {
  ElMessageBox.confirm('是否根据当前 AI 预测模型（客流激增预测），启动动态调价？系统将自动上调门票和热门商品价格 15%。', 'AI 动态定价模型 (Yield Management)', {
    confirmButtonText: '启动动态调价',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await fetch('http://localhost:8080/api/ai/pricing/dynamic', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ strategy: 'SURGE_15_PERCENT' })
      });
      ElMessage.success('已启动 AI 动态调价，各端商城价格正在同步...');
      // Update local view
      productList.value.forEach(p => {
        if (p.type === 'TICKET' || p.type === 'PHYSICAL') {
          p.price = Number((p.price * 1.15).toFixed(2));
        }
      });
    } catch(e) {
      ElMessage.error('触发失败');
    }
  }).catch(() => {});
};

const handleAddProduct = () => {
  editingProduct.value = null;
  Object.assign(productForm, {
    name: '',
    type: 'PHYSICAL',
    description: '',
    price: 0,
    stock: 0,
    imageUrl: '',
    venueId: ''
  });
  dialogVisible.value = true;
};

const handleEditProduct = (row: Product) => {
  editingProduct.value = row;
  Object.assign(productForm, {
    ...row
  });
  dialogVisible.value = true;
};

const handleManageStock = (row: Product) => {
  ElMessageBox.prompt('请输入增加的库存数量', '补充库存', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^[1-9]\d*$/,
    inputErrorMessage: '请输入大于0的正整数'
  }).then(({ value }) => {
    row.stock += parseInt(value);
    ElMessage.success(`已为 ${row.name} 补充 ${value} 件库存`);
  }).catch(() => {});
};

const handleDeleteProduct = (row: Product) => {
  ElMessageBox.confirm(`确定要删除商品 "${row.name}" 吗？此操作不可恢复。`, '警告', {
    type: 'warning'
  }).then(async () => {
    // Simulate delete
    const index = productList.value.findIndex(p => p.id === row.id);
    if (index > -1) {
      productList.value.splice(index, 1);
      ElMessage.success('删除成功');
    }
  }).catch(() => {});
};

const saveProduct = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true;
      try {
        if (editingProduct.value) {
          // Mock update API call (you'd need a PUT endpoint in mock-server)
          const index = MOCK_PRODUCTS.findIndex(p => p.id === editingProduct.value!.id);
          if (index > -1) {
            MOCK_PRODUCTS[index] = { ...editingProduct.value, ...productForm } as Product;
          }
        } else {
          // Push to backend
          await apiClient.post('/products', productForm);
        }
        ElMessage.success(editingProduct.value ? '修改成功' : '新增成功');
        dialogVisible.value = false;
        fetchProducts();
      } catch (err) {
        ElMessage.error('操作失败');
      } finally {
        loading.value = false;
      }
    }
  });
};

onMounted(() => {
  fetchProducts();
});
</script>

<style scoped>
.product-manager {
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
.product-img {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}
.preview-img {
  width: 100px;
  height: 100px;
  border-radius: 4px;
}
.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
