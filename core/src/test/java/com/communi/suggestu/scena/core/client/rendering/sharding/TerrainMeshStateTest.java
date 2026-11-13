package com.communi.suggestu.scena.core.client.rendering.sharding;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerrainMeshStateTest
{
    @Test
    void cancellationBeforeFirstStagingInvalidatesCapturedCompileGeneration()
    {
        final TestSection section = new TestSection();
        final TerrainMeshState mesh = new TerrainMeshState();
        mesh.generation(section, section.pending.generation());
        assertTrue(mesh.isCurrent(section));
        assertFalse(mesh.isCurrent(new TestSection()));

        section.scena$cancelTasks();

        assertFalse(mesh.isCurrent(section));
        mesh.release();
        mesh.release();
        assertTrue(mesh.isReleased());
    }

    private static final class TestSection implements TerrainSectionAccess
    {
        private final TerrainSectionState<CompiledSectionMesh> pending = new TerrainSectionState<>();

        @Override
        public TerrainSectionState<CompiledSectionMesh> scena$pending()
        {
            return pending;
        }

        @Override
        public void scena$cancelTasks()
        {
            pending.cancel(this::scena$release);
        }

        @Override
        public SectionRenderDispatcher scena$dispatcher()
        {
            throw new AssertionError("An unstaged mesh must not access native buffers");
        }

        @Override
        public void scena$vertexUploaded(final CompiledSectionMesh mesh, final ChunkSectionLayer layer)
        {
            throw new AssertionError("An unstaged mesh must not publish");
        }

        @Override
        public void scena$release(final SectionMesh mesh)
        {
            throw new AssertionError("An unstaged mesh has no pending allocation");
        }
    }
}
