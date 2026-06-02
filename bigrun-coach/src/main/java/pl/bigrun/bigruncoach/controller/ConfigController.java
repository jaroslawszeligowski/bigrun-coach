/**
 * Project: bigrun-coach
 * File: ConfigController
 * ------------------------------------------------------------------------
 * Description:
 * Exposes active LLM provider and model configuration so the frontend
 * can display correct model names without hardcoding them.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bigrun.bigruncoach.dto.ConfigResponse;
import pl.bigrun.bigruncoach.service.ConfigService;

@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping
    public ResponseEntity<ConfigResponse> getConfig() {
        return ResponseEntity.ok(configService.getActiveConfig());
    }
}
