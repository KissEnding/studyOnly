import request from '@/utils/request'

export function getProvinces() {
  return request({
    url: '/api/regions/provinces',
    method: 'get'
  })
}

export function getCities(provinceCode) {
  return request({
    url: `/api/regions/cities`,
    method: 'get',
    params: { provinceCode }
  })
}

export function getDistricts(cityCode) {
  return request({
    url: `/api/regions/districts`,
    method: 'get',
    params: { cityCode }
  })
} 