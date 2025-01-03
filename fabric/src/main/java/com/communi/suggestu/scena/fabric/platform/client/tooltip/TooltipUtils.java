package com.communi.suggestu.scena.fabric.platform.client.tooltip;

import com.communi.suggestu.scena.core.event.Settable;
import com.communi.suggestu.scena.fabric.platform.client.events.FabricClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TooltipUtils {

    public TooltipUtils() {
        throw new UnsupportedOperationException("TooltipUtils is a utility class and cannot be instantiated");
    }

    private static int guiWidth() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    private static int guiHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    public static void gatherTooltipComponents(ItemStack stack, List<ClientTooltipComponent> elements) {
        final Settable<Integer> maxWidth = new Settable.Fixed<>(-1);
        final List<TooltipComponent> tooltipElements = new ArrayList<>();
        if (!FabricClientEvents.GATHER_TOOLTIP_COMPONENTS
                .invoker().gather(stack, guiWidth(), guiHeight(), maxWidth, tooltipElements)) {
            return;
        };

        elements.addAll(
                tooltipElements.stream().map(component -> {
                    if (component instanceof ClientTooltipComponent clientTooltipComponent) {
                        return clientTooltipComponent;
                    }
                    return ClientTooltipComponent.create(component);
                }).toList()
        );
    }
}
