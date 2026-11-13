package com.communi.suggestu.scena.fabric.platform.client.rendering;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

class NativeTerrainCompatibilityTest
{
    private static final String COMPILER = "net/minecraft/client/renderer/chunk/SectionCompiler";
    private static final String CALLBACK = "(Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V";
    private static final String BENCHMARK = "mod.chiselsandbits.fabric.mixin.client.SectionCompilerMixin";

    @Test
    void acceptsOnlyReviewedCompilerHooksAndBenchmarkCallbacks()
    {
        assertTrue(NativeTerrainCompatibility.hasSupportedHooks(hook(COMPILER,
            "net.fabricmc.fabric.mixin.client.renderer.block.render.SectionCompilerMixin", "redirect", "()V", Opcodes.ACC_PRIVATE)));
        assertTrue(NativeTerrainCompatibility.hasSupportedHooks(hook(COMPILER, BENCHMARK,
            "handler$abc000$chiselsandbits$onCompileStart", CALLBACK, Opcodes.ACC_PRIVATE)));
        assertTrue(NativeTerrainCompatibility.hasSupportedHooks(hook(COMPILER, BENCHMARK,
            "chiselsandbits$onCompileEnd", CALLBACK, Opcodes.ACC_PRIVATE)));
        assertFalse(NativeTerrainCompatibility.hasSupportedHooks(hook(COMPILER, BENCHMARK,
            "changeOutput", CALLBACK, Opcodes.ACC_PRIVATE)));
        assertFalse(NativeTerrainCompatibility.hasSupportedHooks(hook(COMPILER, BENCHMARK,
            "chiselsandbits$onCompileStart", "()V", Opcodes.ACC_PRIVATE)));
        assertFalse(NativeTerrainCompatibility.hasSupportedHooks(hook(COMPILER, BENCHMARK,
            "chiselsandbits$onCompileStart", CALLBACK, Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)));
        assertFalse(NativeTerrainCompatibility.hasSupportedHooks(hook("test/OtherRenderer", BENCHMARK,
            "chiselsandbits$onCompileStart", CALLBACK, Opcodes.ACC_PRIVATE)));
        assertFalse(NativeTerrainCompatibility.hasSupportedHooks(hook(COMPILER,
            "thirdparty.RendererMixin", "alterOutput", "()V", Opcodes.ACC_PRIVATE)));
    }

    private static Class<?> hook(
        final String target, final String mixin, final String name, final String descriptor, final int modifiers
    )
    {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V17, Opcodes.ACC_PUBLIC, target, null, "java/lang/Object", null);
        final var method = writer.visitMethod(modifiers, name, descriptor, null, null);
        final var annotation = method.visitAnnotation("Lorg/spongepowered/asm/mixin/transformer/meta/MixinMerged;", true);
        annotation.visit("mixin", mixin);
        annotation.visit("priority", 1000);
        annotation.visitEnd();
        method.visitCode();
        method.visitInsn(Opcodes.RETURN);
        method.visitMaxs(0, 2);
        method.visitEnd();
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
}
