<template>
  <div class="i18n-dashboard" v-loading="loading">
    <h2>Translation Management</h2>
    
    <el-card>
      <template #header>Create New Key</template>
      <el-form :model="newKey" label-width="120px">
        <el-form-item label="Key Name">
          <el-input v-model="newKey.keyName" placeholder="e.g. common.welcome" />
        </el-form-item>
        <el-form-item label="中文 (Source)">
          <el-input v-model="newKey.zhCn" type="textarea" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCreate">Save & Auto Translate</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="preview-section">
      <h3>Preview</h3>
      <p>Current Lang: {{ store.currentLang }}</p>
      <div class="demo-box">
        {{ store.t(newKey.keyName) }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue';
import { useI18nStore } from '../../stores/i18n.store';
import I18nService from '../../services/i18n.service';

const store = useI18nStore();
const loading = computed(() => store.loading);

const newKey = reactive({
  keyName: '',
  zhCn: '',
  enUs: '',
  jaJp: ''
});

const handleCreate = async () => {
  try {
    const res = await I18nService.createKey(newKey);
    // Trigger Auto Translate
    await I18nService.autoTranslate(res.data.id);
    alert('Key created and sent for translation!');
  } catch (e) {
    console.error(e);
  }
};
</script>

<style scoped>
.preview-section {
  margin-top: 30px;
  padding: 20px;
  background: #f5f7fa;
}
.demo-box {
  font-size: 1.2em;
  font-weight: bold;
}
</style>
