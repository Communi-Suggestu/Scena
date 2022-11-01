package com.communi.suggestu.scena.fabric.mixin.platform.common;

import com.communi.suggestu.scena.fabric.platform.event.FabricGameEvents;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Mixin(ChunkMap.class)
public abstract class ChunkMapChunkLoadMixin extends ChunkStorage {

    public ChunkMapChunkLoadMixin(Path path, DataFixer dataFixer, boolean bl) {
        super(path, dataFixer, bl);
    }

    @Shadow @Final ServerLevel level;

    @Inject(
            method = "method_17227",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void onProtoChunkToFullChunkComplete(ChunkHolder chunkHolder, ChunkAccess chunkAccess, CallbackInfoReturnable<ChunkAccess> cir, ChunkPos chunkPos, ProtoChunk protoChunk, LevelChunk levelChunk) {
        FabricGameEvents.CHUNK_LOAD.invoker().handle(this.level, levelChunk);
    }
}
