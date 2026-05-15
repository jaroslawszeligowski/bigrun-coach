/**
 * Project: bigrun-coach
 * File: KnowledgeLoader
 * ------------------------------------------------------------------------
 * Description:
 * Loads markdown knowledge files from resources/knowledge/ into pgvector
 * on application startup. Uses MD5 checksum to skip files that have not
 * changed since the last load — avoids redundant embedding calls to Ollama.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeLoader implements ApplicationRunner {

    private static final String KNOWLEDGE_PATH = "classpath:knowledge/*.md";
    private static final int MAX_CHUNK_SIZE_CHARS = 1500;

    private static final String SQL_SELECT_CHECKSUM =
            "SELECT checksum FROM knowledge_metadata WHERE filename = ?";
    private static final String SQL_DELETE_CHUNKS =
            "DELETE FROM vector_store WHERE metadata->>'filename' = ?";
    private static final String SQL_DELETE_METADATA =
            "DELETE FROM knowledge_metadata WHERE filename = ?";
    private static final String SQL_INSERT_METADATA =
            "INSERT INTO knowledge_metadata (filename, checksum, loaded_at) VALUES (?, ?, ?)";

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting knowledge base loading...");

        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources(KNOWLEDGE_PATH);

        if (resources.length == 0) {
            log.warn("No knowledge files found in {}", KNOWLEDGE_PATH);
            return;
        }

        int loaded = 0;
        int skipped = 0;

        for (Resource resource : resources) {
            boolean wasLoaded = loadFile(resource);
            if (wasLoaded) loaded++;
            else skipped++;
        }

        log.info("Knowledge base ready. Loaded: {}, Skipped (unchanged): {}", loaded, skipped);
    }

    private boolean loadFile(Resource resource) {
        String filename = resource.getFilename();
        try {
            String content = resource.getContentAsString(StandardCharsets.UTF_8);
            String checksum = md5(content);

            if (isUpToDate(filename, checksum)) {
                log.info("Skipping '{}' — checksum unchanged", filename);
                return false;
            }

            log.info("Loading '{}' into vector store...", filename);
            deleteExistingChunks(filename);

            List<Document> documents = toDocuments(splitIntoChunks(content), filename);
            vectorStore.add(documents);
            saveChecksum(filename, checksum);

            log.info("Loaded '{}' — {} chunks", filename, documents.size());
            return true;

        } catch (Exception e) {
            log.error("Failed to load '{}': {}", filename, e.getMessage());
            return false;
        }
    }

    private boolean isUpToDate(String filename, String checksum) {
        List<String> result = jdbcTemplate.queryForList(
                SQL_SELECT_CHECKSUM, String.class, filename);
        return !result.isEmpty() && checksum.equals(result.get(0));
    }

    private void deleteExistingChunks(String filename) {
        jdbcTemplate.update(SQL_DELETE_CHUNKS, filename);
        jdbcTemplate.update(SQL_DELETE_METADATA, filename);
    }

    private void saveChecksum(String filename, String checksum) {
        jdbcTemplate.update(SQL_INSERT_METADATA, filename, checksum, LocalDateTime.now());
    }

    private List<String> splitIntoChunks(String content) {
        String[] paragraphs = content.split("\n\n");
        List<String> chunks = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (String paragraph : paragraphs) {
            if (buffer.length() + paragraph.length() > MAX_CHUNK_SIZE_CHARS && !buffer.isEmpty()) {
                chunks.add(buffer.toString().trim());
                buffer = new StringBuilder();
            }
            buffer.append(paragraph).append("\n\n");
        }

        if (!buffer.isEmpty()) {
            chunks.add(buffer.toString().trim());
        }

        return chunks;
    }

    private List<Document> toDocuments(List<String> chunks, String filename) {
        return chunks.stream()
                .map(chunk -> new Document(chunk, Map.of("filename", filename)))
                .toList();
    }

    private String md5(String input) {
        return DigestUtils.md5DigestAsHex(input.getBytes(StandardCharsets.UTF_8));
    }
}
