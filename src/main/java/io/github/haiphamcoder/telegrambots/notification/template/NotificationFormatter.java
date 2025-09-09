package io.github.haiphamcoder.telegrambots.notification.template;

import io.github.haiphamcoder.telegrambots.notification.model.NotificationMessage;
import io.github.haiphamcoder.telegrambots.notification.model.ParseMode;

/**
 * Interface for formatting notification messages for Telegram.
 * Implementations should handle escaping and template rendering for different parse modes.
 */
public interface NotificationFormatter {
    
    /**
     * Formats a notification message using the default template for its severity.
     *
     * @param message the notification message to format
     * @return the formatted string
     * @throws IllegalArgumentException if message is null
     */
    String format(NotificationMessage message);
    
    /**
     * Formats a notification message using a custom template.
     *
     * @param message the notification message to format
     * @param customTemplate the custom template to use
     * @return the formatted string
     * @throws IllegalArgumentException if message or customTemplate is null
     */
    String format(NotificationMessage message, String customTemplate);
    
    /**
     * Gets the parse mode supported by this formatter.
     *
     * @return the parse mode
     */
    ParseMode mode();
}
