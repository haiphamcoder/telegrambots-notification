package io.github.haiphamcoder.telegrambots.notification.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageSplitterTest {

    @Test
    void testSafeSplitHtml_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            MessageSplitter.safeSplitHtml(null, 100);
        });
    }

    @Test
    void testSafeSplitHtml_InvalidLimit() {
        String html = "Test content";
        
        assertThrows(IllegalArgumentException.class, () -> {
            MessageSplitter.safeSplitHtml(html, 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            MessageSplitter.safeSplitHtml(html, -1);
        });
    }

    @Test
    void testSafeSplitHtml_ShortContent() {
        String html = "Short content";
        List<String> result = MessageSplitter.safeSplitHtml(html, 100);
        
        assertEquals(1, result.size());
        assertEquals(html, result.get(0));
    }

    @Test
    void testSafeSplitHtml_ExactLimit() {
        String html = "a".repeat(100);
        List<String> result = MessageSplitter.safeSplitHtml(html, 100);
        
        assertEquals(1, result.size());
        assertEquals(html, result.get(0));
    }

    @Test
    void testSafeSplitHtml_SplitAtBr() {
        String html = "Line 1<br/>Line 2<br/>Line 3";
        List<String> result = MessageSplitter.safeSplitHtml(html, 10);
        
        assertTrue(result.size() > 1);
        for (String part : result) {
            assertTrue(part.length() <= 10);
        }
    }

    @Test
    void testSafeSplitHtml_SplitAtNewline() {
        String html = "Line 1\nLine 2\nLine 3";
        List<String> result = MessageSplitter.safeSplitHtml(html, 10);
        
        assertTrue(result.size() > 1);
        for (String part : result) {
            assertTrue(part.length() <= 10);
        }
    }

    @Test
    void testSafeSplitHtml_SplitAtWhitespace() {
        String html = "This is a very long line that should be split at whitespace boundaries";
        List<String> result = MessageSplitter.safeSplitHtml(html, 20);
        
        assertTrue(result.size() > 1);
        for (String part : result) {
            assertTrue(part.length() <= 20);
        }
    }

    @Test
    void testSafeSplitHtml_ComplexHtml() {
        String html = "<b>Title</b><br/>" +
                     "<blockquote>This is a long blockquote content that should be split properly</blockquote>" +
                     "<pre>Code content here</pre>";
        List<String> result = MessageSplitter.safeSplitHtml(html, 30);
        
        assertTrue(result.size() > 1);
        for (String part : result) {
            assertTrue(part.length() <= 30);
        }
    }

    @Test
    void testSafeSplitHtml_WithHtmlEntities() {
        String html = "Content with &amp; entities &lt;tag&gt; and &quot;quotes&quot;";
        List<String> result = MessageSplitter.safeSplitHtml(html, 20);
        
        assertTrue(result.size() > 1);
        for (String part : result) {
            assertTrue(part.length() <= 20);
            // Ensure entities are not broken
            assertFalse(part.contains("&amp") && !part.contains(";"));
        }
    }

    @Test
    void testSafeSplitHtml_VeryLongContent() {
        // Create a very long HTML content
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("Line ").append(i).append("<br/>");
        }
        String html = sb.toString();
        
        List<String> result = MessageSplitter.safeSplitHtml(html, 50);
        
        assertTrue(result.size() > 1);
        for (String part : result) {
            assertTrue(part.length() <= 50);
        }
    }

    @Test
    void testSafeSplitHtml_EmptyPartsRemoved() {
        String html = "Content<br/><br/><br/>More content";
        List<String> result = MessageSplitter.safeSplitHtml(html, 10);
        
        for (String part : result) {
            assertFalse(part.trim().isEmpty());
        }
    }
}
