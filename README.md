# SPORTS ANT SaaS Super System

## 1. 项目简介 (Project Overview)
本项目是基于“SPORTS ANT SaaS”产品需求文档（PRD）构建的超乐场潮玩馆一体化管理系统。
系统采用前后端分离架构，旨在实现多区域自动适配、全角色AI深度赋能、线上线下全链路闭环。

## 2. 技术栈 (Tech Stack)

### 前端 (Frontend)
- **Framework**: Vue 3
- **Language**: TypeScript
- **UI Library**: Element Plus
- **Build Tool**: Vite
- **Location**: `/frontend`

### 后端 (Backend)
- **Framework**: Spring Boot 3.x
- **Language**: Java 24
- **Database**: MySQL 8.0 (目前配置为 H2 内存数据库，方便快速启动)
- **Build Tool**: Maven
- **Location**: `/backend`

### 文档 (Documentation)
- 所有 PRD 和设计文档均归档在 `/docs` 目录下。

---

## 3. 快速开始 (Quick Start)

### 环境要求
- Java 17+ (推荐 21+)
- Node.js 18+
- Maven 3.x

### 启动后端
```bash
cd backend
mvn spring-boot:run
```
服务默认运行在: `http://localhost:8080`

### 启动前端
```bash
cd frontend
npm install # 初次运行需要
npm run dev
```
服务默认运行在: `http://localhost:5173`

---

## 4. 团队协作与 Git 版本控制指南 (Collaboration Guide)

为了保证多人协作时互不干扰，且代码安全可回溯，请严格遵守以下流程。

### 开发宪法2：Git 自动化管理 (Development Constitution 2)
本项目强制执行 **每10分钟自动保存** 策略，确保代码零丢失。

#### 1. 启动自动保存服务
在开发前，请在终端运行以下命令：
```bash
./auto_save_git.sh &
```
该脚本会每隔 10 分钟自动执行 `git commit`，提交信息包含时间戳。

#### 2. 版本回退 (Rollback)
如果你改坏了代码，想回到之前的某个版本：
```bash
./restore_version.sh
```
按照提示选择 Commit Hash 即可一键回退。

### 分支策略 (Branching Strategy)
- **main**: 主分支，仅存放随时可发布的稳定代码。**严禁直接 Push**。
- **develop**: 开发主分支，所有新功能开发完成后合并至此进行测试。
- **feature/xxx**: 功能分支，每位开发者开发新功能时，必须从 develop 切出独立的 feature 分支（例如: `feature/user-login`, `feature/payment-module`）。

### 开发流程 (Workflow)
1. **获取最新代码**: 每天开始工作前，先拉取最新 develop 分支。
   ```bash
   git checkout develop
   git pull origin develop
   ```
2. **创建功能分支**:
   ```bash
   git checkout -b feature/你的功能名称
   ```
3. **开发与提交**:
   - 在你的分支上进行开发。
   - 频繁提交，填写清晰的 Commit Message (例如: `feat: add user login API`).
   ```bash
   git add .
   git commit -m "feat: 描述你的改动"
   ```
4. **合并代码 (Pull Request)**:
   - **不要**直接合并到 develop 或 main。
   - 将你的分支推送到远程仓库: `git push origin feature/你的功能名称`
   - 在代码托管平台 (GitHub/GitLab) 上发起 **Pull Request (PR)** 或 **Merge Request (MR)** 请求合并到 `develop`。
   - **Code Review**: 让其他同事（或你自己）审查代码，确保无误后再点击合并。

### 如何防止代码“改烂”与回退 (Rollback)
- **代码隔离**: 每个人在自己的 `feature` 分支开发，即使改烂了，也不会影响 `develop` 或 `main` 分支的正常运行。
- **回退版本**: 如果某个提交引入了严重 Bug，可以使用 Git 回退。
  - 回退某次提交（保留历史，推荐）: `git revert <commit-hash>`
  - 重置到旧版本（慎用）: `git reset --hard <commit-hash>`

---

## 5. 目录结构 (Directory Structure)
```
/
├── backend/            # 后端工程 (Spring Boot)
│   ├── src/main/java   # Java 源代码
│   └── src/main/resources # 配置文件
├── frontend/           # 前端工程 (Vue 3 + Vite)
│   ├── src/            # Vue 源代码
│   └── index.html      # 入口文件
├── docs/               # 产品需求文档与设计资料
└── README.md           # 项目说明书
```
