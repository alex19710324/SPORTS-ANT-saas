const http = require('http')
const path = require('path')
const fs = require('fs')

const publicImagesDir = path.join(__dirname, 'public', 'images')
if (!fs.existsSync(publicImagesDir)) {
  fs.mkdirSync(publicImagesDir, { recursive: true })
}

const db = {
  game_library: [
    { id: 'g1', title: 'Cyber Boxing', category: 'VR Sports', image: 'http://localhost:8080/images/asset_52ffd21165f088875945738c4f5fcee2.jpg', players: '1-2' },
    { id: 'g2', title: 'Neon Archery', category: 'Precision', image: 'http://localhost:8080/images/asset_c201d0d9e7378cacbd3e2b61541e6596.jpg', players: '1-4' },
    { id: 'g3', title: 'Zero Gravity Racing', category: 'Simulator', image: 'http://localhost:8080/images/asset_9a4060e9e74e1c83c1a8f4d073ee29c2.jpg', players: '1-8' }
  ],
  community_events: [
    { id: 'EV-2001', title: 'Weekend VR Tournament', status: 'OPEN', date: '2026-04-12', attendees: 48, storeName: '新天地店' },
    { id: 'EV-2002', title: 'Cosplay Night', status: 'UPCOMING', date: '2026-04-20', attendees: 120, storeName: '五角场店' }
  ],
  leaderboard: [
    { rank: 1, name: 'CyberNinja', score: 9500, avatar: 'http://localhost:8080/images/asset_2c774ff276618b470a3caf076631252f.svg' },
    { rank: 2, name: 'NeonRider', score: 8800, avatar: 'http://localhost:8080/images/asset_32f6cf35ab9d16a6a915c4e33c4ad954.svg' },
    { rank: 3, name: 'PixelQueen', score: 8200, avatar: 'http://localhost:8080/images/asset_2db09cad61a85973b4888102ee20178b.svg' },
    { rank: 4, name: 'GlitchMaster', score: 7500, avatar: 'http://localhost:8080/images/asset_4e094a87db60c2489269c1b1e1684a49.svg' },
    { rank: 5, name: 'RetroKing', score: 7100, avatar: 'http://localhost:8080/images/asset_2dc80deef38aceaa7427dc8528444c04.svg' }
  ],
  userProfile: {
    id: 1,
    name: 'Alex S.',
    pts: 1250,
    nfts: 3,
    level: 'V4',
    balance: 500,
    avatar: 'http://localhost:8080/images/asset_2c774ff276618b470a3caf076631252f.svg',
    phone: '138****2048'
  }
}

