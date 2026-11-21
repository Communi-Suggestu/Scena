package com.communi.suggestu.scena.mixin.terrain;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

/**
 * Gates the terrain sharding mixins on {@code scena.experimentalTerrainSharding}.
 * Sharding owns the vanilla section upload path; when Sodium is present it takes terrain rendering,
 * so the mixins stay off and the warning is logged once.
 */
public final class TerrainShardingMixinPlugin implements IMixinConfigPlugin
{
    private static final String PROPERTY = "scena.experimentalTerrainSharding";
    private static final String SODIUM_CLASS = "net.caffeinemc.mods.sodium.client.SodiumClientMod";

    private boolean shardingEnabled;

    @Override
    public void onLoad(final String mixinPackage)
    {
        shardingEnabled = requestedSharding();
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName)
    {
        return !mixinClassName.startsWith("com.communi.suggestu.scena.mixin.terrain.") || shardingEnabled;
    }

    private static boolean requestedSharding()
    {
        if (!Boolean.getBoolean(PROPERTY))
        {
            return false;
        }

        try
        {
            MixinService.getService().getBytecodeProvider().getClassNode(SODIUM_CLASS);
            MixinService.getService().getLogger("scena-terrain").warn(
                "Experimental terrain sharding is disabled: Sodium owns terrain rendering and its adapter is not implemented."
            );
            return false;
        }
        catch (final ClassNotFoundException absent)
        {
            return true;
        }
        catch (final IOException error)
        {
            throw new IllegalStateException("Cannot determine the active terrain backend", error);
        }
    }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public void acceptTargets(final Set<String> ownTargets, final Set<String> otherTargets)
    {
    }

    @Override
    public List<String> getMixins()
    {
        return null;
    }

    @Override
    public void preApply(final String target, final ClassNode node, final String name, final IMixinInfo info)
    {
    }

    @Override
    public void postApply(final String target, final ClassNode node, final String name, final IMixinInfo info)
    {
    }
}
