package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.core.client.event.IRegisterPIPRenderersEvent;
import com.communi.suggestu.scena.fabric.platform.client.events.FabricClientEvents;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.resources.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

//Needs to be less than 1000 as that is the default priority used by fabrics own mixin.
@Mixin(value = GameRenderer.class, priority = 100)
public abstract class GameRendererMixin
{
    @Inject(method = "<init>", at = @At(value = "RETURN"))
	private void guiRendererReady(
            Minecraft minecraft,
            ItemInHandRenderer itemInHandRenderer,
            ModelManager modelManager,
            CallbackInfo ci) {
        FabricClientEvents.REGISTER_PIPS_EVENT.invoker()
            .handle(new IRegisterPIPRenderersEvent.Registrar() {
                @Override
                public <T extends PictureInPictureRenderState> void register(
                        final Class<T> stateClass,
                        final Supplier<PictureInPictureRenderer<T>> factory)
                {
                    PictureInPictureRendererRegistry.register(
                            _ -> factory.get()
                    );
                }
            });
	}
}
