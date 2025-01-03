package com.communi.suggestu.scena.fabric.platform.client.tooltip;

import com.communi.suggestu.scena.core.client.tooltip.IClientTooltipComponentConverter;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientTooltipComponentConverterRegistry {

    private static final ClientTooltipComponentConverterRegistry INSTANCE = new ClientTooltipComponentConverterRegistry();

    public static ClientTooltipComponentConverterRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<Class<?>, IClientTooltipComponentConverter> converters = new ConcurrentHashMap<>();

    private ClientTooltipComponentConverterRegistry() {
    }

    public void register(final Class<?> target, final IClientTooltipComponentConverter converter) {
        converters.put(target, converter);
    }

    @Nullable
    public ClientTooltipComponent convert(final TooltipComponent component) {
        final IClientTooltipComponentConverter converter = converters.get(component.getClass());
        if (converter != null) {
            return converter.convert(component);
        }

        return null;
    }
}
