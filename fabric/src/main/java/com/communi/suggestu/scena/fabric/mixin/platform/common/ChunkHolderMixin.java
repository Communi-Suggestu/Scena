package com.communi.suggestu.scena.fabric.mixin.platform.common;

import com.communi.suggestu.scena.fabric.platform.level.ILoadingAwareChunkHolder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkHolder.class)
public class ChunkHolderMixin implements ILoadingAwareChunkHolder {

    @Unique
    private LevelChunk currentlyLoading;

    @Override
    @Nullable
    public LevelChunk scena$getCurrentlyLoading() {
        return currentlyLoading;
    }

    @Override
    public void scena$setCurrentlyLoading(@Nullable LevelChunk chunk) {
        this.currentlyLoading = chunk;
    }
}
