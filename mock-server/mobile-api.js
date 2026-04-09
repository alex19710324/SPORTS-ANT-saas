module.exports = function(app, io, db) {
  const express = require('express');
  const router = express.Router();
  
  // Initialize some state if missing
  if (!db.orders) db.orders = [
    { id: 'OD-5176-1001', title: 'Weekend VR Tournament 门票', date: '2026-04-05 19:30', amount: 168, status: 'COMPLETED', type: 'EVENT', sourceApp: '5176 玩家站', paymentChannel: '微信支付', contactName: 'Alex S.', ticketCount: 2 },
    { id: 'OD-5176-1002', title: '霓虹追凶双人场', date: '2026-04-06 14:00', amount: 256, status: 'REFUNDING', type: 'BOOKING', sourceApp: '5176 玩家站', paymentChannel: '支付宝', contactName: 'Alex S.', ticketCount: 2, refundRequestedAt: '2026-04-06 16:10:00', refundReason: '行程变更，无法到店' },
    { id: 'OD-5171-2001', title: '赛博补给组合包', date: '2026-04-06 11:20', amount: 258, status: 'COMPLETED', type: 'MALL', sourceApp: '5171 移动端', paymentChannel: '微信支付', contactName: 'Alex S.', ticketCount: 2, items: [ { id: 'MB-1001', title: '赛博朋克限定盲盒·第一弹', sku: '默认规格', quantity: 1, price: 59 }, { id: 'MB-1002', title: '霓虹光剑·复刻版', sku: '冰川蓝', quantity: 1, price: 199 } ] },
    { id: 'OD-5176-1003', title: '会员补给盲盒', date: '2026-04-04 12:10', amount: 59, status: 'REFUNDED', type: 'MALL', sourceApp: '5171 移动端', paymentChannel: '数字钱包', contactName: 'Alex S.', ticketCount: 1, refundRequestedAt: '2026-04-04 13:20:00', refundedAt: '2026-04-04 15:45:00', refundReason: '重复购买', items: [ { id: 'MB-1001', title: '赛博朋克限定盲盒·第一弹', sku: '默认规格', quantity: 1, price: 59 }, { id: 'MB-1002', title: '霓虹光剑·复刻版', sku: '冰川蓝', quantity: 1, price: 199 } ], logistics: { company: '顺丰速运', trackingNo: 'SF5171003001', status: '部分签收', packageCount: 2, receiverName: 'Alex S.', receiverPhone: '138****2048', address: '上海市浦东新区张江科创路 88 号 2 栋 1203', parcels: [ { parcelNo: 'PKG-1003-A', company: '顺丰速运', trackingNo: 'SF5171003001', status: '已签收', items: [ { id: 'MB-1001', title: '赛博朋克限定盲盒·第一弹', sku: '默认规格', quantity: 1, price: 59 } ], tracks: [ { id: 'lg-1', title: '商家已发货', desc: '盲盒包裹已由 5174 仓配中心交接顺丰。', time: '2026-04-04 12:45:00', tag: '已发货' }, { id: 'lg-2', title: '运输中', desc: '盲盒包裹已到达上海浦东中转场，正在派送。', time: '2026-04-04 14:10:00', tag: '运输中' }, { id: 'lg-3', title: '包裹已签收', desc: '前台已代收盲盒包裹，签收结果已同步回订单中心。', time: '2026-04-04 15:00:00', tag: '已签收' } ] }, { parcelNo: 'PKG-1003-B', company: '京东物流', trackingNo: 'JD5171003002', status: '运输中', items: [ { id: 'MB-1002', title: '霓虹光剑·复刻版', sku: '冰川蓝', quantity: 1, price: 199 } ], tracks: [ { id: 'lg-4', title: '商家已发货', desc: '光剑包裹已从北区仓发出，等待干线运输。', time: '2026-04-04 13:20:00', tag: '已发货' }, { id: 'lg-5', title: '运输中', desc: '光剑包裹已进入华东干线，预计今晚送达站点。', time: '2026-04-04 16:30:00', tag: '运输中' } ] } ], tracks: [ { id: 'lg-1', title: '商家已发货', desc: '首个包裹已由 5174 仓配中心完成发货。', time: '2026-04-04 12:45:00', tag: '已发货' }, { id: 'lg-5', title: '部分签收', desc: '盲盒包裹已签收，光剑包裹仍在运输中。', time: '2026-04-04 16:30:00', tag: '部分签收' } ] } }
  ];
  if (!db.rentals) db.rentals = [];
  if (!db.rentalCatalog) db.rentalCatalog = [
    { id: 'VR-001', name: 'PICO 4 VR头显', desc: '4K+画质，沉浸式体验必备', price: 30, unit: '小时', deposit: 500, stock: 50, maintenance: 3, image: 'http://localhost:8080/images/asset_201ce36241422d049cd8b5f223440d7a.jpg' },
    { id: 'LS-001', name: '光剑体感手柄套装', desc: '双持手柄，适配多款音乐节奏游戏', price: 15, unit: '小时', deposit: 200, stock: 80, maintenance: 2, image: 'http://localhost:8080/images/asset_4516e294e06df96eed7a31de50a1073b.jpg' },
    { id: 'COMBO-001', name: '全套装备组合包', desc: 'VR头显 + 手柄 + 护目镜', price: 50, unit: '小时', deposit: 800, stock: 20, maintenance: 1, image: 'http://localhost:8080/images/asset_da473f3dd8815d5a031bf316047315fc.jpg' }
  ];
  const getRentalDevice = (deviceId) => db.rentalCatalog.find(item => String(item.id) === String(deviceId) || String(item.name) === String(deviceId));
  const formatDurationLabel = (durationHours) => Number(durationHours) >= 4 ? '半天' : `${Number(durationHours || 1)}小时`;
  const buildRentalSnapshot = (rental) => {
    const now = Date.now();
    const startedAt = new Date(rental.startAt || rental.createdAt || now).getTime();
    const expiresAt = new Date(rental.expiresAt || startedAt).getTime();
    const remainingMs = expiresAt - now;
    const isOvertime = rental.status === 'OVERDUE' || remainingMs < 0;
    const totalMinutes = Math.max(Math.abs(Math.floor(remainingMs / 60000)), 0);
    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;
    const timeLeft = rental.status === 'RETURNED'
      ? '已归还'
      : isOvertime
        ? `超时 ${hours}小时${minutes}分钟`
        : `${hours}小时${minutes}分钟`;
    return {
      ...rental,
      device: rental.deviceName,
      sn: rental.deviceSn,
      duration: formatDurationLabel(rental.durationHours),
      timeLeft,
      isOvertime
    };
  };
  const buildRentalInventory = () => db.rentalCatalog.map(device => {
    const related = db.rentals.filter(item => item.deviceId === device.id && item.status !== 'RETURNED');
    return {
      id: device.id,
      name: device.name,
      total: device.stock,
      available: Math.max(device.stock - related.length - Number(device.maintenance || 0), 0),
      rented: related.filter(item => item.status === 'ACTIVE' || item.status === 'PENDING_PICKUP' || item.status === 'OVERDUE').length,
      maintenance: Number(device.maintenance || 0)
    };
  });
  if (!db.desks) db.desks = [
    { id: 'desk-01', name: '01号工位', status: 'FREE', type: 'STANDARD', zone: 'A区', location: 'A区靠窗', seatLabel: 'A-01' },
    { id: 'desk-02', name: '02号工位', status: 'FREE', type: 'STANDARD', zone: 'A区', location: 'A区中岛', seatLabel: 'A-02' },
    { id: 'desk-vip1', name: 'VIP包厢', status: 'FREE', type: 'VIP', zone: 'VIP区', location: 'VIP独立包厢', seatLabel: 'VIP-01' }
  ];
  if (!db.meetings) db.meetings = [
    {
      id: 1,
      name: 'V1 赛博洽谈室',
      capacity: 6,
      location: '2F 东侧',
      status: 'AVAILABLE',
      nextSlot: '今天 19:30',
      equipment: ['投影仪', '白板'],
      bookings: [{ left: '10%', width: '20%' }, { left: '50%', width: '15%' }]
    },
    {
      id: 2,
      name: 'V2 全息培训室',
      capacity: 20,
      location: '3F 培训区',
      status: 'OCCUPIED',
      nextSlot: '明天 10:00',
      equipment: ['8K大屏', '音响'],
      bookings: [{ left: '60%', width: '30%' }]
    }
  ];
  if (!db.userProfile) db.userProfile = { id: 1, name: 'Alex S.', pts: 1250, nfts: 3, level: 'V4', balance: 500 };
  if (!db.healthSummary) db.healthSummary = {
    score: 85,
    calories: 650,
    duration: 45,
    standHours: 10,
    standGoal: 12,
    heartRate: 112,
    steps: 8432,
    stepGoal: 10000,
    strain: '高',
    sleep: '7h 20m',
    deepSleep: '2h 10m',
    recentActivities: [
      { id: 1, name: '室内攀岩', icon: '🧗', date: '今天 14:30', duration: '45 分钟', calories: 320 },
      { id: 2, name: 'VR 动感单车', icon: '🚴', date: '昨天 19:00', duration: '30 分钟', calories: 280 },
      { id: 3, name: '跑步机', icon: '🏃', date: '周一 08:00', duration: '40 分钟', calories: 410 }
    ]
  };
  if (!db.trainingCourses) db.trainingCourses = {
    currentLevel: '中级游戏向导 (L2)',
    creditsToNext: 120,
    completedRequired: 3,
    totalRequired: 5,
    courses: [
      { id: 1, title: 'VR 设备基础故障排查 (L2)', desc: '学习如何处理常见的 VR 头显定位丢失、手柄断连问题。', credits: 40, image: 'http://localhost:8080/images/asset_b5d8e97d96ae216c385a29205651edc2.jpg' },
      { id: 2, title: '高客单价会员转化话术', desc: '提升沟通技巧，掌握推销年卡与高级储值的核心方法。', credits: 50, image: 'http://localhost:8080/images/asset_796873a5fff1eb3ff6d1d391a7ea71f6.jpg' }
    ]
  };
  if (!db.helpCenter) db.helpCenter = {
    categories: [
      { id: 1, name: '新手指南', icon: 'i-carbon-direction-straight-right text-blue-400' },
      { id: 2, name: '账号与积分', icon: 'i-carbon-user-profile text-purple-400' },
      { id: 3, name: '设备操作', icon: 'i-carbon-game-console text-green-400' },
      { id: 4, name: '售后退款', icon: 'i-carbon-currency text-yellow-400' }
    ],
    articles: [
      { id: 101, categoryId: 2, title: '如何绑定微信并同步健康数据？', content: '进入“我的-账号设置”，绑定微信后系统会自动同步您的运动健康授权状态。' },
      { id: 102, categoryId: 2, title: '会员等级成长值是如何计算的？', content: '消费、签到、赛事报名与任务完成都会累计成长值，系统每日凌晨同步等级进度。' },
      { id: 103, categoryId: 3, title: 'VR 赛博战场头显佩戴指南', content: '佩戴前请先调整头箍，再确认双眼焦距清晰，体验中如有眩晕请立即暂停。' },
      { id: 104, categoryId: 4, title: '购买盲盒后如何申请退款？', content: '未拆封实物盲盒可在订单页申请售后；虚拟抽盒结果一经开盒不可撤销。' }
    ]
  };
  if (!db.mallProducts) db.mallProducts = [
    {
      id: 'MB-1001',
      name: '赛博朋克限定盲盒·第一弹',
      description: '包含6个常规款+1个隐藏款。采用高精度3D打印技术，配合全息涂层，完美复刻赛博朋克世界的机械美学。',
      cashPrice: 59,
      pointsPrice: 590,
      originalPrice: 99,
      image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg',
      stock: 99,
      saleStatus: 'ON_SHELF',
      statusText: '在售',
      defaultSkuId: 'sku-default',
      skuList: [{ id: 'sku-default', name: '默认规格' }],
      categoryId: 'blindbox'
    },
    {
      id: 'MB-1002',
      name: '霓虹光剑·复刻版',
      description: '收藏级霓虹光剑模型，适合礼品陈列与玩家打卡。',
      cashPrice: 199,
      pointsPrice: 1990,
      originalPrice: 299,
      image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg',
      stock: 48,
      saleStatus: 'ON_SHELF',
      statusText: '在售',
      defaultSkuId: 'sku-blue',
      skuList: [{ id: 'sku-blue', name: '冰川蓝' }],
      categoryId: 'figure'
    },
    {
      id: 'MB-1003',
      name: '赛博能量徽章·限量版',
      description: '线下赛事限定能量徽章，当前批次已售罄。',
      cashPrice: 39,
      pointsPrice: 390,
      originalPrice: 59,
      image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg',
      stock: 0,
      saleStatus: 'ON_SHELF',
      statusText: '已售罄',
      defaultSkuId: 'sku-badge',
      skuList: [{ id: 'sku-badge', name: '限定徽章' }],
      categoryId: 'figure'
    },
    {
      id: 'MB-1004',
      name: '夜行者机能外套',
      description: '季节性周边，当前已下架。',
      cashPrice: 329,
      pointsPrice: 3290,
      originalPrice: 429,
      image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg',
      stock: 8,
      saleStatus: 'OFF_SHELF',
      statusText: '已下架',
      defaultSkuId: 'sku-black',
      skuList: [{ id: 'sku-black', name: '曜石黑' }],
      categoryId: 'figure'
    },
    {
      id: 'MB-1005',
      name: '旧版本数字兑换卡',
      description: '旧活动兑换卡，当前已失效。',
      cashPrice: 9.9,
      pointsPrice: 99,
      originalPrice: 19.9,
      image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg',
      stock: 0,
      saleStatus: 'INVALID',
      statusText: '已失效',
      defaultSkuId: 'sku-card',
      skuList: [{ id: 'sku-card', name: '电子卡' }],
      categoryId: 'blindbox'
    }
  ];
  if (!db.cartItems) db.cartItems = [
    {
      cartItemId: 'CART-1001',
      productId: 'MB-1001',
      skuId: 'sku-default',
      title: '赛博朋克限定盲盒·第一弹',
      sku: '默认规格',
      price: 59,
      quantity: 1,
      checked: true,
      image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg',
      stock: 99,
      sourceType: 'MALL',
      sourcePage: 'pages/mall/detail/index',
      userId: 'Player_Mobile_01'
    },
    {
      cartItemId: 'CART-1002',
      productId: 'MB-1002',
      skuId: 'sku-blue',
      title: '霓虹光剑·复刻版',
      sku: '冰川蓝',
      price: 199,
      quantity: 50,
      checked: false,
      image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg',
      stock: 48,
      sourceType: 'MALL',
      sourcePage: 'pages/mall/detail/index',
      userId: 'Player_Mobile_01'
    },
    {
      cartItemId: 'CART-1003',
      productId: 'MB-1004',
      skuId: 'sku-black',
      title: '夜行者机能外套',
      sku: '曜石黑',
      price: 329,
      quantity: 1,
      checked: false,
      image: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg',
      stock: 8,
      sourceType: 'MALL',
      sourcePage: 'pages/mall/detail/index',
      userId: 'Player_Mobile_01'
    }
  ];
  if (!db.ticket_timelines) db.ticket_timelines = {};
  if (!db.tickets) db.tickets = [
    { id: 'TK-1001', type: '设备报修', title: 'VR头显画面模糊', desc: '体验中画面模糊且偶发黑屏', assignee: '服务台', priority: 'HIGH', status: 'RESOLVED', statusText: '已解决', createdAt: '2026-04-06 10:00:00', updatedAt: '2026-04-06 10:32:00', resolutionNote: '已完成镜片清洁与重新校准。', user: 'Player_Mobile_01' },
    { id: 'TK-1002', type: '退款/售后', title: '盲盒重复申请退款', desc: '需要核对重复扣款记录', assignee: '客服专员', priority: 'MEDIUM', status: 'PROCESSING', statusText: '处理中', createdAt: '2026-04-06 09:10:00', updatedAt: '2026-04-06 09:30:00', resolutionNote: '', user: 'Player_Mobile_01' },
    { id: 'TK-0998', type: '环境客诉', title: 'A区空调太冷', desc: '顾客反馈体感偏冷', assignee: 'Alex (Manager)', priority: 'HIGH', status: 'CLOSED', statusText: '已关闭', createdAt: '2026-04-05 18:00:00', updatedAt: '2026-04-05 18:20:00', resolutionNote: '已将温度调至 24℃ 并完成回访确认', user: 'Player_Mobile_01' }
  ];
  const normalizeOrder = (order = {}) => ({
    sourceApp: '5171 移动端',
    paymentChannel: '微信支付',
    contactName: 'Alex S.',
    ticketCount: 1,
    type: 'BOOKING',
    ...order
  });
  const normalizeTicket = (ticket = {}) => ({
    priority: 'MEDIUM',
    status: 'PENDING',
    statusText: '待处理',
    assignee: '客服中台',
    user: 'Player_Mobile_01',
    attachments: [],
    latestReply: '',
    latestReplyAt: '',
    latestReplyOperator: '',
    ...ticket
  });
  db.orders = db.orders.map(normalizeOrder);
  db.tickets = db.tickets.map(normalizeTicket);
  if (!db.tickets.some(ticket => String(ticket.id).startsWith('TK-'))) {
    db.tickets = [
      normalizeTicket({ id: 'TK-1001', type: '设备报修', title: 'VR头显画面模糊', desc: '体验中画面模糊且偶发黑屏', assignee: '服务台', priority: 'HIGH', status: 'RESOLVED', statusText: '已解决', createdAt: '2026-04-06 10:00:00', updatedAt: '2026-04-06 10:32:00', resolutionNote: '已完成镜片清洁与重新校准。', user: 'Player_Mobile_01' }),
      normalizeTicket({ id: 'TK-1002', type: '退款/售后', title: '盲盒重复申请退款', desc: '需要核对重复扣款记录', assignee: '客服专员', priority: 'MEDIUM', status: 'PROCESSING', statusText: '处理中', createdAt: '2026-04-06 09:10:00', updatedAt: '2026-04-06 09:30:00', resolutionNote: '', latestReply: '客服已收到退款核查申请，正在核对支付流水。', latestReplyAt: '2026-04-06 09:30:00', latestReplyOperator: '客服专员', user: 'Player_Mobile_01' }),
      normalizeTicket({ id: 'TK-0998', type: '环境客诉', title: 'A区空调太冷', desc: '顾客反馈体感偏冷', assignee: 'Alex (Manager)', priority: 'HIGH', status: 'CLOSED', statusText: '已关闭', createdAt: '2026-04-05 18:00:00', updatedAt: '2026-04-05 18:20:00', resolutionNote: '已将温度调至 24℃ 并完成回访确认', user: 'Player_Mobile_01' })
    ];
  }
  const appendTicketTimeline = (ticketId, entry) => {
    if (!ticketId) return;
    if (!db.ticket_timelines[ticketId]) {
      db.ticket_timelines[ticketId] = [];
    }
    db.ticket_timelines[ticketId].unshift({
      id: `${ticketId}-${Date.now()}-${Math.random().toString(16).slice(2, 6)}`,
      timestamp: new Date().toISOString(),
      ...entry
    });
  };
  const ensureWorkflowTickets = () => {
    if (!db.workflow_tickets) db.workflow_tickets = [];
    return db.workflow_tickets;
  };
  const findTicketWorkflowByTicketId = (ticketId) => ensureWorkflowTickets().find(ticket =>
    ticket.relatedType === 'TICKET' && String(ticket.relatedId) === String(ticketId)
  );
  const getTicketWorkflowConfig = (displayType) => {
    if (displayType === '设备报修') {
      return { type: 'REPAIR', processName: '维修工单', externalWorkspace: 'admin', externalPath: '/workflow/list' };
    }
    if (displayType === '环境客诉' || displayType === '退款/售后') {
      return { type: 'CUSTOMER_SERVICE', processName: '客服处理', externalWorkspace: 'business', externalPath: '/ticket-system' };
    }
    return { type: 'SERVICE', processName: '通用服务', externalWorkspace: 'business', externalPath: '/ticket-system' };
  };
  const createTicketWorkflowTask = (ticketPayload) => {
    const workflowTickets = ensureWorkflowTickets();
    const existed = findTicketWorkflowByTicketId(ticketPayload.ticketId);
    if (existed) return existed;
    const config = getTicketWorkflowConfig(ticketPayload.displayType);
    const task = {
      id: `W-${Date.now()}`,
      type: config.type,
      processName: config.processName,
      title: `工单处理 · ${ticketPayload.title}`,
      priority: ticketPayload.priority || 'MEDIUM',
      status: 'NEW',
      starter: ticketPayload.user || 'Player_Mobile_01',
      nodeName: '待接单',
      relatedId: ticketPayload.ticketId,
      relatedType: 'TICKET',
      createdAt: new Date().toISOString(),
      externalWorkspace: config.externalWorkspace,
      externalPath: config.externalPath,
      assignee: ticketPayload.assignee || '客服中台'
    };
    workflowTickets.unshift(task);
    return task;
  };
  const syncTicketByWorkflowAction = (task, action, reason = '') => {
    if (!task || task.relatedType !== 'TICKET') return null;
    const targetTicket = db.tickets.find(item => String(item.id) === String(task.relatedId));
    if (!targetTicket) return null;
    const updatedAt = new Date().toLocaleString();
    targetTicket.updatedAt = updatedAt;
    targetTicket.workflowTaskId = task.id;
    targetTicket.workflowStatus = task.status;
    targetTicket.workflowNodeName = task.nodeName;
    targetTicket.assignee = task.assignee || targetTicket.assignee;
    if (action === 'APPROVED') {
      targetTicket.status = 'PROCESSING';
      targetTicket.statusText = '处理中';
      targetTicket.resolutionNote = reason || targetTicket.resolutionNote || '已进入处理阶段';
      appendTicketTimeline(targetTicket.id, {
        stage: 'ACCEPTED',
        title: '工单已接单',
        operator: task.reviewer || task.assignee || '服务台',
        detail: `${task.reviewer || task.assignee || '服务台'} 已开始跟进处理`,
        meta: [
          { label: '流程单号', value: task.id },
          { label: '当前节点', value: task.nodeName || '处理中' }
        ]
      });
      appendTicketTimeline(targetTicket.id, {
        stage: 'PROCESSING',
        title: '工单进入处理中',
        operator: task.reviewer || task.assignee || '服务台',
        detail: reason || '已进入处理阶段',
        meta: [
          { label: '处理人', value: task.reviewer || task.assignee || '服务台' },
          { label: '状态', value: '处理中' }
        ]
      });
      return {
        ticketId: targetTicket.id,
        title: targetTicket.title,
        status: 'PROCESSING',
        statusText: '处理中',
        assignee: targetTicket.assignee,
        updatedAt,
        resolutionNote: reason || '',
        workflowTaskId: task.id,
        workflowStatus: task.status,
        attachments: targetTicket.attachments || []
      };
    }
    targetTicket.status = 'CLOSED';
    targetTicket.statusText = '已关闭';
    targetTicket.resolutionNote = reason || '工单已驳回并归档关闭';
    appendTicketTimeline(targetTicket.id, {
      stage: 'CLOSED',
      title: '工单已关闭',
      operator: task.reviewer || task.assignee || '服务台',
      detail: targetTicket.resolutionNote,
      meta: [
        { label: '流程单号', value: task.id },
        { label: '归档状态', value: '已关闭' }
      ]
    });
    return {
      ticketId: targetTicket.id,
      title: targetTicket.title,
      status: 'CLOSED',
      statusText: '已关闭',
      assignee: targetTicket.assignee,
      updatedAt,
      closedAt: updatedAt,
      closeNote: targetTicket.resolutionNote,
      workflowTaskId: task.id,
      workflowStatus: task.status,
      attachments: targetTicket.attachments || []
    };
  };
  const findOrderById = (orderId) => db.orders.find(item => String(item.id) === String(orderId));
  const findRefundWorkflowByOrderId = (orderId) => ensureWorkflowTickets().find(ticket => ticket.type === 'REFUND' && String(ticket.relatedId) === String(orderId));
  const findRefundServiceTicketByOrderId = (orderId) => db.tickets.find(ticket =>
    ticket.type === '退款/售后' && String(ticket.relatedOrderId || ticket.relatedId || '') === String(orderId)
  );
  const createRefundWorkflowTicket = (order, reqBody = {}) => {
    const workflowTickets = ensureWorkflowTickets();
    const existed = findRefundWorkflowByOrderId(order.id);
    if (existed && existed.status === 'NEW') return existed;
    const createdAt = new Date().toISOString();
    const ticket = {
      id: `W-${Date.now()}`,
      type: 'REFUND',
      title: `退款审批 · ${order.title}`,
      priority: order.amount >= 200 ? 'HIGH' : 'MEDIUM',
      status: 'NEW',
      starter: reqBody.userId || reqBody.user || 'Player_Mobile_01',
      nodeName: '待审核',
      relatedId: order.id,
      relatedType: 'ORDER',
      createdAt,
      externalWorkspace: 'admin',
      externalPath: `/workflow/list?relatedId=${encodeURIComponent(order.id)}&type=REFUND`,
      requestId: reqBody.requestId || `RF-${Date.now()}`,
      reason: reqBody.reason || order.refundReason || '移动端发起售后申请',
      amount: order.amount,
      evidence: Array.isArray(reqBody.evidence) ? reqBody.evidence : Array.isArray(order.refundEvidence) ? order.refundEvidence : [],
      supplementNote: reqBody.supplementNote || order.refundSupplementNote || ''
    };
    workflowTickets.unshift(ticket);
    return ticket;
  };
  const createRefundServiceTicket = (order, reqBody = {}, workflowTicket = null) => {
    const existed = findRefundServiceTicketByOrderId(order.id);
    const now = new Date().toLocaleString();
    const ticketId = existed?.id || `TK-RF-${Date.now()}`;
    const ticketPayload = normalizeTicket({
      ...existed,
      id: ticketId,
      ticketId,
      type: '退款/售后',
      displayType: '退款/售后',
      title: `退款/售后 · ${order.title}`,
      desc: reqBody.reason || order.refundReason || '5171 移动端发起退款/售后申请',
      assignee: '5178 客服台',
      priority: Number(order.amount || 0) >= 200 ? 'HIGH' : 'MEDIUM',
      status: 'PENDING',
      statusText: '待审核',
      createdAt: existed?.createdAt || now,
      updatedAt: now,
      user: reqBody.userId || reqBody.user || order.user || 'Player_Mobile_01',
      relatedId: order.id,
      relatedOrderId: order.id,
      refundRequestId: reqBody.requestId || order.refundRequestId || `RF-${Date.now()}`,
      workflowTaskId: workflowTicket?.id || order.refundWorkflowTaskId || existed?.workflowTaskId || '',
      workflowStatus: workflowTicket?.status || existed?.workflowStatus || 'NEW',
      workflowNodeName: '退款审核中',
      resolutionNote: reqBody.supplementNote
        ? `用户已补充退款材料：${reqBody.supplementNote}`
        : `5171 已提交退款申请：${reqBody.reason || order.refundReason || '等待 5174 审核'}`
    });
    if (existed) Object.assign(existed, ticketPayload);
    else db.tickets.unshift(ticketPayload);
    appendTicketTimeline(ticketId, {
      stage: reqBody.supplementNote ? 'UPDATED' : 'CREATED',
      title: reqBody.supplementNote ? '退款申请已补件' : '退款申请已提交',
      operator: ticketPayload.user,
      detail: reqBody.supplementNote
        ? `已补充退款材料并重新发起审核：${reqBody.supplementNote}`
        : `${ticketPayload.user} 已在 5171 发起退款/售后申请`,
      meta: [
        { label: '订单号', value: order.id },
        { label: '退款单号', value: ticketPayload.refundRequestId || '待生成' },
        { label: '流程单号', value: workflowTicket?.id || ticketPayload.workflowTaskId || '待生成' }
      ]
    });
    return { ticket: ticketPayload, existed: Boolean(existed) };
  };
  const syncRefundServiceTicket = (orderId, stage, options = {}) => {
    const ticket = findRefundServiceTicketByOrderId(orderId);
    if (!ticket) return null;
    const updatedAt = new Date().toLocaleString();
    ticket.updatedAt = updatedAt;
    if (options.workflowTaskId) ticket.workflowTaskId = options.workflowTaskId;
    if (options.workflowStatus) ticket.workflowStatus = options.workflowStatus;
    if (stage === 'APPROVED') {
      ticket.status = 'PROCESSING';
      ticket.statusText = '处理中';
      ticket.workflowNodeName = '财务待打款';
      ticket.assignee = options.assignee || ticket.assignee || '5178 客服台';
      ticket.resolutionNote = options.reason || `${options.operator || '5174 审核员'} 已通过退款审核，等待财务打款。`;
      appendTicketTimeline(ticket.id, {
        stage: 'PROCESSING',
        title: '退款审核已通过',
        operator: options.operator || '5174 审核员',
        detail: ticket.resolutionNote,
        meta: [
          { label: '订单号', value: orderId },
          { label: '当前节点', value: ticket.workflowNodeName }
        ]
      });
      return {
        ticketId: ticket.id,
        status: ticket.status,
        statusText: ticket.statusText,
        assignee: ticket.assignee,
        updatedAt,
        resolutionNote: ticket.resolutionNote,
        workflowTaskId: ticket.workflowTaskId,
        workflowStatus: ticket.workflowStatus,
        workflowNodeName: ticket.workflowNodeName,
        relatedId: orderId
      };
    }
    if (stage === 'REJECTED') {
      ticket.status = 'CLOSED';
      ticket.statusText = '已关闭';
      ticket.workflowNodeName = '退款审核已驳回';
      ticket.resolutionNote = options.reason || '退款审核未通过，请补充材料后重新提交。';
      appendTicketTimeline(ticket.id, {
        stage: 'CLOSED',
        title: '退款申请已驳回',
        operator: options.operator || '5174 审核员',
        detail: ticket.resolutionNote,
        meta: [
          { label: '订单号', value: orderId },
          { label: '归档状态', value: '已关闭' }
        ]
      });
      return {
        ticketId: ticket.id,
        status: ticket.status,
        statusText: ticket.statusText,
        assignee: ticket.assignee,
        updatedAt,
        closeNote: ticket.resolutionNote,
        workflowTaskId: ticket.workflowTaskId,
        workflowStatus: ticket.workflowStatus,
        workflowNodeName: ticket.workflowNodeName,
        relatedId: orderId
      };
    }
    if (stage === 'SETTLED') {
      ticket.status = 'RESOLVED';
      ticket.statusText = '已解决';
      ticket.workflowNodeName = '退款完成';
      ticket.resolutionNote = `${options.operator || '5174 财务台'} 已完成退款打款，结果已回流到 5171。`;
      appendTicketTimeline(ticket.id, {
        stage: 'RESOLVED',
        title: '退款已完成',
        operator: options.operator || '5174 财务台',
        detail: ticket.resolutionNote,
        meta: [
          { label: '订单号', value: orderId },
          { label: '退款时间', value: options.refundedAt || updatedAt }
        ]
      });
      return {
        ticketId: ticket.id,
        resolvedAt: options.refundedAt || updatedAt,
        assignee: ticket.assignee,
        resolutionNote: ticket.resolutionNote,
        workflowTaskId: ticket.workflowTaskId,
        relatedId: orderId,
        attachments: ticket.attachments || []
      };
    }
    return null;
  };
  const getCartUserId = (req) => req.query.userId || req.body?.userId || 'Player_Mobile_01';
  const getProductById = (productId) => db.mallProducts.find(item => String(item.id) === String(productId));
  const getCartItemsByUserId = (userId) => db.cartItems.filter(item => String(item.userId) === String(userId));
  const resolveMallAvailability = (product, quantity = 1) => {
    if (!product) return { saleStatus: 'INVALID', statusText: '商品已失效', invalidReason: '商品已失效', canCheckout: false };
    if (product.saleStatus === 'OFF_SHELF') return { saleStatus: 'OFF_SHELF', statusText: product.statusText || '已下架', invalidReason: '商品已下架', canCheckout: false };
    if (product.saleStatus === 'INVALID') return { saleStatus: 'INVALID', statusText: product.statusText || '已失效', invalidReason: '商品已失效', canCheckout: false };
    if (Number(product.stock || 0) <= 0) return { saleStatus: product.saleStatus || 'ON_SHELF', statusText: product.statusText || '已售罄', invalidReason: '库存不足', canCheckout: false };
    if (Number(quantity || 1) > Number(product.stock || 0)) return { saleStatus: product.saleStatus || 'ON_SHELF', statusText: `仅剩 ${product.stock} 件`, invalidReason: `库存不足，仅剩 ${product.stock} 件`, canCheckout: false };
    return { saleStatus: product.saleStatus || 'ON_SHELF', statusText: product.statusText || '在售', invalidReason: '', canCheckout: true };
  };
  const serializeCartItem = (item) => {
    const product = getProductById(item.productId);
    const availability = resolveMallAvailability(product, item.quantity);
    return {
      id: item.cartItemId,
      cartItemId: item.cartItemId,
      productId: item.productId,
      skuId: item.skuId || '',
      title: item.title,
      sku: item.sku || '默认规格',
      price: Number(item.price || 0),
      quantity: Number(item.quantity || 1),
      checked: availability.canCheckout ? item.checked !== false : false,
      image: item.image || '',
      stock: Number(product?.stock ?? item.stock ?? 99),
      saleStatus: availability.saleStatus,
      statusText: availability.statusText,
      invalidReason: availability.invalidReason,
      canCheckout: availability.canCheckout,
      sourceType: item.sourceType || 'MALL',
      sourcePage: item.sourcePage || 'pages/mall/detail/index'
    };
  };
  if (!db.ticket_timelines['TK-1001']?.length) {
    appendTicketTimeline('TK-1001', { stage: 'CREATED', title: '工单已创建', operator: 'Player_Mobile_01', detail: '体验中画面模糊且偶发黑屏' });
    appendTicketTimeline('TK-1001', { stage: 'PROCESSING', title: '服务台已介入', operator: '服务台', detail: '已排查镜片污染与设备校准问题' });
    appendTicketTimeline('TK-1001', { stage: 'RESOLVED', title: '工单已解决', operator: '服务台', detail: '已完成镜片清洁与重新校准。' });
  }
  if (!db.ticket_timelines['TK-1002']?.length) {
    appendTicketTimeline('TK-1002', { stage: 'CREATED', title: '售后工单已创建', operator: 'Player_Mobile_01', detail: '需要核对重复扣款记录' });
    appendTicketTimeline('TK-1002', { stage: 'PROCESSING', title: '客服专员处理中', operator: '客服专员', detail: '已进入售后核查流程' });
    appendTicketTimeline('TK-1002', { stage: 'REPLIED', title: '客服回复', operator: '客服专员', detail: '客服已收到退款核查申请，正在核对支付流水。' });
  }
  if (!db.ticket_timelines['TK-0998']?.length) {
    appendTicketTimeline('TK-0998', { stage: 'CREATED', title: '客诉工单已创建', operator: 'Player_Mobile_01', detail: '顾客反馈体感偏冷' });
    appendTicketTimeline('TK-0998', { stage: 'RESOLVED', title: '门店已处理', operator: 'Alex (Manager)', detail: '已将温度调至 24℃ 并完成回访确认' });
    appendTicketTimeline('TK-0998', { stage: 'CLOSED', title: '工单已关闭', operator: 'Alex (Manager)', detail: '顾客确认处理完成' });
  }
  if (!db.mobileNotifications) db.mobileNotifications = [
    { id: 1, category: 'asset', title: '盲盒出货提醒', desc: '恭喜您在线下实体机成功抽取 SSR 级潮玩「Neon Skullpanda - Phantom」，资产已自动同步至您的数字背包。', time: '刚刚', icon: 'i-carbon-gift', iconBg: 'bg-purple-900/30', iconColor: 'text-purple-400', action: true, actionText: '查看资产', actionUrl: '/pages/nft/index', isRead: false },
    { id: 2, category: 'event', title: '新活动发布：第一届赛博战神杯 VR对决', desc: '深圳旗舰店刚刚发布了新的赛事活动，快来抢先报名锁定席位！', time: '10分钟前', icon: 'i-carbon-event-schedule', iconBg: 'bg-pink-900/30', iconColor: 'text-pink-400', action: true, actionText: '立即报名', actionUrl: '/pages/event/index', isRead: false },
    { id: 3, category: 'system', title: '系统维护通知', desc: 'LobsterBrain AI 核心将于今日凌晨 02:00 进行模型升级，预计耗时 1 小时。期间教练建议功能可能出现延迟。', time: '2小时前', icon: 'i-carbon-warning', iconBg: 'bg-orange-900/30', iconColor: 'text-orange-400', action: false, isRead: false },
    { id: 4, category: 'finance', title: '退款申请已提交', desc: '订单 OD-5176-1001 的退款申请已进入审核队列，可在详情页查看售后进度。', time: '刚刚', icon: 'i-carbon-receipt', iconBg: 'bg-cyan-900/30', iconColor: 'text-cyan-400', action: true, actionText: '查看订单', actionUrl: '/pages/order/detail/index?orderId=OD-5176-1001', requestId: 'RF-DEMO-1001', detailMeta: { id: 'OD-5176-1001', title: 'Weekend VR Tournament 门票', date: '2026-04-05 19:30', amount: 168, status: 'REFUNDING', paymentChannel: '移动端售后', paymentSerialNo: 'OD-5176-1001', voucherRemark: '已进入财务审核队列' }, sticky: true, pinUntil: '2026-12-31T23:59:59.000Z', isRead: false },
    { id: 5, category: 'system', title: '工单已处理完成', desc: '工单 TK-1001 已由服务台完成首轮处理，可在详情页查看时间线与处理结果。', time: '刚刚', icon: 'i-carbon-task', iconBg: 'bg-blue-900/30', iconColor: 'text-blue-400', action: true, actionText: '查看工单', actionUrl: '/pages/ticket/detail/index?ticketId=TK-1001', requestId: 'TK-1001', detailMeta: { id: 'TK-1001', title: 'VR头显画面模糊', type: '设备报修', status: 'RESOLVED', statusText: '已解决', assignee: '服务台', resolutionNote: '已完成镜片清洁与重新校准。', operatorName: '服务台' }, sticky: true, pinUntil: '2026-12-31T23:59:59.000Z', isRead: false }
  ];
  db.mobileNotifications = db.mobileNotifications.map(item => ({ ...item, isRead: Boolean(item.isRead) }));
  if (!db.socialPosts) db.socialPosts = [
    {
      id: 1,
      author: 'CyberRunner',
      level: 15,
      avatar: 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg',
      time: '10分钟前',
      content: '今天在VR战场打出了全服最高分！太燃了，那个隐藏的彩蛋居然被我找到了！这波稳赚一个盲盒。有没有兄弟明晚继续组队的？',
      images: [
        'http://localhost:8080/images/asset_22a623a39eb7d987f6426ce652004e4b.jpg',
        'http://localhost:8080/images/asset_296a9124f3a6a3b2d9116e51e226ff5b.jpg'
      ],
      tags: ['VR体验', '全服第一', '高分攻略'],
      likes: 342,
      comments: 56,
      shares: 12,
      isLiked: true,
      isFollowed: false,
      tab: 'recommend',
      link: {
        title: '《未来战场》大空间VR',
        desc: '新天地赛博旗舰店 · 点击预约体验'
      }
    },
    {
      id: 2,
      author: '盲盒收集狂',
      level: 8,
      avatar: 'http://localhost:8080/images/asset_ed24322017e57663c0e4282c1409f9b8.svg',
      time: '1小时前',
      content: '绝了！用AR模式在休闲区扫到了隐藏的「机械之心」碎片，就差最后一个就能合成完整版NFT了！',
      images: ['http://localhost:8080/images/asset_918ce492fc55312bc7cf8653a3015299.jpg'],
      tags: ['数字藏品', 'AR打卡', '欧皇时刻'],
      likes: 890,
      comments: 120,
      shares: 45,
      isLiked: false,
      isFollowed: true,
      tab: 'recommend',
      link: null
    },
    {
      id: 3,
      author: 'NeonPulse_DJ',
      level: 22,
      avatar: 'http://localhost:8080/images/asset_9f8159a269aecd4737fa69bd68d12ea3.svg',
      time: '2小时前',
      content: '明晚的电音派对已经准备就绪！新曲目首发，现场还有特调饮品免单券掉落，记得准时来打卡哦！',
      images: ['http://localhost:8080/images/asset_579d83bd1482f3b8b385463b58379d7e.jpg'],
      tags: ['周末派对', 'DJ现场'],
      likes: 1205,
      comments: 88,
      shares: 300,
      isLiked: false,
      isFollowed: false,
      tab: 'local',
      link: {
        title: 'Neon Nights 赛博电音派对',
        desc: '3月15日 20:00 · 立即报名'
      }
    }
  ];
  if (!db.entryPass) db.entryPass = {
    code: 'ENTRY-8848001-ANT',
    expiresIn: '00:29:59',
    venue: '新天地赛博旗舰店'
  };

  const delay = (ms) => new Promise(res => setTimeout(res, ms));

  const respond = async (res, data, message = 'success', ms = 500) => {
    await delay(ms);
    res.json({ code: 200, data, message });
  };

  // Generic Catch-All for Mobile API Mocks
  router.use('/', async (req, res) => {
    const path = req.path;
    const method = req.method;
    
    console.log(`[MOBILE-API] ${method} ${path}`);
    
    // Broadcast generic notification for closed-loop feeling
    if (method === 'POST' || method === 'PUT' || method === 'DELETE') {
       io.emit('data_sync', {
          type: 'MOBILE_ACTION',
          payload: { path, body: req.body, timestamp: new Date().toISOString() }
       });
    }

    if (path.includes('login') || path.includes('signin')) {
      return respond(res, { token: 'mock-mobile-token-999', user: { id: 1, name: 'CyberPlayer', avatar: '' } });
    }
    
    if (path.includes('home')) {
      return respond(res, { 
        banners: [
          { image: '/static/assets/images/mobile_hero.jpg', title: '全息元宇宙电竞', subtitle: '限时 8 折体验' },
          { image: 'http://localhost:8080/images/asset_491a561331f2f390cf3b2c689e050a6a.jpg', title: '周末极客挑战赛', subtitle: '奖池 50,000 Pts' }
        ], 
        hotGames: db.game_library.slice(0, 4),
        announcements: [
          { id: 'notice-1', title: '系统升级通知', content: '会员中心将在今晚 23:00 完成例行升级，请提前保存预约信息。' },
          { id: 'notice-2', title: '双十一特惠开启', content: '本周到店体验、赛事报名与盲盒商城均已开放限时福利。' }
        ],
        nearbyVenues: [
          { id: 1, name: 'Cyber Arena HQ', distance: '1.2km', tags: ['VR', 'Esports'], image: '/static/assets/images/venue1.jpg' },
          { id: 2, name: 'Neon Fitness Hub', distance: '3.5km', tags: ['Fitness', '24H'], image: '/static/assets/images/venue2.jpg' }
        ]
      });
    }

    if (path.includes('play/categories') && method === 'GET') {
      const categories = Array.from(
        new Set(
          db.game_library
            .map((item) => item.category)
            .filter(Boolean)
        )
      );
      return respond(res, ['全部节点', ...categories]);
    }

    if (path.includes('booking/resources') && method === 'GET') {
      const pricePool = [168, 128, 88, 199];
      const resources = db.game_library.map((item, index) => {
        const id = String(item.id || index + 1);
        const category = item.category || '体验';
        const players = item.players ? `${item.players}人` : '多人';
        return {
          id,
          name: item.title || item.name || `体验项目 ${id}`,
          image: item.image || '/static/assets/images/mobile_hero.jpg',
          price: pricePool[index % pricePool.length],
          status: index % 3 === 2 ? 'BUSY' : 'AVAILABLE',
          hot: index % 2 === 0,
          tags: [category, players]
        };
      });
      return respond(res, resources);
    }

    if (path.includes('play/detail') && method === 'GET') {
      const id = String(req.query?.id || '');
      const item = db.game_library.find((game) => String(game.id) === id);
      const name = item?.title || '未来战场 VR';
      const category = item?.category || '沉浸式';
      const players = item?.players ? `${item.players}人` : '多人联机';
      return respond(res, {
        id: id || 'g1',
        name,
        price: 88,
        tags: [category, players],
        description: `佩戴最新的VR头显，进入 ${name} 的沉浸式场景，支持多人联机与现场 IoT 联动授权入场。`,
        image: item?.image || 'http://localhost:8080/images/asset_52ffd21165f088875945738c4f5fcee2.jpg'
      });
    }

    if (path.includes('booking/create') && method === 'POST') {
      const booking = {
        id: `BK-${Date.now()}`,
        resourceId: String(req.body?.resourceId || ''),
        time: req.body?.time || '',
        people: Number(req.body?.people || 1),
        teamCode: req.body?.teamCode || '',
        status: 'CONFIRMED',
        createdAt: new Date().toISOString()
      };
      if (!db.bookings) db.bookings = [];
      db.bookings.push(booking);
      io.emit('data_sync', { type: 'BOOKING_CREATED', payload: booking });
      return respond(res, booking);
    }
    
    if (path.includes('profile/entry-code')) {
      db.entryPass = {
        code: `ENTRY-${Date.now().toString().slice(-8)}`,
        expiresIn: '00:29:59',
        venue: '新天地赛博旗舰店'
      };
      return respond(res, db.entryPass);
    }

    if (path.includes('info') || path.includes('profile')) {
      return respond(res, db.userProfile);
    }

    // Work Desk Booking
    if (path.includes('desk/book')) {
      const { deskId } = req.body;
      const desk = db.desks.find(d => d.id === deskId);
      if (desk) desk.status = 'MINE';
      
      io.emit('data_sync', { type: 'DESK_STATUS_CHANGED', payload: { desks: db.desks } });
      io.emit('notification', { title: 'Reservation Confirmed', content: `Your desk ${deskId} was booked.` });
      return respond(res, { orderId: 'B' + Date.now(), status: 'CONFIRMED' });
    }
    if (path.includes('desk/list')) {
      return respond(res, db.desks);
    }
    
    // Meeting Room
    if (path.includes('meeting/book')) {
      const { roomId } = req.body;
      const room = db.meetings.find(r => r.id === roomId);
      if (room) {
        room.status = 'OCCUPIED';
        room.nextSlot = '请前往会议室中心查看';
        room.bookings.push({ left: '70%', width: '10%' });
      }
      
      io.emit('data_sync', { type: 'MEETING_ROOMS_UPDATE', payload: { rooms: db.meetings } });
      io.emit('state_update', { type: 'MEETING_BOOKED', payload: { roomId, user: 'Alex' } });
      io.emit('notification', { title: 'Reservation Confirmed', content: `Meeting room booked successfully.` });
      return respond(res, { orderId: 'M' + Date.now(), status: 'CONFIRMED' });
    }
    if (path.includes('meeting/list')) {
      return respond(res, db.meetings);
    }

    if (path.includes('orders/list')) {
      return respond(res, db.orders);
    }

    if (path.includes('orders/detail')) {
      const orderId = req.query.orderId || req.body?.orderId;
      const order = findOrderById(orderId);
      if (!order) {
        return respond(res, null, 'not_found', 404);
      }
      const refundWorkflow = findRefundWorkflowByOrderId(order.id);
      const timeline = [
        {
          id: `${order.id}-created`,
          title: '订单已创建',
          desc: `${order.sourceApp} 已完成订单建单，并同步到 5171 当前账号。`,
          time: order.createdAt || order.date,
          tag: '已建单'
        }
      ];
      timeline.push({
        id: `${order.id}-payment`,
        title: order.status === 'FAILED' ? '支付失败' : order.status === 'CANCELLED' ? '支付已取消' : '支付已完成',
        desc: order.status === 'FAILED'
          ? '支付链路未完成，本次交易保留为失败记录，可重新发起支付。'
          : order.status === 'CANCELLED'
            ? '用户已取消支付，订单保留在当前账号中便于稍后重试。'
            : `支付渠道 ${order.paymentChannel || '待同步'} 已完成扣款并写入订单中心。`,
        time: order.completedAt || order.failedAt || order.cancelledAt || order.updatedAt || order.date,
        tag: order.status === 'FAILED' ? '失败' : order.status === 'CANCELLED' ? '已取消' : '已支付'
      });
      if (order.status !== 'FAILED' && order.status !== 'CANCELLED') {
        const orderFulfillmentType = order.sourceType || order.type;
        const fulfillmentTimeline = orderFulfillmentType === 'EVENT'
          ? [
              {
                id: `${order.id}-event-seat`,
                title: '赛事名额已锁定',
                desc: '系统已为当前账号保留活动席位，并同步玩家站报名记录。',
                time: order.updatedAt || order.date,
                tag: '已锁定'
              },
              {
                id: `${order.id}-event-ticket`,
                title: '电子门票已出票',
                desc: '电子票与入场凭证已同步到玩家中心，可直接核销入场。',
                time: order.completedAt || order.updatedAt || order.date,
                tag: '可核销'
              }
            ]
          : orderFulfillmentType === 'MALL'
            ? (() => {
                const parcelTracks = Array.isArray(order.logistics?.parcels)
                  ? order.logistics.parcels.flatMap((parcel, parcelIndex) =>
                      Array.isArray(parcel?.tracks)
                        ? parcel.tracks.map((track, trackIndex) => ({
                            id: track.id || `${order.id}-parcel-${parcelIndex}-${trackIndex}`,
                            title: `${track.title}${parcel.parcelNo ? ` · ${parcel.parcelNo}` : ''}`,
                            desc: track.desc,
                            time: track.time,
                            tag: track.tag || parcel.status || '物流中'
                          }))
                        : []
                    )
                  : [];
                if (parcelTracks.length) return parcelTracks;
                if (Array.isArray(order.logistics?.tracks) && order.logistics.tracks.length) {
                  return order.logistics.tracks.map((track, index) => ({
                    id: track.id || `${order.id}-mall-logistics-${index}`,
                    title: track.title,
                    desc: track.desc,
                    time: track.time,
                    tag: track.tag || order.logistics?.status || '物流中'
                  }));
                }
                return [
                    {
                      id: `${order.id}-mall-pack`,
                      title: '仓配已出库',
                      desc: '商品已进入仓配处理，订单履约状态已同步到移动端。',
                      time: order.updatedAt || order.date,
                      tag: '已出库'
                    },
                    {
                      id: `${order.id}-mall-delivered`,
                      title: '商品已签收',
                      desc: '订单已完成签收，如有问题可继续发起售后申请。',
                      time: order.completedAt || order.updatedAt || order.date,
                      tag: '已签收'
                    }
                  ];
              })()
            : [
                {
                  id: `${order.id}-booking-confirmed`,
                  title: '预约场次已确认',
                  desc: '门店与玩家站已确认本次体验场次，预约信息已同步。',
                  time: order.updatedAt || order.date,
                  tag: '已确认'
                },
                {
                  id: `${order.id}-booking-fulfilled`,
                  title: '到店体验已核销',
                  desc: '本次预约已核销完成，可继续评价或发起售后处理。',
                  time: order.completedAt || order.updatedAt || order.date,
                  tag: '已核销'
                }
              ];
        timeline.push(...fulfillmentTimeline);
      }
      if (order.status === 'COMPLETED') {
        timeline.push({
          id: `${order.id}-completed`,
          title: '订单已完成',
          desc: '门票 / 商品已交付完成，可继续查看详情或发起售后申请。',
          time: order.completedAt || order.date,
          tag: '已完成'
        });
      }
      if (order.status === 'REFUNDING' || order.refundRequestedAt) {
        timeline.push({
          id: `${order.id}-refund-requested`,
          title: '退款申请已提交',
          desc: order.refundReason || '售后申请已进入客服与财务审核流程。',
          time: order.refundRequestedAt || order.updatedAt || '刚刚',
          tag: '审核中'
        });
      }
      if (Array.isArray(order.refundEvidence) && order.refundEvidence.length) {
        const evidenceLabels = order.refundEvidence.map(item => typeof item === 'string' ? item : item?.name || item?.url || '补件材料');
        timeline.push({
          id: `${order.id}-refund-evidence`,
          title: '补件材料已提交',
          desc: `已上传 ${order.refundEvidence.length} 项材料：${evidenceLabels.join('、')}${order.refundSupplementNote ? `；补充说明：${order.refundSupplementNote}` : ''}`,
          time: order.updatedAt || order.refundRequestedAt || '刚刚',
          tag: '已补件'
        });
      }
      if (refundWorkflow?.status === 'NEW') {
        timeline.push({
          id: `${order.id}-refund-audit-pending`,
          title: '5174 审核中',
          desc: `退款单 ${refundWorkflow.id} 已进入 5174 工作流中心，等待客服与运营审核。`,
          time: refundWorkflow.createdAt || order.updatedAt || '刚刚',
          tag: '待审核'
        });
      }
      if (order.refundServiceTicketId) {
        timeline.push({
          id: `${order.id}-refund-service-ticket`,
          title: '5178 客服售后单已创建',
          desc: `售后客服单 ${order.refundServiceTicketId} 已同步到 5178 客服台，客服与财务可并行跟进。`,
          time: order.refundRequestedAt || order.updatedAt || '刚刚',
          tag: '客服中'
        });
      }
      if (refundWorkflow?.status === 'APPROVED' || order.refundAuditStatus === 'APPROVED') {
        timeline.push({
          id: `${order.id}-refund-audit-approved`,
          title: '退款审核已通过',
          desc: `${refundWorkflow?.reviewer || order.refundReviewedBy || '5174 审核员'} 已通过退款审批，等待财务打款。`,
          time: refundWorkflow?.completedAt || order.refundReviewedAt || order.updatedAt || '刚刚',
          tag: '已通过'
        });
      }
      if (refundWorkflow?.status === 'REJECTED' || order.refundAuditStatus === 'REJECTED') {
        timeline.push({
          id: `${order.id}-refund-audit-rejected`,
          title: '退款审核已驳回',
          desc: order.refundRejectReason || refundWorkflow?.reason || '请前往 5174 查看驳回原因并重新提交。',
          time: refundWorkflow?.completedAt || order.refundReviewedAt || order.updatedAt || '刚刚',
          tag: '已驳回'
        });
      }
      if (order.financeStatus === 'PENDING_PAYOUT') {
        timeline.push({
          id: `${order.id}-finance-queued`,
          title: '财务待打款',
          desc: '退款审批已通过，5174 财务台正在排队处理原路退款。',
          time: order.refundReviewedAt || order.updatedAt || '刚刚',
          tag: '财务中'
        });
      }
      if (order.status === 'REFUNDED' || order.refundedAt) {
        timeline.push({
          id: `${order.id}-refunded`,
          title: '退款已完成',
          desc: `退款金额已原路返回，财务结果已由 ${order.financeOperator || '5174 财务台'} 同步到移动端。`,
          time: order.refundedAt || order.updatedAt || '刚刚',
          tag: '已退款'
        });
      }
      if (order.latestRebook) {
        timeline.push({
          id: `${order.id}-rebook-linked`,
          title: '5176 新预约已回写',
          desc: `玩家站已创建新预约 ${order.latestRebook.bookingId}，并将 ${order.latestRebook.gameName || order.latestRebook.gameId || '新场次'} @ ${order.latestRebook.storeName || order.latestRebook.storeId || '门店'} 写回到原订单。`,
          time: order.latestRebook.linkedAt || order.rebookUpdatedAt || order.updatedAt || '刚刚',
          tag: order.latestRebook.statusText || '已回写'
        });
        if (order.latestRebook.serviceTicketId) {
          timeline.push({
            id: `${order.id}-rebook-service-ticket`,
            title: '重约服务单已创建',
            desc: `新预约服务单 ${order.latestRebook.serviceTicketId} 已同步到 5171/5178，便于继续跟踪到店与改约处理。`,
            time: order.latestRebook.linkedAt || order.rebookUpdatedAt || order.updatedAt || '刚刚',
            tag: '服务联动'
          });
        }
      }
      return respond(res, {
        order,
        timeline,
        crossContext: {
          mobile: '5171 发起订单与售后申请',
          admin: '5174 订单台、退款审批流与财务台处理审核与打款',
          business: '5178 客服台承接退款/售后服务单并跟进处理',
          player: order.latestRebook
            ? `5176 已创建新预约 ${order.latestRebook.bookingId}，并将二次转化结果回写到当前订单`
            : '5176 玩家站承接预约、报名与再次下单'
        }
      });
    }

    if (path.includes('notifications/list')) {
      return respond(res, db.mobileNotifications);
    }
    if (path.includes('notifications/mark-read')) {
      db.mobileNotifications = db.mobileNotifications.map(item => String(item.id) === String(req.body.id) ? { ...item, isRead: true, readAt: new Date().toLocaleString() } : item);
      return respond(res, { status: 'READ' });
    }
    if (path.includes('notifications/read-all')) {
      db.mobileNotifications = db.mobileNotifications.map(item => ({ ...item, isRead: true, readAt: new Date().toLocaleString() }));
      return respond(res, { status: 'ALL_READ' });
    }

    if (path.includes('help/articles')) {
      return respond(res, db.helpCenter);
    }

    if (path.includes('health/summary')) {
      return respond(res, db.healthSummary);
    }

    if (path.includes('training/courses')) {
      return respond(res, db.trainingCourses);
    }

    if (path.includes('feed/posts') && method === 'GET') {
      return respond(res, db.socialPosts);
    }

    if (path.includes('feed/posts') && method === 'POST') {
      const post = {
        id: Date.now(),
        author: req.body.author || 'Player_Mobile_01',
        level: req.body.level || 9,
        avatar: req.body.avatar || 'http://localhost:8080/images/asset_f290576d65b52612bd03f75d45c2a299.svg',
        time: '刚刚',
        content: req.body.content || '新的社交动态',
        images: Array.isArray(req.body.images) ? req.body.images : [],
        tags: Array.isArray(req.body.tags) ? req.body.tags : ['玩家动态'],
        likes: 0,
        comments: 0,
        shares: 0,
        isLiked: false,
        isFollowed: false,
        tab: req.body.tab || 'recommend',
        link: req.body.link || null
      };
      db.socialPosts.unshift(post);
      io.emit('data_sync', { type: 'SOCIAL_POST_CREATED', payload: post });
      io.emit('data_sync', { type: 'NEW_POST_FOR_MODERATION', payload: { id: post.id, content: post.content, user: post.author, status: 'PENDING' } });
      return respond(res, post, 'posted');
    }

    if (path.includes('feed/like')) {
      return respond(res, { status: 'LIKED', targetId: req.body.targetId });
    }

    if (path.includes('feed/follow')) {
      return respond(res, { status: 'FOLLOWED', targetId: req.body.targetId });
    }

    if (path.includes('feed/share/reward')) {
      io.emit('data_sync', { type: 'SOCIAL_POST_SHARED', payload: { targetId: req.body.targetId, user: req.body.user || 'Player_Mobile_01' } });
      return respond(res, { status: 'SHARED', rewardPoints: 20 });
    }

    if (path.includes('feed/comment')) {
      const post = db.socialPosts.find(item => String(item.id) === String(req.body.postId));
      if (post) {
        post.comments += 1;
      }
      const payload = {
        postId: req.body.postId,
        content: req.body.content || '新的评论',
        user: req.body.user || 'Player_Mobile_01'
      };
      io.emit('data_sync', { type: 'SOCIAL_POST_COMMENTED', payload });
      io.emit('data_sync', { type: 'NEW_PLAYER_FEEDBACK', payload: { userId: payload.user, content: payload.content } });
      io.emit('data_sync', { type: 'NEW_POST_FOR_MODERATION', payload: { id: `COMMENT-${Date.now()}`, content: payload.content, user: payload.user, status: 'PENDING' } });
      return respond(res, { status: 'COMMENTED', comments: post?.comments ?? 1 });
    }

    if (path.includes('mall/redeem')) {
      io.emit('data_sync', {
        type: 'GIFT_REDEEMED',
        payload: {
          giftId: req.body.giftId,
          giftName: req.body.giftName,
          points: req.body.points,
          member: req.body.userId || 'Player_Mobile_01'
        }
      });
      return respond(res, { status: 'REDEEMED' });
    }

    // Queue
    if (path.includes('queue/join')) {
      const joinRes = await fetch('http://localhost:8080/api/queue/join', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req.body || {})
      });
      const joinData = await joinRes.json();
      if (!joinRes.ok) {
        return res.status(joinRes.status).json(joinData);
      }
      return respond(res, Object.prototype.hasOwnProperty.call(joinData, 'data') ? joinData.data : joinData, 'success', 200);
    }
    if (path.includes('queue/my')) {
      const params = new URLSearchParams(req.query || {}).toString();
      const myRes = await fetch(`http://localhost:8080/api/queue/my${params ? `?${params}` : ''}`);
      const myData = await myRes.json();
      if (!myRes.ok) {
        return res.status(myRes.status).json(myData);
      }
      return respond(res, Object.prototype.hasOwnProperty.call(myData, 'data') ? myData.data : myData, 'success', 120);
    }
    if (path.includes('queue/call')) {
      const callRes = await fetch('http://localhost:8080/api/queue/call', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req.body || {})
      });
      const callData = await callRes.json();
      if (!callRes.ok) {
        return res.status(callRes.status).json(callData);
      }
      return respond(res, Object.prototype.hasOwnProperty.call(callData, 'data') ? callData.data : callData, 'success', 200);
    }

    if (path.includes('training/book')) {
      io.emit('data_sync', {
        type: 'TRAINING_BOOKED',
        payload: {
          courseId: req.body.courseId,
          courseName: req.body.courseName,
          user: req.body.employeeId || req.body.userId || 'Alex'
        }
      });
      return respond(res, { status: 'BOOKED', courseId: req.body.courseId });
    }

    if (path.includes('rental/list')) {
      const userId = req.query?.userId || req.body?.userId;
      const list = db.rentals
        .filter(item => !userId || String(item.userId) === String(userId))
        .map(buildRentalSnapshot)
        .sort((a, b) => new Date(b.createdAt || b.startAt).getTime() - new Date(a.createdAt || a.startAt).getTime());
      return respond(res, {
        rentals: list,
        inventory: buildRentalInventory(),
        catalog: db.rentalCatalog
      });
    }

    if (path.includes('rental/orders')) {
      return respond(res, {
        orders: db.rentals.map(buildRentalSnapshot).sort((a, b) => new Date(b.createdAt || b.startAt).getTime() - new Date(a.createdAt || a.startAt).getTime()),
        inventory: buildRentalInventory()
      });
    }

    // Play Booking
    if (path.includes('book') || path.includes('reserve') || path.includes('join')) {
      const order = { id: 'B' + Date.now(), target: req.body.target || req.body.eventId, user: 'Alex', status: 'CONFIRMED', date: new Date().toISOString() };
      db.bookings.push(order);
      const eventTicket = req.body.eventId
        ? {
            ticketCode: 'EV' + Date.now().toString().slice(-6),
            eventId: req.body.eventId,
            eventName: req.body.eventName || `活动 ${req.body.eventId}`,
            participants: Number(req.body.participants || 1),
            phone: req.body.phone,
            storeName: req.body.storeName || '新天地店'
          }
        : null
      let eventWorkflowTask = null
      if (eventTicket) {
        const workflowTickets = ensureWorkflowTickets()
        eventWorkflowTask = workflowTickets.find(ticket =>
          ticket.relatedType === 'EVENT_BOOKING' && String(ticket.relatedId) === String(eventTicket.ticketCode)
        ) || null
        if (!eventWorkflowTask) {
          eventWorkflowTask = {
            id: `W-${Date.now()}`,
            type: 'EVENT_BOOKING',
            processName: '活动报名协同',
            title: `活动报名 · ${eventTicket.eventName}`,
            priority: Number(req.body.participants || 1) >= 4 ? 'HIGH' : 'MEDIUM',
            status: 'NEW',
            starter: req.body.user || 'Player_VIP_01',
            nodeName: '待运营确认',
            relatedId: eventTicket.ticketCode,
            relatedType: 'EVENT_BOOKING',
            createdAt: order.date,
            externalWorkspace: 'admin',
            externalPath: `/workflow/list?relatedId=${encodeURIComponent(eventTicket.ticketCode)}&type=EVENT_BOOKING`,
            assignee: '5174 运营排期台'
          }
          workflowTickets.unshift(eventWorkflowTask)
        }
        eventTicket.workflowTaskId = eventWorkflowTask.id
        eventTicket.workflowStatus = eventWorkflowTask.status
        eventTicket.playerPath = `/events?eventId=${encodeURIComponent(eventTicket.eventId)}&eventName=${encodeURIComponent(eventTicket.eventName)}&storeName=${encodeURIComponent(eventTicket.storeName || '')}&ticketCode=${encodeURIComponent(eventTicket.ticketCode)}`
        eventTicket.businessPath = `/event-registration?eventId=${encodeURIComponent(eventTicket.eventId)}&eventName=${encodeURIComponent(eventTicket.eventName)}&ticketCode=${encodeURIComponent(eventTicket.ticketCode)}`
        eventTicket.adminPath = eventWorkflowTask.externalPath
      }
      
      // Global WS events for Admin (5174) & Player (5176)
      io.emit('data_sync', { type: 'NEW_VENUE_BOOKING', payload: { gameId: order.target, userId: order.user, date: order.date, time: 'Now', timestamp: order.date } });
      if (eventTicket) {
        io.emit('data_sync', {
          type: 'EVENT_BOOKED',
          payload: {
            ...eventTicket,
            user: req.body.user || 'Player_VIP_01',
            workflowTaskId: eventWorkflowTask?.id || '',
            timestamp: order.date
          }
        });
      }
      io.emit('notification', { title: 'Reservation Confirmed', content: `Your booking was successful.` });
      return respond(res, { orderId: order.id, status: 'CONFIRMED', eventTicket, workflowTaskId: eventWorkflowTask?.id || '' });
    }

    // Rental
    if (path.includes('rental/lease')) {
      const device = getRentalDevice(req.body.deviceId) || db.rentalCatalog[0];
      const durationHours = Number(req.body.duration || 1);
      const startAt = new Date();
      const expiresAt = new Date(startAt.getTime() + durationHours * 60 * 60 * 1000);
      const rental = {
        id: `RO-${Date.now()}`,
        orderId: `RO-${Date.now()}`,
        deviceId: device.id,
        deviceName: device.name,
        deviceSn: `${device.id}-${Math.floor(Math.random() * 9000 + 1000)}`,
        userId: req.body.userId || 'Player_Mobile_01',
        user: req.body.userId || 'Player_Mobile_01',
        storeName: req.body.storeName || '新天地店',
        durationHours,
        rentFee: Number(device.price || 0) * durationHours,
        deposit: Number(device.deposit || 0),
        status: 'PENDING_PICKUP',
        pickupCode: `RN-${Math.floor(Math.random() * 9000 + 1000)}`,
        createdAt: startAt.toISOString(),
        startAt: startAt.toISOString(),
        expiresAt: expiresAt.toISOString(),
        sourceApp: '5171 移动端'
      };
      db.rentals.unshift(rental);
      const payload = buildRentalSnapshot(rental);
      io.emit('data_sync', { type: 'NEW_RENTAL', payload });
      io.emit('data_sync', { type: 'RENTAL_LEASED', payload });
      io.emit('notification', { title: 'Rental Success', content: `Device ${device.name} leased.` });
      return respond(res, { orderId: rental.id, status: 'CONFIRMED', rental: payload });
    }
    if (path.includes('rental/return')) {
      const { deviceId, orderId } = req.body;
      const rental = db.rentals.find(r =>
        (orderId && String(r.id) === String(orderId)) ||
        (deviceId && String(r.deviceId) === String(deviceId) && ['ACTIVE', 'PENDING_PICKUP', 'OVERDUE'].includes(r.status))
      );
      if (rental) {
        rental.status = 'RETURNED';
        rental.returnedAt = new Date().toISOString();
      }
      const payload = rental ? buildRentalSnapshot(rental) : { deviceId, orderId, status: 'RETURNED' };
      io.emit('data_sync', { type: 'RENTAL_RETURNED', payload });
      io.emit('notification', { title: 'Return Success', content: `Device ${rental?.deviceName || deviceId || ''} returned.` });
      return respond(res, { status: 'RETURNED', rental: payload });
    }
    if (path.includes('rental/pickup')) {
      const rental = db.rentals.find(item => String(item.id) === String(req.body.orderId) || String(item.pickupCode) === String(req.body.pickupCode));
      if (!rental) {
        return respond(res, { status: 'NOT_FOUND' }, 'success', 404);
      }
      rental.status = 'ACTIVE';
      rental.pickedUpAt = new Date().toISOString();
      const payload = buildRentalSnapshot(rental);
      io.emit('data_sync', { type: 'RENTAL_PICKED_UP', payload });
      return respond(res, { status: 'ACTIVE', rental: payload });
    }
    if (path.includes('rental/overdue')) {
      const rental = db.rentals.find(item => String(item.id) === String(req.body.orderId) || String(item.deviceId) === String(req.body.deviceId));
      if (!rental) {
        return respond(res, { status: 'NOT_FOUND' }, 'success', 404);
      }
      rental.status = 'OVERDUE';
      rental.updatedAt = new Date().toISOString();
      const payload = buildRentalSnapshot(rental);
      io.emit('data_sync', { type: 'RENTAL_OVERDUE', payload });
      return respond(res, { status: 'OVERDUE', rental: payload });
    }

    if (path.includes('orders/refund/settle')) {
      const targetOrder = findOrderById(req.body.orderId);
      if (targetOrder) {
        targetOrder.status = 'REFUNDED';
        targetOrder.financeStatus = 'PAID';
        targetOrder.refundedAt = new Date().toISOString();
        targetOrder.updatedAt = targetOrder.refundedAt;
        targetOrder.financeOperator = req.body.operator || 'Finance_01';
      }
      const payload = {
        orderId: req.body.orderId,
        status: 'REFUNDED',
        financeStatus: 'PAID',
        operator: req.body.operator || 'Finance_01',
        refundedAt: targetOrder?.refundedAt || new Date().toISOString()
      };
      const refundServiceTicketPayload = syncRefundServiceTicket(req.body.orderId, 'SETTLED', {
        operator: payload.operator,
        refundedAt: payload.refundedAt
      });
      io.emit('data_sync', { type: 'REFUND_SETTLED', payload });
      io.emit('state_update', { type: 'REFUND_SETTLED', payload });
      if (refundServiceTicketPayload) {
        io.emit('data_sync', { type: 'TICKET_RESOLVED', payload: refundServiceTicketPayload });
      }
      return respond(res, payload);
    }

    if (path.includes('orders/refund') && !path.includes('orders/refund/settle')) {
      const targetOrder = findOrderById(req.body.orderId);
      if (targetOrder) {
        const evidence = Array.isArray(req.body.evidence) ? req.body.evidence : Array.isArray(targetOrder.refundEvidence) ? targetOrder.refundEvidence : [];
        targetOrder.status = 'REFUNDING';
        targetOrder.refundRequestedAt = new Date().toISOString();
        targetOrder.updatedAt = targetOrder.refundRequestedAt;
        targetOrder.refundReason = req.body.reason || '移动端发起售后申请';
        targetOrder.refundRequestId = req.body.requestId || `RF-${Date.now()}`;
        targetOrder.refundAuditStatus = 'PENDING';
        targetOrder.financeStatus = 'QUEUED';
        targetOrder.refundRejectReason = '';
        targetOrder.refundReviewedAt = '';
        targetOrder.refundReviewedBy = '';
        targetOrder.refundEvidence = evidence;
        targetOrder.refundSupplementNote = req.body.supplementNote || targetOrder.refundSupplementNote || '';
      }
      const workflowTicket = targetOrder ? createRefundWorkflowTicket(targetOrder, req.body) : null;
      if (targetOrder && workflowTicket) {
        targetOrder.refundWorkflowTaskId = workflowTicket.id;
      }
      const refundServiceTicketResult = targetOrder ? createRefundServiceTicket(targetOrder, req.body, workflowTicket) : null;
      if (targetOrder && refundServiceTicketResult?.ticket) {
        targetOrder.refundServiceTicketId = refundServiceTicketResult.ticket.id;
      }
      if (workflowTicket) {
        io.emit('data_sync', { type: 'WORKFLOW_TASK_CREATED', payload: workflowTicket });
      }
      if (refundServiceTicketResult?.ticket) {
        if (refundServiceTicketResult.existed) {
          io.emit('data_sync', {
            type: 'TICKET_STATUS_UPDATED',
            payload: {
              ticketId: refundServiceTicketResult.ticket.id,
              status: refundServiceTicketResult.ticket.status,
              statusText: refundServiceTicketResult.ticket.statusText,
              assignee: refundServiceTicketResult.ticket.assignee,
              updatedAt: refundServiceTicketResult.ticket.updatedAt,
              resolutionNote: refundServiceTicketResult.ticket.resolutionNote,
              workflowTaskId: refundServiceTicketResult.ticket.workflowTaskId,
              workflowStatus: refundServiceTicketResult.ticket.workflowStatus,
              workflowNodeName: refundServiceTicketResult.ticket.workflowNodeName,
              relatedId: refundServiceTicketResult.ticket.relatedOrderId || refundServiceTicketResult.ticket.relatedId
            }
          });
        } else {
          io.emit('data_sync', { type: 'WORK_ORDER_CREATED', payload: refundServiceTicketResult.ticket });
        }
      }
      io.emit('data_sync', { type: 'REFUND_REQUESTED', payload: { ...req.body, status: 'REFUNDING', workflowTaskId: workflowTicket?.id || '' } });
      io.emit('state_update', { type: 'REFUND_REQUESTED', payload: { orderId: req.body.orderId, workflowTaskId: workflowTicket?.id || '', refundRequestId: targetOrder?.refundRequestId || req.body.requestId || '', refundServiceTicketId: targetOrder?.refundServiceTicketId || refundServiceTicketResult?.ticket?.id || '' } });
      return respond(res, { status: 'REFUNDING', orderId: req.body.orderId, workflowTaskId: workflowTicket?.id || '', refundRequestId: targetOrder?.refundRequestId || req.body.requestId || '', refundServiceTicketId: targetOrder?.refundServiceTicketId || refundServiceTicketResult?.ticket?.id || '' });
    }

    if (path.includes('mall/cart/list')) {
      const userId = getCartUserId(req);
      const items = getCartItemsByUserId(userId).map(serializeCartItem);
      return respond(res, {
        items,
        cartCount: items.reduce((sum, item) => sum + Number(item.quantity || 0), 0)
      });
    }

    if (path.includes('mall/cart/add')) {
      const userId = getCartUserId(req);
      const product = getProductById(req.body.productId);
      if (!product) return respond(res, null, 'not_found', 404);
      const nextQuantity = Number(req.body.quantity || 1);
      const availability = resolveMallAvailability(product, nextQuantity);
      if (!availability.canCheckout) {
        return respond(res, { status: 'INVALID', reason: availability.invalidReason, productId: product.id }, availability.invalidReason);
      }
      const sku = product.skuList?.find(item => String(item.id) === String(req.body.skuId || product.defaultSkuId)) || product.skuList?.[0] || { id: 'sku-default', name: '默认规格' };
      const existing = db.cartItems.find(item => String(item.userId) === String(userId) && String(item.productId) === String(product.id) && String(item.skuId || '') === String(sku.id || ''));
      if (existing) {
        const mergedQuantity = existing.quantity + nextQuantity;
        const mergedAvailability = resolveMallAvailability(product, mergedQuantity);
        if (!mergedAvailability.canCheckout) {
          return respond(res, { status: 'INVALID', reason: mergedAvailability.invalidReason, productId: product.id }, mergedAvailability.invalidReason);
        }
        existing.quantity = mergedQuantity;
        existing.checked = true;
      } else {
        db.cartItems.unshift({
          cartItemId: `CART-${Date.now()}`,
          productId: product.id,
          skuId: sku.id,
          title: product.name,
          sku: sku.name,
          price: Number(product.cashPrice || product.pointsPrice || 0),
          quantity: Number(req.body.quantity || 1),
          checked: true,
          image: product.image || '',
          stock: Number(product.stock || 99),
          sourceType: 'MALL',
          sourcePage: 'pages/mall/detail/index',
          userId
        });
      }
      const items = getCartItemsByUserId(userId).map(serializeCartItem);
      return respond(res, {
        cartItem: serializeCartItem(existing || db.cartItems[0]),
        cartCount: items.reduce((sum, item) => sum + Number(item.quantity || 0), 0)
      });
    }

    if (path.includes('mall/cart/update')) {
      const userId = getCartUserId(req);
      const target = db.cartItems.find(item => String(item.userId) === String(userId) && String(item.cartItemId) === String(req.body.cartItemId));
      if (target) {
        const product = getProductById(target.productId);
        if (typeof req.body.quantity !== 'undefined') {
          const nextQuantity = Math.max(1, Number(req.body.quantity || 1));
          const availability = resolveMallAvailability(product, nextQuantity);
          if (!availability.canCheckout) {
            return respond(res, { item: serializeCartItem({ ...target, quantity: nextQuantity }), status: 'INVALID', reason: availability.invalidReason }, availability.invalidReason);
          }
          target.quantity = nextQuantity;
        }
        if (typeof req.body.checked !== 'undefined') {
          const availability = resolveMallAvailability(product, target.quantity);
          target.checked = availability.canCheckout ? Boolean(req.body.checked) : false;
        }
      }
      return respond(res, { item: target ? serializeCartItem(target) : null });
    }

    if (path.includes('mall/cart/check-all')) {
      const userId = getCartUserId(req);
      const checked = Boolean(req.body.checked);
      db.cartItems = db.cartItems.map(item => {
        if (String(item.userId) !== String(userId)) return item;
        const availability = resolveMallAvailability(getProductById(item.productId), item.quantity);
        return { ...item, checked: checked ? availability.canCheckout : false };
      });
      return respond(res, { status: 'UPDATED', checked });
    }

    if (path.includes('mall/cart/remove')) {
      const userId = getCartUserId(req);
      const cartItemIds = Array.isArray(req.body.cartItemIds) ? req.body.cartItemIds.map(String) : [];
      const removeChecked = Boolean(req.body.removeChecked);
      db.cartItems = db.cartItems.filter(item => {
        if (String(item.userId) !== String(userId)) return true;
        if (removeChecked) return item.checked !== true;
        if (cartItemIds.length) return !cartItemIds.includes(String(item.cartItemId));
        return true;
      });
      return respond(res, { status: 'REMOVED' });
    }

    if (path.includes('cart') && !path.includes('mall/cart')) {
      const userId = getCartUserId(req);
      const items = getCartItemsByUserId(userId);
      return respond(res, { cartCount: items.reduce((sum, item) => sum + Number(item.quantity || 0), 0) });
    }

    if (path.includes('deposit/refund')) {
      const amount = Number(req.body.amount || 199);
      db.userProfile.balance += amount;
      io.emit('data_sync', { type: 'REFUND_PROCESSED', payload: { amount } });
      io.emit('data_sync', { type: 'AUTO_REFUND_PROCESSED', payload: { amount } });
      return respond(res, { status: 'REFUNDED', balance: db.userProfile.balance, amount });
    }

    // Payment / Checkout / Deposit
    if (path.includes('checkout') || path.includes('pay') || path.includes('deposit')) {
      const amount = req.body.amount || 10;
      const createdAt = new Date().toISOString();
      const items = Array.isArray(req.body.items) ? req.body.items : [];
      const simulateStatus = req.body.simulateStatus === 'FAILED' || req.body.simulateStatus === 'CANCELLED'
        ? req.body.simulateStatus
        : 'COMPLETED';
      if (simulateStatus === 'COMPLETED') {
        db.userProfile.balance -= amount;
      }
      
      const logisticsBaseId = Date.now();
      const mallLogistics = (req.body.sourceType || 'MALL') === 'MALL'
        ? (() => {
            const packageItems = items.length > 1
              ? [
                  {
                    parcelNo: `PKG-${logisticsBaseId}-A`,
                    company: '顺丰速运',
                    trackingNo: `SF${logisticsBaseId}`,
                    status: '已发货',
                    items: [items[0]],
                    tracks: [
                      {
                        id: `lg-${logisticsBaseId}-1`,
                        title: '商家已发货',
                        desc: '首个包裹已由 5174 仓配中心交接顺丰。',
                        time: createdAt,
                        tag: '已发货'
                      }
                    ]
                  },
                  {
                    parcelNo: `PKG-${logisticsBaseId}-B`,
                    company: '京东物流',
                    trackingNo: `JD${logisticsBaseId}`,
                    status: '待揽收',
                    items: items.slice(1),
                    tracks: [
                      {
                        id: `lg-${logisticsBaseId}-2`,
                        title: '分包待揽收',
                        desc: '第二个包裹已拆分完成，等待物流揽收。',
                        time: createdAt,
                        tag: '待揽收'
                      }
                    ]
                  }
                ]
              : [
                  {
                    parcelNo: `PKG-${logisticsBaseId}-A`,
                    company: '顺丰速运',
                    trackingNo: `SF${logisticsBaseId}`,
                    status: '运输中',
                    items,
                    tracks: [
                      {
                        id: `lg-${logisticsBaseId}-1`,
                        title: '商家已发货',
                        desc: '5174 仓配中心已完成出库，包裹已交接顺丰。',
                        time: createdAt,
                        tag: '已发货'
                      },
                      {
                        id: `lg-${logisticsBaseId}-2`,
                        title: '运输中',
                        desc: '包裹已进入干线运输，最新物流状态已同步到订单中心。',
                        time: createdAt,
                        tag: '运输中'
                      }
                    ]
                  }
                ];
            return {
              company: packageItems[0]?.company || '顺丰速运',
              trackingNo: packageItems[0]?.trackingNo || `SF${logisticsBaseId}`,
              status: packageItems.length > 1 ? '部分发货' : '运输中',
              packageCount: packageItems.length,
              receiverName: req.body.contactName || '当前账号',
              receiverPhone: '138****2048',
              address: '上海市浦东新区张江科创路 88 号 2 栋 1203',
              parcels: packageItems,
              tracks: packageItems.flatMap(parcel => parcel.tracks)
            };
          })()
        : undefined;
      const order = normalizeOrder({
        id: 'O' + Date.now(),
        title: req.body.title || 'Cyber Item',
        amount,
        user: 'Alex',
        status: simulateStatus,
        date: createdAt,
        createdAt,
        updatedAt: createdAt,
        completedAt: simulateStatus === 'COMPLETED' ? createdAt : '',
        failedAt: simulateStatus === 'FAILED' ? createdAt : '',
        cancelledAt: simulateStatus === 'CANCELLED' ? createdAt : '',
        sourceApp: req.body.sourceApp || '5171 移动端',
        sourceType: req.body.sourceType || 'MALL',
        sourcePage: req.body.sourcePage || 'pages/mall/cart/index',
        paymentChannel: req.body.channel || '钱包支付',
        ticketCount: items.reduce((sum, item) => sum + Number(item.quantity || 0), 0) || 1,
        items,
        logistics: mallLogistics
      });
      if (simulateStatus === 'COMPLETED' && Array.isArray(req.body.cartItemIds) && req.body.cartItemIds.length) {
        const cartIdSet = new Set(req.body.cartItemIds.map(String));
        db.cartItems = db.cartItems.filter(item => !cartIdSet.has(String(item.cartItemId)));
      }
      db.orders.push(order);
      
      io.emit('data_sync', { type: 'NEW_ORDER', payload: order });
      io.emit('data_sync', {
        type: 'NEW_MALL_ORDER',
        payload: {
          id: order.id,
          productName: order.title,
          userId: order.user,
          price: amount,
          status: order.status,
          createdAt: order.date
        }
      });

      if (simulateStatus === 'COMPLETED') {
        io.emit('state_update', { type: 'PAYMENT_SUCCESS', payload: { amount: amount, source: order.sourceApp, orderId: order.id, sourceType: order.sourceType } });
        io.emit('notification', { title: 'Payment Successful', content: `Deducted ¥${amount} from your wallet.` });
      } else {
        io.emit('state_update', { type: 'PAYMENT_FAILED', payload: { amount: amount, source: order.sourceApp, orderId: order.id, sourceType: order.sourceType, status: simulateStatus } });
        io.emit('notification', { title: 'Payment Incomplete', content: `${order.id} 支付状态为 ${simulateStatus}` });
      }
      return respond(res, { transactionId: 'TX' + Date.now(), orderId: order.id, status: simulateStatus, balance: db.userProfile.balance, order });
    }
    if (path.includes('draw') || path.includes('gacha')) {
      const isSSR = Math.random() > 0.8;
      const item = {
        rarity: isSSR ? 'SSR' : 'SR',
        name: isSSR ? 'Neon Skullpanda - Phantom' : 'Cyber Trooper - Delta'
      };
      const payload = { item: item.name, rarity: item.rarity, user: req.body.userId || 'Alex', boxId: req.body.boxId || 'default-box' };
      io.emit('data_sync', { type: 'GACHA_PULL', payload });
      io.emit('state_update', { type: 'GACHA_PULL', payload });
      return respond(res, { item, result: item });
    }

    // IOT & Lockers
    if (path.includes('scan') || path.includes('unlock') || path.includes('open')) {
      const targetId = req.body.deviceId || req.body.lockerId || req.body.qrcode || 'GATE-01';
      io.emit('data_sync', { type: 'DEVICE_UNLOCKED', payload: { deviceId: targetId, user: 'Alex' } });
      io.emit('data_sync', { type: 'GATE_OPENED', payload: { deviceId: targetId, user: 'Alex', time: new Date().toISOString() } });
      io.emit('data_sync', { type: 'PLAYER_CHECKIN', payload: { player: 'Alex', venue: targetId } });
      
      io.emit('notification', { title: 'Device Unlocked', content: `Device ${targetId || ''} is now active.` });
      return respond(res, { status: 'UNLOCKED', deviceId: targetId });
    }

    if ((path.includes('submit') && !path.includes('ticket/submit')) || path.includes('feedback') || path.includes('post') || path.includes('comment')) {
      const post = { id: 'P' + Date.now(), content: req.body.content || req.body.desc || 'User Feedback', user: 'Alex', status: 'PENDING' };
      io.emit('data_sync', { type: 'NEW_FEEDBACK', payload: post });
      io.emit('data_sync', { type: 'NEW_POST_FOR_MODERATION', payload: post });
      io.emit('data_sync', { type: 'NEW_PLAYER_FEEDBACK', payload: { userId: 'Alex', content: post.content } });
      
      if (post.content.includes('Lost & Found')) {
         io.emit('data_sync', { type: 'ITEM_FOUND_AI_MATCH', payload: { itemName: 'Apple AirPods Pro', location: 'VR 赛博战场 A 区' } });
      }
      
      return respond(res, { status: 'SUBMITTED' });
    }

    if ((path.includes('task') && !path.includes('koc/tasks') && !path.includes('workflow/tickets')) || path.includes('mission')) {
      db.userProfile.pts += 50;
      io.emit('data_sync', { type: 'TASK_COMPLETED', payload: { user: 'Alex', pts: 50 } });
      
      if (db.userProfile.pts > 1300) {
        io.emit('data_sync', { type: 'MEMBERSHIP_UPGRADED', payload: { userId: 'Alex', newTier: 'V5 (Diamond)' } });
      }
      
      return respond(res, { ptsEarned: 50, currentPts: db.userProfile.pts });
    }

    if (path.includes('health/milestone')) {
      const calories = Number(req.body.calories || 0);
      db.healthSummary.calories = Math.max(db.healthSummary.calories, calories);
      db.healthSummary.score = Math.min(100, db.healthSummary.score + 2);
      db.healthSummary.recentActivities.unshift({
        id: Date.now(),
        name: 'AI 同步运动记录',
        icon: '⚡',
        date: '刚刚',
        duration: '自动同步',
        calories
      });
      db.healthSummary.recentActivities = db.healthSummary.recentActivities.slice(0, 5);
      const payload = {
        calories,
        user: req.body.userId || 'Alex',
        userId: req.body.userId || 'Alex',
        score: db.healthSummary.score,
        summary: db.healthSummary
      };
      io.emit('data_sync', { type: 'HEALTH_MILESTONE', payload });
      io.emit('data_sync', { type: 'CALORIE_MILESTONE_REACHED', payload });
      return respond(res, { status: 'SYNCED', summary: db.healthSummary });
    }
    
    if (path.includes('mall/product/')) {
      const productId = path.split('/mall/product/')[1];
      const product = getProductById(productId);
      if (!product) return respond(res, null, 'not_found', 404);
      return respond(res, product);
    }

    if (path.includes('mall/products')) {
      return respond(res, db.mallProducts);
    }

    if (path.includes('mall/categories')) {
      return respond(res, [
        { id: 'blindbox', name: '盲盒' },
        { id: 'figure', name: '手办周边' }
      ]);
    }

    if (path.includes('assets') || path.includes('nfts') || path.includes('inventory') || path.includes('products') || path.includes('categories') || path.includes('highlights') || path.includes('events') || path.includes('teams') || path.includes('resources') || (path.includes('tickets') && !path.includes('workflow/tickets')) || path.includes('dashboard') || path.includes('stats')) {
      return respond(res, [{ id: 1, name: 'Cyber Item A', price: 99, status: 1 }, { id: 2, name: 'Neon Item B', price: 199, status: 1 }]);
    }
    
    if (path.includes('leaderboard') || path.includes('rank')) {
      return respond(res, [{ rank: 1, name: 'Alex S.', score: 9999 }, { rank: 2, name: 'Bob M.', score: 8888 }]);
    }

    // New Batch 4 endpoints
    if (path.includes('membership/upgrade')) {
      return respond(res, { status: 'UPGRADED' });
    }
    if (path.includes('hr/leave')) {
      return respond(res, { status: 'REQUESTED' });
    }
    if (path.includes('health/milestone')) {
      return respond(res, { status: 'ACHIEVED' });
    }
    if (path.includes('koc/overview')) {
      const overviewRes = await fetch(`http://localhost:8080/api/koc/overview?viewerId=${encodeURIComponent(req.query.viewerId || req.body?.viewerId || 'CyberPlayer_01')}`);
      const overviewData = await overviewRes.json();
      return respond(res, overviewData.data || overviewData, 'success', 200);
    }
    if (path.includes('koc/tasks')) {
      if (method === 'GET') {
        const tasksRes = await fetch(`http://localhost:8080/api/koc/tasks?viewerId=${encodeURIComponent(req.query.viewerId || req.body?.viewerId || 'CyberPlayer_01')}`);
        const tasksData = await tasksRes.json();
        return respond(res, tasksData.data || tasksData, 'success', 200);
      }
      const createTaskRes = await fetch('http://localhost:8080/api/koc/tasks', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req.body || {})
      });
      const createTaskData = await createTaskRes.json();
      return respond(res, createTaskData.data || createTaskData, 'success', 200);
    }
    if (path.includes('koc/share')) {
      const shareRes = await fetch('http://localhost:8080/api/koc/share', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req.body || {})
      });
      const shareData = await shareRes.json();
      return respond(res, shareData.data || shareData, 'success', 200);
    }
    if (path.includes('koc/conversion')) {
      const conversionRes = await fetch('http://localhost:8080/api/koc/conversion', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req.body || {})
      });
      const conversionData = await conversionRes.json();
      return respond(res, conversionData.data || conversionData, 'success', 200);
    }
    if (path.includes('station/unlock') || path.includes('mobile/scan')) {
      return respond(res, { status: 'UNLOCKED' });
    }
    if (path.includes('iot/locker')) {
      return respond(res, { status: 'SUCCESS' });
    }
    if (path.includes('task/complete')) {
      return respond(res, { status: 'COMPLETED' });
    }
    if (path.includes('ai/generate-copy')) {
      return respond(res, { status: 'GENERATED' });
    }
    if (path.includes('crm/leads')) {
      return respond(res, { status: 'REGISTERED' });
    }
    if (path.includes('metaverse/chat')) {
      return respond(res, { status: 'SENT' });
    }
    if (path.includes('sop/emergency')) {
      return respond(res, { status: 'REPORTED' });
    }
    if (path.includes('compliance/stream-consent/grant')) {
      return respond(res, { status: 'GRANTED' });
    }

    // New Batch 5 endpoints
    if (path.includes('deposit/refund')) {
      return respond(res, { status: 'REFUNDED' });
    }
    if (path.includes('rental/return')) {
      return respond(res, { status: 'RETURNED' });
    }
    if (path.includes('rental/lease')) {
      return respond(res, { status: 'LEASED' });
    }
    if (path.includes('play/book')) {
      return respond(res, { status: 'BOOKED' });
    }
    if (path.includes('iot/gate/open')) {
      return respond(res, { status: 'OPENED' });
    }
    if (path.includes('iot/vip-env')) {
      return respond(res, { status: 'SYNCED' });
    }

    // Ticket (Support)
    if (path.includes('ticket/submit')) {
      const ticketId = req.body.ticketId || `TK-${Date.now().toString().slice(-6)}`
      const typeMap = {
        '设备报修': 'REPAIR',
        '环境客诉': 'COMPLAINT',
        '退款/售后': 'CUSTOMER_SERVICE',
        '其它帮助': 'SERVICE'
      }
      const assignee = req.body.type === '设备报修' ? '张工' : req.body.type === '环境客诉' ? 'Alex (Manager)' : '客服中台'
      const priority = req.body.type === '设备报修' || req.body.type === '环境客诉' ? 'HIGH' : 'MEDIUM'
      const attachments = Array.isArray(req.body.attachments) ? req.body.attachments.map(item => ({
        name: item?.name || '工单图片',
        url: item?.url || item?.thumbUrl || '',
        thumbUrl: item?.thumbUrl || item?.url || '',
        type: item?.type || 'image',
        sourceText: item?.sourceText || '图片附件',
        sizeText: item?.sizeText || '大小未知'
      })).filter(item => item.url || item.thumbUrl) : []
      const payload = {
        ticketId,
        title: req.body.title,
        desc: req.body.desc,
        type: typeMap[req.body.type] || 'SERVICE',
        displayType: req.body.type,
        user: req.body.user || 'Player_Mobile_01',
        assignee,
        priority,
        attachments,
        status: 'PENDING',
        statusText: '待处理',
        createdAt: new Date().toLocaleString()
      }
      const workflowTask = createTicketWorkflowTask(payload)
      payload.workflowTaskId = workflowTask.id
      payload.workflowStatus = workflowTask.status
      payload.workflowNodeName = workflowTask.nodeName
      db.tickets.unshift(normalizeTicket({
        id: ticketId,
        type: req.body.type,
        title: req.body.title,
        desc: req.body.desc,
        assignee,
        priority,
        status: 'PENDING',
        statusText: '待处理',
        createdAt: payload.createdAt,
        updatedAt: payload.createdAt,
        user: payload.user,
        attachments,
        workflowTaskId: workflowTask.id,
        workflowStatus: workflowTask.status,
        workflowNodeName: workflowTask.nodeName
      }))
      appendTicketTimeline(ticketId, {
        stage: 'CREATED',
        title: '工单已创建',
        operator: payload.user,
        detail: payload.desc || payload.title,
        meta: [
          { label: '提交人', value: payload.user },
          { label: '工单类型', value: payload.displayType || '其它帮助' }
        ]
      })
      appendTicketTimeline(ticketId, {
        stage: 'ASSIGNED',
        title: '工单已自动派单',
        operator: '系统',
        detail: `已分配给 ${assignee} · 优先级 ${priority === 'HIGH' ? '紧急' : '普通'}`,
        meta: [
          { label: '派单对象', value: assignee },
          { label: '优先级', value: priority === 'HIGH' ? '紧急' : '普通' },
          { label: '流程单号', value: workflowTask.id }
        ]
      })
      if (attachments.length) {
        appendTicketTimeline(ticketId, {
          stage: 'CREATED',
          title: '已上传图片附件',
          operator: payload.user,
          detail: `已提交 ${attachments.length} 张图片，服务台可直接查看现场凭证。`,
          meta: attachments.slice(0, 3).map(item => ({ label: item.sourceText || '图片附件', value: item.name || '工单图片' }))
        })
      }
      io.emit('data_sync', { type: 'WORKFLOW_TASK_CREATED', payload: workflowTask })
      io.emit('data_sync', { type: 'WORK_ORDER_CREATED', payload })
      io.emit('notification', { title: 'New Support Ticket', content: `${payload.user} 提交了工单 ${ticketId}：${payload.title}` })
      return respond(res, { status: 'SUBMITTED', ticket: payload });
    }

    if (path.includes('ticket/close')) {
      const payload = {
        ticketId: req.body.ticketId,
        title: req.body.title,
        status: 'CLOSED',
        statusText: '已关闭',
        assignee: req.body.assignee || '客服中台',
        updatedAt: new Date().toLocaleString(),
        closedAt: new Date().toLocaleString(),
        closeNote: req.body.closeNote || '用户已确认关闭工单'
      }
      const targetTicket = db.tickets.find(item => String(item.id) === String(payload.ticketId))
      if (targetTicket) {
        targetTicket.status = 'CLOSED'
        targetTicket.statusText = '已关闭'
        targetTicket.updatedAt = payload.updatedAt
        targetTicket.resolutionNote = payload.closeNote
        payload.attachments = targetTicket.attachments || []
      }
      appendTicketTimeline(payload.ticketId, {
        stage: 'CLOSED',
        title: '工单已关闭',
        operator: payload.assignee,
        detail: payload.closeNote,
        meta: [
          { label: '确认人', value: payload.assignee },
          { label: '归档状态', value: '已关闭' }
        ]
      })
      io.emit('data_sync', { type: 'TICKET_STATUS_UPDATED', payload })
      io.emit('data_sync', { type: 'TICKET_CLOSED', payload })
      io.emit('notification', { title: 'Ticket Closed', content: `${payload.ticketId} 已关闭：${payload.closeNote}` })
      return respond(res, { status: 'CLOSED', ticket: payload });
    }

    if (path.includes('ticket/reply')) {
      const ticketId = req.body.ticketId
      const reply = String(req.body.reply || '').trim()
      if (!ticketId || !reply) {
        return res.status(400).json({ success: false, message: 'ticketId and reply are required' })
      }
      const operator = req.body.operator || '5178 服务台'
      const targetTicket = db.tickets.find(item => String(item.id) === String(ticketId))
      const updatedAt = new Date().toLocaleString()
      if (targetTicket) {
        targetTicket.updatedAt = updatedAt
        if (!['RESOLVED', 'CLOSED'].includes(targetTicket.status)) {
          targetTicket.status = 'PROCESSING'
          targetTicket.statusText = '处理中'
        }
        targetTicket.latestReply = reply
        targetTicket.latestReplyAt = updatedAt
        targetTicket.latestReplyOperator = operator
      }
      const entry = {
        stage: 'REPLIED',
        title: '客服回复',
        operator,
        detail: reply,
        meta: [
          { label: '回复人', value: operator },
          { label: '同步方式', value: '客服记录' }
        ]
      }
      appendTicketTimeline(ticketId, entry)
      const payload = {
        ticketId,
        title: targetTicket?.title || `工单 ${ticketId}`,
        status: targetTicket?.status || 'PROCESSING',
        statusText: targetTicket?.statusText || '处理中',
        assignee: targetTicket?.assignee || operator,
        updatedAt,
        workflowTaskId: targetTicket?.workflowTaskId || '',
        workflowStatus: targetTicket?.workflowStatus || '',
        workflowNodeName: targetTicket?.workflowNodeName || '',
        attachments: targetTicket?.attachments || [],
        latestReply: targetTicket?.latestReply || reply,
        latestReplyAt: targetTicket?.latestReplyAt || updatedAt,
        latestReplyOperator: targetTicket?.latestReplyOperator || operator,
        reply,
        replyEntry: {
          id: `${ticketId}-reply-${Date.now()}`,
          timestamp: new Date().toISOString(),
          ...entry
        }
      }
      io.emit('data_sync', { type: 'TICKET_REPLY_ADDED', payload })
      io.emit('notification', { title: 'Ticket Reply', content: `${operator} 已回复工单 ${ticketId}` })
      return respond(res, { status: 'REPLIED', ticket: payload });
    }

    if (path.includes('ticket/timeline')) {
      const ticketId = req.query.ticketId || req.body.ticketId
      return respond(res, { ticketId, timeline: db.ticket_timelines[ticketId] || [] })
    }

    if (path.includes('ticket/list')) {
      const user = req.query.user || req.body?.user
      const list = db.tickets.filter(ticket => !user || String(ticket.user) === String(user))
      return respond(res, list);
    }

    if (path.includes('ticket/detail')) {
      const ticketId = req.query.ticketId || req.body?.ticketId
      const ticket = db.tickets.find(item => String(item.id) === String(ticketId)) || null
      return respond(res, {
        ticket,
        timeline: ticketId ? db.ticket_timelines[ticketId] || [] : []
      })
    }

    // Shift Swap (Employee)
    if (path.includes('schedule/swap')) {
      io.emit('data_sync', { type: 'SHIFT_CHANGE_REQUEST', payload: req.body });
      io.emit('notification', { title: 'Shift Request', content: `Your shift request was sent to HR.` });
      return respond(res, { status: 'PENDING_APPROVAL' });
    }

    // FnB Orders
    if (path.includes('fnb/orders')) {
      const order = req.body;
      const orderId = 'FNB' + Date.now();
      io.emit('data_sync', { type: 'FNB_ORDER_PLACED', payload: { ...order, orderId } });
      io.emit('state_update', { type: 'PAYMENT_SUCCESS', payload: { amount: order.total, source: '餐饮点单 (5171)' } });
      io.emit('notification', { title: 'Food Ordered', content: `Your order was sent to the kitchen.` });
      return respond(res, { status: 'ORDERED', orderId });
    }

    // Invoice
    if (path.includes('invoice/apply')) {
      return respond(res, { status: 'APPLIED' });
    }
    if (path.includes('reimbursement/apply')) {
      const reimbursementRes = await fetch('http://localhost:8080/api/finance/reimbursements', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req.body || {})
      });
      const reimbursementData = await reimbursementRes.json();
      return respond(res, reimbursementData.data || reimbursementData, 'success', 200);
    }

    // Inspection
    if (path.includes('inspection/submit')) {
      return respond(res, { status: 'SUBMITTED' });
    }

    // Tournament
    if (path.includes('tournament/register') || path.includes('tournament/join')) {
      const payload = {
        tournamentId: req.body.tournamentId,
        tournamentName: req.body.tournamentName || req.body.title || '赛博挑战赛',
        userId: req.body.userId || 'Player_Mobile_01',
        user: req.body.userId || 'Player_Mobile_01'
      };
      io.emit('data_sync', { type: 'TOURNAMENT_REGISTERED', payload });
      io.emit('state_update', { type: 'TOURNAMENT_REGISTERED', payload });
      return respond(res, { status: 'JOINED', ...payload });
    }

    // Safety SOS
    if (path.includes('safety/sos')) {
      return respond(res, { status: 'ALARM_TRIGGERED' });
    }

    // Wallet Swap
    if (path.includes('wallet/swap')) {
      const payload = {
        amount: Number(req.body.amount || 100),
        token: req.body.token || 'ANT',
        user: req.body.userId || 'Player_Mobile_01',
        userId: req.body.userId || 'Player_Mobile_01',
        txHash: `0xswap${Date.now().toString(16)}`
      };
      io.emit('data_sync', { type: 'TOKEN_SWAPPED', payload });
      return respond(res, { status: 'SWAPPED', txHash: payload.txHash });
    }

    if (path.includes('workflow/tickets')) {
      if (!db.workflow_tickets) {
        db.workflow_tickets = [];
      }
      if (method === 'GET') {
        const tasks = db.workflow_tickets.map(ticket => ({
          id: ticket.id,
          processName: ticket.processName || (ticket.type === 'REPAIR' ? '维修工单' : ticket.type === 'REFUND' ? '退款审批' : ticket.type === 'CUSTOMER_SERVICE' ? '客服处理' : ticket.type || '通用流程'),
          title: ticket.title,
          starter: ticket.starter || ticket.user || ticket.creator || 'SYSTEM',
          nodeName: ticket.nodeName || (ticket.status === 'NEW' ? '待处理' : ticket.status === 'APPROVED' ? '已同意' : ticket.status === 'REJECTED' ? '已驳回' : ticket.status),
          time: ticket.completedAt ? '已处理' : '刚刚',
          status: ticket.status,
          priority: ticket.priority || 'MEDIUM',
          relatedId: ticket.relatedId || ticket.id,
          relatedType: ticket.relatedType || ticket.type || 'WORKFLOW',
          actionUrl: ticket.relatedType === 'TICKET' || ticket.type === 'REPAIR' || ticket.type === 'CUSTOMER_SERVICE' || ticket.type === 'SERVICE'
            ? `/pages/ticket/detail/index?ticketId=${encodeURIComponent(ticket.relatedId || ticket.id)}`
            : ticket.type === 'REFUND'
              ? `/pages/order/detail/index?orderId=${encodeURIComponent(ticket.relatedId || ticket.id)}`
              : '/pages/workflow/tasks/index',
          externalWorkspace: ticket.externalWorkspace || (ticket.type === 'REPAIR' ? 'admin' : 'business'),
          externalPath: ticket.externalPath || (ticket.type === 'REPAIR' ? '/workflow/list' : '/ticket-system'),
          createdAt: ticket.createdAt,
          completedAt: ticket.completedAt || '',
          reviewer: ticket.reviewer || '',
          taskType: ticket.type || 'GENERAL'
        }));
        return respond(res, tasks, 'success', 200);
      }
      const ticket = {
        id: `W-${Date.now()}`,
        type: req.body.type || 'GENERAL',
        title: req.body.title || '移动端新建流程',
        priority: req.body.priority || 'MEDIUM',
        status: 'NEW',
        starter: req.body.starter || req.body.user || 'Player_Mobile_01',
        nodeName: '待处理',
        relatedId: req.body.relatedId || req.body.ticketId || req.body.orderId || '',
        relatedType: req.body.relatedType || req.body.type || 'GENERAL',
        createdAt: new Date().toISOString()
      };
      db.workflow_tickets.unshift(ticket);
      io.emit('data_sync', { type: 'WORKFLOW_TASK_CREATED', payload: ticket });
      return respond(res, ticket, 'success', 200);
    }

    if (path.includes('workflow/complete')) {
      if (!db.workflow_tickets) {
        db.workflow_tickets = [];
      }
      const task = db.workflow_tickets.find(item => String(item.id) === String(req.body.taskId));
      const action = req.body.action === 'reject' ? 'REJECTED' : 'APPROVED';
      if (task) {
        task.status = action;
        task.completedAt = new Date().toISOString();
        task.reviewer = req.body.user || 'Manager_01';
      }
      if (task?.type === 'REFUND') {
        const targetOrder = findOrderById(task.relatedId);
        if (targetOrder) {
          targetOrder.updatedAt = task.completedAt;
          targetOrder.refundReviewedAt = task.completedAt;
          targetOrder.refundReviewedBy = task.reviewer;
          if (action === 'APPROVED') {
            targetOrder.status = 'REFUNDING';
            targetOrder.refundAuditStatus = 'APPROVED';
            targetOrder.financeStatus = 'PENDING_PAYOUT';
          } else {
            targetOrder.status = 'COMPLETED';
            targetOrder.refundAuditStatus = 'REJECTED';
            targetOrder.financeStatus = 'REJECTED';
            targetOrder.refundRejectReason = req.body.reason || '5174 审核未通过'
          }
        }
      }
      let refundServiceTicketPayload = null;
      if (task?.type === 'REFUND') {
        refundServiceTicketPayload = syncRefundServiceTicket(task.relatedId, action === 'APPROVED' ? 'APPROVED' : 'REJECTED', {
          reason: req.body.reason || '',
          operator: task.reviewer,
          assignee: '5178 客服台',
          workflowTaskId: task.id,
          workflowStatus: task.status
        });
      }
      let ticketPayload = null;
      if (task?.relatedType === 'TICKET') {
        task.nodeName = action === 'APPROVED' ? '处理中' : '已关闭';
        ticketPayload = syncTicketByWorkflowAction(task, action, req.body.reason || '');
      }
      const payload = {
        taskId: req.body.taskId,
        taskName: task?.title || '流程任务',
        action: req.body.action,
        status: action,
        user: req.body.user || 'Manager_01',
        relatedId: task?.relatedId || '',
        relatedType: task?.relatedType || task?.type || 'GENERAL',
        completedAt: task?.completedAt || new Date().toISOString(),
        taskType: task?.type || 'GENERAL'
      };
      db.mobileNotifications.unshift({
        id: `WORKFLOW-${req.body.taskId}-${Date.now()}`,
        category: 'system',
        title: action === 'APPROVED' ? '流程任务已同意' : '流程任务已驳回',
        desc: `${payload.taskName} 已由 ${payload.user} 完成处理，可前往工作流中心查看记录。`,
        time: '刚刚',
        icon: action === 'APPROVED' ? 'i-carbon-checkmark-outline' : 'i-carbon-close-outline',
        iconBg: action === 'APPROVED' ? 'bg-emerald-900/30' : 'bg-red-900/30',
        iconColor: action === 'APPROVED' ? 'text-emerald-400' : 'text-red-400',
        action: true,
        actionText: '查看流程',
        actionUrl: '/pages/workflow/tasks/index',
        requestId: req.body.taskId,
        detailMeta: {
          reviewer: payload.user,
          updatedAt: payload.completedAt,
          reason: req.body.reason || ''
        },
        isRead: false
      });
      io.emit('data_sync', { type: 'WORKFLOW_TASK_COMPLETED', payload });
      io.emit('state_update', { type: 'WORKFLOW_TASK_COMPLETED', payload });
      if (ticketPayload) {
        io.emit('data_sync', { type: 'TICKET_STATUS_UPDATED', payload: ticketPayload });
        if (ticketPayload.status === 'CLOSED') {
          io.emit('data_sync', { type: 'TICKET_CLOSED', payload: ticketPayload });
        }
      }
      if (task?.type === 'REFUND') {
        io.emit('data_sync', {
          type: 'REFUND_REVIEW_UPDATED',
          payload: {
            orderId: task.relatedId,
            status: action === 'APPROVED' ? 'REFUNDING' : 'COMPLETED',
            refundAuditStatus: action,
            financeStatus: action === 'APPROVED' ? 'PENDING_PAYOUT' : 'REJECTED',
            reviewer: task.reviewer,
            reason: req.body.reason || ''
          }
        });
        if (refundServiceTicketPayload) {
          io.emit('data_sync', {
            type: action === 'APPROVED' ? 'TICKET_STATUS_UPDATED' : 'TICKET_CLOSED',
            payload: refundServiceTicketPayload
          });
        }
      }
      return respond(res, payload, 'success', 200);
    }

    // New Batch 3 endpoints
    if (path.includes('parking/unlock')) {
      const payload = {
        userId: req.body.userId || 'VIP_User_01',
        user: req.body.userId || 'VIP_User_01',
        spaceId: req.body.spaceId || 'A-088',
        plate: req.body.plate || '粤B·88888'
      };
      io.emit('data_sync', { type: 'PARKING_UNLOCKED', payload });
      io.emit('state_update', { type: 'PARKING_UNLOCKED', payload });
      return respond(res, { status: 'UNLOCKED', ...payload });
    }
    if (path.includes('orders/refund') && !path.includes('orders/refund/settle')) {
      return respond(res, { status: 'REFUNDING' });
    }
    if (path.includes('achievement/claim')) {
      return respond(res, { status: 'CLAIMED' });
    }
    if (path.includes('nft/equip')) {
      io.emit('data_sync', {
        type: 'NFT_EQUIPPED',
        payload: {
          userId: req.body.userId || 'Player_Mobile_01',
          user: req.body.userId || 'Player_Mobile_01',
          nftId: req.body.nftId,
          nftName: req.body.nftName,
          equippedAt: new Date().toLocaleString()
        }
      })
      return respond(res, { status: 'EQUIPPED' });
    }
    if (path.includes('event/create')) {
      return respond(res, { status: 'CREATED' });
    }
    if (path.includes('marketing/withdraw')) {
      const withdrawRes = await fetch('http://localhost:8080/api/marketing/withdraw', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req.body || {})
      });
      const withdrawData = await withdrawRes.json();
      return respond(res, withdrawData.data || withdrawData, 'success', 200);
    }
    if (path.includes('orders/pos')) {
      return respond(res, { status: 'PAID' });
    }
    if (path.includes('supply/request')) {
      return respond(res, { status: 'REQUESTED' });
    }
    if (path.includes('hr/kpi/update')) {
      const payload = {
        employeeId: req.body.employeeId || 'Staff-01',
        taskId: req.body.taskId || '默认任务',
        action: req.body.action || 'COMPLETED',
        score: req.body.score || 5,
        user: req.body.employeeName || req.body.employeeId || 'Staff-01'
      };
      io.emit('data_sync', { type: 'KPI_UPDATED', payload });
      io.emit('state_update', { type: 'KPI_UPDATED', payload });
      return respond(res, { status: 'UPDATED' });
    }

    // Shift Swap (Employee)
    if (path.includes('crowdfunding/support')) {
      const amount = req.body.amount || 50;
      io.emit('data_sync', { type: 'CROWDFUNDING_SUPPORTED', payload: { projectId: req.body.projectId, amount, user: 'Alex' } });
      io.emit('state_update', { type: 'PAYMENT_SUCCESS', payload: { amount, source: '众筹支持 (5171)' } });
      io.emit('notification', { title: 'Crowdfunding', content: `Thank you for your support.` });
      return respond(res, { status: 'SUPPORTED' });
    }
    if (path.includes('jukebox/queue')) {
      io.emit('data_sync', { type: 'JUKEBOX_SONG_ADDED', payload: { songName: req.body.songName, artist: req.body.artist, user: 'Alex' } });
      io.emit('notification', { title: 'Jukebox Request', content: `Your song ${req.body.songName} was added to the queue.` });
      return respond(res, { status: 'QUEUED' });
    }
    if (path.includes('inventory/sync')) {
      io.emit('data_sync', { type: 'INVENTORY_SYNCED', payload: { count: req.body.items ? req.body.items.length : 0, user: 'Alex' } });
      return respond(res, { status: 'SYNCED' });
    }

    // Default Fallback
    if (method === 'GET' && (path.includes('list') || path.includes('records') || path.includes('history'))) {
      return respond(res, [{ id: 1, name: 'Sample Item 1' }, { id: 2, name: 'Sample Item 2' }]);
    }
    return respond(res, { message: 'Mock processed successfully', path });
  });

  app.use('/api', router);
};
