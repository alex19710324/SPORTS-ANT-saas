const http = require('node:http')

const args = process.argv.slice(2)
const options = Object.fromEntries(
  args.reduce((pairs, value, index, array) => {
    if (!value.startsWith('--')) return pairs
    pairs.push([value.slice(2), array[index + 1]])
    return pairs
  }, [])
)

const app = options.app || 'mobile'
const port = Number(options.port || 3000)

const html = `<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Regression Fixture</title>
    <style>
      body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; background:#0b1220; color:#e5e7eb; margin:0; }
      main { max-width: 960px; margin: 0 auto; padding: 32px 24px; }
      h1 { margin: 0 0 12px; font-size: 32px; }
      .card { background:#111827; border:1px solid #334155; border-radius:16px; padding:20px; margin-top:16px; }
      .tag { display:inline-block; padding:4px 10px; border-radius:999px; background:#0f172a; border:1px solid #38bdf8; color:#67e8f9; font-size:12px; margin-right:8px; }
      .muted { color:#94a3b8; }
      .mono { font-family: ui-monospace, SFMono-Regular, Menlo, monospace; }
    </style>
  </head>
  <body>
    <main id="app"></main>
    <script>
      const app = ${JSON.stringify(app)}
      const root = document.getElementById('app')

      const card = (title, body) => '<div class="card"><h2>' + title + '</h2><div>' + body + '</div></div>'

      const renderMobile = () => {
        const hashPath = location.hash.replace(/^#/, '') || '/'
        if (hashPath.startsWith('/pages/rental/index')) {
          root.innerHTML = '<h1>设备租赁</h1>' + card('跨端协同', '去 5176 玩家中心 · 去 5178 租赁运营')
          return
        }
        if (hashPath.startsWith('/pages/safety/index')) {
          root.innerHTML = '<h1>安全与应急</h1>' + card('跨端应急协同', '去 5174 应急台 · 去 5178 安全台')
          return
        }
        if (hashPath.startsWith('/pages/inspection/index')) {
          root.innerHTML = '<h1>门店巡查</h1>' + card('巡检说明', '支持离线缓存')
          return
        }
        if (hashPath.startsWith('/pages/health/index')) {
          root.innerHTML = '<h1>健康数据协同</h1>' + card('跨端健康联动', '去 5174 健康评分台')
          return
        }
        if (hashPath.startsWith('/pages/wallet/did')) {
          root.innerHTML = '<h1>DID 协同入口</h1>' + card('跨端 Web3 联动', '去 5178 DID 台')
          return
        }
        root.innerHTML = '<h1>SPORTS ANT Mobile</h1>'
      }

      const renderAdmin = () => {
        const path = location.pathname
        const search = new URLSearchParams(location.search)
        const userId = search.get('userId') || ''
        const relatedId = search.get('relatedId') || ''
        if (path === '/sop/emergency') {
          root.innerHTML = '<h1>应急响应控制台</h1>' + card('事件描述', '四端回归测试-医疗支援')
          return
        }
        if (path === '/workflow/list') {
          root.innerHTML = '<h1>Work Order Management</h1>' + card('工单', '<span class="mono">' + relatedId + '</span> · INSPECTION_REMEDIATION')
          return
        }
        if (path === '/member/health') {
          root.innerHTML = '<h1>AI智能会员健康评分系统</h1>' + card('上下文', '5171 健康页已带入用户上下文，当前评分台优先关注用户 ' + userId + ' 的健康同步与干预数据。')
          return
        }
        if (path === '/trendy/nft') {
          root.innerHTML = '<h1>NFT / DID 控制台</h1>' + card('上下文', '5171 Web3 页面已带入用户上下文，当前 NFT / DID 控制台优先处理用户 ' + userId + ' 的数字资产同步与身份验证。')
          return
        }
        root.innerHTML = '<h1>SPORTS ANT Admin</h1>'
      }

      const renderPlayer = () => {
        const path = location.pathname
        const search = new URLSearchParams(location.search)
        const userId = search.get('userId') || ''
        const orderId = search.get('orderId') || ''
        if (path === '/profile') {
          if (search.get('tab') === 'rentals') {
            root.innerHTML = '<h1>玩家中心</h1>' + card('设备租赁', '<span class="mono">' + orderId + '</span> · 设备租赁')
            return
          }
          if (search.get('tab') === 'wallet') {
            root.innerHTML = '<h1>玩家中心</h1>' + card('数字资产上下文', '5171 Web3 Intake · 用户 ' + userId + ' 的数字资产上下文')
            return
          }
        }
        if (path === '/health') {
          root.innerHTML = '<h1>HEALTH & METRICS</h1>' + card('同步状态', '5171 Health Sync · 健康里程碑已从 5171 同步')
          return
        }
        root.innerHTML = '<h1>SPORTS ANT Player</h1>'
      }

      const renderBusiness = () => {
        const path = location.pathname
        const search = new URLSearchParams(location.search)
        const userId = search.get('userId') || ''
        const orderId = search.get('orderId') || ''
        const reportId = search.get('reportId') || ''
        if (path === '/rental-manager') {
          root.innerHTML = '<h1>设备租赁管理 RENTAL MGR</h1>' + card('5171 Rental Intake', '<span class="mono">' + orderId + '</span>')
          return
        }
        if (path === '/safety') {
          root.innerHTML = '<h1>安全管理台</h1>' + card('5171 SAFETY INTAKE', '已从 5171 安全与应急页带入事件上下文')
          return
        }
        if (path === '/store-inspection') {
          root.innerHTML = '<h1>Store Inspection</h1>' + card('5171 Inspection Intake', '<span class="mono">' + reportId + '</span>')
          return
        }
        if (path === '/health-data') {
          root.innerHTML = '<h1>Health Data</h1>' + card('5171 HEALTH INTAKE', '用户 ' + userId + ' 的健康里程碑已同步到业务台')
          return
        }
        if (path === '/did-wallet') {
          root.innerHTML = '<h1>DID Wallet</h1>' + card('5171 WEB3 INTAKE', '用户 ' + userId + ' 的 DID / NFT 上下文已同步到业务台')
          return
        }
        root.innerHTML = '<h1>SPORTS ANT Business</h1>'
      }

      if (app === 'mobile') renderMobile()
      if (app === 'admin') renderAdmin()
      if (app === 'player') renderPlayer()
      if (app === 'business') renderBusiness()
      if (app === 'mobile') window.addEventListener('hashchange', renderMobile)
    </script>
  </body>
</html>`

const server = http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8', 'Cache-Control': 'no-cache' })
  res.end(html)
})

server.listen(port, '127.0.0.1', () => {
  process.stdout.write(`Regression fixture ${app} ready on http://127.0.0.1:${port}\n`)
})
