<template>
  <div class="dashboard">
    <!-- 轮播图区域 -->
    <el-carousel height="400px" class="banner-carousel">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="banner-content" :style="{ backgroundImage: `url(${item.image})` }">
          <div class="banner-text">
            <h2>{{ item.title }}</h2>
            <p>{{ item.description }}</p>
            <el-button type="primary" size="large" @click="$router.push(item.link)">
              {{ item.buttonText }}
            </el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 天气信息条 -->
    <div class="weather-strip">
      <div class="weather-strip-content" v-if="weatherData">
        <div class="weather-strip-left">
          <el-icon class="weather-icon"><Sunny /></el-icon>
          <span class="location">{{ weatherData.location.name }}</span>
          <span class="temp-main">{{ weatherData.high }}°C</span>
          <span class="weather-text">{{ weatherData.textDay }}</span>
        </div>
        <div class="weather-strip-right">
          <div class="weather-detail-item">
            <span class="label">湿度</span>
            <span class="value">{{ weatherData.humidity }}%</span>
          </div>
          <div class="divider"></div>
          <div class="weather-detail-item">
            <span class="label">风向</span>
            <span class="value">{{ weatherData.windDirection }}</span>
          </div>
          <div class="divider"></div>
          <div class="weather-detail-item">
            <span class="label">风力</span>
            <span class="value">{{ weatherData.windScale }}级</span>
          </div>
        </div>
      </div>
      <div class="weather-strip-loading" v-else>
        <el-skeleton :rows="1" animated />
      </div>
    </div>

    <!-- 服务入口区域 -->
    <div class="service-section">
      <h3 class="section-title">为您服务</h3>
      <div class="service-grid">
        <div class="service-item service-item-1" @click="$router.push('/farmer/market')">
          <div class="service-icon">
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <span>一站式保单查询</span>
          <div class="service-arrow">→</div>
        </div>
        <div class="service-item service-item-2" @click="$router.push('/farmer/consultation')">
          <div class="service-icon">
            <el-icon><Document /></el-icon>
          </div>
          <span>农保咨询</span>
          <div class="service-arrow">→</div>
        </div>
        <div class="service-item service-item-3" @click="$router.push('/farmer/policies')">
          <div class="service-icon">
            <el-icon><Files /></el-icon>
          </div>
          <span>电子保单下载</span>
          <div class="service-arrow">→</div>
        </div>
        <div class="service-item service-item-4" @click="$router.push('/farmer/profile')">
          <div class="service-icon">
            <el-icon><User /></el-icon>
          </div>
          <span>个人中心</span>
          <div class="service-arrow">→</div>
        </div>
      </div>
    </div>

    <!-- 推荐产品区域 -->
    <div class="recommend-section">
      <div class="section-header">
        <h3 class="section-title">为您推荐</h3>
        <div class="recommend-tabs">
          <div class="tab-header">
            <span 
              v-for="tab in tabs" 
              :key="tab.key"
              class="tab-item"
              :class="{ active: activeTab === tab.key }"
              @click="activeTab = tab.key"
            >
              {{ tab.name }}
            </span>
            <span class="view-more" @click="$router.push('/farmer/market')">查看更多 ></span>
          </div>
          <el-row :gutter="20" class="product-grid">
            <el-col :span="12" v-for="product in currentProducts" :key="product.id">
              <div class="product-card" @click="$router.push(`/farmer/market?id=${product.id}`)">
                <div class="product-info">
                  <div class="product-title">
                    <h4>{{ product.name }}</h4>
                    <p class="product-desc">{{ product.description }}</p>
                  </div>
                  <div class="product-features">
                    <div class="feature-item" v-for="(feature, index) in product.features" :key="index">
                      <el-icon><Check /></el-icon>
                      <span>{{ feature }}</span>
                    </div>
                  </div>
                  <div class="product-price">
                    <div class="price-info">
                      <span class="price">¥{{ product.premium }}</span>
                      <span class="unit">/月 起</span>
                    </div>
                    <el-button type="primary" class="buy-button">立即投保</el-button>
                  </div>
                </div>
                <div class="product-image">
                  <img :src="product.image" :alt="product.name">
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { 
  ShoppingCart, Document, Files, Money, Wallet, User, Check
} from '@element-plus/icons-vue'
import { getWeather } from '@/api/weather'
import { formatDate } from '@/utils/date'

const store = useStore()
const router = useRouter()

