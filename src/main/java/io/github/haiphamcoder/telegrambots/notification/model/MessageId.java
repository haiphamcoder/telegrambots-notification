package io.github.haiphamcoder.telegrambots.notification.model;

/**
 * Represents a Telegram message ID returned from Bot API.
 * 
 * @since 1.0.0-SNAPSHOT
 */
public record MessageId(long messageId) {
    
    /**
     * Creates a new MessageId with the specified message ID.
     * 
     * @param messageId the Telegram message ID
     * @return a new MessageId instance
     */
    public static MessageId of(long messageId) {
        return new MessageId(messageId);
    }
    
    @Override
    public String toString() {
        return "MessageId{messageId=" + messageId + "}";
    }
}
