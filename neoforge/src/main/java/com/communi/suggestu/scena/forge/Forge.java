package com.communi.suggestu.scena.forge;

import com.communi.suggestu.scena.core.IScenaPlatform;
import com.communi.suggestu.scena.core.init.PlatformInitializationHandler;
import com.communi.suggestu.scena.forge.platform.ForgeScenaPlatform;
import com.communi.suggestu.scena.forge.utils.Constants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Constants.MOD_ID)
public class Forge
{
    private static final Logger LOGGER = LoggerFactory.getLogger("scena-forge");

	public Forge(IEventBus modBus)
	{
        IScenaPlatform platform = ForgeScenaPlatform.init(modBus);
        PlatformInitializationHandler.getInstance().init(platform);

        LOGGER.info("Initialized scena-forge");
	}
}
