package io.github.haiphamcoder.telegrambots.notification.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.haiphamcoder.telegrambots.notification.exception.*;
import io.github.haiphamcoder.telegrambots.notification.model.BotConfig;
import io.github.haiphamcoder.telegrambots.notification.model.InputFile;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP client for communicating with Telegram Bot API using Apache HttpClient 5.
 * This client handles proxy configuration, timeouts, and error response parsing.
 */
public class TelegramHttpClient5 implements AutoCloseable {
    
    private static final Logger logger = LoggerFactory.getLogger(TelegramHttpClient5.class);
    private static final String TELEGRAM_API_BASE_URL = "https://api.telegram.org";
    
    private final CloseableHttpClient httpClient;
    private final Gson gson;

    /**
     * Creates a new TelegramHttpClient5 with the specified bot configuration.
     *
     * @param botConfig the bot configuration
     * @throws IllegalArgumentException if botConfig is null
     */
    public TelegramHttpClient5(BotConfig botConfig) {
        if (botConfig == null) {
            throw new IllegalArgumentException("Bot config cannot be null");
        }
        
        this.gson = new Gson();
        this.httpClient = createHttpClient(botConfig);
    }

    /**
     * Sends a POST request with form data to the specified URL.
     *
     * @param url the target URL
     * @param formData the form data to send
     * @return the response body as a string
     * @throws TelegramNetworkException if a network error occurs
     * @throws TelegramApiException if the API returns an error response
     */
    public String postForm(String url, Map<String, String> formData) {
        try {
            HttpPost httpPost = new HttpPost(url);
            
            // Build form data
            StringBuilder formBody = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                if (!first) {
                    formBody.append("&");
                }
                formBody.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            
            StringEntity entity = new StringEntity(formBody.toString(), ContentType.APPLICATION_FORM_URLENCODED);
            httpPost.setEntity(entity);
            
            logger.debug("Sending POST request to: {}", url);
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                String responseBody = getResponseBody(response);
                
                logger.debug("Received response: status={}, body={}", statusCode, responseBody);
                
                if (statusCode >= 200 && statusCode < 300) {
                    return responseBody;
                } else {
                    throw createExceptionFromResponse(statusCode, responseBody);
                }
            }
            
        } catch (IOException e) {
            logger.error("Network error while sending POST request to: {}", url, e);
            throw new TelegramNetworkException("Network error occurred", "POST", e);
        }
    }

    /**
     * Sends a POST request with multipart form data to the specified URL.
     *
     * @param url the target URL
     * @param fields the form fields to send
     * @param files the files to upload
     * @return the response body as a string
     * @throws TelegramNetworkException if a network error occurs
     * @throws TelegramApiException if the API returns an error response
     */
    public String postMultipart(String url, Map<String, String> fields, Map<String, InputFile> files) {
        try {
            HttpPost httpPost = new HttpPost(url);
            
            // For now, we'll use a simple approach with form data
            // This is a simplified implementation - in production, you'd want proper multipart support
            StringBuilder formBody = new StringBuilder();
            boolean first = true;
            
            // Add form fields
            if (fields != null) {
                for (Map.Entry<String, String> entry : fields.entrySet()) {
                    if (!first) {
                        formBody.append("&");
                    }
                    formBody.append(entry.getKey()).append("=").append(entry.getValue());
                    first = false;
                }
            }
            
            // Note: File uploads would need proper multipart implementation
            // For now, we'll throw an exception if files are provided
            if (files != null && !files.isEmpty()) {
                throw new UnsupportedOperationException("File uploads not yet implemented in this version");
            }
            
            StringEntity entity = new StringEntity(formBody.toString(), ContentType.APPLICATION_FORM_URLENCODED);
            httpPost.setEntity(entity);
            
            logger.debug("Sending multipart POST request to: {} with {} fields and {} files", 
                        url, fields != null ? fields.size() : 0, files != null ? files.size() : 0);
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                String responseBody = getResponseBody(response);
                
                logger.debug("Received response: status={}, body={}", statusCode, responseBody);
                
                if (statusCode >= 200 && statusCode < 300) {
                    return responseBody;
                } else {
                    throw createExceptionFromResponse(statusCode, responseBody);
                }
            }
            
        } catch (IOException e) {
            logger.error("Network error while sending multipart POST request to: {}", url, e);
            throw new TelegramNetworkException("Network error occurred", "POST", e);
        }
    }

    /**
     * Creates an HTTP client with the specified bot configuration.
     *
     * @param botConfig the bot configuration
     * @return the configured HTTP client
     */
    private CloseableHttpClient createHttpClient(BotConfig botConfig) {
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(10)
                .setMaxConnPerRoute(5)
                .build();
        
        var httpClientBuilder = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(createRequestConfig(botConfig));
        
        // Configure proxy if specified
        if (botConfig.hasProxy() && botConfig.getProxyType() == java.net.Proxy.Type.HTTP) {
            HttpHost proxy = new HttpHost(botConfig.getProxyHost(), botConfig.getProxyPort());
            httpClientBuilder.setProxy(proxy);
            
            logger.debug("Configured HTTP proxy: {}:{}", botConfig.getProxyHost(), botConfig.getProxyPort());
        }
        
        return httpClientBuilder.build();
    }

    /**
     * Creates request configuration with timeouts from bot config.
     *
     * @param botConfig the bot configuration
     * @return the request configuration
     */
    private RequestConfig createRequestConfig(BotConfig botConfig) {
        return RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(botConfig.getConnectTimeoutMs()))
                .setResponseTimeout(Timeout.ofMilliseconds(botConfig.getResponseTimeoutMs()))
                .build();
    }

    /**
     * Extracts the response body from an HTTP response.
     *
     * @param response the HTTP response
     * @return the response body as a string
     * @throws IOException if an I/O error occurs
     */
    private String getResponseBody(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return "";
        }
        
        try (var inputStream = entity.getContent()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Creates an appropriate exception based on the HTTP response.
     *
     * @param statusCode the HTTP status code
     * @param responseBody the response body
     * @return the appropriate exception
     */
    private TelegramApiException createExceptionFromResponse(int statusCode, String responseBody) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            boolean ok = jsonResponse.getAsJsonPrimitive("ok").getAsBoolean();
            
            if (!ok) {
                int errorCode = jsonResponse.getAsJsonPrimitive("error_code").getAsInt();
                String description = jsonResponse.getAsJsonPrimitive("description").getAsString();
                
                // Check for retry_after parameter
                Integer retryAfter = null;
                if (jsonResponse.has("parameters")) {
                    JsonObject parameters = jsonResponse.getAsJsonObject("parameters");
                    if (parameters.has("retry_after")) {
                        retryAfter = parameters.getAsJsonPrimitive("retry_after").getAsInt();
                    }
                }
                
                return createSpecificException(statusCode, errorCode, description, retryAfter);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse error response: {}", responseBody, e);
        }
        
        // Fallback to generic HTTP exception
        return new TelegramHttpException(statusCode, "HTTP error: " + statusCode);
    }

    /**
     * Creates a specific exception type based on the error details.
     *
     * @param statusCode the HTTP status code
     * @param errorCode the Telegram error code
     * @param description the error description
     * @param retryAfter the retry after time in seconds
     * @return the appropriate exception
     */
    private TelegramApiException createSpecificException(int statusCode, int errorCode, String description, Integer retryAfter) {
        if (statusCode == 401 || statusCode == 403) {
            return new TelegramAuthException(statusCode, description);
        } else if (statusCode == 429) {
            return new TelegramRateLimitException(errorCode, description, retryAfter);
        } else {
            return new TelegramHttpException(statusCode, description);
        }
    }

    @Override
    public void close() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
