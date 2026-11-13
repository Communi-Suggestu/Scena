package com.communi.suggestu.scena.mixin.terrain;

import java.nio.ByteBuffer;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainMeshAccess;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainSectionAccess;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainSectionState;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainShardHooks;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SectionRenderDispatcher.RenderSection.class)
public abstract class RenderSectionMixin implements TerrainSectionAccess
{
    @Shadow
    @Final
    private SectionRenderDispatcher this$0;

    @Unique
    private TerrainSectionState<CompiledSectionMesh> scena$pending;

    @Override
    public SectionRenderDispatcher scena$dispatcher()
    {
        return this$0;
    }

    @Override
    public TerrainSectionState<CompiledSectionMesh> scena$pending()
    {
        // RenderSection resets itself during construction, before mixin initializers are guaranteed to run.
        if (scena$pending == null)
        {
            scena$pending = new TerrainSectionState<>();
        }
        return scena$pending;
    }

    @Override
    @Invoker("vertexBufferUploadCallback")
    public abstract void scena$vertexUploaded(CompiledSectionMesh mesh, ChunkSectionLayer layer);

    @Override
    @Invoker("releaseSectionMesh")
    public abstract void scena$release(SectionMesh mesh);

    @Override
    @Invoker("cancelTasks")
    public abstract void scena$cancelTasks();

    @Inject(
        method = "addSectionBuffersToUberBuffer(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayer;Lnet/minecraft/client/renderer/chunk/CompiledSectionMesh;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)Z",
        at = @At("HEAD"), cancellable = true
    )
    private void scena$stage(final ChunkSectionLayer layer, final CompiledSectionMesh mesh,
                            final ByteBuffer vertices, final ByteBuffer indices, final CallbackInfoReturnable<Boolean> cir)
    {
        if (TerrainShardHooks.eligible(layer, vertices, indices) && TerrainShardHooks.needsShards(this, mesh, layer, vertices.remaining()))
        {
            cir.setReturnValue(TerrainShardHooks.stage(this, layer, mesh, vertices));
        }
    }

    @Inject(method = "cancelTasks()V", at = @At("HEAD"))
    private void scena$cancel(final CallbackInfo ci)
    {
        this$0.lock();
        try
        {
            scena$pending().cancel(this::scena$release);
        }
        finally
        {
            this$0.unlock();
        }
    }

    @Inject(method = "releaseSectionMesh(Lnet/minecraft/client/renderer/chunk/SectionMesh;)V", at = @At("HEAD"))
    private void scena$releaseChildren(final SectionMesh mesh, final CallbackInfo ci)
    {
        if (mesh instanceof TerrainMeshAccess access)
        {
            this$0.lock();
            try
            {
                access.scena$shards().release();
            }
            finally
            {
                this$0.unlock();
            }
        }
    }

    @Inject(
        method = "setSectionMesh(Lnet/minecraft/client/renderer/chunk/SectionMesh;)Lnet/minecraft/client/renderer/chunk/SectionMesh;",
        at = @At("HEAD")
    )
    private void scena$published(final SectionMesh mesh, final CallbackInfoReturnable<SectionMesh> cir)
    {
        if (mesh instanceof CompiledSectionMesh compiled)
        {
            this$0.lock();
            try
            {
                scena$pending().published(compiled);
            }
            finally
            {
                this$0.unlock();
            }
        }
    }
}
