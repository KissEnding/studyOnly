<template>
  <div class="profile-page">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <div class="header-actions">
            <el-tag :type="accountStatusType">{{ accountStatusText }}</el-tag>
            <template v-if="!isEditing">
              <el-button type="primary" @click="handleEdit">编辑</el-button>
            </template>
            <template v-else>
              <el-button @click="cancelEdit">取消</el-button>
              <el-button type="primary" @click="saveProfile" :loading="saving">
                保存
              </el-button>
            </template>
          </div>
        </div>
      </template>

      <div class="profile-content">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="100px"
          :disabled="!isEditing"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="formData.name" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idCard">
                <el-input v-model="formData.idCard" :disabled="true" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="formData.contactPhone" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所在地区" prop="location">
                <template v-if="!isEditing">
                  <span>{{ formData.location }}</span>
                </template>
                <template v-else>
                  <el-cascader
                    v-model="selectedRegion"
                    :options="regionOptions"
                    :props="regionProps"
                    @change="handleRegionChange"
                  />
                </template>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 认证状态信息 -->
          <el-divider>认证状态</el-divider>
          <div class="status-info">
            <el-tag :type="accountStatusType" size="large">
              {{ accountStatusText }}
            </el-tag>
            <p v-if="formData.auditComment" class="audit-comment">
              审核意见：{{ formData.auditComment }}
            </p>
          </div>

          <!-- 户口本照片 -->
          <el-divider>户口本照片</el-divider>
          <div class="document-upload">
            <el-image
              v-if="formData.householdDoc"
              :src="formData.householdDoc"
              :preview-src-list="[formData.householdDoc]"
              fit="cover"
              class="document-image"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
            <el-upload
              v-if="isEditing"
              class="document-uploader"
              action="/api/upload"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
            >
              <el-button type="primary">
                {{ formData.householdDoc ? '重新上传' : '上传照片' }}
              </el-button>
            </el-upload>
          </div>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getFarmerProfile } from '@/api/farmer'
import request from '@/utils/request'

const store = useStore()
const formRef = ref(null)
const isEditing = ref(false)
const saving = ref(false)

// 表单数据
const formData = ref({
  name: '',
  idCard: '',
  contactPhone: '',
  location: '',
  householdDoc: '',
  status: '',
  auditComment: ''
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请选择所在地区', trigger: 'change' }
  ]
}

// 地区选择相关
const selectedRegion = ref([])
const regionOptions = ref([])
const regionProps = {
  value: 'code',
  label: 'name',
  children: 'children',
  checkStrictly: true,
  emitPath: false
}

// 获取地区数据并设置选中值
const initRegionData = async (location) => {
  try {
    // 获取省份数据
    const provinces = await request({
      url: '/api/regions/provinces',
      method: 'get'
    })
    
    // 获取城市数据
    const cities = await request({
      url: '/api/regions/cities',
      method: 'get'
    })
    
    // 构建地区数据树
    regionOptions.value = provinces.map(province => ({
      code: province.code,
      name: province.name,
      children: cities.filter(city => city.code.startsWith(province.code.substring(0, 2))).map(city => ({
        code: city.code,
        name: city.name,
        children: []
      }))
    }))

    console.log('Region options loaded:', regionOptions.value)

    // 如果有地区信息，设置选中值
    if (location) {
      const parts = location.split(' ')
      const province = regionOptions.value.find(p => p.name === parts[0])
      if (province) {
        const city = province.children.find(c => c.name === parts[1])
        if (city) {
          selectedRegion.value = [province.code, city.code]
          console.log('Set selected region:', selectedRegion.value)
        }
      }
    }
  } catch (error) {
    console.error('Failed to init region data:', error)
  }
}

// 账号状态
const accountStatusType = computed(() => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return types[formData.value.status] || 'info'
})

const accountStatusText = computed(() => {
  const texts = {
    pending: '待审核',
    approved: '已认证',
    rejected: '已驳回'
  }
  return texts[formData.value.status] || formData.value.status
})

const loading = ref(false)

// 保存原始数据的副本
const originalData = ref(null)

const fetchProfile = async () => {
  try {
    loading.value = true
    console.log('Fetching farmer profile...')
    const response = await getFarmerProfile()
    console.log('Received profile:', response)
    
    // 保存原始数据
    originalData.value = { ...response }
    
    // 更新表单数据
    if (response) {
      formData.value = {
        name: response.name || '',
        idCard: response.idCard || '',
        contactPhone: response.contactPhone || '',
        location: response.location || '',
        householdDoc: response.householdDoc || '',
        status: response.status || '',
        auditComment: response.auditComment || ''
      }
    }
  } catch (error) {
    console.error('Failed to fetch profile:', error)
    ElMessage.error('获取个人信息失败')
  } finally {
    loading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(async () => {
  await fetchProfile()  // 直接获取个人信息，initRegionData 会在 fetchProfile 中调用
})

// 处理函数
const handleEdit = async () => {
  // 在进入编辑模式时初始化地区数据
  await initRegionData(formData.value.location)
  isEditing.value = true
}

const cancelEdit = () => {
  isEditing.value = false
  // 恢复原始数据
  if (originalData.value) {
    formData.value = { ...originalData.value }
  }
  // 清除选中的地区
  selectedRegion.value = []
  formRef.value?.clearValidate()
}

const saveProfile = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    saving.value = true
    
    // TODO: 调用API保存数据
    await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
    
    ElMessage.success('保存成功')
    isEditing.value = false
  } catch (error) {
    console.error('Validation failed:', error)
  } finally {
    saving.value = false
  }
}

// 处理地区选择变化
const handleRegionChange = (value) => {
  console.log('Region changed:', value)
  if (value && value.length > 0) {
    const getRegionName = (code) => {
      for (const province of regionOptions.value) {
        if (province.code === code) return province.name
        for (const city of province.children) {
          if (city.code === code) return city.name
        }
      }
      return ''
    }

    const names = value.map(getRegionName).filter(Boolean)
    formData.value.location = names.join(' ')
    console.log('Location updated:', formData.value.location)
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  formData.value.householdDoc = response.url
  ElMessage.success('上传成功')
}
</script>

<style scoped>
.profile-page {
  padding: 20px;
}

.profile-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.profile-content {
  padding: 20px 0;
}

.status-info {
  text-align: center;
  padding: 20px 0;
}

.audit-comment {
  margin-top: 10px;
  color: #909399;
}

.document-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
}

.document-image {
  width: 240px;
  height: 160px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  object-fit: contain;
  background-color: #f5f7fa;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  height: 100%;
}

.document-uploader {
  text-align: center;
}

@media screen and (max-width: 768px) {
  .el-form-item {
    margin-bottom: 22px;
  }
  
  .document-image {
    width: 100%;
    height: 150px;
  }
}

/* 添加地区文本显示样式 */
.el-form-item span {
  line-height: 32px;  /* 与输入框高度保持一致 */
  color: #606266;
}
</style> 