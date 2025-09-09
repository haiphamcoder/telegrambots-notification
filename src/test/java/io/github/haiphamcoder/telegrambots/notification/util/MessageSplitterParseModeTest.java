package io.github.haiphamcoder.telegrambots.notification.util;

import io.github.haiphamcoder.telegrambots.notification.model.ParseMode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MessageSplitter with ParseMode support.
 */
class MessageSplitterParseModeTest {

    @Test
    void testSafeSplitWithMarkdown() {
        String markdown = "**Bold text** and _italic text_ with [link](url) and `code`.";
        List<String> result = MessageSplitter.safeSplit(markdown, 30, ParseMode.MARKDOWN);
        
        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void testSafeSplitWithMarkdownV2() {
        String markdownV2 = "**Bold text** and _italic text_ with [link](url) and `code`.";
        List<String> result = MessageSplitter.safeSplit(markdownV2, 30, ParseMode.MARKDOWN_V2);
        
        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void testSafeSplitWithCodeBlocks() {
        String markdown = "```\nThis is a code block\nwith multiple lines\n```";
        List<String> result = MessageSplitter.safeSplit(markdown, 20, ParseMode.MARKDOWN);
        
        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void testSafeSplitWithEscapeSequences() {
        String markdownV2 = "This is \\*escaped\\* text with \\_underscores\\_ and \\[brackets\\]";
        List<String> result = MessageSplitter.safeSplit(markdownV2, 20, ParseMode.MARKDOWN_V2);
        
        assertNotNull(result);
        // Should not split escape sequences
        for (String part : result) {
            if (part.contains("\\")) {
                // Check that escape sequences are not broken
                assertFalse(part.contains("\\*") && part.endsWith("\\"));
            }
        }
    }

    @Test
    void testSafeSplitWithShortContent() {
        String content = "Short message";
        List<String> result = MessageSplitter.safeSplit(content, 100, ParseMode.HTML);
        
        assertEquals(1, result.size());
        assertEquals("Short message", result.get(0));
    }

    @Test
    void testSafeSplitWithNullContent() {
        assertThrows(IllegalArgumentException.class, () -> 
            MessageSplitter.safeSplit(null, 100, ParseMode.HTML));
    }

    @Test
    void testSafeSplitWithInvalidSoftLimit() {
        String content = "Test message";
        assertThrows(IllegalArgumentException.class, () -> 
            MessageSplitter.safeSplit(content, 0, ParseMode.HTML));
        assertThrows(IllegalArgumentException.class, () -> 
            MessageSplitter.safeSplit(content, -1, ParseMode.HTML));
    }

    @Test
    void testSafeSplitWithNullParseMode() {
        String content = "Test message";
        assertThrows(IllegalArgumentException.class, () -> 
            MessageSplitter.safeSplit(content, 100, null));
    }

    @Test
    void testSafeSplitWithLongMarkdownV2Message() {
        StringBuilder sb = new StringBuilder();
        sb.append("**CRITICAL ERROR**\n");
        sb.append("System failure detected with multiple issues:\n");
        sb.append("1. Database connection lost\n");
        sb.append("2. Cache service unavailable\n");
        sb.append("3. External API timeout\n");
        sb.append("4. Memory usage exceeded 90%\n");
        sb.append("5. Disk space critical\n");
        sb.append("6. Network latency high\n");
        sb.append("7. Authentication service down\n");
        sb.append("8. Logging service failed\n");
        sb.append("9. Monitoring alerts disabled\n");
        sb.append("10. Backup service unreachable\n");
        sb.append("\n**Context:**\n");
        sb.append("```json\n");
        sb.append("{\n");
        sb.append("  \"timestamp\": \"2023-01-01T12:00:00Z\",\n");
        sb.append("  \"server\": \"prod-web-01\",\n");
        sb.append("  \"region\": \"us-east-1\",\n");
        sb.append("  \"version\": \"1.2.3\",\n");
        sb.append("  \"environment\": \"production\"\n");
        sb.append("}\n");
        sb.append("```\n");
        sb.append("\n**Actions:**\n");
        sb.append("[View Dashboard](https://monitoring.example.com)\n");
        sb.append("[Check Logs](https://logs.example.com)\n");
        sb.append("[Contact Team](https://team.example.com)");
        
        String longMessage = sb.toString();
        List<String> result = MessageSplitter.safeSplit(longMessage, 100, ParseMode.MARKDOWN_V2);
        
        assertNotNull(result);
        assertTrue(result.size() > 1);
        
        // Verify that code blocks are not broken
        for (String part : result) {
            if (part.contains("```")) {
                // If it contains code block markers, they should be properly paired
                long openCount = part.chars().filter(ch -> ch == '`').count();
                assertTrue(openCount % 3 == 0, "Code block markers should be properly paired");
            }
        }
    }

    @Test
    void testSafeSplitWithMixedContent() {
        String mixedContent = "**HTML:** <b>bold</b> and <i>italic</i>\n" +
                            "**Markdown:** *italic* and **bold**\n" +
                            "**MarkdownV2:** _italic_ and **bold**\n" +
                            "```\nCode block\n```\n" +
                            "[Link](https://example.com)";
        
        List<String> result = MessageSplitter.safeSplit(mixedContent, 50, ParseMode.MARKDOWN_V2);
        
        assertNotNull(result);
        assertTrue(result.size() > 1);
    }
}
