import request from '@/utils/request'

// 获取保单列表
export function getPolicies(params) {
  return request({
    url: '/api/policies',
    method: 'get',
    params
  })
}

// 获取保单详情
export function getPolicy(id) {
  return request({
    url: `/api/policies/${id}`,
    method: 'get'
  })
}

// 获取保单统计数据
export function getPolicyStatistics() {
  return request({
    url: '/api/policies/statistics',
    method: 'get'
  })
}

// 创建投保申请
export function createApplication(data) {
  return request({
    url: '/api/policies/applications',
    method: 'post',
    data
  })
} 