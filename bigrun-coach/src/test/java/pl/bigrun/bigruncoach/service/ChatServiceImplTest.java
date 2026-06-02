/**
 * Project: bigrun-coach
 * File: ChatServiceImplTest
 * ------------------------------------------------------------------------
 * Description:
 * Unit tests for ChatServiceImpl. Verifies model selection logic:
 * null/any → smartModel, "fast"/"FAST" → fastModel.
 * Uses pure Mockito — no Spring context, no DB, no Ollama.
 * @Value fields injected via ReflectionTestUtils (Spring not available in Mockito context).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 27/05/2026
 */
package pl.bigrun.bigruncoach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;
import pl.bigrun.bigruncoach.tool.NutritionTool;
import pl.bigrun.bigruncoach.tool.PaceCalculatorTool;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.ai.chat.prompt.Prompt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock ChatClient chatClient;
    @Mock ChatClient.ChatClientRequestSpec requestSpec;
    @Mock ChatClient.CallResponseSpec callSpec;
    @Mock RagService ragService;
    @Mock PaceCalculatorTool paceCalculatorTool;
    @Mock NutritionTool nutritionTool;

    ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatServiceImpl(chatClient, ragService, paceCalculatorTool, nutritionTool);

        // @Value nie jest wstrzykiwane przez Mockito — brak Spring contextu.
        // ReflectionTestUtils ustawia prywatne pola przez refleksję Javy.
        ReflectionTestUtils.setField(chatService, "smartModel", "qwen2.5:14b");
        ReflectionTestUtils.setField(chatService, "fastModel", "qwen2.5:7b");
        ReflectionTestUtils.setField(chatService, "templateResource", new ClassPathResource("prompt-template.st"));

        when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpec);
        when(requestSpec.options(any())).thenReturn(requestSpec);
        when(requestSpec.tools(any(Object[].class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn("mocked response");
        when(ragService.findRelevantContext(any())).thenReturn("");
    }

    @Test
    void shouldUseSmartModelWhenModelIsNull() {
        chatService.chat("jakie tempo na maraton?", null);

        ArgumentCaptor<OllamaChatOptions> captor = ArgumentCaptor.forClass(OllamaChatOptions.class);
        verify(requestSpec).options(captor.capture());
        assertThat(captor.getValue().getModel()).isEqualTo("qwen2.5:14b");
    }

    @Test
    void shouldUseFastModelWhenModelIsFast() {
        chatService.chat("jakie tempo na maraton?", "fast");

        ArgumentCaptor<OllamaChatOptions> captor = ArgumentCaptor.forClass(OllamaChatOptions.class);
        verify(requestSpec).options(captor.capture());
        assertThat(captor.getValue().getModel()).isEqualTo("qwen2.5:7b");
    }

    @Test
    void shouldUseFastModelWhenModelIsUppercase() {
        chatService.chat("jakie tempo na maraton?", "FAST");

        ArgumentCaptor<OllamaChatOptions> captor = ArgumentCaptor.forClass(OllamaChatOptions.class);
        verify(requestSpec).options(captor.capture());
        assertThat(captor.getValue().getModel()).isEqualTo("qwen2.5:7b");
    }

    @Test
    void shouldUseSmartModelWhenModelIsSmart() {
        chatService.chat("jakie tempo na maraton?", "smart");

        ArgumentCaptor<OllamaChatOptions> captor = ArgumentCaptor.forClass(OllamaChatOptions.class);
        verify(requestSpec).options(captor.capture());
        assertThat(captor.getValue().getModel()).isEqualTo("qwen2.5:14b");
    }

    @Test
    void shouldUseSmartModelForUnknownModelValue() {
        chatService.chat("jakie tempo na maraton?", "unknown");

        ArgumentCaptor<OllamaChatOptions> captor = ArgumentCaptor.forClass(OllamaChatOptions.class);
        verify(requestSpec).options(captor.capture());
        assertThat(captor.getValue().getModel()).isEqualTo("qwen2.5:14b");
    }

    @Test
    void shouldCallRagServiceWithUserMessage() {
        chatService.chat("ile wody pić?", null);

        verify(ragService).findRelevantContext("ile wody pić?");
    }

    @Test
    void shouldReturnLlmResponse() {
        String result = chatService.chat("jakie tempo?", null);

        assertThat(result).isEqualTo("mocked response");
    }
}
