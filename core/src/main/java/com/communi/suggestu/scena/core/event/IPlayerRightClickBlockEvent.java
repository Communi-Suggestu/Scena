package com.communi.suggestu.scena.core.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Event raised to indicate that the player right-clicked a particular block.
 */
@FunctionalInterface
public interface IPlayerRightClickBlockEvent extends IEvent {

    /**
     * The processing result of this event.
     *
     * @param handled Indicates if the event was handled and further processing should happen.
     * @param result The result of the event processing.
     */
    record Result(boolean handled, ProcessingResult result) {}

    /**
     * Invoked to handle the event.
     *
     * @param player The player.
     * @param hand The hand used to click.
     * @param usedStack The used stack.
     * @param clickedPosition The position of the block that was hit.
     * @param hitFace The side of the block that was hit.
     * @param currentResult The current result of previous event handlers in the chain.
     * @return The processing result.
     */
    Result handle(Player player, InteractionHand hand, ItemStack usedStack, BlockPos clickedPosition, Direction hitFace, Result currentResult);
}
