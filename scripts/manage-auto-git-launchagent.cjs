#!/usr/bin/env node

const fs = require('node:fs')
const os = require('node:os')
const path = require('node:path')
const { execFileSync } = require('node:child_process')

const repoRoot = path.resolve(__dirname, '..')
const label = 'com.sportsant.saas.git-autocommit'
const launchAgentsDir = path.join(os.homedir(), 'Library', 'LaunchAgents')
const logsDir = path.join(os.homedir(), 'Library', 'Logs')
const plistPath = path.join(launchAgentsDir, `${label}.plist`)
const command = process.argv[2] || 'print'

const plistContent = `<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>Label</key>
  <string>${label}</string>
  <key>ProgramArguments</key>
  <array>
    <string>${process.execPath}</string>
    <string>${path.join(repoRoot, 'scripts', 'auto-git-commit-daemon.cjs')}</string>
  </array>
  <key>WorkingDirectory</key>
  <string>${repoRoot}</string>
  <key>RunAtLoad</key>
  <true/>
  <key>KeepAlive</key>
  <true/>
  <key>StandardOutPath</key>
  <string>${path.join(logsDir, `${label}.log`)}</string>
  <key>StandardErrorPath</key>
  <string>${path.join(logsDir, `${label}.error.log`)}</string>
</dict>
</plist>
`

const print = () => {
  process.stdout.write(plistContent)
}

const lint = () => {
  const tempPath = path.join(repoRoot, 'scripts', `${label}.generated.plist`)
  fs.writeFileSync(tempPath, plistContent)
  try {
    const result = execFileSync('plutil', ['-lint', tempPath], {
      cwd: repoRoot,
      encoding: 'utf8',
      stdio: ['ignore', 'pipe', 'pipe']
    }).trim()
    process.stdout.write(`${result}\n`)
  } finally {
    fs.unlinkSync(tempPath)
  }
}

const install = () => {
  fs.mkdirSync(logsDir, { recursive: true })
  fs.mkdirSync(launchAgentsDir, { recursive: true })
  fs.writeFileSync(plistPath, plistContent)
  execFileSync('plutil', ['-lint', plistPath], { stdio: 'inherit' })
  try {
    execFileSync('launchctl', ['bootout', `gui/${process.getuid()}`, plistPath], { stdio: 'ignore' })
  } catch {}
  execFileSync('launchctl', ['bootstrap', `gui/${process.getuid()}`, plistPath], { stdio: 'inherit' })
  execFileSync('launchctl', ['enable', `gui/${process.getuid()}/${label}`], { stdio: 'inherit' })
  execFileSync('launchctl', ['kickstart', '-k', `gui/${process.getuid()}/${label}`], { stdio: 'inherit' })
  process.stdout.write(`${plistPath}\n`)
}

const uninstall = () => {
  try {
    execFileSync('launchctl', ['bootout', `gui/${process.getuid()}`, plistPath], { stdio: 'ignore' })
  } catch {}
  if (fs.existsSync(plistPath)) {
    fs.unlinkSync(plistPath)
  }
  process.stdout.write(`${label}\n`)
}

const status = () => {
  try {
    const output = execFileSync('launchctl', ['print', `gui/${process.getuid()}/${label}`], {
      cwd: repoRoot,
      encoding: 'utf8',
      stdio: ['ignore', 'pipe', 'pipe']
    }).trim()
    process.stdout.write(`${output}\n`)
  } catch {
    process.stdout.write('not-loaded\n')
  }
}

if (command === 'print') print()
if (command === 'lint') lint()
if (command === 'install') install()
if (command === 'uninstall') uninstall()
if (command === 'status') status()
