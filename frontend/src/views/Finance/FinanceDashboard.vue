<template>
  <div class="finance-dashboard" v-loading="loading">
    <h2>Finance & Accounting</h2>
    
    <!-- AI Prediction Card -->
    <el-card class="forecast-card" v-if="cashFlowForecast">
      <template #header>
        <div class="card-header">
          <span>AI Cash Flow Prediction</span>
          <el-tag type="success">Confidence: {{ cashFlowForecast.confidence * 100 }}%</el-tag>
        </div>
      </template>
      <div class="forecast-list">
        <div v-for="item in cashFlowForecast.forecast" :key="item.date" class="forecast-item">
          <span class="date">{{ item.date }}</span>
          <span class="amount">¥{{ item.balance }}</span>
        </div>
      </div>
    </el-card>

    <!-- Tax Calculator -->
    <el-card class="tax-card">
      <template #header>Quick Tax Calculator</template>
      <el-form :inline="true">
        <el-form-item label="Amount">
          <el-input-number v-model="taxAmount" />
        </el-form-item>
        <el-form-item label="Country">
          <el-select v-model="taxCountry">
            <el-option label="China" value="CN" />
            <el-option label="USA" value="US" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCalculateTax">Calculate</el-button>
        </el-form-item>
      </el-form>
      <div v-if="taxResult" class="tax-result">
        <p>Tax Rate: {{ taxResult.taxRate * 100 }}%</p>
        <p>Tax Amount: ¥{{ taxResult.taxAmount }}</p>
      </div>
    </el-card>

    <!-- Voucher List -->
    <h3>Accounting Vouchers</h3>
    <el-table :data="vouchers" style="width: 100%">
      <el-table-column prop="voucherNo" label="Voucher No" />
      <el-table-column prop="sourceType" label="Type" />
      <el-table-column prop="debitAccount" label="Debit" />
      <el-table-column prop="creditAccount" label="Credit" />
      <el-table-column prop="amount" label="Amount" />
      <el-table-column prop="postedAt" label="Posted At" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useFinanceStore } from '../../stores/finance.store';

const store = useFinanceStore();
const vouchers = computed(() => store.vouchers);
const cashFlowForecast = computed(() => store.cashFlowForecast);
const taxResult = computed(() => store.taxResult);
const loading = computed(() => store.loading);

const taxAmount = ref(1000);
const taxCountry = ref('CN');

const handleCalculateTax = () => {
  store.calculateTax(taxAmount.value, taxCountry.value);
};

onMounted(() => {
  store.fetchVouchers();
  store.fetchCashFlowPrediction();
});
</script>

<style scoped>
.forecast-card, .tax-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forecast-list {
  display: flex;
  gap: 20px;
}

.forecast-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #f0f9eb;
  padding: 10px;
  border-radius: 4px;
}

.amount {
  font-weight: bold;
  color: #67c23a;
}
</style>
