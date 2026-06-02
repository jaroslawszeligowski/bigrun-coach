import { useState, useRef, useEffect } from 'react';
import type { ChatResponse, ConfigResponse } from '../types';

interface Props {
  endpoint: string;
  title: string;
  config: ConfigResponse | null;
}

export default function ChatWindow({ endpoint, config }: Props) {
  const [message, setMessage]   = useState('');
  const [response, setResponse] = useState('');
  const [error, setError]       = useState('');
  const [loading, setLoading]   = useState(false);
  const [model, setModel]       = useState<'smart' | 'fast'>('smart');
  const responseRef              = useRef<HTMLDivElement>(null);
  const textareaRef              = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (response && responseRef.current) {
      responseRef.current.scrollTop = responseRef.current.scrollHeight;
    }
  }, [response]);

  const sendMessage = async () => {
    const trimmed = message.trim();
    if (!trimmed) return;
    setLoading(true);
    setError('');
    setResponse('');
    try {
      const res = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: trimmed, model }),
      });

      if (!res.ok) {
        const err = await res.json();
        setError(`Error ${err.status}: ${err.message}`);
        return;
      }

      const data: ChatResponse = await res.json();
      setResponse(data.answer);
    } catch {
      setError('Cannot connect to server. Is the backend running on :8080?');
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  const handleInput = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setMessage(e.target.value);
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.style.height = textareaRef.current.scrollHeight + 'px';
    }
  };

  return (
    <div data-testid="chat-window" className="h-full flex flex-col gap-3">

      {/* Example queries */}
      <div className="flex gap-3 shrink-0">
        {[
          {
            label: 'tool calling',
            queries: [
              'Pace for 3h marathon?',
              'Finish time at 5:00/km for 42km?',
              'Distance in 90min at 4:30/km?',
              'Split strategy for 3:30 marathon?',
              'Water for 2h at 28°C?',
              'Carbs for 3h run?',
            ],
          },
          {
            label: 'rag',
            queries: [
              'What is threshold heart rate?',
              'How to breathe in cold weather?',
              'Most common running injuries?',
              'Marathon tapering strategy?',
            ],
          },
        ].map(({ label, queries }) => (
          <div key={label} data-testid={`example-queries-${label}`} className="flex flex-col gap-1 flex-1">
            <span className="text-[#fc4c02] text-[9px] uppercase tracking-widest">{label}</span>
            <div className="flex flex-col gap-1">
              {queries.map((q, i) => (
                <button
                  key={q}
                  data-testid={`example-query-${label}-${i}`}
                  onClick={() => setMessage(q)}
                  className="text-[#888] text-[10px] border border-[#e0ddd8] rounded px-2 py-1 hover:border-[#fc4c02] hover:text-[#fc4c02] transition-colors duration-150 font-[inherit] bg-white text-left"
                >
                  {q}
                </button>
              ))}
            </div>
          </div>
        ))}
      </div>

      {/* Input */}
      <div className="
        bg-white border border-[#e0ddd8] rounded-lg
        focus-within:border-[#fc4c02] transition-colors duration-200
        flex flex-col shrink-0
      ">
        <div className="flex items-start gap-2 px-3 pt-2.5 pb-1">
          <span className="text-[#fc4c02] text-xs mt-0.5 shrink-0">›</span>
          <textarea
            ref={textareaRef}
            data-testid="message-input"
            value={message}
            onChange={handleInput}
            onKeyDown={handleKeyDown}
            placeholder="Ask your running coach..."
            rows={2}
            className="
              w-full bg-transparent text-[#1a1a1a] text-xs
              resize-none placeholder:text-[#bbb]
              font-[inherit] leading-relaxed max-h-32 overflow-y-auto
            "
          />
        </div>
        <div className="flex items-center justify-between px-3 pb-2 pt-1 border-t border-[#f0ede8]">
          <div className="flex items-center gap-2">
            <span className="text-[#ccc] text-[10px]">⏎ send · ⇧⏎ new line</span>
            <div className="flex items-center gap-0.5 border border-[#e0ddd8] rounded overflow-hidden">
              {([
                { key: 'smart' as const, label: 'smart', modelName: config?.smartModel ?? '...' },
                ...(config?.provider === 'ollama' ? [{ key: 'fast' as const, label: 'fast', modelName: config.fastModel }] : []),
              ]).map(({ key, label, modelName }) => (
                <button
                  key={key}
                  data-testid={`model-button-${key}`}
                  aria-pressed={model === key}
                  onClick={() => setModel(key)}
                  title={modelName}
                  className={`text-[9px] px-2 py-0.5 font-[inherit] transition-colors duration-150 ${
                    model === key
                      ? 'bg-[#fc4c02] text-white'
                      : 'text-[#888] hover:text-[#fc4c02] bg-white'
                  }`}
                >
                  {label} · {modelName}
                </button>
              ))}
            </div>
          </div>
          <button
            data-testid="send-button"
            onClick={sendMessage}
            disabled={loading || !message.trim()}
            className="
              bg-[#fc4c02] hover:bg-[#e03d00] text-white
              text-[10px] font-medium px-3 py-1 rounded
              disabled:opacity-30 disabled:cursor-not-allowed
              transition-colors duration-150 font-[inherit]
            "
          >
            {loading ? 'running...' : 'Ask'}
          </button>
        </div>
      </div>

      {/* Runner */}
      {loading && (
        <div
          data-testid="loading-spinner"
          className="flex items-center gap-2 text-[#aaa] text-[10px] shrink-0"
        >
          <span className="runner">
            <span className="runner-dash" />
            <span className="runner-dash" />
            <span className="runner-dash" />
            <span className="runner-emoji">🏃</span>
          </span>
          <span>analyzing your question...</span>
        </div>
      )}

      {/* Error */}
      {error && (
        <div
          data-testid="error-message"
          className="border border-[#f0c0b0] bg-[#fdf0ec] rounded px-3 py-2 text-[#c05020] text-[11px] shrink-0"
        >
          ✕ {error}
        </div>
      )}

      {/* Response */}
      {response && (
        <div className="border border-[#e0ddd8] rounded-lg overflow-hidden flex flex-col flex-1 min-h-0">
          <div className="bg-[#f5f4f0] px-3 py-1.5 border-b border-[#e5e3de] flex items-center gap-2 shrink-0">
            <span className="text-[#fc4c02] text-[10px]">●</span>
            <span className="text-[#999] text-[10px]">response</span>
          </div>
          <div
            data-testid="response-output"
            ref={responseRef}
            className="p-3 text-[#333] text-[11px] whitespace-pre-wrap overflow-y-auto leading-relaxed bg-white flex-1"
          >
            {response}
          </div>
        </div>
      )}

    </div>
  );
}
