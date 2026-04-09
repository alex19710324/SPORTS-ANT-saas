import type { VisitorIdentity } from './visitorProfiles'

export type AccessRule = {
  matcher: RegExp
  allow: VisitorIdentity[]
}

export const publicPaths = [
  '/',
  '/pages/login/index',
  '/pages/home/index',
  '/pages/play/index',
  '/pages/mall/index',
  '/pages/feed/index',
  '/pages/my/index'
]

export const customerAllowedMatchers = [
  /^\/pages\/login\//,
  /^\/pages\/home\//,
  /^\/pages\/play\//,
  /^\/pages\/mall\//,
  /^\/pages\/feed\//,
  /^\/pages\/my\//,
  /^\/pages\/event\//,
  /^\/pages\/order\//,
  /^\/pages\/payment\//,
  /^\/pages\/ticket\//,
  /^\/pages\/notification\//,
  /^\/pages\/nft\//,
  /^\/pages\/achievement\//,
  /^\/pages\/leaderboard\//,
  /^\/pages\/queue\//,
  /^\/pages\/jukebox\//,
  /^\/pages\/parking\//,
  /^\/pages\/lostfound\//,
  /^\/pages\/iot\/vip-env/,
  /^\/pages\/vip\/upgrade/,
  /^\/pages\/venue\//,
  /^\/pages\/health\//,
  /^\/pages\/help\//,
  /^\/pages\/tournament\//,
  /^\/pages\/metaverse\//,
  /^\/pages\/locker\//,
  /^\/pages\/wallet\//,
  /^\/pages\/map\//,
  /^\/pages\/deposit\//,
  /^\/pages\/rental\//,
  /^\/pages\/feedback\//,
  /^\/pages\/fnb\//,
  /^\/pages\/desk\//,
  /^\/pages\/meeting\//
]

export const accessRules: AccessRule[] = [
  { matcher: /^\/pages\/invoice\//, allow: ['store_manager', 'cashier', 'business', 'staff'] },
  { matcher: /^\/pages\/koc\//, allow: ['promoter', 'business', 'store_manager', 'staff'] },
  { matcher: /^\/pages\/scan\//, allow: ['cashier', 'guide', 'staff', 'store_manager'] },
  { matcher: /^\/pages\/schedule\//, allow: ['store_manager', 'cashier', 'technician', 'promoter', 'guide', 'staff'] },
  { matcher: /^\/pages\/inspection\//, allow: ['store_manager', 'technician', 'staff'] },
  { matcher: /^\/pages\/safety\//, allow: ['store_manager', 'technician', 'staff'] },
  { matcher: /^\/pages\/inventory\//, allow: ['store_manager', 'technician', 'staff'] },
  { matcher: /^\/pages\/wiki\//, allow: ['store_manager', 'cashier', 'technician', 'promoter', 'guide', 'business', 'staff'] },
  { matcher: /^\/pages\/social\//, allow: ['promoter', 'business', 'staff'] },
  { matcher: /^\/pages\/meeting\//, allow: ['guest', 'player', 'store_manager', 'cashier', 'technician', 'promoter', 'guide', 'business', 'staff'] },
  { matcher: /^\/pages\/training\//, allow: ['store_manager', 'cashier', 'technician', 'promoter', 'guide', 'staff'] },
  { matcher: /^\/pages\/workflow\//, allow: ['store_manager', 'cashier', 'technician', 'promoter', 'guide', 'staff'] },
  { matcher: /^\/pages\/chat\//, allow: ['store_manager', 'cashier', 'technician', 'promoter', 'guide', 'business', 'staff'] },
  { matcher: /^\/pages\/task\//, allow: ['store_manager', 'cashier', 'technician', 'promoter', 'guide', 'staff'] },
  { matcher: /^\/pages\/desk\//, allow: ['guest', 'player', 'store_manager', 'cashier', 'guide', 'staff'] },
  { matcher: /^\/pages\/notice\//, allow: ['store_manager', 'cashier', 'technician', 'promoter', 'guide', 'business', 'staff'] },
  { matcher: /^\/pages\/marketing\//, allow: ['business', 'promoter', 'store_manager'] },
  { matcher: /^\/pages\/open\//, allow: ['business', 'store_manager', 'staff'] },
  { matcher: /^\/pages\/iot\/vip-env/, allow: ['guest', 'player'] },
  { matcher: /^\/pages\/vip\/upgrade/, allow: ['guest', 'player'] }
]
