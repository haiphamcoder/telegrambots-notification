package io.github.haiphamcoder.telegrambots.notification.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for safely splitting HTML messages to fit within Telegram's 4096 character limit.
 * This class provides intelligent splitting that preserves HTML structure and avoids breaking entities.
 */
public final class MessageSplitter {
    
    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&[a-zA-Z0-9#]+;");
    private static final Pattern BR_TAG_PATTERN = Pattern.compile("<br\\s*/?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern BLOCKQUOTE_PATTERN = Pattern.compile("</blockquote>", Pattern.CASE_INSENSITIVE);
    private static final Pattern PRE_TAG_PATTERN = Pattern.compile("</pre>", Pattern.CASE_INSENSITIVE);
    
    private MessageSplitter() {
        // Utility class - prevent instantiation
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
     */
    public static List<String> safeSplitHtml(String html, int softLimit) {
        if (html == null) {
            throw new IllegalArgumentException("HTML content cannot be null");
        }
        if (softLimit <= 0) {
            throw new IllegalArgumentException("Soft limit must be positive");
        }
        
        // If content is already within limit, return as single part
        if (html.length() <= softLimit) {
            return List.of(html);
        }
        
        List<String> parts = new ArrayList<>();
        int start = 0;
        
        while (start < html.length()) {
            int end = findBestSplitPoint(html, start, softLimit);
            if (end <= start) {
                // Fallback: force split at soft limit
                end = Math.min(start + softLimit, html.length());
            }
            
            String part = html.substring(start, end).trim();
            if (!part.isEmpty()) {
                parts.add(part);
            }
            
            start = end;
        }
        
        return parts;
    }

    /**
     * Finds the best split point within the given range.
     * The algorithm tries to split at:
     * 1. &lt;br/&gt; tags (highest priority)
     * 2. Newlines
     * 3. Whitespace
     * 4. Before HTML entities to avoid breaking them
     *
     * @param html the HTML content
     * @param start the start position
     * @param maxLength the maximum length from start
     * @return the best split position, or start if no good split point found
     */
    private static int findBestSplitPoint(String html, int start, int maxLength) {
        int end = Math.min(start + maxLength, html.length());
        
        // Look for <br/> tags first (highest priority)
        int brSplit = findLastOccurrence(html, start, end, BR_TAG_PATTERN);
        if (brSplit > start) {
            return brSplit;
        }
        
        // Look for blockquote endings
        int blockquoteSplit = findLastOccurrence(html, start, end, BLOCKQUOTE_PATTERN);
        if (blockquoteSplit > start) {
            return blockquoteSplit;
        }
        
        // Look for pre tag endings
        int preSplit = findLastOccurrence(html, start, end, PRE_TAG_PATTERN);
        if (preSplit > start) {
            return preSplit;
        }
        
        // Look for newlines
        int newlineSplit = findLastOccurrence(html, start, end, "\n");
        if (newlineSplit > start) {
            return newlineSplit;
        }
        
        // Look for whitespace
        int whitespaceSplit = findLastWhitespace(html, start, end);
        if (whitespaceSplit > start) {
            return whitespaceSplit;
        }
        
        // Look for HTML entity boundaries to avoid breaking them
        int entitySplit = findEntityBoundary(html, start, end);
        if (entitySplit > start) {
            return entitySplit;
        }
        
        // No good split point found, return end
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
}
