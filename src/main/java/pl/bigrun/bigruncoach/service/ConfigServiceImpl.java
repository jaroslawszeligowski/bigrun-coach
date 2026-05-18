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
    private final boolean geminiEnabled;
    private final String smartModel;
    private final String fastModel;
    private final String geminiModel;
    private final String openaiModel;

    public ConfigServiceImpl(
            @Value("${spring.ai.ollama.enabled:true}") boolean ollamaEnabled,
            @Value("${spring.ai.google.genai.enabled:false}") boolean geminiEnabled,
            @Value("${app.models.smart}") String smartModel,
            @Value("${app.models.fast}") String fastModel,
            @Value("${app.models.gemini}") String geminiModel,
            @Value("${app.models.openai}") String openaiModel) {
        this.ollamaEnabled = ollamaEnabled;
        this.geminiEnabled = geminiEnabled;
        this.smartModel = smartModel;
        this.fastModel = fastModel;
        this.geminiModel = geminiModel;
        this.openaiModel = openaiModel;
    }

    @Override
    public ConfigResponse getActiveConfig() {
        if (ollamaEnabled) {
            return new ConfigResponse("ollama", smartModel, fastModel);
        }
        if (geminiEnabled) {
            return new ConfigResponse("gemini", geminiModel, geminiModel);
        }
        return new ConfigResponse("openai", openaiModel, openaiModel);
    }
}
