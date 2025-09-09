package io.github.haiphamcoder.telegrambots.notification.exception;

/**
 * Exception thrown when network-related errors occur while communicating with Telegram API.
 * This includes connection timeouts, read timeouts, and other I/O related issues.
 */
public class TelegramNetworkException extends TelegramApiException {
    private final String operation;

    /**
     * Creates a new TelegramNetworkException with the specified description and cause.
     *
     * @param description the error description
     * @param cause the cause of this exception
     */
    public TelegramNetworkException(String description, Throwable cause) {
        super(0, description, cause);
        this.operation = null;
    }

    /**
     * Creates a new TelegramNetworkException with the specified description, operation, and cause.
     *
     * @param description the error description
     * @param operation the operation that failed
     * @param cause the cause of this exception
     */
    public TelegramNetworkException(String description, String operation, Throwable cause) {
        super(0, description, cause);
        this.operation = operation;
    }

    /**
     * Gets the operation that failed.
     *
     * @return the operation name, or null if not specified
     */
    public String getOperation() {
        return operation;
    }
}
