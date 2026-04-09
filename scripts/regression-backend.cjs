const http = require('node:http')
const { URL } = require('node:url')

const port = 8080
const rentalCatalog = [
  { id: 'VR-001', name: 'PICO 4 VR头显', sn: 'SN-VR-001', price: 30, stock: 50, maintenance: 3 },
  { id: 'LS-001', name: '光剑体感手柄套装', sn: 'SN-LS-001', price: 15, stock: 80, maintenance: 2 },
  { id: 'COMBO-001', name: '全套装备组合包', sn: 'SN-CB-001', price: 50, stock: 20, maintenance: 1 }
]

const state = {
  rentals: [],
  incidents: [],
  reports: [],
  hazards: [],
  workflowTickets: [],
  workflowLogs: {},
  healthSummary: {
    userId: 'Player_Mobile_01',
    calories: 650,
    duration: 45,
    score: 88
  },
  nfts: [
    { id: 'NFT-001', name: '初代拓荒者徽章', rarity: 'Legendary', image: 'http://localhost:8080/images/nft-1.svg' },
    { id: 'NFT-045', name: '黄金弓箭手', rarity: 'Rare', image: 'http://localhost:8080/images/nft-2.svg' }
  ]
}

const app = http.createServer(async (req, res) => {
  try {
    const url = new URL(req.url, `http://${req.headers.host || '127.0.0.1'}`)
    if (req.method === 'OPTIONS') {
      writeJson(res, 204, null)
      return
    }

    if (req.method === 'GET' && url.pathname === '/') {
      writeText(res, 200, 'Regression backend is running')
      return
    }

    if (req.method === 'GET' && url.pathname.startsWith('/images/')) {
      writeSvg(res, `<svg xmlns="http://www.w3.org/2000/svg" width="320" height="320"><rect width="100%" height="100%" fill="#111827"/><text x="50%" y="50%" fill="#38bdf8" font-size="28" text-anchor="middle" dominant-baseline="middle">SPORTS ANT</text></svg>`)
      return
    }

    if (req.method === 'POST' && url.pathname === '/api/mobile/rental/lease') {
      const body = await readJson(req)
      const device = rentalCatalog.find((item) => item.id === body.deviceId) || rentalCatalog[0]
      const orderId = `RO-${Date.now()}`
      const pickupCode = `RN-${String(Date.now()).slice(-6)}`
      const rental = {
        id: orderId,
        orderId,
        userId: body.userId,
        user: body.userId,
        deviceId: device.id,
        deviceName: device.name,
        device: device.name,
        deviceSn: device.sn,
        sn: device.sn,
        durationHours: Number(body.duration || 2),
        duration: `${Number(body.duration || 2)}小时`,
        startAt: new Date().toISOString(),
        createdAt: new Date().toISOString(),
        expiresAt: new Date(Date.now() + Number(body.duration || 2) * 3600000).toISOString(),
        status: 'PENDING_PICKUP',
        pickupCode,
        storeName: body.storeName || '新天地店',
        isOvertime: false
      }
      state.rentals.unshift(rental)
      emit('RENTAL_LEASED', { userId: rental.userId, orderId, pickupCode, deviceId: rental.deviceId })
      writeJson(res, 200, { data: { orderId, rental, pickupCode } })
      return
    }

    if (req.method === 'GET' && url.pathname === '/api/mobile/rental/list') {
      const userId = url.searchParams.get('userId') || ''
      const rentals = state.rentals.filter((item) => !userId || item.userId === userId)
      writeJson(res, 200, { data: { rentals } })
      return
    }

    if (req.method === 'POST' && url.pathname === '/api/mobile/rental/pickup') {
      const body = await readJson(req)
      const rental = state.rentals.find((item) => item.id === body.orderId)
      if (rental) {
        rental.status = 'ACTIVE'
        emit('RENTAL_PICKED_UP', { userId: rental.userId, orderId: rental.id, pickupCode: rental.pickupCode })
      }
      writeJson(res, 200, { data: { success: true } })
      return
    }

    if (req.method === 'POST' && url.pathname === '/api/mobile/rental/return') {
      const body = await readJson(req)
      const rental = state.rentals.find((item) => item.id === body.orderId)
      if (rental) {
        rental.status = 'RETURNED'
        emit('RENTAL_RETURNED', { userId: rental.userId, orderId: rental.id, pickupCode: rental.pickupCode })
      }
      writeJson(res, 200, { data: { success: true } })
      return
    }

    if (req.method === 'GET' && url.pathname === '/api/rental/orders') {
      const inventory = rentalCatalog.map((device) => {
        const related = state.rentals.filter((item) => item.deviceId === device.id && item.status !== 'RETURNED')
        return {
          id: device.id,
          name: device.name,
          total: device.stock,
          available: Math.max(device.stock - related.length - device.maintenance, 0),
          rented: related.length,
          maintenance: device.maintenance
        }
      })
      writeJson(res, 200, { data: { inventory, orders: state.rentals } })
      return
    }

    if (req.method === 'POST' && url.pathname === '/api/safety/incident/report') {
      const body = await readJson(req)
      const incidentId = `INC-${Date.now()}`
      const workflowTaskId = `WF-${Date.now()}`
      const incident = {
        id: incidentId,
        incidentId,
        workflowTaskId,
        storeId: body.storeId || '新天地店',
        type: body.type || 'MEDICAL',
        level: body.type === 'FIRE' ? 'CRITICAL' : 'HIGH',
        location: body.location || 'VR 赛博战场',
        description: body.description || '四端回归测试-医疗支援',
        reporter: body.reporterId || 'EMP-5171',
        reportedAt: new Date().toISOString(),
        status: 'OPEN'
      }
      state.incidents.unshift(incident)
      emit('EMERGENCY_TRIGGERED', {
        storeId: incident.storeId,
        type: incident.type,
        reporter: incident.reporter,
        timestamp: incident.reportedAt,
        description: incident.description,
        incidentId: incident.id
      })
      emit('EMERGENCY_INCIDENT', incident)
      writeJson(res, 200, { data: { incidentId, workflowTaskId } })
      return
    }

    if (req.method === 'GET' && url.pathname === '/api/safety/incidents') {
      const hazards = state.incidents.map((item) => ({
        id: item.id,
        title: item.description,
        location: `${item.storeId} · ${item.location}`,
        reporter: item.reporter,
        severity: item.level === 'CRITICAL' ? 'High' : 'Medium',
        status: item.status === 'RESOLVED' ? 'Resolved' : 'Pending Fix',
        time: item.reportedAt
      }))
      writeJson(res, 200, { data: { incidents: state.incidents, hazards } })
      return
    }

    if (req.method === 'POST' && url.pathname === '/api/store/inspection/submit') {
      const body = await readJson(req)
      const reportId = `IR-${Date.now()}`
      const report = {
        id: reportId,
        reportId,
        storeId: body.storeId || '深圳南山旗舰店',
        template: body.template || '消防安全专项检查',
        inspector: body.inspector || 'EMP-5171-QC',
        score: 15,
        createdAt: new Date().toISOString(),
        status: 'PENDING_FIX'
      }
      const hazard = {
        id: `HZ-${Date.now()}`,
        reportId,
        title: 'INSPECTION_REMEDIATION',
        location: report.storeId,
        status: 'Pending Fix'
      }
      const ticket = {
        id: `TK-${Date.now()}`,
        title: `INSPECTION_REMEDIATION ${reportId}`,
        type: 'INSPECTION_REMEDIATION',
        priority: 'HIGH',
        status: 'NEW',
        assigneeId: 'QC-LEAD',
        createdAt: new Date().toISOString(),
        description: `Inspection remediation created for ${reportId}`
      }
      state.reports.unshift(report)
      state.hazards.unshift(hazard)
      state.workflowTickets.unshift(ticket)
      state.workflowLogs[ticket.id] = [
        { createdAt: new Date().toISOString(), action: 'CREATE', details: `Linked report ${reportId}`, operatorId: report.inspector }
      ]
      emit('INSPECTION_SUBMITTED', { reportId, storeId: report.storeId })
      writeJson(res, 200, { data: { reportId } })
      return
    }

    if (req.method === 'GET' && url.pathname === '/api/store/inspection/reports') {
      writeJson(res, 200, { data: { reports: state.reports, hazards: state.hazards } })
      return
    }

    if (req.method === 'GET' && url.pathname === '/api/workflow/tickets') {
      writeJson(res, 200, state.workflowTickets)
      return
    }

    if (req.method === 'POST' && url.pathname === '/api/workflow/tickets') {
      const body = await readJson(req)
      const ticket = {
        id: `TK-${Date.now()}`,
        title: body.title || 'New Ticket',
        type: body.type || 'REPAIR',
        priority: body.priority || 'MEDIUM',
        status: 'NEW',
        assigneeId: 'AUTO',
        createdAt: new Date().toISOString(),
        description: body.description || ''
      }
      state.workflowTickets.unshift(ticket)
      state.workflowLogs[ticket.id] = []
      writeJson(res, 200, ticket)
      return
    }

    if (req.method === 'GET' && /^\/api\/workflow\/tickets\/[^/]+\/logs$/.test(url.pathname)) {
      const id = url.pathname.split('/')[4]
      writeJson(res, 200, state.workflowLogs[id] || [])
      return
    }

    if (req.method === 'PUT' && /^\/api\/workflow\/tickets\/[^/]+\/status$/.test(url.pathname)) {
      const id = url.pathname.split('/')[4]
      const status = url.searchParams.get('status') || 'RESOLVED'
      const ticket = state.workflowTickets.find((item) => item.id === id)
      if (ticket) ticket.status = status
      writeJson(res, 200, { success: true })
      return
    }

    if (req.method === 'GET' && url.pathname === '/api/mobile/health/summary') {
      writeJson(res, 200, { data: { summary: state.healthSummary } })
      return
    }

    if (req.method === 'POST' && url.pathname === '/api/mobile/health/milestone') {
      const body = await readJson(req)
      state.healthSummary = {
        ...state.healthSummary,
        userId: body.userId || state.healthSummary.userId,
        calories: Number(body.calories || state.healthSummary.calories),
        score: 92
      }
      emit('HEALTH_MILESTONE', state.healthSummary)
      emit('CALORIE_MILESTONE_REACHED', state.healthSummary)
      writeJson(res, 200, { data: { summary: state.healthSummary } })
      return
    }

    if (req.method === 'POST' && url.pathname === '/api/mobile/wallet/swap') {
      const body = await readJson(req)
      const txHash = `0x${Date.now().toString(16)}`
      emit('DID_WALLET_AIRDROP', { userId: body.userId || 'Player_Mobile_01', asset: body.token || 'ANT', amount: body.amount || 100, txHash })
      emit('WEB3_NFT_VERIFIED', { userId: body.userId || 'Player_Mobile_01', txHash })
      writeJson(res, 200, { data: { txHash } })
      return
    }

    if (req.method === 'GET' && url.pathname === '/api/trendy/nfts') {
      writeJson(res, 200, state.nfts)
      return
    }

    if (req.method === 'POST' && url.pathname === '/api/trendy/nfts/list') {
      writeJson(res, 200, { success: true })
      return
    }

    writeJson(res, 404, { message: 'Not Found' })
  } catch (error) {
    writeJson(res, 500, { message: error.message })
  }
})

