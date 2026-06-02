/**
 * Project: bigrun-coach
 * File: ChatControllerTest
 * ------------------------------------------------------------------------
 * Description:
 * Unit tests for ChatController using @WebMvcTest — loads only the HTTP
 * layer.
 * ChatService is mocked — no Spring context, no database, no Ollama.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 25/05/2026
 */
package pl.bigrun.bigruncoach.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.bigrun.bigruncoach.service.ChatService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ChatService chatService;

    @Test
    void shouldReturn200WithAnswerForValidMessage() throws Exception {
        when(chatService.chat(any(), any())).thenReturn("Your pace is 4:16 min/km.");

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"message": "What pace for a 3h marathon?"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Your pace is 4:16 min/km."));
    }

    @Test
    void shouldReturn400ForBlankMessage() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"message": ""}
                                """))
                .andExpect(status().isBadRequest());
    }
}
