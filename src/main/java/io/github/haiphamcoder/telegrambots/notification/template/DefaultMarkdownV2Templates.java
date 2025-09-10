package io.github.haiphamcoder.telegrambots.notification.template;

import io.github.haiphamcoder.telegrambots.notification.model.Severity;

import java.util.EnumMap;
import java.util.Map;

/**
 * Provides default MarkdownV2 templates for different notification severity levels.
 * These templates are designed to work with Telegram's MarkdownV2 parse mode.
 */
public final class DefaultMarkdownV2Templates {
    
    private static final Map<Severity, String> TEMPLATES = new EnumMap<>(Severity.class);
    
    static {
        // INFO template
        TEMPLATES.put(Severity.INFO, 
            "ℹ️ **[INFO]** **{{title}}**\n" +
            "{{body}}\n\n" +
            "> **Context**\n" +
            "{{context}}\n\n" +
            "_Time:_ `{{timestamp}}`"
        );
        
        // WARNING template
        TEMPLATES.put(Severity.WARNING, 
            "⚠️ **[WARNING]** **{{title}}**\n" +
            "{{body}}\n\n" +
            "> *Context*\n" +
            "{{context}}\n\n" +
            "_Time:_ `{{timestamp}}`"
        );
        
        // ERROR template
        TEMPLATES.put(Severity.ERROR, 
            "🛑 **[ERROR]** **{{title}}**\n" +
            "`{{errorCode}}`: {{body}}\n\n" +
            "```json\n" +
            "{{context}}\n" +
            "```\n\n" +
            "_Time:_ `{{timestamp}}`"
        );
        
        // CRITICAL template
        TEMPLATES.put(Severity.CRITICAL, 
            "🚨 **[CRITICAL]** **{{title}}**\n" +
            "{{body}}\n\n" +
            "> **Immediate Action Required**\n" +
            "{{context}}\n\n" +
            "_Time:_ `{{timestamp}}`"
        );
        
        // DEBUG template
        TEMPLATES.put(Severity.DEBUG, 
            "🐞 **[DEBUG]** **{{title}}**\n" +
            "{{body}}\n\n" +
            "```\n" +
            "{{context}}\n" +
            "```\n\n" +
            "_Time:_ `{{timestamp}}`"
        );
    }
    
    private DefaultMarkdownV2Templates() {
        // Utility class - prevent instantiation
    }

    /**
     * Gets the MarkdownV2 template for the specified severity level.
     *
     * @param severity the severity level
     * @return the MarkdownV2 template string
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
     * @return a map of severity levels to their corresponding MarkdownV2 templates
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
