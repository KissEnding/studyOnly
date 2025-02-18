<template>
  <div class="register-container">
    <div class="header">
      <div class="logo-container">
        <img src="@/assets/platformLogo.png" alt="logo" class="platform-logo">
        <div class="platform-title">
          <h1 class="chinese-title">农安心保险服务平台</h1>
          <p class="english-title">Agricultural Insurance Service Platform</p>
        </div>
      </div>
    </div>
    
    <div class="register-box">
      <div class="register-header">
        <h2>{{ isCompany ? '保险公司入驻' : '农户注册' }}</h2>
        <p>{{ isCompany ? '欢迎加入农业保险服务平台' : '欢迎使用农业保险服务' }}</p>
      </div>

      <el-steps :active="currentStep" finish-status="success" class="register-steps">
        <el-step title="填写信息" />
        <el-step title="上传资料" />
        <el-step title="等待审核" />
      </el-steps>

      <div v-if="currentStep === 0" class="step-content">
        <el-form 
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-position="top"
        >
          <!-- 保险公司表单 -->
          <template v-if="isCompany">
            <el-form-item label="公司名称" prop="companyName">
              <el-input v-model="formData.companyName" />
            </el-form-item>
            <el-form-item label="统一社会信用代码" prop="creditCode">
              <el-input v-model="formData.creditCode" />
            </el-form-item>
            <el-form-item label="公司类型" prop="type">
              <el-radio-group v-model="formData.type">
                <el-radio label="headquarters">总公司</el-radio>
                <el-radio label="branch">省级分公司</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="formData.type === 'branch'" label="所属总公司" prop="parentCompany">
              <el-select v-model="formData.parentCompany" filterable>
                <el-option 
                  v-for="company in parentCompanies"
                  :key="company.id"
                  :label="company.name"
                  :value="company.id"
                />
              </el-select>
            </el-form-item>
          </template>

          <!-- 农户表单 -->
          <template v-else>
            <el-form-item label="户主姓名" prop="name">
              <el-input v-model="formData.name" />
            </el-form-item>
            <el-form-item label="身份证号码" prop="idCard">
              <el-input v-model="formData.idCard" />
            </el-form-item>
          </template>

          <!-- 共同字段 -->
          <el-form-item label="所在地区" required>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item prop="cityCode" class="no-label">
                  <el-select 
                    v-model="formData.cityCode" 
                    placeholder="请选择城市"
                    @change="handleCityChange"
                    class="region-select"
                  >
                    <el-option
                      v-for="city in cities"
                      :key="city.code"
                      :label="city.name"
                      :value="city.code"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="districtCode" class="no-label">
                  <el-select 
                    v-model="formData.districtCode" 
                    placeholder="请选择区县"
                    class="region-select"
                  >
                    <el-option
                      v-for="district in districts"
                      :key="district.code"
                      :label="district.name"
                      :value="district.code"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="formData.contactPhone" />
          </el-form-item>
          <el-form-item label="登录用户名" prop="username">
            <el-input v-model="formData.username" />
          </el-form-item>
          <el-form-item label="登录密码" prop="password">
            <el-input type="password" v-model="formData.password" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input type="password" v-model="formData.confirmPassword" show-password />
          </el-form-item>
        </el-form>
      </div>

      <div v-else-if="currentStep === 1" class="step-content">
        <el-upload
          class="upload-area"
          drag
          :action="uploadUrl"
          :on-success="handleUploadSuccess"
          :before-upload="beforeUpload"
          :headers="uploadHeaders"
          :show-file-list="false"
          name="file"
        >
          <template v-if="!formData.householdDoc">
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <div class="el-upload__tip">
              {{ isCompany ? '请上传营业执照、经营许可证等资质文件' : '请上传户口本扫描件' }}
            </div>
          </template>
          <template v-else>
            <div class="upload-preview">
              <img :src="formData.householdDoc" alt="预览图" class="preview-image">
              <div class="preview-actions">
                <el-button type="primary" size="small" @click.stop="handleReupload">重新上传</el-button>
              </div>
            </div>
          </template>
        </el-upload>
      </div>

      <div v-else class="step-content">
        <div class="success-info">
          <el-icon class="success-icon"><circle-check-filled /></el-icon>
          <h3>提交成功</h3>
          <p>我们会尽快审核您的申请，请耐心等待</p>
          <p class="tip">审核结果将通过短信通知您</p>
        </div>
      </div>

      <div class="step-actions">
        <el-button v-if="currentStep > 0" @click="currentStep--">上一步</el-button>
        <el-button 
          v-if="currentStep < 2" 
          type="primary" 
          @click="handleNext"
          :loading="loading"
        >
          {{ currentStep === 1 ? '提交' : '下一步' }}
        </el-button>
        <el-button v-else type="primary" @click="$router.push('/login')">返回登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled, CircleCheckFilled, Check } from '@element-plus/icons-vue'
