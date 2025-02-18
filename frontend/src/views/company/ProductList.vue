<template>
  <div class="product-list">
    <div class="page-header">
      <h2>保险产品管理</h2>
      <el-button type="primary" @click="handleCreate">
        发布新产品
      </el-button>
    </div>

    <el-table
      :data="products"
      v-loading="loading"
      stripe
      style="width: 100%"
    >
      <el-table-column prop="productCode" label="产品编号" width="120" />
      <el-table-column prop="productName" label="产品名称" min-width="150" />
      <el-table-column label="面向用户" width="120">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ formatUserType(row.userType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="农作物" min-width="150">
        <template #default="{ row }">
          <el-tag size="small">{{ row.cropType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="面向地区" min-width="150">
        <template #default="{ row }">
          {{ formatArea(row.provinceCode, row.cityCode, row.districtCode) }}
        </template>
      </el-table-column>
      <el-table-column label="有效期" width="200">
        <template #default="{ row }">
          {{ formatDate(row.startDate) }} 至 {{ formatDate(row.endDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="basePremium" label="基础保费" width="120">
        <template #default="{ row }">
          {{ formatMoney(row.basePremium) }}/亩
        </template>
      </el-table-column>
      <el-table-column prop="stockAmount" label="库存金额" width="120">
        <template #default="{ row }">
          {{ formatMoney(row.stockAmount) }}
        </template>
      </el-table-column>
      <el-table-column prop="visible" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.visible ? 'success' : 'info'">
            {{ row.visible ? '已上架' : '未上架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button 
            link
            type="primary" 
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button 
            link
            type="info" 
            @click="handleCopy(row)"
          >
            复制
          </el-button>
          <el-button 
            link
            :type="row.visible ? 'warning' : 'success'"
            @click="handleToggleVisibility(row)"
          >
            {{ row.visible ? '下架' : '上架' }}
          </el-button>
          <el-button 
            link
            type="danger" 
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 产品表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑产品' : '发布新产品'"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="formData.productName" placeholder="请输入产品名称" />
        </el-form-item>

        <el-form-item label="面向用户" prop="userType">
          <el-select v-model="formData.userType" placeholder="请选择面向用户类型">
            <el-option label="所有用户" value="ALL" />
            <el-option label="新型农民" value="NEW_FARMER" />
            <el-option label="专业农户" value="PROFESSIONAL" />
            <el-option label="农业企业" value="COMPANY" />
          </el-select>
        </el-form-item>

        <el-form-item label="产品介绍" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="6"
            placeholder="请输入产品介绍信息，建议包含以下内容：
1. 产品理念：描述产品的设计初衷和目标
2. 产品特点：列举产品的主要特色和优势
3. 保障范围：详细说明保险覆盖的范围和内容
4. 投保流程：说明投保的具体步骤和注意事项"
          />
        </el-form-item>

        <el-form-item label="承保作物" prop="cropIds">
          <el-select
            v-model="formData.cropIds"
            multiple
            filterable
            placeholder="选择承保作物"
            @change="handleCropChange"
          >
            <el-option-group
              v-for="group in cropGroups"
              :key="group.category"
              :label="group.category"
            >
              <el-option
                v-for="crop in group.crops"
                :key="crop.id"
                :label="crop.cropName"
                :value="crop.id"
              >
                <div class="crop-option">
                  <div class="crop-name">{{ crop.cropName }}</div>
                  <div class="crop-desc">{{ crop.description || '暂无描述' }}</div>
                </div>
              </el-option>
            </el-option-group>
          </el-select>
        </el-form-item>

        <el-form-item label="面向地区" prop="provinceCode">
          <div class="area-select">
            <el-select
              v-model="formData.provinceCode"
              placeholder="请选择省份"
              @change="handleProvinceChange"
            >
              <el-option
                v-for="item in provinces"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>

            <el-select
              v-model="formData.cityCode"
              placeholder="请选择城市（可选）"
              :disabled="!formData.provinceCode"
              @change="handleCityChange"
            >
              <el-option
                v-for="item in cities"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>

            <el-select
              v-model="formData.districtCode"
              placeholder="请选择区县（可选）"
              :disabled="!formData.cityCode"
            >
              <el-option
                v-for="item in districts"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>
          </div>
        </el-form-item>
        <el-form-item label="有效期" prop="dateRange">
          <el-date-picker
            v-model="formData.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
        </el-form-item>
        <el-form-item label="库存金额" prop="stockAmount">
          <el-input-number 
            v-model="formData.stockAmount" 
            :min="0.00"
            :precision="2"
            :step="1000"
          />
          <span class="unit">元</span>
        </el-form-item>
        <el-form-item label="总保额上限" prop="totalCoverage">
          <div class="coverage-input">
            <el-input-number 
              v-model="formData.totalCoverage" 
              :min="0.00"
              :precision="2"
              :step="10000"
            />
            <span class="unit">元</span>
          </div>
          <div class="form-tip form-tip-spacing extra-spacing">
            &nbsp;&nbsp;
            总保额不能超过公司注册资本 ({{ formattedCapital }})
          </div>
        </el-form-item>
        <el-form-item label="基础保费" prop="basePremium">
          <el-input-number 
            v-model="formData.basePremium" 
            :min="0.00"
            :precision="2"
            :step="10"
          />
          <span class="unit">{{ getPremiumUnit(formData.cropType) }}</span>
        </el-form-item>

        <el-form-item label="保额倍数" prop="coverageMultiplier">
          <el-input-number 
            v-model="formData.coverageMultiplier" 
            :min="1.0"
            :precision="1"
            :step="0.1"
          />
          <span class="unit">倍</span>
          <div class="form-tip">保额 = 基础保费 × 保额倍数</div>
        </el-form-item>

        <el-form-item label="上架状态" prop="visible">
          <el-switch v-model="formData.visible" />
        </el-form-item>
        <el-form-item label="阶梯定价">
          <div class="premium-tiers">
            <div v-for="(ladder, index) in formData.premiumLadders" :key="index" class="tier-item">
              <el-input-number 
                v-model="ladder.minArea"
                :min="0.00"
                :precision="2"
                placeholder="起始面积"
              />
              <span class="unit">{{ getPremiumUnit(formData.cropType).replace('元/', '') }}及以上</span>
              <el-input-number 
                v-model="ladder.discountRate"
                :min="0.00"
                :max="1"
                :precision="2"
                :step="0.1"
                placeholder="折扣率"
              />
              <span class="unit">折</span>
              <span class="discount-text">
                ({{ formattedDiscount(ladder.discountRate) }})
              </span>
              <el-button type="danger" link @click="removeLadder(index)">
                删除
              </el-button>
            </div>
            <el-button type="primary" link @click="addLadder">
              添加阶梯定价
            </el-button>
            <div class="form-tip">
              例如：设置100{{ getPremiumUnit(formData.cropType).replace('元/', '') }}以上0.9折，500{{ getPremiumUnit(formData.cropType).replace('元/', '') }}以上0.8折
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getProducts, 
  createProduct, 
  updateProduct, 
  publishProduct, 
  unpublishProduct,
  copyProduct, 
  deleteProduct, 
  getCompanyInfo 
} from '@/api/company'
import { getAllCrops } from '@/api/crops'
import { getProvinces, getCities, getDistricts } from '@/api/area'
import { formatDate } from '@/utils/date'
import { formatMoney, formatPercent } from '@/utils/format'

// 初始化响应式数据
const loading = ref(false)
const products = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const companyCapital = ref(0)
const crops = ref([])
const provinces = ref([])
const cities = ref([])
const districts = ref([])

// 初始化表单数据
const formData = ref({
  productName: '',
  description: '',
  userType: 'ALL',
  cropType: '',
  cropIds: [],
  provinceCode: '',
  cityCode: '',
  districtCode: '',
  dateRange: [],
  stockAmount: 0,
  basePremium: 0,
  visible: false,
  totalCoverage: 0,
  premiumLadders: [],
  coverageMultiplier: 2.0
})

// 农作物相关
const cropGroups = computed(() => {
  const groups = {}
  crops.value.forEach(crop => {
    if (!groups[crop.cropCategory]) {
      groups[crop.cropCategory] = {
        category: crop.cropCategory,
        crops: []
      }
    }
    groups[crop.cropCategory].crops.push(crop)
  })
  return Object.values(groups)
})

// 加载农作物数据
const loadCrops = async () => {
  try {
    const response = await getAllCrops()
    crops.value = response
  } catch (error) {
    console.error('Failed to load crops:', error)
    ElMessage.error('加载农作物数据失败')
  }
}

// 加载地区数据
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
  formData.value.cityCode = null
  formData.value.districtCode = null
  cities.value = []
  districts.value = []
  
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
const handleCityChange = async (cityCode) => {
  formData.value.districtCode = null
  districts.value = []
  
  if (cityCode) {
    try {
      districts.value = await getDistricts(cityCode)
    } catch (error) {
      console.error('Failed to load districts:', error)
      ElMessage.error('加载区县数据失败')
    }
  }
}

// 格式化地区显示
const formatArea = (provinceCode, cityCode, districtCode) => {
  const province = provinces.value.find(p => p.code === provinceCode)?.name || ''
  const city = cities.value.find(c => c.code === cityCode)?.name || ''
  const district = districts.value.find(d => d.code === districtCode)?.name || ''
  
  return [province, city, district].filter(Boolean).join(' / ')
}

// 修改表单验证规则
const rules = {
  productName: [
    { required: true, message: '请输入产品名称', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入产品介绍', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择面向用户类型', trigger: 'change' }
  ],
  cropIds: [
    { type: 'array', required: true, message: '请选择至少一种农作物', trigger: 'change' }
  ],
  provinceCode: [
    { required: true, message: '请选择省份', trigger: 'change' }
  ],
  totalCoverage: [
    { required: true, message: '请输入总保额', trigger: 'blur' }
  ],
  coverageMultiplier: [
    { required: true, message: '请输入保额倍数', trigger: 'blur' },
    { type: 'number', min: 1.0, message: '保额倍数必须大于等于1', trigger: 'blur' }
  ]
}

// 处理日期范围变化
const handleDateRangeChange = (val) => {
  if (val) {
    formData.value.startDate = val[0]
    formData.value.endDate = val[1]
  } else {
    formData.value.startDate = ''
    formData.value.endDate = ''
  }
}

// 获取产品列表
const fetchProducts = async () => {
  try {
    loading.value = true
    const response = await getProducts()
    console.log('获取产品列表响应:', response)
    
    // 根据实际返回的数据结构处理
    if (Array.isArray(response)) {
      products.value = response
    } else if (response && Array.isArray(response.data)) {
      products.value = response.data
    } else if (response && response.content && Array.isArray(response.content)) {
      products.value = response.content
    } else {
      console.warn('产品列表数据格式不正确:', response)
      products.value = []
    }
  } catch (error) {
    console.error('获取产品列表失败:', error)
    ElMessage.error('获取产品列表失败')
    products.value = []
  } finally {
    loading.value = false
  }
}

// 加载所有初始数据
const loadInitialData = async () => {
  try {
    await Promise.all([
      fetchProducts(),
      loadCrops(),
      loadProvinces(),
      loadCompanyInfo()
    ])
  } catch (error) {
    console.error('加载初始数据失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadInitialData()
})

// 处理创建产品
const handleCreate = () => {
  dialogVisible.value = true
  isEdit.value = false
  formRef.value?.resetFields()
  // 设置默认值
  formData.value = {
    productName: '',
    description: '',
    userType: 'ALL',
    cropType: '',
    cropIds: [],
    provinceCode: '',
    cityCode: '',
    districtCode: '',
    dateRange: [],
    stockAmount: 0,
    totalCoverage: 0,
    basePremium: 0,
    visible: false,
    premiumLadders: [],
    coverageMultiplier: 2.0
  }
}

// 处理编辑产品
const handleEdit = (row) => {
  isEdit.value = true
  formData.value = {
    ...row,
    dateRange: [row.startDate, row.endDate],
    cropIds: row.crops?.map(crop => crop.id) || [],
    cropType: row.cropType,
    coverageMultiplier: row.coverage_multiplier || 2.0
  }
  dialogVisible.value = true
}

// 处理复制产品
const handleCopy = async (row) => {
  try {
    await copyProduct(row.id)
    ElMessage.success('复制成功')
    await fetchProducts()
  } catch (error) {
    console.error('Failed to copy product:', error)
    ElMessage.error('复制失败')
  }
}

// 处理删除产品
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该产品吗？', '提示', {
      type: 'warning'
    })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    await fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete product:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 处理上架/下架
const handleToggleVisibility = async (row) => {
  try {
    if (row.visible) {
      await unpublishProduct(row.id)
    } else {
      await publishProduct(row.id)
    }
    await fetchProducts()
    ElMessage.success(row.visible ? '下架成功' : '上架成功')
  } catch (error) {
    console.error('Failed to toggle visibility:', error)
    ElMessage.error('操作失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    // 获取选中的作物信息
    const selectedCrops = crops.value.filter(crop => formData.value.cropIds.includes(crop.id))
    
    const submitData = {
      ...formData.value,
      startDate: formData.value.dateRange[0],
      endDate: formData.value.dateRange[1],
      crops: selectedCrops,
      cropType: formData.value.cropType || (selectedCrops.length > 0 ? selectedCrops[0].cropCategory : '未分类'),
      premiumLadders: formData.value.premiumLadders.map(ladder => ({
        minArea: ladder.minArea || 0,
        discountRate: ladder.discountRate || 1
      })),
      coverage_multiplier: formData.value.coverageMultiplier
    }
    delete submitData.dateRange
    delete submitData.cropIds
    delete submitData.coverageMultiplier

    console.log('提交的数据:', submitData)

    if (isEdit.value) {
      await updateProduct(submitData.id, submitData)
    } else {
      await createProduct(submitData)
    }
    
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    await fetchProducts()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 添加阶梯定价
const addLadder = () => {
  formData.value.premiumLadders.push({
    minArea: null,
    discountRate: 1
  })
}

// 移除阶梯定价
const removeLadder = (index) => {
  formData.value.premiumLadders.splice(index, 1)
}

// 验证阶梯定价
const validatePremiumLadders = (ladders) => {
  if (!ladders || ladders.length === 0) return true

  // 按最小面积排序
  ladders.sort((a, b) => a.minArea - b.minArea)

  // 验证最小面积是否递增
  for (let i = 0; i < ladders.length - 1; i++) {
    if (ladders[i].minArea >= ladders[i + 1].minArea) {
      return '阶梯定价的起始面积必须递增'
    }
  }

  return true
}

// 加载公司信息
const loadCompanyInfo = async () => {
  try {
    const response = await getCompanyInfo()
    companyCapital.value = response.registeredCapital
  } catch (error) {
    console.error('Failed to load company info:', error)
    ElMessage.error('加载公司信息失败，请刷新页面重试')
    companyCapital.value = 0  // 设置默认值
  }
}

// 使用计算属性替代 defineExpose
const formattedCapital = computed(() => formatMoney(companyCapital.value))
const formattedDiscount = computed(() => (value) => formatPercent(value))

// 添加格式化用户类型的方法
const formatUserType = (type) => {
  const typeMap = {
    'NEW_FARMER': '新型农民',
    'PROFESSIONAL': '专业农户',
    'COMPANY': '农业企业',
    'ALL': '所有用户'
  }
  return typeMap[type] || type
}

const getPremiumUnit = (cropType) => {
  if (!cropType) return '元/亩'
  
  // 根据作物类别判断单位
  switch (cropType) {
    case '畜牧养殖':
      return '元/头'
    case '家禽':
      return '元/只'
    case '水产养殖':
      return '元/尾'
    default:
      return '元/亩'
  }
}

// 处理作物选择变化
const handleCropChange = (value) => {
  // 根据选择的作物ID获取作物信息
  if (value && value.length > 0) {
    const selectedCrop = crops.value.find(crop => crop.id === value[0])
    if (selectedCrop) {
      formData.value.cropType = selectedCrop.cropCategory
      console.log('设置作物类型:', selectedCrop.cropCategory)
    }
  }
}

// 打开编辑对话框
const openDialog = (row = null) => {
  resetForm()
  if (row) {
    isEdit.value = true
    Object.assign(formData.value, row)
    formData.value.dateRange = [row.startDate, row.endDate]
    formData.value.cropType = row.cropType  // 设置作物类型
  } else {
    isEdit.value = false
  }
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.value = {
    productName: '',
    description: '',
    userType: 'ALL',
    cropType: '',  // 重置cropType
    cropIds: [],
    provinceCode: null,
    cityCode: null,
    districtCode: null,
    dateRange: [],
    startDate: '',
    endDate: '',
    stockAmount: 0,
    basePremium: 0,
    visible: false,
    totalCoverage: 0,
    premiumLadders: [],
    coverageMultiplier: 2.0
  }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 监听cropType的变化
watch(() => formData.value.cropType, (newType) => {
  console.log('cropType changed:', newType)
}, { immediate: true })
</script>

<style scoped>
.product-list {
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

.unit {
  margin-left: 8px;
  color: #606266;
}

.area-select {
  display: flex;
  gap: 10px;
}

.area-select .el-select {
  width: 160px;
}

.mx-1 {
  margin: 0 4px;
}

@media screen and (max-width: 768px) {
  .product-list {
    padding: 10px;
  }
  
  .area-select {
    flex-direction: column;
    gap: 8px;
  }
  
  .area-select .el-select {
    width: 100%;
  }
}

.crop-option {
  padding: 8px 0;
}

.crop-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.crop-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

/* 调整下拉选项的最大高度 */
:deep(.el-select-dropdown__wrap) {
  max-height: 280px;
}

/* 调整选项的内边距 */
:deep(.el-select-dropdown__item) {
  padding: 0 20px;
}

/* 选中状态的样式 */
:deep(.el-select-dropdown__item.selected) {
  .crop-name {
    color: var(--el-color-primary);
  }
}

/* 悬停状态的样式 */
:deep(.el-select-dropdown__item:hover) {
  background-color: var(--el-fill-color-light);
}

/* 选项组标题样式 */
:deep(.el-select-group__title) {
  padding: 8px 20px;
  font-size: 13px;
  font-weight: bold;
  color: var(--el-text-color-secondary);
  background-color: var(--el-fill-color-light);
}

/* 已选择标签的样式 */
:deep(.el-select__tags) {
  padding: 3px 0;
}

:deep(.el-select__tags-text) {
  display: inline-block;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.premium-tiers {
  margin-top: 10px;
}

.tier-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
}

.form-tip-spacing {
  margin-top: 8px;
  margin-left: 20px;
  display: block;
  clear: both;
}

.discount-text {
  color: #909399;
  font-size: 12px;
  margin-left: 4px;
}

.coverage-input {
  display: flex;
  align-items: center;
}

.form-tip-spacing {
  margin-top: 8px;
  margin-left: 2px;
  display: block;  /* 确保提示文本独占一行 */
  clear: both;     /* 清除浮动 */
}

.unit {
  margin-left: 8px;
  color: #606266;
}
</style> 

.extra-spacing {
  margin-left: 20px;
}
