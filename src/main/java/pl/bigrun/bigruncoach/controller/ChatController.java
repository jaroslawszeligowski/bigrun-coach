/**
 * Project: bigrun-coach
 * File: ChatController
 * ------------------------------------------------------------------------
 * Description:
 * REST controller exposing the AI chat endpoint. Delegates business logic
 * to ChatService — no logic in the controller.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bigrun.bigruncoach.dto.ChatRequest;
import pl.bigrun.bigruncoach.dto.ChatResponse;
import pl.bigrun.bigruncoach.service.ChatService;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("Chat request received: '{}'", request.message());
        String answer = chatService.chat(request.message());
        log.info("Chat response sent: '{}'", answer);
        return ResponseEntity.ok(new ChatResponse(answer));
    }
}