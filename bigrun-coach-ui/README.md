# bigrun-coach-ui — Frontend

React 19 + TypeScript + Vite + Tailwind CSS frontend for BigRun Coach.
See the [root README](../README.md) for architecture overview and Quick Start.

## Development

```bash
npm install
npm run dev       # dev server at http://localhost:5173 (hot reload)
npm run build     # type check (tsc) + production build → dist/
npm run lint      # ESLint
```

API calls are proxied to backend at `http://localhost:8081` via Vite proxy config.

## Docker

Multi-stage build: Node 22 (build) → nginx Alpine (runtime, port 80).

```bash
docker build -t bigrun-coach-ui .
```

![BigRun Coach UI](../docs/images/bigrun-coach%20ui%20chat.png)

## Components

| Component | Purpose |
|-----------|---------|
| `App.tsx` | layout — left sidebar (models, RAG info, stack), fetches `/api/v1/config` on load |
| `ChatWindow.tsx` | main chat UI — model selector (smart/fast/bielik), textarea, response display |
| `MountainRunner.tsx` | branding visual |
| `types.ts` | `ChatRequest`, `ChatResponse`, `ConfigResponse` |

## Model selector behaviour

- `smart` / `fast` — visible only when provider = Ollama
- `bielik` — visible only when `config.bielikModel != null` (Ollama only, hidden for Gemini/OpenAI)
- Model names displayed on buttons come from `/api/v1/config` — no hardcoding in frontend