// 轮播图数据
const banners = [
  {
    id: 1,
    image: require('@/assets/images/rustic.jpg'),
    title: '农业保险助力乡村振兴',
    description: '为您的农业生产保驾护航',
    buttonText: '立即投保',
    link: '/farmer/market'
  },
  {
    id: 2,
    image: require('@/assets/images/riceInsurance.jpg'),
    title: '水稻种植保险',
    description: '为农田撑起保护伞，保障收成，促进乡村发展',
    buttonText: '了解更多',
    link: '/farmer/market?type=rice'
  },
  {
    id: 3,
    image: require('@/assets/images/implementsInsurance.jpg'),
    title: '农机具保险',
    description: '确保您的农机具安全无忧，减少意外损失，提高作业效率',
    buttonText: '查看详情',
    link: '/farmer/market?type=machine'
  },
  {
    id: 4,
    image: require('@/assets/images/FruitInsurance.jpg'),
    title: '农果树保险',
    description: '为您的果树提供专业保障，减少自然灾害损失，保障果园收益',
    buttonText: '查看详情',
    link: '/farmer/market?type=machine'
  }
]

// 标签配置
const tabs = [
  { key: 'hot', name: '热门推荐' },
  { key: 'rice', name: '为水稻' },
  { key: 'machine', name: '为农机' },
  { key: 'fruit', name: '为果树' }
]

// 推荐产品标签页
const activeTab = ref('hot')

// 热门推荐产品
const hotProducts = [
  {
    id: 1,
    name: '水稻种植保险',
    description: '为您的水稻种植保驾护航，全方位保障您的收益',
    premium: '118.8',
    image: 'https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=500&auto=format&fit=crop&q=60',
    features: [
      '最高保额100万',
      '投保便捷',
      '免体检'
    ]
  },
  {
    id: 2,
    name: '农机具综合保险',
    description: '农机具意外损失保障，让您安心生产',
    premium: '274',
    image: 'https://images.unsplash.com/photo-1592982537447-7440770cbfc9?w=500&auto=format&fit=crop&q=60',
    features: [
      '最高400万保额',
      '保险期间长',
      '理赔快速'
    ]
  },
  {
    id: 3,
    name: '果树种植保险',
    description: '果树全生长周期保障，助力果农增收',
    premium: '241',
    image: 'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=500&auto=format&fit=crop&q=60',
    features: [
      '保障范围广',
      '服务专业',
      '理赔及时'
    ]
  },
  {
    id: 4,
    name: '大棚种植保险',
    description: '设施农业保障方案，让您的收入更有保障',
    premium: '690',
    image: 'https://images.unsplash.com/photo-1530836369250-ef72a3f5cda8?w=500&auto=format&fit=crop&q=60',
    features: [
      '保障全面',
      '长期保障',
      '服务到位'
    ]
  }
]

// 水稻保险产品
const riceProducts = [
  {
    id: 101,
    name: '水稻基础保险',
    description: '基础自然灾害保障，覆盖水灾、旱灾等主要风险',
    premium: '98',
    image: 'https://img95.699pic.com/photo/50015/9432.jpg_wh860.jpg',
    features: [
      '保额最高10万/亩',
      '理赔快速',
      '手续简单'
    ]
  },
  {
    id: 102,
    name: '水稻全险保障',
    description: '全方位风险保障，无忧种植保障计划',
    premium: '168',
    image: 'https://img95.699pic.com/photo/50160/9280.jpg_wh860.jpg',
    features: [
      '保额最高15万/亩',
      '承保范围广',
      '优先理赔'
    ]
  },
  {
    id: 103,
    name: '优选水稻保险',
    description: '专为优质稻米品种定制的保险方案',
    premium: '188',
    image: 'https://ts1.cn.mm.bing.net/th/id/R-C.cc8b336f7af8f03adc46cacad4363569?rik=dLWO2Lr3W0HKnw&riu=http%3a%2f%2fimg95.699pic.com%2fphoto%2f50016%2f8413.jpg_wh860.jpg&ehk=buJMYmWw81AP8KtaboZ3OnVGzq%2bQltVDDLDYyCuSseQ%3d&risl=&pid=ImgRaw&r=0',
    features: [
      '品质损失保障',
      '市场价格保障',
      '专家指导服务'
    ]
  },
  {
    id: 104,
    name: '水稻种植险Plus',
    description: '升级版水稻种植保险，提供更全面的保障',
    premium: '258',
    image: 'https://ts1.cn.mm.bing.net/th/id/R-C.cd396ac1557b80fbf9f55b91ad8f9d6d?rik=YIqlf6GhBVYj9g&riu=http%3a%2f%2ffjnews.fjsen.com%2fimages%2f2023-04%2f17%2f77fe9398-cf77-4c1f-b705-e5ce7680ae1a.2&ehk=7dlHXDLMYC%2fQiANV92UZkN2hGPgiwKv%2bB3BE0rHBhYs%3d&risl=&pid=ImgRaw&r=0',
    features: [
      '病虫害损失保障',
      '种子质量保障',
      '收入保障'
    ]
  }
]

