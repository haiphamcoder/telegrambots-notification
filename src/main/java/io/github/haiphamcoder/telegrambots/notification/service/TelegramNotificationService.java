package io.github.haiphamcoder.telegrambots.notification.service;

import io.github.haiphamcoder.telegrambots.notification.client.TelegramBotApiClient;
import io.github.haiphamcoder.telegrambots.notification.exception.TelegramRateLimitException;
import io.github.haiphamcoder.telegrambots.notification.model.*;
import io.github.haiphamcoder.telegrambots.notification.provider.BotConfigProvider;
import io.github.haiphamcoder.telegrambots.notification.template.NotificationFormatter;
import io.github.haiphamcoder.telegrambots.notification.util.CaptionHandler;
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
    private static final CaptionStrategy DEFAULT_CAPTION_STRATEGY = CaptionStrategy.SEND_REST_AS_MESSAGE;
    
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
     * @param customTemplate the custom template to use
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
        
        String content = formatter.format(message, customTemplate);
        sendMessage(botConfig, content, formatter.mode());
    }

    /**
     * Sends a notification message using the specified bot with the specified parse mode.
     *
     * @param botName the name of the bot to use
     * @param message the notification message to send
     * @param parseMode the parse mode to use
     * @throws IllegalArgumentException if any parameter is null
     * @throws io.github.haiphamcoder.telegrambots.notification.exception.TelegramApiException if sending fails
     */
    public void send(String botName, NotificationMessage message, ParseMode parseMode) {
        if (botName == null || botName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot name cannot be null or empty");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (parseMode == null) {
            throw new IllegalArgumentException("Parse mode cannot be null");
        }
        
        BotConfig botConfig = botConfigProvider.get(botName);
        if (botConfig == null) {
            throw new IllegalArgumentException("Bot not found: " + botName);
        }
        
        String content = formatter.format(message);
        sendMessage(botConfig, content, parseMode);
    }

    /**
     * Sends a notification message using the specified bot with a custom template and parse mode.
     *
     * @param botName the name of the bot to use
     * @param message the notification message to send
     * @param customTemplate the custom template to use
     * @param parseMode the parse mode to use
     * @throws IllegalArgumentException if any parameter is null
     * @throws io.github.haiphamcoder.telegrambots.notification.exception.TelegramApiException if sending fails
     */
    public void send(String botName, NotificationMessage message, String customTemplate, ParseMode parseMode) {
        if (botName == null || botName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot name cannot be null or empty");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (customTemplate == null || customTemplate.trim().isEmpty()) {
            throw new IllegalArgumentException("Custom template cannot be null or empty");
        }
        if (parseMode == null) {
            throw new IllegalArgumentException("Parse mode cannot be null");
        }
        
        BotConfig botConfig = botConfigProvider.get(botName);
        if (botConfig == null) {
            throw new IllegalArgumentException("Bot not found: " + botName);
        }
        
        String content = formatter.format(message, customTemplate);
        sendMessage(botConfig, content, parseMode);
    }

    /**
     * Sends an HTML message using the specified bot configuration.
     * This method handles message splitting and retry logic.
     *
     * @param botConfig the bot configuration
     * @param html the HTML content to send
     * @deprecated Use {@link #sendMessage(BotConfig, String, ParseMode)} instead
     */
    @Deprecated
    private void sendHtmlMessage(BotConfig botConfig, String html) {
        sendMessage(botConfig, html, ParseMode.HTML);
    }

    /**
     * Sends a message using the specified bot configuration.
     * This method handles message splitting and retry logic.
     *
     * @param botConfig the bot configuration
     * @param content the content to send
     * @param parseMode the parse mode to use
     */
    private void sendMessage(BotConfig botConfig, String content, ParseMode parseMode) {
        List<String> parts = MessageSplitter.safeSplit(content, messageSoftLimit, parseMode);
        
        if (parts.size() > 1) {
            logger.info("Message split into {} parts for bot {} using {}", parts.size(), botConfig.getName(), parseMode);
        }
        
        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i);
            if (parts.size() > 1) {
                part = String.format("Part %d/%d\n\n%s", i + 1, parts.size(), part);
            }
            
            sendWithRetry(botConfig, part, parseMode);
        }
    }

    /**
     * Sends a message with retry logic for rate limiting.
     *
     * @param botConfig the bot configuration
     * @param content the content to send
     * @param parseMode the parse mode to use
     */
    private void sendWithRetry(BotConfig botConfig, String content, ParseMode parseMode) {
        int attempt = 0;
        
        while (retryPolicy.shouldRetry(attempt)) {
            try {
                sendSingleMessage(botConfig, content, parseMode);
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
     * Sends a message with retry logic for rate limiting (HTML mode).
     *
     * @param botConfig the bot configuration
     * @param html the HTML content to send
     * @deprecated Use {@link #sendWithRetry(BotConfig, String, ParseMode)} instead
     */
    @Deprecated
    private void sendWithRetry(BotConfig botConfig, String html) {
        sendWithRetry(botConfig, html, ParseMode.HTML);
    }

    /**
     * Sends a single message using the bot API client.
     *
     * @param botConfig the bot configuration
     * @param content the content to send
     * @param parseMode the parse mode to use
     */
    private void sendSingleMessage(BotConfig botConfig, String content, ParseMode parseMode) {
        TelegramBotApiClient client = new TelegramBotApiClient(botConfig);
        try {
            client.sendMessage(content, parseMode);
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
     * Sends a single HTML message using the bot API client.
     *
     * @param botConfig the bot configuration
     * @param html the HTML content to send
     * @deprecated Use {@link #sendSingleMessage(BotConfig, String, ParseMode)} instead
     */
    @Deprecated
    private void sendSingleMessage(BotConfig botConfig, String html) {
        sendSingleMessage(botConfig, html, ParseMode.HTML);
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

    // ========== PHOTO METHODS ==========

    /**
     * Sends a photo with notification message to the specified bot.
     *
     * @param botName the name of the bot to use
     * @param message the notification message
     * @param photoSource the photo source
     * @param caption the photo caption (optional)
     * @param parseMode the parse mode for the caption
     * @throws IllegalArgumentException if any required parameter is null or empty
     */
    public void sendPhoto(String botName, NotificationMessage message, PhotoSource photoSource, 
                         String caption, ParseMode parseMode) throws Exception {
        if (botName == null || botName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot name cannot be null or empty");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (photoSource == null) {
            throw new IllegalArgumentException("Photo source cannot be null");
        }
        if (parseMode == null) {
            throw new IllegalArgumentException("Parse mode cannot be null");
        }

        BotConfig botConfig = botConfigProvider.get(botName);
        if (botConfig == null) {
            throw new IllegalArgumentException("Bot not found: " + botName);
        }

        // Format and process the caption if provided
        String processedCaption = null;
        String remainingText = null;
        if (caption != null && !caption.trim().isEmpty()) {
            String formattedCaption = formatter.format(message, caption);
            CaptionHandler.CaptionResult result = CaptionHandler.processCaption(formattedCaption, DEFAULT_CAPTION_STRATEGY, parseMode);
            processedCaption = result.caption();
            remainingText = result.remainingText();
        }

        sendPhotoWithRetry(botConfig, photoSource, processedCaption, remainingText, parseMode);
    }

    /**
     * Sends a photo with notification message to the specified bot using default parse mode.
     *
     * @param botName the name of the bot to use
     * @param message the notification message
     * @param photoSource the photo source
     * @param caption the photo caption (optional)
     * @throws IllegalArgumentException if any required parameter is null or empty
     */
    public void sendPhoto(String botName, NotificationMessage message, PhotoSource photoSource, String caption) throws Exception {
        sendPhoto(botName, message, photoSource, caption, formatter.mode());
    }

    // ========== DOCUMENT METHODS ==========

    /**
     * Sends a document with notification message to the specified bot.
     *
     * @param botName the name of the bot to use
     * @param message the notification message
     * @param documentSource the document source
     * @param caption the document caption (optional)
     * @param parseMode the parse mode for the caption
     * @throws IllegalArgumentException if any required parameter is null or empty
     */
    public void sendDocument(String botName, NotificationMessage message, DocumentSource documentSource, 
                            String caption, ParseMode parseMode) throws Exception {
        if (botName == null || botName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot name cannot be null or empty");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (documentSource == null) {
            throw new IllegalArgumentException("Document source cannot be null");
        }
        if (parseMode == null) {
            throw new IllegalArgumentException("Parse mode cannot be null");
        }

        BotConfig botConfig = botConfigProvider.get(botName);
        if (botConfig == null) {
            throw new IllegalArgumentException("Bot not found: " + botName);
        }

        // Format and process the caption if provided
        String processedCaption = null;
        String remainingText = null;
        if (caption != null && !caption.trim().isEmpty()) {
            String formattedCaption = formatter.format(message, caption);
            CaptionHandler.CaptionResult result = CaptionHandler.processCaption(formattedCaption, DEFAULT_CAPTION_STRATEGY, parseMode);
            processedCaption = result.caption();
            remainingText = result.remainingText();
        }

        sendDocumentWithRetry(botConfig, documentSource, processedCaption, remainingText, parseMode);
    }

    /**
     * Sends a document with notification message to the specified bot using default parse mode.
     *
     * @param botName the name of the bot to use
     * @param message the notification message
     * @param documentSource the document source
     * @param caption the document caption (optional)
     * @throws IllegalArgumentException if any required parameter is null or empty
     */
    public void sendDocument(String botName, NotificationMessage message, DocumentSource documentSource, String caption) throws Exception {
        sendDocument(botName, message, documentSource, caption, formatter.mode());
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Sends a photo with retry logic.
     *
     * @param botConfig the bot configuration
     * @param photoSource the photo source
     * @param caption the processed caption
     * @param remainingText the remaining text to send as separate message
     * @param parseMode the parse mode
     */
    private void sendPhotoWithRetry(BotConfig botConfig, PhotoSource photoSource, String caption, String remainingText, ParseMode parseMode) throws Exception {
        try (TelegramBotApiClient client = new TelegramBotApiClient(botConfig)) {
            MessageId messageId;
            if (photoSource instanceof PhotoSource.ByFileId byFileId) {
                messageId = client.sendPhotoByFileId(byFileId.fileId(), caption, parseMode, false);
            } else if (photoSource instanceof PhotoSource.ByUrl byUrl) {
                messageId = client.sendPhotoByUrl(byUrl.url(), caption, parseMode, false);
            } else if (photoSource instanceof PhotoSource.ByUpload byUpload) {
                messageId = client.sendPhotoUpload(byUpload.inputFile(), caption, parseMode, false);
            } else {
                throw new IllegalArgumentException("Unknown photo source type: " + photoSource.getClass());
            }
            
            logger.debug("Photo sent successfully with message ID: {}", messageId.messageId());
            
            // Send remaining text as separate message if needed
            if (remainingText != null && !remainingText.trim().isEmpty()) {
                sendRemainingTextAsMessage(client, remainingText, parseMode);
            }
        } catch (TelegramRateLimitException e) {
            logger.warn("Rate limit exceeded for bot {}, retrying after {} seconds", 
                       botConfig.getName(), e.getRetryAfter());
            
            if (retryPolicy.shouldRetry(0)) {
                Duration delay = retryPolicy.for429(0, e.getRetryAfter());
                try {
                    Thread.sleep(delay.toMillis());
                    sendPhotoWithRetry(botConfig, photoSource, caption, remainingText, parseMode);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                } catch (Exception ex) {
                    logger.error("Retry failed for photo", ex);
                    throw ex;
                }
            } else {
                throw e;
            }
        } catch (Exception e) {
            logger.error("Failed to send photo for bot {}", botConfig.getName(), e);
            throw e;
        }
    }

    /**
     * Sends a document with retry logic.
     *
     * @param botConfig the bot configuration
     * @param documentSource the document source
     * @param caption the processed caption
     * @param remainingText the remaining text to send as separate message
     * @param parseMode the parse mode
     */
    private void sendDocumentWithRetry(BotConfig botConfig, DocumentSource documentSource, String caption, String remainingText, ParseMode parseMode) throws Exception {
        try (TelegramBotApiClient client = new TelegramBotApiClient(botConfig)) {
            MessageId messageId;
            if (documentSource instanceof DocumentSource.ByFileId byFileId) {
                messageId = client.sendDocumentByFileId(byFileId.fileId(), caption, parseMode);
            } else if (documentSource instanceof DocumentSource.ByUrl byUrl) {
                messageId = client.sendDocumentByUrl(byUrl.url(), caption, parseMode);
            } else if (documentSource instanceof DocumentSource.ByUpload byUpload) {
                messageId = client.sendDocumentUpload(byUpload.inputFile(), caption, parseMode);
            } else {
                throw new IllegalArgumentException("Unknown document source type: " + documentSource.getClass());
            }
            
            logger.debug("Document sent successfully with message ID: {}", messageId.messageId());
            
            // Send remaining text as separate message if needed
            if (remainingText != null && !remainingText.trim().isEmpty()) {
                sendRemainingTextAsMessage(client, remainingText, parseMode);
            }
        } catch (TelegramRateLimitException e) {
            logger.warn("Rate limit exceeded for bot {}, retrying after {} seconds", 
                       botConfig.getName(), e.getRetryAfter());
            
            if (retryPolicy.shouldRetry(0)) {
                Duration delay = retryPolicy.for429(0, e.getRetryAfter());
                try {
                    Thread.sleep(delay.toMillis());
                    sendDocumentWithRetry(botConfig, documentSource, caption, remainingText, parseMode);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                } catch (Exception ex) {
                    logger.error("Retry failed for document", ex);
                    throw ex;
                }
            } else {
                throw e;
            }
        } catch (Exception e) {
            logger.error("Failed to send document for bot {}", botConfig.getName(), e);
            throw e;
        }
    }

    /**
     * Sends remaining text as a separate message with proper splitting.
     *
     * @param client the Telegram API client
     * @param remainingText the remaining text to send
     * @param parseMode the parse mode
     */
    private void sendRemainingTextAsMessage(TelegramBotApiClient client, String remainingText, ParseMode parseMode) {
        try {
            // Split the remaining text if it's too long
            List<String> parts = MessageSplitter.safeSplit(remainingText, messageSoftLimit, parseMode);
            
            for (String part : parts) {
                client.sendMessage(part, parseMode);
                logger.debug("Sent remaining text part: {} characters", part.length());
            }
        } catch (Exception e) {
            logger.error("Failed to send remaining text as message", e);
            // Don't rethrow - this is supplementary content
        }
    }
}
