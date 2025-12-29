package com.communi.suggestu.scena.forge.mixin.compat.shaders.sodium;

import com.communi.suggestu.scena.forge.compat.shaders.sodium.BuffersAccessor;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockRenderer.class)
public interface BlockRendererAccessor extends BuffersAccessor
{
    @Accessor
    ChunkBuildBuffers getBuffers();
}