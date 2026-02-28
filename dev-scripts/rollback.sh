#!/bin/bash
# rollback.sh - 回退到上一次保存点

echo "⚠️ 警告：此操作将撤销自上次保存以来的所有未提交更改！"
read -p "确定要继续吗？(y/n): " confirm

if [ "$confirm" == "y" ]; then
  git reset --hard HEAD
  echo "🔙 已回退到最后一次保存点。"
else
  echo "❌ 操作已取消。"
fi