import { registerCompany, registerFarmer } from '@/api/auth'
import { ZHEJIANG_REGIONS } from '@/config/regions'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const currentStep = ref(0)
const loading = ref(false)

const isCompany = computed(() => route.query.type === 'company')

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  idCard: '',
  cityCode: '',
  districtCode: '',
  contactPhone: '',
  companyName: '',
  creditCode: '',
  type: 'headquarters',
  parentCompany: '',
})

// 地区数据
const cities = ref([])
const districts = ref([])

// 添加浙江省的编码常量
const ZHEJIANG_PROVINCE_CODE = '330000';

// 获取城市列表
const fetchCities = async (provinceCode) => {
  try {
    if (!provinceCode) {
      cities.value = [];
      return;
    }
    const response = await axios.get('/api/regions/cities', {
      params: { provinceCode }
    });
    cities.value = response.data;
  } catch (error) {
    console.error('Failed to fetch cities:', error);
    ElMessage.error('获取城市列表失败');
  }
};

// 获取区县列表
const fetchDistricts = async (cityCode) => {
  if (!cityCode) {
    districts.value = []
    return
  }
  
  try {
    const response = await axios.get(`/api/regions/districts`, {
      params: { cityCode }
    })
    districts.value = response.data
  } catch (error) {
    console.error('Failed to fetch districts:', error)
    ElMessage.error('获取区县列表失败')
  }
}

// 城市选择改变时
const handleCityChange = () => {
  formData.districtCode = ''
  if (formData.cityCode) {
    fetchDistricts(formData.cityCode)
  }
}

// 组件挂载时获取城市列表
onMounted(() => {
  // 直接获取浙江省的城市列表
  fetchCities(ZHEJIANG_PROVINCE_CODE);
});

// 添加表单验证规则
const rules = {
  cityCode: [{ required: true, message: '请选择城市', trigger: 'change' }],
  districtCode: [{ required: true, message: '请选择区县', trigger: 'change' }],
  name: [{ required: true, message: '请输入户主姓名', trigger: 'blur' }],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,20}$/, message: '密码必须包含字母和数字，长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== formData.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 处理下一步
const handleNext = async () => {
  if (currentStep.value === 0) {
    if (!formRef.value) return
    
    await formRef.value.validate(async (valid) => {
      if (valid) {
        // 处理地区数据
        const city = cities.value.find(city => city.code === formData.cityCode)
        const district = districts.value.find(district => district.code === formData.districtCode)
        formData.location = `浙江省${city?.name}${district?.name}`
        currentStep.value++
      }
    })
  } else if (currentStep.value === 1) {
    try {
      loading.value = true
      // 提交注册
      console.log('Submitting registration:', formData);  // 添加日志
      if (isCompany.value) {
        await registerCompany({
          username: formData.username,
          password: formData.password,
          companyName: formData.companyName,
          creditCode: formData.creditCode,
          type: formData.type,
          parentCompany: formData.parentCompany,
          location: formData.location,
          contactPhone: formData.contactPhone,
          businessLicense: formData.householdDoc
        });
      } else {
        await registerFarmer({
          username: formData.username,
          password: formData.password,
          name: formData.name,
          idCard: formData.idCard,
          location: formData.location,
          contactPhone: formData.contactPhone,
          householdDoc: formData.householdDoc
        });
      }
      currentStep.value++
    } catch (error) {
      console.error('Registration failed:', error);  // 添加错误日志
      ElMessage.error(error.response?.data || '注册失败')
    } finally {
      loading.value = false
    }
  }
}

