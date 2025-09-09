package io.github.haiphamcoder.telegrambots.notification.template;

import io.github.haiphamcoder.telegrambots.notification.model.Action;
import io.github.haiphamcoder.telegrambots.notification.model.NotificationMessage;
import io.github.haiphamcoder.telegrambots.notification.model.ParseMode;
import io.github.haiphamcoder.telegrambots.notification.model.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MarkdownV2NotificationFormatter.
 */
class MarkdownV2NotificationFormatterTest {

    private MarkdownV2NotificationFormatter formatter;
    private NotificationMessage testMessage;

    @BeforeEach
    void setUp() {
        formatter = new MarkdownV2NotificationFormatter();
        
        Map<String, String> context = new HashMap<>();
        context.put("userId", "12345");
        context.put("requestId", "req-abc-123");
        context.put("errorCode", "E001");
        
        testMessage = NotificationMessage.builder()
                .severity(Severity.ERROR)
                .title("Test Error")
                .body("Something went wrong with special characters: _*[]()~`>#+-=|{}.!")
                .timestamp(Instant.parse("2023-01-01T12:00:00Z"))
                .context(context)
                .addAction(new Action("View Details", "https://example.com/details"))
                .addAction(new Action("Retry", "https://example.com/retry"))
                .build();
    }

    @Test
    void testFormatWithDefaultTemplate() {
        String result = formatter.format(testMessage);
        
        assertNotNull(result);
        assertTrue(result.contains("**\\[ERROR\\]**"));
        assertTrue(result.contains("**Test Error**"));
        assertTrue(result.contains("Something went wrong with special characters: \\_\\*\\[\\]\\(\\)\\~\\`\\>\\#\\+\\-\\=\\|\\{\\}\\.\\!"));
        assertTrue(result.contains("userId: 12345"));
        assertTrue(result.contains("requestId: req\\-abc\\-123"));
        assertTrue(result.contains("errorCode: E001"));
        assertTrue(result.contains("_Time:_"));
        assertTrue(result.contains("2023\\-01\\-01"));
        assertTrue(result.contains("**Actions:**"));
        assertTrue(result.contains("[View Details](https://example.com/details)"));
        assertTrue(result.contains("[Retry](https://example.com/retry)"));
    }

    @Test
    void testFormatWithCustomTemplate() {
        String customTemplate = "**{{title}}**\n{{body}}\n\nContext: {{context}}";
        String result = formatter.format(testMessage, customTemplate);
        
        assertNotNull(result);
        assertTrue(result.contains("**Test Error**"));
        assertTrue(result.contains("Something went wrong with special characters: \\_\\*\\[\\]\\(\\)\\~\\`\\>\\#\\+\\-\\=\\|\\{\\}\\.\\!"));
        assertTrue(result.contains("Context:"));
        assertTrue(result.contains("userId: 12345"));
    }

    @Test
    void testFormatWithDifferentSeverities() {
        // Test INFO
        NotificationMessage infoMessage = NotificationMessage.builder()
                .severity(Severity.INFO)
                .title("Info Message")
                .body("This is an info message")
                .timestamp(Instant.parse("2023-01-01T12:00:00Z"))
                .build();
        
        String infoResult = formatter.format(infoMessage);
        assertTrue(infoResult.contains("**\\[INFO\\]**"));
        assertTrue(infoResult.contains("**Info Message**"));
        
        // Test WARNING
        NotificationMessage warningMessage = NotificationMessage.builder()
                .severity(Severity.WARNING)
                .title("Warning Message")
                .body("This is a warning message")
                .timestamp(Instant.parse("2023-01-01T12:00:00Z"))
                .build();
        
        String warningResult = formatter.format(warningMessage);
        assertTrue(warningResult.contains("**\\[WARNING\\]**"));
        assertTrue(warningResult.contains("**Warning Message**"));
        
        // Test CRITICAL
        NotificationMessage criticalMessage = NotificationMessage.builder()
                .severity(Severity.CRITICAL)
                .title("Critical Message")
                .body("This is a critical message")
                .timestamp(Instant.parse("2023-01-01T12:00:00Z"))
                .build();
        
        String criticalResult = formatter.format(criticalMessage);
        assertTrue(criticalResult.contains("**\\[CRITICAL\\]**"));
        assertTrue(criticalResult.contains("**Critical Message**"));
        
        // Test DEBUG
        NotificationMessage debugMessage = NotificationMessage.builder()
                .severity(Severity.DEBUG)
                .title("Debug Message")
                .body("This is a debug message")
                .timestamp(Instant.parse("2023-01-01T12:00:00Z"))
                .build();
        
        String debugResult = formatter.format(debugMessage);
        assertTrue(debugResult.contains("**\\[DEBUG\\]**"));
        assertTrue(debugResult.contains("**Debug Message**"));
    }

    @Test
    void testFormatWithEmptyContext() {
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.INFO)
                .title("Test Message")
                .body("Test body")
                .timestamp(Instant.parse("2023-01-01T12:00:00Z"))
                .build();
        
        String result = formatter.format(message);
        assertNotNull(result);
        assertTrue(result.contains("**Test Message**"));
        assertTrue(result.contains("Test body"));
    }

    @Test
    void testFormatWithNullValues() {
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.INFO)
                .title("Test Message")
                .body("Test body")
                .timestamp(Instant.parse("2023-01-01T12:00:00Z"))
                .build();
        
        String result = formatter.format(message);
        assertNotNull(result);
        assertTrue(result.contains("**Test Message**"));
        assertTrue(result.contains("Test body"));
    }

    @Test
    void testFormatWithActions() {
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.INFO)
                .title("Test Message")
                .body("Test body")
                .timestamp(Instant.parse("2023-01-01T12:00:00Z"))
                .addAction(new Action("Action 1", "https://example.com/action1"))
                .addAction(new Action("Action 2", "https://example.com/action2"))
                .build();
        
        String result = formatter.format(message);
        assertTrue(result.contains("**Actions:**"));
        assertTrue(result.contains("[Action 1](https://example.com/action1)"));
        assertTrue(result.contains("[Action 2](https://example.com/action2)"));
    }

    @Test
    void testMode() {
        assertEquals(ParseMode.MARKDOWN_V2, formatter.mode());
    }

    @Test
    void testFormatWithNullMessage() {
        assertThrows(IllegalArgumentException.class, () -> formatter.format(null));
    }

    @Test
    void testFormatWithNullCustomTemplate() {
        assertThrows(IllegalArgumentException.class, () -> formatter.format(testMessage, null));
    }

    @Test
    void testCustomTemplates() {
        Map<Severity, String> customTemplates = new HashMap<>();
        customTemplates.put(Severity.INFO, "Custom: **{{title}}** \\- {{body}}");
        
        MarkdownV2NotificationFormatter customFormatter = new MarkdownV2NotificationFormatter(customTemplates);
        
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.INFO)
                .title("Test")
                .body("Body")
                .timestamp(Instant.parse("2023-01-01T12:00:00Z"))
                .build();
        
        String result = customFormatter.format(message);
        assertEquals("Custom: **Test** \\- Body", result);
    }
}
