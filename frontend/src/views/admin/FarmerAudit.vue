<template>
  <div class="farmer-audit">
    <div class="header">
      <h2>农户审核管理</h2>
      <el-radio-group v-model="currentStatus" @change="handleStatusChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="pending">待审核</el-radio-button>
        <el-radio-button label="approved">已通过</el-radio-button>
        <el-radio-button label="rejected">已拒绝</el-radio-button>
      </el-radio-group>
    </div>

    <el-card 
      class="table-container"
      v-resize-observer="handleResize"
    >
      <el-table 
        ref="tableRef"
        :data="farmers" 
        v-loading="loading" 
        stripe
        style="width: 100%"
        :height="tableHeight"
      >
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="idCard" label="身份证号" />
        <el-table-column prop="location" label="所在地区" />
        <el-table-column prop="contactPhone" label="联系电话" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              link
              @click="handleView(row)"
              v-if="row.status === 'pending'"
            >
              审核
            </el-button>
            <el-button 
              type="primary" 
              link
              @click="handleView(row)"
              v-else
            >
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentFarmer?.status === 'pending' ? '农户审核' : '农户详情'"
      width="600px"
    >
      <div class="audit-content" v-if="currentFarmer">
        <div class="info-section">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="户主姓名">
              {{ currentFarmer.name }}
            </el-descriptions-item>
            <el-descriptions-item label="身份证号">
              {{ currentFarmer.idCard }}
            </el-descriptions-item>
            <el-descriptions-item label="所在地区">
              {{ currentFarmer.location }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ currentFarmer.contactPhone }}
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">
              {{ formatDate(currentFarmer.createdAt) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="doc-section">
          <h3>户口本扫描件</h3>
          <div class="doc-preview">
            <img :src="currentFarmer.householdDoc" alt="户口本扫描件">
          </div>
        </div>

        <div class="audit-section" v-if="currentFarmer.status === 'pending'">
          <h3>审核结果</h3>
          <el-form :model="auditForm" ref="auditFormRef" :rules="auditRules">
            <el-form-item prop="status">
              <el-radio-group v-model="auditForm.status">
                <el-radio label="approved">通过</el-radio>
                <el-radio label="rejected">拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </div>

        <div class="audit-section" v-else-if="currentFarmer.status !== 'pending'">
          <h3>审核结果</h3>
          <el-tag :type="getStatusType(currentFarmer.status)">
            {{ getStatusText(currentFarmer.status) }}
          </el-tag>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button 
          v-if="currentFarmer?.status === 'pending'"
          type="primary" 
          @click="handleAudit" 
          :loading="auditLoading"
        >
          提交审核
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/date'
import { getFarmerList, auditFarmer } from '@/api/admin'
import { vResizeObserver } from '@/directives/resize-observer'

const loading = ref(false)
const farmers = ref([])
const currentStatus = ref('')
const dialogVisible = ref(false)
const currentFarmer = ref(null)
const auditLoading = ref(false)
const tableRef = ref(null)
const tableHeight = ref('400px')

const auditForm = ref({
  status: ''
})

const auditRules = {
  status: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
}

const auditFormRef = ref(null)

// 获取农户列表
const fetchFarmers = async () => {
  try {
    loading.value = true
    console.log('Token:', localStorage.getItem('token'))
    console.log('UserRole:', localStorage.getItem('userRole'))
    const response = await getFarmerList(currentStatus.value)
    console.log('Farmers response:', response)
    farmers.value = response
    console.log('Farmers data:', farmers.value)
  } catch (error) {
    console.error('Failed to fetch farmers:', error)
    ElMessage.error('获取农户列表失败')
  } finally {
    loading.value = false
  }
}

// 状态改变时重新加载列表
const handleStatusChange = () => {
  fetchFarmers()
}

// 查看农户详情
const handleView = (farmer) => {
  currentFarmer.value = farmer
  auditForm.value = {
    status: ''
  }
  dialogVisible.value = true
}

// 提交审核
const handleAudit = async () => {
  if (!auditFormRef.value) return
  
  try {
    await auditFormRef.value.validate()
    
    auditLoading.value = true
    await auditFarmer(currentFarmer.value.id, { status: auditForm.value.status })
    
    ElMessage.success('审核完成')
    dialogVisible.value = false
    fetchFarmers()
  } catch (error) {
    console.error('Audit failed:', error)
    ElMessage.error(error.response?.data?.message || '审核失败')
  } finally {
    auditLoading.value = false
  }
}

// 获取状态标签类型
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

// 处理容器大小变化
const handleResize = () => {
  if (tableRef.value) {
    // 计算表格可用高度
    const windowHeight = window.innerHeight
    const tableTop = tableRef.value.$el.getBoundingClientRect().top
    const padding = 40 // 底部预留空间
    tableHeight.value = `${windowHeight - tableTop - padding}px`
  }
}

onMounted(() => {
  fetchFarmers()
  handleResize() // 初始化表格高度
})
</script>

<style scoped>
.farmer-audit {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.farmer-info {
  margin-bottom: 20px;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: flex-start;
}

.info-label {
  width: 100px;
  color: #606266;
  margin-right: 10px;
}

.info-content {
  flex: 1;
}

.doc-preview {
  margin-top: 20px;
}

.doc-preview h3 {
  margin-bottom: 10px;
  color: #606266;
}

.doc-preview img {
  max-width: 100%;
  max-height: 400px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.audit-section {
  margin-top: 30px;
}
</style> 