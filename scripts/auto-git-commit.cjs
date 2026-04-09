#!/usr/bin/env node

const fs = require('node:fs')
const os = require('node:os')
const path = require('node:path')
const { execFileSync, spawnSync } = require('node:child_process')

const repoRoot = path.resolve(__dirname, '..')
const gitDir = path.join(repoRoot, '.git')
const lockFile = path.join(gitDir, 'auto-commit.lock')
const dryRun = process.argv.includes('--dry-run')

const runGit = (args, options = {}) =>
  execFileSync('git', args, {
    cwd: repoRoot,
    encoding: 'utf8',
    stdio: ['ignore', 'pipe', 'pipe'],
    ...options
  }).trim()

const now = new Date()
const pad = (value) => String(value).padStart(2, '0')
const timestamp = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`

const releaseLock = () => {
  if (fs.existsSync(lockFile)) {
    fs.unlinkSync(lockFile)
  }
}

const pidIsRunning = (pid) => {
  if (!pid || Number.isNaN(Number(pid))) return false
  const result = spawnSync('kill', ['-0', String(pid)], { stdio: 'ignore' })
  return result.status === 0
}

const acquireLock = () => {
  if (fs.existsSync(lockFile)) {
    const existingPid = Number(fs.readFileSync(lockFile, 'utf8').trim())
    if (pidIsRunning(existingPid)) {
      process.stdout.write(`skip: auto-commit already running with pid ${existingPid}\n`)
      process.exit(0)
    }
    fs.unlinkSync(lockFile)
  }
  fs.writeFileSync(lockFile, `${process.pid}\n`)
}

const ensureGitIdentity = () => {
  const name = runGit(['config', 'user.name'])
  const email = runGit(['config', 'user.email'])
  if (!name || !email) {
    throw new Error('git user.name 或 user.email 未配置，无法自动提交')
  }
}

const main = () => {
  if (!fs.existsSync(gitDir)) {
    throw new Error(`未找到 Git 仓库：${repoRoot}`)
  }

  acquireLock()
  process.on('exit', releaseLock)
  process.on('SIGINT', () => process.exit(1))
  process.on('SIGTERM', () => process.exit(1))

  ensureGitIdentity()

  const status = runGit(['status', '--porcelain'])
  if (!status) {
    process.stdout.write('skip: no changes\n')
    return
  }

  if (dryRun) {
    process.stdout.write(`dry-run: detected changes at ${timestamp}\n`)
    process.stdout.write(`${status}\n`)
    return
  }

  runGit(['add', '-A'])
  const stagedDiff = spawnSync('git', ['diff', '--cached', '--quiet'], { cwd: repoRoot, stdio: 'ignore' })
  if (stagedDiff.status === 0) {
    process.stdout.write('skip: nothing staged after git add -A\n')
    return
  }

  const message = `chore: auto snapshot ${timestamp}`
  runGit(['commit', '-m', message], { stdio: ['ignore', 'pipe', 'pipe'] })
  const commitHash = runGit(['rev-parse', '--short', 'HEAD'])
  process.stdout.write(`committed: ${commitHash} ${message}\n`)
}

try {
  main()
} catch (error) {
  releaseLock()
  process.stderr.write(`${error.message}\n`)
  process.exit(1)
}
