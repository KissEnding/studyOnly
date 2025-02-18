import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 直接返回响应数据
    return response.data
  },
  error => {
    console.error('Response error:', error)
    const response = error.response
    if (response?.status === 401) {
      ElMessage.error('请先登录')
      router.push('/login')
    } else if (response?.status === 403) {
      ElMessage.error('没有权限访问')
      router.push('/login')
    } else {
      const errorMessage = response?.data?.message || response?.data?.error || error.message || '请求失败'
      console.error('Error message:', errorMessage)
      ElMessage.error(errorMessage)
    }
    return Promise.reject(error)
  }
)

export default service 