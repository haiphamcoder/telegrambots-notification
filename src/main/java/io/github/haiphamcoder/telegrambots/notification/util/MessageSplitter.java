package io.github.haiphamcoder.telegrambots.notification.util;

import io.github.haiphamcoder.telegrambots.notification.model.ParseMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for safely splitting messages to fit within Telegram's 4096 character limit.
 * This class provides intelligent splitting that preserves markup structure and avoids breaking syntax.
 */
public final class MessageSplitter {
    
    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&[a-zA-Z0-9#]+;");
    private static final Pattern BR_TAG_PATTERN = Pattern.compile("<br\\s*/?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern BLOCKQUOTE_PATTERN = Pattern.compile("</blockquote>", Pattern.CASE_INSENSITIVE);
    private static final Pattern PRE_TAG_PATTERN = Pattern.compile("</pre>", Pattern.CASE_INSENSITIVE);
    
    // Markdown patterns
    private static final Pattern MARKDOWN_CODE_BLOCK_PATTERN = Pattern.compile("```[\\s\\S]*?```");
    private static final Pattern MARKDOWN_LINK_PATTERN = Pattern.compile("\\[([^\\]]+)\\]\\([^)]+\\)");
    private static final Pattern MARKDOWN_ESCAPE_PATTERN = Pattern.compile("\\\\[\\*_\\[\\]\\(\\)~`>#+\\-=|{}\\.!]");
    
    // MarkdownV2 patterns
    private static final Pattern MARKDOWN_V2_CODE_BLOCK_PATTERN = Pattern.compile("```[\\s\\S]*?```");
    private static final Pattern MARKDOWN_V2_LINK_PATTERN = Pattern.compile("\\[([^\\]]+)\\]\\([^)]+\\)");
    private static final Pattern MARKDOWN_V2_ESCAPE_PATTERN = Pattern.compile("\\\\[\\*_\\[\\]\\(\\)~`>#+\\-=|{}\\.!]");
    
    private MessageSplitter() {
        // Utility class - prevent instantiation
    }

    /**
     * Safely splits content into parts that fit within the specified soft limit.
     * The splitting algorithm prioritizes breaking at natural boundaries to preserve readability.
     *
     * @param content the content to split
     * @param softLimit the maximum length for each part (should be less than 4096)
     * @param parseMode the parse mode (HTML, MARKDOWN, MARKDOWN_V2)
     * @return a list of content parts, each within the soft limit
     * @throws IllegalArgumentException if content is null, softLimit is not positive, or parseMode is null
     */
    public static List<String> safeSplit(String content, int softLimit, ParseMode parseMode) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        if (softLimit <= 0) {
            throw new IllegalArgumentException("Soft limit must be positive");
        }
        if (parseMode == null) {
            throw new IllegalArgumentException("Parse mode cannot be null");
        }
        
        // If content is already within limit, return as single part
        if (content.length() <= softLimit) {
            return List.of(content);
        }
        
        List<String> parts = new ArrayList<>();
        int start = 0;
        
        while (start < content.length()) {
            int end = findBestSplitPoint(content, start, softLimit, parseMode);
            if (end <= start) {
                // Fallback: force split at soft limit
                end = Math.min(start + softLimit, content.length());
            }
            
            String part = content.substring(start, end).trim();
            if (!part.isEmpty()) {
                parts.add(part);
            }
            
            start = end;
        }
        
        return parts;
    }

    /**
     * Safely splits HTML content into parts that fit within the specified soft limit.
     * The splitting algorithm prioritizes breaking at natural boundaries like &lt;br/&gt; tags,
     * newlines, and whitespace to preserve readability.
     *
     * @param html the HTML content to split
     * @param softLimit the maximum length for each part (should be less than 4096)
     * @return a list of HTML parts, each within the soft limit
     * @throws IllegalArgumentException if html is null or softLimit is not positive
     * @deprecated Use {@link #safeSplit(String, int, ParseMode)} with ParseMode.HTML instead
     */
    @Deprecated
    public static List<String> safeSplitHtml(String html, int softLimit) {
        return safeSplit(html, softLimit, ParseMode.HTML);
    }

    /**
     * Finds the best split point within the given range based on parse mode.
     * The algorithm tries to split at natural boundaries to preserve markup structure.
     *
     * @param content the content
     * @param start the start position
     * @param maxLength the maximum length from start
     * @param parseMode the parse mode
     * @return the best split position, or start if no good split point found
     */
    private static int findBestSplitPoint(String content, int start, int maxLength, ParseMode parseMode) {
        int end = Math.min(start + maxLength, content.length());
        
        switch (parseMode) {
            case HTML:
                return findBestHtmlSplitPoint(content, start, end);
            case MARKDOWN:
                return findBestMarkdownSplitPoint(content, start, end);
            case MARKDOWN_V2:
                return findBestMarkdownV2SplitPoint(content, start, end);
            default:
                return findBestGenericSplitPoint(content, start, end);
        }
    }

    /**
     * Finds the best split point for HTML content.
     */
    private static int findBestHtmlSplitPoint(String content, int start, int end) {
        // Look for <br/> tags first (highest priority)
        int brSplit = findLastOccurrence(content, start, end, BR_TAG_PATTERN);
        if (brSplit > start) {
            return brSplit;
        }
        
        // Look for blockquote endings
        int blockquoteSplit = findLastOccurrence(content, start, end, BLOCKQUOTE_PATTERN);
        if (blockquoteSplit > start) {
            return blockquoteSplit;
        }
        
        // Look for pre tag endings
        int preSplit = findLastOccurrence(content, start, end, PRE_TAG_PATTERN);
        if (preSplit > start) {
            return preSplit;
        }
        
        // Look for newlines
        int newlineSplit = findLastOccurrence(content, start, end, "\n");
        if (newlineSplit > start) {
            return newlineSplit;
        }
        
        // Look for whitespace
        int whitespaceSplit = findLastWhitespace(content, start, end);
        if (whitespaceSplit > start) {
            return whitespaceSplit;
        }
        
        // Look for HTML entity boundaries to avoid breaking them
        int entitySplit = findEntityBoundary(content, start, end);
        if (entitySplit > start) {
            return entitySplit;
        }
        
        return end;
    }

    /**
     * Finds the best split point for Markdown content.
     */
    private static int findBestMarkdownSplitPoint(String content, int start, int end) {
        // Look for code block endings
        int codeBlockSplit = findLastOccurrence(content, start, end, MARKDOWN_CODE_BLOCK_PATTERN);
        if (codeBlockSplit > start) {
            return codeBlockSplit;
        }
        
        // Look for newlines
        int newlineSplit = findLastOccurrence(content, start, end, "\n");
        if (newlineSplit > start) {
            return newlineSplit;
        }
        
        // Look for whitespace
        int whitespaceSplit = findLastWhitespace(content, start, end);
        if (whitespaceSplit > start) {
            return whitespaceSplit;
        }
        
        // Look for escape sequence boundaries
        int escapeSplit = findEscapeBoundary(content, start, end, MARKDOWN_ESCAPE_PATTERN);
        if (escapeSplit > start) {
            return escapeSplit;
        }
        
        return end;
    }

    /**
     * Finds the best split point for MarkdownV2 content.
     */
    private static int findBestMarkdownV2SplitPoint(String content, int start, int end) {
        // Look for code block endings
        int codeBlockSplit = findLastOccurrence(content, start, end, MARKDOWN_V2_CODE_BLOCK_PATTERN);
        if (codeBlockSplit > start) {
            return codeBlockSplit;
        }
        
        // Look for newlines
        int newlineSplit = findLastOccurrence(content, start, end, "\n");
        if (newlineSplit > start) {
            return newlineSplit;
        }
        
        // Look for whitespace
        int whitespaceSplit = findLastWhitespace(content, start, end);
        if (whitespaceSplit > start) {
            return whitespaceSplit;
        }
        
        // Look for escape sequence boundaries
        int escapeSplit = findEscapeBoundary(content, start, end, MARKDOWN_V2_ESCAPE_PATTERN);
        if (escapeSplit > start) {
            return escapeSplit;
        }
        
        return end;
    }

    /**
     * Finds the best split point for generic content.
     */
    private static int findBestGenericSplitPoint(String content, int start, int end) {
        // Look for newlines
        int newlineSplit = findLastOccurrence(content, start, end, "\n");
        if (newlineSplit > start) {
            return newlineSplit;
        }
        
        // Look for whitespace
        int whitespaceSplit = findLastWhitespace(content, start, end);
        if (whitespaceSplit > start) {
            return whitespaceSplit;
        }
        
        return end;
    }

    /**
     * Finds the last occurrence of a pattern within the specified range.
     *
     * @param html the HTML content
     * @param start the start position
     * @param end the end position
     * @param pattern the pattern to search for
     * @return the position after the last occurrence, or start if not found
     */
    private static int findLastOccurrence(String html, int start, int end, Pattern pattern) {
        int lastMatch = start;
        var matcher = pattern.matcher(html);
        matcher.region(start, end);
        
        while (matcher.find()) {
            lastMatch = matcher.end();
        }
        
        return lastMatch;
    }

    /**
     * Finds the last occurrence of a string within the specified range.
     *
     * @param html the HTML content
     * @param start the start position
     * @param end the end position
     * @param search the string to search for
     * @return the position after the last occurrence, or start if not found
     */
    private static int findLastOccurrence(String html, int start, int end, String search) {
        int lastMatch = start;
        int index = html.indexOf(search, start);
        
        while (index >= 0 && index < end) {
            lastMatch = index + search.length();
            index = html.indexOf(search, lastMatch);
        }
        
        return lastMatch;
    }

    /**
     * Finds the last whitespace character within the specified range.
     *
     * @param html the HTML content
     * @param start the start position
     * @param end the end position
     * @return the position after the last whitespace, or start if not found
     */
    private static int findLastWhitespace(String html, int start, int end) {
        for (int i = end - 1; i >= start; i--) {
            if (Character.isWhitespace(html.charAt(i))) {
                return i + 1;
            }
        }
        return start;
    }

    /**
     * Finds a safe position before an HTML entity to avoid breaking it.
     *
     * @param html the HTML content
     * @param start the start position
     * @param end the end position
     * @return the position before the last entity, or start if not found
     */
    private static int findEntityBoundary(String html, int start, int end) {
        int lastAmpersand = html.lastIndexOf('&', end - 1);
        if (lastAmpersand >= start) {
            // Check if this is the start of an HTML entity
            int semicolon = html.indexOf(';', lastAmpersand);
            if (semicolon > lastAmpersand && semicolon < end) {
                // This is an HTML entity, split before it
                return lastAmpersand;
            }
        }
        return start;
    }

    /**
     * Finds a safe position before an escape sequence to avoid breaking it.
     *
     * @param content the content
     * @param start the start position
     * @param end the end position
     * @param escapePattern the escape pattern to match
     * @return the position before the last escape sequence, or start if not found
     */
    private static int findEscapeBoundary(String content, int start, int end, Pattern escapePattern) {
        int lastEscape = findLastOccurrence(content, start, end, escapePattern);
        if (lastEscape > start) {
            // Move back to before the escape sequence
            return lastEscape - 1;
        }
        return start;
    }
}
