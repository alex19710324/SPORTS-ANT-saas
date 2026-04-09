const net = require('node:net')
const http = require('node:http')
const path = require('node:path')
const { spawn } = require('node:child_process')

const rootDir = path.resolve(__dirname, '..')
const servers = [
  {
    name: '8080 mock-server',
    cwd: path.join(rootDir, 'mock-server'),
    command: 'node dev-5171-server.cjs',
    port: 8080,
    timeoutMs: 30000,
    healthPath: '/',
    isHealthy: (body) => body.includes('Mock Server is running')
  },
  {
    name: '5171 frontend-uniapp',
    cwd: path.join(rootDir, 'frontend-uniapp'),
    command: 'npm run dev',
    port: 5171,
    timeoutMs: 300000,
    healthPath: '/',
    isHealthy: (body) => body.includes('<!DOCTYPE html>') || body.includes('/@vite/client')
  }
]

const childProcesses = []

const prefixOutput = (stream, prefix) => {
  let buffer = ''
  stream.on('data', (chunk) => {
    buffer += chunk.toString()
    const lines = buffer.split(/\r?\n/)
    buffer = lines.pop() ?? ''
    for (const line of lines) {
      process.stdout.write(`[${prefix}] ${line}\n`)
    }
  })
  stream.on('end', () => {
    if (buffer) {
      process.stdout.write(`[${prefix}] ${buffer}\n`)
    }
  })
}

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

const waitForPort = async (port, timeoutMs) => {
  const start = Date.now()
  while (Date.now() - start < timeoutMs) {
    const ready = await new Promise((resolve) => {
      const socket = net.createConnection({ host: '127.0.0.1', port })
      socket.once('connect', () => {
        socket.end()
        resolve(true)
      })
      socket.once('error', () => resolve(false))
    })
    if (ready) return
    await sleep(1000)
  }
  throw new Error(`Port ${port} did not become ready within ${timeoutMs}ms`)
}

const requestHealth = (server) =>
  new Promise((resolve) => {
    const req = http.get(
      {
        hostname: '127.0.0.1',
        port: server.port,
        path: server.healthPath || '/',
        timeout: 3000
      },
      (res) => {
        let body = ''
        res.on('data', (chunk) => {
          body += chunk.toString()
        })
        res.on('end', () => {
          resolve(res.statusCode && res.statusCode < 500 && (!server.isHealthy || server.isHealthy(body, res.statusCode)))
        })
      }
    )
    req.on('error', () => resolve(false))
    req.on('timeout', () => {
      req.destroy()
      resolve(false)
    })
  })

const isPortReady = (port) =>
  new Promise((resolve) => {
    const socket = net.createConnection({ host: '127.0.0.1', port })
    socket.once('connect', () => {
      socket.end()
      resolve(true)
    })
    socket.once('error', () => resolve(false))
  })

const waitForHealthy = async (server, timeoutMs) => {
  const start = Date.now()
  while (Date.now() - start < timeoutMs) {
    if (await requestHealth(server)) return
    await sleep(1000)
  }
  throw new Error(`${server.name} 健康检查未在 ${timeoutMs}ms 内通过`)
}

const startServer = async (server) => {
  if (await isPortReady(server.port)) {
    if (!(await requestHealth(server))) {
      throw new Error(`${server.name} 端口 ${server.port} 已被占用，但当前服务健康检查未通过`)
    }
    process.stdout.write(`Reusing ${server.name}\n`)
    return null
  }
  const child = spawn(server.command, {
    cwd: server.cwd,
    shell: true,
    env: {
      ...process.env,
      FORCE_COLOR: '0'
    }
  })
  childProcesses.push(child)
  prefixOutput(child.stdout, server.name)
  prefixOutput(child.stderr, `${server.name}:err`)
  const readyPromise = waitForPort(server.port, server.timeoutMs)
    .then(() => waitForHealthy(server, server.timeoutMs))
    .then(() => 'ready')
  const exitPromise = new Promise((resolve, reject) => {
    child.once('exit', (code) => resolve(code))
    child.once('error', reject)
  })
  const result = await Promise.race([readyPromise, exitPromise])
  if (result === 'ready') return child
  throw new Error(`${server.name} exited before port ${server.port} became ready (code ${result})`)
}

const stopAll = () => {
  while (childProcesses.length) {
    const child = childProcesses.pop()
    if (!child || child.killed) continue
    try {
      child.kill('SIGTERM')
    } catch {}
  }
}

const main = async () => {
  let keepAliveTimer = null
  const handleSignal = (signal) => {
    stopAll()
    if (keepAliveTimer) clearInterval(keepAliveTimer)
    process.exit(signal === 'SIGINT' ? 130 : 143)
  }
  process.on('SIGINT', handleSignal)
  process.on('SIGTERM', handleSignal)

  try {
    for (const server of servers) {
      process.stdout.write(`Starting ${server.name}\n`)
      await startServer(server)
      process.stdout.write(`Ready ${server.name}\n`)
    }
    process.stdout.write('5171 本地联调已就绪\n')
    process.stdout.write('5171: http://127.0.0.1:5171/\n')
    process.stdout.write('8080: http://127.0.0.1:8080/\n')
    keepAliveTimer = setInterval(() => {}, 60_000)
    await new Promise(() => {})
  } finally {
    if (keepAliveTimer) clearInterval(keepAliveTimer)
    stopAll()
  }
}

main().catch((error) => {
  process.stderr.write(`${error.stack || error.message}\n`)
  process.exit(1)
})