// 农机具保险产品
const machineProducts = [
  {
    id: 201,
    name: '农机综合险',
    description: '为您的农机设备提供全面的保障方案',
    premium: '299',
    image: 'https://images.unsplash.com/photo-1589923188900-85dae523342b?w=500&auto=format&fit=crop&q=60',
    features: [
      '意外损坏保障',
      '第三方责任险',
      '盗抢险'
    ]
  },
  {
    id: 202,
    name: '收割机专项保险',
    description: '专为收割机定制的保险方案',
    premium: '399',
    image: 'https://www.bing.com/th/id/OGC.f86d5631a2e374f9bf62e41c79099a6f?pid=1.7&rurl=https%3a%2f%2fwww.lovol.com%2fupload%2fimages%2f2022%2f06%2f02%2fa0d4247e6860401d869ea949ebd8d358.gif&ehk=mdaDoDjIMxx%2b9Lfu6cKASB5sDITE8Iafx0m5yvKwaPo%3d',
    features: [
      '保额最高50万',
      '车损保障',
      '维修补贴'
    ]
  },
  {
    id: 203,
    name: '拖拉机保险A',
    description: '拖拉机专属保险方案，让您安心作业',
    premium: '289',
    image: 'https://tse4-mm.cn.bing.net/th/id/OIP-C.Uv1yj3D1Q4oAN00TKGGQdQHaFD?rs=1&pid=ImgDetMain',
    features: [
      '机损保障',
      '操作事故保障',
      '24小时救援'
    ]
  },
  {
    id: 204,
    name: '农机设备险',
    description: '农业机械设备综合保险方案',
    premium: '359',
    image: 'https://a.img.s105.cn/UploadFiles/picture/2017/11/2017111614124956501.jpg',
    features: [
      '设备损坏保障',
      '自然灾害保障',
      '维修费用补贴'
    ]
  }
]

// 果树保险产品
const fruitProducts = [
  {
    id: 301,
    name: '果树种植保险A',
    description: '果树生长期全程保障方案',
    premium: '199',
    image: 'https://img95.699pic.com/photo/60020/0797.jpg_wh860.jpg',
    features: [
      '自然灾害保障',
      '病虫害保障',
      '产量保障'
    ]
  },
  {
    id: 302,
    name: '果园综合险',
    description: '一站式果园保险解决方案',
    premium: '299',
    image: 'https://ts1.cn.mm.bing.net/th/id/R-C.c55e1373e98193656b97f8116d2701a8?rik=QqZS3WlC3Brneg&riu=http%3a%2f%2fn.sinaimg.cn%2fsinakd10119%2f215%2fw1149h666%2f20210110%2f92a2-khmynua1557973.jpg&ehk=2zVFsZuF%2fLLDN1idq6tBrzk9DY%2bQ7N5AEdhPzzxyrPg%3d&risl=&pid=ImgRaw&r=0',
    features: [
      '果树保障',
      '设施保障',
      '收入保障'
    ]
  },
  {
    id: 303,
    name: '优质果树险',
    description: '专为优质果品定制的保险方案',
    premium: '259',
    image: 'https://img95.699pic.com/photo/60016/7251.jpg_wh300.jpg!/fh/300/quality/90',
    features: [
      '品质损失保障',
      '市场价格保障',
      '技术指导服务'
    ]
  },
  {
    id: 304,
    name: '果园防灾险',
    description: '果园防灾减损综合保险方案',
    premium: '329',
    image: 'https://tse1-mm.cn.bing.net/th/id/OIP-C.nz6kksc6ZkGf7jtp1dp9xgHaE8?rs=1&pid=ImgDetMain',
    features: [
      '极端天气保障',
      '设施损失保障',
      '防灾设备补贴'
    ]
  }
]

// 根据当前选中的标签返回对应的产品列表
const currentProducts = computed(() => {
  switch (activeTab.value) {
    case 'rice':
      return riceProducts
    case 'machine':
      return machineProducts
    case 'fruit':
      return fruitProducts
    default:
      return hotProducts
  }
})

const weatherData = ref(null)

