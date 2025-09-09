package io.github.haiphamcoder.telegrambots.notification.template;

import java.util.Map;

/**
 * Utility class for escaping Markdown content to be safely used in Telegram Markdown messages.
 * This class provides methods to escape special Markdown characters and format context data.
 */
public final class MarkdownEscaper {
    
    private MarkdownEscaper() {
        // Utility class - prevent instantiation
    }

    /**
     * Escapes Markdown special characters in the given string for text content.
     * Escapes: _ * [ ] ( )
     *
     * @param input the input string to escape
     * @return the escaped string, or null if input is null
     */
    public static String escapeText(String input) {
        if (input == null) {
            return null;
        }
        
        return input
                .replace("\\", "\\\\")
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    /**
     * Escapes Markdown special characters in the given string for code content.
     * Escapes fewer characters than text content.
     *
     * @param input the input string to escape
     * @return the escaped string, or null if input is null
     */
    public static String escapeCode(String input) {
        if (input == null) {
            return null;
        }
        
        return input
                .replace("\\", "\\\\")
                .replace("`", "\\`");
    }

    /**
     * Formats a context map as a readable string with escaped values.
     * Each key-value pair is formatted as "key: value" on separate lines.
     *
     * @param context the context map to format
     * @return the formatted context string, or empty string if context is null or empty
     */
    public static String formatContext(Map<String, String> context) {
        if (context == null || context.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        for (Map.Entry<String, String> entry : context.entrySet()) {
            if (!first) {
                sb.append("\n");
            }
            sb.append(escapeText(entry.getKey()))
              .append(": ")
              .append(escapeText(entry.getValue()));
            first = false;
        }
        
        return sb.toString();
    }

    /**
     * Formats a context map as a compact JSON-like string with escaped values.
     * This is useful for error messages where a more structured format is desired.
     *
     * @param context the context map to format
     * @return the formatted context string in JSON-like format, or empty string if context is null or empty
     */
    public static String formatContextAsJson(Map<String, String> context) {
        if (context == null || context.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        
        for (Map.Entry<String, String> entry : context.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("\"")
              .append(escapeText(entry.getKey()))
              .append("\": \"")
              .append(escapeText(entry.getValue()))
              .append("\"");
            first = false;
        }
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * Escapes and formats a timestamp for display in Markdown.
     *
     * @param timestamp the timestamp string to format
     * @return the escaped timestamp string, or empty string if timestamp is null
     */
    public static String formatTimestamp(String timestamp) {
        if (timestamp == null) {
            return "";
        }
        return escapeText(timestamp);
    }
}
