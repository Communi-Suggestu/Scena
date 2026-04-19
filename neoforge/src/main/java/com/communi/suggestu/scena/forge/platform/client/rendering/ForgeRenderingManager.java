package com.communi.suggestu.scena.forge.platform.client.rendering;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.core.client.tooltip.IClientTooltipComponentConverter;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.forge.platform.client.model.ForgeModelManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ForgeRenderingManager implements IRenderingManager
{
    private static final ForgeRenderingManager INSTANCE = new ForgeRenderingManager();

    public static ForgeRenderingManager getInstance()
    {
        return INSTANCE;
    }

    private final List<Consumer<IBlockEntityRendererRegistrar>>             blockEntityRegistrars                     = Collections.synchronizedList(Lists.newArrayList());
    private final List<Consumer<ISpecialModelRendererRegistrar>>            blockEntityWithoutLevelRegistrars         = Collections.synchronizedList(Lists.newArrayList());
    private final List<Consumer<IClientTooltipComponentConverterRegistrar>> clientTooltipComponentConverterRegistrars = Collections.synchronizedList(Lists.newArrayList());
    private final AtomicBoolean                                                           registered = new AtomicBoolean(false);

    private ForgeRenderingManager()
    {
    }

    @Override
    public void registerClientTooltipComponentConverter(Consumer<IClientTooltipComponentConverterRegistrar> callback) {
        if (registered.get())
        {
            throw new IllegalStateException("Cannot register a client tooltip component converter after the client setup event has been fired.");
        }

        clientTooltipComponentConverterRegistrars.add(callback);
    }

    @Override
    public void registerSpecialModelRenderer(final Consumer<ISpecialModelRendererRegistrar> callback)
    {
        if (registered.get())
        {
            throw new IllegalStateException("Cannot register a block entity without level renderer after the client setup event has been fired.");
        }

        blockEntityWithoutLevelRegistrars.add(callback);
    }

    @Override
    public void registerBlockEntityRenderer(final Consumer<IBlockEntityRendererRegistrar> callback)
    {
        if (registered.get())
        {
            throw new IllegalStateException("Cannot register a block entity renderer after the client setup event has been fired.");
        }

        blockEntityRegistrars.add(callback);
    }

    @SubscribeEvent
    public static void onRegisterBlockEntityWithoutLevelRenderers(final FMLClientSetupEvent event)
    {
        getInstance().registered.set(true);
        getInstance().blockEntityRegistrars.forEach(callback -> callback.accept(BlockEntityRenderers::register));
    }

    @SubscribeEvent
    public static void onRegisterSpecialBlockModelRenderer(RegisterSpecialModelRendererEvent event) {
        getInstance().blockEntityWithoutLevelRegistrars
            .forEach(consumer -> {
                consumer.accept(event::register);
            });
    }

    @SubscribeEvent
    public static void onRegisterClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        getInstance().clientTooltipComponentConverterRegistrars.forEach(callback -> callback.accept(
                new IClientTooltipComponentConverterRegistrar() {
                    @Override
                    public <T extends TooltipComponent> void registerConvert(Class<T> tooltipComponentType, IClientTooltipComponentConverter converter) {
                        event.register(tooltipComponentType, converter::convert);
                    }
                }
        ));
    }

    @Override
    public IModelManager getModelManager()
    {
        return ForgeModelManager.getInstance();
    }

    @Override
    public int adaptVertexColor(int color) {
        return color;
    }

    @NotNull
    private FluidStack buildFluidStack(final FluidInformation fluid)
    {
        if (fluid.data().isEmpty())
            return new FluidStack(fluid.fluid(), (int) fluid.amount());

        return new FluidStack(
            fluid.fluid(), (int) fluid.amount(), fluid.data());
    }
}
