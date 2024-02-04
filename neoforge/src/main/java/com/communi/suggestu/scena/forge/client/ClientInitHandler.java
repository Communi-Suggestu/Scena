package com.communi.suggestu.scena.forge.client;

import com.communi.suggestu.scena.core.client.integration.IOptifineCompatibilityManager;
import com.communi.suggestu.scena.forge.platform.client.event.ForgeScenaRenderWorldLastEvent;
import com.communi.suggestu.scena.forge.utils.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInitHandler
{

    @SubscribeEvent
    public static void onFMLClientSetup(final FMLClientSetupEvent event)
    {
        if (IOptifineCompatibilityManager.getInstance().isInstalled()) {
            NeoForge.EVENT_BUS.addListener((Consumer<RenderLevelStageEvent>) event1 -> {
                if (event1.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
                    return;
                }

                NeoForge.EVENT_BUS.post(new ForgeScenaRenderWorldLastEvent(event1.getLevelRenderer(), event1.getPartialTick(), event1.getPoseStack()));
            });
        }
    }
}
