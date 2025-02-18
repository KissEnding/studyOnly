<template>
  <div class="company-audit">
    <div class="page-header">
      <h2>保险公司审核</h2>
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="审核状态">
            <el-option label="全部" value="" />
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card>
      <el-table :data="companies" stripe>
        <el-table-column prop="companyName" label="公司名称" min-width="180" />
        <el-table-column prop="creditCode" label="统一社会信用代码" width="200" />
        <el-table-column prop="type" label="公司类型" width="100">
          <template #default="{ row }">
            {{ row.type === 'headquarters' ? '总公司' : '分公司' }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="所在地区" min-width="180" />
        <el-table-column prop="contactPerson" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'pending'"
              link 
              type="primary" 
              @click="handleAudit(row)"
            >
              审核
            </el-button>
            <el-button 
              link 
              type="primary" 
              @click="handleViewDetails(row)"
            >
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

    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditVisible"
      title="公司审核"
      width="600px"
    >
      <el-form
        ref="auditFormRef"
        :model="auditForm"
        :rules="auditRules"
        label-width="100px"
      >
        <el-form-item label="公司名称">
          {{ currentCompany?.companyName }}
        </el-form-item>
        <el-form-item label="审核结果" prop="status">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="approved">通过</el-radio>
            <el-radio label="rejected">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="comment">
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitAudit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailsVisible"
      title="公司详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公司名称">
          {{ currentCompany?.companyName }}
        </el-descriptions-item>
        <el-descriptions-item label="统一社会信用代码">
          {{ currentCompany?.creditCode }}
        </el-descriptions-item>
        <el-descriptions-item label="公司类型">
          {{ currentCompany?.type === 'headquarters' ? '总公司' : '分公司' }}
        </el-descriptions-item>
        <el-descriptions-item label="所属总公司" v-if="currentCompany?.type === 'branch'">
          {{ currentCompany?.parentCompany }}
        </el-descriptions-item>
        <el-descriptions-item label="所在地区">
          {{ currentCompany?.location }}
        </el-descriptions-item>
        <el-descriptions-item label="联系人">
          {{ currentCompany?.contactPerson }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentCompany?.contactPhone }}
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">
          {{ currentCompany?.createdAt }}
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(currentCompany?.status)">
            {{ getStatusText(currentCompany?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="currentCompany?.auditTime">
          {{ currentCompany?.auditTime }}
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" v-if="currentCompany?.auditComment">
          {{ currentCompany?.auditComment }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="license-preview">
        <h3>营业执照</h3>
        <el-image
          :src="currentCompany?.license"
          :preview-src-list="[currentCompany?.license]"
          fit="contain"
          class="license-image"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const searchForm = reactive({
  status: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 模拟数据
const companies = ref([
  {
    id: 1,
    companyName: '某某保险公司某某州分公司',
    creditCode: '91330000XXXXXXXXXX',
    type: 'branch',
    parentCompany: '某某保险公司',
    location: '浙江省杭州市西湖区',
    contactPerson: '王经理',
    contactPhone: '0571-88888888',
    license: '/api/uploads/company/license.jpg',
    status: 'pending',
    createdAt: '2024-01-20'
  }
])

const auditVisible = ref(false)
const detailsVisible = ref(false)
const currentCompany = ref(null)
const submitting = ref(false)

const auditForm = reactive({
  status: '',
  comment: ''
})

const auditRules = {
  status: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  comment: [{ required: true, message: '请输入审核意见', trigger: 'blur' }]
}

const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
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

const handleAudit = (row) => {
  currentCompany.value = row
  auditForm.status = ''
  auditForm.comment = ''
  auditVisible.value = true
}

const handleViewDetails = (row) => {
  currentCompany.value = row
  detailsVisible.value = true
}

const handleSubmitAudit = async () => {
  if (!auditFormRef.value) return
  
  await auditFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用API保存数据
        await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
        
        // 更新本地数据
        const company = companies.value.find(c => c.id === currentCompany.value.id)
        if (company) {
          company.status = auditForm.status
          company.auditTime = new Date().toLocaleDateString()
          company.auditComment = auditForm.comment
        }
        
        ElMessage.success('审核成功')
        auditVisible.value = false
      } finally {
        submitting.value = false
      }
    }
  })
}
</script>

<style scoped>
.company-audit {
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

.license-preview {
  margin-top: 20px;
}

.license-preview h3 {
  margin-bottom: 10px;
  color: #606266;
}

.license-image {
  width: 100%;
  max-height: 400px;
  border-radius: 4px;
}
</style> 