// 文件上传相关
const uploadUrl = computed(() => {
  return `/api/upload/${isCompany.value ? 'company' : 'farmer'}`
})

const uploadHeaders = computed(() => ({
  // 如果需要的话，可以添加认证头
  // 'Authorization': `Bearer ${store.state.token}`
}))

const handleUploadSuccess = (response) => {
  console.log('Upload response:', response);  // 添加日志
  formData.householdDoc = response.url
  ElMessage.success('文件上传成功')
}

const handleReupload = () => {
  formData.householdDoc = ''  // 清空当前文件
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background: url('@/assets/back.png') center/cover no-repeat fixed;
  padding: 20px;
  position: relative;
}

.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(3px);
}

.register-box {
  max-width: 800px;
  margin: 40px auto;
  padding: 40px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h2 {
  font-size: 28px;
  color: var(--el-color-primary);
  margin: 0 0 10px;
}

.register-steps {
  margin-bottom: 40px;
}

.step-content {
  min-height: 400px;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
}

.upload-area {
  width: 100%;
  border: 2px dashed #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: var(--el-color-primary);
}

.upload-preview {
  padding: 20px;
  text-align: center;
}

.preview-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.preview-actions {
  margin-top: 20px;
}

.el-upload__tip {
  font-size: 12px;
  color: #666;
  margin-top: 10px;
}

.el-icon--upload {
  font-size: 48px;
  color: #909399;
  margin-bottom: 10px;
}

.el-upload__text {
  color: #606266;
  font-size: 14px;
  margin: 10px 0;
}

.el-upload__text em {
  color: var(--el-color-primary);
  font-style: normal;
}

.success-info {
  text-align: center;
  padding: 40px 0;
}

.success-icon {
  font-size: 72px;
  color: var(--el-color-success);
  margin-bottom: 20px;
}

.success-info h3 {
  font-size: 24px;
  color: var(--el-color-success);
  margin: 0 0 10px;
}

.success-info .tip {
  color: #999;
  margin-top: 20px;
}

.header {
  position: relative;
  padding: 20px 50px;
  z-index: 1;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.platform-logo {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.platform-title {
  display: flex;
  flex-direction: column;
}

.chinese-title {
  font-family: "ZCOOL XiaoWei", cursive;
  font-size: 28px;
  color: #1d953f;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.english-title {
  font-size: 12px;
  color: #666;
  margin: 5px 0 0;
}

@media screen and (max-width: 768px) {
  .header {
    padding: 15px;
  }
  
  .platform-logo {
    width: 50px;
    height: 50px;
  }
  
  .chinese-title {
    font-size: 24px;
  }
  
  .english-title {
    font-size: 11px;
  }
}

.region-select {
  width: 100%;
}

.region-select :deep(.el-input__wrapper) {
  background-color: rgba(245, 247, 250, 0.8);
  border: 1px solid #e4e7ed;
  height: 45px;
  transition: all 0.3s ease;
}

.region-select :deep(.el-input__wrapper:hover) {
  border-color: var(--el-color-primary);
}

.region-select :deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 1px var(--el-color-primary) !important;
  background-color: white;
}

:deep(.region-select-dropdown) {
  min-width: 200px !important;
}

:deep(.region-select-dropdown .el-select-dropdown__item) {
  padding: 8px 20px;
  height: 40px;
  line-height: 24px;
}

.region-option {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.region-option .el-icon {
  color: var(--el-color-primary);
}

:deep(.el-select-dropdown__item.selected) {
  color: var(--el-color-primary);
  font-weight: bold;
  background-color: rgba(29, 149, 63, 0.1);
}

:deep(.el-form-item__label) {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.no-label :deep(.el-form-item__label) {
  display: none;
}

.el-form-item.no-label {
  margin-bottom: 0;
}

:deep(.el-form-item__content) {
  margin-left: 0 !important;
}

.region-select {
  width: 100%;
}

/* 确保嵌套的表单项不会有额外的边距 */
.el-form-item :deep(.el-form-item) {
  margin-bottom: 0;
}

/* 调整行间距 */
.el-row {
  margin-bottom: 0;
}
</style> 