package io.github.haiphamcoder.telegrambots.notification.model;

import java.util.Objects;

/**
 * Represents a source for sending photos via Telegram Bot API.
 * 
 * @since 1.0.0-SNAPSHOT
 */
public sealed interface PhotoSource {
    
    /**
     * Photo source using existing file ID from Telegram.
     * 
     * @param fileId the Telegram file ID
     */
    record ByFileId(String fileId) implements PhotoSource {
        public ByFileId {
            Objects.requireNonNull(fileId, "File ID cannot be null");
            if (fileId.trim().isEmpty()) {
                throw new IllegalArgumentException("File ID cannot be empty");
            }
        }
    }
    
    /**
     * Photo source using URL.
     * 
     * @param url the photo URL
     */
    record ByUrl(String url) implements PhotoSource {
        public ByUrl {
            Objects.requireNonNull(url, "URL cannot be null");
            if (url.trim().isEmpty()) {
                throw new IllegalArgumentException("URL cannot be empty");
            }
        }
    }
    
    /**
     * Photo source using file upload.
     * 
     * @param inputFile the file to upload
     */
    record ByUpload(InputFile inputFile) implements PhotoSource {
        public ByUpload {
            Objects.requireNonNull(inputFile, "InputFile cannot be null");
        }
    }
    
    /**
     * Creates a photo source from file ID.
     * 
     * @param fileId the Telegram file ID
     * @return a new ByFileId instance
     */
    static PhotoSource byFileId(String fileId) {
        return new ByFileId(fileId);
    }
    
    /**
     * Creates a photo source from URL.
     * 
     * @param url the photo URL
     * @return a new ByUrl instance
     */
    static PhotoSource byUrl(String url) {
        return new ByUrl(url);
    }
    
    /**
     * Creates a photo source from file upload.
     * 
     * @param inputFile the file to upload
     * @return a new ByUpload instance
     */
    static PhotoSource byUpload(InputFile inputFile) {
        return new ByUpload(inputFile);
    }
}
