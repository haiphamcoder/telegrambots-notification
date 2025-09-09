package io.github.haiphamcoder.telegrambots.notification.template;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MarkdownEscaper.
 */
class MarkdownEscaperTest {

    @Test
    void testEscapeText() {
        // Test basic escaping
        assertEquals("Hello\\_World", MarkdownEscaper.escapeText("Hello_World"));
        assertEquals("\\*Bold\\*", MarkdownEscaper.escapeText("*Bold*"));
        assertEquals("\\[Link\\]", MarkdownEscaper.escapeText("[Link]"));
        assertEquals("\\(URL\\)", MarkdownEscaper.escapeText("(URL)"));
        
        // Test multiple characters
        assertEquals("\\*\\_\\[\\]\\(\\)", MarkdownEscaper.escapeText("*_[]()"));
        
        // Test backslash escaping
        assertEquals("\\\\", MarkdownEscaper.escapeText("\\"));
        assertEquals("\\\\\\*", MarkdownEscaper.escapeText("\\*"));
        
        // Test null input
        assertNull(MarkdownEscaper.escapeText(null));
        
        // Test empty string
        assertEquals("", MarkdownEscaper.escapeText(""));
        
        // Test string without special characters
        assertEquals("Hello World", MarkdownEscaper.escapeText("Hello World"));
    }

    @Test
    void testEscapeCode() {
        // Test basic code escaping
        assertEquals("\\`code\\`", MarkdownEscaper.escapeCode("`code`"));
        assertEquals("\\\\", MarkdownEscaper.escapeCode("\\"));
        
        // Test null input
        assertNull(MarkdownEscaper.escapeCode(null));
        
        // Test empty string
        assertEquals("", MarkdownEscaper.escapeCode(""));
        
        // Test string without special characters
        assertEquals("code", MarkdownEscaper.escapeCode("code"));
    }

    @Test
    void testFormatContext() {
        Map<String, String> context = new HashMap<>();
        context.put("key1", "value1");
        context.put("key2", "value2_with_underscore");
        context.put("key3", "value3*with*asterisk");
        
        String result = MarkdownEscaper.formatContext(context);
        
        assertTrue(result.contains("key1: value1"));
        assertTrue(result.contains("key2: value2\\_with\\_underscore"));
        assertTrue(result.contains("key3: value3\\*with\\*asterisk"));
        assertTrue(result.contains("\n"));
        
        // Test null context
        assertEquals("", MarkdownEscaper.formatContext(null));
        
        // Test empty context
        assertEquals("", MarkdownEscaper.formatContext(new HashMap<>()));
    }

    @Test
    void testFormatContextAsJson() {
        Map<String, String> context = new HashMap<>();
        context.put("key1", "value1");
        context.put("key2", "value2_with_underscore");
        context.put("key3", "value3*with*asterisk");
        
        String result = MarkdownEscaper.formatContextAsJson(context);
        
        assertTrue(result.contains("\"key1\": \"value1\""));
        assertTrue(result.contains("\"key2\": \"value2\\_with\\_underscore\""));
        assertTrue(result.contains("\"key3\": \"value3\\*with\\*asterisk\""));
        assertTrue(result.startsWith("{"));
        assertTrue(result.endsWith("}"));
        
        // Test null context
        assertEquals("", MarkdownEscaper.formatContextAsJson(null));
        
        // Test empty context
        assertEquals("", MarkdownEscaper.formatContextAsJson(new HashMap<>()));
    }

    @Test
    void testFormatTimestamp() {
        // Test normal timestamp
        assertEquals("2023-01-01 12:00:00", MarkdownEscaper.formatTimestamp("2023-01-01 12:00:00"));
        
        // Test timestamp with special characters (underscore should be escaped)
        assertEquals("2023-01-01\\_12:00:00", MarkdownEscaper.formatTimestamp("2023-01-01_12:00:00"));
        
        // Test null timestamp
        assertEquals("", MarkdownEscaper.formatTimestamp(null));
    }
}
