<template>
  <div class="product-detail">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="10" animated />
    </div>
    
    <div v-else-if="!product" class="empty-state">
      <el-empty description="产品不存在" />
    </div>
    
    <div v-else class="content">
      <!-- 产品标题和基本信息 -->
      <div class="product-title">
        <el-button class="back-button" icon="ArrowLeft" @click="$router.push('/farmer/market')">返回</el-button>
        <h1>{{ product.productName }}</h1>
        <div class="title-tags">
          <el-tag size="large">{{ product.company.companyName }}</el-tag>
          <el-tag type="warning" size="large" v-if="product.userType">{{ formatUserType(product.userType) }}</el-tag>
          <el-tag type="info" size="large">{{ product.productCode }}</el-tag>
        </div>
      </div>

      <!-- 基本信息卡片 -->
      <div class="info-card">
        <div class="info-header">基本信息</div>
        <div class="info-content">
          <div class="info-row">
            <span class="info-label">承保对象</span>
            <span class="info-value">{{ product.cropType }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">承保区域</span>
            <span class="info-value">{{ formatRegion(product) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">保险期限</span>
            <span class="info-value">{{ formatDate(product.startDate) }} 至 {{ formatDate(product.endDate) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">基础保费</span>
            <span class="info-value highlight">¥{{ formatAmount(product.basePremium) }}/{{ getPremiumUnit(product.cropType) }}</span>
          </div>
        </div>
      </div>

      <!-- 保障额度卡片 -->
      <div class="info-card">
        <div class="info-header">保障额度</div>
        <div class="coverage-grid">
          <div class="coverage-item">
            <div class="coverage-amount">{{ (product.totalCoverage / 10000).toFixed(2) }}亿</div>
            <div class="coverage-label">总保额</div>
          </div>
          <div class="coverage-item">
            <div class="coverage-amount">{{ ((product.totalCoverage - product.usedCoverage) / 10000).toFixed(2) }}亿</div>
            <div class="coverage-label">剩余保额</div>
          </div>
          <div class="coverage-item">
            <div class="coverage-amount">{{ (product.usedCoverage / 10000).toFixed(2) }}亿</div>
            <div class="coverage-label">已用保额</div>
          </div>
        </div>
      </div>

      <!-- 阶梯优惠卡片 -->
      <div class="info-card" v-if="product.premiumLadders && product.premiumLadders.length > 0">
        <div class="info-header">阶梯优惠</div>
        <div class="ladder-grid">
          <div class="ladder-item" v-for="(ladder, index) in product.premiumLadders" :key="index">
            <div class="ladder-area">{{ formatAmount(ladder.minArea) }}亩以上</div>
            <div class="ladder-rate">{{ formatDiscount(ladder.discountRate) }}折</div>
          </div>
        </div>
      </div>

      <!-- 产品详情卡片 -->
      <div class="info-card">
        <div class="info-header">产品详情</div>
        <div class="detail-content" v-if="product.description">
          <template v-for="(section, index) in parsedDescription" :key="index">
            <div v-if="section.title" class="detail-section">
              <h3 class="section-title">{{ section.title }}</h3>
              <div class="section-content">
                <template v-if="section.isList">
                  <ul class="detail-list">
                    <li v-for="(item, itemIndex) in section.content" :key="itemIndex">
                      <strong v-if="item.title">{{ item.title }}：</strong>
                      {{ item.text }}
                    </li>
                  </ul>
                </template>
                <p v-else class="detail-text">{{ section.content }}</p>
              </div>
            </div>
          </template>
        </div>
        <div v-else class="empty-detail">暂无详细介绍</div>
      </div>

      <!-- 承保作物卡片 -->
      <div class="info-card" v-if="product.crops && product.crops.length > 0">
        <div class="info-header">承保作物</div>
        <div class="crops-grid">
          <el-tag
            v-for="crop in product.crops"
            :key="crop.id"
            class="crop-tag"
            effect="plain"
            size="large"
          >
            {{ crop.cropName }}
          </el-tag>
        </div>
      </div>

      <!-- 投保按钮 -->
      <div class="action-footer">
        <div class="price-info">
          <div class="final-price">
            <span class="price-label">基础保费：</span>
            <span class="price-amount">¥{{ formatAmount(product.basePremium) }}</span>
            <span class="price-unit">/{{ getPremiumUnit(product.cropType) }}</span>
          </div>
          <div class="price-note">* 实际保费可能因投保面积享受阶梯优惠</div>
        </div>
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="handleApply">立即投保</el-button>
          <el-button size="large" @click="$router.back()">返回</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getProductDetail } from '@/api/market'
import { formatAmount } from '@/utils/format'
import { formatDate } from '@/utils/date'
import { ShoppingCart } from '@element-plus/icons-vue'

export default {
  name: 'ProductDetail',
  components: {
    ShoppingCart
  },
  props: {
    initialProduct: {
      type: Object,
      default: null
    },
    dialogMode: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: true,
      product: this.initialProduct,
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
      }
    }
  },
  created() {
    if (!this.dialogMode) {
      this.fetchProductDetail()
    } else {
      this.loading = false
    }
  },
  methods: {
    async fetchProductDetail() {
      this.loading = true
      try {
        const id = this.$route.params.id
        const response = await getProductDetail(id)
        if (response && response.data) {
          this.product = response.data
        } else if (response) {
          this.product = response
        } else {
          this.$message.error('产品不存在')
        }
      } catch (error) {
        console.error('获取产品详情失败:', error)
        this.$message.error(error.response?.data || '获取产品详情失败')
        this.$router.push('/farmer/market')
      } finally {
        this.loading = false
      }
    },
    handleApply() {
      this.$router.push(`/products/${this.product.id}/apply`)
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
    formatUserType(type) {
      const typeMap = {
        'NEW_FARMER': '新型农民',
        'PROFESSIONAL': '专业农户',
        'COMPANY': '农业企业',
        'ALL': '所有用户'
      }
      return typeMap[type] || type
    },
    formatDiscount(rate) {
      return (rate * 10).toFixed(1)
    },
    getPremiumUnit(cropType) {
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
  },
  computed: {
    parsedDescription() {
      if (!this.product.description) return [];
      
      const sections = [];
      const lines = this.product.description.split('\n').filter(line => line.trim());
      
      let currentSection = null;
      let listItems = [];
      
      lines.forEach(line => {
        const trimmedLine = line.trim();
        
        // 检查是否是标题（以：结尾）
        if (trimmedLine.endsWith('：')) {
          // 如果有未处理的列表项，先保存当前部分
          if (listItems.length > 0) {
            currentSection.content = listItems;
            currentSection.isList = true;
            listItems = [];
          }
          
          currentSection = {
            title: trimmedLine.slice(0, -1),
            content: '',
            isList: false
          };
          sections.push(currentSection);
        }
        // 检查是否是列表项（以特定符号开头）
        else if (trimmedLine.includes('：') && currentSection) {
          const [title, text] = trimmedLine.split('：');
          listItems.push({ title: title.trim(), text: text.trim() });
          currentSection.isList = true;
        }
        // 普通段落
        else if (trimmedLine) {
          if (listItems.length > 0) {
            currentSection.content = listItems;
            currentSection.isList = true;
            listItems = [];
          }
          
          if (!currentSection || currentSection.content) {
            currentSection = {
              title: '',
              content: trimmedLine,
              isList: false
            };
            sections.push(currentSection);
          } else {
            currentSection.content = trimmedLine;
          }
        }
      });
      
      // 处理最后的列表项
      if (listItems.length > 0 && currentSection) {
        currentSection.content = listItems;
        currentSection.isList = true;
      }
      
      return sections;
    }
  }
}
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  background: #f5f7fa;
  min-height: 100vh;
  position: relative;
}

.loading {
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.product-title {
  background: white;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  position: relative;
}

.product-title .back-button {
  position: absolute;
  top: 32px;
  left: 32px;
}

.product-title h1 {
  margin: 0 0 16px;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  text-align: center;
}

.title-tags {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.info-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.info-header {
  padding: 20px 24px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
  background: #fafafa;
}

.info-content {
  padding: 24px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  color: #909399;
  font-size: 15px;
}

.info-value {
  color: #303133;
  font-size: 15px;
}

.info-value.highlight {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 600;
}

.coverage-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  padding: 24px;
  gap: 20px;
}

.coverage-item {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.coverage-amount {
  font-size: 24px;
  font-weight: 600;
  color: #67c23a;
  margin-bottom: 8px;
}

.coverage-label {
  color: #909399;
  font-size: 14px;
}

.ladder-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  padding: 24px;
  gap: 16px;
}

.ladder-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.ladder-area {
  color: #67c23a;
  font-weight: 500;
}

.ladder-rate {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
}

.detail-content {
  padding: 24px;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}

.detail-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.detail-list li {
  position: relative;
  padding-left: 20px;
  margin-bottom: 12px;
  line-height: 1.6;
  color: #606266;
}

.detail-list li:last-child {
  margin-bottom: 0;
}

.detail-list li::before {
  content: "•";
  position: absolute;
  left: 0;
  color: #67c23a;
  font-weight: bold;
}

.detail-text {
  line-height: 1.8;
  color: #606266;
  margin: 0;
}

.empty-detail {
  padding: 40px;
  text-align: center;
  color: #909399;
  font-style: italic;
}

.crops-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 24px;
}

.crop-tag {
  margin: 0;
}

.action-footer {
  position: sticky;
  bottom: 0;
  background: white;
  padding: 20px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 8px;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.05);
}

.price-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.final-price {
  display: flex;
  align-items: baseline;
}

.price-label {
  font-size: 15px;
  color: #909399;
}

.price-amount {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
  margin: 0 4px;
}

.price-unit {
  font-size: 14px;
  color: #909399;
}

.price-note {
  font-size: 13px;
  color: #909399;
}

.buy-button {
  padding: 0 40px;
  height: 48px;
  font-size: 16px;
}

.buy-button :deep(.el-icon) {
  margin-right: 8px;
  font-size: 20px;
}

@media screen and (max-width: 768px) {
  .product-detail {
    padding: 20px 16px;
  }

  .coverage-grid {
    grid-template-columns: 1fr;
  }

  .action-footer {
    flex-direction: column;
    gap: 20px;
    padding: 16px;
  }

  .buy-button {
    width: 100%;
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