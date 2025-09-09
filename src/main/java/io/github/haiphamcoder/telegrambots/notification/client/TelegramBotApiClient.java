package io.github.haiphamcoder.telegrambots.notification.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.haiphamcoder.telegrambots.notification.model.*;
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
    private static final String SEND_PHOTO_ENDPOINT = "/bot{token}/sendPhoto";
    private static final String SEND_DOCUMENT_ENDPOINT = "/bot{token}/sendDocument";
    
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

    // ========== PHOTO METHODS ==========

    /**
     * Sends a photo by file ID to the configured chat.
     *
     * @param fileId the Telegram file ID
     * @param caption the photo caption (optional)
     * @param parseMode the parse mode for the caption
     * @param disablePreview whether to disable web page preview
     * @return the message ID of the sent photo
     * @throws IllegalArgumentException if fileId is null or empty
     */
    public MessageId sendPhotoByFileId(String fileId, String caption, ParseMode parseMode, boolean disablePreview) {
        if (fileId == null || fileId.trim().isEmpty()) {
            throw new IllegalArgumentException("File ID cannot be null or empty");
        }
        
        String url = buildSendPhotoUrl();
        Map<String, String> formData = buildSendPhotoFormData(fileId, caption, parseMode, disablePreview);
        
        logger.debug("Sending photo by file ID to chat {}: {}", botConfig.getChatId(), fileId);
        
        try {
            String response = httpClient.postForm(url, formData);
            return parseMessageId(response);
        } catch (Exception e) {
            logger.error("Failed to send photo to chat {}", botConfig.getChatId(), e);
            throw e;
        }
    }

    /**
     * Sends a photo by URL to the configured chat.
     *
     * @param url the photo URL
     * @param caption the photo caption (optional)
     * @param parseMode the parse mode for the caption
     * @param disablePreview whether to disable web page preview
     * @return the message ID of the sent photo
     * @throws IllegalArgumentException if url is null or empty
     */
    public MessageId sendPhotoByUrl(String url, String caption, ParseMode parseMode, boolean disablePreview) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        
        String apiUrl = buildSendPhotoUrl();
        Map<String, String> formData = buildSendPhotoFormData(url, caption, parseMode, disablePreview);
        
        logger.debug("Sending photo by URL to chat {}: {}", botConfig.getChatId(), url);
        
        try {
            String response = httpClient.postForm(apiUrl, formData);
            return parseMessageId(response);
        } catch (Exception e) {
            logger.error("Failed to send photo to chat {}", botConfig.getChatId(), e);
            throw e;
        }
    }

    /**
     * Sends a photo by uploading file to the configured chat.
     *
     * @param inputFile the file to upload
     * @param caption the photo caption (optional)
     * @param parseMode the parse mode for the caption
     * @param disablePreview whether to disable web page preview
     * @return the message ID of the sent photo
     * @throws IllegalArgumentException if inputFile is null
     */
    public MessageId sendPhotoUpload(InputFile inputFile, String caption, ParseMode parseMode, boolean disablePreview) {
        if (inputFile == null) {
            throw new IllegalArgumentException("InputFile cannot be null");
        }
        
        String url = buildSendPhotoUrl();
        Map<String, String> fields = buildSendPhotoFields(caption, parseMode, disablePreview);
        Map<String, InputFile> files = Map.of("photo", inputFile);
        
        logger.debug("Sending photo upload to chat {}: {}", botConfig.getChatId(), inputFile.getFilename());
        
        try {
            String response = httpClient.postMultipart(url, fields, files);
            return parseMessageId(response);
        } catch (Exception e) {
            logger.error("Failed to send photo to chat {}", botConfig.getChatId(), e);
            throw e;
        }
    }

    // ========== DOCUMENT METHODS ==========

    /**
     * Sends a document by file ID to the configured chat.
     *
     * @param fileId the Telegram file ID
     * @param caption the document caption (optional)
     * @param parseMode the parse mode for the caption
     * @return the message ID of the sent document
     * @throws IllegalArgumentException if fileId is null or empty
     */
    public MessageId sendDocumentByFileId(String fileId, String caption, ParseMode parseMode) {
        if (fileId == null || fileId.trim().isEmpty()) {
            throw new IllegalArgumentException("File ID cannot be null or empty");
        }
        
        String url = buildSendDocumentUrl();
        Map<String, String> formData = buildSendDocumentFormData(fileId, caption, parseMode);
        
        logger.debug("Sending document by file ID to chat {}: {}", botConfig.getChatId(), fileId);
        
        try {
            String response = httpClient.postForm(url, formData);
            return parseMessageId(response);
        } catch (Exception e) {
            logger.error("Failed to send document to chat {}", botConfig.getChatId(), e);
            throw e;
        }
    }

    /**
     * Sends a document by URL to the configured chat.
     *
     * @param url the document URL
     * @param caption the document caption (optional)
     * @param parseMode the parse mode for the caption
     * @return the message ID of the sent document
     * @throws IllegalArgumentException if url is null or empty
     */
    public MessageId sendDocumentByUrl(String url, String caption, ParseMode parseMode) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        
        String apiUrl = buildSendDocumentUrl();
        Map<String, String> formData = buildSendDocumentFormData(url, caption, parseMode);
        
        logger.debug("Sending document by URL to chat {}: {}", botConfig.getChatId(), url);
        
        try {
            String response = httpClient.postForm(apiUrl, formData);
            return parseMessageId(response);
        } catch (Exception e) {
            logger.error("Failed to send document to chat {}", botConfig.getChatId(), e);
            throw e;
        }
    }

    /**
     * Sends a document by uploading file to the configured chat.
     *
     * @param inputFile the file to upload
     * @param caption the document caption (optional)
     * @param parseMode the parse mode for the caption
     * @return the message ID of the sent document
     * @throws IllegalArgumentException if inputFile is null
     */
    public MessageId sendDocumentUpload(InputFile inputFile, String caption, ParseMode parseMode) {
        if (inputFile == null) {
            throw new IllegalArgumentException("InputFile cannot be null");
        }
        
        String url = buildSendDocumentUrl();
        Map<String, String> fields = buildSendDocumentFields(caption, parseMode);
        Map<String, InputFile> files = Map.of("document", inputFile);
        
        logger.debug("Sending document upload to chat {}: {}", botConfig.getChatId(), inputFile.getFilename());
        
        try {
            String response = httpClient.postMultipart(url, fields, files);
            return parseMessageId(response);
        } catch (Exception e) {
            logger.error("Failed to send document to chat {}", botConfig.getChatId(), e);
            throw e;
        }
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
     * Builds the send photo URL for the configured bot.
     *
     * @return the complete send photo URL
     */
    private String buildSendPhotoUrl() {
        return "https://api.telegram.org" + SEND_PHOTO_ENDPOINT.replace("{token}", botConfig.getToken());
    }

    /**
     * Builds the send document URL for the configured bot.
     *
     * @return the complete send document URL
     */
    private String buildSendDocumentUrl() {
        return "https://api.telegram.org" + SEND_DOCUMENT_ENDPOINT.replace("{token}", botConfig.getToken());
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
     * Builds the form data for the send photo request.
     *
     * @param photo the photo (file ID or URL)
     * @param caption the photo caption (optional)
     * @param parseMode the parse mode for the caption
     * @param disablePreview whether to disable web page preview
     * @return the form data map
     */
    private Map<String, String> buildSendPhotoFormData(String photo, String caption, ParseMode parseMode, boolean disablePreview) {
        Map<String, String> formData = new HashMap<>();
        formData.put("chat_id", botConfig.getChatId());
        formData.put("photo", photo);
        
        if (caption != null && !caption.trim().isEmpty()) {
            formData.put("caption", caption);
        }
        
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
     * Builds the form fields for the send photo multipart request.
     *
     * @param caption the photo caption (optional)
     * @param parseMode the parse mode for the caption
     * @param disablePreview whether to disable web page preview
     * @return the form fields map
     */
    private Map<String, String> buildSendPhotoFields(String caption, ParseMode parseMode, boolean disablePreview) {
        Map<String, String> fields = new HashMap<>();
        fields.put("chat_id", botConfig.getChatId());
        
        if (caption != null && !caption.trim().isEmpty()) {
            fields.put("caption", caption);
        }
        
        if (parseMode != null) {
            switch (parseMode) {
                case HTML:
                    fields.put("parse_mode", "HTML");
                    break;
                case MARKDOWN:
                    fields.put("parse_mode", "Markdown");
                    break;
                case MARKDOWN_V2:
                    fields.put("parse_mode", "MarkdownV2");
                    break;
            }
        }
        
        fields.put("disable_web_page_preview", String.valueOf(disablePreview));
        return fields;
    }

    /**
     * Builds the form data for the send document request.
     *
     * @param document the document (file ID or URL)
     * @param caption the document caption (optional)
     * @param parseMode the parse mode for the caption
     * @return the form data map
     */
    private Map<String, String> buildSendDocumentFormData(String document, String caption, ParseMode parseMode) {
        Map<String, String> formData = new HashMap<>();
        formData.put("chat_id", botConfig.getChatId());
        formData.put("document", document);
        
        if (caption != null && !caption.trim().isEmpty()) {
            formData.put("caption", caption);
        }
        
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
        
        return formData;
    }

    /**
     * Builds the form fields for the send document multipart request.
     *
     * @param caption the document caption (optional)
     * @param parseMode the parse mode for the caption
     * @return the form fields map
     */
    private Map<String, String> buildSendDocumentFields(String caption, ParseMode parseMode) {
        Map<String, String> fields = new HashMap<>();
        fields.put("chat_id", botConfig.getChatId());
        
        if (caption != null && !caption.trim().isEmpty()) {
            fields.put("caption", caption);
        }
        
        if (parseMode != null) {
            switch (parseMode) {
                case HTML:
                    fields.put("parse_mode", "HTML");
                    break;
                case MARKDOWN:
                    fields.put("parse_mode", "Markdown");
                    break;
                case MARKDOWN_V2:
                    fields.put("parse_mode", "MarkdownV2");
                    break;
            }
        }
        
        return fields;
    }

    /**
     * Parses the message ID from the Telegram API response.
     *
     * @param response the JSON response from Telegram API
     * @return the message ID
     * @throws RuntimeException if the response cannot be parsed
     */
    private MessageId parseMessageId(String response) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            boolean ok = jsonResponse.getAsJsonPrimitive("ok").getAsBoolean();
            
            if (ok && jsonResponse.has("result")) {
                JsonObject result = jsonResponse.getAsJsonObject("result");
                if (result.has("message_id")) {
                    long messageId = result.getAsJsonPrimitive("message_id").getAsLong();
                    return MessageId.of(messageId);
                }
            }
            
            throw new RuntimeException("Failed to parse message ID from response: " + response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response: " + response, e);
        }
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
