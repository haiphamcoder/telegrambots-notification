package io.github.haiphamcoder.telegrambots.notification.model;

/**
 * Strategy for handling captions that exceed the maximum length (1024 characters).
 * 
 * @since 1.0.0-SNAPSHOT
 */
public enum CaptionStrategy {
    
    /**
     * Truncate the caption to 1024 characters and send only the truncated version.
     */
    TRUNCATE,
    
    /**
     * Send the photo/document with the first 1024 characters as caption,
     * then send the remaining text as a separate message.
     * This is the default strategy.
     */
    SEND_REST_AS_MESSAGE,
    
    /**
     * Throw an exception if the caption exceeds 1024 characters.
     */
    ERROR
}
