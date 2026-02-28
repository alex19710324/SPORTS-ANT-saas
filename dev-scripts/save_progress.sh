#!/bin/bash
# save_progress.sh - 一键保存当前工作进度

# 检查是否有未提交的更改
if [ -z "$(git status --porcelain)" ]; then 
  echo "✅ 没有需要保存的更改。"
  exit 0
fi

# 获取当前时间
TIMESTAMP=$(date "+%Y-%m-%d %H:%M:%S")

# 添加所有文件
git add .

# 提交更改
git commit -m "Auto-save: $TIMESTAMP"

echo "🎉 进度已保存！(Commit: $TIMESTAMP)"
