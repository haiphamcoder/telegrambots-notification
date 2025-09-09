package io.github.haiphamcoder.telegrambots.notification.client;

import io.github.haiphamcoder.telegrambots.notification.model.BotConfig;
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
        Map<String, String> formData = new HashMap<>();
        formData.put("chat_id", botConfig.getChatId());
        formData.put("text", html);
        formData.put("parse_mode", "HTML");
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
