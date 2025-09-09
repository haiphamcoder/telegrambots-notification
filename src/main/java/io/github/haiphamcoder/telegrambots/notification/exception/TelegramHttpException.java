package io.github.haiphamcoder.telegrambots.notification.exception;

/**
 * Exception thrown when a generic HTTP error occurs while communicating with Telegram API.
 * This includes all HTTP non-2xx responses that are not specifically handled by other exception types.
 */
public class TelegramHttpException extends TelegramApiException {
    private final int httpStatusCode;

    /**
     * Creates a new TelegramHttpException with the specified HTTP status code and description.
     *
     * @param httpStatusCode the HTTP status code
     * @param description the error description
     */
    public TelegramHttpException(int httpStatusCode, String description) {
        super(httpStatusCode, description);
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Creates a new TelegramHttpException with the specified HTTP status code, description, and cause.
     *
     * @param httpStatusCode the HTTP status code
     * @param description the error description
     * @param cause the cause of this exception
     */
    public TelegramHttpException(int httpStatusCode, String description, Throwable cause) {
        super(httpStatusCode, description, cause);
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Gets the HTTP status code that caused this exception.
     *
     * @return the HTTP status code
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
