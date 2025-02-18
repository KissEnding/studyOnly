<template>
  <div class="claims-page">
    <div class="page-header">
      <h2>理赔管理</h2>
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-input v-model="searchForm.keyword" placeholder="搜索理赔单号/申请人" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="理赔状态">
            <el-option label="全部" value="" />
            <el-option label="待处理" value="pending" />
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
      <el-table :data="claims" stripe>
        <el-table-column prop="claimNo" label="理赔单号" width="150" />
        <el-table-column prop="policyNo" label="保单号" width="150" />
        <el-table-column prop="farmer" label="申请人" width="120" />
        <el-table-column prop="amount" label="理赔金额" width="120">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="理赔原因" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="申请时间" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getClaimStatusType(row.status)">
              {{ getClaimStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'pending'"
              link 
              type="primary" 
              @click="handleProcess(row)"
            >
              处理
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

    <!-- 理赔处理对话框 -->
    <el-dialog
      v-model="processVisible"
      title="理赔处理"
      width="600px"
    >
      <el-form
        ref="processFormRef"
        :model="processForm"
        :rules="processRules"
        label-width="100px"
      >
        <el-form-item label="理赔单号">
          {{ currentClaim?.claimNo }}
        </el-form-item>
        <el-form-item label="申请人">
          {{ currentClaim?.farmer }}
        </el-form-item>
        <el-form-item label="理赔金额">
          ¥{{ currentClaim?.amount }}
        </el-form-item>
        <el-form-item label="处理结果" prop="status">
          <el-radio-group v-model="processForm.status">
            <el-radio label="approved">通过</el-radio>
            <el-radio label="rejected">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item 
          label="赔付金额" 
          prop="amount"
          v-if="processForm.status === 'approved'"
        >
          <el-input-number 
            v-model="processForm.amount"
            :min="0"
            :max="currentClaim?.amount"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="处理意见" prop="comment">
          <el-input
            v-model="processForm.comment"
            type="textarea"
            rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="processVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitProcess" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 理赔详情对话框 -->
    <el-dialog
      v-model="detailsVisible"
      title="理赔详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="理赔单号">
          {{ currentClaim?.claimNo }}
        </el-descriptions-item>
        <el-descriptions-item label="保单号">
          {{ currentClaim?.policyNo }}
        </el-descriptions-item>
        <el-descriptions-item label="申请人">
          {{ currentClaim?.farmer }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentClaim?.phone }}
        </el-descriptions-item>
        <el-descriptions-item label="理赔金额">
          ¥{{ currentClaim?.amount }}
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">
          {{ currentClaim?.createdAt }}
        </el-descriptions-item>
        <el-descriptions-item label="理赔原因" :span="2">
          {{ currentClaim?.reason }}
        </el-descriptions-item>
        <el-descriptions-item label="理赔状态">
          <el-tag :type="getClaimStatusType(currentClaim?.status)">
            {{ getClaimStatusText(currentClaim?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理时间" v-if="currentClaim?.processedAt">
          {{ currentClaim?.processedAt }}
        </el-descriptions-item>
        <el-descriptions-item label="处理意见" :span="2" v-if="currentClaim?.comment">
          {{ currentClaim?.comment }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="claim-images" v-if="currentClaim?.images?.length">
        <h3>理赔凭证</h3>
        <el-image
          v-for="(image, index) in currentClaim.images"
          :key="index"
          :src="image"
          :preview-src-list="currentClaim.images"
          fit="cover"
          class="claim-image"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const searchForm = reactive({
  keyword: '',
  status: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 模拟数据
const claims = ref([
  {
    claimNo: 'C202401200001',
    policyNo: 'P202401200001',
    farmer: '张三',
    phone: '13800138000',
    amount: 5000,
    reason: '台风导致水稻倒伏，造成严重损失',
    createdAt: '2024-01-20',
    status: 'pending',
    images: [
      '/api/uploads/claims/example1.jpg',
      '/api/uploads/claims/example2.jpg'
    ]
  },
  {
    claimNo: 'C202401190002',
    policyNo: 'P202401190002',
    farmer: '李四',
    phone: '13900139000',
    amount: 3000,
    reason: '农机具在使用过程中意外损坏',
    createdAt: '2024-01-19',
    status: 'approved',
    processedAt: '2024-01-20',
    comment: '核实损失情况属实，同意赔付'
  }
])

const processVisible = ref(false)
const detailsVisible = ref(false)
const currentClaim = ref(null)
const submitting = ref(false)

const processForm = reactive({
  status: '',
  amount: 0,
  comment: ''
})

const processRules = {
  status: [{ required: true, message: '请选择处理结果', trigger: 'change' }],
  comment: [{ required: true, message: '请输入处理意见', trigger: 'blur' }]
}

const getClaimStatusType = (status) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return types[status] || 'info'
}

const getClaimStatusText = (status) => {
  const texts = {
    pending: '待处理',
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

const handleProcess = (row) => {
  currentClaim.value = row
  processForm.status = ''
  processForm.amount = row.amount
  processForm.comment = ''
  processVisible.value = true
}

const handleViewDetails = (row) => {
  currentClaim.value = row
  detailsVisible.value = true
}

const handleSubmitProcess = async () => {
  if (!processFormRef.value) return
  
  await processFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用API保存数据
        await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
        
        // 更新本地数据
        const claim = claims.value.find(c => c.claimNo === currentClaim.value.claimNo)
        if (claim) {
          claim.status = processForm.status
          claim.processedAt = new Date().toLocaleDateString()
          claim.comment = processForm.comment
          if (processForm.status === 'approved') {
            claim.amount = processForm.amount
          }
        }
        
        ElMessage.success('处理成功')
        processVisible.value = false
      } finally {
        submitting.value = false
      }
    }
  })
}
</script>

<style scoped>
.claims-page {
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

.claim-images {
  margin-top: 20px;
}

.claim-images h3 {
  margin-bottom: 10px;
  color: #606266;
}

.claim-image {
  width: 100px;
  height: 100px;
  margin-right: 10px;
  margin-bottom: 10px;
  border-radius: 4px;
  cursor: pointer;
}
</style> 