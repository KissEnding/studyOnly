<template>
  <div class="policies-page">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <h2>我的保单</h2>
      <el-button type="primary" @click="handleBuyInsurance">购买新保险</el-button>
    </div>

    <!-- 数据概览 -->
    <div class="statistics-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="data-card">
            <div class="data-item">
              <div class="data-icon" style="background: #e6f7ff">
                <el-icon color="#1890ff"><Files /></el-icon>
              </div>
              <div class="data-info">
                <div class="data-value">{{ statistics.policyCount }}</div>
                <div class="data-label">生效保单</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="data-card">
            <div class="data-item">
              <div class="data-icon" style="background: #f6ffed">
                <el-icon color="#52c41a"><Money /></el-icon>
              </div>
              <div class="data-info">
                <div class="data-value">{{ formatAmount(statistics.totalPremium) }}</div>
                <div class="data-label">总保费</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="data-card">
            <div class="data-item">
              <div class="data-icon" style="background: #fff7e6">
                <el-icon color="#fa8c16"><Wallet /></el-icon>
              </div>
              <div class="data-info">
                <div class="data-value">{{ formatAmount(statistics.totalCoverage) }}</div>
                <div class="data-label">总保额</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-section">
      <el-select v-model="statusFilter" placeholder="保单状态" clearable @change="handleStatusChange">
        <el-option label="生效中" value="active" />
        <el-option label="待生效" value="pending" />
        <el-option label="已过期" value="expired" />
      </el-select>
    </div>

    <!-- 保单列表 -->
    <el-table :data="policies" v-loading="loading" style="width: 100%; margin-top: 20px">
      <el-table-column prop="policyCode" label="保单号" width="180" />
      <el-table-column prop="product.productName" label="保险产品" width="180" />
      <el-table-column prop="cropType" label="承保作物" width="120" />
      <el-table-column prop="actualPremium" label="保费" width="120">
        <template #default="{ row }">
          {{ formatAmount(row.actualPremium) }}
        </template>
      </el-table-column>
      <el-table-column prop="startDate" label="起保日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.startDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="endDate" label="到期日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.endDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getPolicyStatusType(row.status)">
            {{ getPolicyStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <el-button type="text" @click="handleViewDetails(row)">查看详情</el-button>
          <el-button v-if="row.status === 'active'" type="text" @click="handleClaim(row)">申请理赔</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailsVisible" title="保单详情" width="600px">
      <div v-if="currentPolicy">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="保单号">{{ currentPolicy.policyCode }}</el-descriptions-item>
          <el-descriptions-item label="保险产品">{{ currentPolicy.product.productName }}</el-descriptions-item>
          <el-descriptions-item label="承保作物">{{ currentPolicy.cropType }}</el-descriptions-item>
          <el-descriptions-item label="保险公司">{{ currentPolicy.company.name }}</el-descriptions-item>
          <el-descriptions-item label="保费">{{ formatAmount(currentPolicy.actualPremium) }}</el-descriptions-item>
          <el-descriptions-item label="保险金额">{{ formatAmount(currentPolicy.product.basePremium * currentPolicy.product.coverageMultiplier) }}</el-descriptions-item>
          <el-descriptions-item label="起保日期">{{ formatDate(currentPolicy.startDate) }}</el-descriptions-item>
          <el-descriptions-item label="到期日期">{{ formatDate(currentPolicy.endDate) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getPolicyStatusType(currentPolicy.status)">
              {{ getPolicyStatusText(currentPolicy.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="折扣率">{{ (currentPolicy.discountRate * 100).toFixed(0) }}%</el-descriptions-item>
        </el-descriptions>
        <div style="margin-top: 20px; text-align: right;">
          <el-button v-if="currentPolicy.status === 'active'" type="primary" @click="handleClaim(currentPolicy)">
            申请理赔
          </el-button>
          <el-button @click="detailsVisible = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Files, Money, Wallet } from '@element-plus/icons-vue'
import { getPolicies, getPolicy, getPolicyStatistics } from '@/api/policy'

const router = useRouter()
const detailsVisible = ref(false)
const currentPolicy = ref(null)
const loading = ref(false)
const statusFilter = ref('')
const policies = ref([])

// 统计数据
const statistics = ref({
  policyCount: 0,
  totalPremium: 0,
  totalCoverage: 0
})

const loadStatistics = async () => {
  try {
    const response = await getPolicyStatistics()
    if (response && response.data) {
      statistics.value = {
        policyCount: response.data.policyCount || 0,
        totalPremium: response.data.totalPremium || 0,
        totalCoverage: response.data.totalCoverage || 0
      }
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
    console.error('Failed to load statistics:', error)
  }
}

const loadPolicies = async () => {
  loading.value = true
  try {
    const response = await getPolicies({
      status: statusFilter.value || undefined
    })
    if (response && response.data) {
      policies.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取保单列表失败')
    console.error('Failed to load policies:', error)
  } finally {
    loading.value = false
  }
}

const getPolicyStatusType = (status) => {
  const types = {
    active: 'success',
    pending: 'warning',
    expired: 'info'
  }
  return types[status] || 'info'
}

const getPolicyStatusText = (status) => {
  const texts = {
    active: '生效中',
    pending: '待生效',
    expired: '已过期'
  }
  return texts[status] || status
}

const handleBuyInsurance = () => {
  router.push('/farmer/market')
}

const handleViewDetails = (policy) => {
  currentPolicy.value = policy
  detailsVisible.value = true
}

const handleClaim = (policy) => {
  router.push({
    path: '/farmer/claims',
    query: { policyId: policy.id }
  })
}

const handleStatusChange = async () => {
  await loadPolicies()
}

const formatAmount = (amount) => {
  if (!amount) return '¥0.00'
  return amount.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
    style: 'currency',
    currency: 'CNY'
  })
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

// 初始化数据
loadPolicies()
loadStatistics()
</script>

<style scoped>
.policies-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.statistics-section {
  margin-bottom: 20px;
}

.data-card {
  background: #fff;
  border-radius: 4px;
}

.data-item {
  display: flex;
  align-items: center;
  padding: 20px;
}

.data-icon {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.data-info {
  flex: 1;
}

.data-value {
  font-size: 24px;
  font-weight: bold;
  line-height: 32px;
  color: #262626;
}

.data-label {
  font-size: 14px;
  line-height: 22px;
  color: #8c8c8c;
}

.filter-section {
  margin-bottom: 20px;
}
</style> 