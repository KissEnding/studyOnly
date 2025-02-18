import request from '@/utils/request'

export function getFarmerList(status) {
  return request({
    url: '/api/admin/farmers',
    method: 'get',
    params: { status }
  })
}

// 添加审核接口
export function auditFarmer(id, data) {
  return request({
    url: `/api/admin/farmers/${id}/audit`,
    method: 'post',
    data
  })
} 