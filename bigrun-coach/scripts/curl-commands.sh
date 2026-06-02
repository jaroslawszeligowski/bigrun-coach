#!/bin/bash

BASE_URL="http://localhost:8081"

# ─────────────────────────────────────────────
# Config
# ─────────────────────────────────────────────

echo "GIVEN: app is running  WHEN: GET /api/v1/config  THEN: returns active provider and model names"
echo "curl -X GET $BASE_URL/api/v1/config"
curl -s -X GET "$BASE_URL/api/v1/config"
echo ""
echo ""

# ─────────────────────────────────────────────
# Chat — Tool Calling
# ─────────────────────────────────────────────

echo "GIVEN: runner targets 3h marathon  WHEN: asks for pace  THEN: returns pace in min/km"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"Pace for 3h marathon?\", \"model\": \"smart\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Pace for 3h marathon?", "model": "smart"}'
echo ""
echo ""

echo "GIVEN: pace is 5:00/km and distance is 42km  WHEN: asks for finish time  THEN: returns finish time"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"Finish time at 5:00/km for 42km?\", \"model\": \"smart\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Finish time at 5:00/km for 42km?", "model": "smart"}'
echo ""
echo ""

echo "GIVEN: pace is 4:30/km and duration is 90min  WHEN: asks for distance  THEN: returns distance in km"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"Distance in 90min at 4:30/km?\", \"model\": \"fast\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Distance in 90min at 4:30/km?", "model": "fast"}'
echo ""
echo ""

echo "GIVEN: runner targets 3:30 marathon  WHEN: asks for split strategy  THEN: returns pacing plan"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"Split strategy for 3:30 marathon?\", \"model\": \"smart\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Split strategy for 3:30 marathon?", "model": "smart"}'
echo ""
echo ""

echo "GIVEN: 2h run at 28C  WHEN: asks for water intake  THEN: returns hydration recommendation"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"Water for 2h run at 28°C?\", \"model\": \"fast\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Water for 2h run at 28°C?", "model": "fast"}'
echo ""
echo ""

echo "GIVEN: 3h run planned  WHEN: asks for carb intake  THEN: returns carbohydrate recommendation"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"Carbs for 3h run?\", \"model\": \"smart\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Carbs for 3h run?", "model": "smart"}'
echo ""
echo ""

# ─────────────────────────────────────────────
# Chat — RAG
# ─────────────────────────────────────────────

echo "GIVEN: knowledge base loaded  WHEN: asks about threshold heart rate  THEN: returns explanation from knowledge base"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"What is threshold heart rate?\", \"model\": \"smart\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "What is threshold heart rate?", "model": "smart"}'
echo ""
echo ""

echo "GIVEN: knowledge base loaded  WHEN: asks about breathing in cold weather  THEN: returns tips from knowledge base"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"How to breathe in cold weather?\", \"model\": \"smart\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "How to breathe in cold weather?", "model": "smart"}'
echo ""
echo ""

echo "GIVEN: knowledge base loaded  WHEN: asks about running injuries  THEN: returns injury list from knowledge base"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"Most common running injuries?\", \"model\": \"fast\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Most common running injuries?", "model": "fast"}'
echo ""
echo ""

echo "GIVEN: knowledge base loaded  WHEN: asks about marathon tapering  THEN: returns tapering strategy from knowledge base"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"Marathon tapering strategy?\", \"model\": \"smart\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Marathon tapering strategy?", "model": "smart"}'
echo ""
echo ""

# ─────────────────────────────────────────────
# Chat — Validation
# ─────────────────────────────────────────────

echo "GIVEN: empty message body  WHEN: POST /api/v1/chat  THEN: returns 400 Bad Request"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": ""}'
echo ""
echo ""

echo "GIVEN: question unrelated to running  WHEN: asks for pizza recipe  THEN: returns polite refusal"
echo "curl -X POST $BASE_URL/api/v1/chat -d '{\"message\": \"Give me a pizza recipe\", \"model\": \"fast\"}'"
curl -s -X POST "$BASE_URL/api/v1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Give me a pizza recipe", "model": "fast"}'
echo ""
echo ""
