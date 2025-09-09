package io.github.haiphamcoder.telegrambots.notification.template;

import io.github.haiphamcoder.telegrambots.notification.model.NotificationMessage;
import io.github.haiphamcoder.telegrambots.notification.model.Severity;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Default implementation of NotificationFormatter that uses HTML templates for different severity levels.
 * This formatter automatically escapes HTML content and supports custom templates.
 */
public class DefaultHtmlNotificationFormatter implements NotificationFormatter {
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    
    private final Map<Severity, String> customTemplates;

    /**
     * Creates a new formatter with default templates.
     */
    public DefaultHtmlNotificationFormatter() {
        this.customTemplates = null;
    }

    /**
     * Creates a new formatter with custom templates.
     * Custom templates will override the default templates for specified severity levels.
     *
     * @param customTemplates a map of severity levels to custom HTML templates
     */
    public DefaultHtmlNotificationFormatter(Map<Severity, String> customTemplates) {
        this.customTemplates = customTemplates != null ? Map.copyOf(customTemplates) : null;
    }

    @Override
    public String format(NotificationMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        
        String template = getTemplate(message.getSeverity());
        return formatWithTemplate(message, template);
    }

    @Override
    public String format(NotificationMessage message, String customTemplate) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (customTemplate == null) {
            throw new IllegalArgumentException("Custom template cannot be null");
        }
        
        return formatWithTemplate(message, customTemplate);
    }

    /**
     * Formats a message using the specified template.
     *
     * @param message the message to format
     * @param template the template to use
     * @return the formatted HTML string
     */
    private String formatWithTemplate(NotificationMessage message, String template) {
        String result = template;
        
        // Replace placeholders with escaped values
        result = result.replace("{{title}}", HtmlEscaper.escape(message.getTitle()));
        result = result.replace("{{body}}", HtmlEscaper.escape(message.getBody()));
        result = result.replace("{{timestamp}}", HtmlEscaper.formatTimestamp(
            TIMESTAMP_FORMATTER.format(message.getTimestamp())));
        result = result.replace("{{context}}", HtmlEscaper.formatContext(message.getContext()));
        
        // Handle error code for ERROR severity
        if (message.getSeverity() == Severity.ERROR) {
            String errorCode = message.getContext().get("errorCode");
            if (errorCode != null) {
                result = result.replace("{{errorCode}}", HtmlEscaper.escape(errorCode));
            } else {
                result = result.replace("{{errorCode}}", "UNKNOWN");
            }
        }
        
        // Add actions if present
        if (!message.getActions().isEmpty()) {
            result = addActionsToTemplate(result, message);
        }
        
        return result;
    }

    /**
     * Gets the template for the specified severity level.
     * Custom templates take precedence over default templates.
     *
     * @param severity the severity level
     * @return the template string
     */
    private String getTemplate(Severity severity) {
        if (customTemplates != null && customTemplates.containsKey(severity)) {
            return customTemplates.get(severity);
        }
        return DefaultHtmlTemplates.getTemplate(severity);
    }

    /**
     * Adds action links to the template.
     *
     * @param template the template string
     * @param message the message containing actions
     * @return the template with actions added
     */
    private String addActionsToTemplate(String template, NotificationMessage message) {
        if (message.getActions().isEmpty()) {
            return template;
        }
        
        StringBuilder actionsHtml = new StringBuilder();
        actionsHtml.append("<br/><br/><b>Actions:</b><br/>");
        
        for (int i = 0; i < message.getActions().size(); i++) {
            var action = message.getActions().get(i);
            if (i > 0) {
                actionsHtml.append(" | ");
            }
            actionsHtml.append("<a href=\"")
                      .append(HtmlEscaper.escape(action.getUrl()))
                      .append("\">")
                      .append(HtmlEscaper.escape(action.getLabel()))
                      .append("</a>");
        }
        
        // Add actions before the timestamp
        return template.replace("<i>Time:</i>", actionsHtml.toString() + "<br/><i>Time:</i>");
    }
}
