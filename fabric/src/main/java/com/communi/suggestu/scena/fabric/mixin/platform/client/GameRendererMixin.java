package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.core.client.event.IRegisterPIPRenderersEvent;
import com.communi.suggestu.scena.fabric.platform.client.events.FabricClientEvents;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

//Needs to be less than 1000 as that is the default priority used by fabrics own mixin.
@Mixin(value = GameRenderer.class, priority = 100)
public abstract class GameRendererMixin
{
    @Inject(method = "<init>", at = @At(value = "RETURN"))
	private void guiRendererReady(
        final Minecraft minecraft,
        final ItemInHandRenderer itemInHandRenderer,
        final RenderBuffers renderBuffers,
        final BlockRenderDispatcher blockRenderDispatcher,
        final CallbackInfo ci) {
        FabricClientEvents.REGISTER_PIPS_EVENT.invoker()
            .handle(new IRegisterPIPRenderersEvent.Registrar() {
                @Override
                public <T extends PictureInPictureRenderState> void register(
                    final Class<T> stateClass,
                    final Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<T>> factory)
                {
                    SpecialGuiElementRegistry.register(
                        ctx -> factory.apply(ctx.vertexConsumers())
                    );
                }
            });
	}
}
