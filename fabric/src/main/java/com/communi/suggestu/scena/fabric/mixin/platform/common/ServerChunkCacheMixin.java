package com.communi.suggestu.scena.fabric.mixin.platform.common;

import com.communi.suggestu.scena.fabric.platform.level.ILoadingAwareChunkHolder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerChunkCache.class, priority = 1100)
public abstract class ServerChunkCacheMixin {

    @Shadow
    @Nullable
    protected abstract ChunkHolder getVisibleChunkIfPresent(long chunkPos);

    @Inject(
            method = "getChunk",
            at = {
                    @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;incrementCounter(Ljava/lang/String;)V",
                    ordinal = 1),
                    @At(
                    value = "INVOKE",
                    target = "/getChunkBlocking/",
                    ordinal = 0)
            },
            allow = 1,
            cancellable = true
    )
    public void shortCircuitChunkLoadingFutureIfInCurrentlyLoadingChunk(int x, int z, ChunkStatus chunkStatus, boolean requireChunk, CallbackInfoReturnable<ChunkAccess> cir) {
        long l = ChunkPos.asLong(x, z);
        ChunkHolder chunkholder = this.getVisibleChunkIfPresent(l);
        if (chunkholder instanceof ILoadingAwareChunkHolder loadingAwareChunkHolder && loadingAwareChunkHolder.scena$getCurrentlyLoading() != null) {
            cir.setReturnValue(loadingAwareChunkHolder.scena$getCurrentlyLoading());
        }
    }
}
