#!/bin/bash

# Configuration
COMMIT_INTERVAL=600 # 10 minutes (Commit Check)
PUSH_INTERVAL=3600  # 60 minutes (Push to Remote)

LAST_PUSH_TIME=$(date +%s)

echo ">>> Starting Auto-Git-Save Service"
echo ">>> Commit Check Interval: ${COMMIT_INTERVAL}s"
echo ">>> Remote Push Interval: ${PUSH_INTERVAL}s"

while true; do
    CURRENT_TIME=$(date +%s)
    echo ">>> [$(date '+%Y-%m-%d %H:%M:%S')] Checking for changes..."
    
    # 1. Commit Local Changes
    if [[ -n $(git status -s) ]]; then
        echo ">>> Changes detected. Committing..."
        git add .
        git commit -m "Auto-save: $(date '+%Y-%m-%d %H:%M:%S')"
        echo ">>> Committed successfully."
    else
        echo ">>> No local changes to commit."
    fi
    
    # 2. Push to Remote (Every Hour)
    TIME_DIFF=$((CURRENT_TIME - LAST_PUSH_TIME))
    
    if [ $TIME_DIFF -ge $PUSH_INTERVAL ]; then
        echo ">>> [$(date '+%Y-%m-%d %H:%M:%S')] Time to push to remote..."
        
        # Get current branch name
        CURRENT_BRANCH=$(git branch --show-current)
        
        if [ -n "$CURRENT_BRANCH" ]; then
            echo ">>> Pushing branch '$CURRENT_BRANCH' to origin..."
            git push origin "$CURRENT_BRANCH"
            
            if [ $? -eq 0 ]; then
                echo ">>> Push successful."
                LAST_PUSH_TIME=$CURRENT_TIME
            else
                echo ">>> Push failed. Will retry next cycle."
            fi
        else
            echo ">>> Error: Could not determine current branch."
        fi
    else
        echo ">>> Next push in $((PUSH_INTERVAL - TIME_DIFF)) seconds."
    fi
    
    # Wait for next commit check
    sleep $COMMIT_INTERVAL
done
