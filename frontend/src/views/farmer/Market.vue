<template>
  <div class="market">
    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="category-nav">
        <el-radio-group v-model="activeCategory" size="large">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="crop">农作物保险</el-radio-button>
          <el-radio-button label="equipment">农机具保险</el-radio-button>
          <el-radio-button label="livestock">畜牧保险</el-radio-button>
        </el-radio-group>
      </div>
      
      <div class="quick-filter">
        <el-select v-model="selectedRegion" placeholder="选择地区" clearable>
          <el-option label="全省" value="all" />
          <el-option label="杭州市" value="330100" />
          <el-option label="宁波市" value="330200" />
          <el-option label="温州市" value="330300" />
          <el-option label="嘉兴市" value="330400" />
          <el-option label="湖州市" value="330500" />
          <el-option label="绍兴市" value="330600" />
          <el-option label="金华市" value="330700" />
          <el-option label="衢州市" value="330800" />
          <el-option label="舟山市" value="330900" />
          <el-option label="台州市" value="331000" />
          <el-option label="丽水市" value="331100" />
        </el-select>
        
        <el-select v-model="selectedCropType" placeholder="选择承保对象" clearable>
          <el-option label="全部" value="" />
          <el-option label="水稻" value="水稻" />
          <el-option label="小麦" value="小麦" />
          <el-option label="玉米" value="玉米" />
          <el-option label="油菜" value="油菜" />
          <el-option label="蔬菜" value="蔬菜" />
          <el-option label="水果" value="水果" />
          <el-option label="茶叶" value="茶叶" />
          <el-option label="农机具" value="农机具" />
          <el-option label="畜牧" value="畜牧" />
        </el-select>
        
        <el-select v-model="selectedUserType" placeholder="选择用户类型" clearable>
          <el-option label="全部用户" value="ALL" />
          <el-option label="新型农民" value="NEW_FARMER" />
          <el-option label="专业农户" value="PROFESSIONAL" />
          <el-option label="农业企业" value="COMPANY" />
        </el-select>
      </div>
    </div>

    <!-- 产品列表 -->
    <div v-if="loading" class="product-grid">
      <div v-for="i in 6" :key="i" class="product-card">
        <el-skeleton animated>
          <template #template>
            <div class="product-header">
              <el-skeleton-item variant="text" style="width: 50%" />
              <el-skeleton-item variant="text" style="width: 20%" />
            </div>
            <div class="product-info">
              <div v-for="j in 5" :key="j" class="info-item">
                <el-skeleton-item variant="text" style="width: 30%" />
                <el-skeleton-item variant="text" style="width: 50%" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>
    
    <div v-else-if="products.length === 0" class="empty-state">
      <el-empty description="暂无保险产品" />
    </div>
    
    <div v-else class="product-grid">
      <div v-for="product in filteredProducts" :key="product.id" class="product-card">
        <div class="product-link" @click="handleProductClick(product)">
          <div class="product-image">
            <img :src="getRandomImage(product)" :alt="product.productName">
            <div class="product-tag" v-if="product.cropType">{{ product.cropType }}</div>
          </div>
          <div class="product-content">
            <div class="product-header">
              <h3 class="product-name">{{ product.productName }}</h3>
              <div class="company-tag">{{ product.company.companyName }}</div>
            </div>
            <div class="product-info">
              <div class="info-row">
                <span class="info-label">承保区域</span>
                <span class="info-value">{{ formatRegion(product) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">保险期限</span>
                <span class="info-value">{{ formatDate(product.startDate) }} 至 {{ formatDate(product.endDate) }}</span>
              </div>
            </div>
            <div class="product-features">
              <div class="feature-item">
                <i class="el-icon-check"></i>
                <span>保障范围广</span>
              </div>
              <div class="feature-item">
                <i class="el-icon-check"></i>
                <span>理赔便捷</span>
              </div>
              <div class="feature-item">
                <i class="el-icon-check"></i>
                <span>价格实惠</span>
              </div>
            </div>
            <div class="product-highlights">
              <div class="highlight-item">
                <div class="highlight-value">{{ formatAmount(product.totalCoverage) }}<span class="unit">万</span></div>
                <div class="highlight-label">最高保额</div>
              </div>
              <div class="highlight-item">
                <div class="highlight-value">{{ formatAmount(product.totalCoverage - product.usedCoverage) }}<span class="unit">万</span></div>
                <div class="highlight-label">剩余额度</div>
              </div>
              <div class="highlight-item">
                <div class="highlight-value">{{ formatAmount(product.basePremium) }}</div>
                <div class="highlight-label">基础保费(元/亩)</div>
              </div>
            </div>
            <div class="product-footer">
              <div class="product-price">
                <span class="price-value">¥{{ formatAmount(product.basePremium) }}</span>
                <div class="price-unit">/亩/年</div>
              </div>
              <el-button type="primary" @click.stop="handleApply(product)">立即投保</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getProducts } from '@/api/market'
import { formatAmount } from '@/utils/format'
import { formatDate } from '@/utils/date'

export default {
  name: 'Market',
  data() {
    return {
      loading: true,
      products: [],
      activeCategory: 'all',
      selectedRegion: '',
      selectedUserType: '',
      selectedCropType: '',
      cityMap: {
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
      },
      randomImages: [
        'https://images.unsplash.com/photo-1500937386664-56d1dfef3854',
        'https://images.unsplash.com/photo-1464226184884-fa280b87c399',
        'https://images.unsplash.com/photo-1499529112087-3cb3b73cec95',
        'https://images.unsplash.com/photo-1492496913980-501348b61469',
        'https://images.unsplash.com/photo-1530836369250-ef72a3f5cda8'
      ]
    }
  },
  computed: {
    isMobile() {
      return this.windowWidth < 768
    },
    filteredProducts() {
      let result = this.products

      // 按类别筛选
      if (this.activeCategory !== 'all') {
        result = result.filter(product => {
          switch (this.activeCategory) {
            case 'crop':
              return product.cropType && !product.cropType.includes('农机具') && !product.cropType.includes('畜牧')
            case 'equipment':
              return product.cropType && product.cropType.includes('农机具')
            case 'livestock':
              return product.cropType && product.cropType.includes('畜牧')
            default:
              return true
          }
        })
      }

      // 按地区筛选
      if (this.selectedRegion && this.selectedRegion !== 'all') {
        result = result.filter(product => product.cityCode === this.selectedRegion)
      }

      // 按用户类型筛选
      if (this.selectedUserType) {
        result = result.filter(product => 
          product.userType === this.selectedUserType || 
          product.userType === 'ALL'
        )
      }

      // 按承保对象筛选
      if (this.selectedCropType) {
        result = result.filter(product => {
          if (!product.crops) return false;
          return product.crops.some(crop => crop.cropName.includes(this.selectedCropType));
        })
      }

      return result
    }
  },
  created() {
    this.fetchProducts()
  },
  mounted() {
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    handleResize() {
      this.windowWidth = window.innerWidth
    },
    async fetchProducts() {
      this.loading = true
      try {
        const response = await getProducts()
        console.log('API Response:', response)
        if (Array.isArray(response)) {
          console.log('Products:', response)
          this.products = response
        } else if (response && response.data) {
          console.log('Products:', response.data)
          this.products = response.data
        } else {
          console.warn('No data in response:', response)
          this.products = []
        }
      } catch (error) {
        console.error('获取产品列表失败:', error)
        this.$message.error('获取产品列表失败')
        this.products = []
      } finally {
        this.loading = false
      }
    },
    formatAmount,
    formatDate,
    formatRegion(product) {
      const parts = []
      if (product.provinceCode) parts.push('浙江省')
      if (product.cityCode) {
        parts.push(this.cityMap[product.cityCode] || '未知城市')
      }
      if (product.districtCode) {
        parts.push('具体区县')
      }
      return parts.join(' - ') || '全省'
    },
    getRandomImage(product) {
      const index = product.id % this.randomImages.length
      return this.randomImages[index]
    },
    handleProductClick(product) {
      const route = `/products/${product.id}`
      window.open(route, '_blank')
    },
    handleApply(product) {
      this.$router.push(`/products/${product.id}/apply`)
    }
  }
}
</script>

<style scoped>
.market {
  min-height: 100vh;
  background: #f5f7fa;
}

.filter-section {
  background: white;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.category-nav {
  margin-bottom: 24px;
  text-align: center;
}

.quick-filter {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 24px;
  padding: 24px;
}

.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 8px;
  background: rgba(103, 194, 58, 0.9);
  color: white;
  border-radius: 4px;
  font-size: 12px;
}

.product-content {
  padding: 20px;
}

.product-header {
  margin-bottom: 16px;
}

.product-name {
  font-size: 18px;
  color: #303133;
  margin: 0 0 8px;
  line-height: 1.4;
}

.company-tag {
  display: inline-block;
  padding: 2px 8px;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 13px;
}

.product-info {
  margin-bottom: 16px;
  padding: 12px;
  background: #fafafa;
  border-radius: 4px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  padding: 4px 0;
}

.info-row + .info-row {
  margin-top: 8px;
}

.info-label {
  color: #909399;
}

.info-value {
  color: #303133;
}

.product-features {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #67c23a;
}

.product-highlights {
  display: flex;
  justify-content: space-between;
  margin: 20px 0;
  padding: 16px;
  background: #f6ffed;
  border-radius: 8px;
}

.highlight-item {
  text-align: center;
  flex: 1;
}

.highlight-value {
  font-size: 20px;
  font-weight: bold;
  color: #67c23a;
  margin-bottom: 4px;
}

.highlight-value .unit {
  font-size: 14px;
  margin-left: 2px;
}

.highlight-label {
  font-size: 12px;
  color: #909399;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.product-price {
  display: flex;
  align-items: baseline;
}

.price-value {
  font-size: 24px;
  font-weight: bold;
  color: #ff4d4f;
}

.price-unit {
  font-size: 13px;
  color: #909399;
  margin-left: 4px;
}

.product-link {
  text-decoration: none;
  color: inherit;
  display: block;
}

.product-link:hover {
  text-decoration: none;
}

@media screen and (max-width: 768px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
  
  .product-highlights {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .highlight-item {
    flex: 1 1 33%;
    margin-bottom: 12px;
  }
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background: white;
  border-radius: 8px;
  margin: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.empty-state :deep(.el-empty__description) {
  margin-top: 16px;
  color: #909399;
}
</style> 