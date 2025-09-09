package io.github.haiphamcoder.telegrambots.notification.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RetryPolicyTest {

    @Test
    void testConstructor_ValidParameters() {
        RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        
        assertEquals(3, policy.getMaxRetries());
        assertEquals(Duration.ofSeconds(1), policy.getBaseDelay());
        assertEquals(2.0, policy.getMultiplier());
        assertEquals(Duration.ofSeconds(10), policy.getMaxDelay());
    }

    @Test
    void testConstructor_InvalidParameters() {
        // Negative max retries
        assertThrows(IllegalArgumentException.class, () -> {
            new RetryPolicy(-1, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        });
        
        // Null base delay
        assertThrows(IllegalArgumentException.class, () -> {
            new RetryPolicy(3, null, 2.0, Duration.ofSeconds(10));
        });
        
        // Negative base delay
        assertThrows(IllegalArgumentException.class, () -> {
            new RetryPolicy(3, Duration.ofSeconds(-1), 2.0, Duration.ofSeconds(10));
        });
        
        // Zero multiplier
        assertThrows(IllegalArgumentException.class, () -> {
            new RetryPolicy(3, Duration.ofSeconds(1), 0.0, Duration.ofSeconds(10));
        });
        
        // Negative multiplier
        assertThrows(IllegalArgumentException.class, () -> {
            new RetryPolicy(3, Duration.ofSeconds(1), -1.0, Duration.ofSeconds(10));
        });
        
        // Null max delay
        assertThrows(IllegalArgumentException.class, () -> {
            new RetryPolicy(3, Duration.ofSeconds(1), 2.0, null);
        });
        
        // Negative max delay
        assertThrows(IllegalArgumentException.class, () -> {
            new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(-1));
        });
    }

    @Test
    void testComputeBackoff() {
        RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        
        // First attempt (0)
        assertEquals(Duration.ofSeconds(1), policy.computeBackoff(0));
        
        // Second attempt (1)
        assertEquals(Duration.ofSeconds(2), policy.computeBackoff(1));
        
        // Third attempt (2)
        assertEquals(Duration.ofSeconds(4), policy.computeBackoff(2));
        
        // Fourth attempt (3) - should be capped at max delay
        assertEquals(Duration.ofSeconds(8), policy.computeBackoff(3));
        
        // Fifth attempt (4) - should be capped at max delay
        assertEquals(Duration.ofSeconds(10), policy.computeBackoff(4));
    }

    @Test
    void testComputeBackoff_InvalidAttempt() {
        RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        
        assertThrows(IllegalArgumentException.class, () -> {
            policy.computeBackoff(-1);
        });
    }

    @Test
    void testFor429_WithRetryAfter() {
        RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        
        // Use Telegram's retry_after value
        Duration delay = policy.for429(1, 5);
        assertEquals(Duration.ofSeconds(5), delay);
        
        // Retry_after should be capped at max delay
        Duration longDelay = policy.for429(1, 20);
        assertEquals(Duration.ofSeconds(10), longDelay);
    }

    @Test
    void testFor429_WithoutRetryAfter() {
        RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        
        // Should fall back to exponential backoff
        Duration delay = policy.for429(1, null);
        assertEquals(Duration.ofSeconds(2), delay);
        
        delay = policy.for429(2, null);
        assertEquals(Duration.ofSeconds(4), delay);
    }

    @Test
    void testFor429_ZeroRetryAfter() {
        RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        
        // Zero retry_after should fall back to exponential backoff
        Duration delay = policy.for429(1, 0);
        assertEquals(Duration.ofSeconds(2), delay);
    }

    @Test
    void testFor429_InvalidParameters() {
        RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        
        assertThrows(IllegalArgumentException.class, () -> {
            policy.for429(-1, 5);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            policy.for429(1, -1);
        });
    }

    @Test
    void testShouldRetry() {
        RetryPolicy policy = new RetryPolicy(2, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        
        assertTrue(policy.shouldRetry(0));
        assertTrue(policy.shouldRetry(1));
        assertFalse(policy.shouldRetry(2));
        assertFalse(policy.shouldRetry(3));
    }

    @Test
    void testDefaultPolicy() {
        RetryPolicy policy = RetryPolicy.defaultPolicy();
        
        assertEquals(2, policy.getMaxRetries());
        assertEquals(Duration.ofSeconds(1), policy.getBaseDelay());
        assertEquals(2.0, policy.getMultiplier());
        assertEquals(Duration.ofSeconds(30), policy.getMaxDelay());
    }

    @Test
    void testConservativePolicy() {
        RetryPolicy policy = RetryPolicy.conservativePolicy();
        
        assertEquals(5, policy.getMaxRetries());
        assertEquals(Duration.ofSeconds(2), policy.getBaseDelay());
        assertEquals(1.5, policy.getMultiplier());
        assertEquals(Duration.ofSeconds(60), policy.getMaxDelay());
    }

    @Test
    void testAggressivePolicy() {
        RetryPolicy policy = RetryPolicy.aggressivePolicy();
        
        assertEquals(1, policy.getMaxRetries());
        assertEquals(Duration.ofMillis(500), policy.getBaseDelay());
        assertEquals(2.0, policy.getMultiplier());
        assertEquals(Duration.ofSeconds(5), policy.getMaxDelay());
    }

    @Test
    void testEqualsAndHashCode() {
        RetryPolicy policy1 = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        RetryPolicy policy2 = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        RetryPolicy policy3 = new RetryPolicy(2, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        
        assertEquals(policy1, policy2);
        assertNotEquals(policy1, policy3);
        assertEquals(policy1.hashCode(), policy2.hashCode());
        assertNotEquals(policy1.hashCode(), policy3.hashCode());
    }

    @Test
    void testToString() {
        RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        String str = policy.toString();
        
        assertTrue(str.contains("RetryPolicy"));
        assertTrue(str.contains("maxRetries=3"));
        assertTrue(str.contains("baseDelay=PT1S"));
        assertTrue(str.contains("multiplier=2.0"));
        assertTrue(str.contains("maxDelay=PT10S"));
    }
}
