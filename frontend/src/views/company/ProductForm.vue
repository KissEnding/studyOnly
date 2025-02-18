<template>
  <el-form-item label="服务地区" required>
    <el-cascader
      v-model="selectedArea"
      :options="areaOptions"
      :props="areaProps"
      @change="handleAreaChange"
      placeholder="请选择服务地区"
    />
  </el-form-item>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProvinces, getCities, getDistricts } from '@/api/area'

const selectedArea = ref([])
const areaOptions = ref([])
const formData = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const areaProps = {
  value: 'code',
  label: 'name',
  children: 'children',
  checkStrictly: true,  // 允许选择任意一级
  emitPath: true  // 确保返回完整路径
}

// 处理地区选择变化
const handleAreaChange = (value) => {
  console.log('Selected area:', value)  // 添加日志
  
  // 确保 value 是数组
  if (Array.isArray(value)) {
    const [provinceCode, cityCode, districtCode] = value
    
    // 更新表单数据
    const newFormData = {
      ...formData.modelValue,
      provinceCode: provinceCode || null,
      cityCode: cityCode || null,
      districtCode: districtCode || null
    }
    
    console.log('Updated form data:', newFormData)  // 添加日志
    emit('update:modelValue', newFormData)
  }
}

// 加载省份数据
const loadProvinces = async () => {
  try {
    const provinces = await getProvinces()
    console.log('Loaded provinces:', provinces)  // 添加日志
    areaOptions.value = provinces.map(province => ({
      code: province.code,
      name: province.name,
      children: [], // 城市列表，懒加载
      loading: false
    }))
  } catch (error) {
    console.error('Failed to load provinces:', error)
  }
}

// 懒加载城市数据
const loadCities = async (province, callback) => {
  if (province.children.length > 0) {
    callback()
    return
  }
  
  try {
    province.loading = true
    const cities = await getCities(province.code)
    province.children = cities.map(city => ({
      code: city.code,
      name: city.name,
      children: [], // 区县列表，懒加载
      loading: false
    }))
    callback()
  } catch (error) {
    console.error('Failed to load cities:', error)
  } finally {
    province.loading = false
  }
}

// 懒加载区县数据
const loadDistricts = async (city, callback) => {
  if (city.children.length > 0) {
    callback()
    return
  }
  
  try {
    city.loading = true
    const districts = await getDistricts(city.code)
    city.children = districts.map(district => ({
      code: district.code,
      name: district.name
    }))
    callback()
  } catch (error) {
    console.error('Failed to load districts:', error)
  } finally {
    city.loading = false
  }
}

onMounted(() => {
  loadProvinces()
})
</script> 