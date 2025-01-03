package com.communi.suggestu.scena.fabric.platform.level;

import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public interface ILoadingAwareChunkHolder {

    /**
     * Get the currently loading chunk.
     * @return The currently loading chunk.
     */
    @Nullable
    LevelChunk scena$getCurrentlyLoading();

    /**
     * Set the currently loading chunk.
     * @param chunk The currently loading chunk.
     */
    void scena$setCurrentlyLoading(@Nullable LevelChunk chunk);
}
