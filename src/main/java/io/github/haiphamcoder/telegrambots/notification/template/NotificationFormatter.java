package io.github.haiphamcoder.telegrambots.notification.template;

import io.github.haiphamcoder.telegrambots.notification.model.NotificationMessage;

/**
 * Interface for formatting notification messages into HTML for Telegram.
 * Implementations should handle HTML escaping and template rendering.
 */
public interface NotificationFormatter {
    
    /**
     * Formats a notification message into HTML using the default template for its severity.
     *
     * @param message the notification message to format
     * @return the formatted HTML string
     * @throws IllegalArgumentException if message is null
     */
    String format(NotificationMessage message);
    
    /**
     * Formats a notification message into HTML using a custom template.
     *
     * @param message the notification message to format
     * @param customTemplate the custom HTML template to use
     * @return the formatted HTML string
     * @throws IllegalArgumentException if message or customTemplate is null
     */
    String format(NotificationMessage message, String customTemplate);
}
