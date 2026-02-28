import { defineStore } from 'pinia';
import MembershipService from '../services/membership.service';

export const useMembershipStore = defineStore('membership', {
  state: () => ({
    member: null as any,
    loading: false
  }),
  actions: {
    async fetchMyMembership() {
      this.loading = true;
      try {
        const response = await MembershipService.getMyMembership();
        this.member = response.data;
      } catch (error) {
        console.error('Failed to fetch membership', error);
      } finally {
        this.loading = false;
      }
    },
    async dailyCheckIn() {
      try {
        const response = await MembershipService.dailyCheckIn();
        this.member = response.data; // Update local state
      } catch (error) {
        console.error('Check-in failed', error);
      }
    },
    async simulatePurchase(amount: number) {
      try {
        const response = await MembershipService.simulatePurchase(amount);
        this.member = response.data; // Update local state
      } catch (error) {
        console.error('Purchase simulation failed', error);
      }
    }
  }
});
