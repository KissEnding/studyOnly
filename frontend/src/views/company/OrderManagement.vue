<template>
  <div class="order-management">
    <div class="header">
      <h2>投保单管理</h2>
      <div class="filter">
        <el-select v-model="status" placeholder="选择状态" clearable @change="handleStatusChange">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
      </div>
    </div>

    <el-table :data="orders" v-loading="loading">
      <el-table-column prop="orderCode" label="订单编号" width="180" />
      <el-table-column prop="product.name" label="保险产品" width="180" />
      <el-table-column prop="farmer.name" label="投保人" width="120" />
      <el-table-column prop="area" label="投保面积" width="120">
        <template #default="{ row }">
          {{ row.area }} 亩
        </template>
      </el-table-column>
      <el-table-column prop="premium" label="保费" width="120">
        <template #default="{ row }">
          ¥{{ row.premium }}
        </template>
      </el-table-column>
      <el-table-column prop="coverage" label="保额" width="120">
        <template #default="{ row }">
          ¥{{ row.coverage }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="180" />
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
            v-if="row.status === 'PENDING'"
            type="success" 
            size="small" 
            @click="handleAudit(row, 'approve')"
          >
            通过
          </el-button>
          <el-button 
            v-if="row.status === 'PENDING'"
            type="danger" 
            size="small" 
            @click="handleAudit(row, 'reject')"
          >
            拒绝
          </el-button>
        </template>
      </el-table-column>
    </el-table>

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

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrders, auditOrder } from '@/api/company'

export default {
  name: 'OrderManagement',
  setup() {
    const loading = ref(false)
    const submitting = ref(false)
    const orders = ref([])
    const status = ref('')
    const auditVisible = ref(false)
    const auditAction = ref('')
    const currentOrder = ref(null)
    const auditForm = ref({
      remarks: ''
    })

    // 加载订单列表
    const loadOrders = async () => {
      loading.value = true
      try {
        const response = await getOrders({
          status: status.value
        })
        orders.value = response.data
      } catch (error) {
        console.error('Failed to load orders:', error)
        ElMessage.error('获取投保单列表失败')
      } finally {
        loading.value = false
      }
    }

    // 状态变化
    const handleStatusChange = () => {
      loadOrders()
    }

    // 审核操作
    const handleAudit = (order, action) => {
      auditAction.value = action
      currentOrder.value = order
      auditForm.value.remarks = ''
      auditVisible.value = true
    }

    // 提交审核
    const submitAudit = async () => {
      if (!currentOrder.value) return
      
      submitting.value = true
      try {
        await auditOrder(currentOrder.value.id, {
          action: auditAction.value,
          remarks: auditForm.value.remarks
        })
        
        ElMessage.success('操作成功')
        auditVisible.value = false
        loadOrders()
      } catch (error) {
        console.error('Failed to audit order:', error)
        ElMessage.error('操作失败')
      } finally {
        submitting.value = false
      }
    }

    // 获取状态类型
    const getStatusType = (status) => {
      const types = {
        PENDING: 'warning',
        approved: 'success',
        rejected: 'danger'
      }
      return types[status] || 'info'
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const texts = {
        PENDING: '待审核',
        approved: '已通过',
        rejected: '已拒绝'
      }
      return texts[status] || status
    }

    onMounted(() => {
      loadOrders()
    })

    return {
      orders,
      loading,
      status,
      auditVisible,
      auditAction,
      auditForm,
      submitting,
      handleStatusChange,
      handleAudit,
      submitAudit,
      getStatusType,
      getStatusText
    }
  }
}
</script>

<style scoped>
.order-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 