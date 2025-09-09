package io.github.haiphamcoder.telegrambots.notification.model;

import java.util.Objects;

/**
 * Represents a source for sending documents via Telegram Bot API.
 * 
 * @since 1.0.0-SNAPSHOT
 */
public sealed interface DocumentSource {
    
    /**
     * Document source using existing file ID from Telegram.
     * 
     * @param fileId the Telegram file ID
     */
    record ByFileId(String fileId) implements DocumentSource {
        public ByFileId {
            Objects.requireNonNull(fileId, "File ID cannot be null");
            if (fileId.trim().isEmpty()) {
                throw new IllegalArgumentException("File ID cannot be empty");
            }
        }
    }
    
    /**
     * Document source using URL.
     * 
     * @param url the document URL
     */
    record ByUrl(String url) implements DocumentSource {
        public ByUrl {
            Objects.requireNonNull(url, "URL cannot be null");
            if (url.trim().isEmpty()) {
                throw new IllegalArgumentException("URL cannot be empty");
            }
        }
    }
    
    /**
     * Document source using file upload.
     * 
     * @param inputFile the file to upload
     */
    record ByUpload(InputFile inputFile) implements DocumentSource {
        public ByUpload {
            Objects.requireNonNull(inputFile, "InputFile cannot be null");
        }
    }
    
    /**
     * Creates a document source from file ID.
     * 
     * @param fileId the Telegram file ID
     * @return a new ByFileId instance
     */
    static DocumentSource byFileId(String fileId) {
        return new ByFileId(fileId);
    }
    
    /**
     * Creates a document source from URL.
     * 
     * @param url the document URL
     * @return a new ByUrl instance
     */
    static DocumentSource byUrl(String url) {
        return new ByUrl(url);
    }
    
    /**
     * Creates a document source from file upload.
     * 
     * @param inputFile the file to upload
     * @return a new ByUpload instance
     */
    static DocumentSource byUpload(InputFile inputFile) {
        return new ByUpload(inputFile);
    }
}
