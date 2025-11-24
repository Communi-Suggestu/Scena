package com.communi.suggestu.scena.forge.platform.client.event;

import com.communi.suggestu.scena.core.client.event.*;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.core.event.IGatherTooltipEvent;
import com.communi.suggestu.scena.core.event.Settable;
import com.communi.suggestu.scena.forge.platform.client.model.ForgeModelManager;
import com.communi.suggestu.scena.forge.platform.client.model.unbaked.UnbakedCustomModelWrapper;
import com.communi.suggestu.scena.forge.platform.event.EventBusEventEntryPoint;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ForgeClientEvents implements IClientEvents {
    private static final ForgeClientEvents INSTANCE = new ForgeClientEvents();

    public static ForgeClientEvents getInstance() {
        return INSTANCE;
    }

    private ForgeClientEvents() {
    }

    @Override
    public IEventEntryPoint<IClientTickStartedEvent> getClientTickStartedEvent() {
        return EventBusEventEntryPoint.forge(ClientTickEvent.Pre.class, (event, handler) -> {
            handler.handle();
        });
    }

    @Override
    public IEventEntryPoint<IDrawHighlightEvent> getDrawHighlightEvent() {
        return EventBusEventEntryPoint.forge(ExtractBlockOutlineRenderStateEvent.class, (event, handler) -> {
            if (handler.handle()) {
                event.setCanceled(true);
            }
        }, EventPriority.HIGHEST);
    }

    @Override
    public IEventEntryPoint<IHudRenderEvent> getHUDRenderEvent() {
        return EventBusEventEntryPoint.forge(RenderGuiEvent.Post.class, (event, handler) -> {
            handler.handle(event.getGuiGraphics());
        });
    }

    @Override
    public IEventEntryPoint<IScrollEvent> getScrollEvent() {
        return EventBusEventEntryPoint.forge(InputEvent.MouseScrollingEvent.class, (event, handler) -> {
            event.setCanceled(handler.handle(event.getScrollDeltaY()));
        });
    }

    @Override
    public IEventEntryPoint<IPostRenderWorldEvent> getPostRenderWorldEvent() {
        return EventBusEventEntryPoint.forge(RenderLevelStageEvent.AfterTranslucentBlocks.class, (event, handler) -> {
            handler.handle(
                event.getLevelRenderer(),
                event.getPoseStack(),
                Minecraft.getInstance().renderBuffers().bufferSource(),
                event.getLevelRenderState(),
                Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false)
            );
        });
    }

    @Override
    public IEventEntryPoint<IResourceRegistrationEvent> getResourceRegistrationEvent() {
        return EventBusEventEntryPoint.mod(RegisterColorHandlersEvent.Block.class, (event, handler) -> {
            handler.handle();
        });
    }

    @Override
    public IEventEntryPoint<IGatherTooltipEvent> getGatherTooltipEvent() {
        return EventBusEventEntryPoint.forge(ItemTooltipEvent.class, (event, handler) -> handler.handle(
                event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip()
        ));
    }

    @Override
    public IEventEntryPoint<IGatherTooltipComponentsEvent> getGatherTooltipComponentsEvent() {
        return EventBusEventEntryPoint.forge(RenderTooltipEvent.GatherComponents.class, (event, handler) -> {
            final Settable<Integer> maxWidth = new Settable<>(event::getMaxWidth, event::setMaxWidth);
            final List<TooltipComponent> components = new ArrayList<>();
            if (!handler.gather(event.getItemStack(), event.getScreenWidth(), event.getScreenHeight(), maxWidth, components)) {
                event.setCanceled(true);
            }

            event.getTooltipElements().addAll(
                    components.stream().map(Either::<FormattedText, TooltipComponent>right).toList()
            );
        });
    }

    @Override
    public IEventEntryPoint<IRegisterTextureAtlasesEvent> getRegisterTextureAtlasesEvent()
    {
        return EventBusEventEntryPoint.mod(RegisterTextureAtlasesEvent.class,
            (registerTextureAtlasesEvent, event) -> event.handle(registerTextureAtlasesEvent::register)
        );
    }

    @Override
    public IEventEntryPoint<IRegisterPIPRenderersEvent> getRegisterPIPRenderersEvent()
    {
        return EventBusEventEntryPoint.mod(RegisterPictureInPictureRenderersEvent.class,
            (registerPictureInPictureRenderersEvent, iRegisterPIPRenderersEvent) -> iRegisterPIPRenderersEvent.handle(registerPictureInPictureRenderersEvent::register)
        );
    }

    @Override
    public IEventEntryPoint<IRegisterBlockStateModelEvent> getRegisterBlockStateModelEvent()
    {
        return EventBusEventEntryPoint.mod(RegisterBlockStateModels.class, (forge, scena) -> scena.handle(new IRegisterBlockStateModelEvent.Registrar() {
            @Override
            public <T extends BlockStateModel.Unbaked> void registerModel(final ResourceLocation location, final MapCodec<T> codec)
            {
                final UnbakedCustomModelWrapper<T> wrapper = new UnbakedCustomModelWrapper<>(codec);

                ForgeModelManager.getInstance().registerUnbakedModelCodecWrapping(
                    location,
                    codec,
                    wrapper.codec()
                );

                forge.registerModel(
                    location,
                    wrapper.codec()
                );
            }
        }));
    }

    @Override
    public IEventEntryPoint<IRegisterItemModelEvent> getRegisterItemModelEvent()
    {
        return EventBusEventEntryPoint.mod(
            RegisterItemModelsEvent.class,
            (forge, scena) -> {
                scena.handle(forge::register);
            }
        );
    }

    @Override
    public IEventEntryPoint<IRegisterClientReloadListenersEvent> getRegisterClientResourceReloadListenersEvent()
    {
        return EventBusEventEntryPoint.mod(
            AddClientReloadListenersEvent.class,
            (forge, scena) -> {
                scena.handle(forge::addListener);
            }
        );
    }
}
