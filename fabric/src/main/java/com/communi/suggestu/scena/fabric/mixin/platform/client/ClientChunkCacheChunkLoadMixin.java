package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.event.FabricGameEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Consumer;

@Mixin(ClientChunkCache.class)
public abstract class ClientChunkCacheChunkLoadMixin extends ChunkSource {

    @Inject(method = "replaceWithPacketData(IILnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/nbt/CompoundTag;Ljava/util/function/Consumer;)Lnet/minecraft/world/level/chunk/LevelChunk;", at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onChunkReplacedWithPacketData(int x, int z, FriendlyByteBuf buffer, CompoundTag tag, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> cir,int i, LevelChunk levelChunk, ChunkPos chunkPos) {
        FabricGameEvents.CHUNK_LOAD.invoker().handle(Minecraft.getInstance().level, levelChunk);
    }
}
