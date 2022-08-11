package com.communi.suggestu.scena.core.config;

import com.communi.suggestu.scena.core.IScenaPlatform;

/**
 * Manages the configuration system on the current platform.
 * Allows for the creation of new configuration files, and the retrieval of their values.
 */
public interface IConfigurationManager
{

    static IConfigurationManager getInstance() {
        return IScenaPlatform.getInstance().getConfigurationManager();
    }

    /**
     * Creates a new configuration builder, which will build a configuration of the given type, and name the configuration with the given name.
     *
     * @param type The type of the configuration.
     * @param name The name of the configuration.
     * @return The builder for the configuration.
     */
    IConfigurationBuilder createBuilder(final ConfigurationType type, final String name);
}
