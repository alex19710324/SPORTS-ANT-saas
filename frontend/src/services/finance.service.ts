import apiClient from './api';

export default {
  listVouchers() {
    return apiClient.get('/finance/vouchers');
  },
  
  calculateTax(payload: any) {
    return apiClient.post('/finance/tax/calculate', payload);
  },
  
  predictCashFlow() {
    return apiClient.get('/finance/forecast');
  }
};
