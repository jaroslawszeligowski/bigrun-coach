/**
 * Project: bigrun-coach
 * File: ErrorResponse
 * ------------------------------------------------------------------------
 * Description:
 * DTO for error responses. Returned by GlobalExceptionHandler instead of
 * the default Spring error page, providing a consistent JSON error format.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {}
