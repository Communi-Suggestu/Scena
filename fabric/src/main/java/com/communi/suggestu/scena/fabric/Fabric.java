package com.communi.suggestu.scena.fabric;

import com.communi.suggestu.scena.core.IScenaPlatform;
import com.communi.suggestu.scena.core.init.PlatformInitializationHandler;
import com.communi.suggestu.scena.fabric.platform.FabricScenaPlatform;
import com.communi.suggestu.scena.fabric.platform.server.FabricServerLifecycleManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fabric implements ModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("scena-fabric");

    @Override
	public void onInitialize() {
        IScenaPlatform platform = FabricScenaPlatform.getInstance();
        PlatformInitializationHandler.getInstance().init(platform);

        setupEvents();

        LOGGER.info("Initialized scena-fabric");
	}

    private static void setupEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(FabricServerLifecycleManager.getInstance()::setServer);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> FabricServerLifecycleManager.getInstance().clearServer());
    }
}
