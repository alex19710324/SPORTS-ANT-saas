const express = require('express');
const cors = require('cors');
const { Server } = require('socket.io');
const http = require('http');
const path = require('path');
const fs = require('fs');

const app = express();
const server = http.createServer(app);

// Ensure public/images directory exists
const publicImagesDir = path.join(__dirname, 'public', 'images');
if (!fs.existsSync(publicImagesDir)) {
  fs.mkdirSync(publicImagesDir, { recursive: true });
}

app.use('/images', express.static(publicImagesDir));

// Add root route handler
app.get('/', (req, res) => {
  res.send('Mock Server is running');
});

const io = new Server(server, {
  cors: {
    origin: '*',
    methods: ['GET', 'POST']
  }
});

app.use(cors());
app.use(express.json());

// --- In-Memory Data Store ---
const db = {
  users: [
    { id: '1', username: 'admin', role: 'ADMIN', token: 'mock-token-admin' },
    { id: '2', username: 'player1', role: 'PLAYER', token: 'mock-token-player' },
    { id: '3', username: 'business1', role: 'BUSINESS', token: 'mock-token-business' }
  ],
  leads: [], // B2B leads
  bookings: [],
  notifications: [],
  export_tasks: [],
  verification_logs: [],
  ticket_timelines: {},
  venues: [
    { id: 'v1', name: 'New York VR Center', city: 'new-york', capacity: 20, booked: 5 },
    { id: 'v2', name: 'Los Angeles Arena', city: 'los-angeles', capacity: 30, booked: 12 },
    { id: 'v3', name: 'Chicago Sports Hub', city: 'chicago', capacity: 25, booked: 8 }
  ],
  tasks: [
    { id: 't1', title: 'Fix VR Headset #4', assignee: 'emp1', status: 'PENDING', priority: 'HIGH' },
    { id: 't2', title: 'Restock Vending Machine', assignee: 'emp1', status: 'IN_PROGRESS', priority: 'MEDIUM' }
  ],
  devices: [
    { id: 'd1', name: 'Turnstile A', status: 'ONLINE', type: 'ACCESS', location: 'Main Entrance' },
    { id: 'd2', name: 'VR Pod 1', status: 'BUSY', type: 'VR', location: 'Zone A' },
    { id: 'd3', name: 'Locker 101', status: 'OFFLINE', type: 'LOCKER', location: 'Changing Room' }
  ],
  campaigns: [
    { id: 'c1', name: 'Summer VR Sale', discount: '20%', code: 'SUMMER20', status: 'ACTIVE', type: 'DISCOUNT', targetAudience: 'ALL', reachCount: 1200, conversionCount: 150, roi: 3.5 },
    { id: 'c2', name: 'New Player Bonus', discount: '50%', code: 'NEW50', status: 'ACTIVE', type: 'COUPON', targetAudience: 'NEW_LEADS', reachCount: 500, conversionCount: 80, roi: 4.2 },
    { id: 'c3', name: 'Draft Event', discount: '0%', code: 'EVENT', status: 'DRAFT', type: 'EVENT', targetAudience: 'VIP', reachCount: 0, conversionCount: 0, roi: 0 }
  ],
  tickets: [
    { id: 'tk1', userId: '2', game: 'VR Battle', date: '2026-03-20', status: 'UNUSED', qrCode: 'QR-MOCK-1' },
    { id: 'tk2', userId: '2', game: 'Archery', date: '2026-03-21', status: 'USED', qrCode: 'QR-MOCK-2' }
  ],
  workflow_tickets: [
    { id: 'W-1001', type: 'REPAIR', title: 'VR Headset #4 Display Issue', priority: 'HIGH', status: 'NEW', assigneeId: 'Unassigned', createdAt: new Date().toISOString(), description: 'Left eye screen flickering' }
  ],
  members: [
    { id: '1', userId: '1', name: 'Admin', points: 9999, level: 'DIAMOND', balance: 5000, riskLevel: 'LOW', joinDate: '2025-01-01', currentLevel: { name: 'DIAMOND' } },
    { id: '2', userId: '2', name: 'Player One', points: 150, level: 'SILVER', balance: 200, riskLevel: 'MEDIUM', joinDate: '2026-03-01', currentLevel: { name: 'SILVER' } },
    { id: '3', userId: '3', name: 'Inactive User', points: 0, level: 'BRONZE', balance: 0, riskLevel: 'HIGH', joinDate: '2025-06-15', currentLevel: { name: 'BRONZE' } }
  ],
  success_stories: [
    { id: 's1', title: 'VR Arena Tokyo', description: 'Increased revenue by 300% in 3 months.', image: 'http://localhost:8080/images/asset_d1571c6d42233ed32417eb17e425db24.jpg' },
    { id: 's2', title: 'London eSports Hub', description: 'Hosted 50+ tournaments with Sports Ant.', image: 'http://localhost:8080/images/asset_ab6e15f7f659c0619a9b723a156b3813.jpg' }
  ],
  game_library: [
    { id: 'g1', title: 'Cyber Boxing', category: 'VR Sports', image: 'http://localhost:8080/images/asset_52ffd21165f088875945738c4f5fcee2.jpg', players: '1-2' },
    { id: 'g2', title: 'Neon Archery', category: 'Precision', image: 'http://localhost:8080/images/asset_c201d0d9e7378cacbd3e2b61541e6596.jpg', players: '1-4' },
    { id: 'g3', title: 'Zero Gravity Racing', category: 'Simulator', image: 'http://localhost:8080/images/asset_9a4060e9e74e1c83c1a8f4d073ee29c2.jpg', players: '1-8' }
  ],
  products: [
    { id: 'p1', name: 'VR Standard Session (1 Hour)', price: 199, category: 'Service', status: 'ACTIVE', sales: 120, stock: 999 },
    { id: 'p2', name: 'Sports Ant Energy Drink', price: 15, category: 'Merchandise', status: 'ACTIVE', sales: 450, stock: 50 },
    { id: 'p3', name: 'Cyberpunk Gacha Box', price: 39, category: 'Merchandise', status: 'ACTIVE', sales: 890, stock: 200 }
  ],
  inventory: [
    { id: 'i1', name: 'VR Headset Pro', sku: 'VR-PRO-001', stock: 15, status: 'LOW_STOCK', location: 'Warehouse A' },
    { id: 'i2', name: 'Haptic Vest', sku: 'HAP-VEST-02', stock: 45, status: 'IN_STOCK', location: 'Warehouse B' },
    { id: 'i3', name: 'Motion Controller', sku: 'MOT-CTRL-03', stock: 120, status: 'IN_STOCK', location: 'Warehouse A' },
    { id: 'i4', name: 'Cyberpunk Gacha Box', sku: 'MERCH-GACHA', stock: 200, status: 'IN_STOCK', location: 'Store Front' }
  ],
  expert_reviews: [
    { id: 'r1', venue: 'New York VR Center', expert: 'Dr. Strange', rating: 4.8, comments: 'Excellent VR calibration, safe for kids.', status: 'APPROVED', date: '2026-03-10' },
    { id: 'r2', venue: 'Chicago Sports Hub', expert: 'Coach Carter', rating: 3.5, comments: 'Basketball hoop sensors need recalibration.', status: 'PENDING', date: '2026-03-15' }
  ],
  trendy_items: [
    { id: 't1', name: 'Golden Dragon (Limited)', type: 'BLIND_BOX', price: 99, image: 'http://localhost:8080/images/asset_a9cc5c6eb116fe5711d259291a143e10.jpg', probability: '0.5%', stock: 10 },
    { id: 't2', name: 'Cyber Cat Figure', type: 'FIGURE', price: 59, image: 'http://localhost:8080/images/asset_72fd9f2fdb2f2bb382fa71f905a5d514.jpg', stock: 50 },
    { id: 't3', name: 'Neon Jersey', type: 'APPAREL', price: 129, image: 'http://localhost:8080/images/asset_6348d8ad1ee4b7d6257dc0fd35eddbfd.jpg', stock: 30 }
  ],
  videos: [
    { id: 'vid1', title: 'VR Boxing Championship Highlights', uploader: 'Admin', views: 1250, status: 'PUBLISHED', date: '2026-03-20', url: 'http://localhost:8080/images/asset_5b1f192a19571b7e80775bbad3051aef.mp4' },
    { id: 'vid2', title: 'How to calibrate sensors', uploader: 'Tech Support', views: 340, status: 'PUBLISHED', date: '2026-03-21', url: 'http://localhost:8080/images/asset_5b1f192a19571b7e80775bbad3051aef.mp4' }
  ],
  organization_tree: [
    {
      id: 'hq', label: 'Sports Ant HQ', type: 'HQ', children: [
        { id: 'reg-na', label: 'North America', type: 'REGION', children: [
          { id: 'v1', label: 'New York VR Center', type: 'VENUE', status: 'ACTIVE' },
          { id: 'v3', label: 'Chicago Sports Hub', type: 'VENUE', status: 'ACTIVE' }
        ]},
        { id: 'reg-asia', label: 'Asia Pacific', type: 'REGION', children: [
          { id: 'v-tokyo', label: 'Tokyo Arena', type: 'VENUE', status: 'CONSTRUCTION' }
        ]}
      ]
    }
  ],
  leaderboard: [
    { rank: 1, name: 'CyberNinja', score: 9500, avatar: 'http://localhost:8080/images/asset_2c774ff276618b470a3caf076631252f.svg' },
    { rank: 2, name: 'NeonRider', score: 8800, avatar: 'http://localhost:8080/images/asset_32f6cf35ab9d16a6a915c4e33c4ad954.svg' },
    { rank: 3, name: 'PixelQueen', score: 8200, avatar: 'http://localhost:8080/images/asset_2db09cad61a85973b4888102ee20178b.svg' },
    { rank: 4, name: 'GlitchMaster', score: 7500, avatar: 'http://localhost:8080/images/asset_4e094a87db60c2489269c1b1e1684a49.svg' },
    { rank: 5, name: 'RetroKing', score: 7100, avatar: 'http://localhost:8080/images/asset_2dc80deef38aceaa7427dc8528444c04.svg' }
  ],
  sops: [
    { id: 'sop1', title: 'VR Headset Cleaning Protocol', category: 'Hygiene', version: '1.2', lastUpdated: '2026-03-01' },
    { id: 'sop2', title: 'Emergency Fire Evacuation', category: 'Safety', version: '2.0', lastUpdated: '2025-12-15' },
    { id: 'sop3', title: 'Customer Dispute Resolution', category: 'Service', version: '1.0', lastUpdated: '2026-01-20' }
  ],
  achievements: [
    { id: 'a1', title: 'VR Rookie', description: 'Played your first VR game', icon: 'i-carbon-game-console', earnedAt: '2026-03-01' },
    { id: 'a2', title: 'Sharpshooter', description: 'Scored 500+ in Archery', icon: 'i-carbon-target', earnedAt: '2026-03-10' },
    { id: 'a3', title: 'Social Butterfly', description: 'Attended 3 community events', icon: 'i-carbon-events', earnedAt: '2026-03-18' }
  ],
  community_events: [
    { id: 'e1', title: 'Weekend VR Tournament', date: '2026-03-25', location: 'New York VR Center', attendees: 45, status: 'OPEN' },
    { id: 'e2', title: 'Cosplay Night', date: '2026-04-01', location: 'Tokyo Arena', attendees: 120, status: 'OPEN' }
  ],
  system_health: {
    services: [
      { name: 'API Gateway', status: 'OPERATIONAL', latency: '24ms' },
      { name: 'Database', status: 'OPERATIONAL', latency: '12ms' },
      { name: 'IoT Hub', status: 'DEGRADED', latency: '150ms' }, // Simulated issue
      { name: 'Payment Gateway', status: 'OPERATIONAL', latency: '85ms' }
    ],
    cpu_load: '45%',
    memory_usage: '62%'
  },
  analytics: {
    revenue: [
      { date: '2026-03-15', amount: 1200 },
      { date: '2026-03-16', amount: 1500 },
      { date: '2026-03-17', amount: 1100 },
      { date: '2026-03-18', amount: 2000 } // Today
    ],
    insights: [
      { id: 'i1', type: 'positive', message: 'Revenue up 15% due to "Summer Sale" campaign.' },
      { id: 'i2', type: 'negative', message: 'Customer traffic low in Zone B between 2-4 PM.' },
      { id: 'i3', type: 'neutral', message: 'Most popular game this week: VR Boxing.' }
    ]
  },
  transactions: [
    { id: 'tx1', type: 'income', amount: 250, category: 'Booking', date: '2026-03-18T10:00:00Z', status: 'COMPLETED' },
    { id: 'tx2', type: 'expense', amount: 500, category: 'Maintenance', date: '2026-03-18T09:30:00Z', status: 'COMPLETED' },
    { id: 'tx3', type: 'income', amount: 120, category: 'Merchandise', date: '2026-03-18T11:15:00Z', status: 'COMPLETED' },
    { id: 'tx4', type: 'income', amount: 1000, category: 'Franchise Fee', date: '2026-03-17T15:00:00Z', status: 'COMPLETED' }
  ],
  shifts: [
    { id: 'sh1', employee: 'John Doe', role: 'Trainer', start: '2026-03-19T09:00:00', end: '2026-03-19T17:00:00', venue: 'New York VR Center' },
    { id: 'sh2', employee: 'Jane Smith', role: 'Manager', start: '2026-03-19T10:00:00', end: '2026-03-19T18:00:00', venue: 'New York VR Center' },
    { id: 'sh3', employee: 'Mike Ross', role: 'Technician', start: '2026-03-20T09:00:00', end: '2026-03-20T17:00:00', venue: 'Chicago Sports Hub' }
  ],
  risk_alerts: [
    { id: 'ra1', type: 'FRAUD', level: 'HIGH', message: 'Suspicious transaction pattern detected for User ID 8848.', status: 'OPEN', timestamp: '2026-03-18T10:30:00Z' },
    { id: 'ra2', type: 'COMPLIANCE', level: 'MEDIUM', message: 'Staff certification expiring in 3 days.', status: 'PENDING', timestamp: '2026-03-18T09:00:00Z' },
    { id: 'ra3', type: 'SAFETY', level: 'CRITICAL', message: 'Fire alarm sensor malfunction in Zone B.', status: 'RESOLVED', timestamp: '2026-03-17T14:20:00Z' }
  ],
  courses: [
    { id: 'c1', title: 'VR Safety Basics', duration: '45m', type: 'VIDEO', status: 'PUBLISHED', completionRate: 85 },
    { id: 'c2', title: 'Advanced Equipment Repair', duration: '2h', type: 'WORKSHOP', status: 'DRAFT', completionRate: 0 },
    { id: 'c3', title: 'Customer Service 101', duration: '1h', type: 'VIDEO', status: 'PUBLISHED', completionRate: 92 }
  ],
  kocs: [
    {
      id: 'CyberPlayer_01',
      name: '赛博夜行者',
      level: 'GOLD',
      levelLabel: '黄金',
      commission: 8136,
      fans: 12050,
      traffic: 1205,
      gmv: 45200,
      conversionRate: 12.5,
      tasksCompleted: 11,
      platforms: ['小红书', '抖音'],
      isInternal: false,
      avatar: 'http://localhost:8080/images/asset_3395330c19e5e3fcff6d26d338fb1a0f.svg'
    },
    {
      id: 'CreatorPortal_01',
      name: '霓虹探店官',
      level: 'GOLD',
      levelLabel: '黄金',
      commission: 5778,
      fans: 8900,
      traffic: 890,
      gmv: 32100,
      conversionRate: 18.2,
      tasksCompleted: 9,
      platforms: ['微信朋友圈', '小红书'],
      isInternal: false,
      avatar: 'http://localhost:8080/images/asset_da5e324cba02d71afa70ef68e9d34d9a.svg'
    },
    {
      id: 'EMP_001',
      name: '前台-王乐乐',
      level: 'SILVER',
      levelLabel: '白银',
      commission: 2775,
      fans: 3200,
      traffic: 650,
      gmv: 18500,
      conversionRate: 10.4,
      tasksCompleted: 8,
      platforms: ['微信朋友圈'],
      isInternal: true,
      avatar: 'http://localhost:8080/images/asset_a3473467725453c86948064d160dd13c.svg'
    },
    {
      id: 'koc-004',
      name: '周末组局王',
      level: 'SILVER',
      levelLabel: '白银',
      commission: 1800,
      fans: 5600,
      traffic: 420,
      gmv: 12000,
      conversionRate: 7.6,
      tasksCompleted: 6,
      platforms: ['抖音'],
      isInternal: false,
      avatar: 'http://localhost:8080/images/asset_02a5be223e4b105f697cd61c81cebab0.svg'
    },
    {
      id: 'koc-005',
      name: '新晋玩家007',
      level: 'BRONZE',
      levelLabel: '青铜',
      commission: 350,
      fans: 980,
      traffic: 150,
      gmv: 3500,
      conversionRate: 4.8,
      tasksCompleted: 2,
      platforms: ['B站'],
      isInternal: false,
      avatar: 'http://localhost:8080/images/asset_9a7aefc3fb79f6f2a64342541949ca16.svg'
    }
  ],
  venue_showcase: {
    title: "SPORTS ANT - FUTURE ARENA",
    description: "Experience the ultimate fusion of sports and technology.",
    introduction: "Sports Ant is not just a gym; it's a digital playground. We integrate cutting-edge VR, AR, and AI technology to create an immersive athletic experience like no other. From professional-grade archery to holographic battle arenas, we redefine what it means to play.",
    video_url: "http://localhost:8080/images/asset_5b1f192a19571b7e80775bbad3051aef.mp4",
    cad_url: "https://example.com/venue_blueprint.dwg",
    ppt_url: "https://example.com/project_overview.pptx",
    render_url: "https://example.com/renderings.zip",
    features: [
      {
        title: "Immersive VR Battle Arenas",
        description: "400sqm free-roam arena.",
        gameplay: "Team up 4v4, put on the haptic vest, and survive the zombie apocalypse.",
        image: "http://localhost:8080/images/asset_52ffd21165f088875945738c4f5fcee2.jpg"
      },
      {
        title: "Olympic Standard Digital Archery",
        description: "Professional recurve bows with sensors.",
        gameplay: "Compete in global leaderboards with real-time accuracy tracking.",
        image: "http://localhost:8080/images/asset_92ea2b5f1ef7ecc92cb943191f9b2a78.jpg"
      },
      {
        title: "Holographic Boxing Ring",
        description: "Fight against AI opponents.",
        gameplay: "Shadow box with holographic guides and impact feedback.",
        image: "http://localhost:8080/images/asset_9a4060e9e74e1c83c1a8f4d073ee29c2.jpg"
      }
    ],
    gallery: [
      "http://localhost:8080/images/asset_52ffd21165f088875945738c4f5fcee2.jpg",
      "http://localhost:8080/images/asset_92ea2b5f1ef7ecc92cb943191f9b2a78.jpg",
      "http://localhost:8080/images/asset_1728f60183d6efbfe14bd880c5500878.jpg",
      "http://localhost:8080/images/asset_9a4060e9e74e1c83c1a8f4d073ee29c2.jpg"
    ],
    equipment_list: [
      { name: 'Omni-Directional Treadmill', category: 'VR Motion', specs: 'Virtuix Omni Pro', count: 12, status: 'Operational' },
      { name: 'Haptic Feedback Vest', category: 'Wearable', specs: 'bHaptics TactSuit X40', count: 20, status: 'Operational' },
      { name: '4K VR Headset', category: 'Display', specs: 'Pico Neo 3 / Quest 3', count: 50, status: 'Operational' },
      { name: 'Digital Bow', category: 'Archery', specs: 'Custom Recurve w/ Sensors', count: 10, status: 'Maintenance' }
    ]
  },
  nfts: [
    { id: 'n1', name: 'Genesis Ant #001', series: 'Genesis', rarity: 'LEGENDARY', owner: 'CyberNinja', image: 'http://localhost:8080/images/asset_d1571c6d42233ed32417eb17e425db24.jpg', status: 'MINTED' },
    { id: 'n2', name: 'Cyber Punk #2077', series: 'Cyber', rarity: 'RARE', owner: 'NeonRider', image: 'http://localhost:8080/images/asset_025524517ce1bb70713661239529f60f.jpg', status: 'MINTED' },
    { id: 'n3', name: 'Mecha Ant #88', series: 'Mecha', rarity: 'COMMON', owner: 'Player One', image: 'http://localhost:8080/images/asset_ff2b658b2dec8c5039a402a032508aab.jpg', status: 'MINTED' }
  ]
};

const syncEventAliases = {
  NEW_KOC_TASK: ['ALL_STAFF_MARKETING_TASK'],
  CONTRACT_SIGNED: ['ELECTRONIC_CONTRACT_SIGNED'],
  ELECTRONIC_CONTRACT_SIGNED: ['CONTRACT_SIGNED'],
  NFT_AIRDROP_RECEIVED: ['DID_WALLET_AIRDROP'],
  SHIFT_SWAP_REQUESTED: ['SHIFT_CHANGE_REQUEST'],
  ESS_LEAVE_REQUEST: ['LEAVE_REQUESTED'],
  QR_SCANNED: ['DEVICE_SCANNED'],
  LOCKER_OVERDUE: ['LOCKER_OVERDUE_ALARM', 'LOCKER_OVERSTAY_CLEANUP'],
  EMERGENCY_TRIGGERED: ['SOS_ALARM', 'EMERGENCY_REPORTED'],
  EMERGENCY_SOS_TRIGGERED: ['SOS_ALARM', 'EMERGENCY_REPORTED'],
  EMERGENCY_REPORTED: ['SOS_ALARM']
};

if (!db.koc_tasks) {
  db.koc_tasks = [
    {
      id: 'koc-task-1',
      title: '周末盲盒引流计划',
      reward: '15% ~ 20%',
      commissionRate: 18,
      claimed: 45,
      budget: 15000,
      uv: 3200,
      orders: 125,
      paid: 4500,
      gmv: 30000,
      status: 'ACTIVE',
      minFans: 1000,
      levelTarget: '全部等级',
      materialsReady: true,
      createdBy: '5178-KOC',
      createdAt: '2026-04-02T09:00:00.000Z',
      shareLink: 'sportsant.com/ref/weekend-blindbox'
    },
    {
      id: 'koc-task-2',
      title: '春季机械纪元盲盒清库推广',
      reward: '18% + 盲盒抽奖券',
      commissionRate: 20,
      claimed: 28,
      budget: 8000,
      uv: 1500,
      orders: 42,
      paid: 1260,
      gmv: 12600,
      status: 'ACTIVE',
      minFans: 500,
      levelTarget: '仅限 黄金/白银',
      materialsReady: true,
      createdBy: '5178-KOC',
      createdAt: '2026-04-03T10:30:00.000Z',
      shareLink: 'sportsant.com/ref/mecha-spring'
    }
  ];
}

if (!db.koc_activity) {
  db.koc_activity = [
    {
      id: 'koc-activity-1',
      type: 'TASK_PUBLISHED',
      title: '总部任务已下发',
      detail: '周末盲盒引流计划已同步到 5171 KOC HUB、5174 营销后台与 5176 创作者门户。',
      timestamp: '2026-04-03T10:32:00.000Z'
    },
    {
      id: 'koc-activity-2',
      type: 'KOC_CONVERSION',
      title: '最新成交已回流',
      detail: '赛博夜行者通过春季机械纪元盲盒清库推广完成 1 笔转化，佣金已进入待结算。',
      timestamp: '2026-04-03T14:12:00.000Z'
    }
  ];
}

