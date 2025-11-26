package com.communi.suggestu.scena.core.client.models.processing;

import com.communi.suggestu.scena.core.client.utils.RenderTypeUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

public record ModelQuadLayer(VertexData[] vertexData,
                             TextureAtlasSprite sprite,
                             int light,
                             int tint,
                             boolean shade,
                             @Nullable Direction cullDirection,
                             BakedQuad sourceQuad,
                             TextureAtlasSprite particleSprite,
                             TriState usesAmbientOcclusion,
                             @Nullable RenderType renderType,
                             @Nullable ChunkSectionLayer chunkSectionLayer) {

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder extends BaseModelReader {
        private final Collection<VertexData> manualVertexData = new ArrayList<>();
        private final Collection<VertexData> vertexData = new ArrayList<>(4);
        private TextureAtlasSprite sprite;
        private int light;
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

        private Builder(TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion) {
            this.particleSprite = particleSprite;
            this.usesAmbientOcclusion = usesAmbientOcclusion;
        }

        private Builder(TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion, @Nullable RenderType renderType) {
            this.particleSprite = particleSprite;
            this.usesAmbientOcclusion = usesAmbientOcclusion;
            this.renderType = renderType;
        }

        private Builder(TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion, @Nullable ChunkSectionLayer chunkSectionLayer) {
            this.particleSprite = particleSprite;
            this.usesAmbientOcclusion = usesAmbientOcclusion;
            this.chunkSectionLayer = chunkSectionLayer;
            this.renderType = RenderTypeUtils.renderTypeFor(chunkSectionLayer);
        }

        public static Builder create(TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion) {
            return new Builder(particleSprite, usesAmbientOcclusion);
        }

        public static Builder create(TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion, @Nullable RenderType renderType) {
            return new Builder(particleSprite, usesAmbientOcclusion, renderType);
        }

        public static Builder create(TextureAtlasSprite particleSprite, TriState usesAmbientOcclusion, ChunkSectionLayer chunkSectionLayer) {
            return new Builder(particleSprite, usesAmbientOcclusion, chunkSectionLayer);
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
        public void vertex(final VertexData data)
        {
            this.vertexData.add(data);
        }

        @Override
        public void tintIndex(int tint) {
            withTintIndex(tint);
        }

        @Override
        public void shade(boolean diffuse) {
            withShade(diffuse);
        }

        @Override
        public void cullDirection(@Nullable Direction orientation) {
            withCullDirection(orientation);
        }

        @Override
        public void texture(@NotNull TextureAtlasSprite texture) {
            withSprite(texture);
        }

        @Override
        public void light(final int lightEmission)
        {
            withLight(lightEmission);
        }

        public ModelQuadLayer build() {
            final BakedQuad sourceQuad = this.sourceQuad == null ? buildSourceQuad() : this.sourceQuad;

            Collection<VertexData> vertexData = !manualVertexData.isEmpty() ? manualVertexData : this.vertexData;
            vertexData = vertexData.stream().sorted(Comparator.comparing(VertexData::vertexIndex)).toList();
            return new ModelQuadLayer(vertexData.toArray(VertexData[]::new), sprite, light, tintIndex, shade, cullDirection, sourceQuad, particleSprite, usesAmbientOcclusion, renderType, chunkSectionLayer);
        }

        private BakedQuad buildSourceQuad() {
            if (manualVertexData.size() != 4) {
                throw new IllegalStateException("Cannot build a source quad without 4 vertex data");
            }

            final BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
            builder.cullDirection(cullDirection);
            builder.tintIndex(tintIndex);
            builder.shade(true);
            manualVertexData.forEach(builder::vertex);
            builder.onComplete();
            return builder.build();
        }
    }
}