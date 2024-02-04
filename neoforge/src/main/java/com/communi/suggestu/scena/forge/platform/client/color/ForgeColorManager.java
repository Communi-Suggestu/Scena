package com.communi.suggestu.scena.forge.platform.client.color;

import com.communi.suggestu.scena.core.client.rendering.IColorManager;
import com.google.common.collect.Lists;
import com.communi.suggestu.scena.forge.utils.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeColorManager implements IColorManager
{
    private static final ForgeColorManager INSTANCE = new ForgeColorManager();

    public static ForgeColorManager getInstance()
    {
        return INSTANCE;
    }
    
    private final List<Consumer<IColorManager.IBlockColorSetter>> blockColorSetters = Lists.newArrayList();
    private final List<Consumer<IItemColorSetter>> itemColorSetters = Lists.newArrayList();

    private ForgeColorManager()
    {
    }

    @Override
    public void setupBlockColors(final Consumer<IBlockColorSetter> configurator)
    {
        blockColorSetters.add(configurator);
    }

    @Override
    public void setupItemColors(final Consumer<IItemColorSetter> configurator)
    {
        itemColorSetters.add(configurator);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockColorHandler(final RegisterColorHandlersEvent.Block event)
    {
        ForgeColorManager.getInstance().blockColorSetters.forEach(
            c -> c.accept(event::register)
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemColorHandler(final RegisterColorHandlersEvent.Item event)
    {
        ForgeColorManager.getInstance().itemColorSetters.forEach(
          c -> c.accept(event::register)
        );
    }
}
