import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import ManagerDashboard from '../ManagerDashboard.vue'
import { createTestingPinia } from '@pinia/testing'
import { createI18n } from 'vue-i18n'

// Mock i18n
const i18n = createI18n({
  legacy: false,
  locale: 'en-US',
  messages: {
    'en-US': {
      manager: {
        title: 'Store Manager',
        revenue: 'Revenue',
        visitors: 'Visitors',
        deviceRate: 'Device Online',
        koc: 'KOC Contrib',
        alerts: 'Alerts',
        approvals: 'Approvals',
        cost: 'Cost Breakdown'
      }
    }
  }
})

describe('ManagerDashboard.vue', () => {
  it('renders store overview data correctly', async () => {
    const wrapper = mount(ManagerDashboard, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            initialState: {
              workbench: {
                loading: false,
                managerOverview: {
                  todayRevenue: 12500.50,
                  todayVisitors: 320,
                  deviceOnlineRate: 98.5,
                  kocContribution: 4500.00,
                  alertCount: 2,
                  pendingApprovals: [],
                  costBreakdown: {}
                }
              }
            }
          }),
          i18n
        ],
        stubs: {
            'el-card': { template: '<div class="el-card"><slot name="header"></slot><slot /></div>' },
            'el-table': true,
            'el-table-column': true,
            'el-tag': true,
            'el-button': true
        },
        directives: {
            loading: () => {}
        }
      }
    })

    expect(wrapper.text()).toContain('Store Manager')
    expect(wrapper.text()).toContain('12500.5')
    expect(wrapper.text()).toContain('320')
    expect(wrapper.text()).toContain('98.5%')
  })
})
