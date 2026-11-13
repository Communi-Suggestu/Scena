package com.communi.suggestu.scena.fabric.platform.client.rendering;

import java.util.List;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.function.Predicate;

import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainGeometry;
import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainQuad;
import com.communi.suggestu.scena.core.client.rendering.terrain.FaceTriangleClipper;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.ShadeMode;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Direct FRAPI vertex emission; the compiler's existing alternate renderer owns lighting, tint, offset and output.
 */
public final class ResolvedTerrainModel implements BlockStateModel, FabricBlockStateModel
{
    private final TerrainGeometry geometry;
    private final boolean forceOpaque;
    private final Map<TerrainQuad, List<FaceTriangleClipper.Triangle>> clips;
    private TerrainQuad emitting;

    public ResolvedTerrainModel(final TerrainGeometry geometry, final boolean forceOpaque)
    {
        this.geometry = geometry;
        this.forceOpaque = forceOpaque;
        Map<TerrainQuad, List<FaceTriangleClipper.Triangle>> clips = null;
        for (final var part : geometry.parts())
        {
            for (final TerrainQuad quad : part.quads())
            {
                if (quad.clipRegions() != null)
                {
                    if (clips == null)
                    {
                        clips = new IdentityHashMap<>();
                    }
                    clips.put(quad, quad.clipRegions().stream()
                        .flatMap(region -> FaceTriangleClipper.clip(quad, region).stream()).toList());
                }
            }
        }
        this.clips = clips;
    }

    @Override
    public void emitQuads(
        final QuadEmitter emitter, final BlockAndTintGetter level, final BlockPos pos, final BlockState state,
        final RandomSource random, final Predicate<Direction> cullTest
    )
    {
        for (final var part : geometry.parts())
        {
            final net.fabricmc.fabric.api.util.TriState ao = switch (part.ambientOcclusion())
            {
                case TRUE -> net.fabricmc.fabric.api.util.TriState.TRUE;
                case FALSE -> net.fabricmc.fabric.api.util.TriState.FALSE;
                case DEFAULT -> net.fabricmc.fabric.api.util.TriState.DEFAULT;
            };
            for (final TerrainQuad quad : part.quads())
            {
                if (cullTest.test(quad.cullFace()))
                {
                    continue;
                }
                emitting = quad;
                try
                {
                    emit(emitter, quad, ao, forceOpaque);
                }
                finally
                {
                    emitting = null;
                }
            }

        }
    }

    public TerrainQuad emittingQuad()
    {
        return emitting;
    }

    public List<FaceTriangleClipper.Triangle> clippedTriangles(final TerrainQuad quad)
    {
        return clips == null ? null : clips.get(quad);
    }

    static void emit(
        final QuadEmitter emitter, final TerrainQuad quad, final net.fabricmc.fabric.api.util.TriState ao,
        final boolean forceOpaque
    )
    {
        emitter.clear();
        emitter.cullFace(quad.cullFace());
        emitter.nominalFace(quad.face());
        emitter.atlas(QuadAtlas.BLOCK);
        emitter.chunkLayer(forceOpaque ? net.minecraft.client.renderer.chunk.ChunkSectionLayer.SOLID : quad.material().layer());
        emitter.itemRenderType(quad.material().itemRenderType());
        emitter.animated(quad.material().sprite().contents().isAnimated());
        emitter.tintIndex(quad.tintIndex());
        emitter.diffuseShade(quad.material().shade());
        emitter.emissive(quad.material().lightEmission() == 15);
        emitter.ambientOcclusion(ao);
        emitter.shadeMode(ShadeMode.VANILLA);
        final int light = LightCoordsUtil.pack(quad.material().lightEmission(), quad.material().lightEmission());
        for (int vertex = 0; vertex < 4; vertex++)
        {
            final var data = quad.vertex(vertex);
            emitter.pos(vertex, data.x(), data.y(), data.z());
            emitter.uv(vertex, data.u(), data.v());
            emitter.color(vertex, -1);
            emitter.lightmap(vertex, light);
        }
        emitter.emit();
    }

    public void collectParts(final RandomSource random, final List<BlockStateModelPart> output)
    {
        throw new IllegalStateException("Resolved Fabric geometry requires the native FRAPI renderer");
    }

    public Material.Baked particleMaterial() { return null; }
    public int materialFlags() { return geometry.parts().stream().mapToInt(part -> part.materialFlags()).reduce(0, (a, b) -> a | b); }
}
