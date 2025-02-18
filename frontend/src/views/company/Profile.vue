<template>
  <div class="profile-page">
    <div class="page-header">
      <h2>公司信息</h2>
    </div>

    <el-card>
      <el-form 
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="公司名称" prop="companyName">
          <el-input v-model="formData.companyName" disabled />
        </el-form-item>
        <el-form-item label="统一社会信用代码" prop="creditCode">
          <el-input v-model="formData.creditCode" disabled />
        </el-form-item>
        <el-form-item label="公司类型" prop="type">
          <el-input :value="getCompanyTypeText(formData.type)" disabled />
        </el-form-item>
        <el-form-item 
          label="所属总公司" 
          prop="parentCompany"
          v-if="formData.type === 'branch'"
        >
          <el-input v-model="formData.parentCompany" disabled />
        </el-form-item>
        <el-form-item label="所在地区" prop="location">
          <el-input v-model="formData.location" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="formData.contactPerson" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" />
        </el-form-item>
        <el-form-item label="营业执照">
          <div class="license-preview">
            <img :src="formData.license" alt="营业执照" class="preview-image">
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const formRef = ref(null)
const formData = ref({
  companyName: '某某保险公司杭州分公司',
  creditCode: '91330000XXXXXXXXXX',
  type: 'branch',
  parentCompany: '某某保险公司',
  location: '浙江省杭州市西湖区',
  contactPerson: '王经理',
  contactPhone: '0571-88888888',
  license: '/api/uploads/company/license.jpg'
})

const rules = {
  location: [{ required: true, message: '请输入所在地区', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^(\d{3,4}-?)?\d{7,8}$|^1[3-9]\d{9}$/, message: '请输入正确的电话号码', trigger: 'blur' }
  ]
}

const getCompanyTypeText = (type) => {
  return type === 'headquarters' ? '总公司' : '分公司'
}

const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // TODO: 调用API保存数据
        await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
        ElMessage.success('保存成功')
      } catch (error) {
        ElMessage.error('保存失败')
      }
    }
  })
}
</script>

<style scoped>
.profile-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.license-preview {
  width: 300px;
}

.preview-image {
  width: 100%;
  border-radius: 4px;
}
</style> 