const getKocLevelLabel = (level) => {
  if (level === 'GOLD') return '黄金';
  if (level === 'SILVER') return '白银';
  if (level === 'BRONZE') return '青铜';
  if (level === 'DIAMOND') return '钻石';
  return '青铜';
};

const getKocRecord = (kocId) => {
  if (!kocId) return db.kocs[0];
  return db.kocs.find(item => item.id === kocId) || db.kocs[0];
};

const getKocTask = (taskId, taskTitle) => {
  if (taskId) {
    const matchedById = db.koc_tasks.find(item => item.id === taskId);
    if (matchedById) return matchedById;
  }
  if (taskTitle) {
    return db.koc_tasks.find(item => item.title === taskTitle);
  }
  return db.koc_tasks[0];
};

const pushKocActivity = ({ type, title, detail }) => {
  db.koc_activity.unshift({
    id: `koc-activity-${Date.now()}-${Math.random().toString(16).slice(2, 6)}`,
    type,
    title,
    detail,
    timestamp: new Date().toISOString()
  });
  db.koc_activity = db.koc_activity.slice(0, 12);
};

const getKocOverview = (viewerId = 'CyberPlayer_01') => {
  const kocs = [...db.kocs]
    .sort((a, b) => (b.commission || 0) - (a.commission || 0))
    .map(item => ({
      ...item,
      levelLabel: item.levelLabel || getKocLevelLabel(item.level)
    }));
  const tasks = [...db.koc_tasks]
    .filter(item => item.status !== 'ARCHIVED')
    .sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime());
  const totalKOCs = kocs.length;
  const viralReach = tasks.reduce((sum, item) => sum + Number(item.uv || 0), 0);
  const commissionPaid = tasks.reduce((sum, item) => sum + Number(item.paid || 0), 0);
  const avgConversion = totalKOCs > 0
    ? Number((kocs.reduce((sum, item) => sum + Number(item.conversionRate || 0), 0) / totalKOCs).toFixed(1))
    : 0;
  const totalGMV = tasks.reduce((sum, item) => sum + Number(item.gmv || 0), 0);
  const activeClaims = tasks.reduce((sum, item) => sum + Number(item.claimed || 0), 0);
  const myKoc = getKocRecord(viewerId);
  const spotlightTask = tasks[0] || null;

  return {
    summary: {
      totalKOCs,
      viralReach,
      commissionPaid,
      avgConversion,
      totalGMV,
      activeClaims,
      activeTaskCount: tasks.length
    },
    tasks,
    kocs,
    spotlightTask,
    feed: db.koc_activity,
    myKoc: {
      ...myKoc,
      pendingSettlement: Number((db.finance_requests || [])
        .filter(item => item.employeeId === myKoc.id && item.businessType === 'KOC_COMMISSION' && item.status !== 'SETTLED')
        .reduce((sum, item) => sum + Number(item.amount || 0), 0)
        .toFixed(2)),
      shareLink: `sportsant.com/ref/${String(myKoc.id || 'koc').toLowerCase()}`,
      clicks: Number(myKoc.traffic || 0) * 3,
      conversions: Number(myKoc.conversions || Math.max(1, Math.round(Number(myKoc.tasksCompleted || 0) * 1.2)))
    }
  };
};

if (!db.finance_requests) {
  db.finance_requests = [
    {
      id: 'WD-202604010001',
      requestId: 'WD-202604010001',
      requestType: 'WITHDRAWAL',
      businessType: 'KOC_COMMISSION',
      employeeId: 'EMP_001',
      employeeName: '姚云忠',
      amount: 320,
      sourceTaskTitle: '周末盲盒引流计划',
      status: 'APPROVED',
      statusText: '已通过',
      desc: '微信零钱 提现申请',
      createdAt: '2026-04-01T10:20:00.000Z',
      updatedAt: '2026-04-01T11:00:00.000Z',
      voucher: {
        channel: '微信零钱',
        account: '微信零钱账户 · 8848',
        operatorName: '王财务',
        serialNo: 'FIN-24040101',
        paymentAt: '2026-04-01T11:00:00.000Z',
        voucherName: 'withdrawal-proof.png',
        voucherType: 'png',
        voucherUrl: 'https://finance.sportsant.cn/voucher/withdrawal-proof.png',
        voucherRemark: '已进入财务处理流程'
      },
      timeline: [
        { key: 'WD-202604010001-submitted', label: '申请提交', roleLabel: '员工发起', handlerName: '姚云忠', status: 'PENDING', time: '2026-04-01T10:20:00.000Z', desc: '微信零钱' },
        { key: 'WD-202604010001-approved', label: '审核通过', roleLabel: '财务审核', handlerName: '王财务', status: 'APPROVED', time: '2026-04-01T11:00:00.000Z', desc: '已进入财务处理流程' }
      ]
    }
  ];
}

const financeStatusTextMap = {
  PENDING: '待审核',
  APPROVED: '已通过',
  SETTLED: '已结算',
  REJECTED: '已驳回'
};

const buildFinanceRequestId = (prefix = 'FN') => `${prefix}-${Date.now()}`;

const normalizeFinanceVoucher = (voucher = {}) => ({
  channel: voucher.channel || '',
  account: voucher.account || '',
  operatorName: voucher.operatorName || '王财务',
  serialNo: voucher.serialNo || '',
  paymentAt: voucher.paymentAt || '',
  voucherName: voucher.voucherName || '',
  voucherType: voucher.voucherType || '',
  voucherUrl: voucher.voucherUrl || '',
  voucherRemark: voucher.voucherRemark || '',
  failureReason: voucher.failureReason || ''
});

const prependFinanceTransaction = (entry) => {
  db.transactions.unshift(entry);
  db.transactions = db.transactions.slice(0, 40);
};

const getFinanceRequest = (requestId) => db.finance_requests.find(item => item.requestId === requestId || item.id === requestId);

const getFinanceOverview = () => {
  const queue = [...db.finance_requests].sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime());
  const pendingCount = queue.filter(item => item.status === 'PENDING').length;
  const approvedCount = queue.filter(item => item.status === 'APPROVED').length;
  const settledAmount = queue
    .filter(item => item.status === 'SETTLED')
    .reduce((sum, item) => sum + Number(item.amount || 0), 0);
  const kocSettlementAmount = queue
    .filter(item => item.businessType === 'KOC_COMMISSION')
    .reduce((sum, item) => sum + Number(item.amount || 0), 0);

  return {
    summary: {
      pendingCount,
      approvedCount,
      settledAmount,
      kocSettlementAmount
    },
    queue,
    recentTransactions: db.transactions.slice(0, 12)
  };
};

const createFinanceRequest = ({
  requestId,
  requestType,
  businessType,
  employeeId,
  employeeName,
  amount,
  desc,
  sourceTaskTitle,
  channel,
  account,
  voucherName,
  voucherType,
  voucherUrl,
  voucherRemark,
  meta
}) => {
  const normalizedRequestId = requestId || buildFinanceRequestId(requestType === 'WITHDRAWAL' ? 'WD' : requestType === 'REIMBURSEMENT' ? 'RB' : 'ST');
  const record = {
    id: normalizedRequestId,
    requestId: normalizedRequestId,
    requestType,
    businessType: businessType || (requestType === 'WITHDRAWAL' ? 'STAFF_WITHDRAWAL' : requestType),
    employeeId: employeeId || 'EMP_001',
    employeeName: employeeName || '姚云忠',
    amount: Number(amount || 0),
    desc: desc || '',
    sourceTaskTitle: sourceTaskTitle || '',
    status: 'PENDING',
    statusText: financeStatusTextMap.PENDING,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    voucher: normalizeFinanceVoucher({
      channel,
      account,
      operatorName: '王财务',
      voucherName,
      voucherType,
      voucherUrl,
      voucherRemark
    }),
    meta: meta || {},
    timeline: [
      {
        key: `${normalizedRequestId}-submitted`,
        label: '申请提交',
        roleLabel: '业务发起',
        handlerName: employeeName || '系统',
        status: 'PENDING',
        time: new Date().toISOString(),
        desc: desc || sourceTaskTitle || requestType
      }
    ]
  };
  db.finance_requests.unshift(record);
  db.finance_requests = db.finance_requests.slice(0, 40);
  prependFinanceTransaction({
    id: `tx-${normalizedRequestId}`,
    type: 'expense',
    amount: Number(amount || 0),
    category: businessType || requestType,
    date: record.createdAt,
    status: 'PENDING'
  });
  return record;
};

const syncFinanceRequestStatus = (record, status, extra = {}) => {
  if (!record) return null;
  record.status = status;
  record.statusText = financeStatusTextMap[status] || status;
  record.updatedAt = new Date().toISOString();
  record.voucher = normalizeFinanceVoucher({
    ...record.voucher,
    ...extra.voucher,
    operatorName: extra.operatorName || record.voucher?.operatorName || '王财务',
    serialNo: extra.paymentSerialNo || extra.serialNo || record.voucher?.serialNo,
    paymentAt: extra.paymentAt || extra.approvedAt || extra.completedAt || record.voucher?.paymentAt,
    voucherRemark: extra.voucherRemark || extra.remark || record.voucher?.voucherRemark,
    failureReason: extra.failureReason || ''
  });
  record.timeline.unshift({
    key: `${record.requestId}-${status}-${Date.now()}`,
    label: status === 'REJECTED' ? '审核驳回' : status === 'SETTLED' ? '完成打款' : '审核通过',
    roleLabel: status === 'SETTLED' ? '财务出纳' : '财务审核',
    handlerName: extra.operatorName || extra.approver || '王财务',
    status,
    time: extra.completedAt || extra.approvedAt || new Date().toISOString(),
    desc: extra.failureReason || extra.voucherRemark || extra.remark || (status === 'SETTLED' ? '款项已完成打款' : '已进入财务处理流程')
  });
  return record;
};

const buildSyncEvents = (data = {}) => {
  const baseType = data.type || 'SYSTEM_SYNC';
  const aliases = syncEventAliases[baseType] || [];
  return [baseType, ...aliases].map((type) => ({ ...data, type }));
};

const createSyncNotification = (data = {}) => {
  const title = data.type === 'GACHA_PULL' ? 'New Trendy Item Drawn' :
                data.type === 'GIFT_REDEEMED' ? 'Gift Redeemed' :
                data.type === 'NEW_EVENT_PUBLISHED' ? 'New Event Published' :
                data.type === 'PAYSLIP_PUBLISHED' ? 'Payslip Published' :
                data.type === 'PR_APPROVED' ? 'Procurement Approved' :
                'System Sync';
  const content = data.payload?.item ||
                  data.payload?.giftName ||
                  data.payload?.title ||
                  data.payload?.eventName ||
                  data.payload?.reason ||
                  'A new action occurred in the system.';

  return {
    id: Date.now().toString(),
    title,
    content,
    type: 'sync',
    timestamp: new Date().toISOString()
  };
};

const ensureTicketTimeline = (ticketId) => {
  if (!ticketId) return [];
  if (!db.ticket_timelines[ticketId]) {
    db.ticket_timelines[ticketId] = [];
  }
  return db.ticket_timelines[ticketId];
};

const appendTicketTimeline = (ticketId, entry) => {
  const timeline = ensureTicketTimeline(ticketId);
  timeline.unshift({
    id: `${ticketId}-${Date.now()}-${Math.random().toString(16).slice(2, 6)}`,
    timestamp: new Date().toISOString(),
    ...entry
  });
};

const ensureWorkflowTickets = () => {
  if (!db.workflow_tickets) {
    db.workflow_tickets = [];
  }
  return db.workflow_tickets;
};

const ensureSafetyIncidents = () => {
  if (!db.safety_incidents) {
    db.safety_incidents = [];
  }
  return db.safety_incidents;
};

const ensureSafetyHazards = () => {
  if (!db.safety_hazards) {
    db.safety_hazards = [
      { id: 'HZ-1001', title: '消防通道堆放杂物', location: '深圳南山店', reporter: '张店长', severity: 'High', status: 'Pending Fix', time: '今日 10:20', source: '巡检整改' },
      { id: 'HZ-1002', title: 'VR 手柄未按规定消毒', location: '广州福田店', reporter: '李品控', severity: 'Medium', status: 'Pending Fix', time: '今日 11:10', source: '巡检整改' },
      { id: 'HZ-1003', title: '灭火器即将过期', location: '北京天河店', reporter: '系统提醒', severity: 'High', status: 'Resolved', time: '昨日', source: '台账巡检' }
    ];
  }
  return db.safety_hazards;
};

const ensureInspectionReports = () => {
  if (!db.inspection_reports) {
    db.inspection_reports = [
      { id: 'IR-1001', store: '深圳南山赛博旗舰店', time: '2025-03-24 10:30', inspector: '张大区', score: 95, status: '合格', workflowTaskId: '' },
      { id: 'IR-1002', store: '广州福田电竞中心', time: '2025-03-23 15:45', inspector: '李品控', score: 78, status: '待整改', workflowTaskId: 'W-INSP-1002' },
      { id: 'IR-1003', store: '北京天河VR特区', time: '2025-03-22 09:15', inspector: '王大区', score: 85, status: '整改完成', workflowTaskId: '' }
    ];
  }
  return db.inspection_reports;
};

const resolveBookingServiceUser = (userId) => {
  if (!userId) return 'Player_Mobile_01';
  if (userId === 'Player_PC_01') return 'Player_Mobile_01';
  return userId;
};

const createBookingServiceTicket = (payload = {}) => {
  const normalizedUser = resolveBookingServiceUser(payload.userId || payload.user);
  const serviceTicketId = `TK-BKG-${Date.now().toString().slice(-8)}`;
  const workflowTaskId = `W-${Date.now()}`;
  const storeLabelMap = {
    'neo-city': '新天地赛博旗舰店',
    'cyber-hub': '五角场潮玩中心',
    'neon-alley': '静安大悦城体验店'
  };
  const gameLabelMap = {
    'vr-battle': 'VR 赛博战场',
    'archery': '霓虹射箭',
    'bowling': '赛博保龄球',
    'racing': '沉浸式赛车'
  };
  const storeLabel = storeLabelMap[payload.storeId] || payload.storeId || '门店待确认';
  const gameLabel = gameLabelMap[payload.gameId] || payload.gameId || '预约项目';
  const bookingTime = [payload.date, payload.time].filter(Boolean).join(' ') || '待确认时间';
  const bookingTypeText = payload.type === 'team' ? '组队预约' : '单人预约';
  const createdAt = new Date().toISOString();
  const serviceTicket = {
    id: serviceTicketId,
    ticketId: serviceTicketId,
    type: '预约服务',
    displayType: '预约服务',
    title: `预约服务 · ${gameLabel} · ${bookingTime}`,
    desc: `${bookingTypeText}已提交，门店：${storeLabel}，项目：${gameLabel}，预约时间：${bookingTime}。玩家来源：${payload.userId || normalizedUser}`,
    assignee: '客服中台',
    priority: payload.type === 'team' ? 'HIGH' : 'MEDIUM',
    priorityText: payload.type === 'team' ? '高' : '中',
    status: 'PENDING',
    statusText: '待处理',
    createdAt: new Date().toLocaleString(),
    updatedAt: new Date().toLocaleString(),
    user: normalizedUser,
    sourceUserId: payload.userId || normalizedUser,
    bookingRef: {
      storeId: payload.storeId || '',
      storeName: storeLabel,
      gameId: payload.gameId || '',
      gameName: gameLabel,
      date: payload.date || '',
      time: payload.time || '',
      type: payload.type || 'single'
    },
    workflowTaskId,
    workflowStatus: 'NEW',
    workflowNodeName: '待接单',
    resolutionNote: ''
  };
  db.tickets.unshift(serviceTicket);
  const workflowTicket = {
    id: workflowTaskId,
    type: 'SERVICE',
    title: serviceTicket.title,
    priority: serviceTicket.priority,
    status: 'NEW',
    assigneeId: '客服中台',
    assignee: '客服中台',
    description: serviceTicket.desc,
    createdAt,
    updatedAt: createdAt,
    nodeName: '待接单',
    starter: normalizedUser,
    relatedType: 'TICKET',
    relatedId: serviceTicket.id,
    sourceWorkspace: 'player-site',
    targetWorkspace: 'business',
    targetPath: '/ticket-system'
  };
  ensureWorkflowTickets().unshift(workflowTicket);
  appendTicketTimeline(serviceTicket.id, {
    stage: 'CREATED',
    title: '预约服务单已创建',
    operator: normalizedUser,
    detail: `${bookingTypeText}预约已提交，服务台将跟进到店安排。`,
    meta: [
      { label: '门店', value: storeLabel },
      { label: '项目', value: gameLabel },
      { label: '预约时间', value: bookingTime }
    ]
  });
  appendTicketTimeline(serviceTicket.id, {
    stage: 'ASSIGNED',
    title: '服务单已进入客服台',
    operator: '系统',
    detail: '预约服务单已同步到 5178 客服台，等待接单处理。',
    meta: [
      { label: '流程单号', value: workflowTaskId },
      { label: '当前节点', value: '待接单' }
    ]
  });
  io.emit('data_sync', { type: 'WORKFLOW_TASK_CREATED', payload: workflowTicket });
  io.emit('data_sync', {
    type: 'WORK_ORDER_CREATED',
    payload: {
      ...workflowTicket,
      ticketId: serviceTicket.id,
      displayType: serviceTicket.displayType,
      user: normalizedUser,
      desc: serviceTicket.desc,
      workflowTaskId,
      bookingRef: serviceTicket.bookingRef
    }
  });
  io.emit('data_sync', {
    type: 'TICKET_STATUS_UPDATED',
    payload: {
      ticketId: serviceTicket.id,
      title: serviceTicket.title,
      user: normalizedUser,
      status: serviceTicket.status,
      statusText: serviceTicket.statusText,
      assignee: serviceTicket.assignee,
      updatedAt: serviceTicket.updatedAt,
      workflowTaskId: serviceTicket.workflowTaskId,
      workflowStatus: serviceTicket.workflowStatus,
      workflowNodeName: serviceTicket.workflowNodeName,
      bookingRef: serviceTicket.bookingRef
    }
  });
  return { serviceTicket, workflowTicket };
};

const findWorkflowTicketByRelatedTicketId = (ticketId) => ensureWorkflowTickets().find(item =>
  item.relatedType === 'TICKET' && String(item.relatedId) === String(ticketId)
);

const syncWorkflowTicketWithTicketStatus = (ticketId, nextStatus, extras = {}) => {
  const workflowTicket = findWorkflowTicketByRelatedTicketId(ticketId);
  if (!workflowTicket) return null;
  workflowTicket.status = nextStatus;
  workflowTicket.nodeName = extras.nodeName || workflowTicket.nodeName || nextStatus;
  workflowTicket.updatedAt = extras.updatedAt || new Date().toISOString();
  if (extras.completedAt) workflowTicket.completedAt = extras.completedAt;
  if (extras.reviewer) workflowTicket.reviewer = extras.reviewer;
  if (extras.assignee) workflowTicket.assignee = extras.assignee;
  return workflowTicket;
};

if (!db.queue_projects) {
  db.queue_projects = [
    { id: 'VR01', name: 'VR 赛博战场', queueLength: 15, avgWait: 45, status: 'OPEN', venue: '新天地店' },
    { id: 'KART01', name: '霓虹卡丁车', queueLength: 8, avgWait: 20, status: 'OPEN', venue: '前海店' },
    { id: 'BOWLING', name: '赛博保龄球', queueLength: 3, avgWait: 10, status: 'OPEN', venue: '新天地店' }
  ];
}

if (!db.queue_tickets) {
  db.queue_tickets = [];
}

const formatQueueTime = (value = new Date()) => {
  return new Date(value).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', hour12: false });
};

const getQueueProject = (projectId, projectName) => {
  return db.queue_projects.find(item => item.id === projectId || item.name === projectName);
};

const getQueueProjectSnapshot = (projectId, projectName) => {
  const project = getQueueProject(projectId, projectName);
  if (!project) return null;
  const waitingCount = db.queue_tickets.filter(item => item.projectId === project.id && item.status === '等待中').length;
  return {
    ...project,
    queueLength: waitingCount,
    avgWait: waitingCount > 0 ? waitingCount * 3 : 0,
    canJoin: project.status !== 'PAUSED'
  };
};

const getActiveQueueTicket = (userId) => {
  if (!userId) return null;
  return db.queue_tickets
    .filter(item => item.userId === userId && ['等待中', '已叫号', '请立即入场'].includes(item.status))
    .sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())[0] || null;
};

const buildQueueTicket = ({ userId, projectId, projectName, phone }) => {
  const project = getQueueProject(projectId, projectName);
  if (!project) return null;
  const projectTickets = db.queue_tickets.filter(item => item.projectId === project.id);
  const number = `A${String(projectTickets.length + 101).padStart(3, '0')}`;
  const aheadCount = projectTickets.filter(item => item.status === '等待中').length;
  const estWait = Math.max(project.avgWait, aheadCount * 3 || 3);
  const ticket = {
    id: `queue-${Date.now()}-${Math.random().toString(16).slice(2, 6)}`,
    userId: userId || `guest-${Date.now()}`,
    user: userId || '现场玩家',
    phone: phone || '138****0000',
    projectId: project.id,
    projectName: project.name,
    project: project.name,
    number,
    queueNo: number,
    status: '等待中',
    aheadCount,
    estWait,
    time: formatQueueTime(),
    timestamp: new Date().toISOString()
  };
  db.queue_tickets.unshift(ticket);
  project.queueLength += 1;
  project.avgWait = Math.max(project.avgWait, estWait);
  return ticket;
};

const buildQueueCallPayload = ({ queueNo, number, project, projectName, userId }) => {
  const normalizedQueueNo = queueNo || number;
  const normalizedProject = project || projectName;
  const ticket = normalizedQueueNo
    ? db.queue_tickets.find(item =>
        item.number === normalizedQueueNo &&
        (!normalizedProject || item.projectName === normalizedProject || item.projectId === normalizedProject)
      )
    : db.queue_tickets
        .filter(item =>
          item.status === '等待中' &&
          (!normalizedProject || item.projectName === normalizedProject || item.projectId === normalizedProject)
        )
        .sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime())[0];
  const projectRecord = getQueueProject(ticket?.projectId, normalizedProject);
  if (ticket) {
    ticket.status = '已叫号';
  }
  if (projectRecord && projectRecord.queueLength > 0) {
    projectRecord.queueLength -= 1;
    projectRecord.avgWait = Math.max(0, projectRecord.avgWait - 3);
  }
  return {
    queueNo: ticket?.number || normalizedQueueNo,
    number: ticket?.number || normalizedQueueNo,
    project: ticket?.projectName || normalizedProject,
    projectName: ticket?.projectName || normalizedProject,
    projectId: ticket?.projectId || projectRecord?.id,
    userId: userId || ticket?.userId,
    user: ticket?.user,
    aheadCount: Math.max(0, (ticket?.aheadCount || 1) - 1),
    timestamp: new Date().toISOString()
  };
};

