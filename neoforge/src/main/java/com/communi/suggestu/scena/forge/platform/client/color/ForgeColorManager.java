package com.communi.suggestu.scena.forge.platform.client.color;

import com.communi.suggestu.scena.core.client.rendering.IColorManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
public class ForgeColorManager implements IColorManager
{
    private static final ForgeColorManager INSTANCE = new ForgeColorManager();

    public static ForgeColorManager getInstance()
    {
        return INSTANCE;
    }
    
    private final List<Consumer<IColorManager.IBlockColorSetter>> blockColorSetters = Collections.synchronizedList(new ArrayList<>());
    private final List<Consumer<IItemColorSetter>> itemColorSetters = Collections.synchronizedList(new ArrayList<>());
    private final List<Consumer<IDynamicBlockColorSetter>> dynamicBlockColorSetters = Collections.synchronizedList(new ArrayList<>());

    private ForgeColorManager()
    {
    }

    @Override
    public void setupBlockColors(final Consumer<IBlockColorSetter> configurator)
    {
        blockColorSetters.add(configurator);
    }

    @Override
    public void setupDynamicBlockColors(final Consumer<IDynamicBlockColorSetter> setter)
    {
        dynamicBlockColorSetters.add(setter);
    }

    @Override
    public void setupItemColors(final Consumer<IItemColorSetter> configurator)
    {
        itemColorSetters.add(configurator);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockColorHandler(final RegisterColorHandlersEvent.BlockTintSources event)
    {
        ForgeColorManager.getInstance().blockColorSetters.forEach(
            c -> c.accept(event::register)
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemColorHandler(final RegisterColorHandlersEvent.ItemTintSources event)
    {
        ForgeColorManager.getInstance().itemColorSetters.forEach(
          c -> c.accept(event::register)
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClientBlockExtensionHandler(final RegisterClientExtensionsEvent event)
    {
        ForgeColorManager.getInstance().dynamicBlockColorSetters.forEach(
            setter -> {
                setter.accept((colorManager, blocks) -> event.registerBlock(
                    new IClientBlockExtensions() {
                        @Override
                        public void collectDynamicTintValues(final @NonNull BlockState state, final @NonNull BlockAndTintGetter level, final @NonNull BlockPos pos, final @NonNull IntList tintValues)
                        {
                            colorManager.collect(state, level, pos, tintValues);
                        }
                    },
                    blocks
                ));
            }
        );
    }
}
