import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import ForbiddenView from '../views/Forbidden.vue';
import MainLayout from '../layout/MainLayout.vue';
import ManagerDashboard from '../views/Workbench/Manager/ManagerDashboard.vue';
import FrontDeskDashboard from '../views/Workbench/FrontDesk/FrontDeskDashboard.vue';
import TechnicianDashboard from '../views/Workbench/Technician/TechnicianDashboard.vue';
import SecurityDashboard from '../views/Workbench/Security/SecurityDashboard.vue';
import HQDashboard from '../views/HQ/HQDashboard.vue';
import DataDashboard from '../views/Data/DataDashboard.vue';
import MarketingDashboard from '../views/Marketing/MarketingDashboard.vue';
import FinanceDashboard from '../views/Workbench/Finance/FinanceDashboard.vue';
import I18nDashboard from '../views/I18n/I18nDashboard.vue';
import CommunicationDashboard from '../views/Communication/CommunicationDashboard.vue';
import ReportDashboard from '../views/Report/ReportDashboard.vue';
import MemberPortal from '../views/MemberPortal/MemberPortal.vue';
import HRDashboard from '../views/HR/HRDashboard.vue';
import InventoryDashboard from '../views/Inventory/InventoryDashboard.vue';
import SystemDashboard from '../views/Monitor/SystemDashboard.vue';
import SaaSAdminDashboard from '../views/SaaSAdmin/SaaSAdminDashboard.vue';
import VenueTwin from '../views/IoT/VenueTwin.vue';
import SmartSchedule from '../views/HR/SmartSchedule.vue';
import BookingView from '../views/Booking/BookingView.vue';

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
      path: '/booking',
      name: 'booking-view',
      component: BookingView,
      meta: { layout: MainLayout },
    },
    {
      path: '/iot/venue',
      name: 'venue-twin',
      component: VenueTwin,
      meta: { layout: MainLayout, roles: ['ADMIN', 'STORE_MANAGER', 'TECHNICIAN'] },
    },
    {
      path: '/hr/schedule',
      name: 'smart-schedule',
      component: SmartSchedule,
      meta: { layout: MainLayout, roles: ['ADMIN', 'HR', 'STORE_MANAGER'] },
    },
    {
      path: '/saas/admin',
      name: 'saas-admin-dashboard',
      component: SaaSAdminDashboard,
      meta: { layout: MainLayout, roles: ['SUPER_ADMIN'] },
    },
    {
      path: '/inventory',
      name: 'inventory-dashboard',
      component: InventoryDashboard,
      meta: { layout: MainLayout, roles: ['ADMIN', 'STORE_MANAGER', 'TECHNICIAN'] },
    },
    {
      path: '/hr',
      name: 'hr-dashboard',
      component: HRDashboard,
      meta: { layout: MainLayout, roles: ['ADMIN', 'HR', 'STORE_MANAGER'] },
    },
    {
      path: '/report',
      name: 'report-dashboard',
      component: ReportDashboard,
      meta: { layout: MainLayout, roles: ['ADMIN', 'STORE_MANAGER', 'HQ'] },
    },
    {
      path: '/monitor',
      name: 'system-dashboard',
      component: SystemDashboard,
      meta: { layout: MainLayout, roles: ['ADMIN'] },
    },
    {
      path: '/member/portal',
      name: 'member-portal',
      component: MemberPortal,
      meta: { layout: MainLayout },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/403',
      name: 'forbidden',
      component: ForbiddenView,
    },
    // HQ Routes
    {
      path: '/hq',
      component: MainLayout,
      meta: { roles: ['ADMIN', 'HQ'] },
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
      ],
    },
    // Data Routes
    {
      path: '/data',
      component: MainLayout,
      meta: { roles: ['ADMIN', 'HQ', 'STORE_MANAGER'] },
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
      meta: { roles: ['ADMIN', 'MARKETING', 'STORE_MANAGER'] },
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
      meta: { roles: ['ADMIN', 'FINANCE', 'STORE_MANAGER'] },
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
          meta: { roles: ['ADMIN', 'STORE_MANAGER'] },
        },
        {
          path: 'frontdesk',
          name: 'frontdesk-workbench',
          component: FrontDeskDashboard,
          meta: { roles: ['ADMIN', 'FRONT_DESK', 'STORE_MANAGER'] },
        },
        {
          path: 'technician',
          name: 'technician-workbench',
          component: TechnicianDashboard,
          meta: { roles: ['ADMIN', 'TECHNICIAN', 'STORE_MANAGER'] },
        },
        {
          path: 'security',
          name: 'security-workbench',
          component: SecurityDashboard,
          meta: { roles: ['ADMIN', 'SECURITY', 'STORE_MANAGER'] },
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
  const publicPages = ['/login', '/register', '/403'];
  const authRequired = !publicPages.includes(to.path);
  const userStr = localStorage.getItem('user');
  
  if (authRequired && !userStr) {
    next('/login');
    return;
  }

  if (userStr) {
      const user = JSON.parse(userStr);
      const userRoles = user.roles || [];
      
      // Check RBAC
      if (to.meta.roles) {
          const requiredRoles = to.meta.roles as string[];
          const hasPermission = requiredRoles.some(role => 
              userRoles.includes('ROLE_' + role) || userRoles.includes(role)
          );
          
          if (!hasPermission) {
              next('/403');
              return;
          }
      }
  }

  next();
});

export default router;
