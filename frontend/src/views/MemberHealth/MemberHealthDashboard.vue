<template>
  <div class="member-health-dashboard">
    <!-- 头部 -->
    <div class="dashboard-header">
      <h1>AI智能会员健康评分系统</h1>
      <div class="header-actions">
        <el-button type="primary" @click="runBatchScoring">
          <el-icon><TrendCharts /></el-icon>
          运行AI健康评分
        </el-button>
        <el-button @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button type="success" @click="exportHealthReport">
          <el-icon><Download /></el-icon>
          导出健康报告
        </el-button>
      </div>
    </div>

    <el-alert
      v-if="routeUserId"
      type="success"
      :closable="false"
      show-icon
      class="mb-4"
      title="5171 健康页已带入用户上下文"
      :description="`当前按用户 ${routeUserId} 展示健康评分与干预建议。`"
    />

    <!-- 健康统计卡片 -->
    <div class="health-stats-grid">
      <el-card class="health-stat-card" shadow="hover">
        <template #header>
          <div class="stat-header">
            <el-icon><User /></el-icon>
            <span>监测会员</span>
          </div>
        </template>
        <div class="stat-content">
          <div class="stat-value">{{ healthStats.totalMembers }}</div>
          <div class="stat-label">总会员数</div>
        </div>
      </el-card>

      <el-card class="health-stat-card excellent" shadow="hover">
        <template #header>
          <div class="stat-header">
            <el-icon><CircleCheckFilled /></el-icon>
            <span>平均健康分</span>
          </div>
        </template>
        <div class="stat-content">
          <div class="stat-value">{{ healthStats.avgOverallScore }}</div>
          <div class="stat-label">综合评分</div>
        </div>
      </el-card>

      <el-card class="health-stat-card warning" shadow="hover">
        <template #header>
          <div class="stat-header">
            <el-icon><Warning /></el-icon>
            <span>高风险会员</span>
          </div>
        </template>
        <div class="stat-content">
          <div class="stat-value">{{ healthStats.highRiskCount }}</div>
          <div class="stat-label">需重点关注</div>
        </div>
      </el-card>

      <el-card class="health-stat-card" shadow="hover">
        <template #header>
          <div class="stat-header">
            <el-icon><TrendCharts /></el-icon>
            <span>健康趋势</span>
          </div>
        </template>
        <div class="stat-content">
          <div class="stat-value">{{ getImprovingCount() }}</div>
          <div class="stat-label">会员在改善</div>
        </div>
      </el-card>
    </div>

    <!-- 健康等级分布 -->
    <el-card class="health-distribution-card">
      <template #header>
        <h3>会员健康等级分布</h3>
      </template>
      <div class="distribution-content">
        <div class="distribution-chart">
          <div v-for="(count, level) in healthStats.levelDistribution" :key="level" class="distribution-item">
            <div class="distribution-info">
              <div class="distribution-label">
                <el-tag :type="getHealthLevelTagType(level)" size="large">
                  {{ getHealthLevelText(level) }}
                </el-tag>
              </div>
              <div class="distribution-count">{{ count }}人</div>
              <div class="distribution-percentage">
                {{ calculatePercentage(count, healthStats.totalMembers) }}%
              </div>
            </div>
            <el-progress 
              :percentage="calculatePercentage(count, healthStats.totalMembers)" 
              :stroke-width="20"
              :color="getHealthLevelColor(level)"
            />
          </div>
        </div>
      </div>
    </el-card>

    <!-- 高风险会员列表 -->
    <el-card class="high-risk-members-card" v-if="highRiskMembers.length > 0">
      <template #header>
        <div class="card-header urgent-header">
          <h3>🚨 高风险会员（需立即干预）</h3>
          <el-button type="danger" size="small" @click="batchInterveneHighRisk">
            批量干预
          </el-button>
        </div>
      </template>
      
      <el-table :data="highRiskMembers" style="width: 100%">
        <el-table-column prop="memberName" label="会员姓名" width="120" />
        <el-table-column label="健康等级" width="100">
          <template #default="scope">
            <el-tag :type="getHealthLevelTagType(scope.row.healthLevel)" size="small">
              {{ getHealthLevelText(scope.row.healthLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="综合评分" width="120">
          <template #default="scope">
            <div class="score-display">
              <el-progress 
                :percentage="scope.row.overallScore" 
                :status="getScoreStatus(scope.row.overallScore)"
                :stroke-width="12"
                :show-text="false"
              />
              <span class="score-value">{{ scope.row.overallScore }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="维度评分" width="300">
          <template #default="scope">
            <div class="dimension-scores">
              <div class="dimension-item">
                <span class="dimension-label">活跃:</span>
                <el-progress 
                  :percentage="scope.row.activityScore" 
                  :stroke-width="6"
                  :show-text="false"
                  color="#67c23a"
                />
                <span class="dimension-value">{{ scope.row.activityScore }}</span>
              </div>
              <div class="dimension-item">
                <span class="dimension-label">消费:</span>
                <el-progress 
                  :percentage="scope.row.consumptionScore" 
                  :stroke-width="6"
                  :show-text="false"
                  color="#e6a23c"
                />
                <span class="dimension-value">{{ scope.row.consumptionScore }}</span>
              </div>
              <div class="dimension-item">
                <span class="dimension-label">忠诚:</span>
                <el-progress 
                  :percentage="scope.row.loyaltyScore" 
                  :stroke-width="6"
                  :show-text="false"
                  color="#409eff"
                />
                <span class="dimension-value">{{ scope.row.loyaltyScore }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="风险因素" width="200">
          <template #default="scope">
            <div class="risk-factors">
              <el-tag v-for="factor in getRiskFactors(scope.row.riskFactors)" 
                     :key="factor" type="danger" size="small" class="risk-tag">
                {{ factor }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="流失概率" width="120">
          <template #default="scope">
            <div class="churn-probability">
              <el-progress 
                :percentage="Math.round(scope.row.churnProbability * 100)" 
                status="exception"
                :stroke-width="10"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewMemberHealthDetail(scope.row)">
              健康详情
            </el-button>
            <el-button type="primary" size="small" @click="executeIntervention(scope.row)">
              执行干预
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 会员健康评分列表 -->
    <el-card class="health-scores-card">
      <template #header>
        <div class="card-header">
          <h3>会员健康评分列表</h3>
          <div class="header-filters">
            <el-select v-model="filterHealthLevel" placeholder="健康等级" style="width: 120px">
              <el-option label="全部" value="" />
              <el-option label="优秀" value="EXCELLENT" />
              <el-option label="良好" value="GOOD" />
              <el-option label="一般" value="FAIR" />
              <el-option label="较差" value="POOR" />
              <el-option label="危险" value="CRITICAL" />
            </el-select>
            <el-select v-model="filterTrend" placeholder="趋势" style="width: 120px">
              <el-option label="全部" value="" />
              <el-option label="改善中" value="IMPROVING" />
              <el-option label="稳定" value="STABLE" />
              <el-option label="下降" value="DECLINING" />
              <el-option label="危险下降" value="CRITICAL_DECLINE" />
            </el-select>
            <el-input v-model="searchMemberName" placeholder="搜索会员" style="width: 200px" clearable>
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>
      
      <el-table :data="filteredHealthScores" v-loading="tableLoading" style="width: 100%">
        <el-table-column prop="memberName" label="会员姓名" width="120" fixed />
        <el-table-column label="健康等级" width="100">
          <template #default="scope">
            <el-tag :type="getHealthLevelTagType(scope.row.healthLevel)" size="small">
              {{ getHealthLevelText(scope.row.healthLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="综合评分" width="120">
          <template #default="scope">
            <div class="overall-score">
              <div class="score-circle" :style="getScoreCircleStyle(scope.row.overallScore)">
                {{ scope.row.overallScore }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="维度评分" width="250">
          <template #default="scope">
            <div class="dimension-radar">
              <div class="radar-item">
                <span class="radar-label">活</span>
                <div class="radar-bar">
                  <div class="radar-fill" :style="{ height: scope.row.activityScore + '%' }"></div>
                </div>
              </div>
              <div class="radar-item">
                <span class="radar-label">消</span>
                <div class="radar-bar">
                  <div class="radar-fill" :style="{ height: scope.row.consumptionScore + '%' }"></div>
                </div>
              </div>
              <div class="radar-item">
                <span class="radar-label">忠</span>
                <div class="radar-bar">
                  <div class="radar-fill" :style="{ height: scope.row.loyaltyScore + '%' }"></div>
                </div>
              </div>
              <div class="radar-item">
                <span class="radar-label">险</span>
                <div class="radar-bar">
                  <div class="radar-fill" :style="{ height: scope.row.riskScore + '%' }"></div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="趋势" width="100">
          <template #default="scope">
            <div class="trend-indicator">
              <el-icon v-if="scope.row.trend === 'IMPROVING'" color="#67c23a">
                <Top />
              </el-icon>
              <el-icon v-else-if="scope.row.trend === 'DECLINING'" color="#f56c6c">
                <Bottom />
              </el-icon>
              <el-icon v-else-if="scope.row.trend === 'CRITICAL_DECLINE'" color="#f56c6c">
                <Warning />
              </el-icon>
              <el-icon v-else color="#909399">
                <Right />
              </el-icon>
              <span :class="getTrendClass(scope.row.trend)">
                {{ getTrendText(scope.row.trend) }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="会员天数" width="100">
          <template #default="scope">
            {{ scope.row.memberDays }}天
          </template>
        </el-table-column>
        <el-table-column label="上次访问" width="120">
          <template #default="scope">
            <span :class="{ 'inactive-member': scope.row.daysSinceLastVisit > 14 }">
              {{ scope.row.daysSinceLastVisit }}天前
            </span>
          </template>
        </el-table-column>
        <el-table-column label="流失概率" width="100">
          <template #default="scope">
            <el-tag :type="getChurnTagType(scope.row.churnProbability)" size="small">
              {{ Math.round(scope.row.churnProbability * 100) }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewHealthDetail(scope.row)">
              分析
            </el-button>
            <el-button 
              v-if="scope.row.healthLevel === 'POOR' || scope.row.healthLevel === 'CRITICAL'"
              type="primary" 
              size="small" 
              @click="executeIntervention(scope.row)"
            >
              干预
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="table-footer">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredHealthScores.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- AI健康评分算法说明 -->
    <el-card class="ai-algorithm-card">
      <template #header>
        <h3>🧠 AI健康评分算法说明</h3>
      </template>
      <div class="algorithm-content">
        <div class="algorithm-weights">
          <h4>评分权重配置：</h4>
          <div class="weight-grid">
            <div class="weight-item">
              <div class="weight-label">活跃度评分</div>
              <div class="weight-value">40%</div>
              <div class="weight-desc">访问频率、互动次数</div>
            </div>
            <div class="weight-item">
              <div class="weight-label">消费力评分</div>
              <div class="weight-value">30%</div>
              <div class="weight-desc">消费金额、频率、增长</div>
            </div>
            <div class="weight-item">
              <div class="weight-label">忠诚度评分</div>
              <div class="weight-value">20%</div>
              <div class="weight-desc">会员时长、等级、积分</div>
            </div>
            <div class="weight-item">
              <div class="weight-label">风险度评分</div>
              <div class="weight-value">10%</div>
              <div class="weight-desc">流失风险、异常行为</div>
            </div>
          </div>
        </div>
        
        <div class="algorithm-details">
          <h4>健康等级标准：</h4>
          <ul>
            <li><strong>优秀 (85-100分)</strong>: 高活跃、高消费、高忠诚、低风险</li>
            <li><strong>良好 (70-84分)</strong>: 良好活跃和消费，忠诚度较高</li>
            <li><strong>一般 (50-69分)</strong>: 基本活跃，消费一般，需关注</li>
            <li><strong>较差 (30-49分)</strong>: 低活跃或低消费，有流失风险</li>
            <li><strong>危险 (0-29分)</strong>: 高风险流失，需立即干预</li>
          </ul>
          
          <h4>更新频率：</h4>
          <p>每日凌晨2点自动更新所有会员健康评分，有效期24小时。</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import {
  TrendCharts,
  Refresh,
  Download,
  User,
  CircleCheckFilled,
  Warning,
  Search,
  Top,
  Bottom,
  Right
} from '@element-plus/icons-vue'
import { useMemberHealthStore } from '@/stores/memberHealth.store'

// 状态
const healthStats = ref({
  totalMembers: 0,
  avgOverallScore: '0',
  avgActivityScore: '0',
  avgConsumptionScore: '0',
  levelDistribution: {},
  trendDistribution: {},
  highRiskCount: 0,
  lastUpdated: ''
})

const healthScores = ref([])
const highRiskMembers = ref([])
const tableLoading = ref(false)

const filterHealthLevel = ref('')
const filterTrend = ref('')
const searchMemberName = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const route = useRoute()
const routeUserId = computed(() => String(route.query.userId || '').trim())

// Store
const memberHealthStore = useMemberHealthStore()

// 生命周期
onMounted(() => {
  loadHealthStats()
  loadHealthScores()
  loadHighRiskMembers()
})

// 计算属性
const filteredHealthScores = computed(() => {
  let filtered = healthScores.value
  
  // 按健康等级筛选
  if (filterHealthLevel.value) {
    filtered = filtered.filter(s => s.healthLevel === filterHealthLevel.value)
  }
  
  // 按趋势筛选
  if (filterTrend.value) {
    filtered = filtered.filter(s => s.trend === filterTrend.value)
  }
  
  // 按会员姓名搜索
  if (searchMemberName.value) {
    const keyword = searchMemberName.value.toLowerCase()
    filtered = filtered.filter(s => 
      s.memberName.toLowerCase().includes(keyword)
    )
  }
  
  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

// 方法
const loadHealthStats = async () => {
  try {
    const data = await memberHealthStore.getHealthDashboard()
    healthStats.value = data
  } catch (error) {
    ElMessage.error('加载健康统计数据失败')
  }
}

const loadHealthScores = async () => {
  tableLoading.value = true
  try {
    const data = await memberHealthStore.getHealthScores()
    healthScores.value = data
  } catch (error) {
    ElMessage.error('加载健康评分失败')
  } finally {
    tableLoading.value = false
  }
}

const loadHighRiskMembers = async () => {
  try {
    const data = await memberHealthStore.getHighRiskMembers()
    highRiskMembers.value = data
  } catch (error) {
    ElMessage.error('加载高风险会员失败')
  }
}

const runBatchScoring = async () => {
  try {
    ElMessageBox.confirm(
      '确定要运行AI批量健康评分吗？这将分析所有会员的行为数据并生成健康报告。',
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      await memberHealthStore.runBatchScoring()
      ElMessage.success('AI健康评分任务已启动')
      // 刷新数据
      setTimeout(() => {
        loadHealthStats()
        loadHealthScores()
        loadHighRiskMembers()
      }, 3000)
    })
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const refreshData = () => {
  loadHealthStats()
  loadHealthScores()
  loadHighRiskMembers()
  ElMessage.success('数据已刷新')
}

const exportHealthReport = async () => {
  try {
    await memberHealthStore.exportHealthReport()
    ElMessage.success('健康报告导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const batchInterveneHighRisk = async () => {
  if (highRiskMembers.value.length === 0) {
    ElMessage.warning('没有需要干预的高风险会员')
    return
  }
  
  try {
    ElMessageBox.confirm(
      `确定要对${highRiskMembers.value.length}个高风险会员执行批量干预吗？`,
      '批量干预确认',
      {
        confirmButtonText: '确定干预',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      for (const member of highRiskMembers.value) {
        await memberHealthStore.executeIntervention(member)
      }
      ElMessage.success('批量干预指令已发送')
      refreshData()
    })
  } catch (error) {
    ElMessage.error('批量干预失败')
  }
}

const executeIntervention = async (member: any) => {
  try {
    await memberHealthStore.executeIntervention(member)
    ElMessage.success(`干预指令已发送: ${member.memberName}`)
    refreshData()
  } catch (error) {
    ElMessage.error('干预失败')
  }
}

const viewMemberHealthDetail = (member: any) => {
  ElMessageBox.alert(
    `会员健康详情：<br>
    姓名：${member.memberName}<br>
    健康等级：${getHealthLevelText(member.healthLevel)}<br>
    综合评分：${member.overallScore}<br>
    活跃度：${member.activityScore}<br>
    消费力：${member.consumptionScore}<br>
    忠诚度：${member.loyaltyScore}<br>
    风险度：${member.riskScore}<br>
    流失概率：${Math.round(member.churnProbability * 100)}%<br>
    风险因素：${member.riskFactors}<br>
    改进建议：${member.improvementSuggestions}`,
    '会员健康分析',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

const viewHealthDetail = (score: any) => {
  ElMessageBox.alert(
    `健康评分详情：<br>
    会员：${score.memberName}<br>
    评分日期：${score.scoreDate}<br>
    算法版本：${score.algorithmVersion}<br>
    权重配置：${score.scoreWeights}<br>
    趋势：${getTrendText(score.trend)}<br>
    会员天数：${score.memberDays}天<br>
    7天访问：${score.visitCount7d}次<br>
    7天消费：¥${score.consumptionAmount7d}<br>
    距离上次访问：${score.daysSinceLastVisit}天`,
    '健康评分详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
}

// 辅助方法
const getImprovingCount = () => {
  const trendDist = healthStats.value.trendDistribution
  return trendDist['IMPROVING'] || 0
}

const calculatePercentage = (count: number, total: number) => {
  if (total === 0) return 0
  return Math.round((count / total) * 100)
}

const getHealthLevelTagType = (level: string) => {
  switch (level) {
    case 'EXCELLENT': return 'success'
    case 'GOOD': return 'info'
    case 'FAIR': return 'warning'
    case 'POOR': return 'danger'
    case 'CRITICAL': return 'danger'
    default: return 'info'
  }
}

const getHealthLevelText = (level: string) => {
  switch (level) {
    case 'EXCELLENT': return '优秀'
    case 'GOOD': return '良好'
    case 'FAIR': return '一般'
    case 'POOR': return '较差'
    case 'CRITICAL': return '危险'
    default: return level
  }
}

const getHealthLevelColor = (level: string) => {
  switch (level) {
    case 'EXCELLENT': return '#67c23a'
    case 'GOOD': return '#409eff'
    case 'FAIR': return '#e6a23c'
    case 'POOR': return '#f56c6c'
    case 'CRITICAL': return '#f56c6c'
    default: return '#909399'
  }
}

const getScoreStatus = (score: number) => {
  if (score >= 85) return 'success'
  if (score >= 70) return 'warning'
  return 'exception'
}

const getRiskFactors = (riskFactorsStr: string) => {
  if (!riskFactorsStr) return []
  return riskFactorsStr.split(',').filter(f => f.trim())
}

const getScoreCircleStyle = (score: number) => {
  let color = '#f56c6c' // 红色
  if (score >= 85) color = '#67c23a' // 绿色
  else if (score >= 70) color = '#409eff' // 蓝色
  else if (score >= 50) color = '#e6a23c' // 橙色
  
  return {
    backgroundColor: color,
    color: '#fff',
    width: '40px',
    height: '40px',
    borderRadius: '50%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontWeight: 'bold',
    fontSize: '14px'
  }
}

const getTrendClass = (trend: string) => {
  switch (trend) {
    case 'IMPROVING': return 'trend-improving'
    case 'DECLINING': return 'trend-declining'
    case 'CRITICAL_DECLINE': return 'trend-critical'
    default: return 'trend-stable'
  }
}

const getTrendText = (trend: string) => {
  switch (trend) {
    case 'IMPROVING': return '改善中'
    case 'STABLE': return '稳定'
    case 'DECLINING': return '下降'
    case 'CRITICAL_DECLINE': return '危险下降'
    default: return trend
  }
}

const getChurnTagType = (probability: number) => {
  if (probability > 0.7) return 'danger'
  if (probability > 0.4) return 'warning'
  return 'success'
}
</script>

<style scoped>
.member-health-dashboard {
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.dashboard-header h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.health-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.health-stat-card {
  border-radius: 8px;
  border-top: 3px solid #409eff;
}

.health-stat-card.excellent {
  border-top-color: #67c23a;
}

.health-stat-card.warning {
  border-top-color: #e6a23c;
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.stat-content {
  text-align: center;
  padding: 16px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.health-distribution-card,
.high-risk-members-card,
.health-scores-card,
.ai-algorithm-card {
  margin-bottom: 24px;
  border-radius: 8px;
}

.distribution-content {
  padding: 16px;
}

.distribution-item {
  margin-bottom: 16px;
}

.distribution-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.distribution-count {
  font-weight: bold;
  color: #303133;
}

.distribution-percentage {
  color: #909399;
  font-size: 14px;
}

.urgent-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-filters {
  display: flex;
  gap: 12px;
  align-items: center;
}

.score-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.score-value {
  font-weight: bold;
  min-width: 30px;
}

.dimension-scores {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.dimension-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dimension-label {
  font-size: 12px;
  color: #909399;
  min-width: 30px;
}

.dimension-value {
  font-size: 12px;
  color: #606266;
  min-width: 25px;
}

.risk-factors {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.risk-tag {
  margin: 2px;
}

.churn-probability {
  width: 100px;
}

.overall-score {
  display: flex;
  justify-content: center;
}

.dimension-radar {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 60px;
  padding: 0 10px;
}

.radar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.radar-label {
  font-size: 12px;
  color: #909399;
}

.radar-bar {
  width: 12px;
  height: 50px;
  background: #ebeef5;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
}

.radar-fill {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, #409eff, #79bbff);
  border-radius: 6px;
  transition: height 0.3s;
}

.trend-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
}

.trend-improving {
  color: #67c23a;
  font-weight: 500;
}

.trend-declining {
  color: #f56c6c;
  font-weight: 500;
}

.trend-critical {
  color: #f56c6c;
  font-weight: bold;
}

.trend-stable {
  color: #909399;
}

.inactive-member {
  color: #f56c6c;
  font-weight: 500;
}

.table-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.algorithm-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.weight-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.weight-item {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  text-align: center;
}

.weight-label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

.weight-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.weight-desc {
  font-size: 12px;
  color: #909399;
}

.algorithm-details h4 {
  margin-top: 0;
  margin-bottom: 12px;
  color: #303133;
}

.algorithm-details ul {
  margin: 8px 0;
  padding-left: 20px;
  color: #606266;
}

.algorithm-details li {
  margin: 4px 0;
  line-height: 1.5;
}

.algorithm-details p {
  margin: 8px 0;
  color: #606266;
  line-height: 1.6;
}
</style>
