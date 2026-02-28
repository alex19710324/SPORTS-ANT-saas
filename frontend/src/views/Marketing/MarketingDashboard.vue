<template>
  <div class="marketing-dashboard" v-loading="loading">
    <h2>Marketing Campaigns</h2>
    
    <div class="actions">
      <el-button type="primary" @click="showCreateDialog = true">Create Campaign</el-button>
    </div>

    <!-- Activity List -->
    <el-table :data="activities" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="type" label="Type">
        <template #default="scope">
          <el-tag>{{ scope.row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="Status" />
      <el-table-column prop="startTime" label="Start Time" />
      <el-table-column label="Actions">
        <template #default="scope">
          <el-button size="small" @click="handleGenerateContent(scope.row)">AI Content</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Create Dialog -->
    <el-dialog v-model="showCreateDialog" title="New Campaign">
      <el-form :model="newActivity" label-width="120px">
        <el-form-item label="Name">
          <el-input v-model="newActivity.name" />
        </el-form-item>
        <el-form-item label="Type">
          <el-select v-model="newActivity.type">
            <el-option label="Group Buy" value="GROUP_BUY" />
            <el-option label="Flash Sale" value="FLASH_SALE" />
          </el-select>
        </el-form-item>
        <el-form-item label="Group Size" v-if="newActivity.type === 'GROUP_BUY'">
          <el-input-number v-model="newActivity.groupSize" :min="2" />
        </el-form-item>
        <el-form-item label="Discount" v-if="newActivity.type === 'GROUP_BUY'">
          <el-input-number v-model="newActivity.discount" :step="0.1" :max="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">Cancel</el-button>
        <el-button type="primary" @click="handleCreate">Create</el-button>
      </template>
    </el-dialog>

    <!-- AI Content Dialog -->
    <el-dialog v-model="showContentDialog" title="AI Generated Content">
      <div v-if="generatedContent">
        <h4>{{ generatedContent.wechat_title }}</h4>
        <p>{{ generatedContent.wechat_body }}</p>
        <img :src="generatedContent.poster_url" style="width: 100%; max-width: 300px;" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue';
import { useMarketingStore } from '../../stores/marketing.store';

const store = useMarketingStore();
const activities = computed(() => store.activities);
const generatedContent = computed(() => store.generatedContent);
const loading = computed(() => store.loading);

const showCreateDialog = ref(false);
const showContentDialog = ref(false);

const newActivity = reactive({
  name: '',
  type: 'GROUP_BUY',
  groupSize: 3,
  discount: 0.8
});

const handleCreate = async () => {
  const payload = {
    name: newActivity.name,
    type: newActivity.type,
    rulesJson: JSON.stringify({
      groupSize: newActivity.groupSize,
      discount: newActivity.discount
    })
  };
  await store.createActivity(payload);
  showCreateDialog.value = false;
};

const handleGenerateContent = async (activity: any) => {
  await store.generateContent(activity.id);
  showContentDialog.value = true;
};

onMounted(() => {
  store.fetchActivities();
});
</script>

<style scoped>
.actions {
  display: flex;
  justify-content: flex-end;
}
</style>
