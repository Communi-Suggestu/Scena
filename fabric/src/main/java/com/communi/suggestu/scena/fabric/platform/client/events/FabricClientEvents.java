package com.communi.suggestu.scena.fabric.platform.client.events;

import com.communi.suggestu.scena.core.client.event.*;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.core.event.IGatherTooltipEvent;
import com.communi.suggestu.scena.fabric.platform.client.model.unbaked.UnbakedCustomModelWrapper;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.FabricModelManager;
import com.communi.suggestu.scena.fabric.platform.event.FabricEventEntryPoint;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public final class FabricClientEvents implements IClientEvents {

    //The HUD hooks and event have to be before the INSTANCE as the creation of the instance requires them to be not null!
    private static final Event<IHudRenderEvent> HUD_RENDER = EventFactory.createArrayBacked(
            IHudRenderEvent.class,
            handlers -> poseStack -> {
                for (final IHudRenderEvent eventHandler : handlers) {
                    eventHandler.handle(poseStack);
                }
            }
    );

    private static final HudElement HUD_EVENT_HOOK = (graphics, _) -> HUD_RENDER.invoker().handle(graphics);
    private static final FabricClientEvents INSTANCE = new FabricClientEvents();

    public static FabricClientEvents getInstance() {
        return INSTANCE;
    }

    public static final Event<IScrollEvent> SCROLL = EventFactory.createArrayBacked(IScrollEvent.class, callbacks -> (final double scrollDelta) -> {
        boolean handled = false;
        for (IScrollEvent callback : callbacks) {
            if (callback.handle(scrollDelta)) {
                handled = true;
            }

            if (handled) {
                return true;
            }
        }

        return false;
    });


    public static final Event<IResourceRegistrationEvent> RESOURCE_REGISTRATION = EventFactory.createArrayBacked(IResourceRegistrationEvent.class, callbacks -> () -> {
        for (IResourceRegistrationEvent callback : callbacks) {
            callback.handle();
        }
    });

    public static final Event<IGatherTooltipComponentsEvent> GATHER_TOOLTIP_COMPONENTS = EventFactory.createArrayBacked(IGatherTooltipComponentsEvent.class, callbacks ->
            (itemStack, screenWidth, screenHeight, maxWidth, tooltipElements) -> {
                for (IGatherTooltipComponentsEvent callback : callbacks) {
                    if (!callback.gather(itemStack, screenWidth, screenHeight, maxWidth, tooltipElements)) {
                        return false;
                    }
                }

                return true;
            });

    public static Event<IRegisterTextureAtlasesEvent> REGISTER_TEXTURE_ATLASES_EVENT = EventFactory.createArrayBacked(IRegisterTextureAtlasesEvent.class,
            events -> config -> {
                for (final IRegisterTextureAtlasesEvent iResourceRegistrationEvent : events) {
                    iResourceRegistrationEvent.handle(config);
                }
            });

    public static Event<IRegisterPIPRenderersEvent> REGISTER_PIPS_EVENT = EventFactory.createArrayBacked(IRegisterPIPRenderersEvent.class,
            events -> config -> {
                for (final IRegisterPIPRenderersEvent iResourceRegistrationEvent : events) {
                    iResourceRegistrationEvent.handle(config);
                }
            });

    private FabricClientEvents() {
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(
                        "scena", "hud_event_hook"
                ),
                HUD_EVENT_HOOK
        );
    }

    @Override
    public IEventEntryPoint<IClientTickStartedEvent> getClientTickStartedEvent() {
        return FabricEventEntryPoint.create(ClientTickEvents.START_CLIENT_TICK, handler -> client -> handler.handle());
    }

    @Override
    public IEventEntryPoint<IDrawHighlightEvent> getDrawHighlightEvent() {
        return FabricEventEntryPoint.create(
                LevelRenderEvents.BEFORE_BLOCK_OUTLINE,
                (scena) -> (context, blockOutlineRenderState) -> {
                    final AtomicBoolean cancel = new AtomicBoolean(false);
                    scena.handle(renderer -> {
                        cancel.set(renderer.render(
                                blockOutlineRenderState,
                                context.submitNodeCollector(),
                                context.poseStack(),
                                context.levelState()
                        ));
                    });
                    return cancel.get();
                }
        );
    }

    @Override
    public IEventEntryPoint<IHudRenderEvent> getHUDRenderEvent() {


        return FabricEventEntryPoint.create(HUD_RENDER);
    }

    @Override
    public IEventEntryPoint<IScrollEvent> getScrollEvent() {
        return FabricEventEntryPoint.create(SCROLL);
    }

    @Override
    public IEventEntryPoint<IExtractLevelRenderStateEvent> getExtractLevelRenderStateEvent() {
        return FabricEventEntryPoint.create(LevelExtractionEvents.END_EXTRACTION, scena -> context -> scena.handle(
                context.levelState(),
                context.level(),
                context.camera(),
                context.deltaTracker(),
                new IExtractLevelRenderStateEvent.IRenderStateStore() {
                    @Override
                    public <T> void write(Identifier state, T value) {
                        final FabricRenderState fabricRenderState = context.levelState();
                        fabricRenderState.setData(RenderStateDataKey.create(state::toString), value);
                    }
                }
        ));
    }

    @Override
    public IEventEntryPoint<ISubmitCustomGeometryEvent> getSubmitCustomGeometryEvent() {
        return FabricEventEntryPoint.create(LevelRenderEvents.COLLECT_SUBMITS, scena -> context -> scena.handle(
                context.submitNodeCollector(),
                context.poseStack(),
                context.levelState(),
                new ISubmitCustomGeometryEvent.IRenderStateStore() {
                    @Override
                    public <T> T read(Identifier identifier) {
                        final FabricRenderState fabricRenderState = context.levelState();
                        return fabricRenderState.getData(RenderStateDataKey.create(identifier::toString));
                    }
                }
        ));
    }

    @Override
    public IEventEntryPoint<IResourceRegistrationEvent> getResourceRegistrationEvent() {
        return FabricEventEntryPoint.create(RESOURCE_REGISTRATION, Function.identity());
    }

    @Override
    public IEventEntryPoint<IGatherTooltipEvent> getGatherTooltipEvent() {
        return FabricEventEntryPoint.create(ItemTooltipCallback.EVENT, handler -> handler::handle);
    }

    @Override
    public IEventEntryPoint<IGatherTooltipComponentsEvent> getGatherTooltipComponentsEvent() {
        return FabricEventEntryPoint.create(GATHER_TOOLTIP_COMPONENTS, Function.identity());
    }

    @Override
    public IEventEntryPoint<IRegisterTextureAtlasesEvent> getRegisterTextureAtlasesEvent() {
        return FabricEventEntryPoint.create(REGISTER_TEXTURE_ATLASES_EVENT, Function.identity());
    }

    @Override
    public IEventEntryPoint<IRegisterPIPRenderersEvent> getRegisterPIPRenderersEvent() {
        return FabricEventEntryPoint.create(REGISTER_PIPS_EVENT, Function.identity());
    }

    @Override
    public IEventEntryPoint<IRegisterBlockStateModelEvent> getRegisterBlockStateModelEvent() {
        return handler ->
                handler.handle(new IRegisterBlockStateModelEvent.Registrar() {
                    @Override
                    public <T extends BlockStateModel.Unbaked> void registerModel(final Identifier location, final MapCodec<T> codec) {
                        final UnbakedCustomModelWrapper<T> wrapper = new UnbakedCustomModelWrapper<T>(codec);

                        FabricModelManager.getInstance().registerUnbakedModelCodecWrapping(
                                codec,
                                wrapper.codec()
                        );

                        CustomUnbakedBlockStateModel.register(
                                location,
                                wrapper.codec()
                        );
                    }
                });
    }

    @Override
    public IEventEntryPoint<IRegisterItemModelEvent> getRegisterItemModelEvent() {
        return handler -> handler.handle(ItemModels.ID_MAPPER::put);
    }

    @Override
    public IEventEntryPoint<IRegisterClientReloadListenersEvent> getRegisterClientResourceReloadListenersEvent() {
        return handler -> handler.handle((key, listener) -> ResourceLoader.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(key, listener));
    }
}
