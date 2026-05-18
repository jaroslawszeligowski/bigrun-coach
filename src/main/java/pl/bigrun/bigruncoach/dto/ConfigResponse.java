/**
 * Project: bigrun-coach
 * File: ConfigResponse
 * ------------------------------------------------------------------------
 * Description:
 * DTO returning active LLM provider and model names to the frontend.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.dto;

public record ConfigResponse(
        String provider,
        String smartModel,
        String fastModel
) {}
