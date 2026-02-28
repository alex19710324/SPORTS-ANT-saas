import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import MembershipCard from '../MembershipCard.vue'
import { createTestingPinia } from '@pinia/testing'
import { createI18n } from 'vue-i18n'

// Create a real i18n instance for testing
const i18n = createI18n({
  legacy: false,
  locale: 'en-US',
  messages: {
    'en-US': {
      membership: {
        benefits: 'Benefits',
        noBenefits: 'No benefits yet',
        aiInsights: 'AI Insights',
        checkin: 'Daily Check-in',
        simulate: 'Simulate Purchase'
      }
    }
  }
})

describe('MembershipCard.vue', () => {
  it('renders loading state correctly', async () => {
    const wrapper = mount(MembershipCard, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            initialState: {
              membership: { loading: true, member: null }
            }
          }),
          i18n
        ],
        stubs: {
            'el-tag': true,
            'el-button': true,
            'el-empty': true,
            'el-progress': true
        },
        directives: {
            loading: (el, binding) => {
                if (binding.value) {
                    el.classList.add('el-loading-parent--relative')
                }
            }
        }
      }
    })

    expect(wrapper.find('.membership-card').classes()).toContain('el-loading-parent--relative')
  })

  it('renders member info when data is available', async () => {
    const wrapper = mount(MembershipCard, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            initialState: {
              membership: { 
                loading: false, 
                member: {
                  currentLevel: { levelOrder: 1, name: 'Bronze', benefitsJson: '{}' },
                  growthValue: 100,
                  tags: 'VIP,HighSpender'
                } 
              }
            }
          }),
          i18n
        ],
        stubs: {
            'el-tag': { template: '<div class="el-tag"><slot /></div>' },
            'el-button': true,
            'el-empty': true,
            'el-progress': true
        },
        directives: {
            loading: () => {}
        }
      }
    })

    expect(wrapper.text()).toContain('Bronze')
    expect(wrapper.text()).toContain('Current Growth: 100')
    expect(wrapper.text()).toContain('VIP')
  })
})
