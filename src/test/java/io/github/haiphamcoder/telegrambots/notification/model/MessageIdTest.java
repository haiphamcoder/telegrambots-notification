package io.github.haiphamcoder.telegrambots.notification.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageIdTest {

    @Test
    void testOf() {
        MessageId messageId = MessageId.of(12345L);
        assertEquals(12345L, messageId.messageId());
    }

    @Test
    void testToString() {
        MessageId messageId = MessageId.of(12345L);
        String str = messageId.toString();
        assertTrue(str.contains("12345"));
        assertTrue(str.contains("MessageId"));
    }

    @Test
    void testEquals() {
        MessageId messageId1 = MessageId.of(12345L);
        MessageId messageId2 = MessageId.of(12345L);
        MessageId messageId3 = MessageId.of(67890L);

        assertEquals(messageId1, messageId2);
        assertNotEquals(messageId1, messageId3);
    }

    @Test
    void testHashCode() {
        MessageId messageId1 = MessageId.of(12345L);
        MessageId messageId2 = MessageId.of(12345L);
        MessageId messageId3 = MessageId.of(67890L);

        assertEquals(messageId1.hashCode(), messageId2.hashCode());
        assertNotEquals(messageId1.hashCode(), messageId3.hashCode());
    }
}
