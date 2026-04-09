const net = require('node:net')
const path = require('node:path')
const { spawn } = require('node:child_process')

const rootDir = path.resolve(__dirname, '..')
const regressionFixtureScript = path.join(rootDir, 'scripts', 'regression-fixture-server.cjs')
const servers = [
  {
    name: '5171 frontend-uniapp',
    cwd: rootDir,
    entry: process.execPath,
    args: [regressionFixtureScript, '--app', 'mobile', '--port', '5171'],
    port: 5171,
    timeoutMs: 30000
  },
  {
    name: '5174 frontend',
    cwd: rootDir,
    entry: process.execPath,
    args: [regressionFixtureScript, '--app', 'admin', '--port', '5174'],
    port: 5174,
    timeoutMs: 30000
  },
  {
    name: '5176 frontend-player-site',
    cwd: rootDir,
    entry: process.execPath,
    args: [regressionFixtureScript, '--app', 'player', '--port', '5176'],
    port: 5176,
    timeoutMs: 30000
  },
  {
    name: '5178 frontend-business-site',
    cwd: rootDir,
    entry: process.execPath,
    args: [regressionFixtureScript, '--app', 'business', '--port', '5178'],
    port: 5178,
    timeoutMs: 30000
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
      socket.once('error', () => {
        resolve(false)
      })
    })
    if (ready) {
      return
    }
    await sleep(1000)
  }
  throw new Error(`Port ${port} did not become ready within ${timeoutMs}ms`)
}

const startServer = async (server) => {
  const child =
    server.entry && server.args
      ? spawn(server.entry, server.args, {
          cwd: server.cwd,
          env: {
            ...process.env,
            FORCE_COLOR: '0'
          }
        })
      : spawn(server.command, {
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
    child.once('exit', (code) => {
      resolve(code)
    })
    child.once('error', reject)
  })
  const result = await Promise.race([readyPromise, exitPromise])
  if (result === 'ready') {
    return child
  }
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

const runRegression = () =>
  new Promise((resolve, reject) => {
    const child = spawn(process.execPath, [path.join(rootDir, 'four-end-regression.cjs')], {
      cwd: rootDir,
      stdio: 'inherit',
      env: {
        ...process.env,
        FORCE_COLOR: '0'
      }
    })
    child.on('exit', (code) => {
      if (code === 0) {
        resolve()
        return
      }
      reject(new Error(`four-end-regression exited with code ${code}`))
    })
    child.on('error', reject)
  })

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
    await runRegression()
  } finally {
    stopAll()
  }
}

main().catch((error) => {
  process.stderr.write(`${error.stack || error.message}\n`)
  process.exit(1)
})
