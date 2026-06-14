export interface ChatRequest {
  message: string;
}

export interface ChatResponse {
  answer: string;
}

export interface ErrorResponse {
  status: number;
  message: string;
  timestamp: string;
}

export interface ConfigResponse {
  provider: string;
  smartModel: string;
  fastModel: string;
  bielikModel: string | null;
}

