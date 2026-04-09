# Git 自动提交

## 运行模式

- 常驻模式：`npm run git:auto-commit:start`
- 停止：`npm run git:auto-commit:stop`
- 状态：`npm run git:auto-commit:status`

常驻进程会立即执行一次自动提交检查，之后每 30 分钟执行一次。

## 开机自启

- 打印 launchd 配置：

```bash
npm run git:auto-commit:launchagent:print
```

- 校验 launchd 配置：

```bash
npm run git:auto-commit:launchagent:lint
```

- 安装到当前用户的 LaunchAgents：

```bash
npm run git:auto-commit:launchagent:install
```

- 查看 launchd 状态：

```bash
npm run git:auto-commit:launchagent:status
```

- 卸载 launchd：

```bash
npm run git:auto-commit:launchagent:uninstall
```

## 说明

- 自动提交脚本使用临时索引构建快照，再直接推送到远端 `main`
- 推送成功后会尽量同步本地 `refs/heads/main` 与 `refs/remotes/origin/main`
- 本地对齐命令：

```bash
npm run git:sync-local-main
```

## 文件

- 自动提交逻辑：[auto-git-commit.cjs](file:///Users/yaoyunzhong/Desktop/SPORTS%20ANT%20saas/scripts/auto-git-commit.cjs)
- 常驻守护进程：[auto-git-commit-daemon.cjs](file:///Users/yaoyunzhong/Desktop/SPORTS%20ANT%20saas/scripts/auto-git-commit-daemon.cjs)
- launchd 管理脚本：[manage-auto-git-launchagent.cjs](file:///Users/yaoyunzhong/Desktop/SPORTS%20ANT%20saas/scripts/manage-auto-git-launchagent.cjs)
- 本地 main 对齐脚本：[sync-local-main.cjs](file:///Users/yaoyunzhong/Desktop/SPORTS%20ANT%20saas/scripts/sync-local-main.cjs)
