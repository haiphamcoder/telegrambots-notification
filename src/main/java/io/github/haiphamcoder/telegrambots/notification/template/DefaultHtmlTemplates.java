package io.github.haiphamcoder.telegrambots.notification.template;

import io.github.haiphamcoder.telegrambots.notification.model.Severity;

import java.util.EnumMap;
import java.util.Map;

/**
 * Provides default HTML templates for different notification severity levels.
 * These templates are designed to work with Telegram's HTML parse mode and use only
 * supported HTML tags: b/strong, i/em, u/ins, s/del, code, pre, blockquote, a href, br.
 */
public final class DefaultHtmlTemplates {
    
    private static final Map<Severity, String> TEMPLATES = new EnumMap<>(Severity.class);
    
    static {
        // INFO template
        TEMPLATES.put(Severity.INFO, 
            "<b>ℹ️ [INFO]</b> <b>{{title}}</b><br/>" +
            "{{body}}<br/><br/>" +
            "<blockquote>" +
            "<b>Context</b><br/>" +
            "{{context}}" +
            "</blockquote>" +
            "<i>Time:</i> <code>{{timestamp}}</code>"
        );
        
        // WARNING template
        TEMPLATES.put(Severity.WARNING, 
            "<b>⚠️ [WARNING]</b> <b>{{title}}</b><br/>" +
            "<u>{{body}}</u><br/><br/>" +
            "<blockquote expandable>" +
            "<b>Context</b><br/>" +
            "{{context}}" +
            "</blockquote>" +
            "<i>Time:</i> <code>{{timestamp}}</code>"
        );
        
        // ERROR template
        TEMPLATES.put(Severity.ERROR, 
            "<b>🛑 [ERROR]</b> <b>{{title}}</b><br/>" +
            "<code>{{errorCode}}</code>: {{body}}<br/><br/>" +
            "<pre><code class=\"language-json\">{{context}}</code></pre>" +
            "<i>Time:</i> <code>{{timestamp}}</code>"
        );
        
        // CRITICAL template
        TEMPLATES.put(Severity.CRITICAL, 
            "<b>🚨 [CRITICAL]</b> <b>{{title}}</b><br/>" +
            "{{body}}<br/><br/>" +
            "<blockquote>" +
            "<b>Immediate Action Required</b><br/>" +
            "{{context}}" +
            "</blockquote>" +
            "<i>Time:</i> <code>{{timestamp}}</code>"
        );
        
        // DEBUG template
        TEMPLATES.put(Severity.DEBUG, 
            "<b>🐞 [DEBUG]</b> <b>{{title}}</b><br/>" +
            "{{body}}<br/><br/>" +
            "<pre><code>{{context}}</code></pre>" +
            "<i>Time:</i> <code>{{timestamp}}</code>"
        );
    }
    
    private DefaultHtmlTemplates() {
        // Utility class - prevent instantiation
    }

    /**
     * Gets the HTML template for the specified severity level.
     *
     * @param severity the severity level
     * @return the HTML template string
     * @throws IllegalArgumentException if severity is null
     */
    public static String getTemplate(Severity severity) {
        if (severity == null) {
            throw new IllegalArgumentException("Severity cannot be null");
        }
        return TEMPLATES.get(severity);
    }

    /**
     * Gets all available templates as a map.
     *
     * @return a map of severity levels to their corresponding HTML templates
     */
    public static Map<Severity, String> getAllTemplates() {
        return new EnumMap<>(TEMPLATES);
    }

    /**
     * Checks if a template exists for the specified severity level.
     *
     * @param severity the severity level
     * @return true if a template exists, false otherwise
     */
    public static boolean hasTemplate(Severity severity) {
        return severity != null && TEMPLATES.containsKey(severity);
    }
}
