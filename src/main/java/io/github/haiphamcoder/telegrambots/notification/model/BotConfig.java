package io.github.haiphamcoder.telegrambots.notification.model;

import java.net.Proxy;
import java.util.Objects;

/**
 * Configuration for a Telegram bot including authentication, chat settings, and proxy configuration.
 */
public class BotConfig {
    private String name;
    private String token;
    private String chatId;
    private Proxy.Type proxyType;
    private String proxyHost;
    private int proxyPort;
    private String proxyUsername;
    private String proxyPassword;
    private int connectTimeoutMs = 5000;
    private int responseTimeoutMs = 10000;

    /**
     * Default constructor.
     */
    public BotConfig() {
    }

    /**
     * Gets the bot name identifier.
     *
     * @return the bot name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the bot name identifier.
     *
     * @param name the bot name
     * @return this BotConfig instance for method chaining
     */
    public BotConfig setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets the bot token.
     *
     * @return the bot token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the bot token.
     *
     * @param token the bot token
     * @return this BotConfig instance for method chaining
     */
    public BotConfig setToken(String token) {
        this.token = token;
        return this;
    }

    /**
     * Gets the chat ID where messages will be sent.
     *
     * @return the chat ID
     */
    public String getChatId() {
        return chatId;
    }

    /**
     * Sets the chat ID where messages will be sent.
     *
     * @param chatId the chat ID
     * @return this BotConfig instance for method chaining
     */
    public BotConfig setChatId(String chatId) {
        this.chatId = chatId;
        return this;
    }

    /**
     * Gets the proxy type.
     *
     * @return the proxy type
     */
    public Proxy.Type getProxyType() {
        return proxyType;
    }

    /**
     * Sets the proxy type.
     *
     * @param proxyType the proxy type
     * @return this BotConfig instance for method chaining
     */
    public BotConfig setProxyType(Proxy.Type proxyType) {
        this.proxyType = proxyType;
        return this;
    }

    /**
     * Gets the proxy host.
     *
     * @return the proxy host
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * Sets the proxy host.
     *
     * @param proxyHost the proxy host
     * @return this BotConfig instance for method chaining
     */
    public BotConfig setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        return this;
    }

    /**
     * Gets the proxy port.
     *
     * @return the proxy port
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Sets the proxy port.
     *
     * @param proxyPort the proxy port
     * @return this BotConfig instance for method chaining
     */
    public BotConfig setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }

    /**
     * Gets the proxy username.
     *
     * @return the proxy username
     */
    public String getProxyUsername() {
        return proxyUsername;
    }

    /**
     * Sets the proxy username.
     *
     * @param proxyUsername the proxy username
     * @return this BotConfig instance for method chaining
     */
    public BotConfig setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
        return this;
    }

    /**
     * Gets the proxy password.
     *
     * @return the proxy password
     */
    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * Sets the proxy password.
     *
     * @param proxyPassword the proxy password
     * @return this BotConfig instance for method chaining
     */
    public BotConfig setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
        return this;
    }

    /**
     * Gets the connection timeout in milliseconds.
     *
     * @return the connection timeout in milliseconds
     */
    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    /**
     * Sets the connection timeout in milliseconds.
     *
     * @param connectTimeoutMs the connection timeout in milliseconds
     * @return this BotConfig instance for method chaining
     */
    public BotConfig setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
        return this;
    }

    /**
     * Gets the response timeout in milliseconds.
     *
     * @return the response timeout in milliseconds
     */
    public int getResponseTimeoutMs() {
        return responseTimeoutMs;
    }

    /**
     * Sets the response timeout in milliseconds.
     *
     * @param responseTimeoutMs the response timeout in milliseconds
     * @return this BotConfig instance for method chaining
     */
    public BotConfig setResponseTimeoutMs(int responseTimeoutMs) {
        this.responseTimeoutMs = responseTimeoutMs;
        return this;
    }

    /**
     * Checks if this bot configuration has proxy settings.
     *
     * @return true if proxy is configured, false otherwise
     */
    public boolean hasProxy() {
        return proxyType != null && proxyHost != null && !proxyHost.trim().isEmpty() && proxyPort > 0;
    }

    /**
     * Checks if this bot configuration has proxy authentication.
     *
     * @return true if proxy authentication is configured, false otherwise
     */
    public boolean hasProxyAuth() {
        return hasProxy() && proxyUsername != null && !proxyUsername.trim().isEmpty() && 
               proxyPassword != null && !proxyPassword.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotConfig botConfig = (BotConfig) o;
        return proxyPort == botConfig.proxyPort &&
                connectTimeoutMs == botConfig.connectTimeoutMs &&
                responseTimeoutMs == botConfig.responseTimeoutMs &&
                Objects.equals(name, botConfig.name) &&
                Objects.equals(token, botConfig.token) &&
                Objects.equals(chatId, botConfig.chatId) &&
                proxyType == botConfig.proxyType &&
                Objects.equals(proxyHost, botConfig.proxyHost) &&
                Objects.equals(proxyUsername, botConfig.proxyUsername) &&
                Objects.equals(proxyPassword, botConfig.proxyPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, token, chatId, proxyType, proxyHost, proxyPort, 
                           proxyUsername, proxyPassword, connectTimeoutMs, responseTimeoutMs);
    }

    @Override
    public String toString() {
        return "BotConfig{" +
                "name='" + name + '\'' +
                ", token='***'" +
                ", chatId='" + chatId + '\'' +
                ", proxyType=" + proxyType +
                ", proxyHost='" + proxyHost + '\'' +
                ", proxyPort=" + proxyPort +
                ", proxyUsername='" + proxyUsername + '\'' +
                ", proxyPassword='***'" +
                ", connectTimeoutMs=" + connectTimeoutMs +
                ", responseTimeoutMs=" + responseTimeoutMs +
                '}';
    }
}
