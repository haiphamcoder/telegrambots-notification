package io.github.haiphamcoder.telegrambots.notification.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a notification message that can be sent via Telegram.
 * This class uses the builder pattern for easy construction.
 */
public class NotificationMessage {
    private final Severity severity;
    private final String title;
    private final String body;
    private final Instant timestamp;
    private final Map<String, String> context;
    private final List<Action> actions;

    private NotificationMessage(Builder builder) {
        this.severity = builder.severity;
        this.title = builder.title.trim();
        this.body = builder.body.trim();
        this.timestamp = builder.timestamp != null ? builder.timestamp : Instant.now();
        this.context = builder.context != null ? Collections.unmodifiableMap(builder.context) : Collections.emptyMap();
        this.actions = builder.actions != null ? Collections.unmodifiableList(new ArrayList<>(builder.actions)) : Collections.emptyList();
    }

    /**
     * Gets the severity level of this notification.
     *
     * @return the severity level
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Gets the title of this notification.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the body content of this notification.
     *
     * @return the body content
     */
    public String getBody() {
        return body;
    }

    /**
     * Gets the timestamp when this notification was created.
     *
     * @return the timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the context information associated with this notification.
     *
     * @return an unmodifiable map of context key-value pairs
     */
    public Map<String, String> getContext() {
        return context;
    }

    /**
     * Gets the actions associated with this notification.
     *
     * @return an unmodifiable list of actions
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Creates a new builder for constructing NotificationMessage instances.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationMessage that = (NotificationMessage) o;
        return severity == that.severity &&
                Objects.equals(title, that.title) &&
                Objects.equals(body, that.body) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(context, that.context) &&
                Objects.equals(actions, that.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(severity, title, body, timestamp, context, actions);
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "severity=" + severity +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", timestamp=" + timestamp +
                ", context=" + context +
                ", actions=" + actions +
                '}';
    }

    /**
     * Builder class for constructing NotificationMessage instances.
     */
    public static class Builder {
        private Severity severity;
        private String title;
        private String body;
        private Instant timestamp;
        private Map<String, String> context;
        private List<Action> actions;

        /**
         * Sets the severity level of the notification.
         *
         * @param severity the severity level
         * @return this builder instance
         */
        public Builder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        /**
         * Sets the title of the notification.
         *
         * @param title the title
         * @return this builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the body content of the notification.
         *
         * @param body the body content
         * @return this builder instance
         */
        public Builder body(String body) {
            this.body = body;
            return this;
        }

        /**
         * Sets the timestamp of the notification.
         * If not set, the current time will be used when building.
         *
         * @param timestamp the timestamp
         * @return this builder instance
         */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Sets the context information for the notification.
         *
         * @param context a map of context key-value pairs
         * @return this builder instance
         */
        public Builder context(Map<String, String> context) {
            this.context = context;
            return this;
        }

        /**
         * Sets the actions for the notification.
         *
         * @param actions a list of actions
         * @return this builder instance
         */
        public Builder actions(List<Action> actions) {
            this.actions = actions;
            return this;
        }

        /**
         * Adds a single action to the notification.
         *
         * @param action the action to add
         * @return this builder instance
         */
        public Builder addAction(Action action) {
            if (this.actions == null) {
                this.actions = new ArrayList<>();
            }
            this.actions.add(action);
            return this;
        }

        /**
         * Builds the NotificationMessage instance.
         *
         * @return the built NotificationMessage
         * @throws IllegalArgumentException if required fields are not set
         */
        public NotificationMessage build() {
            if (severity == null) {
                throw new IllegalArgumentException("Severity is required");
            }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Body is required");
        }
            return new NotificationMessage(this);
        }
    }
}
