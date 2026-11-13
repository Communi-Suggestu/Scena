package com.communi.suggestu.scena.forge.platform.client.rendering;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

class NativeTerrainCompatibilityTest
{
    @Test
    void rejectsForeignRendererHooksButPermitsItsOwnShardingAndGeometryHooks()
    {
        assertFalse(NativeTerrainCompatibility.hasOnlySupportedHooks(withHooks("thirdparty.shader.ModelBlockRendererMixin")));
        assertTrue(NativeTerrainCompatibility.hasOnlySupportedHooks(withHooks(
            "com.communi.suggestu.scena.mixin.terrain.CpuSectionCompilerMixin",
            "com.communi.suggestu.scena.forge.mixin.platform.client.TerrainGeometrySectionCompilerMixin"
        )));
    }

    @Test
    void rejectsUnknownRendererInterfacesEvenWithoutMergedMethods()
    {
        assertFalse(NativeTerrainCompatibility.hasOnlySupportedHooks(ForeignVertexWriter.class));
    }

    @Test
    void acceptsOnlyReviewedCbBenchmarkCallbacksOnTheSectionCompiler()
    {
        final String mixin = "mod.chiselsandbits.forge.mixin.client.SectionCompilerMixin";
        final String compiler = "net/minecraft/client/renderer/chunk/SectionCompiler";
        final String callback = "(Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V";
        final Hook start = new Hook(mixin, "handler$abc000$chiselsandbits$onCompileStart", callback);
        final Hook end = new Hook(mixin, "handler$abc000$chiselsandbits$onCompileEnd", callback);
        assertTrue(NativeTerrainCompatibility.hasOnlySupportedHooks(withNamedHooks(compiler, start, end)));
        assertFalse(NativeTerrainCompatibility.hasOnlySupportedHooks(withNamedHooks(
            compiler, start, new Hook(mixin, "changeGeometry", callback)
        )));
        assertFalse(NativeTerrainCompatibility.hasOnlySupportedHooks(withNamedHooks(
            compiler, new Hook(mixin, start.name(), "()V")
        )));
        assertFalse(NativeTerrainCompatibility.hasOnlySupportedHooks(withNamedHooks(
            "net/minecraft/client/renderer/block/ModelBlockRenderer", start, end
        )));
        assertFalse(NativeTerrainCompatibility.hasOnlySupportedHooks(withNamedHooks(
            compiler, start, end, new Hook("thirdparty.CompilerMixin", "alterOutput", "()V")
        )));
    }

    private interface ForeignVertexContract
    {
    }

    private static final class ForeignVertexWriter implements ForeignVertexContract
    {
    }

    private static Class<?> withHooks(final String... mixins)
    {
        final Hook[] hooks = new Hook[mixins.length];
        for (int i = 0; i < mixins.length; i++)
        {
            hooks[i] = new Hook(mixins[i], "hook" + i, "()V");
        }
        return withNamedHooks("test/RendererHooks", hooks);
    }

    private static Class<?> withNamedHooks(final String target, final Hook... hooks)
    {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V17, Opcodes.ACC_PUBLIC, target, null, "java/lang/Object", null);
        for (final Hook hook : hooks)
        {
            final var method = writer.visitMethod(Opcodes.ACC_PRIVATE, hook.name(), hook.descriptor(), null, null);
            final var annotation = method.visitAnnotation("Lorg/spongepowered/asm/mixin/transformer/meta/MixinMerged;", true);
            annotation.visit("mixin", hook.mixin());
            annotation.visit("priority", 1000);
            annotation.visitEnd();
            method.visitCode();
            method.visitInsn(Opcodes.RETURN);
            method.visitMaxs(0, hook.descriptor().equals("()V") ? 1 : 2);
            method.visitEnd();
        }
        writer.visitEnd();
        final byte[] bytes = writer.toByteArray();
        return new ClassLoader(NativeTerrainCompatibilityTest.class.getClassLoader())
        {
            Class<?> define()
            {
                return defineClass(null, bytes, 0, bytes.length);
            }
        }.define();
    }

    private record Hook(String mixin, String name, String descriptor)
    {
    }
}
