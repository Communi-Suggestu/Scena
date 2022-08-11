package com.communi.suggestu.scena.forge.mixin.platform.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.communi.suggestu.scena.forge.platform.client.rendering.ForgeRenderingManager;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public abstract class BlockEntityWithoutLevelRendererMixin
{
    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    private void onRender(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, CallbackInfo info) {
        final Optional<BlockEntityWithoutLevelRenderer> renderer =
          ForgeRenderingManager.getInstance().getRenderer(stack.getItem());

        renderer.ifPresent(blockEntityWithoutLevelRenderer -> {
            blockEntityWithoutLevelRenderer.renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
            info.cancel();
        });
    }

}
