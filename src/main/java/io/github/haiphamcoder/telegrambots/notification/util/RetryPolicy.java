package io.github.haiphamcoder.telegrambots.notification.util;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuration for retry behavior when handling rate limiting and other transient errors.
 * This class provides exponential backoff and support for Telegram's retry_after parameter.
 */
public final class RetryPolicy {
    
    private final int maxRetries;
    private final Duration baseDelay;
    private final double multiplier;
    private final Duration maxDelay;

    /**
     * Creates a new RetryPolicy with the specified parameters.
     *
     * @param maxRetries the maximum number of retry attempts
     * @param baseDelay the base delay duration for the first retry
     * @param multiplier the exponential backoff multiplier
     * @param maxDelay the maximum delay duration between retries
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public RetryPolicy(int maxRetries, Duration baseDelay, double multiplier, Duration maxDelay) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative");
        }
        if (baseDelay == null || baseDelay.isNegative()) {
            throw new IllegalArgumentException("Base delay must be non-negative");
        }
        if (multiplier <= 0) {
            throw new IllegalArgumentException("Multiplier must be positive");
        }
        if (maxDelay == null || maxDelay.isNegative()) {
            throw new IllegalArgumentException("Max delay must be non-negative");
        }
        
        this.maxRetries = maxRetries;
        this.baseDelay = baseDelay;
        this.multiplier = multiplier;
        this.maxDelay = maxDelay;
    }

    /**
     * Gets the maximum number of retry attempts.
     *
     * @return the maximum number of retries
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * Gets the base delay duration.
     *
     * @return the base delay duration
     */
    public Duration getBaseDelay() {
        return baseDelay;
    }

    /**
     * Gets the exponential backoff multiplier.
     *
     * @return the multiplier
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Gets the maximum delay duration.
     *
     * @return the maximum delay duration
     */
    public Duration getMaxDelay() {
        return maxDelay;
    }

    /**
     * Computes the backoff delay for the specified attempt number using exponential backoff.
     * The delay is calculated as: baseDelay * (multiplier ^ attempt), capped at maxDelay.
     *
     * @param attempt the attempt number (0-based)
     * @return the computed delay duration
     * @throws IllegalArgumentException if attempt is negative
     */
    public Duration computeBackoff(int attempt) {
        if (attempt < 0) {
            throw new IllegalArgumentException("Attempt number cannot be negative");
        }
        
        if (attempt == 0) {
            return baseDelay;
        }
        
        double delayMs = baseDelay.toMillis() * Math.pow(multiplier, attempt);
        Duration computedDelay = Duration.ofMillis((long) delayMs);
        
        // Cap at maxDelay
        return computedDelay.compareTo(maxDelay) > 0 ? maxDelay : computedDelay;
    }

    /**
     * Computes the delay for a 429 (rate limit) response.
     * If retryAfter is provided, it takes precedence over exponential backoff.
     * Otherwise, uses the standard exponential backoff calculation.
     *
     * @param attempt the attempt number (0-based)
     * @param retryAfter the retry_after value from Telegram API (in seconds), or null
     * @return the computed delay duration
     * @throws IllegalArgumentException if attempt is negative or retryAfter is negative
     */
    public Duration for429(int attempt, Integer retryAfter) {
        if (attempt < 0) {
            throw new IllegalArgumentException("Attempt number cannot be negative");
        }
        if (retryAfter != null && retryAfter < 0) {
            throw new IllegalArgumentException("Retry after cannot be negative");
        }
        
        if (retryAfter != null && retryAfter > 0) {
            // Use Telegram's suggested retry time
            Duration telegramDelay = Duration.ofSeconds(retryAfter);
            // But still cap it at our maxDelay
            return telegramDelay.compareTo(maxDelay) > 0 ? maxDelay : telegramDelay;
        }
        
        // Fall back to exponential backoff
        return computeBackoff(attempt);
    }

    /**
     * Checks if retries are allowed for the specified attempt number.
     *
     * @param attempt the attempt number (0-based)
     * @return true if retries are allowed, false otherwise
     */
    public boolean shouldRetry(int attempt) {
        return attempt < maxRetries;
    }

    /**
     * Creates a default retry policy with reasonable defaults:
     * - Max retries: 2
     * - Base delay: 1 second
     * - Multiplier: 2.0
     * - Max delay: 30 seconds
     *
     * @return a default retry policy
     */
    public static RetryPolicy defaultPolicy() {
        return new RetryPolicy(2, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(30));
    }

    /**
     * Creates a conservative retry policy with more retries and longer delays:
     * - Max retries: 5
     * - Base delay: 2 seconds
     * - Multiplier: 1.5
     * - Max delay: 60 seconds
     *
     * @return a conservative retry policy
     */
    public static RetryPolicy conservativePolicy() {
        return new RetryPolicy(5, Duration.ofSeconds(2), 1.5, Duration.ofSeconds(60));
    }

    /**
     * Creates an aggressive retry policy with fewer retries and shorter delays:
     * - Max retries: 1
     * - Base delay: 500 milliseconds
     * - Multiplier: 2.0
     * - Max delay: 5 seconds
     *
     * @return an aggressive retry policy
     */
    public static RetryPolicy aggressivePolicy() {
        return new RetryPolicy(1, Duration.ofMillis(500), 2.0, Duration.ofSeconds(5));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RetryPolicy that = (RetryPolicy) o;
        return maxRetries == that.maxRetries &&
                Double.compare(that.multiplier, multiplier) == 0 &&
                Objects.equals(baseDelay, that.baseDelay) &&
                Objects.equals(maxDelay, that.maxDelay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxRetries, baseDelay, multiplier, maxDelay);
    }

    @Override
    public String toString() {
        return "RetryPolicy{" +
                "maxRetries=" + maxRetries +
                ", baseDelay=" + baseDelay +
                ", multiplier=" + multiplier +
                ", maxDelay=" + maxDelay +
                '}';
    }
}
