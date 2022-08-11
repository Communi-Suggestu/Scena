package com.communi.suggestu.scena.core.config;

/**
 * Indicates the configuration type used by the configuration that is about to be build.
 */
public enum ConfigurationType
{
    /**
     * Indicates that the configuration is a client configuration.
     * The server will not load this configuration, nor is it synced to the client.
     */
    CLIENT_ONLY,

    /**
     * Indicates that the configuration is common to both distributions.
     * Both the server and the client will load the configuration from disk, however the values will not be synced from the server to the client.
     */
    NOT_SYNCED,

    /**
     * Indicates that the configuration is common to both distributions, and has influence on logical decisions.
     * Both the server and the client will load the configuration from disk, and the server will sync its settings to the client when it joins.
     */
    SYNCED;
}
