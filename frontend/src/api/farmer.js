import request from '@/utils/request'

export function getFarmerProfile() {
  return request({
    url: '/api/farmer/profile',
    method: 'get'
  }).catch(error => {
    console.error('API Error:', error)
    throw error
  })
} 