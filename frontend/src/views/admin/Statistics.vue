<template>
  <div class="statistics-page">
    <div class="page-header">
      <h2>数据统计</h2>
      <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
        <el-radio-button label="week">本周</el-radio-button>
        <el-radio-button label="month">本月</el-radio-button>
        <el-radio-button label="year">本年</el-radio-button>
      </el-radio-group>
    </div>

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
      <!-- 图表 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>保单数量趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <!-- TODO: 添加折线图 -->
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>理赔金额分布</span>
            </div>
          </template>
          <div class="chart-container">
            <!-- TODO: 添加饼图 -->
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>各地区保单分布</span>
            </div>
          </template>
          <div class="chart-container">
            <!-- TODO: 添加地图 -->
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const timeRange = ref('month')

// 统计数据
const statisticsCards = ref([
  { title: '总保单数', value: 1280, type: 'primary', icon: 'Document' },
  { title: '总保费收入', value: '128.5万', type: 'success', icon: 'Money' },
  { title: '总赔付金额', value: '32.6万', type: 'warning', icon: 'Wallet' },
  { title: '理赔率', value: '25.4%', type: 'info', icon: 'DataLine' }
])

const handleTimeRangeChange = (value) => {
  // TODO: 根据时间范围重新加载数据
  console.log('Time range changed:', value)
}
</script>

<style scoped>
.statistics-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
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

.chart-card {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}
</style> 