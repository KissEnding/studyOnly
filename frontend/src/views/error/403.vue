<template>
  <div class="error-page">
    <h1>403</h1>
    <h2>无权访问</h2>
    <p>抱歉，您没有权限访问此页面</p>
    <el-button type="primary" @click="$router.push('/login')">返回登录</el-button>
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
  color: #f56c6c;
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