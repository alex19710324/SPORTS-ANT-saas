import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import ManagerDashboard from '../views/Workbench/Manager/ManagerDashboard.vue';
import FrontDeskDashboard from '../views/Workbench/FrontDesk/FrontDeskDashboard.vue';
import TechnicianDashboard from '../views/Workbench/Technician/TechnicianDashboard.vue';
import HQDashboard from '../views/HQ/HQDashboard.vue';
import DataDashboard from '../views/Data/DataDashboard.vue';
import MarketingDashboard from '../views/Marketing/MarketingDashboard.vue';
import FinanceDashboard from '../views/Finance/FinanceDashboard.vue';
import I18nDashboard from '../views/I18n/I18nDashboard.vue';
import CommunicationDashboard from '../views/Communication/CommunicationDashboard.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    // HQ Routes
    {
      path: '/hq',
      name: 'hq-dashboard',
      component: HQDashboard,
    },
    // Data Routes
    {
      path: '/data',
      name: 'data-dashboard',
      component: DataDashboard,
    },
    // Marketing Routes
    {
      path: '/marketing',
      name: 'marketing-dashboard',
      component: MarketingDashboard,
    },
    // Finance Routes
    {
      path: '/finance',
      name: 'finance-dashboard',
      component: FinanceDashboard,
    },
    // I18n Routes
    {
      path: '/i18n',
      name: 'i18n-dashboard',
      component: I18nDashboard,
    },
    // Communication Routes
    {
      path: '/communication',
      name: 'communication-dashboard',
      component: CommunicationDashboard,
    },
    // Workbench Routes
    {
      path: '/workbench/manager',
      name: 'manager-workbench',
      component: ManagerDashboard,
    },
    {
      path: '/workbench/frontdesk',
      name: 'frontdesk-workbench',
      component: FrontDeskDashboard,
    },
    {
      path: '/workbench/technician',
      name: 'technician-workbench',
      component: TechnicianDashboard,
    }
  ],
});

router.beforeEach((to, _from, next) => {
  const publicPages = ['/login', '/register', '/home'];
  const authRequired = !publicPages.includes(to.path);
  const loggedIn = localStorage.getItem('user');

  // trying to access a restricted page + not logged in
  // redirect to login page
  if (authRequired && !loggedIn) {
    next('/login');
  } else {
    next();
  }
});

export default router;
