package com.communi.suggestu.scena.core.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Event fired when a tooltip is being gathered.
 */
@FunctionalInterface
public interface IGatherTooltipEvent extends IEvent {

    /**
     * Handles the tooltip gathering event.
     *
     * @param stack The stack being hovered over.
     * @param tooltipContext The context of the tooltip.
     * @param tooltipType The type of tooltip.
     * @param lines The lines of the tooltip.
     */
    void handle(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipType, List<Component> lines);
}
