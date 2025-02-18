import request from '@/utils/request'

// 获取农户所在地区的天气信息
export function getWeather() {
  return request({
    url: `/api/weather/farmer`,
    method: 'get'
  })
} 