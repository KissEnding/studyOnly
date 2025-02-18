<template>
  <div class="login-container">
    <div class="header">
      <div class="logo-container">
        <img src="@/assets/platformLogo.png" alt="logo" class="platform-logo">
        <div class="platform-title">
          <h1 class="chinese-title">农安心保险服务平台</h1>
          <p class="english-title">Agricultural Insurance Service Platform</p>
        </div>
      </div>
    </div>
    <div class="login-box">
      <div class="welcome-text">
        <h2>欢迎使用</h2>
        <p>为三农保驾护航，让农业更有保障</p>
      </div>
      <div class="role-switcher">
        <div 
          v-for="role in roles" 
          :key="role.value"
          :class="['role-item', { active: loginForm.userType === role.value }]"
          @click="switchRole(role.value)"
        >
          <span class="role-icon-wrapper">
            <component :is="role.icon" />
          </span>
          <span>{{ role.label }}</span>
        </div>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            :loading="loading" 
            @click="handleLogin"
            class="login-button"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-links">
        <el-button 
          type="text" 
          @click="goToRegister('company')"
          class="register-link"
        >
          保险公司入驻
        </el-button>
        <el-divider direction="vertical" />
        <el-button 
          type="text" 
          @click="goToRegister('farmer')"
          class="register-link"
        >
          农户注册
        </el-button>
      </div>
      <div class="login-footer">
        <p>中国农业保险监督管理委员会 版权所有</p>
        <p>Copyright © 2024 Agricultural Insurance Regulatory Commission</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, markRaw, defineAsyncComponent } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { User, Lock, Management, Office } from '@element-plus/icons-vue'
import { login } from '@/api/auth'

const router = useRouter()
const store = useStore()
const loginFormRef = ref(null)
const loading = ref(false)

const roles = [
  { 
    label: '农户', 
    value: 'farmer', 
    icon: markRaw(defineAsyncComponent(() => import('@/icons/Farmer.vue')))
  },
  { 
    label: '保险公司', 
    value: 'company', 
    icon: markRaw(defineAsyncComponent(() => import('@/icons/Insurance.vue')))
  },
  { 
    label: '管理员', 
    value: 'admin', 
    icon: markRaw(defineAsyncComponent(() => import('@/icons/Admin.vue')))
  }
]

const loginForm = reactive({
  username: '',
  password: '',
  userType: 'farmer' // 默认选中农户
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const switchRole = (role) => {
  loginForm.userType = role
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    const valid = await loginFormRef.value.validate()
    if (valid) {
      loading.value = true
      console.log('Validating login form...')
      
      const response = await login(loginForm)
      console.log('Login response:', response)
      
      // 保存登录信息
      localStorage.setItem('token', response.token)
      localStorage.setItem('userRole', response.userType.toLowerCase())  // 转换为小写
      localStorage.setItem('username', response.username)  // 存储登录账户名而不是真实姓名
      
      store.commit('SET_TOKEN', response.token)
      store.commit('SET_USER_INFO', {
        userType: response.userType.toLowerCase(),  // 转换为小写
        username: response.username,
        name: response.name
      })
      
      ElMessage.success('登录成功')
      
      // 根据用户类型跳转到不同的首页
      const redirectMap = {
        admin: '/admin/dashboard',
        company: '/company/dashboard',
        farmer: '/farmer/dashboard'
      }
      
      const userType = response.userType.toLowerCase()  // 转换为小写
      const redirectPath = redirectMap[userType]
      
      if (redirectPath) {
        router.push(redirectPath)
      } else {
        ElMessage.error(`未知的用户类型: ${response.userType}`)
        console.error('Unknown user type:', response.userType)
      }
    }
  } catch (error) {
    console.error('Login error:', error)
    ElMessage.error(error.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const goToRegister = (type) => {
  router.push({
    path: '/register',
    query: { type }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  width: 100vw;
  background: url('@/assets/back.png') center/cover no-repeat fixed;
  display: flex;
  flex-direction: column;
  position: relative;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(3px);
}

.header {
  position: relative;
  padding: 20px 50px;
  z-index: 1;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.platform-logo {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.platform-title {
  display: flex;
  flex-direction: column;
}

.chinese-title {
  font-family: "ZCOOL XiaoWei", cursive;
  font-size: 32px;
  color: #1d953f;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.english-title {
  font-size: 14px;
  color: #666;
  margin: 5px 0 0;
}

.login-box {
  position: relative;
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  margin: 40px auto;
  z-index: 1;
}

.welcome-text {
  text-align: center;
  margin-bottom: 30px;
}

.welcome-text h2 {
  font-size: 24px;
  color: #1d953f;
  margin: 0 0 10px;
}

.welcome-text p {
  color: #666;
  margin: 0;
}

.role-switcher {
  display: flex;
  justify-content: space-between;
  background: #f5f7fa;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 30px;
}

.role-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s ease;
  color: #606266;
  gap: 8px;
}

.role-icon-wrapper {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.role-item:hover {
  color: var(--el-color-primary);
}

.role-item.active {
  background: white;
  color: var(--el-color-primary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-input__wrapper) {
  background-color: rgba(245, 247, 250, 0.8);
  box-shadow: none !important;
  border: 1px solid #e4e7ed;
  height: 45px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: var(--el-color-primary);
  transform: translateY(-1px);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 1px var(--el-color-primary) !important;
  background-color: white;
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 10px;
  transition: all 0.3s ease;
  background: linear-gradient(45deg, #1d953f, #2bb04a);
}

.login-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(29, 149, 63, 0.3);
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  color: #999;
  font-size: 12px;
}

.login-footer p {
  margin: 5px 0;
}

.register-links {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.register-link {
  color: var(--el-color-primary);
  font-size: 14px;
}

.register-link:hover {
  color: var(--el-color-primary-light-3);
}

@media screen and (max-width: 768px) {
  .header {
    padding: 15px;
  }
  
  .platform-logo {
    width: 60px;
    height: 60px;
  }
  
  .chinese-title {
    font-size: 24px;
  }
  
  .login-box {
    width: 90%;
    max-width: 400px;
    padding: 30px 20px;
    margin: 20px auto;
  }
}
</style> 