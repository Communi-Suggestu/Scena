package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.rendering.FabricRenderingManager;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialModelRenderers.class)
public class SpecialModelRenderersMixin
{

    @Inject(
        method = "bootstrap",
        at = @At("RETURN")
    )
    private static void onBootstrapReturn(final CallbackInfo ci) {
        FabricRenderingManager.ON_SPECIAL_MODEL_BAKE.invoker().accept(
            SpecialModelRenderers.ID_MAPPER::put
        );
    }
}
