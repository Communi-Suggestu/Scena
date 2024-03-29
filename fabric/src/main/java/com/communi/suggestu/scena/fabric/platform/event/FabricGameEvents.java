package com.communi.suggestu.scena.fabric.platform.event;

import com.communi.suggestu.scena.core.dist.Dist;
import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.communi.suggestu.scena.core.event.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Function;

public final class FabricGameEvents implements IGameEvents {
    private static final FabricGameEvents INSTANCE = new FabricGameEvents();

    public static FabricGameEvents getInstance() {
        return INSTANCE;
    }

    public static final Event<IItemEntityPickupEvent> ENTITY_ITEM_PICKUP = EventFactory.createArrayBacked(IItemEntityPickupEvent.class, callbacks -> (final ItemEntity entityToPickup, final Player player) -> {
        boolean handled = false;
        for (IItemEntityPickupEvent callback : callbacks) {
            if (callback.handle(entityToPickup, player)) {
                handled = true;
            }

            if (handled)
                return true;
        }

        return false;
    });

    public static final Event<IChunkLoadEvent> CHUNK_LOAD = EventFactory.createArrayBacked(IChunkLoadEvent.class, callbacks -> (level, chunkAccess) -> {
        for (IChunkLoadEvent callback : callbacks) {
            callback.handle(level, chunkAccess);
        }
    });

    public static final Event<IChunkSentEvent> CHUNK_SENT = EventFactory.createArrayBacked(IChunkSentEvent.class, callbacks -> (player, chunk, level) -> {
        for (IChunkSentEvent callback : callbacks) {
            callback.handle(player, chunk, level);
        }
    });

    public static final Event<ICommonConfigurationLoaded> COMMON_CONFIGURATION_LOADED = EventFactory.createArrayBacked(ICommonConfigurationLoaded.class, callbacks -> () -> {
        for (ICommonConfigurationLoaded callback : callbacks) {
            callback.handle();
        }
    });

    private FabricGameEvents() {
    }

    @Override
    public IEventEntryPoint<IItemEntityPickupEvent> getItemEntityPickupEvent() {
        return FabricEventEntryPoint.create(ENTITY_ITEM_PICKUP, Function.identity());
    }

    @Override
    public IEventEntryPoint<IPlayerLeftClickBlockEvent> getPlayerLeftClickEvent() {
        return FabricEventEntryPoint.create(AttackBlockCallback.EVENT, handler -> (player, world, hand, pos, direction) -> {
            final IPlayerLeftClickBlockEvent.Result result = handler.handle(
                    player,
                    hand,
                    player.getItemInHand(hand),
                    pos,
                    direction,
                    new IPlayerLeftClickBlockEvent.Result(false, ProcessingResult.DEFAULT, ProcessingResult.DEFAULT)
            );

            if (result.useBlockResult() != ProcessingResult.DEFAULT) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    if (Minecraft.getInstance() != null && Minecraft.getInstance().gameMode != null) {
                        Minecraft.getInstance().gameMode.destroyDelay = 3;
                    }
                });
            }

