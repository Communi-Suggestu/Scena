package com.communi.suggestu.scena.core.client.models.processing;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

public record ModelQuadLayer(VertexData[] vertexData,
                             @Nullable Direction cullDirection,
                             BakedQuad sourceQuad,
                             BakedQuad.MaterialInfo texture,
                             Material.Baked particleMaterial,
                             TriState usesAmbientOcclusion) {

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder extends BaseModelReader {
        private final Collection<VertexData> manualVertexData = new ArrayList<>();
        private final Collection<VertexData> vertexData = new ArrayList<>(4);
        private BakedQuad.MaterialInfo       material;
        @Nullable
        private Direction cullDirection;
        private       BakedQuad      sourceQuad;
        private final Material.Baked particleMaterial;
        private final TriState       usesAmbientOcclusion;
        private Builder(Material.Baked particleMaterial, TriState usesAmbientOcclusion) {
            this.particleMaterial = particleMaterial;
            this.usesAmbientOcclusion = usesAmbientOcclusion;
        }

        public static Builder create(Material.Baked particleSprite, TriState usesAmbientOcclusion) {
            return new Builder(particleSprite, usesAmbientOcclusion);
        }

        public Builder withVertexData(final Consumer<VertexData.Builder> vertexDataConsumer) {
            final VertexData.Builder vertexDataBuilder = VertexData.Builder.create();
            vertexDataConsumer.accept(vertexDataBuilder);
            manualVertexData.add(vertexDataBuilder.build());
            return this;
        }

        public Builder withMaterial(BakedQuad.MaterialInfo sprite) {
            this.material = sprite;
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
        public void cullDirection(@Nullable Direction orientation) {
            withCullDirection(orientation);
        }

        @Override
        public void texture(@NotNull BakedQuad.MaterialInfo texture) {
            withMaterial(texture);
        }

        public ModelQuadLayer build() {
            final BakedQuad sourceQuad = this.sourceQuad == null ? buildSourceQuad() : this.sourceQuad;

            Collection<VertexData> vertexData = !manualVertexData.isEmpty() ? manualVertexData : this.vertexData;
            vertexData = vertexData.stream().sorted(Comparator.comparing(VertexData::vertexIndex)).toList();

            return new ModelQuadLayer(vertexData.toArray(VertexData[]::new), cullDirection, sourceQuad, material, particleMaterial, usesAmbientOcclusion);
        }

        private BakedQuad buildSourceQuad() {
            if (manualVertexData.size() != 4) {
                throw new IllegalStateException("Cannot build a source quad without 4 vertex data");
            }

            final BakedQuadBuilder builder = new BakedQuadBuilder(material);
            builder.cullDirection(cullDirection);
            manualVertexData.stream().sorted(Comparator.comparing(VertexData::vertexIndex)).forEach(builder::vertex);
            builder.onComplete();
            return builder.build();
        }
    }
}