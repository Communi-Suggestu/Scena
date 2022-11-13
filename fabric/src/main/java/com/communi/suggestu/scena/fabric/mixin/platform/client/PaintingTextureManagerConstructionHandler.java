package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.events.FabricClientEvents;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.PaintingTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PaintingTextureManager.class)
public abstract class PaintingTextureManagerConstructionHandler
{

    @Inject(
      method = "<init>",
      at = @At("RETURN")
    )
    public void onConstruction(final TextureManager textureManager, final CallbackInfo ci) {
        FabricClientEvents.RESOURCE_REGISTRATION.invoker().handle();
    }
}