            return mapResult(result);
        });
    }

    @Override
    public IEventEntryPoint<IPlayerRightClickBlockEvent> getPlayerRightClickEvent() {
        return FabricMultiEventEntryPoint.create(
             FabricEventEntryPoint.create(UseBlockCallback.EVENT, handler -> (Player player, Level world, InteractionHand hand, BlockHitResult hitResult) -> {
                final IPlayerRightClickBlockEvent.Result result = handler.handle(
                        player,
                        hand,
                        player.getItemInHand(hand),
                        hitResult.getBlockPos(),
                        hitResult.getDirection(),
                        new IPlayerRightClickBlockEvent.Result(false, ProcessingResult.DEFAULT, ProcessingResult.DEFAULT)
                );
    
                return mapBlockResult(result);
            }),
            FabricEventEntryPoint.create(UseItemCallback.EVENT, handler -> (Player player, Level world, InteractionHand hand) -> {
                final IPlayerRightClickBlockEvent.Result result = handler.handle(
                        player,
                        hand,
                        player.getItemInHand(hand),
                        BlockPos.ZERO,
                        Direction.DOWN,
                        new IPlayerRightClickBlockEvent.Result(false, ProcessingResult.DEFAULT, ProcessingResult.DEFAULT)
                );

                return new InteractionResultHolder<>(mapItemResult(result), player.getItemInHand(hand));
            })
        );
    }

    @Override
    public IEventEntryPoint<IPlayerJoinedWorldEvent> getPlayerJoinedWorldEvent() {
        return FabricEventEntryPoint.create(ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD, handler -> (player, origin, destination) -> handler.handle(player, destination));
    }

    @Override
    public IEventEntryPoint<IPlayerLoggedInEvent> getPlayerLoggedInEvent() {
        return FabricEventEntryPoint.create(ServerPlayConnectionEvents.JOIN, handler -> (serverGamePacketListener, sender, server) -> handler.handle(serverGamePacketListener.player));
    }

    @Override
    public IEventEntryPoint<IRegisterCommandsEvent> getRegisterCommandsEvent() {
        return FabricEventEntryPoint.create(CommandRegistrationCallback.EVENT, handler -> (dispatcher, registryAccess, environment) -> handler.handle(dispatcher, registryAccess));
    }

    @Override
    public IEventEntryPoint<IServerAboutToStartEvent> getServerAboutToStartEvent() {
        return FabricEventEntryPoint.create(ServerLifecycleEvents.SERVER_STARTING, handler -> handler::handle);
    }

    @Override
    public IEventEntryPoint<IChunkLoadEvent> getChunkLoadEvent() {
        return FabricEventEntryPoint.create(CHUNK_LOAD, Function.identity());
    }

    @Override
    public IEventEntryPoint<IChunkSentEvent> getChunkSentEvent() {
        return FabricEventEntryPoint.create(CHUNK_SENT, Function.identity());
    }

    @Override
    public IEventEntryPoint<ICommonConfigurationLoaded> getCommonConfigurationLoadedEvent() {
        return FabricEventEntryPoint.create(COMMON_CONFIGURATION_LOADED, Function.identity());
    }

    @Override
    public IEventEntryPoint<IServerTickEvent> getServerPreTickEvent() {
        return FabricEventEntryPoint.create(ServerTickEvents.START_SERVER_TICK, iServerTickEvent -> iServerTickEvent::onTick);
    }

    @Override
    public IEventEntryPoint<IServerTickEvent> getServerPostTickEvent() {
        return FabricEventEntryPoint.create(ServerTickEvents.END_SERVER_TICK, iServerTickEvent -> iServerTickEvent::onTick);
    }

    private static InteractionResult mapResult(
            final IPlayerLeftClickBlockEvent.Result processingResult
    ) {
        return switch (processingResult.useBlockResult())
                {
                    case DENY -> InteractionResult.FAIL;
                    case DEFAULT -> InteractionResult.PASS;
                    case ALLOW -> processingResult.handled() ? InteractionResult.SUCCESS : InteractionResult.PASS;
                };
    }

    private static InteractionResult mapBlockResult(
            final IPlayerRightClickBlockEvent.Result processingResult
    ) {
        return switch (processingResult.useBlockResult())
                {
                    case DENY -> InteractionResult.FAIL;
                    case DEFAULT -> InteractionResult.PASS;
                    case ALLOW -> processingResult.handled() ? InteractionResult.SUCCESS : InteractionResult.PASS;
                };
    }

    private static InteractionResult mapItemResult(
            final IPlayerRightClickBlockEvent.Result processingResult
    ) {
        return switch (processingResult.useItemResult())
        {
            case DENY -> InteractionResult.FAIL;
            case DEFAULT -> InteractionResult.PASS;
            case ALLOW -> processingResult.handled() ? InteractionResult.SUCCESS : InteractionResult.PASS;
        };
    }

}
