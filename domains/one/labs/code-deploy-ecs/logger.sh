#! /usr/bin/env bash

while true; do
    TIMESTAMP=$(date +%s%3N)
    echo "Running revision 1 at $TIMESTAMP"
    sleep 10
done
