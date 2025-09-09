package io.github.haiphamcoder.telegrambots.notification.exception;

/**
 * Base exception for all Telegram API related errors.
 * This exception and its subclasses represent various types of errors that can occur
 * when communicating with the Telegram Bot API.
 */
public class TelegramApiException extends RuntimeException {
    private final int errorCode;
    private final String description;

    /**
     * Creates a new TelegramApiException with the specified error code and description.
     *
     * @param errorCode the error code from Telegram API or HTTP status code
     * @param description the error description
     */
    public TelegramApiException(int errorCode, String description) {
        super(description);
        this.errorCode = errorCode;
        this.description = description;
    }

    /**
     * Creates a new TelegramApiException with the specified error code, description, and cause.
     *
     * @param errorCode the error code from Telegram API or HTTP status code
     * @param description the error description
     * @param cause the cause of this exception
     */
    public TelegramApiException(int errorCode, String description, Throwable cause) {
        super(description, cause);
        this.errorCode = errorCode;
        this.description = description;
    }

    /**
     * Gets the error code associated with this exception.
     *
     * @return the error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the error description associated with this exception.
     *
     * @return the error description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "errorCode=" + errorCode +
                ", description='" + description + '\'' +
                '}';
    }
}
