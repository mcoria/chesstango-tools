#!/usr/bin/env python3
"""Scan a directory for .json files and run migrate() on each.

Usage: epd_migrate_1_7.py <directory>
"""
from __future__ import annotations
import shutil
import sys
import os
import json
from typing import Any


def migration_logic(data: Any) -> Any:
    """Perform migration on the data loaded from JSON.

    Adjust this function to implement real migration logic.
    """

    # Rename 'success' to 'movesSuccess'
    if "success" in data:
        data["movesSuccess"] = data.pop("success")

    
    # Rename 'successRate' to 'movesSuccessPct'
    if "successRate" in data:
        data["movesSuccessPct"] = data.pop("successRate")


    # Iterate over 'searchDetail' list 
    if "searchDetail" in data:
        for item in data["searchDetail"]:
            # Rename 'success' to 'moveSuccess' in each item
            if "success" in item:
                item["moveSuccess"] = item.pop("success")
            
            if "depthAccuracyPercentage" in item:
                del item["depthAccuracyPercentage"]

    return data

def migrate(path: str) -> None:
    """Load JSON at path, perform migration (no-op by default), and overwrite file.

    Adjust this function to implement real migration logic.
    """    

    # create a backup copy before modifying
    try:
        backup = path + '.bak'
        shutil.copy2(path, backup)
    except Exception as e:
        print(f"Warning: failed to create backup for {path}: {e}")

    try:
        with open(path, 'r', encoding='utf-8') as f:
            data: Any = json.load(f)
    except Exception as e:
        print(f"Skipping {path}: failed to read JSON: {e}")
        return

    # perform migration; define migration_logic below
    data = migration_logic(data)

    try:
        with open(path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
    except Exception as e:
        print(f"Failed to write {path}: {e}")


def main(directory: str) -> None:
    if not os.path.isdir(directory):
        print(f"Not a directory: {directory}")
        sys.exit(1)

    for root, _, files in os.walk(directory):
        for name in files:
            if name.lower().endswith('.json'):
                full = os.path.join(root, name)
                migrate(full)


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print(__doc__)
        sys.exit(1)
    main(sys.argv[1])
