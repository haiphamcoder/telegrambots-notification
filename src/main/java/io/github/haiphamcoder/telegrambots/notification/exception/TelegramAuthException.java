package io.github.haiphamcoder.telegrambots.notification.exception;

/**
 * Exception thrown when authentication fails while communicating with Telegram API.
 * This includes HTTP 401 (Unauthorized) and 403 (Forbidden) responses.
 */
public class TelegramAuthException extends TelegramApiException {
    private final int httpStatusCode;

    /**
     * Creates a new TelegramAuthException with the specified HTTP status code and description.
     *
     * @param httpStatusCode the HTTP status code (401 or 403)
     * @param description the error description
     */
    public TelegramAuthException(int httpStatusCode, String description) {
        super(httpStatusCode, description);
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Creates a new TelegramAuthException with the specified HTTP status code, description, and cause.
     *
     * @param httpStatusCode the HTTP status code (401 or 403)
     * @param description the error description
     * @param cause the cause of this exception
     */
    public TelegramAuthException(int httpStatusCode, String description, Throwable cause) {
        super(httpStatusCode, description, cause);
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Gets the HTTP status code that caused this exception.
     *
     * @return the HTTP status code (401 or 403)
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
