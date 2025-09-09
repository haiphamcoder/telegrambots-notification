package io.github.haiphamcoder.telegrambots.notification.provider;

import io.github.haiphamcoder.telegrambots.notification.model.BotConfig;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Static implementation of BotConfigProvider that stores bot configurations in memory.
 * This provider is thread-safe and suitable for applications with a fixed set of bots.
 */
public class StaticBotConfigProvider implements BotConfigProvider {
    
    private final Map<String, BotConfig> configs;

    /**
     * Creates a new StaticBotConfigProvider with an empty configuration map.
     */
    public StaticBotConfigProvider() {
        this.configs = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new StaticBotConfigProvider with the specified initial configurations.
     *
     * @param initialConfigs the initial bot configurations
     * @throws IllegalArgumentException if initialConfigs is null
     */
    public StaticBotConfigProvider(Map<String, BotConfig> initialConfigs) {
        if (initialConfigs == null) {
            throw new IllegalArgumentException("Initial configs cannot be null");
        }
        this.configs = new ConcurrentHashMap<>(initialConfigs);
    }

    @Override
    public BotConfig get(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot name cannot be null or empty");
        }
        return configs.get(name.trim());
    }

    @Override
    public Map<String, BotConfig> all() {
        return Collections.unmodifiableMap(configs);
    }

    /**
     * Adds or updates a bot configuration.
     *
     * @param name the bot name identifier
     * @param config the bot configuration
     * @return the previous configuration for this name, or null if none existed
     * @throws IllegalArgumentException if name is null/empty or config is null
     */
    public BotConfig put(String name, BotConfig config) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot name cannot be null or empty");
        }
        if (config == null) {
            throw new IllegalArgumentException("Bot config cannot be null");
        }
        return configs.put(name.trim(), config);
    }

    /**
     * Removes a bot configuration.
     *
     * @param name the bot name identifier
     * @return the removed configuration, or null if none existed
     * @throws IllegalArgumentException if name is null or empty
     */
    public BotConfig remove(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot name cannot be null or empty");
        }
        return configs.remove(name.trim());
    }

    /**
     * Clears all bot configurations.
     */
    public void clear() {
        configs.clear();
    }

    /**
     * Adds multiple bot configurations at once.
     *
     * @param configs the bot configurations to add
     * @throws IllegalArgumentException if configs is null
     */
    public void putAll(Map<String, BotConfig> configs) {
        if (configs == null) {
            throw new IllegalArgumentException("Configs cannot be null");
        }
        this.configs.putAll(configs);
    }

    /**
     * Creates a builder for constructing StaticBotConfigProvider instances.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticBotConfigProvider that = (StaticBotConfigProvider) o;
        return Objects.equals(configs, that.configs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configs);
    }

    @Override
    public String toString() {
        return "StaticBotConfigProvider{" +
                "configs=" + configs +
                '}';
    }

    /**
     * Builder class for constructing StaticBotConfigProvider instances.
     */
    public static class Builder {
        private final Map<String, BotConfig> configs = new ConcurrentHashMap<>();

        /**
         * Adds a bot configuration to the builder.
         *
         * @param name the bot name identifier
         * @param config the bot configuration
         * @return this builder instance
         * @throws IllegalArgumentException if name is null/empty or config is null
         */
        public Builder addBot(String name, BotConfig config) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Bot name cannot be null or empty");
            }
            if (config == null) {
                throw new IllegalArgumentException("Bot config cannot be null");
            }
            configs.put(name.trim(), config);
            return this;
        }

        /**
         * Adds multiple bot configurations to the builder.
         *
         * @param configs the bot configurations to add
         * @return this builder instance
         * @throws IllegalArgumentException if configs is null
         */
        public Builder addBots(Map<String, BotConfig> configs) {
            if (configs == null) {
                throw new IllegalArgumentException("Configs cannot be null");
            }
            this.configs.putAll(configs);
            return this;
        }

        /**
         * Builds the StaticBotConfigProvider instance.
         *
         * @return the built StaticBotConfigProvider
         */
        public StaticBotConfigProvider build() {
            return new StaticBotConfigProvider(configs);
        }
    }
}
