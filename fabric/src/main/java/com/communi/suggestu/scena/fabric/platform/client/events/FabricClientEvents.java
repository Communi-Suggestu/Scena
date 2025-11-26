package com.communi.suggestu.scena.fabric.platform.client.events;

import com.communi.suggestu.scena.core.client.event.IClientEvents;
import com.communi.suggestu.scena.core.client.event.IClientTickStartedEvent;
import com.communi.suggestu.scena.core.client.event.IDrawHighlightEvent;
import com.communi.suggestu.scena.core.client.event.IGatherTooltipComponentsEvent;
import com.communi.suggestu.scena.core.client.event.IHudRenderEvent;
import com.communi.suggestu.scena.core.client.event.IPostRenderWorldEvent;
import com.communi.suggestu.scena.core.client.event.IRegisterBlockStateModelEvent;
import com.communi.suggestu.scena.core.client.event.IRegisterClientReloadListenersEvent;
import com.communi.suggestu.scena.core.client.event.IRegisterItemModelEvent;
import com.communi.suggestu.scena.core.client.event.IRegisterPIPRenderersEvent;
import com.communi.suggestu.scena.core.client.event.IRegisterTextureAtlasesEvent;
import com.communi.suggestu.scena.core.client.event.IResourceRegistrationEvent;
import com.communi.suggestu.scena.core.client.event.IScrollEvent;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.core.event.IGatherTooltipEvent;
import com.communi.suggestu.scena.fabric.platform.client.model.unbaked.UnbakedCustomModelWrapper;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.FabricModelManager;
import com.communi.suggestu.scena.fabric.platform.event.FabricEventEntryPoint;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import java.util.function.Function;

public final class FabricClientEvents implements IClientEvents
{
    private static final FabricClientEvents INSTANCE = new FabricClientEvents();

    public static FabricClientEvents getInstance()
    {
        return INSTANCE;
    }

    public static final Event<IScrollEvent> SCROLL = EventFactory.createArrayBacked(IScrollEvent.class, callbacks -> (final double scrollDelta) -> {
        boolean handled = false;
        for (IScrollEvent callback : callbacks)
        {
            if (callback.handle(scrollDelta))
            {
                handled = true;
            }

            if (handled)
            {
                return true;
            }
        }

        return false;
    });

    public static final Event<IResourceRegistrationEvent> RESOURCE_REGISTRATION = EventFactory.createArrayBacked(IResourceRegistrationEvent.class, callbacks -> () -> {
        for (IResourceRegistrationEvent callback : callbacks)
        {
            callback.handle();
        }
    });

    public static final Event<IGatherTooltipComponentsEvent> GATHER_TOOLTIP_COMPONENTS = EventFactory.createArrayBacked(IGatherTooltipComponentsEvent.class, callbacks ->
        (itemStack, screenWidth, screenHeight, maxWidth, tooltipElements) -> {
            for (IGatherTooltipComponentsEvent callback : callbacks)
            {
                if (!callback.gather(itemStack, screenWidth, screenHeight, maxWidth, tooltipElements))
                {
                    return false;
                }
            }

            return true;
        });

    public static Event<IRegisterTextureAtlasesEvent> REGISTER_TEXTURE_ATLASES_EVENT = EventFactory.createArrayBacked(IRegisterTextureAtlasesEvent.class,
        events -> config -> {
            for (final IRegisterTextureAtlasesEvent iResourceRegistrationEvent : events)
            {
                iResourceRegistrationEvent.handle(config);
            }
        });

    public static Event<IRegisterPIPRenderersEvent> REGISTER_PIPS_EVENT = EventFactory.createArrayBacked(IRegisterPIPRenderersEvent.class,
        events -> config -> {
            for (final IRegisterPIPRenderersEvent iResourceRegistrationEvent : events)
            {
                iResourceRegistrationEvent.handle(config);
            }
        });

    private FabricClientEvents()
    {
    }

    @Override
    public IEventEntryPoint<IClientTickStartedEvent> getClientTickStartedEvent()
    {
        return FabricEventEntryPoint.create(ClientTickEvents.START_CLIENT_TICK, handler -> client -> handler.handle());
    }

    @Override
    public IEventEntryPoint<IDrawHighlightEvent> getDrawHighlightEvent()
    {
        return FabricEventEntryPoint.create(
            WorldRenderEvents.AFTER_BLOCK_OUTLINE_EXTRACTION,
            (scena) -> (context, result) -> {
                if (scena.handle())
                {
                    context.worldState().blockOutlineRenderState = null;
                }
            }
        );
    }

    @Override
    public IEventEntryPoint<IHudRenderEvent> getHUDRenderEvent()
    {
        return FabricEventEntryPoint.create(HudRenderCallback.EVENT, handler -> (matrixStack, tickDelta) -> handler.handle(matrixStack));
    }

    @Override
    public IEventEntryPoint<IScrollEvent> getScrollEvent()
    {
        return FabricEventEntryPoint.create(SCROLL, Function.identity());
    }

    @Override
    public IEventEntryPoint<IPostRenderWorldEvent> getPostRenderWorldEvent()
    {
        return FabricEventEntryPoint.create(
            WorldRenderEvents.END_MAIN,
            scena -> new WorldRenderEvents.EndMain()
            {
                @Override
                public void endMain(final WorldRenderContext context)
                {
                    scena.handle(
                        context.worldRenderer(),
                        context.matrices(),
                        Minecraft.getInstance().renderBuffers().bufferSource(),
                        context.worldState(),
                        Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false)
                    );
                }
            }
        );
    }

    @Override
    public IEventEntryPoint<IResourceRegistrationEvent> getResourceRegistrationEvent()
    {
        return FabricEventEntryPoint.create(RESOURCE_REGISTRATION, Function.identity());
    }

    @Override
    public IEventEntryPoint<IGatherTooltipEvent> getGatherTooltipEvent()
    {
        return FabricEventEntryPoint.create(ItemTooltipCallback.EVENT, handler -> handler::handle);
    }

    @Override
    public IEventEntryPoint<IGatherTooltipComponentsEvent> getGatherTooltipComponentsEvent()
    {
        return FabricEventEntryPoint.create(GATHER_TOOLTIP_COMPONENTS, Function.identity());
    }

    @Override
    public IEventEntryPoint<IRegisterTextureAtlasesEvent> getRegisterTextureAtlasesEvent()
    {
        return FabricEventEntryPoint.create(REGISTER_TEXTURE_ATLASES_EVENT, Function.identity());
    }

    @Override
    public IEventEntryPoint<IRegisterPIPRenderersEvent> getRegisterPIPRenderersEvent()
    {
        return FabricEventEntryPoint.create(REGISTER_PIPS_EVENT, Function.identity());
    }

    @Override
    public IEventEntryPoint<IRegisterBlockStateModelEvent> getRegisterBlockStateModelEvent()
    {
        return handler ->
            handler.handle(new IRegisterBlockStateModelEvent.Registrar()
            {
                @Override
                public <T extends BlockStateModel.Unbaked> void registerModel(final Identifier location, final MapCodec<T> codec)
                {
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
    public IEventEntryPoint<IRegisterItemModelEvent> getRegisterItemModelEvent()
    {
        return handler -> handler.handle(ItemModels.ID_MAPPER::put);
    }

    @Override
    public IEventEntryPoint<IRegisterClientReloadListenersEvent> getRegisterClientResourceReloadListenersEvent()
    {
        return handler -> handler.handle((key, listener) -> ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloader(key, listener));
    }
}
