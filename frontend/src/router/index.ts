import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import MainLayout from '../layout/MainLayout.vue';
import ManagerDashboard from '../views/Workbench/Manager/ManagerDashboard.vue';
import FrontDeskDashboard from '../views/Workbench/FrontDesk/FrontDeskDashboard.vue';
import TechnicianDashboard from '../views/Workbench/Technician/TechnicianDashboard.vue';
import SecurityDashboard from '../views/Workbench/Security/SecurityDashboard.vue';
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
      meta: { layout: MainLayout },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      // No layout meta needed, defaults to div in App.vue
    },
    // HQ Routes
    {
      path: '/hq',
      component: MainLayout,
      children: [
        {
          path: '',
          redirect: 'dashboard',
        },
        {
          path: 'dashboard',
          name: 'hq-dashboard',
          component: HQDashboard,
        },
        // Add more HQ sub-routes
      ],
    },
    // Data Routes
    {
      path: '/data',
      component: MainLayout,
      children: [
        {
          path: '',
          redirect: 'dashboard',
        },
        {
          path: 'dashboard',
          name: 'data-dashboard',
          component: DataDashboard,
        },
      ],
    },
    // Marketing Routes
    {
      path: '/marketing',
      component: MainLayout,
      children: [
        {
          path: '',
          redirect: 'dashboard',
        },
        {
          path: 'dashboard',
          name: 'marketing-dashboard',
          component: MarketingDashboard,
        },
      ],
    },
    // Finance Routes
    {
      path: '/finance',
      component: MainLayout,
      children: [
        {
          path: '',
          redirect: 'dashboard',
        },
        {
          path: 'dashboard',
          name: 'finance-dashboard',
          component: FinanceDashboard,
        },
      ],
    },
    // I18n Routes
    {
      path: '/i18n',
      component: MainLayout,
      children: [
        {
          path: '',
          redirect: 'dashboard',
        },
        {
          path: 'dashboard',
          name: 'i18n-dashboard',
          component: I18nDashboard,
        },
      ],
    },
    // Communication Routes
    {
      path: '/communication',
      component: MainLayout,
      children: [
        {
          path: '',
          redirect: 'dashboard',
        },
        {
          path: 'dashboard',
          name: 'communication-dashboard',
          component: CommunicationDashboard,
        },
      ],
    },
    // Workbench Routes
    {
      path: '/workbench',
      component: MainLayout,
      children: [
        {
          path: 'manager',
          name: 'manager-workbench',
          component: ManagerDashboard,
        },
        {
          path: 'frontdesk',
          name: 'frontdesk-workbench',
          component: FrontDeskDashboard,
        },
        {
          path: 'technician',
          name: 'technician-workbench',
          component: TechnicianDashboard,
        },
        {
          path: 'security',
          name: 'security-workbench',
          component: SecurityDashboard,
        },
      ],
    },
    // 404 Route
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/NotFound.vue'),
    },
  ],
});

router.beforeEach((to, _from, next) => {
  const publicPages = ['/login', '/register']; // Removed '/home'
  const authRequired = !publicPages.includes(to.path);
  const loggedIn = localStorage.getItem('user');

  if (authRequired && !loggedIn) {
    next('/login');
  } else {
    next();
  }
});

export default router;
