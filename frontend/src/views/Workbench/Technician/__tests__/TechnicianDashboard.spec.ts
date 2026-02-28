import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import TechnicianDashboard from '../TechnicianDashboard.vue'
import { createTestingPinia } from '@pinia/testing'
import { createI18n } from 'vue-i18n'

// Mock i18n
const i18n = createI18n({
  legacy: false,
  locale: 'en-US',
  messages: {
    'en-US': {
      technician: {
        title: 'Technician Workbench',
        workOrders: 'Work Orders',
        faultyDevices: 'Faulty Devices',
        offlineDevices: 'Offline Devices',
        inspection: 'Inspection',
        scanQr: 'Scan Device QR',
        reportFault: 'Report Fault',
        preventive: 'Preventive Maint',
        maintenance: 'Today\'s Plan'
      }
    }
  }
})

// Mock DeviceMonitor component since it might have complex dependencies
const DeviceMonitorStub = { template: '<div class="device-monitor-stub"></div>' }

describe('TechnicianDashboard.vue', () => {
  it('renders technician tasks correctly', async () => {
    const wrapper = mount(TechnicianDashboard, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            initialState: {
              workbench: {
                loading: false,
                technicianTasks: {
                  pendingOrders: [{ id: 1, deviceId: 'DEV-001', description: 'Screen broken', priority: 'HIGH' }],
                  offlineDevices: [{ id: 2, name: 'Treadmill A', location: 'Zone 1' }],
                  todayInspectionsCompleted: 12,
                  todayInspectionsTotal: 15
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
            'el-button': true,
            'DeviceMonitor': DeviceMonitorStub
        },
        directives: {
            loading: () => {}
        }
      }
    })

    expect(wrapper.text()).toContain('Technician Workbench')
    expect(wrapper.text()).toContain('1') // Pending Orders Count
    expect(wrapper.text()).toContain('80%') // Inspection Progress (12/15)
    expect(wrapper.text()).toContain('Treadmill A') // Offline Device Name
  })
})
