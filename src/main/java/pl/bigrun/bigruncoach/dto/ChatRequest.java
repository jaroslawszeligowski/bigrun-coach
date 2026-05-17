/**
 * Project: bigrun-coach
 * File: ChatRequest
 * ------------------------------------------------------------------------
 * Description:
 * DTO for incoming chat requests. Validated on arrival — blank messages
 * are rejected with 400 Bad Request before reaching the service layer.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "Message must not be blank")
        String message
) {}
