import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import FrontDeskDashboard from '../FrontDeskDashboard.vue'
import { createTestingPinia } from '@pinia/testing'
import { createI18n } from 'vue-i18n'

// Mock i18n
const i18n = createI18n({
  legacy: false,
  locale: 'en-US',
  messages: {
    'en-US': {
      frontdesk: {
        title: 'Front Desk',
        target: 'Today\'s Target',
        sales: 'Current Sales',
        checkins: 'Pending Check-ins',
        verifications: 'Pending Verifications',
        quickCheckin: 'Quick Check-in',
        newSale: 'New Sale',
        memberReg: 'Member Reg'
      }
    }
  }
})

describe('FrontDeskDashboard.vue', () => {
  it('renders front desk tasks correctly', async () => {
    const wrapper = mount(FrontDeskDashboard, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            initialState: {
              workbench: {
                loading: false,
                frontDeskTasks: {
                  todayTarget: 5000.00,
                  currentSales: 1250.00,
                  pendingCheckins: 3,
                  pendingVerifications: 2
                }
              }
            }
          }),
          i18n
        ],
        stubs: {
            'el-card': { template: '<div class="el-card"><slot name="header"></slot><slot /></div>' },
            'el-progress': true,
            'el-button': true,
            'el-dialog': true,
            'el-input': true
        },
        directives: {
            loading: () => {}
        }
      }
    })

    expect(wrapper.text()).toContain('Front Desk')
    expect(wrapper.text()).toContain('5000')
    expect(wrapper.text()).toContain('1250')
    expect(wrapper.text()).toContain('3') // Pending Checkins
  })
})
