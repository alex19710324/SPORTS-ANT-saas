<template>
  <div class="membership-card" v-loading="loading">
    <div v-if="member">
      <div class="card-header">
        <div class="level-badge" :class="'level-' + member.currentLevel.levelOrder">
          {{ member.currentLevel.name }}
        </div>
        <div class="level-info">
          Level {{ member.currentLevel.levelOrder }}
        </div>
      </div>
      
      <div class="growth-section">
        <div class="growth-text">
          <span>Current Growth: {{ member.growthValue }}</span>
          <span v-if="nextLevelGrowth">Next Level: {{ nextLevelGrowth }}</span>
        </div>
        <el-progress 
          :percentage="progressPercentage" 
          :status="progressStatus"
          :stroke-width="15"
          striped
          striped-flow
        ></el-progress>
      </div>

      <div class="benefits-section">
        <h4>Member Benefits</h4>
        <div class="benefits-list">
          <el-tag v-for="(value, key) in benefits" :key="key" class="benefit-tag">
            {{ key }}: {{ value }}
          </el-tag>
          <el-tag v-if="Object.keys(benefits).length === 0" type="info">No specific benefits</el-tag>
        </div>
      </div>
      
      <div class="ai-insights" v-if="member.tags">
         <h4>AI Insights</h4>
         <div class="tags">
             <el-tag v-for="tag in member.tags.split(',')" :key="tag" type="warning" effect="dark">
                 {{ tag }}
             </el-tag>
         </div>
      </div>
    </div>
    <div v-else class="no-data">
      <el-empty description="Membership data not found"></el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useMembershipStore } from '../stores/membership.store';

const store = useMembershipStore();

onMounted(() => {
  store.fetchMyMembership();
});

const member = computed(() => store.member);
const loading = computed(() => store.loading);

const nextLevelGrowth = computed(() => {
    // Simple logic: assume next level is current + 1. In real app, fetch all levels.
    if (!member.value) return 1000;
    const currentOrder = member.value.currentLevel.levelOrder;
    if (currentOrder === 1) return 1000;
    if (currentOrder === 2) return 5000;
    if (currentOrder === 3) return 20000;
    if (currentOrder === 4) return 50000;
    return null; // Max level
});

const progressPercentage = computed(() => {
    if (!member.value || !nextLevelGrowth.value) return 100;
    const current = member.value.growthValue;
    const target = nextLevelGrowth.value;
    return Math.min(Math.round((current / target) * 100), 100);
});

const progressStatus = computed(() => {
    if (progressPercentage.value >= 100) return 'success';
    return '';
});

const benefits = computed(() => {
    if (!member.value || !member.value.currentLevel.benefitsJson) return {};
    try {
        return JSON.parse(member.value.currentLevel.benefitsJson);
    } catch (e) {
        return {};
    }
});
</script>

<style scoped>
.membership-card {
  background: linear-gradient(135deg, #2c3e50 0%, #4ca1af 100%);
  color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.2);
  min-height: 200px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.level-badge {
  font-size: 1.5em;
  font-weight: bold;
  padding: 5px 15px;
  border-radius: 20px;
  background: rgba(255,255,255,0.2);
  backdrop-filter: blur(5px);
}

.growth-section {
  margin-bottom: 20px;
}

.growth-text {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 0.9em;
  opacity: 0.9;
}

.benefits-section h4, .ai-insights h4 {
  margin-bottom: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.3);
  padding-bottom: 5px;
}

.benefit-tag {
  margin-right: 10px;
  margin-bottom: 5px;
}

.ai-insights {
    margin-top: 15px;
    padding-top: 10px;
    border-top: 1px dashed rgba(255,255,255,0.3);
}

/* Level Colors */
.level-1 { color: #f0f0f0; }
.level-2 { color: #a0d8ef; }
.level-3 { color: #ffd700; text-shadow: 0 0 5px #ffd700; }
.level-4 { color: #e0e0e0; text-shadow: 0 0 10px #fff; }
.level-5 { color: #ff4500; text-shadow: 0 0 15px #ff4500; }
</style>
