package com.communi.suggestu.scena.core.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Event fired when a tooltip is being gathered.
 */
@FunctionalInterface
public interface IGatherTooltipEvent extends IEvent {

    /**
     * Invoked when a tooltip is being gathered.
     *
     * @param stack The stack to gather tooltip for.
     * @param currentTooltip The current tooltip.
     */
    void handle(ItemStack stack, List<Component> currentTooltip);
}
