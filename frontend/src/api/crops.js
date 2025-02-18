import request from '@/utils/request'

export function getAllCrops() {
  return request({
    url: '/api/crops',
    method: 'get'
  })
}

export function getCropsByCategory(category) {
  return request({
    url: '/api/crops',
    method: 'get',
    params: { category }
  })
} 