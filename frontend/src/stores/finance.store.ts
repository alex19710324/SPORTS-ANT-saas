import { defineStore } from 'pinia';
import FinanceService from '../services/finance.service';

export const useFinanceStore = defineStore('finance', {
  state: () => ({
    vouchers: [] as any[],
    cashFlowForecast: null as any,
    taxResult: null as any,
    loading: false
  }),
  actions: {
    async fetchVouchers() {
      this.loading = true;
      try {
        const response = await FinanceService.listVouchers();
        this.vouchers = response.data;
      } catch (error) {
        console.error('Failed to fetch vouchers', error);
      } finally {
        this.loading = false;
      }
    },
    
    async fetchCashFlowPrediction() {
      try {
        const response = await FinanceService.predictCashFlow();
        this.cashFlowForecast = response.data;
      } catch (error) {
        console.error('Failed to predict cash flow', error);
      }
    },
    
    async calculateTax(amount: number, country: string) {
      try {
        const response = await FinanceService.calculateTax({ amount, country });
        this.taxResult = response.data;
      } catch (error) {
        console.error('Failed to calculate tax', error);
      }
    }
  }
});
