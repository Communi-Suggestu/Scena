package com.communi.suggestu.scena.fabric.platform.configuration;

import com.google.gson.JsonObject;

public final class FabricConfigurationSource
{

    private final String name;
    private final JsonObject   localSource;
    private JsonObject activeSource;

    public FabricConfigurationSource(final String name, final JsonObject localSource) {
        this.name = name;
        this.localSource = localSource;
        this.activeSource = localSource;
    }

    public JsonObject getConfig()
    {
        return activeSource;
    }

    public String getName()
    {
        return name;
    }

    public void setConfig(final JsonObject configSource)
    {
        this.activeSource = configSource;
    }

    public void reset() {
        this.activeSource = this.localSource;
    }
}
