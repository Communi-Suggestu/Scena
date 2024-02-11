package com.communi.suggestu.scena.fabric.mixin.platform.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @Inject(
            method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V",
            at = @At("RETURN")
    )
    private void onUpgradeChunk(ServerLevel serverLevel, ProtoChunk protoChunk, LevelChunk.PostLoadProcessor postLoadProcessor, CallbackInfo ci) {
        final IFabricBlockEntityPositionHolder protoHolder = (IFabricBlockEntityPositionHolder) protoChunk;
        final IFabricBlockEntityPositionHolder holder = (IFabricBlockEntityPositionHolder) this;

        holder.scena$getBlockEntityPositions().clear();
        holder.scena$getBlockEntityPositions().putAll(protoHolder.scena$getBlockEntityPositions());
    }
}
