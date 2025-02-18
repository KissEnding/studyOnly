<template>
  <div class="insurance-application">
    <div class="page-header">
      <el-button class="back-button" icon="ArrowLeft" @click="$router.back()">返回</el-button>
      <h2>投保申请</h2>
    </div>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="10" animated />
    </div>

    <div v-else-if="!product" class="empty-state">
      <el-empty description="产品不存在" />
    </div>

    <div v-else class="content">
      <!-- 产品信息 -->
      <el-card class="product-info">
        <template #header>
          <div class="card-header">
            <span>产品信息</span>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="产品名称">{{ product.productName }}</el-descriptions-item>
          <el-descriptions-item label="保险公司">{{ product.company.companyName }}</el-descriptions-item>
          <el-descriptions-item label="承保作物">{{ product.cropType }}</el-descriptions-item>
          <el-descriptions-item label="基础保费">{{ formatAmount(product.basePremium) }}/{{ getPremiumUnit(product.cropType) }}</el-descriptions-item>
          <el-descriptions-item label="保险期限">{{ formatDate(product.startDate) }} 至 {{ formatDate(product.endDate) }}</el-descriptions-item>
          <el-descriptions-item label="承保区域">{{ formatRegion(product) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 投保信息表单 -->
      <el-card class="application-form">
        <template #header>
          <div class="card-header">
            <span>投保信息</span>
          </div>
        </template>
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="120px"
          class="application-form"
          @submit.prevent
        >
          <el-form-item label="投保面积" prop="area">
            <el-input-number
              v-model="form.area"
              :min="1"
              :precision="2"
              :step="1"
              style="width: 200px"
            />
            <span class="unit-label">亩</span>
          </el-form-item>

          <el-form-item label="种植地址" required>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item prop="cityCode" class="no-label">
                  <el-select 
                    v-model="form.cityCode" 
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
                    v-model="form.districtCode" 
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

          <el-form-item label="详细地址" prop="addressDetail">
            <el-input
              v-model="form.addressDetail"
              type="textarea"
              :rows="2"
              placeholder="请输入详细地址"
            />
          </el-form-item>

          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" style="width: 200px" />
          </el-form-item>

          <el-form-item label="备注" prop="remarks">
            <el-input
              v-model="form.remarks"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息（选填）"
            />
          </el-form-item>
        </el-form>

        <!-- 费用计算 -->
        <div class="price-calculation">
          <div class="price-item">
            <span class="label">基础保费：</span>
            <span class="value">{{ formatAmount(basePrice) }}</span>
          </div>
          <div class="price-item" v-if="discount < 1">
            <span class="label">优惠折扣：</span>
            <span class="value">{{ (discount * 10).toFixed(1) }}折</span>
          </div>
          <div class="price-item total">
            <span class="label">应付金额：</span>
            <span class="value highlight">{{ formatAmount(finalPrice) }}</span>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            提交申请
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail, submitOrder } from '@/api/market'
import { createApplication } from '@/api/policy'
import { formatAmount } from '@/utils/format'
import { formatDate } from '@/utils/date'
import request from '@/utils/request'

export default {
  name: 'InsuranceApplication',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const submitting = ref(false)
    const product = ref(null)
    const formRef = ref(null)
    const form = ref({
      area: 1,
      cityCode: '',
      districtCode: '',
      addressDetail: '',
      phone: '',
      remarks: ''
    })

    // 地区数据
    const cities = ref([])
    const districts = ref([])

    // 添加浙江省的编码常量
    const ZHEJIANG_PROVINCE_CODE = '330000'

    // 获取城市列表
    const fetchCities = async (provinceCode) => {
      try {
        if (!provinceCode) {
          cities.value = []
          return
        }
        const response = await request({
          url: '/api/regions/cities',
          method: 'get',
          params: { provinceCode }
        })
        console.log('Cities response:', response)
        if (Array.isArray(response)) {
          cities.value = response
        } else {
          cities.value = []
          ElMessage.warning('城市数据格式不正确')
        }
      } catch (error) {
        console.error('Failed to fetch cities:', error)
        ElMessage.error('获取城市列表失败')
        cities.value = []
      }
    }

    // 获取区县列表
    const fetchDistricts = async (cityCode) => {
      if (!cityCode) {
        districts.value = []
        return
      }
      
      try {
        const response = await request({
          url: '/api/regions/districts',
          method: 'get',
          params: { cityCode }
        })
        console.log('Districts response:', response)
        if (Array.isArray(response)) {
          districts.value = response
        } else {
          districts.value = []
          ElMessage.warning('区县数据格式不正确')
        }
      } catch (error) {
        console.error('Failed to fetch districts:', error)
        ElMessage.error('获取区县列表失败')
        districts.value = []
      }
    }

    // 城市选择改变时
    const handleCityChange = () => {
      form.value.districtCode = ''
      districts.value = [] // 清空区县数据
      if (form.value.cityCode) {
        fetchDistricts(form.value.cityCode)
      }
    }

    // 组件挂载时获取城市列表
    onMounted(() => {
      // 直接获取浙江省的城市列表
      fetchCities(ZHEJIANG_PROVINCE_CODE)
    })

    // 表单验证规则
    const rules = {
      area: [
        { required: true, message: '请输入投保面积', trigger: 'blur' },
        { type: 'number', min: 1, message: '面积必须大于0', trigger: 'blur' }
      ],
      cityCode: [
        { required: true, message: '请选择城市', trigger: 'change' }
      ],
      districtCode: [
        { required: true, message: '请选择区县', trigger: 'change' }
      ],
      addressDetail: [
        { required: true, message: '请输入详细地址', trigger: 'blur' },
        { min: 5, message: '详细地址至少5个字符', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
      ],
      remarks: [
        { max: 200, message: '备注不能超过200个字符', trigger: 'blur' }
      ]
    }

    // 计算基础价格
    const basePrice = computed(() => {
      if (!product.value || !form.value.area) return 0
      return product.value.basePremium * form.value.area
    })

    // 计算折扣
    const discount = computed(() => {
      if (!product.value || !product.value.premiumLadders || !form.value.area) return 1
      
      const ladders = [...product.value.premiumLadders].sort((a, b) => b.minArea - a.minArea)
      const ladder = ladders.find(l => form.value.area >= l.minArea)
      return ladder ? ladder.discountRate : 1
    })

    // 计算最终价格
    const finalPrice = computed(() => {
      return basePrice.value * discount.value
    })

    // 加载产品详情
    const fetchProductDetail = async (id) => {
      loading.value = true
      try {
        const response = await getProductDetail(id)
        console.log('Product detail response:', response)
        if (response && response.data) {
          product.value = response.data
        } else if (response) {
          product.value = response
        } else {
          ElMessage.error('产品不存在')
          router.back()
        }
      } catch (error) {
        console.error('获取产品详情失败:', error)
        ElMessage.error(error.response?.data?.message || '获取产品详情失败')
        router.back()
      } finally {
        loading.value = false
      }
    }

    // 提交申请
    const handleSubmit = async () => {
      if (!product.value) return

      try {
        await formRef.value.validate()
      } catch (error) {
        console.error('表单验证失败:', error)
        return
      }
      
      submitting.value = true
      try {
        // 处理地区数据
        const city = cities.value.find(city => city.code === form.value.cityCode)
        const district = districts.value.find(district => district.code === form.value.districtCode)
        const location = `浙江省${city?.name || ''}${district?.name || ''}`

        const applicationData = {
          productId: product.value.id,
          area: Number(form.value.area),
          contactPerson: form.value.contactPerson || localStorage.getItem('userName'),
          contactPhone: form.value.phone,
          address: location + (form.value.addressDetail || ''),
          remarks: form.value.remarks || '',
          premium: Number(finalPrice.value),
          cityCode: form.value.cityCode,
          districtCode: form.value.districtCode,
          coverage: Number(form.value.area) * 1000 // 默认每亩1000元保额
        }
        
        console.log('Submitting application data:', applicationData)
        const response = await submitOrder(applicationData)
        console.log('Submit response:', response)
        ElMessage.success('投保申请提交成功')
        router.push('/farmer/policies')
      } catch (error) {
        console.error('提交申请失败:', error)
        if (error.response?.data?.message) {
          ElMessage.error(error.response.data.message)
        } else {
          ElMessage.error('提交申请失败，请稍后重试')
        }
      } finally {
        submitting.value = false
      }
    }

    // 初始化
    const init = async () => {
      const id = router.currentRoute.value.params.id
      console.log('Product ID:', id)
      if (id) {
        await fetchProductDetail(id)
        // 获取城市列表
        await fetchCities(ZHEJIANG_PROVINCE_CODE)
      } else {
        ElMessage.error('产品ID不存在')
        router.back()
      }
    }

    // 执行初始化
    onMounted(() => {
      init()
    })

    return {
      loading,
      submitting,
      product,
      form,
      formRef,
      rules,
      cities,
      districts,
      basePrice,
      discount,
      finalPrice,
      handleCityChange,
      handleSubmit,
      formatAmount,
      formatDate,
      getPremiumUnit: (cropType) => {
        if (!cropType) return '亩'
        switch (cropType) {
          case '畜牧养殖':
            return '头'
          case '家禽':
            return '只'
          case '水产养殖':
            return '尾'
          default:
            return '亩'
        }
      },
      formatRegion: (product) => {
        const parts = []
        if (product.provinceCode) parts.push('浙江省')
        if (product.cityCode) {
          const cityMap = {
            '330100': '杭州市',
            '330200': '宁波市',
            '330300': '温州市',
            '330400': '嘉兴市',
            '330500': '湖州市',
            '330600': '绍兴市',
            '330700': '金华市',
            '330800': '衢州市',
            '330900': '舟山市',
            '331000': '台州市',
            '331100': '丽水市'
          }
          parts.push(cityMap[product.cityCode] || '未知城市')
        }
        if (product.districtCode) {
          parts.push('具体区县')
        }
        return parts.join(' - ') || '全省'
      }
    }
  }
}
</script>

<style scoped>
.insurance-application {
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.back-button {
  margin-right: 20px;
}

.content {
  max-width: 1000px;
  margin: 0 auto;
}

.product-info {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.application-form {
  margin-bottom: 20px;
}

.unit-label {
  margin-left: 10px;
  color: #606266;
}

.region-select {
  width: 100%;
}

.no-label :deep(.el-form-item__label) {
  display: none;
}

.price-calculation {
  margin: 20px 0;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.price-item:last-child {
  margin-bottom: 0;
}

.price-item.total {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #dcdfe6;
}

.price-item .label {
  color: #606266;
}

.price-item .value {
  font-weight: bold;
}

.price-item .value.highlight {
  color: #f56c6c;
  font-size: 20px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}
</style> 