#!/usr/bin/env node

const fs = require('node:fs')
const path = require('node:path')

const pidFile = path.join(__dirname, '..', '.git', 'auto-commit-daemon.pid')

if (!fs.existsSync(pidFile)) {
  process.stdout.write('not-running\n')
  process.exit(0)
}

const pid = Number(fs.readFileSync(pidFile, 'utf8').trim())

try {
  process.kill(pid, 0)
  process.stdout.write(`running:${pid}\n`)
} catch {
  fs.unlinkSync(pidFile)
  process.stdout.write('not-running\n')
}
