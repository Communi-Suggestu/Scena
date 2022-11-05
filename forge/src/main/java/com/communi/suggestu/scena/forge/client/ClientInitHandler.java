package com.communi.suggestu.scena.forge.client;

import com.communi.suggestu.scena.core.client.integration.IOptifineCompatibilityManager;
import com.communi.suggestu.scena.forge.platform.client.event.ForgeScenaRenderWorldLastEvent;
import com.communi.suggestu.scena.forge.utils.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInitHandler
{

    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void onFMLClientSetup(final FMLClientSetupEvent event)
    {
        if (IOptifineCompatibilityManager.getInstance().isInstalled()) {
            Mod.EventBusSubscriber.Bus.FORGE.bus().get().addListener((Consumer<RenderLevelLastEvent>) event1 -> {
                Mod.EventBusSubscriber.Bus.FORGE.bus().get().post(new ForgeScenaRenderWorldLastEvent(event1.getLevelRenderer(), event1.getPartialTick(), event1.getPoseStack()));
            });
        }
    }
}
