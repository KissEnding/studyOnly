<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6" v-for="card in statisticsCards" :key="card.title">
        <el-card class="statistics-card" :class="card.type">
          <div class="card-content">
            <div class="card-value">{{ card.value }}</div>
            <div class="card-title">{{ card.title }}</div>
          </div>
          <el-icon class="card-icon"><component :is="card.icon" /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 待审核列表 -->
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>待审核农户</span>
              <el-button @click="$router.push('/admin/farmer-audit')" text>查看全部</el-button>
            </div>
          </template>
          <el-table :data="pendingFarmers" stripe style="width: 100%">
            <el-table-column prop="name" label="户主姓名" />
            <el-table-column prop="createdAt" label="注册时间">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleAudit(row)">
                  审核
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 待审核保险公司 -->
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>待审核保险公司</span>
              <el-button @click="$router.push('/admin/company-audit')" text>查看全部</el-button>
            </div>
          </template>
          <el-table :data="pendingCompanies" stripe style="width: 100%">
            <el-table-column prop="companyName" label="公司名称" />
            <el-table-column prop="createdAt" label="申请时间">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleCompanyAudit(row)">
                  审核
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils/date'
import axios from 'axios'

const router = useRouter()

// 统计数据
const statisticsCards = ref([
  { title: '待审核农户', value: 0, type: 'primary', icon: 'User' },
  { title: '待审核公司', value: 0, type: 'warning', icon: 'Office' },
  { title: '已签订保单', value: 0, type: 'success', icon: 'Document' },
  { title: '待处理理赔', value: 0, type: 'danger', icon: 'Money' }
])

// 待审核数据
const pendingFarmers = ref([])
const pendingCompanies = ref([])

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await axios.get('/api/admin/statistics')
    const data = response.data
    statisticsCards.value[0].value = data.pendingFarmers
    statisticsCards.value[1].value = data.pendingCompanies
    statisticsCards.value[2].value = data.totalPolicies
    statisticsCards.value[3].value = data.pendingClaims
  } catch (error) {
    console.error('Failed to fetch statistics:', error)
  }
}

// 获取待审核农户
const fetchPendingFarmers = async () => {
  try {
    const response = await axios.get('/api/admin/farmers?status=pending')
    pendingFarmers.value = response.data.slice(0, 5) // 只显示前5条
  } catch (error) {
    console.error('Failed to fetch pending farmers:', error)
  }
}

// 获取待审核公司
const fetchPendingCompanies = async () => {
  try {
    const response = await axios.get('/api/admin/companies?status=pending')
    pendingCompanies.value = response.data.slice(0, 5) // 只显示前5条
  } catch (error) {
    console.error('Failed to fetch pending companies:', error)
  }
}

// 处理农户审核
const handleAudit = (farmer) => {
  router.push(`/admin/farmer-audit?id=${farmer.id}`)
}

// 处理公司审核
const handleCompanyAudit = (company) => {
  router.push(`/admin/company-audit?id=${company.id}`)
}

onMounted(() => {
  fetchStatistics()
  fetchPendingFarmers()
  fetchPendingCompanies()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.statistics-card {
  position: relative;
  overflow: hidden;
  height: 120px;
}

.card-content {
  position: relative;
  z-index: 1;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.card-title {
  font-size: 14px;
  color: #666;
}

.card-icon {
  position: absolute;
  right: 20px;
  bottom: 20px;
  font-size: 48px;
  opacity: 0.2;
}

.statistics-card.primary {
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  color: white;
}

.statistics-card.warning {
  background: linear-gradient(135deg, #ffa940 0%, #ffd666 100%);
  color: white;
}

.statistics-card.success {
  background: linear-gradient(135deg, #52c41a 0%, #95de64 100%);
  color: white;
}

.statistics-card.danger {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
}

.statistics-card .card-title {
  color: rgba(255, 255, 255, 0.85);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.box-card {
  margin-bottom: 20px;
}
</style> 