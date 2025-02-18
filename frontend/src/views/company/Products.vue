<template>
  <div class="products-page">
    <div class="page-header">
      <h2>保险产品管理</h2>
      <el-button type="primary" @click="handleAdd">新增产品</el-button>
    </div>

    <!-- 产品列表 -->
    <el-card>
      <el-table :data="products" stripe style="width: 100%">
        <el-table-column prop="name" label="产品名称" min-width="150" />
        <el-table-column prop="type" label="保险类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getProductTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="coverage" label="保障范围" min-width="200" show-overflow-tooltip />
        <el-table-column prop="maxAmount" label="最高保额" width="120">
          <template #default="{ row }">
            ¥{{ row.maxAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="premium" label="保费率" width="100">
          <template #default="{ row }">
            {{ row.premium }}%
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              link 
              :type="row.status === 'active' ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'active' ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 产品表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑产品' : '新增产品'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="产品名称" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="保险类型" prop="type">
          <el-select v-model="formData.type" style="width: 100%">
            <el-option label="种植保险" value="crop" />
            <el-option label="养殖保险" value="livestock" />
            <el-option label="农机具保险" value="machinery" />
          </el-select>
        </el-form-item>
        <el-form-item label="保障范围" prop="coverage">
          <el-input
            v-model="formData.coverage"
            type="textarea"
            rows="3"
          />
        </el-form-item>
        <el-form-item label="最高保额" prop="maxAmount">
          <el-input-number 
            v-model="formData.maxAmount"
            :min="1000"
            :step="1000"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="保费率" prop="premium">
          <el-input-number
            v-model="formData.premium"
            :min="0.1"
            :max="100"
            :step="0.1"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 模拟数据
const products = ref([
  {
    id: 1,
    name: '水稻种植保险',
    type: 'crop',
    coverage: '覆盖水稻种植过程中的自然灾害损失，包括台风、洪涝、干旱等',
    maxAmount: 100000,
    premium: 2.5,
    status: 'active'
  },
  {
    id: 2,
    name: '农机具综合保险',
    type: 'machinery',
    coverage: '农业机械设备的意外损坏、自然灾害损失等',
    maxAmount: 50000,
    premium: 3.0,
    status: 'inactive'
  }
])

const formData = reactive({
  name: '',
  type: '',
  coverage: '',
  maxAmount: 10000,
  premium: 1.0
})

const rules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择保险类型', trigger: 'change' }],
  coverage: [{ required: true, message: '请输入保障范围', trigger: 'blur' }],
  maxAmount: [{ required: true, message: '请输入最高保额', trigger: 'blur' }],
  premium: [{ required: true, message: '请输入保费率', trigger: 'blur' }]
}

const getProductTypeText = (type) => {
  const types = {
    crop: '种植保险',
    livestock: '养殖保险',
    machinery: '农机具保险'
  }
  return types[type] || type
}

const getStatusType = (status) => {
  return status === 'active' ? 'success' : 'info'
}

const getStatusText = (status) => {
  return status === 'active' ? '已上架' : '已下架'
}

const resetForm = () => {
  formData.name = ''
  formData.type = ''
  formData.coverage = ''
  formData.maxAmount = 10000
  formData.premium = 1.0
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleToggleStatus = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 'active' ? '下架' : '上架'}该产品吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    row.status = row.status === 'active' ? 'inactive' : 'active'
    ElMessage.success(`${row.status === 'active' ? '上架' : '下架'}成功`)
  } catch {
    // 用户取消操作
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用API保存数据
        await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
        ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
        dialogVisible.value = false
        
        if (!isEdit.value) {
          products.value.unshift({
            id: Date.now(),
            ...formData,
            status: 'active'
          })
        } else {
          const index = products.value.findIndex(p => p.id === formData.id)
          if (index !== -1) {
            products.value[index] = { ...products.value[index], ...formData }
          }
        }
      } finally {
        submitting.value = false
      }
    }
  })
}
</script>

<style scoped>
.products-page {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 