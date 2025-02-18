<template>
  <div class="company-dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6" v-for="card in statisticsCards" :key="card.title">
        <el-card 
          class="statistics-card" 
          :class="card.type"
          @click="handleCardClick(card.route)"
          style="cursor: pointer;"
        >
          <div class="card-content">
            <div class="card-value">{{ card.value }}</div>
            <div class="card-title">{{ card.title }}</div>
          </div>
          <el-icon class="card-icon"><component :is="card.icon" /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 待处理投保单 -->
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>待处理投保单</span>
              <el-button @click="$router.push('/company/orders')" text>查看全部</el-button>
            </div>
          </template>
          <el-table :data="pendingOrders" stripe>
            <el-table-column prop="productName" label="保险产品" />
            <el-table-column prop="farmer" label="投保人" />
            <el-table-column prop="premium" label="保费">
              <template #default="{ row }">
                ¥{{ row.premium }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 最新保单 -->
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>最新保单</span>
              <el-button @click="$router.push('/company/policies')" text>查看全部</el-button>
            </div>
          </template>
          <el-table :data="latestPolicies" stripe>
            <el-table-column prop="productName" label="保险产品" />
            <el-table-column prop="farmer" label="投保人" />
            <el-table-column prop="amount" label="保额">
              <template #default="{ row }">
                ¥{{ row.amount }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 待处理理赔 -->
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>待处理理赔</span>
              <el-button @click="$router.push('/company/claims')" text>查看全部</el-button>
            </div>
          </template>
          <el-table :data="pendingClaims" stripe>
            <el-table-column prop="policyNo" label="保单号" />
            <el-table-column prop="farmer" label="申请人" />
            <el-table-column prop="amount" label="理赔金额">
              <template #default="{ row }">
                ¥{{ row.amount }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 统计数据
const statisticsCards = ref([
  { 
    title: '待处理投保', 
    value: 8, 
    type: 'primary', 
    icon: 'Document',
    route: '/company/orders'
  },
  { 
    title: '有效保单', 
    value: 128, 
    type: 'success', 
    icon: 'Files',
    route: '/company/policies'
  },
  { 
    title: '待处理理赔', 
    value: 5, 
    type: 'warning', 
    icon: 'Warning',
    route: '/company/claims'
  },
  { 
    title: '累计赔付', 
    value: '128.5万', 
    type: 'info', 
    icon: 'Money',
    route: '/company/claims'
  }
])

// 处理卡片点击
const handleCardClick = (route) => {
  if (route) {
    router.push(route)
  }
}

// 待处理投保单数据
const pendingOrders = ref([
  {
    productName: '水稻种植保险',
    farmer: '王五',
    premium: 2000,
    createdAt: '2024-01-21'
  },
  {
    productName: '果树保险',
    farmer: '赵六',
    premium: 3000,
    createdAt: '2024-01-20'
  }
])

// 最新保单数据
const latestPolicies = ref([
  {
    productName: '水稻种植保险',
    farmer: '张三',
    amount: 50000,
    createdAt: '2024-01-20'
  },
  {
    productName: '农机具保险',
    farmer: '李四',
    amount: 30000,
    createdAt: '2024-01-19'
  }
])

// 待处理理赔
const pendingClaims = ref([
  {
    policyNo: 'P202401200001',
    farmer: '张三',
    amount: 5000,
    createdAt: '2024-01-20'
  },
  {
    policyNo: 'P202401190002',
    farmer: '李四',
    amount: 3000,
    createdAt: '2024-01-19'
  }
])
</script>

<style scoped>
.company-dashboard {
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

.statistics-card.success {
  background: linear-gradient(135deg, #52c41a 0%, #95de64 100%);
  color: white;
}

.statistics-card.warning {
  background: linear-gradient(135deg, #faad14 0%, #ffd666 100%);
  color: white;
}

.statistics-card.info {
  background: linear-gradient(135deg, #13c2c2 0%, #87e8de 100%);
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