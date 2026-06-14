/**
 * Project: bigrun-coach
 * File: AppConfig
 * ------------------------------------------------------------------------
 * Description:
 * Spring configuration class. Defines the main ChatClient bean used by
 * ChatService to communicate with the active LLM provider (Ollama, Gemini,
 * or OpenAI — switched via environment variables, never in code).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class AppConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
