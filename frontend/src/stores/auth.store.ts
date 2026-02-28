import { defineStore } from 'pinia';
import AuthService from '../services/auth.service';

const user = JSON.parse(localStorage.getItem('user') || 'null');
const initialState = user
  ? { status: { loggedIn: true }, user }
  : { status: { loggedIn: false }, user: null };

export const useAuthStore = defineStore('auth', {
  state: () => (initialState),
  actions: {
    async login(user: any) {
      try {
        const response = await AuthService.login(user);
        if (response.data.token) {
          localStorage.setItem('user', JSON.stringify(response.data));
          this.status.loggedIn = true;
          this.user = response.data;
        }
        return Promise.resolve(response.data);
      } catch (error) {
        this.status.loggedIn = false;
        this.user = null;
        return Promise.reject(error);
      }
    },
    logout() {
      localStorage.removeItem('user');
      this.status.loggedIn = false;
      this.user = null;
    },
    async register(user: any) {
      try {
        const response = await AuthService.register(user);
        return Promise.resolve(response.data);
      } catch (error) {
        return Promise.reject(error);
      }
    }
  }
});