const relaySyncEvent = (data, socket, mode = 'broadcast') => {
  if (data?.type === 'TICKET_STATUS_UPDATED' && data?.payload?.ticketId) {
    const targetTicket = (db.tickets || []).find(item => String(item.id) === String(data.payload.ticketId));
    if (targetTicket) {
      targetTicket.status = data.payload.status || targetTicket.status;
      targetTicket.statusText = data.payload.statusText || targetTicket.statusText;
      targetTicket.assignee = data.payload.assignee || targetTicket.assignee;
      targetTicket.updatedAt = data.payload.updatedAt || data.payload.resolvedAt || targetTicket.updatedAt;
      targetTicket.resolutionNote = data.payload.resolutionNote || targetTicket.resolutionNote;
      if (data.payload.workflowTaskId) targetTicket.workflowTaskId = data.payload.workflowTaskId;
      if (data.payload.workflowStatus) targetTicket.workflowStatus = data.payload.workflowStatus;
    }
    syncWorkflowTicketWithTicketStatus(data.payload.ticketId, data.payload.status === 'PROCESSING' ? 'ASSIGNED' : 'NEW', {
      nodeName: data.payload.status === 'PROCESSING' ? '处理中' : '待接单',
      updatedAt: data.payload.updatedAt,
      assignee: data.payload.assignee,
      reviewer: data.payload.assignee
    });
    if (data.payload.status === 'PROCESSING') {
      appendTicketTimeline(data.payload.ticketId, {
        stage: 'ACCEPTED',
        title: '工单已接单',
        operator: data.payload.assignee || '服务台',
        detail: `${data.payload.assignee || '服务台'} 已开始处理`,
        meta: [
          { label: '处理人', value: data.payload.assignee || '服务台' },
          { label: '状态', value: '已接单' }
        ]
      });
      appendTicketTimeline(data.payload.ticketId, {
        stage: 'PROCESSING',
        title: '工单进入处理中',
        operator: data.payload.assignee || '服务台',
        detail: data.payload.resolutionNote || data.payload.statusText || '处理中',
        meta: [
          { label: '当前处理', value: data.payload.assignee || '服务台' },
          { label: '进度', value: data.payload.statusText || '处理中' }
        ]
      });
    }
  }
  if (data?.type === 'TICKET_RESOLVED' && data?.payload?.ticketId) {
    const targetTicket = (db.tickets || []).find(item => String(item.id) === String(data.payload.ticketId));
    if (targetTicket) {
      targetTicket.status = 'RESOLVED';
      targetTicket.statusText = '已解决';
      targetTicket.assignee = data.payload.assignee || targetTicket.assignee;
      targetTicket.updatedAt = data.payload.resolvedAt || targetTicket.updatedAt;
      targetTicket.resolutionNote = data.payload.resolutionNote || data.payload.message || targetTicket.resolutionNote;
    }
    syncWorkflowTicketWithTicketStatus(data.payload.ticketId, 'RESOLVED', {
      nodeName: '已解决',
      updatedAt: data.payload.resolvedAt,
      completedAt: data.payload.resolvedAt,
      reviewer: data.payload.assignee,
      assignee: data.payload.assignee
    });
    appendTicketTimeline(data.payload.ticketId, {
      stage: 'RESOLVED',
      title: '工单已解决',
      operator: data.payload.assignee || '服务台',
      detail: data.payload.resolutionNote || data.payload.message || '已完成处理',
      meta: [
        { label: '处理人', value: data.payload.assignee || '服务台' },
        { label: '结果', value: '已解决' }
      ]
    });
  }
  if (data?.type === 'TICKET_CLOSED' && data?.payload?.ticketId) {
    const targetTicket = (db.tickets || []).find(item => String(item.id) === String(data.payload.ticketId));
    if (targetTicket) {
      targetTicket.status = 'CLOSED';
      targetTicket.statusText = '已关闭';
      targetTicket.assignee = data.payload.assignee || targetTicket.assignee;
      targetTicket.updatedAt = data.payload.closedAt || targetTicket.updatedAt;
      targetTicket.resolutionNote = data.payload.closeNote || data.payload.message || targetTicket.resolutionNote;
    }
    syncWorkflowTicketWithTicketStatus(data.payload.ticketId, 'CLOSED', {
      nodeName: '已关闭',
      updatedAt: data.payload.closedAt,
      completedAt: data.payload.closedAt,
      reviewer: data.payload.assignee,
      assignee: data.payload.assignee
    });
    appendTicketTimeline(data.payload.ticketId, {
      stage: 'CLOSED',
      title: '工单已关闭',
      operator: data.payload.assignee || '服务台',
      detail: data.payload.closeNote || data.payload.message || '已归档关闭',
      meta: [
        { label: '归档状态', value: '已关闭' },
        { label: '确认人', value: data.payload.assignee || '服务台' }
      ]
    });
  }
  const events = buildSyncEvents(data);

  events.forEach((event) => {
    if (mode === 'broadcast' && socket) {
      socket.broadcast.emit('data_sync', event);
    } else {
      io.emit('data_sync', event);
    }
    io.emit('state_update', event);
  });

  io.emit('notification', createSyncNotification(data));
};

// --- Socket.io ---
io.on('connection', (socket) => {
  console.log('Client connected:', socket.id);
  
  // Send initial venue stats
  socket.emit('venue_stats', db.venues);
  socket.emit('device_stats', db.devices);

  // Cross-port sync generic handler
  socket.on('data_sync', (data) => {
    console.log('Global Sync Event Received:', data);
    relaySyncEvent(data, socket, 'broadcast');
  });

  socket.on('cross_end_event', (data) => {
    console.log('Cross End Event Received:', data);
    relaySyncEvent(data, socket, 'all');
  });

  socket.on('sync_action', (data) => {
    console.log('Sync Action Received:', data);
    relaySyncEvent(data, socket, 'all');
  });

  socket.on('disconnect', () => {
    console.log('Client disconnected:', socket.id);
  });
  
  // Device Control
  socket.on('control_device', ({ deviceId, action }) => {
    const device = db.devices.find(d => d.id === deviceId);
    if (device) {
      if (action === 'open') device.status = 'OPEN';
      if (action === 'close') device.status = 'ONLINE';
      
      // Broadcast updated stats
      io.emit('device_stats', db.devices);
      
      io.emit('notification', {
        id: Date.now().toString(),
        title: 'Device Status Changed',
        content: `${device.name} is now ${device.status}`,
        type: 'info'
      });
    }
  });

  // Task Handoff
  socket.on('request_handoff', (data) => {
    console.log('Handoff requested:', data);
    // Broadcast to all clients (simplification)
    io.emit('task_handoff', {
      ...data,
      status: 'pending',
      timestamp: new Date().toISOString()
    });
    
    // Create a notification for the target device
    const notification = {
      id: Date.now().toString(),
      title: 'Task Handoff Received',
      content: `Incoming task from ${data.targetDevice === 'mobile' ? 'PC' : 'Mobile'}`,
      type: 'task',
      timestamp: new Date().toISOString()
    };
    io.emit('notification', notification);
  });
});

// --- REST API ---

// Venues
app.get('/api/venues', (req, res) => {
  res.json(db.venues);
});

app.get('/api/venues/:city', (req, res) => {
  const venue = db.venues.find(v => v.city === req.params.city);
  if (venue) {
    res.json(venue);
  } else {
    // Default fallback for other 100 cities
    res.json({ id: `v-${req.params.city}`, name: `${req.params.city} Center`, city: req.params.city, capacity: 20, booked: 0 });
  }
});

// Auth
app.post('/api/auth/login', (req, res) => {
  const { username, password } = req.body;
  const user = db.users.find(u => u.username === username);
  if (user) {
    res.json({
      token: user.token,
      user: {
        id: user.id,
        username: user.username,
        roles: [`ROLE_${user.role}`]
      }
    });
  } else {
    res.status(401).json({ message: 'Invalid credentials' });
  }
});

app.get('/api/user/profile', (req, res) => {
  // Mock profile return
  res.json({
    id: '1',
    username: 'admin',
    email: 'admin@sportsant.com',
    roles: ['ROLE_ADMIN']
  });
});

// Business Leads (from Business Site)
app.post('/api/contact/lead', (req, res) => {
  const lead = { id: Date.now(), ...req.body, status: 'NEW', createdAt: new Date() };
  db.leads.push(lead);
  console.log('New Lead:', lead);
  
  // Notify Admin
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New B2B Lead',
    content: `${lead.name} from ${lead.company} is interested.`,
    type: 'alert',
    actionUrl: '/crm/leads'
  });
  
  res.json({ success: true, id: lead.id });
});

app.get('/api/leads', (req, res) => {
  res.json(db.leads);
});

