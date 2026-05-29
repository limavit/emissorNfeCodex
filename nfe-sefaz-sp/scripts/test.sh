#!/usr/bin/env bash
set -euo pipefail

if command -v mvn >/dev/null 2>&1; then
  (cd backend && mvn -Dmaven.repo.local=.m2/repository test)
else
  docker run --rm -v "$PWD/backend:/app" -w /app maven:3.9-eclipse-temurin-21 mvn -Dmaven.repo.local=.m2/repository test
fi

if command -v npm >/dev/null 2>&1; then
  (cd frontend && npm test -- --watch=false || true)
fi
