package com.communi.suggestu.scena.forge.platform.event;

import com.communi.suggestu.scena.core.event.IChunkLoadEvent;
import com.communi.suggestu.scena.core.event.ICommonConfigurationLoaded;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.core.event.IGameEvents;
import com.communi.suggestu.scena.core.event.IItemEntityPickupEvent;
import com.communi.suggestu.scena.core.event.IPlayerJoinedWorldEvent;
import com.communi.suggestu.scena.core.event.IPlayerLeftClickBlockEvent;
import com.communi.suggestu.scena.core.event.IPlayerLoggedInEvent;
import com.communi.suggestu.scena.core.event.IPlayerRightClickBlockEvent;
import com.communi.suggestu.scena.core.event.IRegisterCommandsEvent;
import com.communi.suggestu.scena.core.event.IServerAboutToStartEvent;
import com.communi.suggestu.scena.core.event.IServerTickEvent;
import com.communi.suggestu.scena.core.event.ProcessingResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public final class ForgeGameEvents implements IGameEvents {
    private static final ForgeGameEvents INSTANCE = new ForgeGameEvents();

    public static ForgeGameEvents getInstance() {
        return INSTANCE;
    }

    @Override
    public IEventEntryPoint<IItemEntityPickupEvent> getItemEntityPickupEvent() {
        return EventBusEventEntryPoint.forge(EntityItemPickupEvent.class, (event, handler) -> handler.handle(event.getItem(), event.getEntity()));
    }

    @Override
    public IEventEntryPoint<IPlayerLeftClickBlockEvent> getPlayerLeftClickEvent() {
        return EventBusEventEntryPoint.forge(PlayerInteractEvent.LeftClickBlock.class, (event, handler) -> {
            final IPlayerLeftClickBlockEvent.Result current = new IPlayerLeftClickBlockEvent.Result(event.isCanceled(), ProcessingResult.valueOf(event.getResult().name()));

            final IPlayerLeftClickBlockEvent.Result result = handler.handle(event.getEntity(), event.getHand(), event.getItemStack(), event.getPos(), event.getFace(), current);

            event.setCanceled(result.handled() || event.isCanceled());
            event.setResult(Event.Result.valueOf(result.result().name()));
        });
    }

    @Override
    public IEventEntryPoint<IPlayerRightClickBlockEvent> getPlayerRightClickEvent() {
        return EventBusEventEntryPoint.forge(PlayerInteractEvent.RightClickBlock.class, (event, handler) -> {
            final IPlayerRightClickBlockEvent.Result current = new IPlayerRightClickBlockEvent.Result(event.isCanceled(), ProcessingResult.valueOf(event.getResult().name()), ProcessingResult.valueOf(event.getUseItem().name()), ProcessingResult.valueOf(event.getUseBlock().name()));

            final IPlayerRightClickBlockEvent.Result result = handler.handle(event.getEntity(), event.getHand(), event.getItemStack(), event.getPos(), event.getFace(), current);

            event.setCanceled(result.handled() || event.isCanceled());
            event.setResult(Event.Result.valueOf(result.result().name()));
            event.setUseItem(Event.Result.valueOf(result.useItemResult().name()));
            event.setUseBlock(Event.Result.valueOf(result.useBlockResult().name()));
        });
    }

    @Override
    public IEventEntryPoint<IPlayerJoinedWorldEvent> getPlayerJoinedWorldEvent() {
        return EventBusEventEntryPoint.forge(EntityJoinLevelEvent.class, (event, handler) -> {
            if (!(event.getEntity() instanceof Player player))
                return;

            handler.handle(player, event.getLevel());
        });
    }

    @Override
    public IEventEntryPoint<IPlayerLoggedInEvent> getPlayerLoggedInEvent() {
        return EventBusEventEntryPoint.forge(PlayerEvent.PlayerLoggedInEvent.class, (event, handler) -> handler.handle(event.getEntity()));
    }

    @Override
    public IEventEntryPoint<IRegisterCommandsEvent> getRegisterCommandsEvent() {
        return EventBusEventEntryPoint.forge(RegisterCommandsEvent.class, (event, handler) -> handler.handle(event.getDispatcher(), event.getBuildContext()));
    }

    @Override
    public IEventEntryPoint<IServerAboutToStartEvent> getServerAboutToStartEvent() {
        return EventBusEventEntryPoint.forge(ServerAboutToStartEvent.class, (event, handler) -> handler.handle(event.getServer()));
    }

    @Override
    public IEventEntryPoint<IChunkLoadEvent> getChunkLoadEvent() {
        return EventBusEventEntryPoint.forge(ChunkEvent.Load.class, (event, handler) -> handler.handle(event.getLevel(), event.getChunk()));
    }

    @Override
    public IEventEntryPoint<ICommonConfigurationLoaded> getCommonConfigurationLoadedEvent() {
        return EventBusEventEntryPoint.mod(ModConfigEvent.Loading.class, (event, handler) -> handler.handle());
    }

    @Override
    public IEventEntryPoint<IServerTickEvent> getServerPreTickEvent() {
        return EventBusEventEntryPoint.forge(TickEvent.ServerTickEvent.class, (event, handler) -> {
            if (event.phase != TickEvent.Phase.START)
                return;

            handler.onTick(event.getServer());
        });
    }

    @Override
    public IEventEntryPoint<IServerTickEvent> getServerPostTickEvent() {
        return EventBusEventEntryPoint.forge(TickEvent.ServerTickEvent.class, (event, handler) -> {
            if (event.phase != TickEvent.Phase.END)
                return;

            handler.onTick(event.getServer());
        });
    }

    private ForgeGameEvents() {
    }

}
