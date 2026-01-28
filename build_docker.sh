#!/bin/bash
NAMESPACE="${1:-codebase_b1290_app}"
docker build -t "$NAMESPACE" .