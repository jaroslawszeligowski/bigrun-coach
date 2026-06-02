#!/bin/bash

# ─────────────────────────────────────────────
# BigRun Coach — standalone start
# ─────────────────────────────────────────────
# Folder structure:
#   ./bigrun-coach-0.0.1-SNAPSHOT.jar
#   ./.env
# ─────────────────────────────────────────────

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$SCRIPT_DIR/bigrun-coach-0.0.1-SNAPSHOT.jar"
ENV_FILE="$SCRIPT_DIR/.env"

if [ ! -f "$JAR" ]; then
  echo "ERROR: jar not found: $JAR"
  exit 1
fi

if [ ! -f "$ENV_FILE" ]; then
  echo "ERROR: .env not found: $ENV_FILE"
  exit 1
fi

set -a
source "$ENV_FILE"
set +a

SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"

echo "Starting BigRun Coach..."
echo "  Profile : $SPRING_PROFILES_ACTIVE"
echo "  Ollama  : ${OLLAMA_BASE_URL:-http://localhost:11434}"
echo ""

java -jar "$JAR" --spring.profiles.active="$SPRING_PROFILES_ACTIVE"
