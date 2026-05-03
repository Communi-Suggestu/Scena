package com.communi.suggestu.scena.fabric.mixin.platform.common;

import com.communi.suggestu.scena.fabric.platform.event.FabricGameEvents;
import com.communi.suggestu.scena.fabric.platform.level.ILoadingAwareChunkHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkStatusTasks.class)
public class ChunkStatusTasksMixin {

    @Unique
    private static final String FULL_LAMBDA = "method_60553";

    @Inject(
            method = "lambda$full$0",
            at = @At("RETURN")
    )
    private static void onProtoChunkToFullChunkComplete(ChunkAccess chunk, WorldGenContext context, GenerationChunkHolder holder, CallbackInfoReturnable<ChunkAccess> cir, @Local(name = "level") ServerLevel level, @Local(name = "levelChunk") LevelChunk levelChunk) {
        FabricGameEvents.CHUNK_LOAD.invoker().handle(level, levelChunk);
    }

    @WrapOperation(
            method = "lambda$full$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;runPostLoad()V"
            )
    )
    private static void wrapPostLoadToHandleLevelChunk(LevelChunk instance, Operation<Void> original, @Local(argsOnly = true) GenerationChunkHolder chunkHolder) {
        try {
            if (chunkHolder instanceof ILoadingAwareChunkHolder loadingAwareChunkHolder)
                loadingAwareChunkHolder.scena$setCurrentlyLoading(instance);

            original.call(instance);
        } finally {
            if (chunkHolder instanceof ILoadingAwareChunkHolder loadingAwareChunkHolder)
                loadingAwareChunkHolder.scena$setCurrentlyLoading(null);
        }
    }

    @WrapOperation(
            method = "lambda$full$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;registerAllBlockEntitiesAfterLevelLoad()V"
            )
    )
    private static void wrapRegisterAllBlockEntitiesAfterLevelLoadToHandleLevelChunk(LevelChunk instance, Operation<Void> original, @Local(argsOnly = true) GenerationChunkHolder chunkHolder) {
        try {
            if (chunkHolder instanceof ILoadingAwareChunkHolder loadingAwareChunkHolder)
                loadingAwareChunkHolder.scena$setCurrentlyLoading(instance);

            original.call(instance);
        } finally {
            if (chunkHolder instanceof ILoadingAwareChunkHolder loadingAwareChunkHolder)
                loadingAwareChunkHolder.scena$setCurrentlyLoading(null);
        }
    }
}
