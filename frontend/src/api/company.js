import request from '@/utils/request'

// 获取投保单列表
export function getOrders(params) {
  return request({
    url: '/api/company/orders',
    method: 'get',
    params
  })
}

// 审核投保单
export function auditOrder(id, data) {
  return request({
    url: `/api/company/orders/${id}/audit`,
    method: 'post',
    data
  })
}

// 获取产品列表
export function getProducts() {
  return request({
    url: '/api/company/products',
    method: 'get'
  })
}

// 创建产品
export function createProduct(data) {
  return request({
    url: '/api/company/products',
    method: 'post',
    data
  })
}

// 更新产品
export function updateProduct(id, data) {
  return request({
    url: `/api/company/products/${id}`,
    method: 'put',
    data
  })
}

// 发布产品
export function publishProduct(id) {
  return request({
    url: `/api/company/products/${id}/publish`,
    method: 'post'
  })
}

// 下架产品
export function unpublishProduct(id) {
  return request({
    url: `/api/company/products/${id}/unpublish`,
    method: 'post'
  })
}

// 切换产品可见性
export function toggleProductVisibility(id) {
  return request({
    url: `/api/company/products/${id}/visibility`,
    method: 'put'
  })
}

// 复制产品
export function copyProduct(id) {
  return request({
    url: `/api/company/products/${id}/copy`,
    method: 'post'
  })
}

// 删除产品
export function deleteProduct(id) {
  return request({
    url: `/api/company/products/${id}`,
    method: 'delete'
  })
}

// 获取公司信息
export function getCompanyInfo() {
  return request({
    url: '/api/company/info',
    method: 'get'
  })
}

// 更新公司信息
export function updateCompanyInfo(data) {
  return request({
    url: '/api/company/info',
    method: 'put',
    data
  })
} 