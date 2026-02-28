<template>
  <div class="ai-suggestions">
    <h3>🤖 AI Suggestions ({{ suggestions.length }})</h3>
    <el-scrollbar height="400px">
      <div v-if="suggestions.length === 0" class="no-suggestions">
        <el-empty description="No pending suggestions. System is optimal."></el-empty>
      </div>
      <el-card v-for="suggestion in suggestions" :key="suggestion.id" class="suggestion-card">
        <template #header>
          <div class="card-header">
            <span>{{ suggestion.title }}</span>
            <el-tag :type="getPriorityType(suggestion.priority)">{{ suggestion.priority }}</el-tag>
          </div>
        </template>
        <div class="content">
          <p>{{ suggestion.content }}</p>
        </div>
        <div class="actions">
          <el-button type="success" size="small" @click="handleAction(suggestion, true)">
            {{ suggestion.actionLink ? 'Execute' : 'Acknowledge' }}
          </el-button>
          <el-button type="danger" size="small" @click="handleAction(suggestion, false)">Dismiss</el-button>
        </div>
      </el-card>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useAiStore } from '../../stores/ai.store';
import apiClient from '../../services/api';
import { ElMessage, ElMessageBox } from 'element-plus';

const aiStore = useAiStore();
const suggestions = computed(() => aiStore.suggestions);

onMounted(() => {
  aiStore.fetchSuggestions();
  // Poll for new insights every 30s
  setInterval(() => aiStore.fetchSuggestions(), 30000);
});

const getPriorityType = (priority: string) => {
  switch (priority) {
    case 'CRITICAL': return 'danger';
    case 'HIGH': return 'warning';
    case 'MEDIUM': return 'info';
    default: return '';
  }
};

const handleAction = async (suggestion: any, accepted: boolean) => {
  try {
    if (accepted) {
        if (suggestion.actionLink) {
            await ElMessageBox.confirm(`Do you want AI to execute: ${suggestion.title}?`, 'AI Execution', {
                confirmButtonText: 'Execute',
                cancelButtonText: 'Cancel',
                type: 'warning'
            });
            
            // Execute Action
            await apiClient.post('/ai/execute', { actionLink: suggestion.actionLink });
            ElMessage.success('Action executed successfully');
        } else {
            ElMessage.success('Suggestion acknowledged');
        }
    }
    
    // Mark as handled in backend
    await aiStore.handleSuggestion(suggestion.id, accepted, accepted ? 'Executed' : 'Dismissed');
  } catch (e) {
      // Cancelled
  }
};
</script>

<style scoped>
.ai-suggestions {
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}
.suggestion-card {
  margin-bottom: 10px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}
</style>
