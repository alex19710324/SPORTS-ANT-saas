<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>{{ $t('login.title') }}</span>
          <el-dropdown @command="switchLang">
            <span class="el-dropdown-link">
              Language<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="zh-CN">简体中文</el-dropdown-item>
                <el-dropdown-item command="en-US">English</el-dropdown-item>
                <el-dropdown-item command="fr-FR">Français</el-dropdown-item>
                <el-dropdown-item command="it-IT">Italiano</el-dropdown-item>
                <el-dropdown-item command="de-DE">Deutsch</el-dropdown-item>
                <el-dropdown-item command="es-ES">Español</el-dropdown-item>
                <el-dropdown-item command="pt-PT">Português</el-dropdown-item>
                <el-dropdown-item command="ar-SA">العربية</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </template>
      <el-form :model="loginForm" ref="formRef" :rules="rules" label-width="80px">
        <el-form-item :label="$t('login.username')" prop="username">
          <el-input v-model="loginForm.username" :placeholder="$t('login.placeholder.username')"></el-input>
        </el-form-item>
        <el-form-item :label="$t('login.password')" prop="password">
          <el-input v-model="loginForm.password" type="password" :placeholder="$t('login.placeholder.password')" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading">{{ $t('login.submit') }}</el-button>
          <el-button @click="resetForm">{{ $t('login.reset') }}</el-button>
        </el-form-item>
      </el-form>
      <div v-if="message" class="error-message">
        {{ message }}
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useAuthStore } from '../stores/auth.store';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowDown } from '@element-plus/icons-vue';
import type { FormInstance, FormRules } from 'element-plus';
import { loadLanguageAsync } from '../i18n';

const authStore = useAuthStore();
const router = useRouter();
const formRef = ref<FormInstance>();
const loading = ref(false);
const message = ref('');

const loginForm = reactive({
  username: '',
  password: ''
});

const rules = reactive<FormRules>({
  username: [
    { required: true, message: 'Please input username', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Please input password', trigger: 'blur' },
  ],
});

const handleLogin = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await authStore.login(loginForm);
        ElMessage.success('Login successful');
        loading.value = false;
        router.push('/');
      } catch (error: any) {
        message.value =
          (error.response && error.response.data && error.response.data.message) ||
          error.message ||
          error.toString();
        loading.value = false;
      }
    }
  });
};

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

const switchLang = async (lang: string) => {
    await loadLanguageAsync(lang);
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  width: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.error-message {
  color: red;
  margin-top: 10px;
  text-align: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  display: flex;
  align-items: center;
}
</style>
