<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="isCollapse"
    :unique-opened="false"
    :collapse-transition="false"
    class="sidebar-menu"
    @select="handleSelect"
  >
    <!-- 农户菜单 -->
    <template v-if="userRole === 'farmer'">
      <el-menu-item index="/farmer/dashboard">
        <el-icon><HomeFilled /></el-icon>
        <span>我的主页</span>
      </el-menu-item>
      <el-menu-item index="/farmer/market">
        <el-icon><ShoppingBag /></el-icon>
        <span>保险市场</span>
      </el-menu-item>
      <el-menu-item index="/farmer/policies">
        <el-icon><Document /></el-icon>
        <span>我的保单</span>
      </el-menu-item>
      <el-menu-item index="/farmer/claims">
        <el-icon><Money /></el-icon>
        <span>理赔申请</span>
      </el-menu-item>
    </template>

    <!-- 保险公司菜单 -->
    <template v-if="userRole === 'company'">
      <el-menu-item index="/company/dashboard">
        <el-icon><HomeFilled /></el-icon>
        <span>工作台</span>
      </el-menu-item>
      <el-menu-item index="/company/products">
        <el-icon><ShoppingBag /></el-icon>
        <span>保险产品</span>
      </el-menu-item>
      <el-menu-item index="/company/orders">
        <el-icon><Document /></el-icon>
        <span>投保单管理</span>
      </el-menu-item>
      <el-menu-item index="/company/policies">
        <el-icon><Files /></el-icon>
        <span>保单管理</span>
      </el-menu-item>
      <el-menu-item index="/company/claims">
        <el-icon><Money /></el-icon>
        <span>理赔管理</span>
      </el-menu-item>
    </template>

    <!-- 管理员菜单 -->
    <template v-if="userRole === 'admin'">
      <el-menu-item index="/admin/dashboard">
        <el-icon><HomeFilled /></el-icon>
        <span>控制台</span>
      </el-menu-item>
      <el-menu-item index="/admin/farmer-audit">
        <el-icon><User /></el-icon>
        <span>农户审核</span>
      </el-menu-item>
      <el-menu-item index="/admin/company-audit">
        <el-icon><Office /></el-icon>
        <span>公司审核</span>
      </el-menu-item>
      <el-menu-item index="/admin/statistics">
        <el-icon><DataLine /></el-icon>
        <span>数据统计</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  HomeFilled,
  Document,
  Files,
  Money,
  ShoppingBag,
  User,
  Office,
  DataLine
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const userRole = computed(() => localStorage.getItem('userRole'))
const activeMenu = computed(() => route.path)

const handleSelect = (key) => {
  if (key === route.path) {
    // 如果点击的是当前路由，强制刷新
    router.go(0)
  } else {
    router.push(key)
  }
}
</script>

<style scoped>
.sidebar-menu {
  height: 100%;
  border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
}
</style> 