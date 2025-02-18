<template>
  <div class="error-page">
    <h1>404</h1>
    <h2>页面不存在</h2>
    <p>抱歉，您访问的页面不存在</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<style scoped>
.error-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

h1 {
  font-size: 72px;
  color: #909399;
  margin: 0;
}

h2 {
  font-size: 24px;
  color: #606266;
  margin: 20px 0;
}

p {
  color: #909399;
  margin-bottom: 30px;
}
</style> 