package io.github.haiphamcoder.telegrambots.notification.client;

import io.github.haiphamcoder.telegrambots.notification.model.BotConfig;
import io.github.haiphamcoder.telegrambots.notification.model.ParseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * High-level client for Telegram Bot API operations.
 * This client provides convenient methods for sending messages and other operations.
 */
public class TelegramBotApiClient implements AutoCloseable {
    
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotApiClient.class);
    private static final String SEND_MESSAGE_ENDPOINT = "/bot{token}/sendMessage";
    
    private final BotConfig botConfig;
    private final TelegramHttpClient5 httpClient;

    /**
     * Creates a new TelegramBotApiClient with the specified bot configuration.
     *
     * @param botConfig the bot configuration
     * @throws IllegalArgumentException if botConfig is null
     */
    public TelegramBotApiClient(BotConfig botConfig) {
        if (botConfig == null) {
            throw new IllegalArgumentException("Bot config cannot be null");
        }
        this.botConfig = botConfig;
        this.httpClient = new TelegramHttpClient5(botConfig);
    }

    /**
     * Sends an HTML message to the configured chat.
     *
     * @param html the HTML content to send
     * @param disablePreview whether to disable web page preview
     * @throws IllegalArgumentException if html is null or empty
     */
    public void sendMessageHtml(String html, boolean disablePreview) {
        if (html == null || html.trim().isEmpty()) {
            throw new IllegalArgumentException("HTML content cannot be null or empty");
        }
        
        String url = buildSendMessageUrl();
        Map<String, String> formData = buildSendMessageFormData(html, disablePreview);
        
        logger.debug("Sending HTML message to chat {}: {}", botConfig.getChatId(), html);
        
        try {
            String response = httpClient.postForm(url, formData);
            logger.debug("Message sent successfully: {}", response);
        } catch (Exception e) {
            logger.error("Failed to send message to chat {}", botConfig.getChatId(), e);
            throw e;
        }
    }

    /**
     * Sends an HTML message to the configured chat with web page preview enabled.
     *
     * @param html the HTML content to send
     * @throws IllegalArgumentException if html is null or empty
     */
    public void sendMessageHtml(String html) {
        sendMessageHtml(html, false);
    }

    /**
     * Sends a message with the specified parse mode to the configured chat.
     *
     * @param text the message content to send
     * @param parseMode the parse mode to use
     * @param disablePreview whether to disable web page preview
     * @throws IllegalArgumentException if text is null or empty
     */
    public void sendMessage(String text, ParseMode parseMode, boolean disablePreview) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }
        
        String url = buildSendMessageUrl();
        Map<String, String> formData = buildSendMessageFormData(text, parseMode, disablePreview);
        
        logger.debug("Sending {} message to chat {}: {}", parseMode, botConfig.getChatId(), text);
        
        try {
            String response = httpClient.postForm(url, formData);
            logger.debug("Message sent successfully: {}", response);
        } catch (Exception e) {
            logger.error("Failed to send message to chat {}", botConfig.getChatId(), e);
            throw e;
        }
    }

    /**
     * Sends a message with the specified parse mode to the configured chat with web page preview enabled.
     *
     * @param text the message content to send
     * @param parseMode the parse mode to use
     * @throws IllegalArgumentException if text is null or empty
     */
    public void sendMessage(String text, ParseMode parseMode) {
        sendMessage(text, parseMode, false);
    }

    /**
     * Gets the bot configuration used by this client.
     *
     * @return the bot configuration
     */
    public BotConfig getBotConfig() {
        return botConfig;
    }

    /**
     * Builds the send message URL for the configured bot.
     *
     * @return the complete send message URL
     */
    private String buildSendMessageUrl() {
        return "https://api.telegram.org" + SEND_MESSAGE_ENDPOINT.replace("{token}", botConfig.getToken());
    }

    /**
     * Builds the form data for the send message request.
     *
     * @param html the HTML content
     * @param disablePreview whether to disable web page preview
     * @return the form data map
     */
    private Map<String, String> buildSendMessageFormData(String html, boolean disablePreview) {
        return buildSendMessageFormData(html, ParseMode.HTML, disablePreview);
    }

    /**
     * Builds the form data for the send message request.
     *
     * @param text the message content
     * @param parseMode the parse mode to use
     * @param disablePreview whether to disable web page preview
     * @return the form data map
     */
    private Map<String, String> buildSendMessageFormData(String text, ParseMode parseMode, boolean disablePreview) {
        Map<String, String> formData = new HashMap<>();
        formData.put("chat_id", botConfig.getChatId());
        formData.put("text", text);
        
        if (parseMode != null) {
            switch (parseMode) {
                case HTML:
                    formData.put("parse_mode", "HTML");
                    break;
                case MARKDOWN:
                    formData.put("parse_mode", "Markdown");
                    break;
                case MARKDOWN_V2:
                    formData.put("parse_mode", "MarkdownV2");
                    break;
            }
        }
        
        formData.put("disable_web_page_preview", String.valueOf(disablePreview));
        return formData;
    }

    /**
     * Closes the underlying HTTP client and releases resources.
     *
     * @throws Exception if an error occurs while closing
     */
    public void close() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
