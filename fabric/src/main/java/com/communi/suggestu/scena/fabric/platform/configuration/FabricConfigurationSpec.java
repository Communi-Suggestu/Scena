package com.communi.suggestu.scena.fabric.platform.configuration;

import com.google.gson.JsonObject;

import java.util.List;

public final class FabricConfigurationSpec
{
    private final FabricConfigurationSource source;
    private final List<FabricConfigurationValue<?>> values;

    public FabricConfigurationSpec(
      final FabricConfigurationSource source,
      final List<FabricConfigurationValue<?>> values
    ) {
        this.source = source;
        this.values = values;
    }

    public void reset() {
        source.reset();
        values.forEach(FabricConfigurationValue::resetCache);
    }

    public FabricConfigurationSource getSource()
    {
        return source;
    }

    public void loadFrom(final JsonObject jsonObject) {
        this.reset();
        this.source.setConfig(jsonObject);
    }

    public void forceGetAll() {
        this.values.forEach(FabricConfigurationValue::get);
    }

    public void writeAll() {
        this.values.forEach(FabricConfigurationValue::write);
    }
}
