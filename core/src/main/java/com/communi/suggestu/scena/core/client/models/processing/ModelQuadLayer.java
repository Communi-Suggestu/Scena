package com.communi.suggestu.scena.core.client.models.processing;

import com.communi.suggestu.scena.core.client.utils.RenderTypeUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

public record ModelQuadLayer(VertexData[] vertexData,
                             TextureAtlasSprite sprite,
                             int light,
                             int color,
                             int tint,
                             boolean shade,
                             @Nullable Direction cullDirection,
                             BakedQuad sourceQuad,
                             TextureAtlasSprite particleSprite,
                             TriState usesAmbientOcclusion,
                             @Nullable RenderType renderType,
                             @Nullable ChunkSectionLayer chunkSectionLayer) {

    public ModelQuadLayer withColor(final int color) {
        return new ModelQuadLayer(
            vertexData,
            sprite,
            light,
            color,
            tint,
            shade,
            cullDirection,
            sourceQuad,
            particleSprite,
            usesAmbientOcclusion,
            renderType,
            chunkSectionLayer
        );
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder extends BaseModelReader {
        private final BlockState             blockState;
        private final Collection<VertexData> manualVertexData = new ArrayList<>();
        private final ModelLightMapReader lightValueExtractor;
        private final ModelVertexDataReader uvExtractor;
        private TextureAtlasSprite sprite;
        private int light;
        private int     color     = -1;
        private int     tintIndex = -1;
        private boolean shade;
        @Nullable
        private Direction cullDirection;
        private       BakedQuad          sourceQuad;
        private final TextureAtlasSprite particleSprite;
        private final TriState           usesAmbientOcclusion;
        @Nullable
        private       RenderType         renderType = null;
        @Nullable
        private ChunkSectionLayer chunkSectionLayer = null;

        private Builder(final BlockState blockState, TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion) {
            this.lightValueExtractor = new ModelLightMapReader();
            this.uvExtractor = new ModelVertexDataReader();
            this.blockState = blockState;
            this.particleSprite = particleSprite;
            this.usesAmbientOcclusion = usesAmbientOcclusion;
        }

        private Builder(final BlockState blockState, TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion, @Nullable RenderType renderType) {
            this.lightValueExtractor = new ModelLightMapReader();
            this.uvExtractor = new ModelVertexDataReader();
            this.blockState = blockState;
            this.particleSprite = particleSprite;
            this.usesAmbientOcclusion = usesAmbientOcclusion;
            this.renderType = renderType;
        }

        private Builder(final BlockState blockState, TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion, @Nullable ChunkSectionLayer chunkSectionLayer) {
            this.lightValueExtractor = new ModelLightMapReader();
            this.uvExtractor = new ModelVertexDataReader();
            this.blockState = blockState;
            this.particleSprite = particleSprite;
            this.usesAmbientOcclusion = usesAmbientOcclusion;
            this.chunkSectionLayer = chunkSectionLayer;
            this.renderType = RenderTypeUtils.renderTypeFor(chunkSectionLayer);
        }

        public static Builder create(final BlockState blockState, TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion) {
            return new Builder(blockState, particleSprite, usesAmbientOcclusion);
        }

        public static Builder create(final BlockState blockState, TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion, @Nullable RenderType renderType) {
            return new Builder(blockState, particleSprite, usesAmbientOcclusion, renderType);
        }

        public static Builder create(final BlockState blockState, TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion, ChunkSectionLayer chunkSectionLayer) {
            return new Builder(blockState, particleSprite, usesAmbientOcclusion, chunkSectionLayer);
        }

        public Builder withVertexData(final Consumer<VertexData.Builder> vertexDataConsumer) {
            final VertexData.Builder vertexDataBuilder = VertexData.Builder.create();
            vertexDataConsumer.accept(vertexDataBuilder);
            manualVertexData.add(vertexDataBuilder.build());
            return this;
        }

        public Builder withSprite(TextureAtlasSprite sprite) {
            this.sprite = sprite;
            return this;
        }

        public Builder withLight(int light) {
            this.light = light;
            return this;
        }

        public Builder withTintIndex(int tintIndex) {
            this.tintIndex = tintIndex;
            return this;
        }

        public Builder withShade(boolean shade) {
            this.shade = shade;
            return this;
        }

        public Builder withSourceQuad(BakedQuad sourceQuad) {
            this.sourceQuad = sourceQuad;
            return this;
        }

        private Builder withCullDirection(Direction orientation) {
            this.cullDirection = orientation;
            return this;
        }

        @Override
        public void put(final int vertexIndex,
                        final int element,
                        final float @NotNull ... data) {
            uvExtractor.put(vertexIndex, element, data);
            lightValueExtractor.put(vertexIndex, element, data);
        }

        @Override
        public void setQuadTint(int tint) {
            withTintIndex(tint);
        }

        @Override
        public void setApplyDiffuseLighting(boolean diffuse) {
            withShade(diffuse);
        }

        @Override
        public void setQuadOrientation(@NotNull Direction orientation) {
            withCullDirection(orientation);
        }

        @Override
        public void setTexture(@NotNull TextureAtlasSprite texture) {
            withSprite(texture);
        }

        @Override
        public void setLightEmission(final int lightEmission)
        {
            withLight(lightEmission);
        }

        @Override
        public void onComplete() {
            uvExtractor.onComplete();
            lightValueExtractor.onComplete();
        }

        public ModelQuadLayer build() {
            light = Math.max(this.light, lightValueExtractor.getLv());

            final BakedQuad sourceQuad = this.sourceQuad == null ? buildSourceQuad() : this.sourceQuad;

            final Collection<VertexData> vertexData =
                !manualVertexData.isEmpty() ? manualVertexData.stream().sorted(Comparator.comparing(VertexData::vertexIndex)).toList() : uvExtractor.getVertexData();
            return new ModelQuadLayer(vertexData.toArray(VertexData[]::new), sprite, light, color, tintIndex, shade, cullDirection, sourceQuad, particleSprite, usesAmbientOcclusion, renderType, chunkSectionLayer);
        }

        private BakedQuad buildSourceQuad() {
            if (manualVertexData.size() != 4) {
                throw new IllegalStateException("Cannot build a source quad without 4 vertex data");
            }

            final VertexData[] verticesData = manualVertexData.stream().sorted(Comparator.comparing(VertexData::vertexIndex)).toArray(VertexData[]::new);

            final BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
            builder.setQuadOrientation(cullDirection);
            builder.setQuadTint(tintIndex);

            for (int vertexIndex = 0; vertexIndex < verticesData.length; vertexIndex++) {
                final VertexData vertexData = verticesData[vertexIndex];

                for (int elementIndex = 0; elementIndex < DefaultVertexFormat.BLOCK.getElements().size(); elementIndex++) {
                    final VertexFormatElement element = DefaultVertexFormat.BLOCK.getElements().get(elementIndex);
                    switch (element.usage()) {
                        case POSITION:
                            builder.put(vertexIndex, elementIndex, vertexData.positionData());
                            break;
                        case COLOR:
                            builder.put(vertexIndex, elementIndex, 1f, 1f, 1f, 1f);
                            break;
                        case NORMAL:
                            builder.put(vertexIndex, elementIndex, cullDirection.getStepX(), cullDirection.getStepY(), cullDirection.getStepZ());
                            break;
                        case UV:
                            if (element.index() == 0) {
                                builder.put(vertexIndex, elementIndex, vertexData.uvData());
                            } else if (element.index() == 1) {
                                builder.put(vertexIndex, elementIndex, 0, 0);
                            } else {
                                builder.put(vertexIndex, elementIndex, 0, 0);
                            }
                            break;
                        default:
                            builder.put(vertexIndex, elementIndex);
                            break;
                    }
                }
            }

            builder.setApplyDiffuseLighting(true);

            builder.onComplete();
            return builder.build();
        }
    }
}