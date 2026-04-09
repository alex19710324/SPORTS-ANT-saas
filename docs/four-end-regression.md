# 四端总回归

## 入口

在仓库根目录执行：

```bash
npm run regression:four-end
```

如果四个前端端口已经由你手动启动，也可以只执行断言脚本：

```bash
npm run regression:four-end:check
```

## 覆盖范围

- 5171 → 5176 → 5178 租赁链
- 5171 → 5174 → 5178 安全应急链
- 5171 → 5174 → 5178 巡检整改链
- 5171 → 5176 → 5174 → 5178 健康 / Web3 链

## 前置条件

- 仓库根目录可执行 `node` 与 `python3`
- 本地可启动 Playwright 所需浏览器

## 执行方式

- `npm run regression:four-end`
  - 自动拉起 8080 轻量回归后端
  - 自动拉起 5171 / 5174 / 5176 / 5178 四个回归夹具页面
  - 自动执行 `four-end-regression.cjs`
- `npm run regression:four-end:check`
  - 只执行断言脚本
  - 适用于你已经手动准备好 8080 / 5171 / 5174 / 5176 / 5178 的场景

## 回归夹具说明

- 正式入口不再依赖仓库里当前的四套前端构建产物是否完整
- 5171 / 5174 / 5176 / 5178 使用专用回归夹具页面，只承载这条四端联调真正关心的跨端入口、query 上下文与承接文案
- 8080 使用专用轻量回归后端，只实现本次回归实际需要的接口
- 这样可以把验证重点收敛到：
  - 接口联调是否成功
  - 跨端跳转语义是否一致
  - 关键文案和上下文承接是否完整

## 输出

脚本会打印本次联调生成的：

- `rentalId`
- `pickupCode`
- `incidentId`
- `reportId`

并输出 `api`、`rental`、`safety`、`inspection`、`healthWeb3` 五个分组的布尔结果；`failures` 为空表示四端联调通过。

## 实现文件

- 正式入口脚本：[run-four-end-regression.cjs](file:///Users/yaoyunzhong/Desktop/SPORTS%20ANT%20saas/scripts/run-four-end-regression.cjs)
- 轻量回归后端：[regression-backend.cjs](file:///Users/yaoyunzhong/Desktop/SPORTS%20ANT%20saas/scripts/regression-backend.cjs)
- 四端夹具页面服务：[regression-fixture-server.cjs](file:///Users/yaoyunzhong/Desktop/SPORTS%20ANT%20saas/scripts/regression-fixture-server.cjs)
- 实际断言脚本：[four-end-regression.cjs](file:///Users/yaoyunzhong/Desktop/SPORTS%20ANT%20saas/four-end-regression.cjs)
