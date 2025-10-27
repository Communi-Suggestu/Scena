package com.communi.suggestu.scena.forge.platform.event;

import com.communi.suggestu.scena.core.event.*;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.function.Consumer;

public final class ForgeGameEvents implements IGameEvents {
    private static final ForgeGameEvents INSTANCE = new ForgeGameEvents();

    public static ForgeGameEvents getInstance() {
        return INSTANCE;
    }

    @Override
    public IEventEntryPoint<IItemEntityPickupEvent> getItemEntityPickupEvent() {
        return EventBusEventEntryPoint.forge(ItemEntityPickupEvent.Pre.class, (event, handler) -> handler.handle(event.getItemEntity(), event.getPlayer()));
    }

    @Override
    public IEventEntryPoint<IPlayerLeftClickBlockEvent> getPlayerLeftClickEvent() {
        return EventBusEventEntryPoint.forge(PlayerInteractEvent.LeftClickBlock.class, (event, handler) -> {
            final IPlayerLeftClickBlockEvent.Result current = new IPlayerLeftClickBlockEvent.Result(event.isCanceled(), toResult(event.getUseItem()), toResult(event.getUseBlock()));

            final IPlayerLeftClickBlockEvent.Result result = handler.handle(event.getEntity(), event.getHand(), event.getItemStack(), event.getPos(), event.getFace(), current);

            event.setCanceled(result.handled() || event.isCanceled());
            event.setUseItem(fromResult(result.useItemResult()));
            event.setUseBlock(fromResult(result.useBlockResult()));

        });
    }

    @Override
    public IEventEntryPoint<IPlayerRightClickBlockEvent> getPlayerRightClickEvent() {
        return EventBusEventEntryPoint.forge(PlayerInteractEvent.RightClickBlock.class, (event, handler) -> {
            final IPlayerRightClickBlockEvent.Result current = new IPlayerRightClickBlockEvent.Result(event.isCanceled(), toResult(event.getUseItem()), toResult(event.getUseBlock()));

            final IPlayerRightClickBlockEvent.Result result = handler.handle(event.getEntity(), event.getHand(), event.getItemStack(), event.getPos(), event.getFace(), current);

            event.setCanceled(result.handled() || event.isCanceled());
            event.setUseItem(fromResult(result.useItemResult()));
            event.setUseBlock(fromResult(result.useBlockResult()));
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
    public IEventEntryPoint<IChunkSentEvent> getChunkSentEvent() {
        return EventBusEventEntryPoint.forge(ChunkWatchEvent.Sent.class , (event, handler) -> handler.handle(event.getPlayer(), event.getChunk(), event.getLevel()));
    }

    @Override
    public IEventEntryPoint<ICommonConfigurationLoaded> getCommonConfigurationLoadedEvent() {
        return EventBusEventEntryPoint.mod(ModConfigEvent.Loading.class, (event, handler) -> handler.handle());
    }

    @Override
    public IEventEntryPoint<IServerTickEvent> getServerPreTickEvent() {
        return EventBusEventEntryPoint.forge(ServerTickEvent.Pre.class, (event, handler) -> {
            handler.onTick(event.getServer());
        });
    }

    @Override
    public IEventEntryPoint<IServerTickEvent> getServerPostTickEvent() {
        return EventBusEventEntryPoint.forge(ServerTickEvent.Post.class, (event, handler) -> {
            handler.onTick(event.getServer());
        });
    }

    @Override
    public IEventEntryPoint<IDataPackSyncEvent> getDataPackSyncEvent() {
        return EventBusEventEntryPoint.forge(OnDatapackSyncEvent.class, (event, handler) -> {
            handler.onSync(event.getPlayerList(), event.getRelevantPlayers(), event::sendRecipes);
        });
    }

    @Override
    public IEventEntryPoint<IRecipesReceivedEvent> getRecipesReceivedEvent()
    {
        return EventBusEventEntryPoint.forge(RecipesReceivedEvent.class, (forge, scena) -> {
            scena.handle(forge.getRecipeMap());
        });
    }

    private static TriState fromResult(ProcessingResult result) {
        return switch (result) {
            case DENY -> TriState.FALSE;
            case ALLOW -> TriState.TRUE;
            default -> TriState.DEFAULT;
        };
    }

    private static ProcessingResult toResult(TriState result) {
        return switch (result) {
            case FALSE -> ProcessingResult.DENY;
            case TRUE -> ProcessingResult.ALLOW;
            default -> ProcessingResult.DEFAULT;
        };
    }

    private ForgeGameEvents() {
    }

}
