import request from '@/utils/request'

// 获取产品列表
export function getProducts(params) {
  return request({
    url: '/api/products',
    method: 'get',
    params
  })
}

// 获取产品详情
export function getProductDetail(id) {
  return request({
    url: `/api/products/${id}`,
    method: 'get'
  })
}

// 获取保险公司列表
export function getCompanies() {
  return request({
    url: '/api/companies',
    method: 'get'
  })
}

// 提交投保订单
export function submitOrder(data) {
  const formData = new FormData();
  formData.append('productId', data.productId);
  formData.append('area', data.area);
  formData.append('contactPerson', data.contactPerson || localStorage.getItem('userName'));
  formData.append('contactPhone', data.contactPhone);
  formData.append('address', data.address);
  formData.append('remarks', data.remarks || '');
  formData.append('premium', data.premium);
  formData.append('cityCode', data.cityCode);
  formData.append('districtCode', data.districtCode);
  formData.append('coverage', data.coverage || data.area * 1000); // 默认每亩1000元保额

  return request({
    url: '/api/market/orders',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 