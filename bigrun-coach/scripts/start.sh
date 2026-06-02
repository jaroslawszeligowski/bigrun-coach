#!/bin/bash

# ─────────────────────────────────────────────
# BigRun Coach — start from jar
# ─────────────────────────────────────────────
# Requirements:
#   - PostgreSQL running on localhost:5432
#   - Ollama running on localhost:11434
#   - .env file in bigrun-coach/ directory
#   - jar built: mvn clean package -DskipTests
# ─────────────────────────────────────────────

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
JAR="$PROJECT_DIR/target/bigrun-coach-0.0.1-SNAPSHOT.jar"
ENV_FILE="$PROJECT_DIR/.env"

# Check jar exists
if [ ! -f "$JAR" ]; then
  echo "ERROR: jar not found at $JAR"
  echo "Run: mvn clean package -DskipTests"
  exit 1
fi

# Check .env exists
if [ ! -f "$ENV_FILE" ]; then
  echo "ERROR: .env not found at $ENV_FILE"
  exit 1
fi

# Load .env
set -a
source "$ENV_FILE"
set +a

# Set default profile if not set
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"

echo "Starting BigRun Coach..."
echo "  Profile : $SPRING_PROFILES_ACTIVE"
echo "  Jar     : $JAR"
echo "  Ollama  : ${OLLAMA_BASE_URL:-http://localhost:11434}"
echo ""

java -jar "$JAR" --spring.profiles.active="$SPRING_PROFILES_ACTIVE"
