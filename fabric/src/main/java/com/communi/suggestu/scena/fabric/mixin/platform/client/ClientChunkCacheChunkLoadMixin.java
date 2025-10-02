package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.event.FabricGameEvents;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.function.Consumer;

@Mixin(ClientChunkCache.class)
public abstract class ClientChunkCacheChunkLoadMixin extends ChunkSource {

    @Inject(
        method = "replaceWithPacketData", at = @At(value = "RETURN", ordinal = 1))
    public void onChunkReplacedWithPacketData(
        final int x,
        final int z,
        final FriendlyByteBuf readBuffer,
        final Map<Heightmap.Types, long[]> heightmaps,
        final Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer,
        final CallbackInfoReturnable<LevelChunk> cir,
        @Local final LevelChunk levelChunk) {
        FabricGameEvents.CHUNK_LOAD.invoker().handle(Minecraft.getInstance().level, levelChunk);
    }
}
