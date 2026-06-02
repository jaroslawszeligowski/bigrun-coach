/**
 * Project: bigrun-coach
 * File: ChatResponse
 * ------------------------------------------------------------------------
 * Description:
 * DTO for outgoing chat responses. Wraps the LLM-generated answer.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.dto;

public record ChatResponse(String answer) {}
