package io.github.haiphamcoder.telegrambots.notification.template;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MarkdownV2Escaper.
 */
class MarkdownV2EscaperTest {

    @Test
    void testEscapeText() {
        // Test basic escaping
        assertEquals("Hello\\_World", MarkdownV2Escaper.escapeText("Hello_World"));
        assertEquals("\\*Bold\\*", MarkdownV2Escaper.escapeText("*Bold*"));
        assertEquals("\\[Link\\]", MarkdownV2Escaper.escapeText("[Link]"));
        assertEquals("\\(URL\\)", MarkdownV2Escaper.escapeText("(URL)"));
        assertEquals("\\~Strike\\~", MarkdownV2Escaper.escapeText("~Strike~"));
        assertEquals("\\`Code\\`", MarkdownV2Escaper.escapeText("`Code`"));
        assertEquals("\\>Quote\\>", MarkdownV2Escaper.escapeText(">Quote>"));
        assertEquals("\\#Header\\#", MarkdownV2Escaper.escapeText("#Header#"));
        assertEquals("\\+Plus\\+", MarkdownV2Escaper.escapeText("+Plus+"));
        assertEquals("\\-Minus\\-", MarkdownV2Escaper.escapeText("-Minus-"));
        assertEquals("\\=Equals\\=", MarkdownV2Escaper.escapeText("=Equals="));
        assertEquals("\\|Pipe\\|", MarkdownV2Escaper.escapeText("|Pipe|"));
        assertEquals("\\{Brace\\}", MarkdownV2Escaper.escapeText("{Brace}"));
        assertEquals("\\.Dot\\.", MarkdownV2Escaper.escapeText(".Dot."));
        assertEquals("\\!Exclamation\\!", MarkdownV2Escaper.escapeText("!Exclamation!"));
        
        // Test multiple characters
        assertEquals("\\*\\_\\[\\]\\(\\)\\~\\`\\>\\#\\+\\-\\=\\|\\{\\}\\.\\!", 
                   MarkdownV2Escaper.escapeText("*_[]()~`>#+-=|{}.!"));
        
        // Test backslash escaping
        assertEquals("\\\\", MarkdownV2Escaper.escapeText("\\"));
        assertEquals("\\\\\\*", MarkdownV2Escaper.escapeText("\\*"));
        
        // Test null input
        assertNull(MarkdownV2Escaper.escapeText(null));
        
        // Test empty string
        assertEquals("", MarkdownV2Escaper.escapeText(""));
        
        // Test string without special characters
        assertEquals("Hello World", MarkdownV2Escaper.escapeText("Hello World"));
    }

    @Test
    void testEscapeCode() {
        // Test basic code escaping
        assertEquals("\\`code\\`", MarkdownV2Escaper.escapeCode("`code`"));
        assertEquals("\\\\", MarkdownV2Escaper.escapeCode("\\"));
        
        // Test null input
        assertNull(MarkdownV2Escaper.escapeCode(null));
        
        // Test empty string
        assertEquals("", MarkdownV2Escaper.escapeCode(""));
        
        // Test string without special characters
        assertEquals("code", MarkdownV2Escaper.escapeCode("code"));
    }

    @Test
    void testFormatContext() {
        Map<String, String> context = new HashMap<>();
        context.put("key1", "value1");
        context.put("key2", "value2_with_underscore");
        context.put("key3", "value3*with*asterisk");
        context.put("key4", "value4#with#hash");
        
        String result = MarkdownV2Escaper.formatContext(context);
        
        assertTrue(result.contains("key1: value1"));
        assertTrue(result.contains("key2: value2\\_with\\_underscore"));
        assertTrue(result.contains("key3: value3\\*with\\*asterisk"));
        assertTrue(result.contains("key4: value4\\#with\\#hash"));
        assertTrue(result.contains("\n"));
        
        // Test null context
        assertEquals("", MarkdownV2Escaper.formatContext(null));
        
        // Test empty context
        assertEquals("", MarkdownV2Escaper.formatContext(new HashMap<>()));
    }

    @Test
    void testFormatContextAsJson() {
        Map<String, String> context = new HashMap<>();
        context.put("key1", "value1");
        context.put("key2", "value2_with_underscore");
        context.put("key3", "value3*with*asterisk");
        context.put("key4", "value4#with#hash");
        
        String result = MarkdownV2Escaper.formatContextAsJson(context);
        
        assertTrue(result.contains("\"key1\": \"value1\""));
        assertTrue(result.contains("\"key2\": \"value2\\_with\\_underscore\""));
        assertTrue(result.contains("\"key3\": \"value3\\*with\\*asterisk\""));
        assertTrue(result.contains("\"key4\": \"value4\\#with\\#hash\""));
        assertTrue(result.startsWith("{"));
        assertTrue(result.endsWith("}"));
        
        // Test null context
        assertEquals("", MarkdownV2Escaper.formatContextAsJson(null));
        
        // Test empty context
        assertEquals("", MarkdownV2Escaper.formatContextAsJson(new HashMap<>()));
    }

    @Test
    void testFormatTimestamp() {
        // Test normal timestamp (dash should be escaped in MarkdownV2)
        assertEquals("2023\\-01\\-01 12:00:00", MarkdownV2Escaper.formatTimestamp("2023-01-01 12:00:00"));
        
        // Test timestamp with special characters (underscore should be escaped)
        assertEquals("2023\\-01\\-01\\_12:00:00", MarkdownV2Escaper.formatTimestamp("2023-01-01_12:00:00"));
        
        // Test null timestamp
        assertEquals("", MarkdownV2Escaper.formatTimestamp(null));
    }
}
