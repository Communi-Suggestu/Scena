package com.communi.suggestu.scena.forge.platform.client.rendering;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Conservative eligibility for the stock NeoForge compiler, not a general replacement-renderer compatibility claim.
 */
public final class NativeTerrainCompatibility
{
    private static final Set<String> SUPPORTED_MIXINS = Set.of(
        "com.communi.suggestu.scena.forge.mixin.platform.client.TerrainGeometrySectionCompilerMixin",
        "com.communi.suggestu.scena.forge.mixin.platform.client.TerrainCompileScopeMixin",
        "com.communi.suggestu.scena.mixin.terrain.CpuSectionCompilerMixin",
        "com.communi.suggestu.scena.mixin.terrain.BufferBuilderMixin"
    );
    private static final Set<String> UNSUPPORTED_MODS = Set.of("sodium", "embeddium", "rubidium", "iris", "oculus");
    private static final Set<String> SUPPORTED_INTERFACES = Set.of(
        "com.mojang.blaze3d.vertex.VertexConsumer",
        "net.neoforged.neoforge.client.extensions.IVertexConsumerExtension",
        "com.communi.suggestu.scena.core.client.rendering.sharding.TerrainBuilderAccess",
        "com.communi.suggestu.scena.mixin.terrain.BufferBuilderMixin"
    );
    private static final ClassValue<Boolean> SUPPORTED_HOOKS = new ClassValue<>()
    {
        @Override
        protected Boolean computeValue(final Class<?> type)
        {
            return hasOnlySupportedHooks(type);
        }
    };

    private NativeTerrainCompatibility()
    {
    }

    public static boolean supportsRenderer(final Class<?> renderer)
    {
        if (renderer != ModelBlockRenderer.class)
        {
            return false;
        }
        final ModList mods = ModList.get();
        if (mods == null)
        {
            return false;
        }
        for (final String mod : UNSUPPORTED_MODS)
        {
            if (mods.isLoaded(mod))
            {
                return false;
            }
        }
        return SUPPORTED_HOOKS.get(renderer)
            && SUPPORTED_HOOKS.get(SectionCompiler.class)
            && SUPPORTED_HOOKS.get(BufferBuilder.class)
            && SUPPORTED_HOOKS.get(VertexConsumer.class);
    }

    static boolean hasOnlySupportedHooks(final Class<?> type)
    {
        try
        {
            for (Class<?> current = type; current != null; current = current.getSuperclass())
            {
                for (final Class<?> contract : current.getInterfaces())
                {
                    if (!SUPPORTED_INTERFACES.contains(contract.getName()) || !hasOnlySupportedHooks(contract))
                    {
                        return false;
                    }
                }
                for (final var method : current.getDeclaredMethods())
                {
                    final MixinMerged merged = method.getAnnotation(MixinMerged.class);
                    if (merged != null && !SUPPORTED_MIXINS.contains(merged.mixin())
                        && !isReviewedBenchmarkHook(current, method, merged.mixin()))
                    {
                        return false;
                    }

                }
            }
            return true;
        }
        catch (final LinkageError | SecurityException unsupported)
        {
            return false;
        }
    }

    private static boolean isReviewedBenchmarkHook(final Class<?> target, final Method method, final String mixin)
    {
        if (!mixin.equals("mod.chiselsandbits.forge.mixin.client.SectionCompilerMixin")
            || !target.getName().equals("net.minecraft.client.renderer.chunk.SectionCompiler")
            || !Modifier.isPrivate(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
            || method.getReturnType() != void.class || method.getParameterCount() != 1
            || method.getParameterTypes()[0] != CallbackInfoReturnable.class)
        {
            return false;
        }
        // These HEAD/RETURN observers only time compilation and read completed mesh counts.
        return Set.of("chiselsandbits$onCompileStart", "chiselsandbits$onCompileEnd").stream()
            .anyMatch(name -> method.getName().equals(name) || method.getName().endsWith("$" + name));
    }
}
