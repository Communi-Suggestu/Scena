package com.communi.suggestu.scena.fabric.platform.client.rendering;

import java.util.Set;
import java.lang.reflect.Modifier;

import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainGeometry;
import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainGeometryValidation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AltModelBlockRendererImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The first resolved adapter supports only the stock Indigo renderer, not arbitrary FRAPI implementations or shaders.
 */
public final class NativeTerrainCompatibility
{
    private static final Set<String> SUPPORTED_MIXINS = Set.of(
        "com.communi.suggestu.scena.fabric.mixin.platform.client.TerrainGeometrySectionCompilerMixin",
        "com.communi.suggestu.scena.fabric.mixin.platform.client.TerrainCompileScopeMixin",
        "com.communi.suggestu.scena.mixin.terrain.CpuSectionCompilerMixin",
        "com.communi.suggestu.scena.mixin.terrain.BufferBuilderMixin",
        "net.fabricmc.fabric.mixin.client.renderer.block.render.SectionCompilerMixin"
    );
    private static final String[] UNSUPPORTED_MODS = {"sodium", "iris", "canvas", "indium"};
    private static final ClassValue<Boolean> SUPPORTED_HOOKS = new ClassValue<>()
    {
        protected Boolean computeValue(final Class<?> type)
        {
            return hasSupportedHooks(type);
        }
    };

    private NativeTerrainCompatibility()
    {
    }

    public static boolean supports(final TerrainGeometry geometry)
    {
        if (!TerrainGeometryValidation.supportsCurrentTerrainShader(geometry))
        {
            return false;
        }
        for (final String mod : UNSUPPORTED_MODS)
        {
            if (FabricLoader.getInstance().isModLoaded(mod))
            {
                return false;
            }
        }
        final Class<?> renderer = Renderer.get().getClass();
        if (!renderer.getName().equals("net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer"))
        {
            return false;
        }
        if (!SUPPORTED_HOOKS.get(renderer) || !SUPPORTED_HOOKS.get(SectionCompiler.class)
            || !SUPPORTED_HOOKS.get(BufferBuilder.class) || !SUPPORTED_HOOKS.get(VertexConsumer.class)
            || !SUPPORTED_HOOKS.get(AltModelBlockRendererImpl.class) || !SUPPORTED_HOOKS.get(MutableQuadViewImpl.class))
        {
            return false;
        }
        final var parts = geometry.parts();
        for (int partIndex = 0; partIndex < parts.size(); partIndex++)
        {
            final var quads = parts.get(partIndex).quads();
            for (int quadIndex = 0; quadIndex < quads.size(); quadIndex++)
            {
                final var quad = quads.get(quadIndex);
                if (quad.material().sprite() == null || quad.material().itemRenderType() == null
                    || !quad.material().sprite().atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS))
                {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean hasSupportedHooks(final Class<?> type)
    {
        try
        {
            for (Class<?> current = type; current != null; current = current.getSuperclass())
            {
                for (final var method : current.getDeclaredMethods())
                {
                    final MixinMerged hook = method.getAnnotation(MixinMerged.class);
                    if (hook == null || SUPPORTED_MIXINS.contains(hook.mixin()))
                    {
                        continue;
                    }
                    final boolean benchmark = hook.mixin().equals("mod.chiselsandbits.fabric.mixin.client.SectionCompilerMixin")
                        && current.getName().equals(SectionCompiler.class.getName())
                        && Modifier.isPrivate(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())
                        && method.getReturnType() == void.class && method.getParameterCount() == 1
                        && method.getParameterTypes()[0] == CallbackInfoReturnable.class
                        && (method.getName().equals("chiselsandbits$onCompileStart")
                            || method.getName().equals("chiselsandbits$onCompileEnd")
                            || method.getName().endsWith("$chiselsandbits$onCompileStart")
                            || method.getName().endsWith("$chiselsandbits$onCompileEnd"));
                    if (!benchmark)
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
}
