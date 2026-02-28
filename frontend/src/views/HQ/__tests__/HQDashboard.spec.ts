import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import HQDashboard from '../HQDashboard.vue'
import { createTestingPinia } from '@pinia/testing'
import { createI18n } from 'vue-i18n'

// Mock i18n
const i18n = createI18n({
  legacy: false,
  locale: 'en-US',
  messages: {
    'en-US': {
      hq: {
        title: 'HQ Dashboard',
        revenue: 'Total Revenue',
        visitors: 'Total Visitors',
        stores: 'Store Count',
        koc: 'Active KOC',
        map: 'Global Store Map',
        franchise: 'Franchise Applications',
        applicant: 'Applicant',
        city: 'City',
        contact: 'Contact',
        status: 'Status',
        approve: 'Approve',
        reject: 'Reject'
      }
    }
  }
})

describe('HQDashboard.vue', () => {
  it('renders overview data correctly', async () => {
    const wrapper = mount(HQDashboard, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            initialState: {
              hq: {
                loading: false,
                overview: {
                  totalRevenue: 50000,
                  totalVisitors: 1200,
                  storeCount: 5,
                  activeKoc: 300
                },
                storeMapData: [],
                franchiseApplications: []
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
            'el-button': true,
            'el-dialog': true,
            'el-form': true,
            'el-form-item': true,
            'el-input': true
        },
        directives: {
            loading: () => {}
        }
      }
    })

    expect(wrapper.text()).toContain('HQ Dashboard')
    expect(wrapper.text()).toContain('50000')
    expect(wrapper.text()).toContain('1200')
    expect(wrapper.text()).toContain('5')
  })
})
