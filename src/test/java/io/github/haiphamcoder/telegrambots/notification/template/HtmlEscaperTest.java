package io.github.haiphamcoder.telegrambots.notification.template;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HtmlEscaperTest {

    @Test
    void testEscape() {
        // Test null input
        assertNull(HtmlEscaper.escape(null));
        
        // Test empty string
        assertEquals("", HtmlEscaper.escape(""));
        
        // Test basic escaping
        assertEquals("&amp;", HtmlEscaper.escape("&"));
        assertEquals("&lt;", HtmlEscaper.escape("<"));
        assertEquals("&gt;", HtmlEscaper.escape(">"));
        assertEquals("&quot;", HtmlEscaper.escape("\""));
        
        // Test complex string
        String input = "Test & <tag> with \"quotes\"";
        String expected = "Test &amp; &lt;tag&gt; with &quot;quotes&quot;";
        assertEquals(expected, HtmlEscaper.escape(input));
    }

    @Test
    void testFormatContext() {
        // Test null context
        assertEquals("", HtmlEscaper.formatContext(null));
        
        // Test empty context
        assertEquals("", HtmlEscaper.formatContext(new HashMap<>()));
        
        // Test single entry
        Map<String, String> singleEntry = Map.of("key", "value");
        assertEquals("key: value", HtmlEscaper.formatContext(singleEntry));
        
        // Test multiple entries
        Map<String, String> multipleEntries = Map.of(
            "host", "server-01",
            "port", "8080",
            "status", "running"
        );
        String result = HtmlEscaper.formatContext(multipleEntries);
        assertTrue(result.contains("host: server-01"));
        assertTrue(result.contains("port: 8080"));
        assertTrue(result.contains("status: running"));
        
        // Test escaping in values
        Map<String, String> withSpecialChars = Map.of(
            "error", "Error & <failure>",
            "message", "Test \"quoted\" message"
        );
        String escapedResult = HtmlEscaper.formatContext(withSpecialChars);
        assertTrue(escapedResult.contains("Error &amp; &lt;failure&gt;"));
        assertTrue(escapedResult.contains("Test &quot;quoted&quot; message"));
    }

    @Test
    void testFormatContextAsJson() {
        // Test null context
        assertEquals("", HtmlEscaper.formatContextAsJson(null));
        
        // Test empty context
        assertEquals("", HtmlEscaper.formatContextAsJson(new HashMap<>()));
        
        // Test single entry
        Map<String, String> singleEntry = Map.of("key", "value");
        assertEquals("{\"key\": \"value\"}", HtmlEscaper.formatContextAsJson(singleEntry));
        
        // Test multiple entries
        Map<String, String> multipleEntries = Map.of(
            "host", "server-01",
            "port", "8080"
        );
        String result = HtmlEscaper.formatContextAsJson(multipleEntries);
        assertTrue(result.startsWith("{"));
        assertTrue(result.endsWith("}"));
        assertTrue(result.contains("\"host\": \"server-01\""));
        assertTrue(result.contains("\"port\": \"8080\""));
        
        // Test escaping in values
        Map<String, String> withSpecialChars = Map.of(
            "error", "Error & <failure>",
            "message", "Test \"quoted\" message"
        );
        String escapedResult = HtmlEscaper.formatContextAsJson(withSpecialChars);
        assertTrue(escapedResult.contains("\"Error &amp; &lt;failure&gt;\""));
        assertTrue(escapedResult.contains("\"Test &quot;quoted&quot; message\""));
    }

    @Test
    void testFormatTimestamp() {
        // Test null timestamp
        assertEquals("", HtmlEscaper.formatTimestamp(null));
        
        // Test normal timestamp
        assertEquals("2023-01-01 12:00:00", HtmlEscaper.formatTimestamp("2023-01-01 12:00:00"));
        
        // Test timestamp with special characters
        String timestampWithSpecialChars = "2023-01-01 12:00:00 & <test>";
        String expected = "2023-01-01 12:00:00 &amp; &lt;test&gt;";
        assertEquals(expected, HtmlEscaper.formatTimestamp(timestampWithSpecialChars));
    }
}
