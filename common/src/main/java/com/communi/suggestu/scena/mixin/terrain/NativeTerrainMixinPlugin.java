package com.communi.suggestu.scena.mixin.terrain;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainBackendSelection;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

public final class NativeTerrainMixinPlugin implements IMixinConfigPlugin
{
    private boolean shardingEnabled;
    private boolean geometryEnabled;

    @Override
    public void onLoad(final String mixinPackage)
    {
        shardingEnabled = enabledForVanillaBackend(
            "scena.experimentalTerrainSharding",
            "Experimental vanilla terrain sharding is disabled: Sodium owns terrain rendering and its adapter is not implemented."
        );
        geometryEnabled = enabledForVanillaBackend(
            "scena.experimentalNativeTerrainGeometry",
            "Experimental vanilla terrain geometry is disabled: Sodium owns terrain rendering and no Sodium adapter is implemented."
        );
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName)
    {
        if (mixinClassName.endsWith(".TerrainGeometrySectionCompilerMixin"))
        {
            return geometryEnabled;
        }

        return (!mixinClassName.startsWith("com.communi.suggestu.scena.mixin.terrain.")
            && !mixinClassName.endsWith(".TerrainCompileScopeMixin")) || shardingEnabled;
    }

    private static boolean enabledForVanillaBackend(final String property, final String warning)
    {
        final boolean requested = Boolean.getBoolean(property);
        if (!requested)
        {
            return false;
        }

        try
        {
            MixinService.getService().getBytecodeProvider().getClassNode("net.caffeinemc.mods.sodium.client.SodiumClientMod");
            final boolean enabled = TerrainBackendSelection.enableVanillaHook(requested, true);
            if (!enabled)
            {
                MixinService.getService().getLogger("scena-terrain").warn(warning);
            }
            return enabled;
        }
        catch (final ClassNotFoundException absent)
        {
            return TerrainBackendSelection.enableVanillaHook(requested, false);
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
