<template>
  <div class="forbidden-container">
    <el-result
      icon="warning"
      title="Access Denied"
      sub-title="You do not have permission to access this page."
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/')">Back to Home</el-button>
      </template>
    </el-result>
  </div>
</template>

<style scoped>
.forbidden-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>
