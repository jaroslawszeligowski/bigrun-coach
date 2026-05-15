/**
 * Project: bigrun-coach
 * File: RagService
 * ------------------------------------------------------------------------
 * Description:
 * Retrieves relevant context from the vector store based on semantic
 * similarity to the user's query. Used by ChatService to enrich prompts
 * with knowledge base content before sending to the LLM.
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
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private static final String DOCUMENT_SEPARATOR = "\n\n---\n\n";

    @Value("${app.rag.top-k:3}")
    private int topK;

    @Value("${app.rag.similarity-threshold:0.5}")
    private double similarityThreshold;

    private final VectorStore vectorStore;

    public String findRelevantContext(String query) {
        log.debug("Searching vector store for query: '{}'", query);

        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThreshold(similarityThreshold)
                        .build()
        );

        if (documents.isEmpty()) {
            log.warn("No relevant context found for query: '{}'", query);
            return "";
        }

        log.debug("Found {} relevant chunks for query: '{}'", documents.size(), query);

        return documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining(DOCUMENT_SEPARATOR));
    }
}
