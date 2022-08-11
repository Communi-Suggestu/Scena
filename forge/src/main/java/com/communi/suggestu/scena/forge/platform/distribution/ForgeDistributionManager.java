package com.communi.suggestu.scena.forge.platform.distribution;

import com.communi.suggestu.scena.core.dist.Dist;
import com.communi.suggestu.scena.core.dist.IDistributionManager;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ForgeDistributionManager implements IDistributionManager
{
    private static final ForgeDistributionManager INSTANCE = new ForgeDistributionManager();

    public static ForgeDistributionManager getInstance()
    {
        return INSTANCE;
    }

    private ForgeDistributionManager()
    {
    }

    @Override
    public Dist getCurrentDistribution()
    {
        return switch (FMLEnvironment.dist) {
            case CLIENT -> Dist.CLIENT;
            case DEDICATED_SERVER -> Dist.DEDICATED_SERVER;
        };
    }

    @Override
    public boolean isProduction()
    {
        return FMLEnvironment.production;
    }
}
