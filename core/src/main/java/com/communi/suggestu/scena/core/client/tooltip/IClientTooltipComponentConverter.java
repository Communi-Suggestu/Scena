package com.communi.suggestu.scena.core.client.tooltip;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.Optional;

public interface IClientTooltipComponentConverter {

    /**
     * Converts a tooltip component to a client tooltip component.
     *
     * @param component The tooltip component to convert.
     * @return The converted client tooltip component
     */
    ClientTooltipComponent convert(final TooltipComponent component);
}