// 获取天气数据
const fetchWeather = async () => {
  try {
    // 检查当前的认证状态
    const token = localStorage.getItem('token')
    const storedUsername = localStorage.getItem('username')
    console.log('当前认证信息:', {
      token: token ? '已设置' : '未设置',
      storedUsername
    })
    
    const response = await getWeather()
    console.log('天气API返回数据:', response)
    
    if (response && response.results && response.results.length > 0) {
      const result = response.results[0]
      const dailyWeather = result.daily[0]
      
      // 确保所有字段都正确映射
      weatherData.value = {
        location: {
          name: result.location?.name || '未知地区'
        },
        high: dailyWeather.high || '0',
        low: dailyWeather.low || '0',
        textDay: dailyWeather.text_day || '未知',
        textNight: dailyWeather.text_night || '未知',
        rainfall: dailyWeather.rainfall || '0.00',
        precip: dailyWeather.precip || '0',
        humidity: dailyWeather.humidity || '0',
        windDirection: dailyWeather.wind_direction || '未知',
        windScale: dailyWeather.wind_scale || '0',
        windSpeed: dailyWeather.wind_speed || '0',
        lastUpdate: result.last_update
      }
      console.log('处理后的天气数据:', weatherData.value)
    } else {
      console.warn('天气数据格式不正确:', response)
    }
  } catch (error) {
    console.error('获取天气信息失败:', error)
  }
}

// 格式化最后更新时间
const formatLastUpdate = (lastUpdate) => {
  if (!lastUpdate) return ''
  // 将时间字符串转换为本地时间
  const date = new Date(lastUpdate)
  return formatDate(date, 'YYYY-MM-DD HH:mm')
}

// 在组件挂载时获取天气数据
onMounted(() => {
  fetchWeather()
  // 每30分钟更新一次天气
  const timer = setInterval(fetchWeather, 30 * 60 * 1000)
  
  // 组件卸载时清除定时器
  onUnmounted(() => {
    clearInterval(timer)
  })
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.banner-carousel {
  margin-bottom: 40px;
  border-radius: 8px;
  overflow: hidden;
}

.banner-content {
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  padding: 0 60px;
}

.banner-text {
  color: white;
  max-width: 500px;
}

.banner-text h2 {
  font-size: 36px;
  margin-bottom: 20px;
}

.banner-text p {
  font-size: 18px;
  margin-bottom: 30px;
  opacity: 0.9;
}

.service-section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 32px;
  margin-bottom: 30px;
  color: #303133;
  text-align: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  font-weight: bold;
}

.section-title::before,
.section-title::after {
  content: '';
  height: 2px;
  background: #dcdfe6;
  flex: 1;
  margin: 0 20px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(4, 200px);
  gap: 20px;
  padding: 0 20px;
  justify-content: center;
}

.service-item {
  background: #fff;
  padding: 20px 15px;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  border: 1px solid transparent;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 120px;
}

.service-icon {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  transition: all 0.3s;
}

.service-item .el-icon {
  font-size: 22px;
  transition: all 0.3s;
}

.service-item span {
  font-size: 14px;
  color: #333;
  margin-top: 8px;
  transition: all 0.3s;
}

.service-arrow {
  position: absolute;
  right: 15px;
  bottom: 15px;
  opacity: 0;
  transition: all 0.3s;
  font-size: 16px;
}

/* 为每个服务项定制颜色 */
.service-item-1 {
  background: #fff9f2;
}
.service-item-1 .service-icon {
  background: #ffe7cc;
}
.service-item-1 .el-icon {
  color: #ff9933;
}
.service-item-1:hover {
  border-color: #ff9933;
}
.service-item-1:hover .service-arrow {
  color: #ff9933;
}

.service-item-2 {
  background: #f0f9ff;
}
.service-item-2 .service-icon {
  background: #cce7ff;
}
.service-item-2 .el-icon {
  color: #3399ff;
}
.service-item-2:hover {
  border-color: #3399ff;
}
.service-item-2:hover .service-arrow {
  color: #3399ff;
}

.service-item-3 {
  background: #f0fff0;
}
.service-item-3 .service-icon {
  background: #ccffcc;
}
.service-item-3 .el-icon {
  color: #33cc33;
}
.service-item-3:hover {
  border-color: #33cc33;
}
.service-item-3:hover .service-arrow {
  color: #33cc33;
}

.service-item-4 {
  background: #fff0f9;
}
.service-item-4 .service-icon {
  background: #ffcce7;
}
.service-item-4 .el-icon {
  color: #ff3399;
}
.service-item-4:hover {
  border-color: #ff3399;
}
.service-item-4:hover .service-arrow {
  color: #ff3399;
}

/* 悬浮效果 */
.service-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px 0 rgba(0,0,0,0.1);
}

