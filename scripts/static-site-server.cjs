const http = require('node:http')
const fs = require('node:fs')
const path = require('node:path')
const { URL } = require('node:url')

const args = process.argv.slice(2)
const options = Object.fromEntries(
  args.reduce((pairs, value, index, array) => {
    if (!value.startsWith('--')) return pairs
    pairs.push([value.slice(2), array[index + 1]])
    return pairs
  }, [])
)

const root = path.resolve(options.root || '.')
const port = Number(options.port || 3000)
const mode = options.mode || 'spa'
const proxyPrefixes = ['/api/', '/socket.io/', '/images/']

const contentTypes = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.mjs': 'application/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.webp': 'image/webp',
  '.gif': 'image/gif',
  '.ico': 'image/x-icon',
  '.txt': 'text/plain; charset=utf-8',
  '.xml': 'application/xml; charset=utf-8',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.ttf': 'font/ttf',
  '.mp4': 'video/mp4'
}

const safeJoin = (base, target) => {
  const resolved = path.resolve(base, target)
  if (!resolved.startsWith(base)) return null
  return resolved
}

const sendFile = (res, filePath) => {
  const ext = path.extname(filePath).toLowerCase()
  res.writeHead(200, {
    'Content-Type': contentTypes[ext] || 'application/octet-stream',
    'Cache-Control': 'no-cache'
  })
  fs.createReadStream(filePath).pipe(res)
}

const proxyRequest = (req, res) => {
  const upstream = http.request(
    {
      hostname: '127.0.0.1',
      port: 8080,
      path: req.url,
      method: req.method,
      headers: {
        ...req.headers,
        host: '127.0.0.1:8080'
      }
    },
    (upstreamRes) => {
      res.writeHead(upstreamRes.statusCode || 502, upstreamRes.headers)
      upstreamRes.pipe(res)
    }
  )
  upstream.on('error', () => {
    res.writeHead(502, { 'Content-Type': 'text/plain; charset=utf-8' })
    res.end('Bad Gateway')
  })
  req.pipe(upstream)
}

const findFile = (pathname) => {
  const cleanPath = pathname === '/' ? '/' : pathname.replace(/\/+$/, '')

  if (mode === 'spa') {
    if (cleanPath !== '/') {
      const directFile = safeJoin(root, cleanPath.slice(1))
      if (directFile && fs.existsSync(directFile) && fs.statSync(directFile).isFile()) {
        return directFile
      }
    }
    return safeJoin(root, 'index.html')
  }

  if (mode === 'mpa') {
    if (cleanPath === '/') {
      return safeJoin(root, 'index.html')
    }
    const directFile = safeJoin(root, cleanPath.slice(1))
    if (directFile && fs.existsSync(directFile) && fs.statSync(directFile).isFile()) {
      return directFile
    }
    const htmlFile = safeJoin(root, `${cleanPath.slice(1)}.html`)
    if (htmlFile && fs.existsSync(htmlFile) && fs.statSync(htmlFile).isFile()) {
      return htmlFile
    }
    return safeJoin(root, 'index.html')
  }

  return safeJoin(root, 'index.html')
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, `http://${req.headers.host || '127.0.0.1'}`)
  if (proxyPrefixes.some((prefix) => url.pathname.startsWith(prefix))) {
    proxyRequest(req, res)
    return
  }
  const filePath = findFile(url.pathname)

  if (!filePath || !fs.existsSync(filePath)) {
    res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' })
    res.end('Not Found')
    return
  }

  sendFile(res, filePath)
})

server.listen(port, '127.0.0.1', () => {
  process.stdout.write(`Static server ready on http://127.0.0.1:${port}\n`)
})
