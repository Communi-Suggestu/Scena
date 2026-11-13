package com.communi.suggestu.scena.fabric.platform.client.rendering;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

import com.communi.suggestu.scena.core.client.rendering.terrain.*;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.ShadeMode;
import net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class ResolvedTerrainModelTest
{
    @Test
    void directEmitterMatchesBakedAdaptationIncludingUvsTintMaterialAndEmission()
    {
        try (final SpriteContents contents = new SpriteContents(
            Identifier.fromNamespaceAndPath("scena", "test"), new FrameSize(2, 2), new NativeImage(2, 2, false));
             final ByteBufferBuilder directBytes = new ByteBufferBuilder(1024);
             final ByteBufferBuilder bakedBytes = new ByteBufferBuilder(1024))
        {
            final TextureAtlasSprite sprite = new TextureAtlasSprite(TextureAtlas.LOCATION_BLOCKS, contents, 2, 2, 0, 0, 0) {};
            final var material = new BakedQuad.MaterialInfo(sprite, ChunkSectionLayer.CUTOUT, Sheets.cutoutBlockItemSheet(), 42, false, 7);
            final TerrainQuad quad = new TerrainQuad(
                new TerrainVertex(.2f, .1f, .5f, .75f, .25f), new TerrainVertex(.9f, .1f, .5f, .25f, .25f),
                new TerrainVertex(.9f, .8f, .5f, .25f, .75f), new TerrainVertex(.2f, .8f, .5f, .75f, .75f),
                Direction.SOUTH, Direction.EAST, material, 3
            );
            final var geometry = new TerrainGeometry(List.of(new TerrainGeometryPart(TriState.TRUE, null, 0, List.of(quad))));
            final BufferBuilder actual = new BufferBuilder(directBytes, PrimitiveTopology.QUADS, DefaultVertexFormat.BLOCK);
            final BufferBuilder expected = new BufferBuilder(bakedBytes, PrimitiveTopology.QUADS, DefaultVertexFormat.BLOCK);
            final AtomicInteger emitted = new AtomicInteger();
            final var directEmitter = IndigoRenderer.INSTANCE.quadEmitter(emittedQuad -> {
                assertEquals(3, emittedQuad.tintIndex());
                assertEquals(net.fabricmc.fabric.api.util.TriState.TRUE, emittedQuad.ambientOcclusion());
                assertEquals(Direction.EAST, emittedQuad.cullFace());
                assertEquals(Direction.SOUTH, emittedQuad.nominalFace());
                assertFalse(emittedQuad.diffuseShade());
                assertEquals(ChunkSectionLayer.CUTOUT, emittedQuad.chunkLayer());
                emittedQuad.buffer(OverlayTexture.NO_OVERLAY, actual);
                emitted.incrementAndGet();
            });
            new ResolvedTerrainModel(geometry, false).emitQuads(
                directEmitter, BlockAndTintGetter.EMPTY, BlockPos.ZERO, null, RandomSource.create(123), direction -> false
            );
            final var bakedEmitter = IndigoRenderer.INSTANCE.quadEmitter(emittedQuad -> emittedQuad.buffer(OverlayTexture.NO_OVERLAY, expected));
            bakedEmitter.cullFace(Direction.EAST);
            bakedEmitter.fromBakedQuad(new BakedQuad(
                new Vector3f(.2f, .1f, .5f), new Vector3f(.9f, .1f, .5f),
                new Vector3f(.9f, .8f, .5f), new Vector3f(.2f, .8f, .5f),
                UVPair.pack(.75f, .25f), UVPair.pack(.25f, .25f), UVPair.pack(.25f, .75f), UVPair.pack(.75f, .75f),
                Direction.SOUTH, new BakedQuad.MaterialInfo(sprite, ChunkSectionLayer.CUTOUT, Sheets.cutoutBlockItemSheet(), 3, false, 7)
            ));
            bakedEmitter.ambientOcclusion(net.fabricmc.fabric.api.util.TriState.TRUE);
            bakedEmitter.shadeMode(ShadeMode.VANILLA);
            bakedEmitter.emit();
            try (final var mesh = actual.buildOrThrow(); final var reference = expected.buildOrThrow())
            {
                assertEquals(reference.vertexBuffer(), mesh.vertexBuffer());
            }
            assertEquals(1, emitted.get());
            new ResolvedTerrainModel(geometry, false).emitQuads(
                directEmitter, BlockAndTintGetter.EMPTY, BlockPos.ZERO, null, RandomSource.create(123), direction -> true
            );
            assertEquals(1, emitted.get());
        }
    }

    @Test
    void animatedTranslucentSpritesKeepTheirAtlasFlagsAndLayer()
    {
            try (final SpriteContents contents = new SpriteContents(
                Identifier.fromNamespaceAndPath("scena", "animated_test"), new FrameSize(2, 2), new NativeImage(4, 2, false),
                java.util.Optional.of(new AnimationMetadataSection(
                    java.util.Optional.of(List.of(new AnimationFrame(0, java.util.Optional.empty()),
                        new AnimationFrame(1, java.util.Optional.empty()))),
                    java.util.Optional.of(2), java.util.Optional.of(2), 1, false
                )),
                List.of(),
                java.util.Optional.empty()
            ))
            {
                final TextureAtlasSprite sprite = new TextureAtlasSprite(TextureAtlas.LOCATION_BLOCKS, contents, 2, 2, 0, 0, 0) {};
                final var material = new BakedQuad.MaterialInfo(
                    sprite, ChunkSectionLayer.TRANSLUCENT, Sheets.translucentBlockItemSheet(), -1, true, 0
                );
                final TerrainQuad quad = new TerrainQuad(
                    new TerrainVertex(.2f, .1f, .5f, .75f, .25f), new TerrainVertex(.9f, .1f, .5f, .25f, .25f),
                    new TerrainVertex(.9f, .8f, .5f, .25f, .75f), new TerrainVertex(.2f, .8f, .5f, .75f, .75f),
                    Direction.SOUTH, null, material, -1
                );
                final var geometry = new TerrainGeometry(List.of(new TerrainGeometryPart(
                    TriState.DEFAULT, null, BakedQuad.FLAG_TRANSLUCENT | BakedQuad.FLAG_ANIMATED, List.of(quad)
                )));
                assertTrue(TerrainGeometryValidation.supports(geometry));
                final AtomicInteger emitted = new AtomicInteger();
                final var emitter = IndigoRenderer.INSTANCE.quadEmitter(emittedQuad -> {
                    assertEquals(ChunkSectionLayer.TRANSLUCENT, emittedQuad.chunkLayer());
                    assertTrue(emittedQuad.animated(), "Indigo receives the existing animated atlas sprite flag");
                    emitted.incrementAndGet();
                });
                new ResolvedTerrainModel(geometry, false).emitQuads(
                    emitter, BlockAndTintGetter.EMPTY, BlockPos.ZERO, null, RandomSource.create(123), direction -> false
                );
                assertEquals(1, emitted.get());
        }
    }

        @Test
        void clippedOutputUsesPostTransformLightingAndKeepsTranslucentLayer()
        {
            try (final SpriteContents contents = new SpriteContents(
                Identifier.fromNamespaceAndPath("scena", "clip_test"), new FrameSize(2, 2), new NativeImage(2, 2, false));
                 final ByteBufferBuilder bytes = new ByteBufferBuilder(4096))
            {
                final TextureAtlasSprite sprite = new TextureAtlasSprite(TextureAtlas.LOCATION_BLOCKS, contents, 2, 2, 0, 0, 0) {};
                final var material = new BakedQuad.MaterialInfo(
                    sprite, ChunkSectionLayer.TRANSLUCENT, Sheets.translucentBlockItemSheet(), -1, true, 0
                );
                final TerrainQuad region = rectangle(.25f, .25f, .75f, .75f, material);
                final TerrainQuad original = rectangle(0, 0, 1, 1, material);
                final TerrainQuad clipped = new TerrainQuad(original.v0(), original.v1(), original.v2(), original.v3(),
                    original.face(), original.cullFace(), material, -1, List.of(region));
                final var model = new ResolvedTerrainModel(new TerrainGeometry(List.of(
                    new TerrainGeometryPart(TriState.DEFAULT, null, BakedQuad.FLAG_TRANSLUCENT, List.of(clipped))
                )), false);
                assertFalse(NativeTerrainCompatibility.supports(new TerrainGeometry(List.of(
                    new TerrainGeometryPart(TriState.DEFAULT, null, BakedQuad.FLAG_TRANSLUCENT, List.of(clipped))
                ))), "The current terrain shader cannot preserve arbitrary clipped lighting");
                final BufferBuilder builder = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.BLOCK);
                final var output = new ClippedTerrainOutput(model, layer -> {
                    assertEquals(ChunkSectionLayer.TRANSLUCENT, layer);
                    return builder;
                }, 2, 4, 6);
                final var emitter = IndigoRenderer.INSTANCE.quadEmitter(output::accept);
                final int[] colors = {0xff1a4060, 0xe090d010, 0xc03070e0, 0x80e02090};
                final int[] lights = {0x00500020, 0x008000a0, 0x00e000d0, 0x00b00070};
                emitter.pushTransform(quad -> {
                    for (int i = 0; i < 4; i++)
                    {
                        quad.color(i, colors[i]);
                        quad.lightmap(i, lights[i]);
                    }
                    quad.translate(2, 4, 6);
                    return true;
                });
                model.emitQuads(emitter, BlockAndTintGetter.EMPTY, BlockPos.ZERO, null,
                    RandomSource.create(123), direction -> false);
                emitter.popTransform();
                final var triangles = FaceTriangleClipper.clip(original, region);
                try (final var mesh = builder.buildOrThrow())
                {
                    assertEquals(triangles.size() * 4, mesh.drawState().vertexCount());
                    final var buffer = mesh.vertexBuffer().order(ByteOrder.nativeOrder());
                    for (int t = 0; t < triangles.size(); t++)
                    {
                        final var triangle = triangles.get(t);
                        final FaceTriangleClipper.Vertex[] vertices = {triangle.v0(), triangle.v1(), triangle.v2(), triangle.v2()};
                        for (int v = 0; v < 4; v++)
                        {
                            final var vertex = vertices[v];
                            final int offset = (t * 4 + v) * DefaultVertexFormat.BLOCK.getVertexSize();
                            assertEquals(vertex.position().x() + 2, buffer.getFloat(offset));
                            assertEquals(vertex.position().y() + 4, buffer.getFloat(offset + 4));
                            assertEquals(vertex.position().z() + 6, buffer.getFloat(offset + 8));
                            final int[] shifts = {16, 8, 0, 24};
                            for (int component = 0; component < shifts.length; component++)
                            {
                                final float expected = interpolate(vertex, colors, shifts[component], 0xff);
                                assertTrue(Math.abs((buffer.get(offset + 12 + component) & 0xff) - expected) <= .501f);
                            }
                            for (int component = 0; component < 2; component++)
                            {
                                final float expected = interpolate(vertex, lights, component * 16, 0xffff);
                                assertTrue(Math.abs((buffer.getShort(offset + 24 + component * 2) & 0xffff) - expected) <= .501f);
                            }
                        }
                    }
                }
            }
        }

        private static float interpolate(final FaceTriangleClipper.Vertex vertex, final int[] values, final int shift, final int mask)
        {
            return vertex.w0() * ((values[0] >>> shift) & mask) + vertex.w1() * ((values[1] >>> shift) & mask)
                + vertex.w2() * ((values[2] >>> shift) & mask) + vertex.w3() * ((values[3] >>> shift) & mask);
        }

        private static TerrainQuad rectangle(
            final float minX, final float minY, final float maxX, final float maxY, final BakedQuad.MaterialInfo material
        )
        {
            return new TerrainQuad(
                new TerrainVertex(minX, minY, .5f, minX, minY), new TerrainVertex(maxX, minY, .5f, maxX, minY),
                new TerrainVertex(maxX, maxY, .5f, maxX, maxY), new TerrainVertex(minX, maxY, .5f, minX, maxY),
                Direction.SOUTH, Direction.SOUTH, material, -1
            );
        }
}