db.notifications = [
  { id: 'n1', title: '会员权益更新', content: '本周签到积分已到账。', time: '刚刚', unread: true },
  { id: 'n2', title: '活动提醒', content: 'Weekend VR Tournament 开放报名。', time: '10分钟前', unread: true }
]
db.desks = [
  { id: 'desk-01', name: '01号工位', status: 'FREE', type: 'STANDARD', zone: 'A区', location: 'A区靠窗', seatLabel: 'A-01' },
  { id: 'desk-02', name: '02号工位', status: 'FREE', type: 'STANDARD', zone: 'A区', location: 'A区中岛', seatLabel: 'A-02' },
  { id: 'desk-vip1', name: 'VIP包厢', status: 'FREE', type: 'VIP', zone: 'VIP区', location: 'VIP独立包厢', seatLabel: 'VIP-01' }
]
db.meetings = [
  { id: 1, name: 'V1 赛博洽谈室', capacity: 6, location: '2F 东侧', status: 'AVAILABLE', nextSlot: '今天 19:30', equipment: ['投影仪', '白板'], bookings: [{ left: '10%', width: '20%' }, { left: '50%', width: '15%' }] },
  { id: 2, name: 'V2 全息培训室', capacity: 20, location: '3F 培训区', status: 'OCCUPIED', nextSlot: '明天 10:00', equipment: ['8K大屏', '音响'], bookings: [{ left: '60%', width: '30%' }] }
]
db.mallCategories = [
  { id: 'blindbox', name: '潮玩盲盒' },
  { id: 'figure', name: '收藏周边' }
]
db.mallProducts = [
  { id: 'MB-1001', name: '赛博朋克限定盲盒·第一弹', description: '包含6个常规款+1个隐藏款。', cashPrice: 59, pointsPrice: 590, originalPrice: 99, image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg', stock: 99, saleStatus: 'ON_SHELF', statusText: '在售', defaultSkuId: 'sku-default', skuList: [{ id: 'sku-default', name: '默认规格' }], categoryId: 'blindbox' },
  { id: 'MB-1002', name: '霓虹光剑·复刻版', description: '收藏级霓虹光剑模型。', cashPrice: 199, pointsPrice: 1990, originalPrice: 299, image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg', stock: 48, saleStatus: 'ON_SHELF', statusText: '在售', defaultSkuId: 'sku-blue', skuList: [{ id: 'sku-blue', name: '冰川蓝' }], categoryId: 'figure' },
  { id: 'MB-1003', name: '赛博能量徽章·限量版', description: '当前批次已售罄。', cashPrice: 39, pointsPrice: 390, originalPrice: 59, image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg', stock: 0, saleStatus: 'ON_SHELF', statusText: '已售罄', defaultSkuId: 'sku-badge', skuList: [{ id: 'sku-badge', name: '限定徽章' }], categoryId: 'figure' },
  { id: 'MB-1004', name: '夜行者机能外套', description: '季节性周边，当前已下架。', cashPrice: 329, pointsPrice: 3290, originalPrice: 429, image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg', stock: 8, saleStatus: 'OFF_SHELF', statusText: '已下架', defaultSkuId: 'sku-black', skuList: [{ id: 'sku-black', name: '曜石黑' }], categoryId: 'figure' }
]
db.cartItems = []
db.orders = []
db.bookings = []
const io = { emit() {} }

const respond = (res, data, message = 'success', statusCode = 200) => {
  res.writeHead(statusCode, {
    'Content-Type': 'application/json; charset=utf-8',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET,POST,OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization'
  })
  res.end(JSON.stringify({ code: 200, data, message }))
}

const readBody = (req) =>
  new Promise((resolve) => {
    let raw = ''
    req.on('data', (chunk) => {
      raw += chunk.toString()
    })
    req.on('end', () => {
      if (!raw) return resolve({})
      try {
        resolve(JSON.parse(raw))
      } catch {
        resolve({})
      }
    })
  })

const buildMallCartItems = () => db.cartItems.map((item) => {
  const product = db.mallProducts.find((entry) => entry.id === item.productId)
  return {
    cartItemId: item.cartItemId,
    productId: item.productId,
    skuId: item.skuId,
    title: product?.name || item.title,
    sku: item.sku || product?.skuList?.[0]?.name || '默认规格',
    price: Number(product?.cashPrice || item.price || 0),
    quantity: Number(item.quantity || 1),
    checked: Boolean(item.checked),
    image: product?.image || item.image,
    stock: Number(product?.stock || 0),
    sourceType: 'MALL',
    sourcePage: 'pages/mall/detail/index',
    userId: 'Player_Mobile_01'
  }
})

const server = http.createServer(async (req, res) => {
  const url = new URL(req.url, 'http://127.0.0.1:8080')
  const { pathname, searchParams } = url

  if (req.method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET,POST,OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization'
    })
    return res.end()
  }

  if (pathname === '/') {
    res.writeHead(200, { 'Content-Type': 'text/plain; charset=utf-8', 'Access-Control-Allow-Origin': '*' })
    return res.end('Mock Server is running')
  }

  if (pathname.startsWith('/socket.io/')) {
    const sid = searchParams.get('sid') || 'mock-sid'
    if (req.method === 'GET' && searchParams.get('transport') === 'polling' && !searchParams.get('sid')) {
      res.writeHead(200, {
        'Content-Type': 'text/plain; charset=utf-8',
        'Access-Control-Allow-Origin': '*'
      })
      return res.end(`0{"sid":"${sid}","upgrades":[],"pingInterval":25000,"pingTimeout":20000,"maxPayload":1000000}`)
    }
    if (req.method === 'GET' && searchParams.get('transport') === 'polling') {
      res.writeHead(200, {
        'Content-Type': 'text/plain; charset=utf-8',
        'Access-Control-Allow-Origin': '*'
      })
      return res.end('40')
    }
    res.writeHead(200, {
      'Content-Type': 'text/plain; charset=utf-8',
      'Access-Control-Allow-Origin': '*'
    })
    return res.end('ok')
  }

  if (pathname.startsWith('/images/')) {
    const filePath = path.join(publicImagesDir, pathname.replace('/images/', ''))
    if (fs.existsSync(filePath)) {
      res.writeHead(200, { 'Access-Control-Allow-Origin': '*' })
      return fs.createReadStream(filePath).pipe(res)
    }
    res.writeHead(404, { 'Access-Control-Allow-Origin': '*' })
    return res.end('Not Found')
  }

  if (pathname === '/api/community/events' && req.method === 'GET') return respond(res, db.community_events)
  if (pathname === '/api/community/leaderboard' && req.method === 'GET') return respond(res, db.leaderboard)
  if (pathname === '/api/membership/me' && req.method === 'GET') {
    return respond(res, {
      id: db.userProfile.id,
      name: db.userProfile.name,
      points: db.userProfile.pts,
      balance: db.userProfile.balance,
      level: db.userProfile.level,
      currentLevel: { name: db.userProfile.level },
      avatar: db.userProfile.avatar,
      phone: db.userProfile.phone
    })
  }
  if (pathname === '/api/mobile/profile' && req.method === 'GET') return respond(res, db.userProfile)
  if (pathname === '/api/mobile/notifications/list' && req.method === 'GET') return respond(res, db.notifications)
  if (pathname === '/api/mobile/home' && req.method === 'GET') {
    return respond(res, {
      banners: [
        { id: 1, imgUrl: 'https://via.placeholder.com/600x300/ff0000/ffffff?text=Banner1', image: 'https://via.placeholder.com/600x300/ff0000/ffffff?text=Banner1', title: '全息元宇宙电竞', subtitle: '限时 8 折体验' },
        { id: 2, imgUrl: 'https://via.placeholder.com/600x300/00ff00/ffffff?text=Banner2', image: 'https://via.placeholder.com/600x300/00ff00/ffffff?text=Banner2', title: '周末极客挑战赛', subtitle: '奖池 50,000 Pts' }
      ],
      hotGames: db.game_library,
      announcements: [
        { id: 1, title: 'New VR Game Released!', content: '新上架项目已开放预约，顾客可直接从 5171 首页进入报名。' },
        { id: 2, title: 'Summer Sale starts now!', content: '本周活动、门票和商城福利均已联动更新。' }
      ],
      nearbyVenues: [
        { id: 'v1', name: 'Cyber Arena HQ', distance: '1.2km', tags: ['VR', 'Esports'], image: 'https://via.placeholder.com/480x300/111827/ffffff?text=Cyber+Arena' },
        { id: 'v2', name: 'Neon Fitness Hub', distance: '3.5km', tags: ['Fitness', '24H'], image: 'https://via.placeholder.com/480x300/1f2937/ffffff?text=Neon+Fitness' }
      ]
    })
  }
  if (pathname === '/api/mobile/iot/vip-env' && req.method === 'POST') return respond(res, { status: 'SYNCED' })
  if (pathname === '/api/mobile/deposit/refund' && req.method === 'POST') {
    const body = await readBody(req)
    const amount = Number(body.amount || 199)
    db.userProfile.balance += amount
    io.emit('data_sync', { type: 'REFUND_PROCESSED', payload: { amount } })
    return respond(res, { status: 'REFUNDED', balance: db.userProfile.balance, amount })
  }
  if (pathname === '/api/mobile/desk/list' && req.method === 'GET') return respond(res, db.desks)
  if (pathname === '/api/mobile/desk/book' && req.method === 'POST') {
    const body = await readBody(req)
    const desk = db.desks.find((item) => item.id === body.deskId)
    if (desk) desk.status = 'MINE'
    io.emit('data_sync', { type: 'DESK_STATUS_CHANGED', payload: db.desks })
    return respond(res, { status: 'BOOKED', desk })
  }
  if (pathname === '/api/mobile/meeting/list' && req.method === 'GET') return respond(res, db.meetings)
  if (pathname === '/api/mobile/meeting/book' && req.method === 'POST') {
    const body = await readBody(req)
    const room = db.meetings.find((item) => String(item.id) === String(body.roomId))
    if (room) {
      room.status = 'OCCUPIED'
      room.nextSlot = '请前往会议室中心查看'
      room.bookings.push({ left: '70%', width: '10%' })
    }
    io.emit('data_sync', { type: 'MEETING_ROOMS_UPDATE', payload: db.meetings })
    return respond(res, { status: 'BOOKED', room })
  }
  if (pathname === '/api/booking/resources' && req.method === 'GET') {
    return respond(res, db.game_library.map((item, index) => ({
      id: item.id,
      name: item.title,
      image: item.image,
      price: [168, 128, 88][index % 3],
      status: index % 3 === 2 ? 'BUSY' : 'AVAILABLE',
      hot: index % 2 === 0,
      tags: [item.category, `${item.players}人`]
    })))
  }
  if (pathname === '/api/play/detail' && req.method === 'GET') {
    const id = searchParams.get('id') || 'g1'
    const item = db.game_library.find((game) => game.id === id) || db.game_library[0]
    return respond(res, {
      id: item.id,
      name: item.title,
      price: 88,
      tags: [item.category, `${item.players}人`],
      description: `佩戴最新的VR头显，进入 ${item.title} 的沉浸式场景，支持多人联机与现场 IoT 联动授权入场。`,
      image: item.image
    })
  }
  if (pathname === '/api/booking/create' && req.method === 'POST') {
    const body = await readBody(req)
    const booking = { id: `BK-${Date.now()}`, resourceId: body.resourceId, time: body.time, people: Number(body.people || 1), teamCode: body.teamCode || '', status: 'CONFIRMED', createdAt: new Date().toISOString() }
    db.bookings.push(booking)
    io.emit('data_sync', { type: 'BOOKING_CREATED', payload: booking })
    return respond(res, booking)
  }
  if (pathname === '/api/mall/categories' && req.method === 'GET') return respond(res, db.mallCategories)
  if (pathname === '/api/mall/products' && req.method === 'GET') return respond(res, db.mallProducts)
  if (pathname.startsWith('/api/mall/product/') && req.method === 'GET') {
    const productId = pathname.split('/').pop()
    const product = db.mallProducts.find((item) => item.id === productId)
    return respond(res, product || db.mallProducts[0])
  }
  if (pathname === '/api/mall/cart/list' && req.method === 'GET') return respond(res, buildMallCartItems())
  if (pathname === '/api/mall/cart/add' && req.method === 'POST') {
    const body = await readBody(req)
    const product = db.mallProducts.find((item) => item.id === body.productId)
    const existing = db.cartItems.find((item) => item.productId === body.productId && item.skuId === body.skuId)
    if (existing) {
      existing.quantity += Number(body.quantity || 1)
    } else {
      db.cartItems.push({
        cartItemId: `CART-${Date.now()}`,
        productId: body.productId,
        skuId: body.skuId || product?.defaultSkuId || 'sku-default',
        quantity: Number(body.quantity || 1),
        checked: true,
        price: Number(product?.cashPrice || 0),
        title: product?.name,
        image: product?.image
      })
    }
    return respond(res, { status: 'ADDED', items: buildMallCartItems() })
  }
  if (pathname === '/api/mall/cart/update' && req.method === 'POST') {
    const body = await readBody(req)
    const item = db.cartItems.find((entry) => entry.cartItemId === body.cartItemId)
    if (item) {
      if (typeof body.checked === 'boolean') item.checked = body.checked
      if (body.quantity !== undefined) item.quantity = Number(body.quantity)
    }
    return respond(res, { status: 'UPDATED', items: buildMallCartItems() })
  }
  if (pathname === '/api/mall/cart/check-all' && req.method === 'POST') {
    const body = await readBody(req)
    db.cartItems = db.cartItems.map((item) => ({ ...item, checked: Boolean(body.checked) }))
    return respond(res, { status: 'UPDATED', items: buildMallCartItems() })
  }
  if (pathname === '/api/mall/cart/remove' && req.method === 'POST') {
    const body = await readBody(req)
    const ids = Array.isArray(body.cartItemIds) ? body.cartItemIds : []
    db.cartItems = db.cartItems.filter((item) => !ids.includes(item.cartItemId))
    return respond(res, { status: 'REMOVED', items: buildMallCartItems() })
  }
  if (pathname === '/api/payment/create' && req.method === 'POST') {
    const body = await readBody(req)
    const checkedItems = buildMallCartItems().filter((item) => item.checked)
    const orderId = `OD-${Date.now()}`
    db.orders.unshift({
      id: orderId,
      title: checkedItems.map((item) => item.title).join('、') || '商城订单',
      amount: checkedItems.reduce((sum, item) => sum + item.price * item.quantity, 0),
      status: 'COMPLETED',
      type: 'MALL',
      items: checkedItems
    })
    db.cartItems = db.cartItems.filter((item) => !checkedItems.some((entry) => entry.cartItemId === item.cartItemId))
    return respond(res, { paymentId: `PM-${Date.now()}`, orderId, amount: body.amount || 0, status: 'SUCCESS' })
  }

  res.writeHead(404, {
    'Content-Type': 'application/json; charset=utf-8',
    'Access-Control-Allow-Origin': '*'
  })
  res.end(JSON.stringify({ code: 404, message: 'Not Found', path: pathname }))
})

server.listen(8080, () => {
  console.log('Unified Mock Backend running on port 8080')
  console.log('WebSocket Server ready')
})
