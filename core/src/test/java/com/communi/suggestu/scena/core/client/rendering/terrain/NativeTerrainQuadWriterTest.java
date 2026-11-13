package com.communi.suggestu.scena.core.client.rendering.terrain;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class NativeTerrainQuadWriterTest
{
    @Test
    void nativeBytesMatchLegacyIncludingTintRotatedUvsOffsetsAndEmission()
    {
        final BakedQuad quad = quad(ChunkSectionLayer.CUTOUT, 9);
        final QuadInstance instance = new QuadInstance();
        for (int vertex = 0; vertex < 4; vertex++)
        {
            instance.setColor(vertex, 0xffc08040 - vertex * 0x00101010);
            instance.setLightCoords(vertex, 0x00b00020 + vertex * 0x00100010);
        }
        instance.multiplyColor(0xff2080c0);
        try (final ByteBufferBuilder raw = new ByteBufferBuilder(1024);
             final ByteBufferBuilder legacy = new ByteBufferBuilder(1024))
        {
            final BufferBuilder actual = builder(raw);
            final BufferBuilder expected = builder(legacy);
            final List<ChunkSectionLayer> layers = new ArrayList<>();
            new NativeTerrainQuadWriter(layer -> {
                layers.add(layer);
                return actual;
            }, false).put(12.25f, 4.5f, 9.75f, quad, instance);
            expected.putBlockBakedQuad(12.25f, 4.5f, 9.75f, quad, instance);
            try (final var mesh = actual.buildOrThrow(); final var reference = expected.buildOrThrow())
            {
                assertEquals(List.of(ChunkSectionLayer.CUTOUT), layers);
                assertEquals(reference.vertexBuffer(), mesh.vertexBuffer());
                assertEquals(4, mesh.drawState().vertexCount());
                final var bytes = mesh.vertexBuffer().order(ByteOrder.nativeOrder());
                for (int vertex = 0; vertex < 4; vertex++)
                {
                    final int offset = vertex * DefaultVertexFormat.BLOCK.getVertexSize();
                    assertEquals(12.25f + quad.position(vertex).x(), bytes.getFloat(offset));
                    assertEquals(4.5f + quad.position(vertex).y(), bytes.getFloat(offset + 4));
                    assertEquals(9.75f + quad.position(vertex).z(), bytes.getFloat(offset + 8));
                    assertEquals(UVPair.unpackU(quad.packedUV(vertex)), bytes.getFloat(offset + 16));
                    assertEquals(UVPair.unpackV(quad.packedUV(vertex)), bytes.getFloat(offset + 20));
                    assertEquals(
                        LightCoordsUtil.lightCoordsWithEmission(instance.getLightCoords(vertex), 7),
                        bytes.getInt(offset + 24)
                    );
                }
            }
        }
    }

    @Test
    void reacquiresBuilderAtEveryQuadBoundaryAndHonorsOpaqueOverride()
    {
        try (final ByteBufferBuilder first = new ByteBufferBuilder(1024);
             final ByteBufferBuilder second = new ByteBufferBuilder(1024))
        {
            final List<BufferBuilder> builders = List.of(builder(first), builder(second));
            final AtomicInteger calls = new AtomicInteger();
            final NativeTerrainQuadWriter writer = new NativeTerrainQuadWriter(layer -> {
                assertEquals(ChunkSectionLayer.SOLID, layer);
                return builders.get(calls.getAndIncrement());
            }, true);
            writer.put(0, 0, 0, quad(ChunkSectionLayer.CUTOUT, -1), new QuadInstance());
            writer.put(0, 0, 0, quad(ChunkSectionLayer.SOLID, -1), new QuadInstance());
            assertEquals(2, calls.get());
            for (final BufferBuilder builder : builders)
            {
                try (final var mesh = builder.buildOrThrow())
                {
                    assertEquals(4, mesh.drawState().vertexCount());
                }
            }
        }
    }

    @Test
    void routesTranslucentQuadsToTheSectionLayerForNativeSorting()
    {
        final AtomicInteger calls = new AtomicInteger();
        final List<ChunkSectionLayer> layers = new ArrayList<>();
        try (final ByteBufferBuilder bytes = new ByteBufferBuilder(1024))
        {
            final BufferBuilder builder = builder(bytes);
            final NativeTerrainQuadWriter writer = new NativeTerrainQuadWriter(layer -> {
                calls.incrementAndGet();
                layers.add(layer);
                return builder;
            }, true);
            writer.put(0, 0, 0, quad(ChunkSectionLayer.TRANSLUCENT, -1), new QuadInstance());
            assertEquals(List.of(ChunkSectionLayer.SOLID), layers, "The explicit opaque override must remain authoritative");
        }

        try (final ByteBufferBuilder bytes = new ByteBufferBuilder(1024))
        {
            final BufferBuilder builder = builder(bytes);
            final NativeTerrainQuadWriter writer = new NativeTerrainQuadWriter(layer -> {
                calls.incrementAndGet();
                layers.add(layer);
                return builder;
            }, false);
            writer.put(0, 0, 0, quad(ChunkSectionLayer.TRANSLUCENT, -1), new QuadInstance());
            assertEquals(ChunkSectionLayer.TRANSLUCENT, layers.getLast(),
                "The SectionCompiler sorts all vertices written to its translucent layer after compilation");
        }
        assertEquals(2, calls.get());
    }

    @Test
    void clippedTrianglesPreserveOriginalLitAttributesWithinVertexQuantization()
    {
        final BakedQuad quad = quad(ChunkSectionLayer.TRANSLUCENT, 9);
        final TerrainQuad source = new TerrainQuad(
            terrainVertex(quad, 0), terrainVertex(quad, 1), terrainVertex(quad, 2), terrainVertex(quad, 3),
            quad.direction(), null, quad.materialInfo(), 9
        );
        final TerrainQuad bounds = new TerrainQuad(
            new TerrainVertex(.3f, .6f, .4f, 0, 0), new TerrainVertex(.3f, .2f, .4f, 0, 0),
            new TerrainVertex(.8f, .2f, .4f, 0, 0), new TerrainVertex(.8f, .6f, .4f, 0, 0),
            quad.direction(), null, quad.materialInfo(), 9
        );
        final var triangles = FaceTriangleClipper.clip(source, bounds);
        assertTrue(triangles.size() > 2);
        final QuadInstance instance = new QuadInstance();
        final int[] colors = {0xff1a4060, 0xe090d010, 0xc03070e0, 0x80e02090};
        final int[] lights = {0x00500020, 0x008000a0, 0x00e000d0, 0x00b00070};
        for (int i = 0; i < 4; i++)
        {
            instance.setColor(i, colors[i]);
            instance.setLightCoords(i, lights[i]);
            lights[i] = instance.getLightCoordsWithEmission(i, quad.materialInfo().lightEmission());
        }
        try (final ByteBufferBuilder bytes = new ByteBufferBuilder(4096))
        {
            final BufferBuilder builder = builder(bytes);
            final List<ChunkSectionLayer> layers = new ArrayList<>();
            new NativeTerrainQuadWriter(layer -> {
                layers.add(layer);
                return builder;
            }, false).putClipped(2, 4, 6, quad, instance, triangles);
            try (final var mesh = builder.buildOrThrow())
            {
                assertEquals(triangles.size() * 4, mesh.drawState().vertexCount());
                assertEquals(triangles.size(), layers.size());
                assertTrue(layers.stream().allMatch(layer -> layer == ChunkSectionLayer.TRANSLUCENT));
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
                        assertEquals(vertex.position().u(), buffer.getFloat(offset + 16));
                        assertEquals(vertex.position().v(), buffer.getFloat(offset + 20));
                        final int[] shifts = {16, 8, 0, 24};
                        for (int component = 0; component < shifts.length; component++)
                        {
                            final int shift = shifts[component];
                            final float expected = interpolate(vertex, colors, shift, 0xff);
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

    @Test
    void interpolatedAttributesCannotInGeneralPreserveTheVanillaVertexShaderResult()
    {
                        final BakedQuad base = quad(ChunkSectionLayer.TRANSLUCENT, -1);
                        final BakedQuad quad = new BakedQuad(base.position0(), base.position1(), base.position2(), base.position3(),
                            base.packedUV0(), base.packedUV1(), base.packedUV2(), base.packedUV3(), base.direction(),
                            new BakedQuad.MaterialInfo(null, ChunkSectionLayer.TRANSLUCENT, null, -1, true, 0));
                        final QuadInstance instance = new QuadInstance();
                        instance.setColor(0, 0xff000000);
                        instance.setColor(1, 0xffffffff);
                        instance.setLightCoords(0, 0);
                        instance.setLightCoords(1, 0x00f000f0);
                        final var middle = new FaceTriangleClipper.Vertex(new TerrainVertex(.2f, .45f, .4f, 0, 0),
                            .5f, .5f, 0, 0);
                        final var v1 = new FaceTriangleClipper.Vertex(terrainVertex(quad, 1), 0, 1, 0, 0);
                        final var v2 = new FaceTriangleClipper.Vertex(terrainVertex(quad, 2), 0, 0, 1, 0);
                        try (final ByteBufferBuilder bytes = new ByteBufferBuilder(1024))
                        {
                            final BufferBuilder builder = builder(bytes);
                            new NativeTerrainQuadWriter(layer -> builder, false).putClipped(0, 0, 0, quad, instance,
                                List.of(new FaceTriangleClipper.Triangle(middle, v1, v2)));
                            try (final var mesh = builder.buildOrThrow())
                            {
                                final var buffer = mesh.vertexBuffer().order(ByteOrder.nativeOrder());
                                final float color = (buffer.get(12) & 0xff) / 255f;
                                final float linearlySampledLight = (buffer.getShort(24) & 0xffff) / 240f;
                                final float actualVertexShaderColor = color * linearlySampledLight;
                                final float originalTriangleColor = .5f * (0f * 0f) + .5f * (1f * 1f);
                                assertTrue(Math.abs(actualVertexShaderColor - originalTriangleColor) > .2f,
                                    "Interpolating color and light separately changes their product even for a linear lightmap");
            }
        }
    }

    private static float interpolate(final FaceTriangleClipper.Vertex vertex, final int[] values, final int shift, final int mask)
    {
        return vertex.w0() * ((values[0] >>> shift) & mask) + vertex.w1() * ((values[1] >>> shift) & mask)
            + vertex.w2() * ((values[2] >>> shift) & mask) + vertex.w3() * ((values[3] >>> shift) & mask);
    }

    private static TerrainVertex terrainVertex(final BakedQuad quad, final int index)
    {
        final var position = quad.position(index);
        return new TerrainVertex(position.x(), position.y(), position.z(),
            UVPair.unpackU(quad.packedUV(index)), UVPair.unpackV(quad.packedUV(index)));
    }

    private static BufferBuilder builder(final ByteBufferBuilder bytes)
    {
        return new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.BLOCK);
    }

    private static BakedQuad quad(final ChunkSectionLayer layer, final int tint)
    {
        return new BakedQuad(
            new Vector3f(.2f, .8f, .4f), new Vector3f(.2f, .1f, .4f),
            new Vector3f(.9f, .1f, .4f), new Vector3f(.9f, .8f, .4f),
            UVPair.pack(.75f, .25f), UVPair.pack(.25f, .25f),
            UVPair.pack(.25f, .75f), UVPair.pack(.75f, .75f),
            Direction.NORTH, new BakedQuad.MaterialInfo(null, layer, null, tint, true, 7)
        );
    }
}
