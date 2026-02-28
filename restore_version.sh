#!/bin/bash

echo ">>> Git Version Restoration Tool"
echo ">>> Current History:"

# Show last 10 commits with easy-to-read format
git log --pretty=format:"%h - %an, %ar : %s" -n 10 --graph

echo ""
echo "----------------------------------------"
read -p "Enter the Commit Hash (e.g., a1b2c3d) to restore to: " COMMIT_HASH

if [ -z "$COMMIT_HASH" ]; then
    echo "Error: Commit Hash cannot be empty."
    exit 1
fi

echo ">>> WARNING: This will reset your current workspace to $COMMIT_HASH."
echo ">>> Any uncommitted changes will be LOST."
read -p "Are you sure? (y/n): " CONFIRM

if [ "$CONFIRM" == "y" ]; then
    git reset --hard $COMMIT_HASH
    echo ">>> Successfully restored to $COMMIT_HASH."
else
    echo ">>> Operation cancelled."
fi
