package com.communi.suggestu.scena.forge.platform.event;

import com.communi.suggestu.scena.core.event.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.Event;

public final class ForgeGameEvents implements IGameEvents {
    private static final ForgeGameEvents INSTANCE = new ForgeGameEvents();

    public static ForgeGameEvents getInstance() {
        return INSTANCE;
    }

    @Override
    public IEventEntryPoint<IItemEntityPickupEvent> getItemEntityPickupEvent() {
        return EventBusEventEntryPoint.<IItemEntityPickupEvent, EntityItemPickupEvent>forge((event, handler) -> handler.handle(event.getItem(), event.getEntity()));
    }

    @Override
    public IEventEntryPoint<IPlayerLeftClickBlockEvent> getPlayerLeftClickEvent() {
        return EventBusEventEntryPoint.<IPlayerLeftClickBlockEvent, PlayerInteractEvent.LeftClickBlock>forge((event, handler) -> {
            final IPlayerLeftClickBlockEvent.Result current = new IPlayerLeftClickBlockEvent.Result(event.isCanceled(), ProcessingResult.valueOf(event.getResult().name()));

            final IPlayerLeftClickBlockEvent.Result result = handler.handle(event.getEntity(), event.getHand(), event.getItemStack(), event.getPos(), event.getFace(), current);

            event.setCanceled(result.handled() || event.isCanceled());
            event.setResult(Event.Result.valueOf(result.result().name()));
        });
    }

    @Override
    public IEventEntryPoint<IPlayerRightClickBlockEvent> getPlayerRightClickEvent() {
        return EventBusEventEntryPoint.<IPlayerRightClickBlockEvent, PlayerInteractEvent.RightClickBlock>forge((event, handler) -> {
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
        return EventBusEventEntryPoint.<IPlayerJoinedWorldEvent, EntityJoinLevelEvent>forge((event, handler) -> {
            if (!(event.getEntity() instanceof Player player))
                return;

            handler.handle(player, event.getLevel());
        });
    }

    @Override
    public IEventEntryPoint<IPlayerLoggedInEvent> getPlayerLoggedInEvent() {
        return EventBusEventEntryPoint.<IPlayerLoggedInEvent, PlayerEvent.PlayerLoggedInEvent>forge((event, handler) -> handler.handle(event.getEntity()));
    }

    @Override
    public IEventEntryPoint<IRegisterCommandsEvent> getRegisterCommandsEvent() {
        return EventBusEventEntryPoint.<IRegisterCommandsEvent, RegisterCommandsEvent>forge((event, handler) -> handler.handle(event.getDispatcher(), event.getBuildContext()));
    }

    @Override
    public IEventEntryPoint<IServerAboutToStartEvent> getServerAboutToStartEvent() {
        return EventBusEventEntryPoint.<IServerAboutToStartEvent, ServerAboutToStartEvent>forge((event, handler) -> handler.handle(event.getServer()));
    }

    @Override
    public IEventEntryPoint<IGatherTooltipEvent> getGatherTooltipEvent() {
        return EventBusEventEntryPoint.<IGatherTooltipEvent, ItemTooltipEvent>forge((event, handler) -> handler.handle(event.getItemStack(), event.getToolTip()));
    }

    @Override
    public IEventEntryPoint<IChunkLoadEvent> getChunkLoadEvent() {
        return EventBusEventEntryPoint.<IChunkLoadEvent, ChunkEvent.Load>forge((event, handler) -> handler.handle(event.getLevel(), event.getChunk()));
    }

    private ForgeGameEvents() {
    }

}
