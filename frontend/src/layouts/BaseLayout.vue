<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo-container">
        <img src="@/assets/platformLogo.png" alt="logo" class="logo">
        <span class="title">农安心保险</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="menu"
        router
      >
        <template v-for="item in menuItems" :key="item.path">
          <el-menu-item :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-content">
          <div class="breadcrumb">
            <el-breadcrumb>
              <el-breadcrumb-item>{{ currentRoute.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                {{ userName }}
                <el-icon><CaretBottom /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      
      <el-main>
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ArrowDown, CaretBottom } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const store = useStore()

const userName = computed(() => store.state.userInfo?.name || '用户')
const userRole = computed(() => store.state.userInfo?.userType)

const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route)

// 根据用户类型获取菜单项
const menuItems = computed(() => {
  const items = {
    farmer: [
      { path: '/farmer/dashboard', title: '我的主页', icon: 'HomeFilled' },
      { path: '/farmer/policies', title: '我的保单', icon: 'Document' },
      { path: '/farmer/claims', title: '理赔申请', icon: 'Money' }
    ],
    company: [
      { path: '/company/dashboard', title: '工作台', icon: 'HomeFilled' },
      { path: '/company/products', title: '保险产品', icon: 'Goods' },
      { path: '/company/policies', title: '保单管理', icon: 'Document' },
      { path: '/company/claims', title: '理赔管理', icon: 'Money' }
    ],
    admin: [
      { path: '/admin/dashboard', title: '控制台', icon: 'HomeFilled' },
      { path: '/admin/farmer-audit', title: '农户审核', icon: 'UserFilled' },
      { path: '/admin/company-audit', title: '公司审核', icon: 'Office' },
      { path: '/admin/statistics', title: '数据统计', icon: 'DataLine' }
    ]
  }
  return items[userRole.value] || []
})

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      // 根据用户角色跳转到对应的个人信息页面
      const profileRoutes = {
        farmer: '/farmer/profile',
        company: '/company/profile',
        admin: '/admin/profile'
      }
      const route = profileRoutes[userRole.value]
      if (route) {
        router.push(route)
      }
      break
    case 'logout':
      store.dispatch('logout')
      router.push('/login')
      break
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.logo-container {
  height: 60px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  background-color: var(--el-color-primary-dark-2);
}

.logo {
  width: 32px;
  height: 32px;
  border-radius: 50%;
}

.title {
  color: white;
  font-size: 18px;
  font-weight: bold;
}

.menu {
  border-right: none;
  height: calc(100vh - 60px);
}

.el-header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-content {
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 100%;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 0 12px;
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 