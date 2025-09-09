package io.github.haiphamcoder.telegrambots.notification.exception;

/**
 * Exception thrown when rate limiting occurs while communicating with Telegram API.
 * This includes HTTP 429 (Too Many Requests) responses with optional retry_after information.
 */
public class TelegramRateLimitException extends TelegramApiException {
    private final Integer retryAfter;

    /**
     * Creates a new TelegramRateLimitException with the specified error code and description.
     *
     * @param errorCode the error code from Telegram API
     * @param description the error description
     */
    public TelegramRateLimitException(int errorCode, String description) {
        super(errorCode, description);
        this.retryAfter = null;
    }

    /**
     * Creates a new TelegramRateLimitException with the specified error code, description, and retry after time.
     *
     * @param errorCode the error code from Telegram API
     * @param description the error description
     * @param retryAfter the number of seconds to wait before retrying (null if not specified)
     */
    public TelegramRateLimitException(int errorCode, String description, Integer retryAfter) {
        super(errorCode, description);
        this.retryAfter = retryAfter;
    }

    /**
     * Creates a new TelegramRateLimitException with the specified error code, description, retry after time, and cause.
     *
     * @param errorCode the error code from Telegram API
     * @param description the error description
     * @param retryAfter the number of seconds to wait before retrying (null if not specified)
     * @param cause the cause of this exception
     */
    public TelegramRateLimitException(int errorCode, String description, Integer retryAfter, Throwable cause) {
        super(errorCode, description, cause);
        this.retryAfter = retryAfter;
    }

    /**
     * Gets the retry after time in seconds.
     *
     * @return the number of seconds to wait before retrying, or null if not specified
     */
    public Integer getRetryAfter() {
        return retryAfter;
    }

    /**
     * Checks if a retry after time is specified.
     *
     * @return true if retry after time is specified, false otherwise
     */
    public boolean hasRetryAfter() {
        return retryAfter != null && retryAfter > 0;
    }
}
