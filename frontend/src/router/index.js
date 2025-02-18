import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import BaseLayout from '@/layouts/BaseLayout.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/farmer',
    component: BaseLayout,
    meta: { requiresAuth: true, roles: ['farmer'] },
    children: [
      {
        path: '',
        redirect: 'dashboard'
      },
      {
        path: 'dashboard',
        name: 'FarmerDashboard',
        component: () => import('@/views/farmer/Dashboard.vue'),
        meta: { title: '我的主页' }
      },
      {
        path: 'policies',
        name: 'FarmerPolicies',
        component: () => import('@/views/farmer/Policies.vue'),
        meta: { title: '我的保单' }
      },
      {
        path: 'claims',
        name: 'FarmerClaims',
        component: () => import('@/views/farmer/Claims.vue'),
        meta: { title: '理赔申请' }
      },
      {
        path: 'profile',
        name: 'FarmerProfile',
        component: () => import('@/views/farmer/Profile.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'market',
        name: 'Market',
        component: () => import('@/views/farmer/Market.vue'),
        meta: { title: '保险市场' }
      },
      {
        path: 'consultation',
        name: 'FarmerConsultation',
        component: () => import('@/views/farmer/Consultation.vue'),
        meta: { title: '农保咨询' }
      }
    ]
  },
  {
    path: '/products/:id',
    name: 'ProductDetail',
    component: () => import('@/views/farmer/ProductDetail.vue'),
    meta: { title: '产品详情', requiresAuth: true }
  },
  {
    path: '/products/:id/apply',
    name: 'InsuranceApplication',
    component: () => import('@/views/farmer/InsuranceApplication.vue'),
    meta: { title: '投保申请', requiresAuth: true }
  },
  {
    path: '/company',
    component: BaseLayout,
    meta: { requiresAuth: true, roles: ['company'] },
    children: [
      {
        path: '',
        redirect: 'dashboard'
      },
      {
        path: 'dashboard',
        name: 'CompanyDashboard',
        component: () => import('@/views/company/Dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'products',
        name: 'ProductList',
        component: () => import('@/views/company/ProductList.vue'),
        meta: { title: '产品管理' }
      },
      {
        path: 'orders',
        name: 'OrderManagement',
        component: () => import('@/views/company/OrderManagement.vue'),
        meta: { title: '投保单管理' }
      },
      {
        path: 'policies',
        name: 'CompanyPolicies',
        component: () => import('@/views/company/Policies.vue'),
        meta: { title: '保单管理' }
      },
      {
        path: 'claims',
        name: 'CompanyClaims',
        component: () => import('@/views/company/Claims.vue'),
        meta: { title: '理赔管理' }
      },
      {
        path: 'profile',
        name: 'CompanyProfile',
        component: () => import('@/views/company/Profile.vue'),
        meta: { title: '公司信息' }
      }
    ]
  },
  {
    path: '/admin',
    component: BaseLayout,
    meta: { requiresAuth: true, roles: ['admin'] },
    children: [
      {
        path: '',
        redirect: 'dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'farmer-audit',
        name: 'FarmerAudit',
        component: () => import('@/views/admin/FarmerAudit.vue'),
        meta: { title: '农户审核' }
      },
      {
        path: 'company-audit',
        name: 'CompanyAudit',
        component: () => import('@/views/admin/CompanyAudit.vue'),
        meta: { title: '公司审核' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { title: '数据统计' }
      }
    ]
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: { title: '无权访问' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
  console.log('Route change:', { from: from.path, to: to.path })
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const token = localStorage.getItem('token')
    const userRole = localStorage.getItem('userRole')
    
    console.log('Auth check:', { 
      token: !!token,
      userRole,
      requiredRoles: to.meta.roles
    })

    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    if (to.meta.roles && !to.meta.roles.includes(userRole)) {
      console.log('Permission denied')
      next({ path: '/403' })
      return
    }
  }
  next()
})

export default router
