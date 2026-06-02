import { useState, useEffect } from 'react';
import ChatWindow from './components/ChatWindow';
import type { ConfigResponse } from './types';

export default function App() {
  const [config, setConfig] = useState<ConfigResponse | null>(null);

  useEffect(() => {
    fetch('/api/v1/config')
      .then(r => r.ok ? r.json() : null)
      .then((data: ConfigResponse | null) => { if (data) setConfig(data); })
      .catch(() => {});
  }, []);

  const providerLabel = config
    ? { ollama: 'Ollama', gemini: 'Gemini', openai: 'OpenAI' }[config.provider] ?? config.provider
    : 'LLM';

  const modelRows = config ? [
    ...(config.provider === 'ollama'
      ? [
          { mode: 'smart', model: config.smartModel, note: 'RAG + tools' },
          { mode: 'fast',  model: config.fastModel,  note: 'RAG + tools' },
          { mode: 'embed', model: 'nomic-embed-text', note: 'vectors' },
        ]
      : config.provider === 'gemini'
      ? [
          { mode: 'chat',  model: config.smartModel,      note: 'RAG + tools' },
          { mode: 'embed', model: 'text-embedding-004',   note: 'vectors' },
        ]
      : [
          { mode: 'chat',  model: config.smartModel,         note: 'RAG + tools' },
          { mode: 'embed', model: 'text-embedding-3-small',  note: 'vectors' },
        ]
    )
  ] : [
    { mode: 'smart', model: '...', note: '' },
  ];

  return (
    <div className="h-screen bg-[#f5f4f0] flex flex-col overflow-hidden">

      {/* Top bar */}
      <div data-testid="app-header" className="flex items-center justify-between px-5 py-2.5 bg-white border-b border-[#e5e3de] shrink-0">
        <div className="flex items-center gap-2">
          <span className="text-[#fc4c02] text-xs">●</span>
          <span data-testid="app-title" className="text-[#1a1a1a] font-medium text-sm">BigRun Coach</span>
          <span className="text-[#999] text-xs">v1.0</span>
        </div>
        <div className="flex items-center gap-1.5">
          {['Spring Boot 3', 'Spring AI', providerLabel, 'pgvector'].map(tag => (
            <span
              key={tag}
              data-testid={tag === providerLabel ? 'provider-tag' : undefined}
              className="text-[10px] text-[#888] bg-[#eeece8] px-2 py-0.5 rounded"
            >
              {tag}
            </span>
          ))}
        </div>
      </div>

      {/* Main layout */}
      <div className="flex flex-1 overflow-hidden">

        {/* Left sidebar — info */}
        <div className="w-64 shrink-0 border-r border-[#e5e3de] bg-white flex flex-col p-4 gap-4 overflow-y-auto">

          {/* About */}
          <div>
            <p className="text-[#fc4c02] text-[10px] uppercase tracking-widest mb-1.5">about</p>
            <p className="text-[#555] text-[11px] leading-relaxed">
              AI running assistant with RAG knowledge base and Tool Calling for real-time calculations.
            </p>
          </div>

          <div className="border-t border-[#f0ede8]" />

          {/* RAG */}
          <div>
            <p className="text-[#fc4c02] text-[10px] uppercase tracking-widest mb-1.5">
              RAG · Retrieval Augmented Generation
            </p>
            <p className="text-[#555] text-[11px] leading-relaxed mb-2">
              Your question is converted to a vector and compared against a knowledge base stored in pgvector.
              Top 3 matching chunks are injected into the prompt as context before calling the LLM.
            </p>
            <div className="bg-[#f5f4f0] rounded p-2 flex flex-col gap-1">
              {['question → embedding', 'embedding → pgvector', 'top-3 chunks → prompt', 'prompt → LLM'].map((step, i) => (
                <div key={i} className="flex items-center gap-1.5 text-[10px] text-[#888]">
                  <span className="text-[#fc4c02]">{i + 1}</span>
                  <span>{step}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="border-t border-[#f0ede8]" />

          {/* Tools */}
          <div>
            <p className="text-[#fc4c02] text-[10px] uppercase tracking-widest mb-1.5">
              Tool Calling
            </p>
            <p className="text-[#555] text-[11px] leading-relaxed mb-2">
              LLM decides when to call Java methods for precise calculations instead of guessing.
            </p>
            <div className="bg-[#f5f4f0] rounded p-2 flex flex-col gap-1">
              {[
                'calculatePace()',
                'calculateFinishTime()',
                'calculateDistance()',
                'calculateSplitStrategy()',
                'calculateHydration()',
                'calculateCarbohydrates()',
              ].map(fn => (
                <span key={fn} className="text-[10px] text-[#888]">
                  <span className="text-[#fc4c02]">m</span> {fn}
                </span>
              ))}
            </div>
          </div>

          <div className="border-t border-[#f0ede8]" />

          {/* Models */}
          <div>
            <p className="text-[#fc4c02] text-[10px] uppercase tracking-widest mb-1.5">
              models · {providerLabel}
            </p>
            <div className="bg-[#f5f4f0] rounded p-2 flex flex-col gap-1">
              {modelRows.map(({ mode, model, note }) => (
                <div key={mode} className="flex flex-col text-[10px] leading-tight">
                  <div className="flex items-center gap-1.5">
                    <span className="text-[#fc4c02] w-8 shrink-0">{mode}</span>
                    <span className="text-[#555]">{model}</span>
                  </div>
                  {note && <span className="text-[#bbb] pl-9">{note}</span>}
                </div>
              ))}
            </div>
          </div>

          <div className="border-t border-[#f0ede8]" />

          {/* Stack */}
          <div>
            <p className="text-[#fc4c02] text-[10px] uppercase tracking-widest mb-1.5">stack</p>
            <div className="flex flex-col gap-0.5">
              {[
                ['vector',   'pgvector (PostgreSQL)'],
                ['backend',  'Spring Boot + Spring AI'],
                ['frontend', 'React + Vite + TS'],
              ].map(([label, value]) => (
                <div key={label} className="flex gap-1.5 text-[10px]">
                  <span className="text-[#fc4c02] w-14 shrink-0">{label}</span>
                  <span className="text-[#888]">{value}</span>
                </div>
              ))}
            </div>
          </div>

        </div>

        {/* Right — chat */}
        <div className="flex-1 flex flex-col overflow-hidden p-4">
          <ChatWindow
            endpoint="/api/v1/chat"
            title="BigRun Coach"
            config={config}
          />
        </div>

      </div>
    </div>
  );
}
