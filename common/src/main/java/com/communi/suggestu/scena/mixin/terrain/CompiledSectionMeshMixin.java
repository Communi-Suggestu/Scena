package com.communi.suggestu.scena.mixin.terrain;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainMeshAccess;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainMeshState;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainCpuResultAccess;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainCompileGeneration;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.TranslucencyPointOfView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CompiledSectionMesh.class)
public abstract class CompiledSectionMeshMixin implements TerrainMeshAccess
{
    @Unique
    private final TerrainMeshState scena$shards = new TerrainMeshState();

    @Override
    public TerrainMeshState scena$shards()
    {
        return scena$shards;
    }

    @Inject(
        method = "<init>(Lnet/minecraft/client/renderer/chunk/TranslucencyPointOfView;Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;)V",
        at = @At("RETURN")
    )
    private void scena$cpuOwnership(final TranslucencyPointOfView view, final SectionCompiler.Results results, final CallbackInfo ci)
    {
        scena$shards.cpuBatch(((TerrainCpuResultAccess) (Object) results).scena$cpuBatch());
        TerrainCompileGeneration.attach(scena$shards);
    }
}
