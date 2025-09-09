package io.github.haiphamcoder.telegrambots.notification.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMessageTest {

    @Test
    void testBuilder_ValidMessage() {
        Instant timestamp = Instant.now();
        Map<String, String> context = Map.of("key1", "value1", "key2", "value2");
        List<Action> actions = List.of(
            new Action("Action 1", "https://example.com/action1"),
            new Action("Action 2", "https://example.com/action2")
        );
        
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.ERROR)
                .title("Test Title")
                .body("Test Body")
                .timestamp(timestamp)
                .context(context)
                .actions(actions)
                .build();
        
        assertEquals(Severity.ERROR, message.getSeverity());
        assertEquals("Test Title", message.getTitle());
        assertEquals("Test Body", message.getBody());
        assertEquals(timestamp, message.getTimestamp());
        assertEquals(context, message.getContext());
        assertEquals(actions, message.getActions());
    }

    @Test
    void testBuilder_DefaultTimestamp() {
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.INFO)
                .title("Test Title")
                .body("Test Body")
                .build();
        
        assertNotNull(message.getTimestamp());
        assertTrue(message.getTimestamp().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    void testBuilder_EmptyContextAndActions() {
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.WARNING)
                .title("Test Title")
                .body("Test Body")
                .build();
        
        assertTrue(message.getContext().isEmpty());
        assertTrue(message.getActions().isEmpty());
    }

    @Test
    void testBuilder_AddSingleAction() {
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.INFO)
                .title("Test Title")
                .body("Test Body")
                .addAction(new Action("Action 1", "https://example.com"))
                .addAction(new Action("Action 2", "https://example2.com"))
                .build();
        
        assertEquals(2, message.getActions().size());
        assertEquals("Action 1", message.getActions().get(0).getLabel());
        assertEquals("https://example.com", message.getActions().get(0).getUrl());
    }

    @Test
    void testBuilder_MissingRequiredFields() {
        // Missing severity
        assertThrows(IllegalArgumentException.class, () -> {
            NotificationMessage.builder()
                    .title("Test Title")
                    .body("Test Body")
                    .build();
        });
        
        // Missing title
        assertThrows(IllegalArgumentException.class, () -> {
            NotificationMessage.builder()
                    .severity(Severity.INFO)
                    .body("Test Body")
                    .build();
        });
        
        // Empty title
        assertThrows(IllegalArgumentException.class, () -> {
            NotificationMessage.builder()
                    .severity(Severity.INFO)
                    .title("")
                    .body("Test Body")
                    .build();
        });
        
        // Missing body
        assertThrows(IllegalArgumentException.class, () -> {
            NotificationMessage.builder()
                    .severity(Severity.INFO)
                    .title("Test Title")
                    .build();
        });
        
        // Empty body
        assertThrows(IllegalArgumentException.class, () -> {
            NotificationMessage.builder()
                    .severity(Severity.INFO)
                    .title("Test Title")
                    .body("")
                    .build();
        });
    }

    @Test
    void testBuilder_WhitespaceTrimming() {
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.INFO)
                .title("  Test Title  ")
                .body("  Test Body  ")
                .build();
        
        assertEquals("Test Title", message.getTitle());
        assertEquals("Test Body", message.getBody());
    }

    @Test
    void testEqualsAndHashCode() {
        Instant timestamp = Instant.now();
        Map<String, String> context = Map.of("key", "value");
        List<Action> actions = List.of(new Action("Action", "https://example.com"));
        
        NotificationMessage message1 = NotificationMessage.builder()
                .severity(Severity.ERROR)
                .title("Test Title")
                .body("Test Body")
                .timestamp(timestamp)
                .context(context)
                .actions(actions)
                .build();
        
        NotificationMessage message2 = NotificationMessage.builder()
                .severity(Severity.ERROR)
                .title("Test Title")
                .body("Test Body")
                .timestamp(timestamp)
                .context(context)
                .actions(actions)
                .build();
        
        NotificationMessage message3 = NotificationMessage.builder()
                .severity(Severity.INFO)
                .title("Test Title")
                .body("Test Body")
                .timestamp(timestamp)
                .context(context)
                .actions(actions)
                .build();
        
        assertEquals(message1, message2);
        assertNotEquals(message1, message3);
        assertEquals(message1.hashCode(), message2.hashCode());
        assertNotEquals(message1.hashCode(), message3.hashCode());
    }

    @Test
    void testToString() {
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.WARNING)
                .title("Test Title")
                .body("Test Body")
                .build();
        
        String str = message.toString();
        assertTrue(str.contains("NotificationMessage"));
        assertTrue(str.contains("severity=WARNING"));
        assertTrue(str.contains("title='Test Title'"));
        assertTrue(str.contains("body='Test Body'"));
    }

    @Test
    void testImmutability() {
        Map<String, String> context = Map.of("key", "value");
        List<Action> actions = List.of(new Action("Action", "https://example.com"));
        
        NotificationMessage message = NotificationMessage.builder()
                .severity(Severity.INFO)
                .title("Test Title")
                .body("Test Body")
                .context(context)
                .actions(actions)
                .build();
        
        // Context should be unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> {
            message.getContext().put("newKey", "newValue");
        });
        
        // Actions should be unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> {
            message.getActions().add(new Action("New Action", "https://example2.com"));
        });
    }
}
