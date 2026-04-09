<template>
  <div class="emergency-console p-6 h-full flex flex-col bg-gray-50">
    <div class="header-actions mb-6 flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-bold flex items-center gap-2 text-red-600">
          <el-icon><WarnTriangleFilled /></el-icon> 应急响应控制台
        </h2>
        <p class="text-gray-500 text-sm mt-1">处理突发事件、一键报警及启动应急预案</p>
      </div>
      <el-tag v-if="activeIncidents.length > 0" type="danger" effect="dark" size="large" class="animate-pulse">
        {{ activeIncidents.length }} 起未处理的突发事件
      </el-tag>
    </div>

    <el-alert
      v-if="routeIncidentId"
      type="error"
      :closable="false"
      show-icon
      class="mb-4"
      title="5171 安全页已带入事件上下文"
      :description="incidentSummary"
    />

    <!-- Quick Actions (一键报警) -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
      <el-button type="danger" class="emergency-btn shadow-md hover:shadow-lg transition-all border-none bg-gradient-to-br from-red-500 to-red-600" @click="report('FIRE')">
        <el-icon class="text-4xl mb-2"><WarnTriangleFilled /></el-icon>
        <span class="text-lg font-bold tracking-widest">火警/烟雾</span>
      </el-button>
      <el-button type="warning" class="emergency-btn shadow-md hover:shadow-lg transition-all border-none bg-gradient-to-br from-orange-400 to-orange-500" @click="report('MEDICAL')">
        <el-icon class="text-4xl mb-2"><FirstAidKit /></el-icon>
        <span class="text-lg font-bold tracking-widest">医疗/受伤</span>
      </el-button>
      <el-button type="info" class="emergency-btn shadow-md hover:shadow-lg transition-all border-none bg-gradient-to-br from-gray-600 to-gray-700" @click="report('FIGHT')">
        <el-icon class="text-4xl mb-2"><UserFilled /></el-icon>
        <span class="text-lg font-bold tracking-widest">治安/冲突</span>
      </el-button>
      <el-button type="primary" class="emergency-btn shadow-md hover:shadow-lg transition-all border-none bg-gradient-to-br from-blue-500 to-blue-600" @click="report('OTHER')">
        <el-icon class="text-4xl mb-2"><QuestionFilled /></el-icon>
        <span class="text-lg font-bold tracking-widest">其他突发</span>
      </el-button>
    </div>

    <!-- Active Incidents -->
    <el-card class="flex-1 border-none shadow-sm flex flex-col overflow-hidden" body-style="padding: 0; display: flex; flex-direction: column; height: 100%;">
      <div class="p-4 border-b border-gray-100 flex justify-between items-center bg-white">
        <h3 class="font-bold text-gray-800 text-lg">当前应急事件列表</h3>
        <el-button plain @click="fetchIncidents">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>
      
      <div v-if="activeIncidents.length === 0" class="flex-1 flex flex-col items-center justify-center text-gray-400 p-8">
        <el-icon class="text-6xl mb-4 text-green-200"><CircleCheckFilled /></el-icon>
        <p>当前无突发事件，场馆运营平稳。</p>
      </div>

      <el-table v-else :data="activeIncidents" stripe style="width: 100%; flex: 1;">
        <el-table-column prop="type" label="事件类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getIncidentTagType(row.type)" effect="dark">{{ getIncidentName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="严重级别" width="100">
          <template #default="{ row }">
            <span :class="row.level === 'CRITICAL' ? 'text-red-500 font-bold' : 'text-orange-500 font-bold'">{{ row.level === 'CRITICAL' ? '极高' : '高' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="现场描述" min-width="200" />
        <el-table-column prop="reporter" label="上报人" width="120" />
        <el-table-column prop="reportedAt" label="发生时间" width="160">
          <template #default="{ row }">{{ formatDate(row.reportedAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="resolve(row)">解除警报</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Report Dialog -->
    <el-dialog v-model="showReportDialog" title="上报突发事件" width="500px" :close-on-click-modal="false">
      <el-form :model="reportForm" label-width="80px">
        <el-form-item label="事件类型">
          <el-tag :type="getIncidentTagType(reportForm.type)" effect="dark" size="large">{{ getIncidentName(reportForm.type) }}</el-tag>
        </el-form-item>
        <el-form-item label="严重级别">
          <el-radio-group v-model="reportForm.level">
            <el-radio label="HIGH">高 (局部影响)</el-radio>
            <el-radio label="CRITICAL">极高 (需全店疏散)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="现场描述" required>
          <el-input type="textarea" v-model="reportForm.description" placeholder="请简述发生地点、受伤人数、火势大小等关键信息..." :rows="3" />
        </el-form-item>
        
        <div class="mt-4 p-4 bg-red-50 border border-red-100 rounded-lg text-red-600" v-if="currentPlan">
          <h4 class="font-bold mb-2 flex items-center gap-1"><el-icon><WarnTriangleFilled /></el-icon> 标准处置预案 (SOP):</h4>
          <div class="text-sm leading-relaxed whitespace-pre-wrap">{{ currentPlan.procedureSteps }}</div>
        </div>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="showReportDialog = false">取消</el-button>
          <el-button type="danger" @click="submitReport" class="w-32">确认上报</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';
import { io } from 'socket.io-client';
import { useRoute } from 'vue-router';
import { 
  WarnTriangleFilled, FirstAidKit, UserFilled, QuestionFilled, 
  Refresh, CircleCheckFilled 
} from '@element-plus/icons-vue';

// State
const activeIncidents = ref<any[]>([]);
const showReportDialog = ref(false);
const reportForm = ref({ type: '', description: '', level: 'HIGH' });
const currentPlan = ref<any>(null);
const route = useRoute();
const routeIncidentId = computed(() => String(route.query.incidentId || '').trim());
const incidentSummary = computed(() => {
  const incident = activeIncidents.value.find(item => String(item.id) === routeIncidentId.value);
  return incident?.description || '当前事件已从 5171 安全页同步到应急控制台。';
});

// Mock Data for MVP
const MOCK_INCIDENTS: any[] = [
  // { id: 1, type: 'MEDICAL', level: 'HIGH', description: 'B区玩家在VR体验中摔倒，疑似脚踝扭伤', reporter: '前台小李', reportedAt: new Date().toISOString() }
];

const SOP_PLANS: Record<string, any> = {
  'FIRE': { title: '火警应急预案', procedureSteps: '1. 立即按下最近的消防报警按钮\n2. 使用对讲机通知全店员工组织疏散\n3. 拨打119并报告准确位置\n4. 在确保安全的情况下切断总电源\n5. 引导顾客从安全通道撤离，切勿使用电梯' },
  'MEDICAL': { title: '医疗急救预案', procedureSteps: '1. 立即停止相关设备运行\n2. 疏散围观人群，保持空气流通\n3. 呼叫持有急救证书的员工到场\n4. 视情况拨打120\n5. 取出前台急救箱进行初步包扎/处理' },
  'FIGHT': { title: '治安冲突预案', procedureSteps: '1. 安保人员立即介入隔开冲突双方\n2. 保持冷静，避免肢体接触\n3. 保护其他顾客安全，疏散至安全区\n4. 视情况拨打110\n5. 保存现场监控录像' },
  'OTHER': { title: '通用突发预案', procedureSteps: '1. 立即上报店长\n2. 保护现场\n3. 安抚受影响顾客的情绪\n4. 记录事件经过' }
};

const fetchIncidents = async () => {
  try {
    const res = await fetch('http://localhost:8080/api/safety/incidents');
    const data = await res.json();
    const incidents = data?.data?.incidents || [];
    activeIncidents.value = [...incidents, ...MOCK_INCIDENTS];
  } catch {
    activeIncidents.value = [...MOCK_INCIDENTS];
  }
};

const report = (type: string) => {
  reportForm.value = { type, description: '', level: type === 'FIRE' ? 'CRITICAL' : 'HIGH' };
  currentPlan.value = SOP_PLANS[type] || SOP_PLANS['OTHER'];
  showReportDialog.value = true;
};

const submitReport = () => {
  if (!reportForm.value.description) {
    ElMessage.warning('请填写现场描述');
    return;
  }
  
  MOCK_INCIDENTS.unshift({
    id: Date.now(),
    type: reportForm.value.type,
    level: reportForm.value.level,
    description: reportForm.value.description,
    reporter: '当前用户 (店长)',
    reportedAt: new Date().toISOString()
  });
  
  ElMessage.error({ message: '突发事件已上报！请立即执行应急预案！', duration: 5000 });
  showReportDialog.value = false;
  fetchIncidents();
};

const resolve = (incident: any) => {
  ElMessageBox.confirm(`确认该突发事件（${getIncidentName(incident.type)}）已得到妥善解决？`, '解除警报', {
    type: 'success',
    confirmButtonText: '确认解除',
    cancelButtonText: '取消'
  }).then(() => {
    const index = MOCK_INCIDENTS.findIndex(i => i.id === incident.id);
    if (index > -1) MOCK_INCIDENTS.splice(index, 1);
    ElMessage.success('警报已解除');
    fetchIncidents();
  }).catch(() => {});
};

// Utils
const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm:ss');

const getIncidentName = (type: string) => {
  const map: Record<string, string> = { FIRE: '火警/烟雾', MEDICAL: '医疗/受伤', FIGHT: '治安/冲突', OTHER: '其他突发' };
  return map[type] || type;
};

const getIncidentTagType = (type: string) => {
  const map: Record<string, string> = { FIRE: 'danger', MEDICAL: 'warning', FIGHT: 'info', OTHER: 'primary' };
  return map[type] || 'info';
};

let socket: any = null;

onMounted(() => {
  fetchIncidents();

  socket = io('http://localhost:8080');
  socket.on('data_sync', (data: any) => {
    if (data.type === 'EMERGENCY_TRIGGERED') {
      ElMessage.error({
        message: `🚨【紧急警报】门店 ${data.payload.storeId} 触发了 ${getIncidentName(data.payload.type)} 警报！请立即处理！`,
        duration: 0, // Stay until manually closed
        showClose: true
      });
      
      MOCK_INCIDENTS.unshift({
        id: Date.now(),
        type: data.payload.type,
        level: data.payload.type === 'FIRE' ? 'CRITICAL' : 'HIGH',
        description: `来自员工 ${data.payload.reporter} 的紧急上报`,
        reporter: data.payload.reporter,
        reportedAt: data.payload.timestamp
      });
      fetchIncidents();
    }
  });
});

import { onUnmounted } from 'vue';
onUnmounted(() => {
  if (socket) socket.disconnect();
});
</script>

<style scoped>
.emergency-btn {
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 12px;
  color: white;
}
.emergency-btn :deep(span) {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
