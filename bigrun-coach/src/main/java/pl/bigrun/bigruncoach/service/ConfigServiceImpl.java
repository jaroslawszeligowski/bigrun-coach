/**
 * Project: bigrun-coach
 * File: ConfigServiceImpl
 * ------------------------------------------------------------------------
 * Description:
 * Returns active LLM provider and model names based on application config.
 * Provider priority: Ollama → Gemini → OpenAI.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.bigrun.bigruncoach.dto.ConfigResponse;

@Service
public class ConfigServiceImpl implements ConfigService {

    private final boolean ollamaEnabled;
    private final boolean openaiEnabled;
    private final boolean geminiEnabled;
    private final String smartModel;
    private final String fastModel;
    private final String bielikModel;
    private final String geminiModel;
    private final String openaiModel;

    public ConfigServiceImpl(
            @Value("${spring.ai.model.chat:ollama}") String activeProvider,
            @Value("${OLLAMA_SMART_MODEL:qwen2.5:14b}") String smartModel,
            @Value("${OLLAMA_FAST_MODEL:llama3.2:3b}") String fastModel,
            @Value("${OLLAMA_BIELIK_MODEL:SpeakLeash/bielik-11b-v3.0-instruct:Q4_K_M}") String bielikModel,
            @Value("${GEMINI_CHAT_MODEL:gemini-2.0-flash}") String geminiModel,
            @Value("${OPENAI_CHAT_MODEL:gpt-4o-mini}") String openaiModel) {
        this.ollamaEnabled = "ollama".equals(activeProvider);
        this.openaiEnabled = "openai".equals(activeProvider);
        this.geminiEnabled = "google-genai".equals(activeProvider);
        this.smartModel = smartModel;
        this.fastModel = fastModel;
        this.bielikModel = bielikModel;
        this.geminiModel = geminiModel;
        this.openaiModel = openaiModel;
    }

    @Override
    public ConfigResponse getActiveConfig() {
        if (ollamaEnabled) {
            return new ConfigResponse("ollama", smartModel, fastModel, bielikModel);
        }
        if (geminiEnabled) {
            return new ConfigResponse("gemini", geminiModel, null, null);
        }
        return new ConfigResponse("openai", openaiModel, null, null);
    }
}