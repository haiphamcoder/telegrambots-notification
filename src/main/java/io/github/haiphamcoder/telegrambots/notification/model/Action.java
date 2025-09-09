package io.github.haiphamcoder.telegrambots.notification.model;

import java.util.Objects;

/**
 * Represents an action that can be associated with a notification message.
 * An action typically contains a label and a URL for user interaction.
 */
public class Action {
    private final String label;
    private final String url;

    /**
     * Creates a new Action with the specified label and URL.
     *
     * @param label the display label for the action
     * @param url the URL to navigate to when the action is triggered
     * @throws IllegalArgumentException if label or url is null or empty
     */
    public Action(String label, String url) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Label cannot be null or empty");
        }
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        this.label = label.trim();
        this.url = url.trim();
    }

    /**
     * Gets the label of this action.
     *
     * @return the action label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the URL of this action.
     *
     * @return the action URL
     */
    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(label, action.label) && Objects.equals(url, action.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, url);
    }

    @Override
    public String toString() {
        return "Action{" +
                "label='" + label + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