const emit = () => {}

const readJson = (req) =>
  new Promise((resolve, reject) => {
    let body = ''
    req.on('data', (chunk) => {
      body += chunk.toString()
    })
    req.on('end', () => {
      if (!body) {
        resolve({})
        return
      }
      try {
        resolve(JSON.parse(body))
      } catch (error) {
        reject(error)
      }
    })
    req.on('error', reject)
  })

const setCors = (res) => {
  res.setHeader('Access-Control-Allow-Origin', '*')
  res.setHeader('Access-Control-Allow-Methods', 'GET,POST,PUT,OPTIONS')
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization')
  res.setHeader('Cache-Control', 'no-cache')
}

const writeJson = (res, statusCode, payload) => {
  setCors(res)
  if (statusCode === 204) {
    res.writeHead(204)
    res.end()
    return
  }
  res.writeHead(statusCode, { 'Content-Type': 'application/json; charset=utf-8' })
  res.end(JSON.stringify(payload))
}

const writeText = (res, statusCode, text) => {
  setCors(res)
  res.writeHead(statusCode, { 'Content-Type': 'text/plain; charset=utf-8' })
  res.end(text)
}

const writeSvg = (res, svg) => {
  setCors(res)
  res.writeHead(200, { 'Content-Type': 'image/svg+xml' })
  res.end(svg)
}

app.listen(port, '127.0.0.1', () => {
  process.stdout.write(`Regression backend ready on http://127.0.0.1:${port}\n`)
})
