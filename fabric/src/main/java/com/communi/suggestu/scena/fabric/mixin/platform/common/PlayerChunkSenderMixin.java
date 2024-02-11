package com.communi.suggestu.scena.fabric.mixin.platform.common;

import com.communi.suggestu.scena.fabric.platform.event.FabricGameEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerChunkSender.class)
public class PlayerChunkSenderMixin {

    @Inject(
            method = "sendChunk",
            at = @At("RETURN")
    )
    private static void onSendChunkReturn(
            ServerGamePacketListenerImpl packetListener, ServerLevel level, LevelChunk chunk, CallbackInfo ci
    ) {
        FabricGameEvents.CHUNK_SENT.invoker().handle(packetListener.player, chunk, level);
    }
}
