<template>
  <div class="insurance-applications">
    <div class="page-header">
      <h2>投保单管理</h2>
      <div class="filter-section">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="选择状态" clearable>
              <el-option label="待审核" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadApplications">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 投保单列表 -->
    <el-table :data="applications" v-loading="loading" style="width: 100%; margin-top: 20px">
      <el-table-column prop="applicationCode" label="申请编号" width="180" />
      <el-table-column prop="product.productName" label="保险产品" width="180" />
      <el-table-column prop="farmer.name" label="投保人" width="120" />
      <el-table-column prop="area" label="投保面积" width="120">
        <template #default="{ row }">
          {{ row.area }} 亩
        </template>
      </el-table-column>
      <el-table-column prop="premium" label="保费" width="120">
        <template #default="{ row }">
          {{ formatAmount(row.premium) }}
        </template>
      </el-table-column>
      <el-table-column prop="coverage" label="保额" width="120">
        <template #default="{ row }">
          {{ formatAmount(row.coverage) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <el-button 
            v-if="row.status === 'pending'"
            type="success" 
            size="small" 
            @click="handleApprove(row)"
          >
            通过
          </el-button>
          <el-button 
            v-if="row.status === 'pending'"
            type="danger" 
            size="small" 
            @click="handleReject(row)"
          >
            拒绝
          </el-button>
          <el-button 
            type="primary" 
            size="small" 
            @click="handleViewDetails(row)"
          >
            查看
          </el-button>
        </template>
      </el-table-column>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailsVisible" title="投保单详情" width="600px">
      <div v-if="currentApplication">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申请编号">{{ currentApplication.applicationCode }}</el-descriptions-item>
          <el-descriptions-item label="保险产品">{{ currentApplication.product?.productName }}</el-descriptions-item>
          <el-descriptions-item label="投保人">{{ currentApplication.farmer?.name }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentApplication.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="投保面积">{{ currentApplication.area }} 亩</el-descriptions-item>
          <el-descriptions-item label="保费">{{ formatAmount(currentApplication.premium) }}</el-descriptions-item>
          <el-descriptions-item label="保额">{{ formatAmount(currentApplication.coverage) }}</el-descriptions-item>
          <el-descriptions-item label="投保地址">{{ currentApplication.address }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatDateTime(currentApplication.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentApplication.status)">
              {{ getStatusText(currentApplication.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentApplication.remarks || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailsVisible = false">关闭</el-button>
          <el-button 
            v-if="currentApplication?.status === 'pending'"
            type="success" 
            @click="handleApprove(currentApplication)"
          >
            通过
          </el-button>
          <el-button 
            v-if="currentApplication?.status === 'pending'"
            type="danger" 
            @click="handleReject(currentApplication)"
          >
            拒绝
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 审核确认弹窗 -->
    <el-dialog v-model="auditVisible" :title="auditAction === 'approve' ? '通过审核' : '拒绝申请'" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入审核意见（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAudit" :loading="submitting">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitting = ref(false)
const applications = ref([])
const currentApplication = ref(null)
const detailsVisible = ref(false)
const auditVisible = ref(false)
const auditAction = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filterForm = ref({
  status: ''
})

const auditForm = ref({
  remarks: ''
})

// 加载投保单列表
const loadApplications = async () => {
  loading.value = true
  try {
    const response = await request({
      url: '/api/company/insurance-applications',
      method: 'get',
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        status: filterForm.value.status
      }
    })
    applications.value = response.data.content
    total.value = response.data.totalElements
  } catch (error) {
    console.error('Failed to load applications:', error)
    ElMessage.error('获取投保单列表失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const handleViewDetails = (application) => {
  currentApplication.value = application
  detailsVisible.value = true
}

// 通过申请
const handleApprove = (application) => {
  auditAction.value = 'approve'
  currentApplication.value = application
  auditVisible.value = true
}

// 拒绝申请
const handleReject = (application) => {
  auditAction.value = 'reject'
  currentApplication.value = application
  auditVisible.value = true
}

// 提交审核
const submitAudit = async () => {
  if (!currentApplication.value) return
  
  submitting.value = true
  try {
    await request({
      url: `/api/company/insurance-applications/${currentApplication.value.id}/audit`,
      method: 'post',
      data: {
        action: auditAction.value,
        remarks: auditForm.value.remarks
      }
    })
    
    ElMessage.success('操作成功')
    auditVisible.value = false
    detailsVisible.value = false
    loadApplications()
  } catch (error) {
    console.error('Failed to audit application:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  loadApplications()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  loadApplications()
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '¥0.00'
  return amount.toLocaleString('zh-CN', {
    style: 'currency',
    currency: 'CNY'
  })
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return ''
  return new Date(datetime).toLocaleString('zh-CN')
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return texts[status] || status
}

// 初始化
onMounted(() => {
  loadApplications()
})
</script>

<style scoped>
.insurance-applications {
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
}

.filter-section {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 