package io.github.haiphamcoder.telegrambots.notification.provider;

import io.github.haiphamcoder.telegrambots.notification.model.BotConfig;

import java.util.Map;

/**
 * Interface for providing bot configurations.
 * Implementations can load bot configurations from various sources such as static maps,
 * configuration files, databases, or external services.
 */
public interface BotConfigProvider {
    
    /**
     * Gets the bot configuration for the specified bot name.
     *
     * @param name the bot name identifier
     * @return the bot configuration, or null if not found
     * @throws IllegalArgumentException if name is null or empty
     */
    BotConfig get(String name);
    
    /**
     * Gets all available bot configurations.
     *
     * @return a map of bot names to their configurations
     */
    Map<String, BotConfig> all();
    
    /**
     * Checks if a bot configuration exists for the specified name.
     *
     * @param name the bot name identifier
     * @return true if the bot configuration exists, false otherwise
     * @throws IllegalArgumentException if name is null or empty
     */
    default boolean exists(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot name cannot be null or empty");
        }
        return get(name) != null;
    }
    
    /**
     * Gets the number of available bot configurations.
     *
     * @return the number of bot configurations
     */
    default int size() {
        return all().size();
    }
}
