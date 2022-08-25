package com.communi.suggestu.scena.forge.mixin.platform.client;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin
{

    @Shadow
    private ModelBakery modelBakery;

    @Inject(
        method = "apply",
        at = @At(
            value = "HEAD"
        )
    )
    protected void apply(ModelBakery p_119419_, ResourceManager p_119420_, ProfilerFiller p_119421_, CallbackInfo ci) {
        this.modelBakery = p_119419_;
    }
}
