<template>
  <el-dialog :model-value="modelValue" title="Create Marketing Campaign" width="600px" @update:model-value="$emit('update:modelValue', $event)">
    <el-steps :active="activeStep" finish-status="success" simple style="margin-bottom: 20px">
        <el-step title="Details" />
        <el-step title="Targeting" />
        <el-step title="Review" />
    </el-steps>

    <div v-if="activeStep === 0">
        <el-form label-width="100px">
            <el-form-item label="Campaign Name">
                <el-input v-model="form.name" placeholder="e.g. Summer Sale" />
            </el-form-item>
            <el-form-item label="Description">
                <el-input type="textarea" v-model="form.description" />
            </el-form-item>
        </el-form>
    </div>

    <div v-if="activeStep === 1">
        <el-form label-width="100px">
            <el-form-item label="Target Segment">
                <el-select v-model="form.targetSegment" placeholder="Select Segment">
                    <el-option label="Active Members" value="ACTIVE_MEMBERS" />
                    <el-option label="Lapsed Members" value="LAPSED_MEMBERS" />
                    <el-option label="All Users" value="ALL" />
                </el-select>
            </el-form-item>
            <el-alert type="info" :closable="false" show-icon>
                Estimated Reach: {{ getEstimatedReach() }} users
            </el-alert>
        </el-form>
    </div>

    <div v-if="activeStep === 2">
        <el-descriptions title="Summary" border :column="1">
            <el-descriptions-item label="Name">{{ form.name }}</el-descriptions-item>
            <el-descriptions-item label="Segment">{{ form.targetSegment }}</el-descriptions-item>
            <el-descriptions-item label="Reach">{{ getEstimatedReach() }}</el-descriptions-item>
        </el-descriptions>
    </div>

    <template #footer>
        <el-button @click="$emit('update:modelValue', false)">Cancel</el-button>
        <el-button v-if="activeStep > 0" @click="activeStep--">Back</el-button>
        <el-button v-if="activeStep < 2" type="primary" @click="activeStep++">Next</el-button>
        <el-button v-if="activeStep === 2" type="success" :loading="loading" @click="submitCampaign">Create Campaign</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import apiClient from '../../services/api';
import { ElMessage } from 'element-plus';

const props = defineProps(['modelValue']);
const emit = defineEmits(['update:modelValue', 'created']);

const activeStep = ref(0);
const loading = ref(false);

const form = reactive({
    name: '',
    description: '',
    targetSegment: 'ACTIVE_MEMBERS'
});

const getEstimatedReach = () => {
    if (form.targetSegment === 'ACTIVE_MEMBERS') return 850;
    if (form.targetSegment === 'LAPSED_MEMBERS') return 320;
    return 1200;
};

const submitCampaign = async () => {
    loading.value = true;
    try {
        await apiClient.post('/marketing/campaigns', form);
        ElMessage.success('Campaign Created Successfully');
        emit('update:modelValue', false);
        emit('created');
        // Reset
        activeStep.value = 0;
        form.name = '';
        form.description = '';
    } catch (e) {
        ElMessage.error('Failed to create campaign');
    } finally {
        loading.value = false;
    }
};
</script>
