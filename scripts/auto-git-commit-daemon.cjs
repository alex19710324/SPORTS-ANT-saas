#!/usr/bin/env node

const fs = require('node:fs')
const path = require('node:path')
const { spawn } = require('node:child_process')

const repoRoot = path.resolve(__dirname, '..')
const pidFile = path.join(repoRoot, '.git', 'auto-commit-daemon.pid')
const intervalMs = 30 * 60 * 1000
let timer = null

const pidIsRunning = (pid) => {
  if (!pid || Number.isNaN(Number(pid))) return false
  try {
    process.kill(Number(pid), 0)
    return true
  } catch {
    return false
  }
}

const cleanup = () => {
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
  if (fs.existsSync(pidFile)) {
    fs.unlinkSync(pidFile)
  }
}

const schedule = (delay) => {
  timer = setTimeout(runOnce, delay)
}

const runOnce = () => {
  const child = spawn(process.execPath, [path.join(__dirname, 'auto-git-commit.cjs')], {
    cwd: repoRoot,
    stdio: 'inherit'
  })

  child.on('exit', () => {
    schedule(intervalMs)
  })
}

if (fs.existsSync(pidFile)) {
  const existingPid = Number(fs.readFileSync(pidFile, 'utf8').trim())
  if (pidIsRunning(existingPid)) {
    process.stdout.write(`already-running:${existingPid}\n`)
    process.exit(0)
  }
  fs.unlinkSync(pidFile)
}

fs.writeFileSync(pidFile, `${process.pid}\n`)
process.on('SIGINT', () => {
  cleanup()
  process.exit(0)
})
process.on('SIGTERM', () => {
  cleanup()
  process.exit(0)
})
process.on('exit', cleanup)

process.stdout.write(`started:${process.pid}\n`)
runOnce()
