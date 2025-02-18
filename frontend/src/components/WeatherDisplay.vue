<template>
  <div class="weather-display">
    <div v-if="loading" class="weather-skeleton">
      <el-skeleton-item variant="circle" style="width: 40px; height: 40px"/>
      <el-skeleton-item variant="text" style="width: 120px"/>
    </div>
    <div v-else class="weather-content">
      <el-icon :size="24">
        <component :is="weatherIcon" />
      </el-icon>
      <div class="weather-text">
        <div class="location-weather">
          <span class="city">{{ location }}</span>
          <span class="weather">{{ weather?.textDay }}</span>
        </div>
        <span class="temperature" v-if="weather">{{ weather.high }}°C / {{ weather.low }}°C</span>
        <span v-else>获取天气信息失败</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Sunny, Cloudy, PartlyCloudy, Lightning, Rain, Snow } from '@element-plus/icons-vue'

const props = defineProps({
  loading: Boolean,
  weather: Object,
  location: String
})

const weatherIcon = computed(() => {
  if (!props.weather?.textDay) return Sunny
  const weather = props.weather.textDay
  if (weather.includes('晴')) return Sunny
  if (weather.includes('阴')) return Cloudy
  if (weather.includes('多云')) return PartlyCloudy
  if (weather.includes('雷')) return Lightning
  if (weather.includes('雨')) return Rain
  if (weather.includes('雪')) return Snow
  return Sunny
})
</script>

<style scoped>
.weather-display {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  min-height: 48px;
}

.weather-content,
.weather-skeleton {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.weather-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 14px;
}

.location-weather {
  display: flex;
  align-items: center;
  gap: 8px;
}

.city {
  font-weight: bold;
}

.weather {
  color: rgba(255, 255, 255, 0.9);
}

.temperature {
  color: rgba(255, 255, 255, 0.9);
}
</style> 