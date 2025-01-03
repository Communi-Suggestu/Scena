package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.communi.suggestu.scena.core.event.Settable;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Event fired when tooltip components are being gathered.
 */
public interface IGatherTooltipComponentsEvent extends IEvent {

    /**
     * Gather tooltip components.
     * <p>
     *     The tooltip elements are added to the given list.
     *
     * @param itemStack The item stack.
     * @param screenWidth The screen width.
     * @param screenHeight The screen height.
     * @param maxWidth The maximum width.
     * @param tooltipElements The tooltip elements.
     * @return True if the tooltip should be rendered, false otherwise.
     */
    boolean gather(ItemStack itemStack, int screenWidth, int screenHeight, Settable<Integer> maxWidth, List<TooltipComponent> tooltipElements);
}
