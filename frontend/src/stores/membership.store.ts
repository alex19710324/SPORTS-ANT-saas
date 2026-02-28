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
    }
  }
});
