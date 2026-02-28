<template>
  <div class="security-dashboard" v-loading="loading">
    <div class="header">
        <h2>{{ $t('security.title') }}</h2>
        <el-button type="danger" @click="showReportModal = true">Report Incident</el-button>
    </div>
    
    <div class="alert-banner" v-if="incidents.length > 0">
        <h3>Active Incidents</h3>
        <el-table :data="incidents" style="width: 100%" stripe>
            <el-table-column prop="title" label="Title" />
            <el-table-column prop="severity" label="Severity">
                <template #default="scope">
                    <el-tag :type="getSeverityType(scope.row.severity)">{{ scope.row.severity }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="location" label="Location" />
            <el-table-column prop="status" label="Status" />
            <el-table-column label="Actions">
                <template #default="scope">
                    <el-button size="small" type="success" @click="resolveIncident(scope.row)">Resolve</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>

    <!-- Create Incident Modal -->
    <el-dialog v-model="showReportModal" title="Report Safety Incident">
        <el-form :model="newIncident" label-width="120px">
            <el-form-item label="Title">
                <el-input v-model="newIncident.title" placeholder="e.g. Wet Floor" />
            </el-form-item>
            <el-form-item label="Type">
                <el-select v-model="newIncident.type">
                    <el-option label="Injury" value="INJURY" />
                    <el-option label="Equipment Failure" value="EQUIPMENT_FAILURE" />
                    <el-option label="Security" value="SECURITY" />
                    <el-option label="Other" value="OTHER" />
                </el-select>
            </el-form-item>
            <el-form-item label="Severity">
                <el-select v-model="newIncident.severity">
                    <el-option label="Low" value="LOW" />
                    <el-option label="Medium" value="MEDIUM" />
                    <el-option label="High" value="HIGH" />
                    <el-option label="Critical" value="CRITICAL" />
                </el-select>
            </el-form-item>
            <el-form-item label="Location">
                <el-input v-model="newIncident.location" />
            </el-form-item>
            <el-form-item label="Description">
                <el-input v-model="newIncident.description" type="textarea" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showReportModal = false">Cancel</el-button>
            <el-button type="danger" @click="submitIncident">Report</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import apiClient from '../../../services/api';
import { ElMessage, ElMessageBox } from 'element-plus';

const loading = ref(false);
const incidents = ref<any[]>([]);
const showReportModal = ref(false);
const newIncident = ref({
    title: '',
    type: 'OTHER',
    severity: 'LOW',
    location: '',
    description: ''
});

const fetchIncidents = async () => {
    loading.value = true;
    try {
        const res = await apiClient.get('/safety/incidents/active');
        incidents.value = res.data;
    } catch (e) {
        ElMessage.error('Failed to load incidents');
    } finally {
        loading.value = false;
    }
};

const submitIncident = async () => {
    try {
        await apiClient.post('/safety/report', newIncident.value);
        ElMessage.success('Incident reported successfully');
        showReportModal.value = false;
        fetchIncidents();
        // Reset form
        newIncident.value = { title: '', type: 'OTHER', severity: 'LOW', location: '', description: '' };
    } catch (e) {
        ElMessage.error('Failed to report incident');
    }
};

const resolveIncident = async (incident: any) => {
    ElMessageBox.prompt('Resolution Notes:', 'Resolve Incident', {
        confirmButtonText: 'Resolve',
        cancelButtonText: 'Cancel',
    }).then(async (result) => {
        // 'result' can be { value: string, action: 'confirm' } or just 'confirm' depending on types
        // Casting to any to access value safely
        const notes = (result as any).value;
        try {
            await apiClient.put(`/safety/incidents/${incident.id}/resolve`, { notes: notes });
            ElMessage.success('Incident resolved');
            fetchIncidents();
        } catch (e) {
            ElMessage.error('Failed to resolve');
        }
    });
};

const getSeverityType = (severity: string) => {
    if (severity === 'CRITICAL') return 'danger';
    if (severity === 'HIGH') return 'warning';
    return 'info';
};

onMounted(() => {
    fetchIncidents();
});
</script>

<style scoped>
.security-dashboard {
    padding: 20px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.alert-banner {
    margin-bottom: 20px;
}
</style>