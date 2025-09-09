package io.github.haiphamcoder.telegrambots.notification.service;

import io.github.haiphamcoder.telegrambots.notification.client.TelegramBotApiClient;
import io.github.haiphamcoder.telegrambots.notification.exception.TelegramRateLimitException;
import io.github.haiphamcoder.telegrambots.notification.model.BotConfig;
import io.github.haiphamcoder.telegrambots.notification.model.NotificationMessage;
import io.github.haiphamcoder.telegrambots.notification.provider.BotConfigProvider;
import io.github.haiphamcoder.telegrambots.notification.template.NotificationFormatter;
import io.github.haiphamcoder.telegrambots.notification.util.MessageSplitter;
import io.github.haiphamcoder.telegrambots.notification.util.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Main service for sending Telegram notifications.
 * This service handles message formatting, splitting, retry logic, and bot management.
 */
public class TelegramNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);
    private static final int DEFAULT_MESSAGE_SOFT_LIMIT = 3900;
    
    private final BotConfigProvider botConfigProvider;
    private final NotificationFormatter formatter;
    private final RetryPolicy retryPolicy;
    private final int messageSoftLimit;

    /**
     * Creates a new TelegramNotificationService with the specified dependencies.
     *
     * @param botConfigProvider the bot configuration provider
     * @param formatter the notification formatter
     * @param retryPolicy the retry policy for handling rate limits
     * @throws IllegalArgumentException if any required parameter is null
     */
    public TelegramNotificationService(BotConfigProvider botConfigProvider, 
                                     NotificationFormatter formatter, 
                                     RetryPolicy retryPolicy) {
        this(botConfigProvider, formatter, retryPolicy, DEFAULT_MESSAGE_SOFT_LIMIT);
    }

    /**
     * Creates a new TelegramNotificationService with the specified dependencies and message limit.
     *
     * @param botConfigProvider the bot configuration provider
     * @param formatter the notification formatter
     * @param retryPolicy the retry policy for handling rate limits
     * @param messageSoftLimit the soft limit for message length (should be less than 4096)
     * @throws IllegalArgumentException if any required parameter is null or messageSoftLimit is invalid
     */
    public TelegramNotificationService(BotConfigProvider botConfigProvider, 
                                     NotificationFormatter formatter, 
                                     RetryPolicy retryPolicy, 
                                     int messageSoftLimit) {
        if (botConfigProvider == null) {
            throw new IllegalArgumentException("Bot config provider cannot be null");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("Formatter cannot be null");
        }
        if (retryPolicy == null) {
            throw new IllegalArgumentException("Retry policy cannot be null");
        }
        if (messageSoftLimit <= 0 || messageSoftLimit >= 4096) {
            throw new IllegalArgumentException("Message soft limit must be between 1 and 4095");
        }
        
        this.botConfigProvider = botConfigProvider;
        this.formatter = formatter;
        this.retryPolicy = retryPolicy;
        this.messageSoftLimit = messageSoftLimit;
    }

    /**
     * Sends a notification message using the specified bot.
     *
     * @param botName the name of the bot to use
     * @param message the notification message to send
     * @throws IllegalArgumentException if botName or message is null
     * @throws io.github.haiphamcoder.telegrambots.notification.exception.TelegramApiException if sending fails
     */
    public void send(String botName, NotificationMessage message) {
        if (botName == null || botName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot name cannot be null or empty");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        
        BotConfig botConfig = botConfigProvider.get(botName);
        if (botConfig == null) {
            throw new IllegalArgumentException("Bot not found: " + botName);
        }
        
        String html = formatter.format(message);
        sendHtmlMessage(botConfig, html);
    }

    /**
     * Sends a notification message using the specified bot with a custom template.
     *
     * @param botName the name of the bot to use
     * @param message the notification message to send
     * @param customTemplate the custom HTML template to use
     * @throws IllegalArgumentException if any parameter is null
     * @throws io.github.haiphamcoder.telegrambots.notification.exception.TelegramApiException if sending fails
     */
    public void send(String botName, NotificationMessage message, String customTemplate) {
        if (botName == null || botName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot name cannot be null or empty");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (customTemplate == null || customTemplate.trim().isEmpty()) {
            throw new IllegalArgumentException("Custom template cannot be null or empty");
        }
        
        BotConfig botConfig = botConfigProvider.get(botName);
        if (botConfig == null) {
            throw new IllegalArgumentException("Bot not found: " + botName);
        }
        
        String html = formatter.format(message, customTemplate);
        sendHtmlMessage(botConfig, html);
    }

    /**
     * Sends an HTML message using the specified bot configuration.
     * This method handles message splitting and retry logic.
     *
     * @param botConfig the bot configuration
     * @param html the HTML content to send
     */
    private void sendHtmlMessage(BotConfig botConfig, String html) {
        List<String> parts = MessageSplitter.safeSplitHtml(html, messageSoftLimit);
        
        if (parts.size() > 1) {
            logger.info("Message split into {} parts for bot {}", parts.size(), botConfig.getName());
        }
        
        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i);
            if (parts.size() > 1) {
                part = String.format("Part %d/%d\n\n%s", i + 1, parts.size(), part);
            }
            
            sendWithRetry(botConfig, part);
        }
    }

    /**
     * Sends a message with retry logic for rate limiting.
     *
     * @param botConfig the bot configuration
     * @param html the HTML content to send
     */
    private void sendWithRetry(BotConfig botConfig, String html) {
        int attempt = 0;
        
        while (retryPolicy.shouldRetry(attempt)) {
            try {
                sendSingleMessage(botConfig, html);
                return; // Success, exit retry loop
                
            } catch (TelegramRateLimitException e) {
                attempt++;
                
                if (!retryPolicy.shouldRetry(attempt)) {
                    logger.error("Max retries exceeded for bot {} after {} attempts", botConfig.getName(), attempt);
                    throw e;
                }
                
                Duration delay = retryPolicy.for429(attempt, e.getRetryAfter());
                logger.warn("Rate limited for bot {}, retrying in {} seconds (attempt {}/{})", 
                           botConfig.getName(), delay.getSeconds(), attempt, retryPolicy.getMaxRetries());
                
                try {
                    Thread.sleep(delay.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for retry", ie);
                }
                
            } catch (Exception e) {
                // Non-retryable exception, rethrow immediately
                throw e;
            }
        }
    }

    /**
     * Sends a single message using the bot API client.
     *
     * @param botConfig the bot configuration
     * @param html the HTML content to send
     */
    private void sendSingleMessage(BotConfig botConfig, String html) {
        TelegramBotApiClient client = new TelegramBotApiClient(botConfig);
        try {
            client.sendMessageHtml(html);
        } catch (Exception e) {
            logger.error("Failed to send message with bot {}", botConfig.getName(), e);
            throw e;
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                logger.warn("Failed to close client for bot {}", botConfig.getName(), e);
            }
        }
    }

    /**
     * Gets the bot configuration provider used by this service.
     *
     * @return the bot configuration provider
     */
    public BotConfigProvider getBotConfigProvider() {
        return botConfigProvider;
    }

    /**
     * Gets the notification formatter used by this service.
     *
     * @return the notification formatter
     */
    public NotificationFormatter getFormatter() {
        return formatter;
    }

    /**
     * Gets the retry policy used by this service.
     *
     * @return the retry policy
     */
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    /**
     * Gets the message soft limit used by this service.
     *
     * @return the message soft limit
     */
    public int getMessageSoftLimit() {
        return messageSoftLimit;
    }
}
