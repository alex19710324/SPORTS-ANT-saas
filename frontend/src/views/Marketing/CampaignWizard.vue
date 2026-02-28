<template>
  <el-dialog v-model="visible" title="Create Marketing Campaign" width="600px">
    <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step title="Basics" />
        <el-step title="Content (AI)" />
        <el-step title="Schedule" />
    </el-steps>

    <div class="wizard-content">
        <!-- Step 1: Basics -->
        <el-form v-if="activeStep === 0" :model="form" label-width="100px">
            <el-form-item label="Campaign Name">
                <el-input v-model="form.name" placeholder="e.g., Summer Sale" />
            </el-form-item>
            <el-form-item label="Type">
                <el-select v-model="form.type" placeholder="Select Type">
                    <el-option label="Group Buy" value="GROUP_BUY" />
                    <el-option label="Flash Sale" value="FLASH_SALE" />
                    <el-option label="Coupon" value="COUPON" />
                </el-select>
            </el-form-item>
        </el-form>

        <!-- Step 2: Content -->
        <div v-if="activeStep === 1">
            <div class="ai-box">
                <el-button type="warning" icon="MagicStick" :loading="generating" @click="generateContent">
                    Auto-Generate with AI
                </el-button>
                <div v-if="aiContent" class="preview-box">
                    <h4>{{ aiContent.wechat_title }}</h4>
                    <p>{{ aiContent.wechat_body }}</p>
                    <img :src="aiContent.poster_url" class="poster-preview" />
                </div>
            </div>
        </div>

        <!-- Step 3: Schedule -->
        <el-form v-if="activeStep === 2" :model="form" label-width="100px">
            <el-form-item label="Start Time">
                <el-date-picker v-model="form.startTime" type="datetime" placeholder="Select date and time" />
            </el-form-item>
            <el-form-item label="End Time">
                <el-date-picker v-model="form.endTime" type="datetime" placeholder="Select date and time" />
            </el-form-item>
        </el-form>
    </div>

    <template #footer>
        <el-button @click="visible = false">Cancel</el-button>
        <el-button v-if="activeStep > 0" @click="activeStep--">Previous</el-button>
        <el-button v-if="activeStep < 2" type="primary" @click="activeStep++">Next</el-button>
        <el-button v-if="activeStep === 2" type="success" @click="submit">Launch Campaign</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';

const props = defineProps(['modelValue']);
const emit = defineEmits(['update:modelValue', 'created']);

const visible = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
});

const activeStep = ref(0);
const generating = ref(false);
const aiContent = ref<any>(null);

const form = ref({
    name: '',
    type: 'GROUP_BUY',
    startTime: '',
    endTime: ''
});

const generateContent = async () => {
    if (!form.value.name) {
        ElMessage.warning('Please enter a campaign name first');
        return;
    }
    generating.value = true;
    try {
        // Mocking the generate call logic since we haven't saved the campaign yet
        // In real flow: Save Draft -> Generate -> Update Draft
        // Here we simulate the AI response directly for UI
        await new Promise(r => setTimeout(r, 1500));
        
        if (form.value.type === 'GROUP_BUY') {
            aiContent.value = {
                wechat_title: "🔥 Limited Time Group Buy: " + form.value.name + "!",
                wechat_body: "Gather your friends and get amazing discounts! Only for " + form.value.name + ". Join now!",
                poster_url: "https://via.placeholder.com/300x400?text=Group+Buy"
            };
        } else {
            aiContent.value = {
                wechat_title: "Special Offer: " + form.value.name,
                wechat_body: "Don't miss out on " + form.value.name,
                poster_url: "https://via.placeholder.com/300x400?text=Offer"
            };
        }
        ElMessage.success('Content generated!');
    } catch (error) {
        ElMessage.error('Failed to generate content');
    } finally {
        generating.value = false;
    }
};

const submit = async () => {
    try {
        await apiClient.post('/marketing/campaigns', {
            ...form.value,
            status: 'ACTIVE',
            aiGeneratedContent: JSON.stringify(aiContent.value)
        });
        ElMessage.success('Campaign Launched!');
        visible.value = false;
        emit('created');
        // Reset
        activeStep.value = 0;
        form.value = { name: '', type: 'GROUP_BUY', startTime: '', endTime: '' };
        aiContent.value = null;
    } catch (error) {
        ElMessage.error('Failed to create campaign');
    }
};
</script>

<style scoped>
.wizard-content {
    margin: 30px 0;
    min-height: 200px;
}
.ai-box {
    text-align: center;
}
.preview-box {
    margin-top: 20px;
    border: 1px solid #eee;
    padding: 15px;
    border-radius: 8px;
    text-align: left;
    background: #f9f9f9;
}
.poster-preview {
    max-width: 100%;
    margin-top: 10px;
    border-radius: 4px;
}
</style>
