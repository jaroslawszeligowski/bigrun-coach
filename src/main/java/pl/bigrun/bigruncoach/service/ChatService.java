/**
 * Project: bigrun-coach
 * File: ChatService
 * ------------------------------------------------------------------------
 * Description:
 * Contract for the AI chat service. Implementations handle the full
 * RAG + LLM pipeline for answering runner questions.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.service;

public interface ChatService {

    String chat(String message);
}
