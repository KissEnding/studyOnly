<template>
  <div class="policies-page">
    <div class="page-header">
      <h2>保单管理</h2>
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-input v-model="searchForm.keyword" placeholder="搜索保单号/投保人" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="保单状态">
            <el-option label="全部" value="" />
            <el-option label="生效中" value="active" />
            <el-option label="待生效" value="pending" />
            <el-option label="已过期" value="expired" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card>
      <el-table :data="policies" stripe>
        <el-table-column prop="policyNo" label="保单号" width="150" />
        <el-table-column prop="productName" label="保险产品" min-width="150" />
        <el-table-column prop="farmer" label="投保人" width="120" />
        <el-table-column prop="amount" label="保额" width="120">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="premium" label="保费" width="120">
          <template #default="{ row }">
            ¥{{ row.premium }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="起保日期" width="120" />
        <el-table-column prop="endDate" label="到期日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getPolicyStatusType(row.status)">
              {{ getPolicyStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetails(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 保单详情对话框 -->
    <el-dialog
      v-model="detailsVisible"
      title="保单详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="保单号">
          {{ currentPolicy?.policyNo }}
        </el-descriptions-item>
        <el-descriptions-item label="保险产品">
          {{ currentPolicy?.productName }}
        </el-descriptions-item>
        <el-descriptions-item label="投保人">
          {{ currentPolicy?.farmer }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentPolicy?.phone }}
        </el-descriptions-item>
        <el-descriptions-item label="保额">
          ¥{{ currentPolicy?.amount }}
        </el-descriptions-item>
        <el-descriptions-item label="保费">
          ¥{{ currentPolicy?.premium }}
        </el-descriptions-item>
        <el-descriptions-item label="起保日期">
          {{ currentPolicy?.startDate }}
        </el-descriptions-item>
        <el-descriptions-item label="到期日期">
          {{ currentPolicy?.endDate }}
        </el-descriptions-item>
        <el-descriptions-item label="投保地址">
          {{ currentPolicy?.address }}
        </el-descriptions-item>
        <el-descriptions-item label="保单状态">
          <el-tag :type="getPolicyStatusType(currentPolicy?.status)">
            {{ getPolicyStatusText(currentPolicy?.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const searchForm = reactive({
  keyword: '',
  status: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 模拟数据
const policies = ref([
  {
    policyNo: 'P202401200001',
    productName: '水稻种植保险',
    farmer: '张三',
    phone: '13800138000',
    amount: 50000,
    premium: 1250,
    startDate: '2024-01-20',
    endDate: '2024-12-31',
    address: '浙江省杭州市萧山区XX镇',
    status: 'active'
  },
  {
    policyNo: 'P202401190002',
    productName: '农机具保险',
    farmer: '李四',
    phone: '13900139000',
    amount: 30000,
    premium: 900,
    startDate: '2024-02-01',
    endDate: '2025-01-31',
    address: '浙江省杭州市余杭区XX镇',
    status: 'pending'
  }
])

const detailsVisible = ref(false)
const currentPolicy = ref(null)

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

const handleSearch = () => {
  // TODO: 实现搜索功能
  console.log('Search with:', searchForm)
}

const handleSizeChange = (val) => {
  pageSize.value = val
  // TODO: 重新加载数据
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  // TODO: 重新加载数据
}

const handleViewDetails = (row) => {
  currentPolicy.value = row
  detailsVisible.value = true
}
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

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 