// Player Bookings (from Player Site / UniApp)
app.post('/api/crowdfunding/success', (req, res) => {
  const { projectId, target, reward } = req.body;
  
  io.emit('data_sync', {
    type: 'CROWDFUNDING_MILESTONE_REACHED',
    payload: { projectId, target, reward, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Crowdfunding Success',
    content: `${projectId} reached ${target} backers.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/aigc/push_feed', (req, res) => {
  const { author, content, tags, videoUrl } = req.body;
  
  io.emit('data_sync', {
    type: 'AIGC_CONTENT_BROADCAST',
    payload: { author, content, tags, videoUrl, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'AIGC Feed Pushed',
    content: `New AIGC post by ${author}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/competitor/counter-strategy', (req, res) => {
  const { competitor, priceDrop } = req.body;
  
  io.emit('data_sync', {
    type: 'COMPETITOR_COUNTER_STRATEGY',
    payload: { competitor, priceDrop, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Competitor Defense Active',
    content: `Matched price drop from ${competitor}. Issued subsidies.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/fleet/shuttle-arrive', (req, res) => {
  const { userId, shuttleId } = req.body;
  
  io.emit('data_sync', {
    type: 'VIP_SHUTTLE_ARRIVED',
    payload: { userId, shuttleId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Shuttle Arrived',
    content: `${shuttleId} arrived with ${userId}.`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/arcade/jackpot', (req, res) => {
  const { userId, machineId, tickets } = req.body;
  
  io.emit('data_sync', {
    type: 'ARCADE_JACKPOT_WIN',
    payload: { userId, machineId, tickets, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: '🎰 JACKPOT WIN',
    content: `${userId} won ${tickets} tickets on ${machineId}!`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/iot/vr/overheat', (req, res) => {
  const { deviceId, temp } = req.body;
  
  io.emit('data_sync', {
    type: 'DEVICE_OVERHEAT_WARNING',
    payload: { deviceId, temp, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'IoT Alert: Overheat',
    content: `${deviceId} reached ${temp}C. Pause and swap required.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/compliance/gdpr/forget', (req, res) => {
  const { userId } = req.body;
  
  io.emit('data_sync', {
    type: 'GDPR_ACCOUNT_DELETED',
    payload: { userId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'GDPR Action',
    content: `Right to be forgotten executed for ${userId}.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/alliance/airdrop', (req, res) => {
  const { partner, userId, coupon } = req.body;
  
  io.emit('data_sync', {
    type: 'ALLIANCE_AIRDROP_RECEIVED',
    payload: { partner, userId, coupon, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Alliance Airdrop',
    content: `${partner} sent ${coupon} to ${userId} via Open API.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/health/milestone', (req, res) => {
  const { userId, calories } = req.body;
  
  io.emit('data_sync', {
    type: 'CALORIE_MILESTONE_REACHED',
    payload: { userId, calories, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Health Milestone',
    content: `${userId} burned ${calories} kcal. Dropping sponsor ad.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/fnb/orders', (req, res) => {
  const { tableNo, items, item, qty, total, useRobot, userId } = req.body;
  const orderId = 'FNB' + Date.now();
  const normalizedItems = Array.isArray(items) && items.length
    ? items
    : [{ name: item || '未知餐品', quantity: qty || 1 }];
  
  io.emit('data_sync', {
    type: 'FNB_ORDER_PLACED',
    payload: {
      orderId,
      tableNo,
      items: normalizedItems,
      item: normalizedItems[0]?.name || item || '未知餐品',
      qty: normalizedItems[0]?.quantity || qty || 1,
      total,
      useRobot,
      timestamp: new Date().toISOString()
    }
  });
  
  if (useRobot) {
    setTimeout(() => {
      io.emit('data_sync', {
        type: 'ROBOT_DELIVERY_DISPATCHED',
        payload: { userId, tableNo, timestamp: new Date().toISOString() }
      });
      io.emit('notification', {
        id: Date.now().toString(),
        title: 'Robot Dispatched',
        content: `Robot-01 delivering F&B to ${tableNo}.`,
        type: 'info'
      });
    }, 5000);
  }

  res.json({ success: true, orderId });
});

app.post('/api/booking', (req, res) => {
  const { city } = req.body;
  const booking = { id: Date.now(), ...req.body, status: 'CONFIRMED', createdAt: new Date() };
  db.bookings.push(booking);
  const { serviceTicket, workflowTicket } = createBookingServiceTicket({
    userId: req.body.userId || req.body.user || 'Player_Mobile_01',
    storeId: city || req.body.storeId || 'neo-city',
    gameId: req.body.gameId || req.body.game || 'vr-battle',
    date: req.body.date || '',
    time: req.body.time || '',
    type: req.body.type || 'single'
  });
  
  // Update Capacity
  const venue = db.venues.find(v => v.city === city);
  if (venue) {
    venue.booked++;
    // Broadcast new stats
    io.emit('venue_stats', db.venues);
  } else if (city) {
    // Dynamically add venue if not exists (for mock purposes)
    db.venues.push({ id: `v-${city}`, name: `${city} Center`, city, capacity: 20, booked: 1 });
    io.emit('venue_stats', db.venues);
  }

  console.log('New Booking:', booking);

  // Notify Admin & User
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Booking',
    content: `${booking.game || 'VR Session'} booked by ${booking.user || 'Guest'} in ${city || 'Unknown City'}`,
    type: 'order',
    actionUrl: '/booking'
  });

  res.json({
    success: true,
    id: booking.id,
    serviceTicketId: serviceTicket.id,
    workflowTaskId: workflowTicket.id
  });
});

app.get('/api/bookings', (req, res) => {
  res.json(db.bookings);
});

// Tasks (for Employee App)
app.post('/api/web3/did/verify', (req, res) => {
  const { userId, nftId, walletAddress } = req.body;
  const txHash = `0x${Math.random().toString(16).slice(2, 18)}${Date.now().toString(16)}`
  
  io.emit('data_sync', {
    type: 'WEB3_NFT_VERIFIED',
    payload: { userId, nftId, walletAddress, txHash, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Web3 DID Verified',
    content: `${userId} verified NFT ${nftId}. Unlocking special zone.`,
    type: 'success'
  });

  res.json({ success: true, data: { userId, nftId, walletAddress, txHash } });
});

app.post('/api/supply/inventory-discrepancy', (req, res) => {
  const { assetId, expected, actual } = req.body;
  
  io.emit('data_sync', {
    type: 'INVENTORY_DISCREPANCY_ALERT',
    payload: { assetId, expected, actual, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Inventory Alert',
    content: `Discrepancy for ${assetId}: expected ${expected}, got ${actual}.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/alliance/airdrop', (req, res) => {
  const { partner, userId, coupon } = req.body;
  
  io.emit('data_sync', {
    type: 'ALLIANCE_VOUCHER_DROP',
    payload: { partner, userId, coupon, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Alliance Airdrop',
    content: `${partner} sent ${coupon} to ${userId}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/event/auto-checkin', (req, res) => {
  const { eventName, userId } = req.body;
  
  io.emit('data_sync', {
    type: 'EVENT_AUTO_CHECKIN',
    payload: { eventName, userId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Auto Check-in',
    content: `${userId} checked into ${eventName}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/event/verify-ticket', (req, res) => {
  const { eventId, eventName, ticketCode, userId, storeName } = req.body;
  const payload = {
    eventId,
    eventName,
    ticketCode,
    userId: userId || 'Player_VIP_01',
    storeName: storeName || '新天地店',
    timestamp: new Date().toISOString()
  };

  db.verification_logs.unshift({
    ...payload,
    verifiedAt: payload.timestamp
  });

  io.emit('data_sync', {
    type: 'EVENT_TICKET_VERIFIED',
    payload
  });

  io.emit('data_sync', {
    type: 'EVENT_AUTO_CHECKIN',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Ticket Verified',
    content: `${payload.userId} verified ticket ${ticketCode} for ${eventName}.`,
    type: 'success'
  });

  res.json({ success: true, data: payload });
});

app.get('/api/event/verification-logs', (req, res) => {
  const { eventId, eventName } = req.query;
  let logs = db.verification_logs || [];
  if (eventId || eventName) {
    logs = logs.filter(log =>
      (eventId && String(log.eventId) === String(eventId)) ||
      (eventName && log.eventName === eventName)
    );
  }
  res.json({ success: true, data: logs });
});

app.post('/api/print/cloud-job', (req, res) => {
  const { documentName, printerId } = req.body;
  
  io.emit('data_sync', {
    type: 'CLOUD_PRINT_JOB_STARTED',
    payload: { documentName, printerId, status: 'PRINTING', timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Print Job Started',
    content: `Printing ${documentName} on ${printerId}.`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/system/monitor/cpu-alert', (req, res) => {
  io.emit('data_sync', {
    type: 'SERVER_CPU_CRITICAL_ALERT',
    payload: { cpu: '98%', memory: '95%', timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Server Critical Alert',
    content: `CPU usage at 98%. Immediate action required.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/saas/tenant/freeze', (req, res) => {
  const { tenantId, reason } = req.body;
  
  io.emit('data_sync', {
    type: 'SAAS_TENANT_FROZEN',
    payload: { tenantId, reason, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Tenant Frozen',
    content: `${tenantId} frozen due to ${reason}.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/queue/call', (req, res) => {
  const payload = buildQueueCallPayload(req.body || {});
  
  io.emit('data_sync', {
    type: 'SMART_QUEUE_CALL',
    payload
  });

  io.emit('data_sync', {
    type: 'QUEUE_CALLED',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Queue Call',
    content: `Calling ${payload.queueNo} to ${payload.project}.`,
    type: 'info'
  });

  res.json({ success: true, data: payload });
});

app.post('/api/marketing/staff-conversion', (req, res) => {
  const { staffId, staffName, amount, commission, points } = req.body;
  
  io.emit('data_sync', {
    type: 'MARKETING_STAFF_CONVERSION',
    payload: { staffId, staffName, amount, commission, points, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Conversion',
    content: `${staffName} generated ¥${amount} in revenue!`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/marketing/rules/update', (req, res) => {
  io.emit('data_sync', {
    type: 'MARKETING_RULES_UPDATED',
    payload: { rules: req.body, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Rules Updated',
    content: `Marketing rules updated successfully.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/marketing/team-pk/start', (req, res) => {
  io.emit('data_sync', {
    type: 'MARKETING_TEAM_PK_STARTED',
    payload: { ...req.body, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Team PK Started',
    content: `Team PK ${req.body.title} has started.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/marketing/secondary-fission', (req, res) => {
  const { parentStaffId, subPromoterName, amount, secondaryCommission } = req.body;
  
  io.emit('data_sync', {
    type: 'MARKETING_SECONDARY_FISSION',
    payload: { parentStaffId, subPromoterName, amount, secondaryCommission, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Secondary Fission',
    content: `Passive income from ${subPromoterName}`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/marketing/push-material', (req, res) => {
  io.emit('data_sync', {
    type: 'MARKETING_DAILY_MATERIAL',
    payload: { ...req.body, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Material Pushed',
    content: `Daily marketing material pushed to staff.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/marketing/withdraw', (req, res) => {
  const record = createFinanceRequest({
    requestId: req.body?.requestId,
    requestType: 'WITHDRAWAL',
    businessType: 'KOC_COMMISSION',
    employeeId: req.body?.staffId || req.body?.employeeId || 'EMP_001',
    employeeName: req.body?.staffName || req.body?.employeeName || '姚云忠',
    amount: Number(req.body?.amount || 0),
    desc: `${req.body?.channel || '微信零钱'} 提现申请`,
    channel: req.body?.channel || '微信零钱',
    account: '微信零钱账户 · 8848',
    voucherName: 'withdrawal-proof.png',
    voucherType: 'png',
    voucherUrl: 'https://finance.sportsant.cn/voucher/withdrawal-proof.png',
    voucherRemark: '等待风控审核'
  });

  relaySyncEvent({
    type: 'WITHDRAWAL_REQUESTED',
    payload: {
      requestId: record.requestId,
      employeeId: record.employeeId,
      employeeName: record.employeeName,
      amount: record.amount,
      channel: record.voucher.channel,
      account: record.voucher.account,
      remark: record.voucher.voucherRemark,
      timestamp: record.createdAt
    }
  });

  res.json({ success: true, status: 'PENDING_AUDIT', data: record });
});

app.post('/api/marketing/fraud-alert', (req, res) => {
  const { staffName, amount, reason } = req.body;
  
  io.emit('data_sync', {
    type: 'MARKETING_FRAUD_ALERT',
    payload: { staffName, amount, reason, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Fraud Alert',
    content: `Suspicious withdrawal blocked for ${staffName}`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/marketing/all-staff-task', (req, res) => {
  const { taskName, reward } = req.body;
  
  io.emit('data_sync', {
    type: 'ALL_STAFF_MARKETING_TASK',
    payload: { taskName, reward, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Marketing Task',
    content: `${taskName} task distributed.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/scheduler/trigger-clearing', (req, res) => {
  io.emit('data_sync', {
    type: 'MIDNIGHT_BILL_CLEARING',
    payload: { month: '2026-03', amount: 398, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Job Triggered',
    content: `Midnight clearing job triggered successfully.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/open/rate-limit', (req, res) => {
  const { merchantId, limit, current } = req.body;
  
  io.emit('data_sync', {
    type: 'API_RATE_LIMIT_EXCEEDED',
    payload: { merchantId, limit, current, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'API Rate Limit',
    content: `${merchantId} exceeded API rate limit.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/social/content/publish', (req, res) => {
  const { title, requireForward, platforms } = req.body;
  
  if (requireForward) {
    io.emit('data_sync', {
      type: 'SOCIAL_MATRIX_FORWARD_TASK',
      payload: { title, platforms, timestamp: new Date().toISOString() }
    });
  }

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Content Published',
    content: `${title} published to ${platforms}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/hr/schedules/publish', (req, res) => {
  // First route matching this, ensure it emits both to cover both components
  io.emit('data_sync', {
    type: 'SMART_SCHEDULE_PUBLISHED',
    payload: { week: 'Next Week', timestamp: new Date().toISOString() }
  });

  io.emit('data_sync', {
    type: 'SCHEDULE_PUBLISHED',
    payload: {
      message: '店长已发布下周排班表。请进入排班模块查看详情，如有异议请及时申请调班。',
      date: new Date().toISOString()
    }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Schedule Published',
    content: 'Next week schedule has been published to all employees.',
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/fnb/order-ready', (req, res) => {
  const { orderId, tableNo, useRobot, userId } = req.body;
  
  io.emit('data_sync', {
    type: 'FNB_ORDER_READY',
    payload: { orderId, tableNo, useRobot, userId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Order Ready',
    content: `Order ${orderId} is ready for ${tableNo}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/renovation/noise-warning', (req, res) => {
  const { area, duration, compensation } = req.body;
  
  io.emit('data_sync', {
    type: 'RENOVATION_NOISE_WARNING',
    payload: { area, duration, compensation, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Renovation Noise',
    content: `Noise in ${area} for ${duration}.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/iot/desk/power', (req, res) => {
  const { deskId, userId } = req.body;
  
  io.emit('data_sync', {
    type: 'DESK_POWER_ACTIVATED',
    payload: { deskId, userId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Desk Power ON',
    content: `${deskId} activated for ${userId}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/finance/invoice/issue', (req, res) => {
  const { userId, amount, title } = req.body;
  const invoiceNo = 'INV-' + Date.now();
  
  io.emit('data_sync', {
    type: 'E_INVOICE_ISSUED',
    payload: { userId, amount, title, invoiceNo, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Invoice Issued',
    content: `${invoiceNo} for ¥${amount} issued to ${userId}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/inspection/ai-defect', (req, res) => {
  const { location, defectType, severity } = req.body;
  
  io.emit('data_sync', {
    type: 'AI_CAMERA_INSPECTION_DEFECT',
    payload: { location, defectType, severity, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'AI Camera Defect',
    content: `${defectType} detected at ${location}.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/internal/broadcast', (req, res) => {
  const { message, level } = req.body;
  
  io.emit('data_sync', {
    type: 'EMPLOYEE_URGENT_BROADCAST',
    payload: { message, level, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Internal Broadcast',
    content: message,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/lostfound/register', (req, res) => {
  const { itemName, location, storage, finder } = req.body;
  const payload = {
    itemName,
    location,
    storage: storage || '前台 01号储物箱',
    finder: finder || '前台服务台',
    matchedUser: 'Player_Mobile_01',
    timestamp: new Date().toISOString()
  };
  
  io.emit('data_sync', {
    type: 'LOST_FOUND_REGISTERED',
    payload
  });

  io.emit('data_sync', {
    type: 'LOST_FOUND_AI_MATCH',
    payload
  });

  io.emit('data_sync', {
    type: 'ITEM_FOUND_AI_MATCH',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Lost Item AI Match',
    content: `Found ${itemName} at ${location}. Matched to Player_Mobile_01.`,
    type: 'success'
  });

  res.json({ success: true, data: payload });
});

app.post('/api/iot/esl/sync', (req, res) => {
  const { product, oldPrice, newPrice, shelfId } = req.body;
  
  io.emit('data_sync', {
    type: 'ESL_PRICE_SYNC',
    payload: { product, oldPrice, newPrice, shelfId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'ESL Updated',
    content: `${product} price updated to ${newPrice} on ${shelfId}.`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/seo/auto-optimize', (req, res) => {
  const { keywords, tdkScore } = req.body;
  
  io.emit('data_sync', {
    type: 'SEO_AUTO_OPTIMIZATION_COMPLETED',
    payload: { keywords, tdkScore, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'SEO Optimized',
    content: `Keywords updated. TDK Score: ${tdkScore}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/supply/supplier-penalty', (req, res) => {
  const { supplierId, reason, penalty } = req.body;
  
  io.emit('data_sync', {
    type: 'SUPPLIER_PERFORMANCE_PENALTY',
    payload: { supplierId, reason, penalty, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Supplier Penalty',
    content: `${supplierId} penalized: ${penalty}.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/supply/drone-delivery', (req, res) => {
  const { item, droneId, location } = req.body;
  
  io.emit('data_sync', {
    type: 'DRONE_DELIVERY_ARRIVED',
    payload: { item, droneId, location, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Supply Chain Alert',
    content: `${droneId} arrived with ${item} at ${location}.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/esports/obs-hook', (req, res) => {
  const { userId, eventName, platform } = req.body;
  
  io.emit('data_sync', {
    type: 'ESPORTS_LIVE_STREAM_STARTED',
    payload: { userId, eventName, platform, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'OBS Hook Active',
    content: `${userId} is streaming ${eventName} to ${platform}.`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/esg/vpp-trade', (req, res) => {
  const { powerReduced, income } = req.body;
  
  io.emit('data_sync', {
    type: 'VPP_TRADE_EXECUTED',
    payload: { powerReduced, income, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'ESG VPP Trade',
    content: `Reduced ${powerReduced}kW, earned ¥${income}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/safety/fire-emergency', (req, res) => {
  io.emit('data_sync', {
    type: 'FIRE_EMERGENCY_EVACUATION',
    payload: { timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: '🚨 FIRE ALARM 🚨',
    content: `Evacuate immediately! All screens overridden.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/system/rollback', (req, res) => {
  io.emit('data_sync', {
    type: 'DISASTER_RECOVERY_ROLLBACK',
    payload: { timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'System Rollback Initiated',
    content: `Data restoring to previous snapshot.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/sop/ada', (req, res) => {
  io.emit('data_sync', {
    type: 'ADA_ACCESSIBILITY_MODE',
    payload: { timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'ADA Mode Active',
    content: `Accessibility features enabled across venue.`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/sop/ar-nav', (req, res) => {
  io.emit('data_sync', {
    type: 'AR_NAV_DISPATCH',
    payload: { targetUser: 'Player_VIP_01', location: 'VR Arena A', timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'AR Nav Dispatch',
    content: `Locate user Player_VIP_01 at VR Arena A.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/franchise/koc-invite', (req, res) => {
  const { storeName, budget } = req.body;
  
  io.emit('data_sync', {
    type: 'FRANCHISE_KOC_INVITE',
    payload: { storeName, budget, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'KOC Invites Sent',
    content: `Invites sent for ${storeName} with budget ${budget}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/system/export', (req, res) => {
  const { name, type, filters = {} } = req.body;
  const taskId = 'EXP-' + Date.now();
  const task = {
    id: taskId,
    taskName: name || `Export ${type}`,
    type,
    status: 'PROCESSING',
    createdAt: new Date().toISOString(),
    filters
  };
  db.export_tasks.unshift(task);

  io.emit('data_sync', {
    type: 'EXPORT_TASK_UPDATE',
    payload: task
  });

  setTimeout(() => {
    let csvRows = [['字段', '值']];
    if (type === 'VERIFICATION_LOG') {
      const logs = (db.verification_logs || []).filter(log => {
        if (filters.eventId && String(log.eventId) !== String(filters.eventId)) return false;
        if (filters.eventName && log.eventName !== filters.eventName) return false;
        if (filters.storeName && (log.storeName || '新天地店') !== filters.storeName) return false;
        const verifiedAt = new Date(log.verifiedAt || log.timestamp || Date.now()).getTime();
        if (filters.verifiedFrom) {
          const from = new Date(
            String(filters.verifiedFrom).length === 10
              ? `${filters.verifiedFrom}T00:00:00.000Z`
              : filters.verifiedFrom
          ).getTime();
          if (!Number.isNaN(from) && verifiedAt < from) return false;
        }
        if (filters.verifiedTo) {
          const to = new Date(
            String(filters.verifiedTo).length === 10
              ? `${filters.verifiedTo}T23:59:59.999Z`
              : filters.verifiedTo
          ).getTime();
          if (!Number.isNaN(to) && verifiedAt > to) return false;
        }
        return true;
      });
      csvRows = [
        ['票码', '活动', '门店', '用户', '核销时间'],
        ...logs.map(log => [log.ticketCode, log.eventName, log.storeName || '新天地店', log.userId || 'Player_VIP_01', log.verifiedAt || log.timestamp])
      ];
      task.total = logs.length;
    }
    task.status = 'COMPLETED';
    task.completedAt = new Date().toISOString();
    task.downloadUrl = `http://localhost:8080/api/system/export/${taskId}/download`;
    task.fileContent = `\uFEFF${csvRows.map(row => row.join(',')).join('\n')}`;
    io.emit('data_sync', {
      type: 'EXPORT_TASK_UPDATE',
      payload: task
    });
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Data Export Ready',
      content: `Your export task "${task.taskName}" is ready to download.`,
      type: 'success'
    });
  }, 3000);

  res.json({ success: true, taskId, data: task });
});

app.get('/api/system/export-tasks', (req, res) => {
  res.json({ success: true, data: db.export_tasks.map(task => ({ ...task, fileContent: undefined })) });
});

app.get('/api/system/export/:taskId/download', (req, res) => {
  const task = db.export_tasks.find(item => item.id === req.params.taskId);
  if (!task || !task.fileContent) {
    return res.status(404).send('Export not ready');
  }
  res.setHeader('Content-Type', 'text/csv; charset=utf-8');
  res.setHeader('Content-Disposition', `attachment; filename="${task.id}.csv"`);
  res.send(task.fileContent);
});

app.post('/api/push/geo-fence', (req, res) => {
  const { userId, distance } = req.body;
  
  io.emit('data_sync', {
    type: 'GEO_FENCE_PUSH_NOTIFICATION',
    payload: { userId, distance, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'LBS Push Sent',
    content: `Sent push to ${userId} within ${distance}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/system/lcms/override', (req, res) => {
  const { language } = req.body;
  
  io.emit('data_sync', {
    type: 'LCMS_GLOBAL_LANGUAGE_OVERRIDE',
    payload: { language, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'LCMS Update',
    content: `Global language override to ${language}.`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/rental/overdue-lock', (req, res) => {
  const { userId, deviceId } = req.body;
  const targetRental = Array.isArray(db.rentals)
    ? db.rentals.find(item => String(item.userId) === String(userId) && String(item.deviceId) === String(deviceId) && item.status !== 'RETURNED')
    : null;
  if (targetRental) {
    targetRental.status = 'OVERDUE';
    targetRental.updatedAt = new Date().toISOString();
  }
  if (!db.workflow_tickets) {
    db.workflow_tickets = [];
  }
  const workflowTask = {
    id: `W-${Date.now()}`,
    type: 'RENTAL_OVERDUE',
    processName: '租赁超时协同',
    title: `租赁超时 · ${targetRental?.deviceName || deviceId}`,
    priority: 'HIGH',
    status: 'NEW',
    starter: userId || 'Player_Mobile_01',
    nodeName: '待处理',
    relatedId: targetRental?.id || deviceId,
    relatedType: 'RENTAL_OVERDUE',
    createdAt: new Date().toISOString(),
    externalWorkspace: 'business',
    externalPath: `/rental-manager?orderId=${encodeURIComponent(targetRental?.id || '')}`,
    assignee: '5178 租赁运营台'
  };
  db.workflow_tickets.unshift(workflowTask);
  if (targetRental) {
    targetRental.workflowTaskId = workflowTask.id;
  }
  
  io.emit('data_sync', {
    type: 'RENTAL_DEVICE_OVERDUE_LOCK',
    payload: {
      userId,
      deviceId,
      orderId: targetRental?.id || '',
      pickupCode: targetRental?.pickupCode || '',
      workflowTaskId: workflowTask.id,
      timestamp: new Date().toISOString()
    }
  });
  io.emit('data_sync', {
    type: 'WORKFLOW_TASK_CREATED',
    payload: workflowTask
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Rental Overdue',
    content: `Device ${deviceId} locked for user ${userId}.`,
    type: 'warning'
  });

  res.json({ success: true, data: { workflowTaskId: workflowTask.id, orderId: targetRental?.id || '' } });
});

app.post('/api/legal/esign-contract', (req, res) => {
  const { contractId, partyB } = req.body;
  const hash = '0x' + Math.random().toString(16).substr(2, 40);
  
  io.emit('data_sync', {
    type: 'ELECTRONIC_CONTRACT_SIGNED',
    payload: { contractId, partyB, hash, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'e-Contract Signed',
    content: `${contractId} signed. Hash: ${hash.substring(0,10)}...`,
    type: 'success'
  });

  res.json({ success: true, hash });
});

app.post('/api/system/audit/threat', (req, res) => {
  io.emit('data_sync', {
    type: 'SYSTEM_AUDIT_THREAT_LOCKOUT',
    payload: { timestamp: new Date().toISOString(), level: 'CRITICAL', source: 'Multiple failed login attempts from unknown IP' }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'SECURITY THREAT',
    content: `Critical security breach detected. Initiating global lockout.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.get('/api/tasks', (req, res) => {
  res.json(db.tasks);
});

// Devices (for Admin/Business)
app.get('/api/devices', (req, res) => {
  res.json(db.devices);
});

// Campaigns (Marketing)
app.get('/api/campaigns', (req, res) => {
  res.json(db.campaigns);
});

app.post('/api/campaigns', (req, res) => {
  const campaign = { id: `c${Date.now()}`, ...req.body, status: 'DRAFT', reachCount: 0, conversionCount: 0, roi: 0 };
  db.campaigns.push(campaign);
  res.json({ success: true, campaign });
});

app.post('/api/marketing/campaigns/:id/launch', (req, res) => {
  const campaign = db.campaigns.find(c => c.id === req.params.id);
  if (campaign) {
    campaign.status = 'ACTIVE';
    // Notify
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Campaign Launched',
      content: `Campaign ${campaign.name} is now LIVE!`,
      type: 'marketing'
    });
    res.json({ success: true, campaign });
  } else {
    res.status(404).json({ success: false });
  }
});

app.post('/api/marketing/campaigns/:id/ai-content', (req, res) => {
  const campaign = db.campaigns.find(c => c.id === req.params.id);
  if (campaign) {
    campaign.aiGeneratedContent = JSON.stringify({
      wechat_title: `Exclusive for you: ${campaign.name}`,
      sms_body: `Hi! Don't miss ${campaign.name} at Sports Ant. Use code ${campaign.code || 'VIP'}!`,
      email_subject: `You're invited to ${campaign.name}`
    });
    res.json({ success: true, aiGeneratedContent: campaign.aiGeneratedContent });
  } else {
    res.status(404).json({ success: false });
  }
});

// Tickets (User)
app.get('/api/tickets', (req, res) => {
  // In real app, filter by userId from token
  res.json(db.tickets);
});

// Ticket Verification (Employee)
app.post('/api/ticket/verify', (req, res) => {
  const { code } = req.body;
  const ticket = db.tickets.find(t => t.qrCode === code);
  
  if (!ticket) {
    return res.status(404).json({ success: false, message: 'Invalid Ticket' });
  }
  
  if (ticket.status === 'USED') {
    return res.status(400).json({ success: false, message: 'Ticket Already Used' });
  }
  
  ticket.status = 'USED';
  ticket.usedAt = new Date().toISOString();
  
  // Notify User (if connected)
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Ticket Used',
    content: `Your ticket for ${ticket.game} has been verified. Enjoy!`,
    type: 'system'
  });
  
  res.json({ success: true, ticket });
});

// Orders
app.post('/api/orders', (req, res) => {
  const order = { id: `ORD-${Date.now()}`, ...req.body, status: 'PAID', date: new Date().toISOString() };
  if (!db.orders) db.orders = [];
  db.orders.unshift(order);
  
  // Push to player's mobile app
  io.emit('data_sync', {
    type: 'NEW_ORDER_CREATED',
    payload: order
  });
  
  // Detect Risk Anomaly (e.g. rapid multiple orders)
  const recentOrders = db.orders.filter(o => o.userId === req.body.userId && (Date.now() - new Date(o.date).getTime()) < 60000);
  if (recentOrders.length >= 3) {
    const riskAlert = {
      id: `RSK-${Date.now()}`,
      type: 'FRAUD',
      level: 'HIGH',
      message: `Anomalous purchasing pattern detected for user ${req.body.userId}. 3+ orders in 1 minute.`,
      status: 'OPEN',
      timestamp: new Date().toISOString()
    };
    io.emit('data_sync', { type: 'RISK_ALERT', payload: riskAlert });
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Risk Control Alert',
      content: riskAlert.message,
      type: 'error'
    });
  }
  
  // Deduct inventory if it's a merchandise order (like Gacha)
  if (order.items && order.items[0] && order.items[0].name.includes('Gacha')) {
    const invItem = db.inventory.find(i => i.sku === 'MERCH-GACHA');
    if (invItem) {
      // Calculate quantity based on order description or amount
      const match = order.items[0].name.match(/x(\d+)/);
      const qty = match ? parseInt(match[1]) : 1;
      invItem.stock = Math.max(0, invItem.stock - qty);
      if (invItem.stock < 50) invItem.status = 'LOW_STOCK';
      
      // Notify Admin of inventory change
      io.emit('data_sync', { type: 'INVENTORY_UPDATE', payload: db.inventory });
      
      if (invItem.stock < 20) {
        io.emit('notification', {
          id: Date.now().toString(),
          title: 'Inventory Alert',
          content: `${invItem.name} stock is critically low (${invItem.stock} left).`,
          type: 'warning'
        });
      }
    }
  }
  
  // Real-time notify Admin
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Order Received',
    content: `Order ${order.id} for ¥${order.amount}`,
    type: 'success'
  });
  
  io.emit('data_sync', { type: 'ORDER_CREATED', payload: order });
  
  res.json({ success: true, order });
});

// Analytics (Admin/Business)
app.get('/api/analytics', (req, res) => {
  res.json(db.analytics);
});

app.get('/api/finance/transactions', (req, res) => {
  res.json(db.transactions);
});

app.get('/api/finance/overview', (req, res) => {
  res.json({ success: true, data: getFinanceOverview() });
});

app.get('/api/finance/queue', (req, res) => {
  res.json({ success: true, data: getFinanceOverview().queue });
});

app.post('/api/finance/reimbursements', (req, res) => {
  const record = createFinanceRequest({
    requestId: req.body?.requestId,
    requestType: 'REIMBURSEMENT',
    businessType: req.body?.businessType || 'STAFF_REIMBURSEMENT',
    employeeId: req.body?.employeeId || 'EMP_001',
    employeeName: req.body?.employeeName || '姚云忠',
    amount: Number(req.body?.amount || 0),
    desc: `${req.body?.category || '报销申请'} · ${req.body?.desc || ''}`,
    channel: '企业对公报销',
    account: '招商银行 · 4021',
    voucherName: 'reimbursement-proof.pdf',
    voucherType: 'pdf',
    voucherUrl: 'https://finance.sportsant.cn/voucher/reimbursement-proof.pdf',
    voucherRemark: '等待财务复核',
    meta: {
      category: req.body?.category || '门店采购',
      receiptNo: req.body?.receiptNo || ''
    }
  });

  relaySyncEvent({
    type: 'REIMBURSEMENT_REQUESTED',
    payload: {
      requestId: record.requestId,
      employeeId: record.employeeId,
      employeeName: record.employeeName,
      amount: record.amount,
      category: req.body?.category || '门店采购',
      receiptNo: req.body?.receiptNo || '',
      desc: req.body?.desc || '报销申请',
      timestamp: record.createdAt
    }
  });

  res.json({ success: true, data: record });
});

app.post('/api/finance/requests/:requestId/review', (req, res) => {
  const record = getFinanceRequest(req.params.requestId);
  if (!record) {
    return res.status(404).json({ success: false, message: 'Finance request not found' });
  }
  const status = req.body?.status === 'REJECTED' ? 'REJECTED' : 'APPROVED';
  const reviewed = syncFinanceRequestStatus(record, status, {
    approver: req.body?.approver || '财务中心',
    operatorName: req.body?.operatorName || '王财务',
    approvedAt: new Date().toISOString(),
    paymentSerialNo: status === 'APPROVED' ? `FIN-${Date.now().toString().slice(-8)}` : '',
    voucherRemark: status === 'APPROVED' ? '已进入财务处理流程' : '',
    failureReason: status === 'REJECTED' ? '票据待补充，请修改后重提' : '',
    remark: status === 'APPROVED' ? '已进入财务处理流程' : '票据待补充，请修改后重提'
  });
  const eventType = reviewed.requestType === 'WITHDRAWAL'
    ? 'WITHDRAWAL_RESULT'
    : reviewed.requestType === 'REIMBURSEMENT'
      ? 'REIMBURSEMENT_RESULT'
      : 'SETTLEMENT_RESULT';
  relaySyncEvent({
    type: eventType,
    payload: {
      employeeId: reviewed.employeeId,
      employeeName: reviewed.employeeName,
      requestId: reviewed.requestId,
      amount: reviewed.amount,
      status,
      resultText: status === 'APPROVED' ? '已通过' : '已驳回',
      approver: '财务中心',
      approvedAt: reviewed.updatedAt,
      remark: status === 'APPROVED' ? '已进入财务处理流程' : '票据待补充，请修改后重提',
      paymentChannel: reviewed.voucher.channel,
      payeeAccountMasked: reviewed.voucher.account,
      paymentSerialNo: reviewed.voucher.serialNo,
      operatorName: reviewed.voucher.operatorName,
      paymentAt: reviewed.voucher.paymentAt,
      voucherName: reviewed.voucher.voucherName,
      voucherType: reviewed.voucher.voucherType,
      voucherUrl: reviewed.voucher.voucherUrl,
      voucherRemark: reviewed.voucher.voucherRemark,
      failureReason: reviewed.voucher.failureReason,
      voucher: reviewed.voucher
    }
  });
  res.json({ success: true, data: reviewed });
});

app.post('/api/finance/requests/:requestId/settle', (req, res) => {
  const record = getFinanceRequest(req.params.requestId);
  if (!record) {
    return res.status(404).json({ success: false, message: 'Finance request not found' });
  }
  const settled = syncFinanceRequestStatus(record, 'SETTLED', {
    operatorName: req.body?.operatorName || '王财务',
    completedAt: new Date().toISOString(),
    paymentSerialNo: record.voucher?.serialNo || `FIN-${Date.now().toString().slice(-8)}`,
    voucherRemark: '款项已打款至员工绑定账户',
    remark: '款项已打款至员工绑定账户'
  });
  relaySyncEvent({
    type: 'SETTLEMENT_COMPLETED',
    payload: {
      employeeId: settled.employeeId,
      employeeName: settled.employeeName,
      requestId: settled.requestId,
      amount: settled.amount,
      completedAt: settled.updatedAt,
      remark: '款项已打款至员工绑定账户',
      paymentChannel: settled.voucher.channel,
      payeeAccountMasked: settled.voucher.account,
      paymentSerialNo: settled.voucher.serialNo,
      operatorName: settled.voucher.operatorName,
      paymentAt: settled.voucher.paymentAt,
      voucherName: settled.voucher.voucherName,
      voucherType: settled.voucher.voucherType,
      voucherUrl: settled.voucher.voucherUrl,
      voucherRemark: settled.voucher.voucherRemark,
      voucher: settled.voucher
    }
  });
  res.json({ success: true, data: settled });
});

app.get('/api/hr/schedules', (req, res) => {
  res.json([
    { id: 's1', employee: 'Alice Chen', role: 'Manager', start: '2026-03-25T09:00:00Z', end: '2026-03-25T18:00:00Z', venue: 'Store A' },
    { id: 's2', employee: 'Bob Smith', role: 'Technician', start: '2026-03-25T10:00:00Z', end: '2026-03-25T19:00:00Z', venue: 'Store A' },
    { id: 's3', employee: 'Staff-01', role: 'Trainer', start: '2026-03-26T13:00:00Z', end: '2026-03-26T22:00:00Z', venue: 'Store B' }
  ]);
});

app.post('/api/hr/schedules/publish2', (req, res) => {
  // Notify employees via mobile app
  io.emit('data_sync', {
    type: 'SCHEDULE_PUBLISHED',
    payload: {
      message: 'Next week\'s schedule has been published.',
      date: new Date().toISOString()
    }
  });
  
  res.json({ success: true });
});

app.get('/api/risk/alerts', (req, res) => {
  res.json(db.risk_alerts);
});

app.get('/api/hr/courses', (req, res) => {
  res.json(db.courses);
});

app.get('/api/marketing/kocs', (req, res) => {
  res.json(db.kocs);
});

app.get('/api/trendy/nfts', (req, res) => {
  res.json(db.nfts);
});

app.post('/api/trendy/nfts', (req, res) => {
  const nft = { id: `nft${Date.now()}`, ...req.body, status: 'MINTED', series: 'Cyber Genesis' };
  if (!db.nfts) db.nfts = [];
  db.nfts.unshift(nft);
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New NFT Minted',
    content: `${nft.name} by ${nft.owner}`,
    type: 'success'
  });
  
  io.emit('data_sync', { type: 'NFT_MINTED', payload: nft });
  res.json({ success: true, nft });
});

app.get('/api/content/venue-showcase', (req, res) => {
  res.json(db.venue_showcase);
});

app.post('/api/content/venue-showcase', (req, res) => {
  db.venue_showcase = { ...db.venue_showcase, ...req.body };
  res.json({ success: true, data: db.venue_showcase });
});

// Operations Demo
app.get('/api/public/demo/digital-ops', (req, res) => {
  res.json({
    success: true,
    data: {
      message: 'Digital Operations Demo API active',
      metrics: { visitors: 120, conversions: 45, revenue: 5600 }
    }
  });
});

// System Health (Admin)
app.get('/api/system/health', (req, res) => {
  res.json(db.system_health);
});

// Success Stories (Business)
app.get('/api/content/stories', (req, res) => {
  res.json(db.success_stories);
});

// Game Library (Player)
app.get('/api/content/games', (req, res) => {
  res.json(db.game_library);
});

// Inventory (Admin)
app.get('/api/inventory', (req, res) => {
  res.json(db.inventory);
});

app.post('/api/inventory/replenish', (req, res) => {
  const { id, amount } = req.body;
  const item = db.inventory.find(i => i.id === id);
  if (item) {
    item.stock += amount;
    item.status = item.stock > 20 ? 'IN_STOCK' : 'LOW_STOCK';
    
    // Notify
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Stock Replenished',
      content: `${item.name} stock updated to ${item.stock}.`,
      type: 'system'
    });
    
    res.json({ success: true, item });
  } else {
    res.status(404).json({ success: false, message: 'Item not found' });
  }
});

app.get('/api/expert/reviews', (req, res) => {
  res.json(db.expert_reviews);
});

app.post('/api/expert/review', (req, res) => {
  const review = { id: `r${Date.now()}`, ...req.body, status: 'PENDING', date: new Date().toISOString() };
  db.expert_reviews.push(review);
  res.json({ success: true, review });
});

app.get('/api/trendy/items', (req, res) => {
  res.json(db.trendy_items);
});

app.post('/api/trendy/draw', (req, res) => {
  // Simulate draw logic
  const items = db.trendy_items;
  const wonItem = Math.random() > 0.8 ? items[0] : (Math.random() > 0.5 ? items[1] : items[2]);
  
  if (wonItem.stock > 0) {
    wonItem.stock--;
    
    // Notify
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Rare Item Won!',
      content: `A lucky player just won: ${wonItem.name}`,
      type: 'alert'
    });
    
    res.json({ success: true, item: wonItem });
  } else {
    res.status(400).json({ success: false, message: 'Out of stock' });
  }
});

app.get('/api/organization/tree', (req, res) => {
  res.json(db.organization_tree);
});

app.get('/api/community/leaderboard', (req, res) => {
  res.json(db.leaderboard);
});

app.get('/api/knowledge/sops', (req, res) => {
  res.json(db.sops);
});

app.get('/api/community/achievements', (req, res) => {
  res.json(db.achievements);
});

app.get('/api/community/events', (req, res) => {
  res.json(db.community_events);
});

app.post('/api/community/events', (req, res) => {
  const event = { id: `e${Date.now()}`, ...req.body, status: 'OPEN' };
  db.community_events.push(event);
  res.json({ success: true, event });
});

app.get('/api/workflow/tickets', (req, res) => {
  res.json(db.workflow_tickets || []);
});

app.post('/api/workflow/tickets', (req, res) => {
  const ticket = { id: `W-${Date.now()}`, ...req.body, status: 'NEW', assigneeId: 'Unassigned', createdAt: new Date().toISOString() };
  if (!db.workflow_tickets) db.workflow_tickets = [];
  db.workflow_tickets.unshift(ticket);
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Work Order',
    content: `[${ticket.priority}] ${ticket.title}`,
    type: 'warning'
  });
  
  io.emit('data_sync', { type: 'WORK_ORDER_CREATED', payload: ticket });
  res.json({ success: true, ticket });
});

app.get('/api/workflow/tickets/:id/logs', (req, res) => {
  const workflowTicket = (db.workflow_tickets || []).find(item => String(item.id) === String(req.params.id));
  const relatedTicketId = workflowTicket?.relatedType === 'TICKET' ? workflowTicket.relatedId : null;
  const logs = relatedTicketId
    ? (db.ticket_timelines?.[relatedTicketId] || []).map(item => ({
        action: item.stage || item.title,
        details: item.detail || item.title,
        operatorId: item.operator || 'SYSTEM',
        createdAt: item.timestamp
      }))
    : [
        { action: 'CREATED', details: 'Ticket created', operatorId: 'SYSTEM', createdAt: workflowTicket?.createdAt || new Date().toISOString() }
      ];
  res.json(logs);
});

app.put('/api/workflow/tickets/:id/status', (req, res) => {
  const t = (db.workflow_tickets || []).find(x => x.id === req.params.id);
  if (!t) {
    return res.status(404).json({ success: false, message: 'Workflow ticket not found' });
  }
  const action = String(req.query.action || req.body?.action || '').toUpperCase();
  const requestedStatus = String(req.query.status || req.body?.status || '').toUpperCase();
  const comment = String(req.query.comment || req.body?.comment || '').trim();
  const operatorId = String(req.query.operatorId || req.body?.operatorId || 'Manager_5174');
  const assignee = String(req.query.assignee || req.body?.assignee || operatorId);
  const rescheduleDate = String(req.query.rescheduleDate || req.body?.rescheduleDate || '').trim();
  const rescheduleTime = String(req.query.rescheduleTime || req.body?.rescheduleTime || '').trim();
  const previousAssignee = t.assignee || t.assigneeId || '';
  const targetTicket = t.relatedType === 'TICKET' ? (db.tickets || []).find(item => String(item.id) === String(t.relatedId)) : null;
  const isBookingService = Boolean(targetTicket?.bookingRef || targetTicket?.type === '预约服务');
  const status = requestedStatus || (action === 'ACCEPT' || action === 'REASSIGN' || action === 'BOOKING_CONFIRM' || action === 'BOOKING_RESCHEDULE' ? 'ASSIGNED' : action === 'ARRIVAL_CONFIRMED' ? 'RESOLVED' : action === 'BOOKING_CANCEL' ? 'CLOSED' : t.status);
  t.status = status || t.status;
  t.assignee = assignee;
  t.assigneeId = assignee;
  t.nodeName = action === 'BOOKING_CONFIRM'
    ? '已确认预约'
    : action === 'BOOKING_RESCHEDULE'
      ? '已确认改约'
    : action === 'ARRIVAL_CONFIRMED'
      ? '已到店'
      : action === 'BOOKING_CANCEL'
        ? '已取消预约'
        : status === 'RESOLVED'
          ? '已解决'
          : status === 'CLOSED'
            ? '已关闭'
            : status === 'ASSIGNED'
              ? (action === 'REASSIGN' ? '已转派' : '处理中')
              : t.nodeName;
  t.updatedAt = new Date().toISOString();
  if (status === 'RESOLVED' || status === 'CLOSED') {
    t.completedAt = t.updatedAt;
  } else {
    delete t.completedAt;
  }
  t.reviewer = operatorId;
  if (t.relatedType === 'TICKET') {
    if (targetTicket) {
      targetTicket.workflowTaskId = t.id;
      targetTicket.workflowStatus = t.status;
      targetTicket.workflowNodeName = t.nodeName;
      targetTicket.assignee = assignee;
      targetTicket.updatedAt = t.updatedAt;
      if (isBookingService && action === 'BOOKING_RESCHEDULE' && targetTicket.bookingRef) {
        targetTicket.bookingRef = {
          ...targetTicket.bookingRef,
          date: rescheduleDate || targetTicket.bookingRef.date || '',
          time: rescheduleTime || targetTicket.bookingRef.time || ''
        };
      }
      if (isBookingService && action === 'ARRIVAL_CONFIRMED') {
        targetTicket.status = 'RESOLVED';
        targetTicket.statusText = '已到店';
        targetTicket.resolutionNote = comment || `${operatorId} 已确认玩家到店`;
        appendTicketTimeline(targetTicket.id, {
          stage: 'ARRIVAL_CONFIRMED',
          title: '玩家已到店',
          operator: operatorId,
          detail: targetTicket.resolutionNote,
          meta: [
            { label: '流程单号', value: t.id },
            { label: '预约门店', value: targetTicket.bookingRef?.storeName || targetTicket.bookingRef?.storeId || '待确认' }
          ]
        });
      } else if (isBookingService && action === 'BOOKING_CANCEL') {
        targetTicket.status = 'CLOSED';
        targetTicket.statusText = '预约已取消';
        targetTicket.resolutionNote = comment || `${operatorId} 已取消预约`;
        appendTicketTimeline(targetTicket.id, {
          stage: 'BOOKING_CANCELLED',
          title: '预约已取消',
          operator: operatorId,
          detail: targetTicket.resolutionNote,
          meta: [
            { label: '流程单号', value: t.id },
            { label: '预约项目', value: targetTicket.bookingRef?.gameName || targetTicket.bookingRef?.gameId || '待确认' }
          ]
        });
      } else if (status === 'RESOLVED') {
        targetTicket.status = 'RESOLVED';
        targetTicket.statusText = '已解决';
        targetTicket.resolutionNote = comment || `${operatorId} 已完成处理`;
        appendTicketTimeline(targetTicket.id, {
          stage: 'RESOLVED',
          title: '工单已解决',
          operator: operatorId,
          detail: targetTicket.resolutionNote,
          meta: [
            { label: '流程单号', value: t.id },
            { label: '处理结果', value: '已解决' }
          ]
        });
      } else if (status === 'CLOSED') {
        targetTicket.status = 'CLOSED';
        targetTicket.statusText = '已关闭';
        targetTicket.resolutionNote = comment || `${operatorId} 已归档关闭`;
        appendTicketTimeline(targetTicket.id, {
          stage: 'CLOSED',
          title: '工单已关闭',
          operator: operatorId,
          detail: targetTicket.resolutionNote,
          meta: [
            { label: '流程单号', value: t.id },
            { label: '归档状态', value: '已关闭' }
          ]
        });
      } else {
        targetTicket.status = 'PROCESSING';
        targetTicket.statusText = '处理中';
        targetTicket.resolutionNote = action === 'REASSIGN' ? (comment || targetTicket.resolutionNote || '') : (comment || '');
        if (action === 'REASSIGN') {
          appendTicketTimeline(targetTicket.id, {
            stage: 'ASSIGNED',
            title: '工单已转派',
            operator: operatorId,
            detail: comment || `${previousAssignee || operatorId} 已将工单转派给 ${assignee}`,
            meta: [
              { label: '流程单号', value: t.id },
              { label: '原处理人', value: previousAssignee || operatorId },
              { label: '当前处理人', value: assignee }
            ]
          });
        } else if (isBookingService && action === 'BOOKING_CONFIRM') {
          targetTicket.resolutionNote = comment || `${operatorId} 已确认预约，等待玩家按时到店`;
          appendTicketTimeline(targetTicket.id, {
            stage: 'BOOKING_CONFIRMED',
            title: '预约已确认',
            operator: operatorId,
            detail: targetTicket.resolutionNote,
            meta: [
              { label: '流程单号', value: t.id },
              { label: '预约时间', value: `${targetTicket.bookingRef?.date || ''} ${targetTicket.bookingRef?.time || ''}`.trim() || '待确认' }
            ]
          });
        } else if (isBookingService && action === 'BOOKING_RESCHEDULE') {
          const nextSchedule = `${targetTicket.bookingRef?.date || ''} ${targetTicket.bookingRef?.time || ''}`.trim() || '待确认';
          targetTicket.resolutionNote = comment || `${operatorId} 已确认改约，新的预约时间为 ${nextSchedule}`;
          appendTicketTimeline(targetTicket.id, {
            stage: 'BOOKING_RESCHEDULED',
            title: '改约已确认',
            operator: operatorId,
            detail: targetTicket.resolutionNote,
            meta: [
              { label: '流程单号', value: t.id },
              { label: '新预约时间', value: nextSchedule }
            ]
          });
        } else {
          appendTicketTimeline(targetTicket.id, {
            stage: 'ACCEPTED',
            title: '工单已接单',
            operator: operatorId,
            detail: comment || `${operatorId} 已接单，当前由 ${assignee} 跟进处理`,
            meta: [
              { label: '流程单号', value: t.id },
              { label: '当前处理人', value: assignee }
            ]
          });
          appendTicketTimeline(targetTicket.id, {
            stage: 'PROCESSING',
            title: '工单进入处理中',
            operator: operatorId,
            detail: comment || `${assignee} 已开始跟进处理`,
            meta: [
              { label: '流程单号', value: t.id },
              { label: '当前状态', value: '处理中' }
            ]
          });
        }
      }
    }
    const basePayload = {
      ticketId: t.relatedId,
      title: t.title,
      assignee,
      workflowTaskId: t.id,
      workflowStatus: t.status,
      workflowNodeName: t.nodeName,
      action: action || status,
      previousAssignee,
      bookingRef: targetTicket?.bookingRef || undefined,
      attachments: Array.isArray(targetTicket?.attachments) ? targetTicket.attachments : []
    };
    if (status === 'RESOLVED') {
      io.emit('data_sync', {
        type: 'TICKET_RESOLVED',
        payload: {
          ...basePayload,
          resolvedAt: t.updatedAt,
          resolutionNote: targetTicket?.resolutionNote || comment || `${operatorId} 已完成处理`,
          message: targetTicket?.resolutionNote || comment || `${operatorId} 已完成处理`
        }
      });
    } else if (status === 'CLOSED') {
      io.emit('data_sync', {
        type: 'TICKET_CLOSED',
        payload: {
          ...basePayload,
          closedAt: t.updatedAt,
          closeNote: targetTicket?.resolutionNote || comment || `${operatorId} 已归档关闭`,
          message: targetTicket?.resolutionNote || comment || `${operatorId} 已归档关闭`
        }
      });
    } else {
      io.emit('data_sync', {
        type: 'TICKET_STATUS_UPDATED',
        payload: {
          ...basePayload,
          status: 'PROCESSING',
          statusText: action === 'BOOKING_CONFIRM' ? '已确认预约' : action === 'BOOKING_RESCHEDULE' ? '已确认改约' : '处理中',
          updatedAt: t.updatedAt,
          resolutionNote: targetTicket?.resolutionNote || comment || ''
        }
      });
    }
  }
  io.emit('data_sync', { type: 'WORKFLOW_TASK_COMPLETED', payload: { taskId: t.id, relatedId: t.relatedId, relatedType: t.relatedType, status: t.status, completedAt: t.completedAt || t.updatedAt } });
  res.json({ success: true, ticket: t });
});

app.post('/api/trendy/nfts/list', (req, res) => {
  const { id, price } = req.body;
  const nft = db.nfts.find(n => n.id === id);
  if (nft) {
    nft.status = 'LISTED';
    nft.price = price;
    
    io.emit('data_sync', {
      type: 'NFT_LISTED',
      payload: nft
    });
    
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'NFT Market Activity',
      content: `User listed ${nft.name} for ${price} Pts.`,
      type: 'info'
    });
  }
  res.json({ success: true });
});

// Notifications
app.get('/api/notification/system/my', (req, res) => {
  res.json(db.notifications);
});

// OpenClaw Mock Endpoints
app.get('/api/openclaw/handshake', (req, res) => {
  res.json({
    code: 200,
    success: true,
    data: "树哥汇报：龙虾哥，您的龙虾军团全员（10名专家）已就位！所有开发任务将严格按照您的计划执行！🦞🫡"
  });
});

app.get('/api/openclaw/dashboard', (req, res) => {
  res.json({
    success: true,
    data: {
      status: 'active',
      cpu_usage: 45,
      memory_usage: 60,
      active_connections: 120
    }
  });
});

app.get('/api/openclaw/suggestions', (req, res) => {
  res.json({
    success: true,
    data: [
      { id: 1, type: 'optimization', message: 'Optimize database indexes' },
      { id: 2, type: 'security', message: 'Update SSL certificates' }
    ]
  });
});

app.get('/api/ai/brain/health', (req, res) => {
  res.json({
    success: true,
    data: {
      status: 'healthy',
      latency: '24ms'
    }
  });
});

app.get('/api/language/:lang', (req, res) => {
  res.json({ success: true, lang: req.params.lang });
});

app.get('/api/globalization/profile/:lang', (req, res) => {
  res.json({ success: true, profile: 'default' });
});

app.get('/api/membership/me', (req, res) => {
  res.json({ success: true, data: db.members[0] });
});

app.get('/api/membership/profile/:id', (req, res) => {
  res.json({ success: true, data: db.members.find(m => m.id === req.params.id) });
});

app.get('/api/membership/list', (req, res) => {
  res.json(db.members);
});

app.get('/api/membership/:id/ai-insights', (req, res) => {
  // Mock insights
  res.json({
    userId: req.params.id,
    riskLevel: 'HIGH',
    churnProbability: 0.85,
    retentionStrategy: 'Send 50% discount coupon for next VR session.',
    nextBestAction: 'Call to offer free trial',
    engagementScore: 40
  });
});

app.get('/api/openclaw/brain/insight', (req, res) => {
  const path = req.query.path || '/';
  
  const insights = {
    '/': 'Welcome back, Boss! Revenue is up 12% today. Check the "Command Center" to see your legion in action.',
    '/monitor/auto-center': 'All systems green. Sarah (Marketing) is waiting for approval on the Summer Campaign.',
    '/membership/dashboard': 'Churn risk is slightly up in Los Angeles. Suggest sending a "We Miss You" coupon to inactive members.',
    '/marketing/dashboard': 'Revenue goal is 85% met. Launch a flash sale to close the gap?',
    '/iot/venue': 'Energy usage is high in the Cardio Zone. Enable "Eco Mode"?',
    '/inventory/smart': 'VR Headset Pro stock is critically low. Auto-reorder is recommended.',
    '/trendy/dashboard': 'Users love the "Golden Dragon". Increase drop rate for the weekend event?',
    '/franchise/tree': 'Tokyo Arena construction is 80% complete. Ready to start pre-sales?',
    '/knowledge/sops': '3 new employees haven\'t completed the Safety SOP training.',
    '/franchise/community': 'The Weekend VR Tournament in New York has 45 attendees. Promote it to hit 100?'
  };

  // Find exact or partial match
  const matchedKey = Object.keys(insights).find(key => path.includes(key));
  const suggestion = matchedKey ? insights[matchedKey] : 'I am monitoring system health. Everything looks optimal.';

  res.json({
    success: true,
    data: {
      path: path,
      suggestion: suggestion
    }
  });
});

app.get('/api/iot/predictive/dashboard/stats', (req, res) => {
  res.json({
    totalDevices: db.devices.length,
    onlineDevices: db.devices.filter(d => d.status === 'ONLINE').length,
    highRiskDevices: 1,
    predictionAccuracy: 94.5,
    costSaving: 12500
  });
});

app.get('/api/iot/predictive/risky-devices', (req, res) => {
  res.json([
    {
      id: 'd2',
      serialNumber: 'SN-VR-002',
      name: 'VR Pod 2',
      type: 'VR',
      status: 'ONLINE',
      healthScore: 45,
      temperature: 78.5,
      usageHours: 1200,
      predictedFault: 'Cooling System Failure',
      lastMaintenanceDate: '2025-12-01'
    }
  ]);
});

app.get('/api/iot/predictive/devices', (req, res) => {
  res.json(db.devices.map(d => ({
    ...d,
    serialNumber: `SN-${d.id.toUpperCase()}`,
    healthScore: d.id === 'd2' ? 45 : 95,
    temperature: d.id === 'd2' ? 78.5 : 45.0,
    lastMaintenanceDate: '2026-01-15'
  })));
});

app.post('/api/iot/predictive/analyze', (req, res) => {
  // Simulate AI analysis discovering a fault and auto-generating a ticket
  const ticket = { 
    id: `W-${Date.now()}`, 
    type: 'REPAIR', 
    title: `[PREDICTIVE] Omni Treadmill 3 - Motor Bearing Wear`, 
    priority: 'HIGH', 
    status: 'NEW', 
    assigneeId: 'Unassigned', 
    createdAt: new Date().toISOString() 
  };
  db.workflow_tickets.unshift(ticket);
  
  io.emit('data_sync', { type: 'WORK_ORDER_CREATED', payload: ticket });
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Predictive Maintenance Alert',
    content: `AI detected high risk of motor failure on Omni Treadmill 3. Work order created.`,
    type: 'warning'
  });
  
  res.json({ success: true, message: 'Analysis complete' });
});

app.post('/api/marketing/campaigns/launch', (req, res) => {
  const campaign = req.body;
  
  // Real-time broadcast to Player PC and Mobile
  io.emit('data_sync', { 
    type: 'CAMPAIGN_LAUNCHED', 
    payload: {
      id: campaign.id,
      title: campaign.name,
      content: campaign.description || 'New exciting event is live!',
      date: new Date().toISOString()
    }
  });
  
  // Also push to news feed automatically
  const newsItem = { id: `n${Date.now()}`, title: campaign.name, content: campaign.description || 'Join our latest campaign now!', date: new Date().toISOString() };
  if (!db.news) db.news = [];
  db.news.unshift(newsItem);
  io.emit('data_sync', { type: 'NEWS_CREATED', payload: newsItem });

  res.json({ success: true });
});

app.post('/api/system/broadcast', (req, res) => {
  const { message } = req.body;
  
  // Real-time broadcast to ALL clients
  io.emit('data_sync', { 
    type: 'GLOBAL_BROADCAST', 
    payload: {
      message,
      timestamp: new Date().toISOString()
    }
  });
  
  res.json({ success: true });
});

app.get('/api/mall/orders', (req, res) => {
  const orders = (db.orders || [])
    .filter(order => (order.sourceType || order.type) === 'MALL')
    .map(order => {
      const logistics = order.logistics || {};
      const logisticsStatus = logistics.status || '';
      const status = order.fulfillmentStatus
        || (!logisticsStatus
          ? 'PENDING_SHIPMENT'
          : logisticsStatus === '部分发货' || logisticsStatus === '部分签收'
            ? 'PARTIALLY_SHIPPED'
            : logisticsStatus === '已签收'
              ? 'COMPLETED'
              : 'SHIPPED');
      return {
        id: order.id,
        orderNo: order.id,
        userId: order.userId || order.user || 'Player_Mobile_01',
        userPhone: order.contactPhone || '13800000000',
        totalAmount: Number(order.amount || 0),
        status,
        createdAt: order.createdAt || order.date,
        date: order.date || order.createdAt,
        items: Array.isArray(order.items)
          ? order.items.map(item => ({
              productId: item.id || item.productId || item.title,
              productName: item.title || item.productName || '商城商品',
              price: Number(item.price || 0),
              quantity: Number(item.quantity || 1),
              imageUrl: item.imageUrl || 'http://localhost:8080/images/asset_d1a1323301151b15d3facde9e14f607b.jpg',
              sku: item.sku || '默认规格'
            }))
          : [],
        logistics
      };
    })
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  res.json(orders);
});

app.post('/api/mall/orders/:id/ship', (req, res) => {
  const order = (db.orders || []).find(item => String(item.id) === String(req.params.id));
  if (!order) {
    return res.status(404).json({ success: false, message: 'Order not found' });
  }
  const packages = Array.isArray(req.body?.packages) ? req.body.packages : [];
  if (!packages.length) {
    return res.status(400).json({ success: false, message: 'Packages required' });
  }
  const createdAt = new Date().toISOString();
  const parcels = packages.map((pkg, index) => {
    const items = Array.isArray(pkg.items) ? pkg.items : [];
    const status = pkg.status || '运输中';
    const tracks = status === '已签收'
      ? [
          { id: `${req.params.id}-${index}-1`, title: '商家已发货', desc: `包裹已由 ${pkg.company} 揽收并发出。`, time: createdAt, tag: '已发货' },
          { id: `${req.params.id}-${index}-2`, title: '运输中', desc: '包裹已进入干线运输。', time: createdAt, tag: '运输中' },
          { id: `${req.params.id}-${index}-3`, title: '包裹已签收', desc: '包裹已完成签收并同步到订单中心。', time: createdAt, tag: '已签收' }
        ]
      : status === '待揽收'
        ? [
            { id: `${req.params.id}-${index}-1`, title: '分包待揽收', desc: '包裹已拆分完成，等待物流公司揽收。', time: createdAt, tag: '待揽收' }
          ]
        : [
            { id: `${req.params.id}-${index}-1`, title: '商家已发货', desc: `包裹已由 ${pkg.company} 揽收并发出。`, time: createdAt, tag: '已发货' },
            { id: `${req.params.id}-${index}-2`, title: '运输中', desc: '包裹已进入干线运输。', time: createdAt, tag: '运输中' }
          ];
    return {
      parcelNo: pkg.parcelNo || `PKG-${String(req.params.id).replace(/[^a-zA-Z0-9]/g, '')}-${index + 1}`,
      company: pkg.company,
      trackingNo: pkg.trackingNo,
      status,
      items,
      tracks
    };
  });
  const overallStatus = parcels.every(item => item.status === '已签收')
    ? '已签收'
    : parcels.some(item => item.status === '已签收')
      ? '部分签收'
      : parcels.some(item => item.status === '待揽收')
        ? '部分发货'
        : '已发货';
  order.logistics = {
    company: parcels[0]?.company || '待分配',
    trackingNo: parcels[0]?.trackingNo || '',
    status: overallStatus,
    packageCount: parcels.length,
    receiverName: order.logistics?.receiverName || order.contactName || '当前账号',
    receiverPhone: order.logistics?.receiverPhone || '138****2048',
    address: order.logistics?.address || '上海市浦东新区张江科创路 88 号 2 栋 1203',
    parcels,
    tracks: parcels.flatMap(item => item.tracks)
  };
  order.fulfillmentStatus = overallStatus === '已签收' ? 'COMPLETED' : overallStatus === '已发货' ? 'SHIPPED' : 'PARTIALLY_SHIPPED';
  order.updatedAt = createdAt;
  io.emit('data_sync', {
    type: 'MALL_ORDER_SHIPPED',
    payload: {
      orderId: order.id,
      logistics: order.logistics,
      fulfillmentStatus: order.fulfillmentStatus,
      updatedAt: createdAt
    }
  });
  res.json({ success: true, order });
});

app.post('/api/mall/orders/:id/parcels/:parcelNo/note', (req, res) => {
  const order = (db.orders || []).find(item => String(item.id) === String(req.params.id));
  if (!order || !order.logistics || !Array.isArray(order.logistics.parcels)) {
    return res.status(404).json({ success: false, message: 'Order not found' });
  }
  const parcel = order.logistics.parcels.find(item =>
    String(item.parcelNo) === String(req.params.parcelNo) || String(item.trackingNo) === String(req.params.parcelNo)
  );
  if (!parcel) {
    return res.status(404).json({ success: false, message: 'Parcel not found' });
  }
  const note = String(req.body?.note || '').trim();
  if (!note) {
    return res.status(400).json({ success: false, message: 'Note required' });
  }
  const operator = String(req.body?.operator || '运营同学');
  const createdAt = new Date().toISOString();
  const track = {
    id: `${req.params.id}-${String(parcel.parcelNo || parcel.trackingNo || 'parcel').replace(/[^a-zA-Z0-9]/g, '')}-note-${Date.now()}`,
    title: '人工备注',
    desc: `${operator}：${note}`,
    time: createdAt,
    tag: '人工备注'
  };
  if (!Array.isArray(parcel.tracks)) parcel.tracks = [];
  parcel.tracks.unshift(track);
  order.logistics.tracks = order.logistics.parcels.flatMap(item => Array.isArray(item.tracks) ? item.tracks : []);
  order.updatedAt = createdAt;
  io.emit('data_sync', {
    type: 'MALL_ORDER_SHIPPED',
    payload: {
      orderId: order.id,
      logistics: order.logistics,
      fulfillmentStatus: order.fulfillmentStatus,
      updatedAt: createdAt
    }
  });
  res.json({ success: true, order, track });
});

app.post('/api/mall/order', (req, res) => {
  const { userId, productId, productName, price, type } = req.body;
  const order = {
    id: `ORD-${Date.now()}`,
    userId,
    productId,
    productName,
    price,
    type,
    status: 'PENDING_FULFILLMENT',
    createdAt: new Date().toISOString()
  };
  
  if (!db.mall_orders) db.mall_orders = [];
  db.mall_orders.unshift(order);

  // Notify Admin Dashboard
  io.emit('data_sync', {
    type: 'NEW_MALL_ORDER',
    payload: order
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Mall Order',
    content: `${userId} just ordered ${productName}.`,
    type: 'success'
  });

  res.json({ success: true, order });
});

app.post('/api/queue/join', (req, res) => {
  const body = req.body || {};
  const project = getQueueProject(body.projectId, body.projectName);
  if (!project) {
    return res.status(400).json({ success: false, message: 'Invalid queue project' });
  }
  if (project.status === 'PAUSED') {
    return res.status(409).json({ success: false, message: `${project.name} 当前暂停取号，请稍后再试。` });
  }
  const activeTicket = getActiveQueueTicket(body.userId);
  if (activeTicket && activeTicket.projectId === project.id) {
    return res.json({ success: true, data: activeTicket, restored: true });
  }
  const ticket = buildQueueTicket(body);
  if (!ticket) {
    return res.status(400).json({ success: false, message: 'Invalid queue project' });
  }
  
  io.emit('data_sync', {
    type: 'NEW_QUEUE_JOIN',
    payload: {
      ...ticket,
      queueCount: getQueueProject(ticket.projectId, ticket.projectName)?.queueLength || 0
    }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Queue Entry',
    content: `${ticket.userId} joined queue for ${ticket.projectName}.`,
    type: 'info'
  });

  res.json({ success: true, data: ticket });
});

app.get('/api/queue/projects', (req, res) => {
  const projects = db.queue_projects.map(project => getQueueProjectSnapshot(project.id, project.name));
  res.json({ success: true, data: projects });
});

app.get('/api/queue/my', (req, res) => {
  const ticket = getActiveQueueTicket(req.query?.userId);
  res.json({ success: true, data: ticket });
});

app.get('/api/queue/status', (req, res) => {
  const { projectId, projectName } = req.query;
  const queues = db.queue_tickets
    .filter(item => !projectId && !projectName ? true : item.projectId === projectId || item.projectName === projectName)
    .map(item => ({
      ...item,
      project: item.projectName
    }));
  res.json({ success: true, data: queues });
});

app.post('/api/queue/status', (req, res) => {
  const { id, queueNo, number, status } = req.body || {};
  const targetNumber = queueNo || number;
  const ticket = db.queue_tickets.find(item => item.id === id || item.number === targetNumber);
  if (!ticket) {
    return res.status(404).json({ success: false, message: 'Queue ticket not found' });
  }
  ticket.status = status || ticket.status;
  io.emit('data_sync', {
    type: 'QUEUE_STATUS_UPDATED',
    payload: {
      ...ticket,
      queueNo: ticket.number,
      project: ticket.projectName,
      projectName: ticket.projectName,
      timestamp: new Date().toISOString()
    }
  });
  res.json({ success: true, data: ticket });
});

app.post('/api/queue/project-status', (req, res) => {
  const { projectId, status } = req.body || {};
  const project = getQueueProject(projectId, projectId);
  if (!project) {
    return res.status(404).json({ success: false, message: 'Queue project not found' });
  }
  if (!['OPEN', 'PAUSED'].includes(status)) {
    return res.status(400).json({ success: false, message: 'Invalid queue project status' });
  }
  project.status = status;
  const snapshot = getQueueProjectSnapshot(project.id, project.name);
  io.emit('data_sync', {
    type: 'QUEUE_PROJECT_UPDATED',
    payload: {
      ...snapshot,
      timestamp: new Date().toISOString()
    }
  });
  res.json({ success: true, data: snapshot });
});

app.post('/api/queue/rejoin', (req, res) => {
  const { id, queueNo, number } = req.body || {};
  const targetNumber = queueNo || number;
  const ticket = db.queue_tickets.find(item => item.id === id || item.number === targetNumber);
  if (!ticket) {
    return res.status(404).json({ success: false, message: 'Queue ticket not found' });
  }
  const refreshed = buildQueueTicket({
    userId: ticket.userId,
    projectId: ticket.projectId,
    projectName: ticket.projectName,
    phone: ticket.phone
  });
  ticket.status = '已过号';
  if (!db.workflow_tickets) {
    db.workflow_tickets = [];
  }
  const workflowTask = {
    id: `W-${Date.now()}`,
    type: 'QUEUE_EXCEPTION',
    processName: '排队异常协同',
    title: `过号重排 · ${ticket.projectName}`,
    priority: 'MEDIUM',
    status: 'NEW',
    starter: ticket.userId || 'Player_Mobile_01',
    nodeName: '待服务台确认',
    relatedId: refreshed.number,
    relatedType: 'QUEUE_EXCEPTION',
    createdAt: new Date().toISOString(),
    externalWorkspace: 'admin',
    externalPath: `/workflow/list?relatedId=${encodeURIComponent(refreshed.number)}&type=QUEUE_EXCEPTION`,
    assignee: '5174 现场调度'
  };
  db.workflow_tickets.unshift(workflowTask);
  refreshed.workflowTaskId = workflowTask.id;
  refreshed.workflowStatus = workflowTask.status;
  refreshed.adminPath = workflowTask.externalPath;
  refreshed.businessPath = `/queue-manager?projectId=${encodeURIComponent(refreshed.projectId)}&queueNo=${encodeURIComponent(refreshed.number)}`;
  io.emit('data_sync', {
    type: 'QUEUE_REJOINED',
    payload: {
      ...refreshed,
      previousQueueNo: ticket.number,
      queueCount: getQueueProject(refreshed.projectId, refreshed.projectName)?.queueLength || 0,
      workflowTaskId: workflowTask.id
    }
  });
  io.emit('data_sync', {
    type: 'WORKFLOW_TASK_CREATED',
    payload: workflowTask
  });
  res.json({ success: true, data: refreshed });
});

app.post('/api/ai/pricing/dynamic', (req, res) => {
  const { strategy } = req.body;
  
  io.emit('data_sync', {
    type: 'DYNAMIC_PRICING_APPLIED',
    payload: { strategy, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'AI Yield Management',
    content: `Dynamic pricing strategy ${strategy} applied across all channels.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/queue/call', (req, res) => {
  const payload = buildQueueCallPayload(req.body || {});
  const projectSnapshot = getQueueProjectSnapshot(payload.projectId, payload.projectName);
  
  io.emit('data_sync', {
    type: 'QUEUE_CALLED',
    payload
  });

  io.emit('data_sync', {
    type: 'SMART_QUEUE_CALL',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Queue Call',
    content: `Now serving ${payload.number} for ${payload.project}`,
    type: 'info'
  });

  if (projectSnapshot) {
    io.emit('data_sync', {
      type: 'QUEUE_PROJECT_UPDATED',
      payload: {
        ...projectSnapshot,
        timestamp: new Date().toISOString()
      }
    });
  }

  res.json({ success: true, data: payload });
});

app.post('/api/membership/upgrade', (req, res) => {
  const { userId, newTier, amount } = req.body;
  
  io.emit('data_sync', {
    type: 'MEMBERSHIP_UPGRADED',
    payload: { userId, newTier, amount, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Membership Upgrade',
    content: `${userId} upgraded to ${newTier}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/nft/equip', (req, res) => {
  const { userId, nftId, nftName } = req.body;
  
  io.emit('data_sync', {
    type: 'NFT_EQUIPPED',
    payload: { userId, nftId, nftName, timestamp: new Date().toISOString() }
  });

  res.json({ success: true });
});

app.post('/api/schedule/swap', (req, res) => {
  const { employeeId, employeeName, shiftDate, shiftType } = req.body;
  
  io.emit('data_sync', {
    type: 'SHIFT_SWAP_REQUESTED',
    payload: { employeeId, employeeName, shiftDate, shiftType, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Shift Swap Request',
    content: `${employeeName} requested to swap ${shiftDate} ${shiftType}`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/iot/locker/overstay', (req, res) => {
  const { lockerId, overstayHours } = req.body;
  
  io.emit('data_sync', {
    type: 'LOCKER_OVERSTAY_CLEANUP',
    payload: { lockerId, overstayHours, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Locker Overstay Alert',
    content: `${lockerId} overstayed by ${overstayHours}h. Cleanup task dispatched.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/ux/rage-click', (req, res) => {
  const { userId, page, element } = req.body;
  
  io.emit('data_sync', {
    type: 'RAGE_CLICK_COMPENSATION',
    payload: { userId, page, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'UX Alert: Rage Click',
    content: `${userId} frustrated on ${page}. Auto-compensation sent.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/esg/grid-shedding', (req, res) => {
  const { reason } = req.body;
  
  io.emit('data_sync', {
    type: 'GRID_LOAD_SHEDDING_TRIGGERED',
    payload: { reason, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Grid Load Shedding',
    content: `Triggered due to ${reason}. Degrading non-essential power.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/crm/birthday-surprise', (req, res) => {
  const { userId } = req.body;
  
  io.emit('data_sync', {
    type: 'BIRTHDAY_SURPRISE_DROP',
    payload: { userId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Birthday Surprise Triggered',
    content: `AR Cake and Coupon dropped for ${userId}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/iot/trash/overflow', (req, res) => {
  const { deviceId, location } = req.body;
  
  io.emit('data_sync', {
    type: 'SMART_TRASH_CAN_OVERFLOW',
    payload: { deviceId, location, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Trash Can Full',
    content: `${deviceId} at ${location} is full. Dispatching janitor.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/iot/vr/motion-sickness', (req, res) => {
  const { userId, deviceId } = req.body;
  
  io.emit('data_sync', {
    type: 'VR_MOTION_SICKNESS_DETECTED',
    payload: { userId, deviceId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'VR Motion Sickness Alert',
    content: `${userId} on ${deviceId} showing motion sickness. Game paused.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/invoice/apply', (req, res) => {
  const { userId, amount, title, taxId, email, orderIds } = req.body;
  
  io.emit('data_sync', {
    type: 'INVOICE_REQUESTED',
    payload: { userId, amount, title, taxId, email, orderIds, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Invoice Request',
    content: `Invoice for ¥${amount} requested by ${title}`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/booking/submit', (req, res) => {
  const { userId, storeId, gameId, date, time, type, originalOrderId, originalOrderTitle, refundServiceTicketId } = req.body;
  const booking = {
    id: `BK-${Date.now()}`,
    userId,
    storeId,
    gameId,
    date,
    time,
    type,
    status: 'CONFIRMED',
    createdAt: new Date().toISOString(),
    originalOrderId: originalOrderId || '',
    originalOrderTitle: originalOrderTitle || '',
    refundServiceTicketId: refundServiceTicketId || ''
  };
  db.bookings.unshift(booking);
  const { serviceTicket, workflowTicket } = createBookingServiceTicket({ userId, storeId, gameId, date, time, type });
  let rebookPayload = null;
  if (originalOrderId && Array.isArray(db.orders)) {
    const sourceOrder = db.orders.find(item => String(item.id) === String(originalOrderId));
    if (sourceOrder) {
      const rebookRecord = {
        id: `RB-${Date.now()}`,
        originalOrderId: sourceOrder.id,
        originalOrderTitle: sourceOrder.title || originalOrderTitle || '',
        bookingId: booking.id,
        status: 'CONFIRMED',
        statusText: '已锁定新场次',
        sourceApp: '5176 玩家站',
        storeId,
        storeName: serviceTicket.bookingRef?.storeName || storeId,
        gameId,
        gameName: serviceTicket.bookingRef?.gameName || gameId,
        date,
        time,
        bookingType: type,
        teamSize: Number(req.body.teamSize || req.body.ticketCount || 1),
        ticketCount: Number(req.body.ticketCount || req.body.teamSize || 1),
        serviceTicketId: serviceTicket.id,
        workflowTaskId: workflowTicket.id,
        refundServiceTicketId: refundServiceTicketId || sourceOrder.refundServiceTicketId || '',
        linkedAt: new Date().toISOString()
      };
      sourceOrder.latestRebook = rebookRecord;
      sourceOrder.rebookHistory = [rebookRecord, ...(Array.isArray(sourceOrder.rebookHistory) ? sourceOrder.rebookHistory : [])].slice(0, 5);
      sourceOrder.rebookUpdatedAt = rebookRecord.linkedAt;
      sourceOrder.updatedAt = rebookRecord.linkedAt;
      rebookPayload = {
        orderId: sourceOrder.id,
        title: sourceOrder.title || originalOrderTitle || '',
        rebook: rebookRecord,
        updatedAt: rebookRecord.linkedAt
      };
    }
  }
  
  io.emit('data_sync', {
    type: 'NEW_VENUE_BOOKING',
    payload: { userId, storeId, gameId, date, time, bookingType: type, timestamp: new Date().toISOString(), serviceTicketId: serviceTicket.id, workflowTaskId: workflowTicket.id }
  });
  if (rebookPayload) {
    io.emit('data_sync', {
      type: 'ORDER_REBOOKED',
      payload: rebookPayload
    });
  }

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Booking',
    content: `${userId} booked ${gameId} at ${storeId} for ${date} ${time}`,
    type: 'success'
  });

  res.json({
    success: true,
    booking,
    serviceTicketId: serviceTicket.id,
    workflowTaskId: workflowTicket.id,
    serviceTicket,
    originalOrderId: originalOrderId || '',
    rebookWriteback: rebookPayload
  });
});

app.post('/api/invoice/apply', (req, res) => {
  const { userId, amount, needPaper } = req.body;
  
  if (needPaper) {
    io.emit('data_sync', {
      type: 'CLOUD_PRINT_DISPATCHED',
      payload: { userId, amount, printerId: 'PRINTER_FRONT_DESK_01', timestamp: new Date().toISOString() }
    });

    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Cloud Print Triggered',
      content: `Printing invoice for ${userId} at Front Desk.`,
      type: 'info'
    });
  }

  res.json({ success: true });
});

app.post('/api/ai/strategy/competitor', (req, res) => {
  const { target, action } = req.body;
  
  io.emit('data_sync', {
    type: 'COMPETITOR_COUNTER_STRATEGY',
    payload: { target, action, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Counter Strategy Active',
    content: `AI executing ${action} against ${target}.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/ai/strategy/weather', (req, res) => {
  const { weather } = req.body;
  
  io.emit('data_sync', {
    type: 'WEATHER_STRATEGY_TRIGGERED',
    payload: { weather, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Weather Strategy Active',
    content: `AI detected ${weather}, pushing indoor promos.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/iot/jukebox/queue', (req, res) => {
  const { userId, songName, artist } = req.body;
  
  io.emit('data_sync', {
    type: 'JUKEBOX_SONG_QUEUED',
    payload: { userId, songName, artist, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Jukebox Updated',
    content: `${userId} queued ${songName} by ${artist}`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/iot/parking/unlock', (req, res) => {
  const { userId, spaceId, plate } = req.body;
  const payload = { userId, user: userId, spaceId, plate, timestamp: new Date().toISOString() };
  
  io.emit('data_sync', {
    type: 'PARKING_UNLOCKED',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'VIP Parking Arrived',
    content: `Space ${spaceId} unlocked for ${plate} (${userId}).`,
    type: 'success'
  });

  res.json({ success: true, data: { status: 'UNLOCKED', ...payload } });
});

app.post('/api/crowdfunding/support', (req, res) => {
  const { projectId, amount, userId } = req.body;
  
  // We simulate it reaching 100% since the frontend starts at 99,950 / 100,000 and adds 50
  io.emit('data_sync', {
    type: 'CROWDFUNDING_SUCCESS_TRIGGERED',
    payload: { projectId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Crowdfunding Goal Reached!',
    content: `Project ${projectId} reached 100%. Auto-triggering supply chain PO.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/compliance/stream-consent/request', (req, res) => {
  const { userId, stationId } = req.body;
  
  io.emit('data_sync', {
    type: 'STREAM_CONSENT_REQUESTED',
    payload: { userId, stationId, timestamp: new Date().toISOString() }
  });

  res.json({ success: true });
});

app.post('/api/compliance/stream-consent/grant', (req, res) => {
  const { userId, stationId } = req.body;
  
  io.emit('data_sync', {
    type: 'STREAM_CONSENT_GRANTED',
    payload: { userId, stationId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Live Stream Started',
    content: `${userId} granted privacy consent. Stream is live on ${stationId}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/inventory/expiry-flash-sale', (req, res) => {
  const { item, discount } = req.body;
  
  io.emit('data_sync', {
    type: 'EXPIRY_FLASH_SALE',
    payload: { item, discount, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Food Expiry Flash Sale',
    content: `${item} is now at ${discount * 100}% off to clear stock!`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/sop/emergency', (req, res) => {
  const { storeId, reporter, type } = req.body;
  
  io.emit('data_sync', {
    type: 'EMERGENCY_TRIGGERED',
    payload: { storeId, reporter, type, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: '🚨 EMERGENCY ALERT',
    content: `Store ${storeId} reported an emergency: ${type}`,
    type: 'error'
  });

  res.json({ success: true });
});

app.get('/api/koc/overview', (req, res) => {
  const viewerId = req.query.viewerId || 'CyberPlayer_01';
  res.json({ success: true, data: getKocOverview(String(viewerId)) });
});

app.get('/api/koc/tasks', (req, res) => {
  const viewerId = req.query.viewerId || 'CyberPlayer_01';
  res.json({ success: true, data: getKocOverview(String(viewerId)).tasks });
});

app.post('/api/koc/tasks', (req, res) => {
  const {
    title,
    reward,
    commissionRate,
    minFans,
    levelTarget,
    materialsReady,
    createdBy
  } = req.body || {};

  if (!title) {
    return res.status(400).json({ success: false, message: 'Task title is required' });
  }

  const task = {
    id: `koc-task-${Date.now()}`,
    title,
    reward: reward || `${commissionRate || 15}%`,
    commissionRate: Number(commissionRate || 15),
    claimed: 0,
    budget: Number(req.body?.budget || 10000),
    uv: 0,
    orders: 0,
    paid: 0,
    gmv: 0,
    status: 'ACTIVE',
    minFans: Number(minFans || 0),
    levelTarget: levelTarget || '全部等级',
    materialsReady: materialsReady !== false,
    createdBy: createdBy || '5178-KOC',
    createdAt: new Date().toISOString(),
    shareLink: `sportsant.com/ref/${Date.now()}`
  };

  db.koc_tasks.unshift(task);
  pushKocActivity({
    type: 'TASK_PUBLISHED',
    title: '新悬赏任务已发布',
    detail: `${task.title} 已同步至 5171 KOC HUB、5174 营销后台与 5176 创作者门户。`
  });
  relaySyncEvent({
    type: 'NEW_KOC_TASK',
    payload: {
      taskId: task.id,
      title: task.title,
      taskName: task.title,
      reward: task.reward,
      commission: task.reward,
      minFans: task.minFans,
      levelTarget: task.levelTarget,
      timestamp: task.createdAt
    }
  });

  res.json({ success: true, data: task });
});

app.post('/api/koc/share', (req, res) => {
  const { kocId, taskId, taskTitle } = req.body || {};
  const task = getKocTask(taskId, taskTitle);
  const koc = getKocRecord(kocId || 'CyberPlayer_01');

  if (task) {
    task.claimed += 1;
  }

  const payload = {
    kocId: koc.id,
    kocName: koc.name,
    taskId: task?.id,
    taskTitle: task?.title || taskTitle,
    title: task?.title || taskTitle,
    shareLink: `${task?.shareLink || `sportsant.com/ref/${Date.now()}`}?koc=${encodeURIComponent(koc.id)}`,
    timestamp: new Date().toISOString()
  };

  pushKocActivity({
    type: 'KOC_LINK_GENERATED',
    title: '专属链接已生成',
    detail: `${koc.name} 已领取《${payload.taskTitle}》并生成专属分销链接。`
  });
  relaySyncEvent({
    type: 'KOC_LINK_GENERATED',
    payload
  });
  relaySyncEvent({
    type: 'KOC_TASK_SHARED',
    payload: {
      userId: koc.id,
      kocId: koc.id,
      taskId: task?.id,
      taskTitle: payload.taskTitle,
      title: payload.taskTitle,
      shareLink: payload.shareLink,
      timestamp: payload.timestamp
    }
  });

  res.json({ success: true, data: { ...payload, task } });
});

// Compliance Alert endpoint
// Mock IoT Energy Alert Trigger
app.post('/api/marketing/ad/push', (req, res) => {
  const { campaignId, title, targetAudience } = req.body;
  
  io.emit('data_sync', {
    type: 'DYNAMIC_AD_PUSHED',
    payload: { campaignId, title, targetAudience, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Ad Campaign Pushed',
    content: `Dynamic ad "${title}" sent to ${targetAudience}.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/security/ban', (req, res) => {
  const { playerId, game, reason } = req.body;
  
  io.emit('data_sync', {
    type: 'PLAYER_BANNED',
    payload: { playerId, game, reason, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Global Anti-Cheat Alert',
    content: `Player ${playerId} was banned for cheating.`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/esl/update', (req, res) => {
  const { productNameKeyword, newPrice, reason } = req.body;
  
  // Simulate network delay to ESL gateway
  setTimeout(() => {
    io.emit('data_sync', {
      type: 'ESL_PRICE_UPDATED',
      payload: { productNameKeyword, newPrice, reason, timestamp: new Date().toISOString() }
    });

    io.emit('notification', {
      id: Date.now().toString(),
      title: 'ESL Updated',
      content: `Prices for "${productNameKeyword}" updated to ¥${newPrice}`,
      type: 'info'
    });
  }, 2000);

  res.json({ success: true });
});

app.post('/api/contract/dispatch', (req, res) => {
  const { contractId, partyB, phone, template } = req.body;
  
  // Simulate B2B Partner signing the contract after a short delay
  setTimeout(() => {
    const txHash = `0x${Math.random().toString(16).substring(2, 42)}`
    
    io.emit('data_sync', {
      type: 'CONTRACT_SIGNED',
      payload: { contractId, partyB, txHash, timestamp: new Date().toISOString() }
    });

    // If it's an NFT Creator contract, also trigger DID wallet airdrop
    if (template && template.includes('创作者分润')) {
      io.emit('data_sync', {
        type: 'DID_WALLET_AIRDROP',
        payload: { 
          target: partyB, 
          asset: 'Creator Royalties Share Token', 
          amount: '500 USDT', 
          txHash 
        }
      });
    }

    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Contract E-Signed',
      content: `${partyB} signed ${contractId}`,
      type: 'success'
    });
  }, 4000);

  res.json({ success: true });
});

app.post('/api/fnb/orders', (req, res) => {
  const { userId, tableNo, items, total } = req.body;
  
  io.emit('data_sync', {
    type: 'FNB_NEW_ORDER',
    payload: { userId, tableNo, items, total, orderId: `FNB${Date.now()}`, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New F&B Order',
    content: `Table ${tableNo} ordered ${items.length} items.`,
    type: 'info'
  });

  res.json({ success: true, orderId: `FNB${Date.now()}` });
});

app.post('/api/sop/emergency', (req, res) => {
  const { storeId, reporter, type, location } = req.body;
  
  io.emit('data_sync', {
    type: 'EMERGENCY_SOS_TRIGGERED',
    payload: { storeId, reporter, incidentType: type, location, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: `EMERGENCY: ${type}`,
    content: `SOS triggered by ${reporter} at ${location}`,
    type: 'error'
  });

  res.json({ success: true });
});

app.post('/api/b2b/event/create', (req, res) => {
  const { company, code } = req.body;
  
  io.emit('data_sync', {
    type: 'B2B_EVENT_CREATED',
    payload: { company, code, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'B2B Event Room Created',
    content: `${company} private room code: ${code}`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/iot/predictive/dispatch', (req, res) => {
  const { devices } = req.body;
  
  io.emit('data_sync', {
    type: 'PREDICTIVE_MAINTENANCE_DISPATCHED',
    payload: { devices, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Maintenance Dispatched',
    content: `${devices.length} predictive maintenance tasks dispatched.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/lostfound/report', (req, res) => {
  const { itemName, location, finder, storage } = req.body;
  const payload = {
    itemName,
    location,
    finder,
    storage: storage || '前台 01号储物箱',
    matchedUser: 'Player_Mobile_01',
    timestamp: new Date().toISOString()
  };
  
  io.emit('data_sync', {
    type: 'LOST_FOUND_REGISTERED',
    payload
  });

  io.emit('data_sync', {
    type: 'LOST_FOUND_AI_MATCH',
    payload
  });

  io.emit('data_sync', {
    type: 'ITEM_FOUND_AI_MATCH',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Lost Item Auto-Matched',
    content: `${itemName} found at ${location}. Notifying potential owner.`,
    type: 'success'
  });

  res.json({ success: true, data: payload });
});

app.post('/api/lostfound/claim', (req, res) => {
  const { itemName, location, storage, userId } = req.body;
  const payload = {
    itemName,
    location,
    storage: storage || '前台 01号储物箱',
    userId: userId || 'Player_Mobile_01',
    status: 'CLAIMING',
    timestamp: new Date().toISOString()
  };

  io.emit('data_sync', {
    type: 'LOST_FOUND_CLAIM_REQUESTED',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Lost Item Claim Requested',
    content: `${payload.userId} requested to claim ${itemName}.`,
    type: 'info'
  });

  res.json({ success: true, data: payload });
});

app.post('/api/lostfound/complete', (req, res) => {
  const { itemName, location, storage, userId, verifiedBy } = req.body;
  const payload = {
    itemName,
    location,
    storage: storage || '前台 01号储物箱',
    userId: userId || 'Player_Mobile_01',
    verifiedBy: verifiedBy || 'FrontDesk_01',
    status: 'CLAIMED',
    timestamp: new Date().toISOString()
  };

  io.emit('data_sync', {
    type: 'LOST_FOUND_COMPLETED',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Lost Item Claimed',
    content: `${itemName} was claimed by ${payload.userId}.`,
    type: 'success'
  });

  res.json({ success: true, data: payload });
});

app.post('/api/ai/digital-human/summon', (req, res) => {
  const { userId, location, request } = req.body;
  
  io.emit('data_sync', {
    type: 'DIGITAL_HUMAN_SUMMONED',
    payload: { userId, location, request, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'AI Digital Human Deployed',
    content: `Hologram dispatched to ${location} for ${userId}`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/server/crash', (req, res) => {
  const { userId, gameSessionId, amount } = req.body;
  
  // 1. Alert CS / IT
  io.emit('data_sync', {
    type: 'SERVER_CRASH_ALERT',
    payload: { gameSessionId, timestamp: new Date().toISOString() }
  });

  // 2. Simulate AI Finance auto-refund
  setTimeout(() => {
    io.emit('data_sync', {
      type: 'AUTO_REFUND_PROCESSED',
      payload: { userId, amount, reason: 'Server Crash', timestamp: new Date().toISOString() }
    });

    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Auto Refund Processed',
      content: `Refunded ¥${amount} to ${userId} due to server crash.`,
      type: 'success'
    });
  }, 3000);

  res.json({ success: true });
});

app.post('/api/iot/vip-env', (req, res) => {
  const { userId, lightColor, temperature } = req.body;
  
  io.emit('data_sync', {
    type: 'VIP_ENV_UPDATED',
    payload: { userId, lightColor, temperature, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'VIP Zone Environment Adjusted',
    content: `HVAC set to ${temperature}°C, lights to ${lightColor} for ${userId}`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/station/unlock', (req, res) => {
  const { stationId, userId } = req.body;
  
  io.emit('data_sync', {
    type: 'STATION_UNLOCKED',
    payload: { stationId, userId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'PC Station Unlocked',
    content: `${stationId} unlocked by ${userId}`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/hr/leave', (req, res) => {
  const { employeeId, name, date, type } = req.body;
  
  io.emit('data_sync', {
    type: 'ESS_LEAVE_REQUEST',
    payload: { employeeId, name, date, leaveType: type, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Leave Request (ESS)',
    content: `${name} requested ${type} on ${date}.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/iot/locker/rent', (req, res) => {
  res.json({ success: true });
});

app.post('/api/iot/locker/return', (req, res) => {
  res.json({ success: true });
});

app.post('/api/iot/locker/overdue', (req, res) => {
  const { lockerId, userId } = req.body;
  
  io.emit('data_sync', {
    type: 'LOCKER_OVERDUE_ALARM',
    payload: { lockerId, userId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Locker Overdue',
    content: `${lockerId} is overdue by ${userId}.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/aigc/push_feed', (req, res) => {
  const { author, content, tags, vip } = req.body;
  
  io.emit('data_sync', {
    type: 'AIGC_CONTENT_PUBLISHED',
    payload: { author, content, tags, vip, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'AIGC Content Published',
    content: `New post by ${author} pushed to App Feed.`,
    type: 'success'
  });

  res.json({ success: true });
});

app.get('/api/safety/incidents', (req, res) => {
  res.json({
    success: true,
    data: {
      incidents: ensureSafetyIncidents(),
      hazards: ensureSafetyHazards()
    }
  });
});

app.post('/api/safety/incident/report', (req, res) => {
  const { storeId, type, location, reporterId, description } = req.body;
  const reportedAt = new Date().toISOString();
  const incidentId = `INC-${Date.now()}`;
  const severity = ['FIRE', 'MEDICAL'].includes(type) ? 'CRITICAL' : 'HIGH';
  const incident = {
    id: incidentId,
    type,
    level: severity,
    description: description || '前线员工一键上报',
    reporter: reporterId || 'EMP-001',
    reporterId: reporterId || 'EMP-001',
    storeId: storeId || '深圳南山旗舰店',
    location: location || 'VR 赛博战场',
    reportedAt,
    status: 'ACTIVE'
  };
  ensureSafetyIncidents().unshift(incident);
  const workflowTask = {
    id: `W-${Date.now()}`,
    type: 'SAFETY_INCIDENT',
    processName: '安全应急协同',
    title: `突发事件 · ${type}`,
    priority: severity === 'CRITICAL' ? 'HIGH' : 'MEDIUM',
    status: 'NEW',
    starter: incident.reporterId,
    nodeName: '待应急指挥',
    relatedId: incidentId,
    relatedType: 'SAFETY_INCIDENT',
    createdAt: reportedAt,
    externalWorkspace: 'admin',
    externalPath: `/workflow/list?relatedId=${encodeURIComponent(incidentId)}&type=SAFETY_INCIDENT`,
    assignee: '5174 应急指挥台'
  };
  ensureWorkflowTickets().unshift(workflowTask);
  incident.workflowTaskId = workflowTask.id;

  io.emit('data_sync', {
    type: 'EMERGENCY_INCIDENT',
    payload: { ...incident, workflowTaskId: workflowTask.id, timestamp: reportedAt }
  });
  io.emit('data_sync', {
    type: 'WORKFLOW_TASK_CREATED',
    payload: workflowTask
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'CRITICAL: EMERGENCY INCIDENT',
    content: `${type} reported at ${incident.storeId} (${incident.location})`,
    type: 'error'
  });

  res.json({
    success: true,
    data: {
      incidentId,
      workflowTaskId: workflowTask.id,
      adminPath: `/sop/emergency?incidentId=${encodeURIComponent(incidentId)}`,
      businessPath: `/safety?incidentId=${encodeURIComponent(incidentId)}`
    }
  });
});

app.post('/api/safety/incident/resolve', (req, res) => {
  const { incidentId } = req.body || {};
  const incident = ensureSafetyIncidents().find(item => String(item.id) === String(incidentId));
  if (!incident) {
    return res.status(404).json({ success: false, message: 'incident not found' });
  }
  incident.status = 'RESOLVED';
  incident.resolvedAt = new Date().toISOString();
  io.emit('data_sync', {
    type: 'EMERGENCY_RESOLVED',
    payload: { incidentId: incident.id, resolvedAt: incident.resolvedAt }
  });
  res.json({ success: true, data: incident });
});

app.post('/api/koc/conversion', (req, res) => {
  const { kocId, taskId, taskTitle, commissionAmount } = req.body || {};
  const task = getKocTask(taskId, taskTitle);
  const koc = getKocRecord(kocId || 'CyberPlayer_01');
  const amount = Number(commissionAmount || 15.5);
  const gmv = Number(req.body?.gmv || Number((amount / Math.max((Number(task?.commissionRate || 15) / 100), 0.01)).toFixed(2)));

  if (task) {
    task.orders += 1;
    task.paid = Number((Number(task.paid || 0) + amount).toFixed(2));
    task.uv += Number(req.body?.uvIncrement || 18);
    task.gmv = Number((Number(task.gmv || 0) + gmv).toFixed(2));
  }

  if (koc) {
    koc.commission = Number((Number(koc.commission || 0) + amount).toFixed(2));
    koc.traffic = Number(koc.traffic || 0) + 1;
    koc.gmv = Number((Number(koc.gmv || 0) + gmv).toFixed(2));
    koc.tasksCompleted = Number(koc.tasksCompleted || 0) + 1;
    koc.conversions = Number(koc.conversions || 0) + 1;
    const trafficBase = Math.max(Number(koc.traffic || 0), 1);
    koc.conversionRate = Number(((Number(koc.conversions || 0) / trafficBase) * 100).toFixed(1));
  }

  const settlementRequest = createFinanceRequest({
    requestType: 'SETTLEMENT',
    businessType: 'KOC_COMMISSION',
    employeeId: koc.id,
    employeeName: koc.name,
    amount,
    desc: `KOC 佣金待结算 · ${task?.title || taskTitle}`,
    sourceTaskTitle: task?.title || taskTitle,
    channel: '微信零钱',
    account: '微信零钱账户 · 8848',
    voucherName: 'koc-commission-proof.png',
    voucherType: 'png',
    voucherUrl: 'https://finance.sportsant.cn/voucher/koc-commission-proof.png',
    voucherRemark: '待财务审核后打款',
    meta: {
      kocId: koc.id,
      taskId: task?.id
    }
  });

  pushKocActivity({
    type: 'KOC_CONVERSION',
    title: '成交佣金已回流',
    detail: `${koc.name} 通过《${task?.title || taskTitle}》完成成交，佣金 ¥${amount} 已同步到 5171 / 5174 / 5178，并进入财务待结算队列。`
  });
  relaySyncEvent({
    type: 'KOC_CONVERSION',
    payload: {
      kocId: koc.id,
      kocName: koc.name,
      taskId: task?.id,
      taskTitle: task?.title || taskTitle,
      requestId: settlementRequest.requestId,
      commissionAmount: amount,
      gmv,
      timestamp: new Date().toISOString()
    }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'KOC Affiliate Conversion',
    content: `${koc.name} earned ¥${amount} via ${task?.title || taskTitle}`,
    type: 'success'
  });

  res.json({ success: true, data: getKocOverview(koc.id) });
});

app.post('/api/crm/vip/entry', (req, res) => {
  const { userId, username, level } = req.body;
  
  io.emit('data_sync', {
    type: 'VIP_FACE_RECOGNITION',
    payload: { userId, username, level, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'VIP Entry Detected',
    content: `${level} member ${username} arrived at gate.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.get('/api/globalization/profile/:locale', (req, res) => {
  res.json({ success: true, locale: req.params.locale, settings: {} });
});

app.get('/api/notification/system/my', (req, res) => {
  res.json({
    success: true,
    data: {
      content: [
        { id: 1, title: 'Welcome to SPORTS ANT', content: 'System initialized.', isRead: false, createdAt: new Date().toISOString() }
      ],
      totalElements: 1
    }
  });
});

// System Audit Logs (PRD-080)
const generateAuditLogs = () => {
  const logs = [];
  const actions = ['LOGIN', 'UPDATE_CONFIG', 'DELETE_USER', 'EXPORT_DATA', 'ISSUE_COUPON', 'START_CAMPAIGN'];
  const resources = ['/api/auth', '/api/system', '/api/users', '/api/finance', '/api/marketing'];
  const statuses = ['SUCCESS', 'SUCCESS', 'SUCCESS', 'FAILED'];
  const users = ['admin', 'manager_li', 'finance_wang', 'marketing_zhang'];

  for (let i = 0; i < 30; i++) {
    logs.push({
      id: `log_${30 - i}`,
      timestamp: new Date(Date.now() - i * 3600000).toISOString(),
      user: users[Math.floor(Math.random() * users.length)],
      action: actions[Math.floor(Math.random() * actions.length)],
      resource: resources[Math.floor(Math.random() * resources.length)],
      ip: `192.168.1.${Math.floor(Math.random() * 255)}`,
      status: statuses[Math.floor(Math.random() * statuses.length)],
      details: 'System auto-generated mock log'
    });
  }
  return logs;
};

const mockAuditLogs = generateAuditLogs();

app.get('/api/system/audit', (req, res) => {
  const page = parseInt(req.query.page) || 1;
  const size = parseInt(req.query.size) || 30;
  const userFilter = req.query.user?.toLowerCase();
  const resourceFilter = req.query.resource?.toLowerCase();

  let filteredLogs = mockAuditLogs;

  if (userFilter) {
    filteredLogs = filteredLogs.filter(log => log.user.toLowerCase().includes(userFilter));
  }
  if (resourceFilter) {
    filteredLogs = filteredLogs.filter(log => log.resource.toLowerCase().includes(resourceFilter));
  }

  const total = filteredLogs.length;
  const startIndex = (page - 1) * size;
  const items = filteredLogs.slice(startIndex, startIndex + size);

  res.json({
    data: {
      items,
      total
    }
  });
});

app.get('/api/system/logs', (req, res) => {
  res.json({
    data: mockAuditLogs.slice(0, 30).map(log => ({
      timestamp: log.timestamp,
      username: log.user,
      action: log.action,
      resource: log.resource,
      status: log.status
    }))
  });
});

app.get('/api/audit/logs', (req, res) => {
  res.json({
    data: {
      items: mockAuditLogs.slice(0, 30),
      total: 30
    }
  });
});

app.post('/api/supplier/feedback', (req, res) => {
  const { supplier, sn, deviceName, issue } = req.body;
  
  io.emit('data_sync', {
    type: 'SUPPLIER_FEEDBACK_SENT',
    payload: { supplier, sn, deviceName, issue, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'R&D Feedback Dispatched',
    content: `Sent to ${supplier} regarding ${deviceName}`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/ai/strategy/apply', (req, res) => {
  const { strategyType, region } = req.body;
  
  io.emit('data_sync', {
    type: 'AI_STRATEGY_APPLIED',
    payload: { strategyType, region, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'AI Strategy Executed',
    content: `${strategyType} applied to ${region}`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/alliance/trigger', (req, res) => {
  const { partner, action } = req.body;
  
  io.emit('data_sync', {
    type: 'ALLIANCE_API_CALLED',
    payload: { partner, action, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Alliance API Triggered',
    content: `${partner} executed ${action}`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/finance/reconcile', (req, res) => {
  // Simulate heavy bank API connection and reconciliation processing
  setTimeout(() => {
    io.emit('data_sync', {
      type: 'RECONCILIATION_COMPLETED',
      payload: { 
        systemCount: 1420, 
        bankCount: 1420, 
        discrepancy: '¥0.00', 
        timestamp: new Date().toISOString() 
      }
    });
    
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Bank Reconciliation',
      content: `Completed with 0 discrepancies.`,
      type: 'success'
    });
  }, 4000);

  res.json({ success: true, message: 'Reconciliation started' });
});

app.post('/api/iot/hvac/control', (req, res) => {
  const { zoneId, zoneName, type, state } = req.body;
  
  io.emit('data_sync', {
    type: 'HVAC_LIGHTING_CHANGED',
    payload: { zoneId, zoneName, controlType: type, state, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Digital Twin Update',
    content: `${zoneName} ${type} changed to ${state}`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/gamification/achievement/claim', (req, res) => {
  const { userId, achievementId, name } = req.body;
  
  io.emit('data_sync', {
    type: 'ACHIEVEMENT_UNLOCKED',
    payload: { userId, achievementId, name, pointsReward: 500, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Achievement Unlocked',
    content: `${userId} unlocked ${name}`,
    type: 'success'
  });

  res.json({ success: true, pointsReward: 500 });
});

app.post('/api/metaverse/chat', (req, res) => {
  const { author, content, vip } = req.body;
  
  io.emit('data_sync', {
    type: 'METAVERSE_CHAT',
    payload: { author, content, vip, timestamp: new Date().toISOString() }
  });

  // Also notify admin system
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Metaverse World Chat',
    content: `${author}: ${content}`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/procurement/bidding/publish', (req, res) => {
  const { prId, title, itemsSummary } = req.body;
  
  io.emit('data_sync', {
    type: 'BIDDING_PUBLISHED',
    payload: { prId, title, itemsSummary, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Bidding Request',
    content: `Procurement: ${title} (${itemsSummary})`,
    type: 'info'
  });

  // Simulate supplier placing a bid after 5 seconds
  setTimeout(() => {
    io.emit('data_sync', {
      type: 'NEW_SUPPLIER_BID',
      payload: { 
        prId, 
        supplierName: '泡泡玛特(官方直供)', 
        bidAmount: '11500.00', 
        timestamp: new Date().toISOString() 
      }
    });
  }, 5000);

  res.json({ success: true });
});

app.post('/api/orders/refund', (req, res) => {
  const { userId, orderId, amount, reason } = req.body;
  
  io.emit('data_sync', {
    type: 'REFUND_REQUESTED',
    payload: { userId, orderId, amount, reason, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Refund Request',
    content: `${userId} requested refund for ${orderId} (¥${amount})`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/contract/dispatch', (req, res) => {
  const { contractId, partyB, phone } = req.body;
  
  // Simulate third party e-sign platform notifying user, and user signing it instantly
  setTimeout(() => {
    io.emit('data_sync', {
      type: 'CONTRACT_SIGNED',
      payload: { contractId, partyB, phone, timestamp: new Date().toISOString() }
    });

    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Contract Signed',
      content: `${partyB} has signed contract ${contractId}`,
      type: 'success'
    });
  }, 3000); // 3 seconds delay to simulate user signing

  res.json({ success: true });
});

app.post('/api/lostfound/register', (req, res) => {
  const { itemName, location, storage } = req.body;
  const payload = {
    itemName,
    location,
    storage,
    finder: '前台服务台',
    matchedUser: 'Player_Mobile_01',
    timestamp: new Date().toISOString()
  };
  
  io.emit('data_sync', {
    type: 'LOST_FOUND_REGISTERED',
    payload
  });

  io.emit('data_sync', {
    type: 'LOST_FOUND_AI_MATCH',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Lost & Found',
    content: `New item registered: ${itemName} found at ${location}`,
    type: 'info'
  });

  res.json({ success: true, data: payload });
});

app.post('/api/iot/locker/rent', (req, res) => {
  const { userId, lockerId } = req.body;
  
  io.emit('data_sync', {
    type: 'LOCKER_RENTED',
    payload: { userId, lockerId, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Smart Locker Update',
    content: `${userId} rented ${lockerId}`,
    type: 'info'
  });

  res.json({ success: true });
});

app.post('/api/fnb/orders', (req, res) => {
  const { userId, tableNo, items, item, qty, total, useRobot } = req.body;
  const orderId = `FNB${Date.now()}`;
  const normalizedItems = Array.isArray(items) && items.length
    ? items
    : [{ name: item || '未知餐品', quantity: qty || 1 }];
  const payload = {
    userId,
    tableNo,
    items: normalizedItems,
    item: normalizedItems[0]?.name || item || '未知餐品',
    qty: normalizedItems[0]?.quantity || qty || 1,
    total,
    useRobot: Boolean(useRobot),
    orderId,
    timestamp: new Date().toISOString()
  };
  
  io.emit('data_sync', {
    type: 'FNB_ORDER_PLACED',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New F&B Order',
    content: `Table ${tableNo} ordered ${normalizedItems.length} items. Total: ¥${total}`,
    type: 'success'
  });

  res.json({ success: true, data: { status: 'ORDERED', orderId } });
});

app.post('/api/mall/products/status', (req, res) => {
  const { id, name, status } = req.body;
  
  io.emit('data_sync', {
    type: 'DYNAMIC_PRICING_UPDATE',
    payload: { id, name, status, timestamp: new Date().toISOString() }
  });

  res.json({ success: true });
});

app.post('/api/iot/energy/alert', (req, res) => {
  const { location, issue, value } = req.body;
  
  io.emit('data_sync', {
    type: 'ENERGY_ALERT',
    payload: { location, issue, value, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Energy Alert',
    content: `${location} - ${issue} detected.`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/franchise/apply', (req, res) => {
  const { name, phone, city, budget, model } = req.body;
  
  io.emit('data_sync', {
    type: 'FRANCHISE_APPLICATION',
    payload: { name, phone, city, budget, model, timestamp: new Date().toISOString() }
  });

  // Also push to CRM leads
  io.emit('state_update', {
    type: 'NEW_LEAD',
    payload: {
      name,
      company: `${city} 加盟意向`,
      source: 'FRANCHISE_SITE',
      timestamp: new Date().toISOString()
    }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Franchise Application',
    content: `${name} from ${city} applied for ${model}. Budget: ${budget}`,
    type: 'success'
  });

  res.json({ success: true });
});

app.post('/api/compliance/alert', (req, res) => {
  const { message, severity } = req.body;
  
  io.emit('data_sync', {
    type: 'COMPLIANCE_ALERT',
    payload: { message, severity, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Compliance Warning',
    content: message,
    type: severity === 'HIGH' ? 'error' : 'warning'
  });

  res.json({ success: true });
});

app.get('/api/store/inspection/reports', (req, res) => {
  const pendingHazards = ensureSafetyHazards().filter(item => item.source === '巡检整改' || item.status !== 'Resolved');
  res.json({
    success: true,
    data: {
      reports: ensureInspectionReports(),
      hazards: pendingHazards
    }
  });
});

app.post('/api/store/inspection/submit', (req, res) => {
  const { storeId, template, inspector, items } = req.body;
  
  // Calculate average score
  let totalScore = 0;
  let count = 0;
  let hasIssues = false;
  const hazardRecords = [];
  items.forEach(item => {
    if (item.score !== null) {
      totalScore += item.score;
      count++;
      if (item.score < 10) {
        hasIssues = true;
        hazardRecords.push({
          id: `HZ-${Date.now()}-${item.id}`,
          title: item.title,
          location: storeId,
          reporter: inspector,
          severity: item.score === 0 ? 'High' : 'Medium',
          status: 'Pending Fix',
          time: new Date().toLocaleString(),
          source: '巡检整改'
        });
      }
    }
  });
  const avgScore = count > 0 ? (totalScore / count) * 10 : 0; // Convert to 100 base
  const reportId = `IR-${Date.now()}`;
  let workflowTask = null;
  if (hasIssues) {
    workflowTask = {
      id: `W-${Date.now()}`,
      type: 'INSPECTION_REMEDIATION',
      processName: '巡检整改协同',
      title: `巡检整改 · ${storeId}`,
      priority: avgScore < 80 ? 'HIGH' : 'MEDIUM',
      status: 'NEW',
      starter: inspector,
      nodeName: '待整改督办',
      relatedId: reportId,
      relatedType: 'INSPECTION_REMEDIATION',
      createdAt: new Date().toISOString(),
      externalWorkspace: 'admin',
      externalPath: `/workflow/list?relatedId=${encodeURIComponent(reportId)}&type=INSPECTION_REMEDIATION`,
      assignee: '5174 品控协同台'
    };
    ensureWorkflowTickets().unshift(workflowTask);
  }
  const reportRecord = {
    id: reportId,
    store: storeId,
    time: new Date().toISOString().replace('T', ' ').slice(0, 16),
    inspector,
    score: Number(avgScore.toFixed(1)),
    status: hasIssues ? '待整改' : '合格',
    workflowTaskId: workflowTask?.id || '',
    template
  };
  ensureInspectionReports().unshift(reportRecord);
  if (hazardRecords.length) {
    ensureSafetyHazards().unshift(...hazardRecords);
  }

  io.emit('data_sync', {
    type: 'INSPECTION_SUBMITTED',
    payload: { 
      reportId,
      storeId, 
      inspector, 
      score: avgScore.toFixed(1), 
      hasIssues,
      workflowTaskId: workflowTask?.id || '',
      timestamp: new Date().toISOString() 
    }
  });
  if (workflowTask) {
    io.emit('data_sync', {
      type: 'WORKFLOW_TASK_CREATED',
      payload: workflowTask
    });
  }

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Store Inspection',
    content: `Store ${storeId} inspection completed. Score: ${avgScore.toFixed(1)}/100`,
    type: hasIssues ? 'warning' : 'success'
  });

  res.json({
    success: true,
    data: {
      reportId,
      score: Number(avgScore.toFixed(1)),
      hasIssues,
      workflowTaskId: workflowTask?.id || '',
      adminPath: workflowTask?.externalPath || '',
      businessPath: `/store-inspection?reportId=${encodeURIComponent(reportId)}`
    }
  });
});

app.post('/api/iot/maintenance/dispatch', (req, res) => {
  const { deviceId, deviceName, fault } = req.body;
  
  io.emit('data_sync', {
    type: 'MAINTENANCE_DISPATCHED',
    payload: { deviceId, deviceName, fault, timestamp: new Date().toISOString() }
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Maintenance Task',
    content: `Repair assigned for ${deviceName}: ${fault}`,
    type: 'warning'
  });

  res.json({ success: true });
});

app.post('/api/game/score', (req, res) => {
  const { userId, gameId, scoreDelta } = req.body;
  
  io.emit('data_sync', {
    type: 'GAME_SCORE_UPDATE',
    payload: { userId, gameId, scoreDelta, timestamp: new Date().toISOString() }
  });

  res.json({ success: true });
});

app.post('/api/training/enroll', (req, res) => {
  const { employeeId, courseId, courseName } = req.body;
  
  io.emit('data_sync', {
    type: 'TRAINING_ENROLLED',
    payload: { employeeId, courseId, courseName, timestamp: new Date().toISOString() }
  });

  res.json({ success: true });
});

app.post('/api/tournament/register', (req, res) => {
  const { userId, tournamentId, tournamentName } = req.body;
  const payload = {
    userId,
    user: userId,
    tournamentId,
    tournamentName: tournamentName || tournamentId,
    timestamp: new Date().toISOString()
  };
  
  io.emit('data_sync', {
    type: 'TOURNAMENT_REGISTERED',
    payload
  });

  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Tournament Registration',
    content: `${userId} registered for ${payload.tournamentName}. Bracket updated.`,
    type: 'info'
  });

  res.json({ success: true, data: { status: 'JOINED', ...payload } });
});

app.post('/api/crm/leads', (req, res) => {
  const lead = {
    id: `L${Date.now()}`,
    ...req.body,
    status: 'NEW',
    score: Math.floor(Math.random() * 40) + 60, // 60-100
    createdAt: new Date().toISOString()
  };
  
  // db.leads.push(lead);
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New CRM Lead Captured',
    content: `${lead.name} from ${lead.source}`,
    type: 'info'
  });
  
  io.emit('data_sync', { type: 'NEW_LEAD_CAPTURED', payload: lead });
  
  res.json({ success: true, lead });
});

app.post('/api/system/jobs/:id/trigger', (req, res) => {
  const jobId = req.params.id;
  
  // Simulate job execution
  setTimeout(() => {
    io.emit('data_sync', { 
      type: 'JOB_COMPLETED', 
      payload: { id: jobId, name: `Job ${jobId}` } 
    });
    
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Job Execution Complete',
      content: `Job ${jobId} finished successfully.`,
      type: 'success'
    });
  }, 2000);
  
  res.json({ success: true });
});

app.post('/api/system/jobs/:id/:action', (req, res) => {
  res.json({ success: true });
});

let aiConfig = {
  personality: 'cyberpunk',
  prompt: 'You are an AI assistant for Sports Ant...'
};

app.post('/api/ai/config', (req, res) => {
  aiConfig = { ...aiConfig, ...req.body };
  
  io.emit('data_sync', {
    type: 'AI_CONFIG_UPDATED',
    payload: aiConfig
  });
  
  res.json({ success: true, config: aiConfig });
});

app.post('/api/ai/chat', (req, res) => {
  const { message } = req.body;
  
  // Real-time broadcast to Admin dashboard
  io.emit('data_sync', { 
    type: 'AI_AGENT_CHAT', 
    payload: {
      message,
      timestamp: new Date().toISOString()
    }
  });
  
  // Simple mock AI response based on personality
  let reply = `This is a mock AI response for: "${message}". I'm here to help you with any questions about Sports Ant!`;
  if (aiConfig.personality === 'sarcastic') reply = `Oh great, another question: "${message}". Have you tried turning it off and on again?`;
  if (aiConfig.personality === 'cyberpunk') reply = `[SYSTEM_OVERRIDE]: Query "${message}" received. Processing data streams... The Matrix provides.`;
  if (aiConfig.personality === 'friendly') reply = `Hi there! 😊 I'd love to help you with: "${message}"! Let's get this sorted out together!`;
  
  res.json({
    reply,
    timestamp: new Date().toISOString()
  });
});

app.post('/api/mall/products/status', (req, res) => {
  const { id, status } = req.body;
  
  // Real-time broadcast to all frontends (Dynamic Pricing / Status sync)
  io.emit('data_sync', { 
    type: 'PRODUCT_UPDATE', 
    payload: { id, status, timestamp: new Date().toISOString() } 
  });
  
  res.json({ success: true });
});

app.post('/api/open/apps/register', (req, res) => {
  const appName = req.body.name || 'Unknown App';
  const newApp = {
    id: `app_${Date.now()}`,
    name: appName,
    appKey: `ak_${Math.random().toString(36).substring(2, 15)}`,
    appSecret: `as_${Math.random().toString(36).substring(2, 15)}`,
    status: 'ACTIVE'
  };
  
  // Real-time broadcast
  io.emit('data_sync', { 
    type: 'NEW_OPEN_APP', 
    payload: newApp 
  });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Open Platform Event',
    content: `New application registered: ${appName}`,
    type: 'info'
  });
  
  res.json({ success: true, app: newApp });
});

app.post('/api/sop/publish', (req, res) => {
  const sop = req.body;
  
  // Real-time broadcast to mobile staff app
  io.emit('data_sync', { 
    type: 'SOP_UPDATED', 
    payload: {
      id: sop.id,
      title: sop.title,
      version: sop.version,
      timestamp: new Date().toISOString()
    }
  });
  
  res.json({ success: true });
});

app.post('/api/game/status', (req, res) => {
  const { userId, game, status } = req.body;
  
  io.emit('data_sync', {
    type: 'GAME_STATUS_CHANGED',
    payload: { userId, game, status, timestamp: new Date().toISOString() }
  });
  
  res.json({ success: true });
});

app.post('/api/membership/points/add', (req, res) => {
  const { userId, points } = req.body;
  
  // Real-time broadcast to mobile/pc profile
  io.emit('data_sync', { 
    type: 'POINTS_UPDATED', 
    payload: {
      userId,
      pointsAdded: points,
      timestamp: new Date().toISOString()
    }
  });
  
  res.json({ success: true });
});

app.post('/api/marketing/coupons/issue', (req, res) => {
  const { name, discount } = req.body;
  const coupon = {
    id: `CPN-${Date.now()}`,
    name,
    discount,
    validUntil: new Date(Date.now() + 86400000 * 7).toISOString()
  };
  
  io.emit('data_sync', {
    type: 'COUPON_ISSUED',
    payload: coupon
  });
  
  res.json({ success: true, coupon });
});

app.post('/api/devices/error', (req, res) => {
  const { deviceId, name } = req.body;
  const device = db.devices.find(d => d.id === deviceId);
  
  if (device) {
    device.status = 'ERROR';
    
    // Auto-generate Work Order
    const ticket = { 
      id: `W-${Date.now()}`, 
      type: 'REPAIR', 
      title: `[AUTO] ${name} Hardware Failure`, 
      priority: 'HIGH', 
      status: 'NEW', 
      assigneeId: 'Unassigned', 
      createdAt: new Date().toISOString() 
    };
    db.workflow_tickets.unshift(ticket);
    
    io.emit('device_stats', db.devices);
    io.emit('data_sync', { type: 'WORK_ORDER_CREATED', payload: ticket });
    
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'IoT Device Alert',
      content: `${name} reported a hardware error. Work order created.`,
      type: 'error'
    });
  }
  res.json({ success: true });
});

app.get('/api/videos', (req, res) => {
  res.json(db.videos);
});

app.post('/api/videos/generate', (req, res) => {
  const { game, player } = req.body;
  const newVideo = {
    id: `vid_${Date.now()}`,
    title: `${player} - ${game} Gameplay Replay`,
    uploader: 'System Auto-Gen',
    views: 0,
    status: 'PUBLISHED',
    date: new Date().toISOString(),
    url: 'http://localhost:8080/images/asset_5b1f192a19571b7e80775bbad3051aef.mp4'
  };
  
  db.videos.unshift(newVideo);
  
  io.emit('data_sync', {
    type: 'NEW_REPLAY_GENERATED',
    payload: newVideo
  });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Game Replay Ready',
    content: `New auto-generated replay for ${player} is now available.`,
    type: 'success'
  });
  
  res.json({ success: true, video: newVideo });
});

app.post('/api/community/tournaments/start', (req, res) => {
  const { name, prize } = req.body;
  
  io.emit('data_sync', {
    type: 'TOURNAMENT_STARTED',
    payload: { name, prize, timestamp: new Date().toISOString() }
  });
  
  // Also push a global broadcast so everyone sees it immediately
  io.emit('data_sync', {
    type: 'GLOBAL_BROADCAST',
    payload: {
      message: `🏆 蚂蚁电竞全服锦标赛【${name}】现已开启！\n奖池: ${prize}\n快前往各门店设备端参与挑战吧！`,
      timestamp: new Date().toISOString()
    }
  });
  
  res.json({ success: true });
});

app.post('/api/hr/kpi/update', (req, res) => {
  const { employeeId, taskId, action } = req.body;
  
  io.emit('data_sync', {
    type: 'KPI_UPDATED',
    payload: { employeeId, taskId, action, timestamp: new Date().toISOString() }
  });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Performance KPI Updated',
    content: `Task completed by ${employeeId}. KPI increased.`,
    type: 'success'
  });
  
  res.json({ success: true });
});

app.post('/api/feedback/submit', (req, res) => {
  const feedback = {
    id: `FB-${Date.now()}`,
    ...req.body,
    status: 'PENDING',
    date: new Date().toISOString()
  };
  
  io.emit('data_sync', {
    type: 'NEW_PLAYER_FEEDBACK',
    payload: feedback
  });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Player Feedback',
    content: `${feedback.user} submitted a ${feedback.rating}-star review.`,
    type: 'warning'
  });
  
  res.json({ success: true });
});

app.post('/api/store/checkin', (req, res) => {
  const { player, venue } = req.body;
  
  io.emit('data_sync', {
    type: 'PLAYER_CHECKIN',
    payload: { player, venue, timestamp: new Date().toISOString() }
  });
  
  res.json({ success: true });
});

app.post('/api/supply/request', (req, res) => {
  const { item, quantity, requester } = req.body;
  const po = {
    id: `PO-${Date.now()}`,
    item,
    quantity,
    requester,
    status: 'PENDING',
    date: new Date().toISOString()
  };
  
  io.emit('data_sync', {
    type: 'NEW_PROCUREMENT_REQUEST',
    payload: po
  });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'New Procurement Request',
    content: `${requester} requested ${quantity}x ${item}.`,
    type: 'warning'
  });
  
  res.json({ success: true, po });
});

app.post('/api/supply/approve', (req, res) => {
  const { id } = req.body;
  // Simulate supplier fulfillment
  setTimeout(() => {
    io.emit('data_sync', {
      type: 'PROCUREMENT_FULFILLED',
      payload: { id, status: 'DELIVERED', timestamp: new Date().toISOString() }
    });
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'Supplier Update',
      content: `PO ${id} has been fulfilled by supplier.`,
      type: 'success'
    });
  }, 3000);
  
  res.json({ success: true });
});

app.post('/api/moderation/submit', (req, res) => {
  const post = {
    id: `POST-${Date.now()}`,
    ...req.body,
    status: 'PENDING',
    timestamp: new Date().toISOString()
  };
  
  io.emit('data_sync', { type: 'NEW_POST_FOR_MODERATION', payload: post });
  res.json({ success: true, post });
});

app.post('/api/moderation/resolve', (req, res) => {
  const { id, status } = req.body;
  io.emit('data_sync', {
    type: 'MODERATION_RESOLVED',
    payload: { id, status }
  });
  res.json({ success: true });
});

app.post('/api/i18n/publish', (req, res) => {
  const { keyName, zhCn } = req.body;
  
  // Simulate AI translation
  const translations = {
    'zh-CN': zhCn,
    'en-US': `[EN] ${zhCn}`,
    'ja-JP': `[JP] ${zhCn}`
  };
  
  io.emit('data_sync', {
    type: 'I18N_OTA_UPDATE',
    payload: { key: keyName, translations }
  });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'OTA Translation Update',
    content: `New translation key '${keyName}' pushed to all clients.`,
    type: 'info'
  });
  
  res.json({ success: true, translations });
});

app.post('/api/saas/tenant/config', (req, res) => {
  const { id, maxStores, features } = req.body;
  
  io.emit('data_sync', {
    type: 'TENANT_CONFIG_UPDATED',
    payload: { id, maxStores, features }
  });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'SaaS Plan Updated',
    content: `Tenant limits and features have been re-provisioned.`,
    type: 'info'
  });
  
  res.json({ success: true });
});

app.post('/api/metaverse/theme', (req, res) => {
  const { zoneId, theme } = req.body;
  
  io.emit('data_sync', {
    type: 'METAVERSE_THEME_UPDATED',
    payload: { zoneId, theme, timestamp: new Date().toISOString() }
  });
  
  res.json({ success: true });
});

app.post('/api/communication/broadcast', (req, res) => {
  const { title, message } = req.body;
  
  io.emit('data_sync', {
    type: 'GLOBAL_BROADCAST',
    payload: {
      message: `【系统通知】${title}\n${message}`,
      timestamp: new Date().toISOString()
    }
  });
  
  res.json({ success: true });
});

app.post('/api/iot/gate/open', (req, res) => {
  const { userId, zone } = req.body;
  
  // Find matching device by name or assume 'd2' (VR Pod)
  const device = db.devices.find(d => d.name.includes(zone) || d.name === zone) || db.devices.find(d => d.type === 'VR' && d.status === 'ONLINE');
  
  if (device) {
    device.status = 'BUSY';
    io.emit('device_stats', db.devices);
  }

  io.emit('data_sync', {
    type: 'GATE_OPENED',
    payload: { userId, zone, deviceId: device ? device.id : null, timestamp: new Date().toISOString() }
  });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Access Control',
    content: `VIP Gate/Device opened for ${userId} at ${zone}.`,
    type: 'success'
  });
  
  res.json({ success: true, device });
});

app.post('/api/hr/schedule/publish', (req, res) => {
  const { week, message } = req.body;
  
  io.emit('data_sync', {
    type: 'SCHEDULE_PUBLISHED',
    payload: { week, message, timestamp: new Date().toISOString() }
  });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'HR Schedule',
    content: `Schedule for ${week} has been published.`,
    type: 'info'
  });
  
  res.json({ success: true });
});

app.post('/api/game/score', (req, res) => {
  const { name, score } = req.body;
  
  io.emit('data_sync', {
    type: 'GAME_SCORE_SUBMITTED',
    payload: { name, score, timestamp: new Date().toISOString() }
  });
  
  res.json({ success: true });
});

// SaaS API Sandbox Endpoints
app.get('/api/open/test', (req, res) => {
  const appKey = req.headers['x-app-key'];
  if (!appKey) return res.status(401).json({ error: 'Missing X-App-Key' });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'Open API Call',
    content: `GET /api/open/test called with AppKey: ${appKey.substring(0,6)}...`,
    type: 'info'
  });
  
  res.json({ success: true, message: 'API connection successful', appKey });
});

app.post('/api/open/points/sync', (req, res) => {
  const appKey = req.headers['x-app-key'];
  if (!appKey) return res.status(401).json({ error: 'Missing X-App-Key' });
  
  io.emit('notification', {
    id: Date.now().toString(),
    title: 'B2B Points Sync',
    content: `Tenant synced ${req.body.points} points for user ${req.body.userId}.`,
    type: 'success'
  });
  
  res.json({ success: true, transactionId: `TX-${Date.now()}` });
});

app.post('/api/bi/export', (req, res) => {
  // Simulate long-running BI generation process
  setTimeout(() => {
    io.emit('data_sync', {
      type: 'BI_REPORT_READY',
      payload: { 
        link: 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
        timestamp: new Date().toISOString() 
      }
    });
    
    io.emit('notification', {
      id: Date.now().toString(),
      title: 'BI Report Ready',
      content: `Your requested Data Analytics report has been generated.`,
      type: 'success'
    });
  }, 4000);
  
  res.json({ success: true, message: 'Processing started' });
});

app.get('/api/mobile/home', (req, res) => {
  res.json({
    success: true,
    data: {
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
    }
  });
});

const PORT = Number(process.env.PORT || 8080);
require('./mobile-api')(app, io, db);

server.listen(PORT, () => {
  console.log(`Unified Mock Backend running on port ${PORT}`);
  console.log(`WebSocket Server ready`);
});
