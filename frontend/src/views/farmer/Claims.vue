<template>
  <div class="claims-page">
    <div class="page-header">
      <h2>理赔记录</h2>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable>
            <el-option label="待赔付" :value="0" />
            <el-option label="已赔付" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadClaims">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 理赔记录表格 -->
    <el-table :data="claims" v-loading="loading" style="width: 100%; margin-top: 20px">
      <el-table-column prop="policy.policyCode" label="保单号" width="180" />
      <el-table-column prop="policy.product.productName" label="保险产品" width="180" />
      <el-table-column prop="disasterTime" label="灾害时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.disasterTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="disasterType" label="灾害类型" width="120" />
      <el-table-column prop="damagedArea" label="受损面积" width="120">
        <template #default="{ row }">
          {{ row.damagedArea }} 亩
        </template>
      </el-table-column>
      <el-table-column prop="compensationAmount" label="赔付金额" width="120">
        <template #default="{ row }">
          {{ formatMoney(row.compensationAmount) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status ? 'success' : 'warning'">
            {{ row.status ? '已赔付' : '待赔付' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="paymentDate" label="赔付日期" width="120">
        <template #default="{ row }">
          {{ row.paymentDate ? formatDate(row.paymentDate) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="备注" min-width="200" show-overflow-tooltip />
    </el-table>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDateTime, formatDate, formatMoney } from '@/utils/format'

// 数据状态
const loading = ref(false)
const claims = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 筛选表单
const filterForm = ref({
  status: ''
})

// 加载理赔记录
const loadClaims = async () => {
  loading.value = true
  try {
    const response = await fetch(`/api/farmer/claims?page=${currentPage.value}&size=${pageSize.value}&status=${filterForm.value.status}`)
    if (!response.ok) {
      const errorData = await response.text()
      throw new Error(errorData || '获取理赔记录失败')
    }
    const data = await response.json()
    claims.value = data.content
    total.value = data.total
  } catch (error) {
    console.error('获取理赔记录失败:', error)
    ElMessage.error(error.message || '获取理赔记录失败')
  } finally {
    loading.value = false
  }
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  loadClaims()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  loadClaims()
}

// 组件挂载时加载数据
onMounted(() => {
  loadClaims()
})
</script>

<style scoped>
.claims-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.filter-section {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 