.service-item:hover .service-arrow {
  opacity: 1;
  transform: translateX(5px);
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .service-grid {
    grid-template-columns: repeat(2, 160px);
    gap: 15px;
  }
  
  .service-item {
    min-height: 110px;
  }
}

.recommend-section {
  margin: 40px auto;
  max-width: 1200px;
  padding: 0 20px;
}

.section-header {
  margin-bottom: 20px;
}

.recommend-tabs {
  margin-top: 20px;
}

.tab-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  position: relative;
}

.tab-item {
  font-size: 18px;
  color: #333;
  margin: 0 40px;
  cursor: pointer;
  padding: 0 4px 10px;
  position: relative;
  font-weight: 500;
}

.tab-item.active {
  color: #1890ff;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #1890ff;
}

.view-more {
  position: absolute;
  right: 0;
  color: #1890ff;
  cursor: pointer;
  font-size: 14px;
}

.product-grid {
  margin-top: 20px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  border: 1px solid #eee;
  transition: all 0.3s;
  cursor: pointer;
  
}

.product-card:hover {
  border-color: #1890ff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  margin-right: 15px;
}

.product-title h4 {
  font-size: 18px;
  color: #333;
  margin: 0 0 6px;
}

.product-desc {
  font-size: 13px;
  color: #666;
  margin: 0;
  line-height: 1.4;
}

.product-features {
  margin: 12px 0;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  color: #666;
  font-size: 13px;
}

.feature-item .el-icon {
  color: #52c41a;
  margin-right: 6px;
  font-size: 14px;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-info {
  display: flex;
  align-items: baseline;
}

.price {
  font-size: 20px;
  color: #ff4d4f;
  font-weight: bold;
}

.unit {
  font-size: 13px;
  color: #999;
  margin-left: 4px;
}

.buy-button {
  padding: 6px 16px;
  font-size: 13px;
}

.product-image {
  width: 140px;
  height: 140px;
  overflow: hidden;
  border-radius: 4px;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media screen and (max-width: 768px) {
  .product-card {
    flex-direction: column;
    height: auto;
    padding: 15px;
  }

  .product-image {
    width: 120px;
    height: 120px;
    margin: 12px auto;
  }

  .product-info {
    margin-right: 0;
  }
}

.weather-section {
  display: none;
}

.service-item-weather {
  background: linear-gradient(135deg, #52c41a 0%, #95de64 100%);
  color: white;
  cursor: default;
  overflow: hidden;
}

.service-item-weather:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(82, 196, 26, 0.2);
}

.weather-mini-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.weather-mini-header {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 12px;
  color: rgba(255, 255, 255, 0.95);
}

.weather-mini-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.weather-mini-temp {
  text-align: center;
  margin-bottom: 12px;
}

.temp-value {
  font-size: 32px;
  font-weight: bold;
  color: rgba(255, 255, 255, 0.95);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.temp-text {
  font-size: 16px;
  margin-left: 8px;
  color: rgba(255, 255, 255, 0.9);
}

.weather-mini-details {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
}

.weather-mini-loading {
  padding: 12px;
}

.weather-strip {
  margin: -20px auto 40px;
  max-width: 1200px;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.weather-strip-content {
  background: linear-gradient(135deg, #52c41a 0%, #95de64 100%);
  border-radius: 12px;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  box-shadow: 0 4px 16px rgba(82, 196, 26, 0.15);
  transition: all 0.3s ease;
}

.weather-strip-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(82, 196, 26, 0.2);
}

.weather-strip-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.weather-icon {
  font-size: 28px;
  color: rgba(255, 255, 255, 0.9);
}

.location {
  font-size: 16px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.9);
}

.temp-main {
  font-size: 24px;
  font-weight: bold;
  color: white;
}

.weather-text {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
}

.weather-strip-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.weather-detail-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.weather-detail-item .label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.weather-detail-item .value {
  font-size: 15px;
  font-weight: 500;
  color: white;
}

.divider {
  width: 1px;
  height: 24px;
  background: rgba(255, 255, 255, 0.2);
}

.weather-strip-loading {
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

@media screen and (max-width: 768px) {
  .weather-strip {
    margin: 0 auto 30px;
    padding: 0 15px;
  }

  .weather-strip-content {
    flex-direction: column;
    gap: 16px;
    padding: 16px;
  }

  .weather-strip-left {
    width: 100%;
    justify-content: space-between;
  }

  .weather-strip-right {
    width: 100%;
    justify-content: space-between;
  }

  .divider {
    height: 20px;
  }
}
</style> 