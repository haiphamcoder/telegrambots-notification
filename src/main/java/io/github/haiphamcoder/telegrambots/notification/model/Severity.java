package io.github.haiphamcoder.telegrambots.notification.model;

/**
 * Represents the severity level of a notification message.
 * Each severity level has a corresponding HTML template for formatting.
 */
public enum Severity {
    /**
     * Debug level - for debugging information.
     */
    DEBUG,
    
    /**
     * Info level - for general information.
     */
    INFO,
    
    /**
     * Warning level - for warning messages.
     */
    WARNING,
    
    /**
     * Error level - for error messages.
     */
    ERROR,
    
    /**
     * Critical level - for critical messages requiring immediate attention.
     */
    CRITICAL
}
