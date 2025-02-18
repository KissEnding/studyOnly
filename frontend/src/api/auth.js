import request from '@/utils/request'

export function login(data) {
  console.log('Sending login request to:', '/api/auth/login')
  console.log('Request data:', data)
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

export function registerCompany(data) {
  return request({
    url: '/api/auth/register/company',
    method: 'post',
    data
  })
}

export function registerFarmer(data) {
  console.log('Registering farmer with data:', data);
  return request({
    url: '/api/auth/register/farmer',
    method: 'post',
    data
  })
}

export function getParentCompanies() {
  return request({
    url: '/api/auth/companies/headquarters',
    method: 'get'
  })
} 