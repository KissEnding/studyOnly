<template>
  <div class="insurance-market">
    <div class="page-header">
      <h2>保险市场</h2>
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索产品名称"
          clearable
          @input="handleSearch"
          style="width: 200px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="selectedProvince"
          placeholder="选择省份"
          clearable
          @change="handleProvinceChange"
          style="width: 160px"
        >
          <el-option
            v-for="item in provinces"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          />
        </el-select>
        <el-select
          v-model="selectedCity"
          placeholder="选择城市"
          clearable
          :disabled="!selectedProvince"
          @change="handleCityChange"
          style="width: 160px"
        >
          <el-option
            v-for="item in cities"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          />
        </el-select>
      </div>
    </div>

    <div class="product-grid" v-loading="loading">
      <el-card v-for="product in filteredProducts" :key="product.id" class="product-card">
        <template #header>
          <div class="card-header">
            <span class="product-name">{{ product.productName }}</span>
            <el-tag size="small" type="success">{{ product.company.companyName }}</el-tag>
          </div>
        </template>
        <div class="product-content">
          <div class="product-info">
            <div class="info-item">
              <span class="label">承保作物：</span>
              <div class="crop-tags">
                <el-tag
                  v-for="crop in product.crops"
                  :key="crop.id"
                  size="small"
                  class="crop-tag"
                >
                  {{ crop.cropName }}
                </el-tag>
              </div>
            </div>
            <div class="info-item">
              <span class="label">保险区域：</span>
              <span>{{ formatArea(product.provinceCode, product.cityCode, product.districtCode) }}</span>
            </div>
            <div class="info-item">
              <span class="label">保险期限：</span>
              <span>{{ formatDate(product.startDate) }} 至 {{ formatDate(product.endDate) }}</span>
            </div>
            <div class="info-item">
              <span class="label">基础保费：</span>
              <span class="premium">{{ formatMoney(product.basePremium) }}/{{ getPremiumUnit(product.cropType) }}</span>
            </div>
            <div class="info-item">
              <span class="label">剩余额度：</span>
              <span class="amount">{{ formatMoney(product.stockAmount) }}</span>
            </div>
          </div>
          <div class="product-actions">
            <el-button type="primary" @click="handlePurchase(product)">立即投保</el-button>
            <el-button link type="primary" @click="showDetails(product)">查看详情</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 产品详情对话框 -->
    <el-dialog
      v-model="detailsVisible"
      title="保险产品详情"
      width="600px"
    >
      <div v-if="selectedProduct" class="product-details">
        <div class="detail-item">
          <span class="label">产品名称：</span>
          <span>{{ selectedProduct.productName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">承保公司：</span>
          <span>{{ selectedProduct.company.companyName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">承保作物：</span>
          <div class="crop-tags">
            <el-tag
              v-for="crop in selectedProduct.crops"
              :key="crop.id"
              size="small"
              class="crop-tag"
            >
              {{ crop.cropName }}
            </el-tag>
          </div>
        </div>
        <div class="detail-item">
          <span class="label">保险区域：</span>
          <span>{{ formatArea(selectedProduct.provinceCode, selectedProduct.cityCode, selectedProduct.districtCode) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">保险期限：</span>
          <span>{{ formatDate(selectedProduct.startDate) }} 至 {{ formatDate(selectedProduct.endDate) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">基础保费：</span>
          <span class="premium">{{ formatMoney(selectedProduct.basePremium) }}/{{ getPremiumUnit(selectedProduct.cropType) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">剩余额度：</span>
          <span class="amount">{{ formatMoney(selectedProduct.stockAmount) }}</span>
        </div>
        <div class="detail-item" v-if="selectedProduct.premiumLadders?.length">
          <span class="label">优惠政策：</span>
          <div class="ladder-list">
            <div v-for="(ladder, index) in selectedProduct.premiumLadders" :key="index" class="ladder-item">
              {{ ladder.minArea }}亩及以上：{{ formatPercent(ladder.discountRate) }}
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 投保对话框 -->
    <el-dialog
      v-model="purchaseVisible"
      title="投保申请"
      width="500px"
    >
      <el-form
        v-if="selectedProduct"
        ref="purchaseFormRef"
        :model="purchaseForm"
        :rules="purchaseRules"
        label-width="100px"
      >
        <el-form-item label="投保面积" prop="area">
          <el-input-number
            v-model="purchaseForm.area"
            :min="0.01"
            :precision="2"
            :step="1"
            style="width: 200px"
          />
          <span class="unit">{{ getPremiumUnit(selectedProduct.cropType) }}</span>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="purchaseForm.contactPerson" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="purchaseForm.contactPhone" />
        </el-form-item>
        <el-form-item label="预计保费">
          <span class="premium">{{ formatMoney(calculatePremium) }}元</span>
          <div class="premium-tip" v-if="appliedDiscount">
            已享受{{ formatPercent(appliedDiscount) }}优惠
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="purchaseVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPurchase" :loading="submitting">
            确认投保
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getMarketProducts } from '@/api/market'
import { getProvinces, getCities } from '@/api/area'
import { formatDate } from '@/utils/date'
import { formatMoney, formatPercent } from '@/utils/format'

// 响应式数据
const loading = ref(false)
const products = ref([])
const searchQuery = ref('')
const selectedProvince = ref('')
const selectedCity = ref('')
const provinces = ref([])
const cities = ref([])
const detailsVisible = ref(false)
const purchaseVisible = ref(false)
const selectedProduct = ref(null)
const submitting = ref(false)
const purchaseFormRef = ref(null)

// 投保表单数据
const purchaseForm = ref({
  area: 1,
  contactPerson: '',
  contactPhone: ''
})

// 表单验证规则
const purchaseRules = {
  area: [
    { required: true, message: '请输入投保面积', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '投保面积必须大于0', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ]
}

// 计算优惠后的保费
const calculatePremium = computed(() => {
  if (!selectedProduct.value || !purchaseForm.value.area) return 0
  
  const area = purchaseForm.value.area
  const basePremium = selectedProduct.value.basePremium
  let discountRate = 1

  // 查找适用的阶梯优惠
  if (selectedProduct.value.premiumLadders) {
    const applicableLadders = selectedProduct.value.premiumLadders
      .filter(ladder => area >= ladder.minArea)
      .sort((a, b) => b.minArea - a.minArea)

    if (applicableLadders.length > 0) {
      discountRate = applicableLadders[0].discountRate
    }
  }

  return area * basePremium * discountRate
})

// 计算适用的优惠折扣
const appliedDiscount = computed(() => {
  if (!selectedProduct.value || !purchaseForm.value.area) return null
  
  const area = purchaseForm.value.area
  if (selectedProduct.value.premiumLadders) {
    const applicableLadders = selectedProduct.value.premiumLadders
      .filter(ladder => area >= ladder.minArea)
      .sort((a, b) => b.minArea - a.minArea)

    if (applicableLadders.length > 0) {
      return applicableLadders[0].discountRate
    }
  }
  return null
})

// 过滤产品列表
const filteredProducts = computed(() => {
  return products.value.filter(product => {
    const matchQuery = !searchQuery.value || 
      product.productName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      product.company.companyName.toLowerCase().includes(searchQuery.value.toLowerCase())
    
    const matchProvince = !selectedProvince.value || product.provinceCode === selectedProvince.value
    const matchCity = !selectedCity.value || product.cityCode === selectedCity.value
    
    return matchQuery && matchProvince && matchCity
  })
})

// 加载产品列表
const loadProducts = async () => {
  try {
    loading.value = true
    const response = await getMarketProducts()
    products.value = response
  } catch (error) {
    console.error('Failed to load products:', error)
    ElMessage.error('加载产品列表失败')
  } finally {
    loading.value = false
  }
}

// 加载省份数据
const loadProvinces = async () => {
  try {
    provinces.value = await getProvinces()
  } catch (error) {
    console.error('Failed to load provinces:', error)
    ElMessage.error('加载省份数据失败')
  }
}

// 处理省份变化
const handleProvinceChange = async (provinceCode) => {
  selectedCity.value = ''
  cities.value = []
  
  if (provinceCode) {
    try {
      cities.value = await getCities(provinceCode)
    } catch (error) {
      console.error('Failed to load cities:', error)
      ElMessage.error('加载城市数据失败')
    }
  }
}

// 处理城市变化
const handleCityChange = () => {
  // 可以添加额外的处理逻辑
}

// 处理搜索
const handleSearch = () => {
  // 可以添加防抖逻辑
}

// 格式化地区显示
const formatArea = (provinceCode, cityCode, districtCode) => {
  const province = provinces.value.find(p => p.code === provinceCode)?.name || ''
  const city = cities.value.find(c => c.code === cityCode)?.name || ''
  return [province, city].filter(Boolean).join(' / ')
}

// 显示产品详情
const showDetails = (product) => {
  selectedProduct.value = product
  detailsVisible.value = true
}

// 处理投保
const handlePurchase = (product) => {
  selectedProduct.value = product
  purchaseForm.value = {
    area: 1,
    contactPerson: '',
    contactPhone: ''
  }
  purchaseVisible.value = true
}

// 提交投保
const submitPurchase = async () => {
  if (!purchaseFormRef.value) return
  
  try {
    await purchaseFormRef.value.validate()
    submitting.value = true
    
    const submitData = {
      productId: selectedProduct.value.id,
      area: purchaseForm.value.area,
      contactPerson: purchaseForm.value.contactPerson,
      contactPhone: purchaseForm.value.contactPhone
    }
    
    await submitOrder(submitData)
    ElMessage.success('投保申请提交成功')
    purchaseVisible.value = false
    await loadProducts()  // 刷新产品列表
  } catch (error) {
    console.error('Failed to submit purchase:', error)
    ElMessage.error(error.response?.data?.message || '投保申请提交失败')
  } finally {
    submitting.value = false
  }
}

const getPremiumUnit = (cropType) => {
  if (!cropType) return '亩'
  
  // 根据作物类别判断单位
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
}

// 页面加载时执行
onMounted(() => {
  loadProducts()
  loadProvinces()
})
</script>

<style scoped>
.insurance-market {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 16px 0;
  font-size: 24px;
  color: #303133;
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.product-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.product-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
}

.info-item .label {
  color: #909399;
  min-width: 70px;
}

.crop-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.crop-tag {
  margin: 2px;
}

.premium, .amount {
  color: #f56c6c;
  font-weight: bold;
}

.product-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.product-details {
  padding: 20px;
}

.detail-item {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
}

.detail-item .label {
  color: #909399;
  min-width: 100px;
}

.ladder-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ladder-item {
  color: #f56c6c;
}

.premium-tip {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 4px;
}

.unit {
  margin-left: 8px;
  color: #606266;
}

@media screen and (max-width: 768px) {
  .insurance-market {
    padding: 10px;
  }
  
  .search-bar {
    flex-direction: column;
  }
  
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style> 