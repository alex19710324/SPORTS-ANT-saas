#!/bin/bash

# Configuration
INTERVAL=600 # 10 minutes in seconds

echo ">>> Starting Auto-Git-Save Service (Interval: 10 mins)"

while true; do
    echo ">>> [$(date)] Checking for changes..."
    
    # Check if there are changes
    if [[ -n $(git status -s) ]]; then
        echo ">>> Changes detected. Committing..."
        
        git add .
        git commit -m "Auto-save: $(date '+%Y-%m-%d %H:%M:%S')"
        
        # Optional: Push to remote if configured
        # git push origin main
        
        echo ">>> Saved successfully."
    else
        echo ">>> No changes detected."
    fi
    
    # Wait for the next interval
    sleep $INTERVAL
done
