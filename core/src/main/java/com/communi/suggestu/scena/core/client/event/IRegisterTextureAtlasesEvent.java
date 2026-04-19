package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import net.minecraft.client.resources.model.sprite.AtlasManager;

import java.util.function.Consumer;

/**
 * Event fired to register new texture atlases.
 */
@FunctionalInterface
public interface IRegisterTextureAtlasesEvent extends IEvent
{

    /**
     * Used to register a new atlas using its config to the atlas manager on construction.
     *
     * @param config The atlas configuration to consumer, invoking this with a config causes it to be loaded.
     */
    void handle(Consumer<AtlasManager.AtlasConfig> config);
}
