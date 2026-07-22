/**
 * Project: bigrun-coach
 * File: ChatServiceImpl
 * ------------------------------------------------------------------------
 * Description:
 * Default implementation of ChatService. Orchestrates the AI chat flow:
 * retrieves relevant context from the vector store via RagService, fills
 * the prompt template, and calls the LLM with registered tools
 * (PaceCalculatorTool, NutritionTool).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.bigrun.bigruncoach.tool.NutritionTool;
import pl.bigrun.bigruncoach.tool.PaceCalculatorTool;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final RagService ragService;
    private final PaceCalculatorTool paceCalculatorTool;
    private final NutritionTool nutritionTool;

    @Value("classpath:prompt-template.st")
    private Resource templateResource;

    @Value("${spring.ai.model.chat:ollama}")
    private String activeProvider;

    @Value("${OLLAMA_SMART_MODEL:qwen2.5:14b}")
    private String smartModel;

    @Value("${OLLAMA_FAST_MODEL:llama3.2:3b}")
    private String fastModel;

    @Value("${OLLAMA_BIELIK_MODEL:SpeakLeash/bielik-11b-v3.0-instruct:Q4_K_M}")
    private String bielikModel;

    public String chat(String message, String model) {
        String context = ragService.findRelevantContext(message);
        log.debug("RAG context length: {} chars", context.length());

        Prompt prompt = new PromptTemplate(templateResource)
                .create(Map.of(
                        "context",  context,
                        "question", message
                ));

        log.debug("LLM prompt content: {}", prompt.getContents());

        var requestSpec = chatClient
                .prompt(prompt)
                .tools(paceCalculatorTool, nutritionTool);

        if ("ollama".equals(activeProvider)) {
            String selectedModel = switch (model == null ? "" : model.toLowerCase()) {
                case "fast"   -> fastModel;
                case "bielik" -> bielikModel;
                default       -> smartModel;
            };
            log.info("Using Ollama model: {}", selectedModel);
            requestSpec = requestSpec.options(OllamaChatOptions.builder().model(selectedModel));
        } else {
            log.info("Using non-Ollama provider, model from config");
        }

        String response = requestSpec.call().content();
        log.debug("LLM response length: {} chars", response.length());
        return response;
    }
}
