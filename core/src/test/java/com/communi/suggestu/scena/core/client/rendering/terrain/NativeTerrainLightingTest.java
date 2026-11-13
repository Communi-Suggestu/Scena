package com.communi.suggestu.scena.core.client.rendering.terrain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.SharedConstants;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class NativeTerrainLightingTest
{
    @Test
    void activeRendererProducesSameNativeBytesAsLegacyForFlatAndAoTintedQuads()
    {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        final BlockColors colors = new BlockColors();
        colors.register(List.of(BlockTintSources.constant(0xff4080c0)), Blocks.STONE, Blocks.SHORT_GRASS, Blocks.GLOWSTONE);
        for (final boolean ao : List.of(false, true))
        for (final BlockState state : List.of(
            Blocks.STONE.defaultBlockState(), Blocks.SHORT_GRASS.defaultBlockState(), Blocks.GLOWSTONE.defaultBlockState()))
        {
            final ModelBlockRenderer renderer = new ModelBlockRenderer(true, true, colors);
            final var model = model(ao);
            try (final ByteBufferBuilder nativeBytes = new ByteBufferBuilder(4096);
                 final ByteBufferBuilder legacyBytes = new ByteBufferBuilder(4096))
            {
                final BufferBuilder actual = new BufferBuilder(nativeBytes, PrimitiveTopology.QUADS, DefaultVertexFormat.BLOCK);
                final BufferBuilder expected = new BufferBuilder(legacyBytes, PrimitiveTopology.QUADS, DefaultVertexFormat.BLOCK);
                renderer.tesselateBlock(
                    (x, y, z, quad, instance) -> expected.putBlockBakedQuad(x, y, z, quad, instance),
                    2, 4, 6, BlockAndTintGetter.EMPTY, BlockPos.ZERO, state, model, 123
                );
                renderer.tesselateBlock(
                    new NativeTerrainQuadWriter(layer -> actual, false),
                    2, 4, 6, BlockAndTintGetter.EMPTY, BlockPos.ZERO, state, model, 123
                );
                try (final var mesh = actual.buildOrThrow(); final var reference = expected.buildOrThrow())
                {
                    assertEquals(reference.vertexBuffer(), mesh.vertexBuffer());
                }
            }
        }
    }

    @Test
    void nativeOutputRetainsRendererFaceCullingAndUnculledGroups()
    {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        final BlockColors colors = new BlockColors();
        colors.register(List.of(BlockTintSources.constant(-1)), Blocks.STONE);
        final BlockAndTintGetter solidNeighbors = new BlockAndTintGetter()
        {
            public CardinalLighting cardinalLighting() { return CardinalLighting.DEFAULT; }
            public LevelLightEngine getLightEngine() { return LevelLightEngine.EMPTY; }
            public int getBlockTint(final BlockPos pos, final ColorResolver resolver) { return -1; }
            public BlockEntity getBlockEntity(final BlockPos pos) { return null; }
            public BlockState getBlockState(final BlockPos pos) { return Blocks.STONE.defaultBlockState(); }
            public FluidState getFluidState(final BlockPos pos) { return Blocks.STONE.defaultBlockState().getFluidState(); }
            public int getHeight() { return 256; }
            public int getMinY() { return 0; }
        };
        try (final ByteBufferBuilder bytes = new ByteBufferBuilder(4096))
        {
            final BufferBuilder actual = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.BLOCK);
            new ModelBlockRenderer(true, true, colors).tesselateBlock(
                new NativeTerrainQuadWriter(layer -> actual, false),
                0, 0, 0, solidNeighbors, BlockPos.ZERO, Blocks.STONE.defaultBlockState(), model(true), 123
            );
            try (final var mesh = actual.buildOrThrow())
            {
                assertEquals(4, mesh.drawState().vertexCount());
            }
        }
    }

    private static BlockStateModel model(final boolean ao)
    {
        final BakedQuad quad = new BakedQuad(
            new Vector3f(0, 0, 1), new Vector3f(0, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 0, 1),
            0, 0, 0, 0, Direction.SOUTH, new BakedQuad.MaterialInfo(null, ChunkSectionLayer.SOLID, null, 0, true, 7)
        );
        final BlockStateModelPart part = new BlockStateModelPart()
        {
            public List<BakedQuad> getQuads(final Direction direction)
            {
                return direction == Direction.SOUTH || direction == null ? List.of(quad) : List.of();
            }

            public boolean useAmbientOcclusion() { return ao; }
            public Material.Baked particleMaterial() { return null; }
            public int materialFlags() { return 0; }
        };
        return new BlockStateModel()
        {
            public void collectParts(final RandomSource random, final List<BlockStateModelPart> output) { output.add(part); }
            public Material.Baked particleMaterial() { return null; }
            public int materialFlags() { return 0; }
        };
    }
}
