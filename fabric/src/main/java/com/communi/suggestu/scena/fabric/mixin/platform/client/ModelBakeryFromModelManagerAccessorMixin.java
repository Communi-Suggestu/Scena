package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.rendering.model.IModelBakeryAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public abstract class ModelBakeryFromModelManagerAccessorMixin implements IModelBakeryAccessor
{

    @Shadow private Object2IntMap<BlockState> modelGroups;
    @Unique
    private ModelBakery currentBakery = null;

    @Inject(
            method = "loadModels",
            at = @At(
                    value = "HEAD"
            )
    )
    private static void onApply(
        final SpriteLoader.Preparations preparations,
        final SpriteLoader.Preparations preparations2,
        final ModelBakery modelBakery,
        final Object2IntMap<BlockState> object2IntMap,
        final EntityModelSet entityModelSet,
        final SpecialBlockModelRenderer specialBlockModelRenderer,
        final Executor executor,
        final CallbackInfoReturnable<CompletableFuture<ModelManager.ReloadState>> cir) {
        final IModelBakeryAccessor accessor = (IModelBakeryAccessor) Minecraft.getInstance().getModelManager();

        accessor.scena$setModelBakeryInternal(modelBakery);
    }
    @Override
    public void scena$setModelBakeryInternal(ModelBakery modelBakery)
    {
        this.currentBakery = modelBakery;
    }

    @Override
    public ModelBakery scena$getModelBakeryInternal()
    {
        if (currentBakery == null) {
            throw new IllegalStateException("ModelBakery is not available yet");
        }

        return currentBakery;
    }
}
