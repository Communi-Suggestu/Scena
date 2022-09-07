package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.rendering.model.IModelBakeryAccessor;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelManager.class)
public abstract class ModelBakeryFromModelManagerAccessorMixin extends SimplePreparableReloadListener<ModelBakery> implements IModelBakeryAccessor
{

    @Unique
    private ModelBakery currentBakery = null;

    @Inject(
            method = "apply(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(
                    value = "HEAD"
            )
    )
    public void onApply(final ModelBakery object, final ResourceManager resourceManager, final ProfilerFiller profiler, final CallbackInfo ci) {
        this.currentBakery = object;
    }

    @Override
    public ModelBakery getModelBakery()
    {
        if (currentBakery == null) {
            throw new IllegalStateException("ModelBakery is not available yet");
        }

        return currentBakery;
    }
}
