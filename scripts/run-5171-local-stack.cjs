const net = require('node:net')
const path = require('node:path')
const { spawn } = require('node:child_process')

const rootDir = path.resolve(__dirname, '..')
const servers = [
  {
    name: '8080 mock-server',
    cwd: path.join(rootDir, 'mock-server'),
    command: 'npm run start',
    port: 8080,
    timeoutMs: 30000
  },
  {
    name: '5171 frontend-uniapp',
    cwd: path.join(rootDir, 'frontend-uniapp'),
    command: 'npm run dev',
    port: 5171,
    timeoutMs: 60000
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

const isPortReady = (port) =>
  new Promise((resolve) => {
    const socket = net.createConnection({ host: '127.0.0.1', port })
    socket.once('connect', () => {
      socket.end()
      resolve(true)
    })
    socket.once('error', () => resolve(false))
  })

const startServer = async (server) => {
  if (await isPortReady(server.port)) {
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
  const readyPromise = waitForPort(server.port, server.timeoutMs).then(() => 'ready')
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
  const handleSignal = (signal) => {
    stopAll()
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
    await new Promise(() => {})
  } finally {
    stopAll()
  }
}

main().catch((error) => {
  process.stderr.write(`${error.stack || error.message}\n`)
  process.exit(1)
})
