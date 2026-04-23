package com.communi.suggestu.scena.core.client.models.processing;

import com.communi.suggestu.scena.core.client.models.vertices.QuadProcessor;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

public record ModelQuadLayer(
    VertexData[] vertexData,
    @Nullable Direction cullDirection,
    BakedQuad sourceQuad,
    BakedQuad.MaterialInfo material)
{

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder implements QuadProcessor<Builder>
    {
        private final Collection<VertexData> manualVertexData = new ArrayList<>();
        private final Collection<VertexData> vertexData       = new ArrayList<>(4);
        private       BakedQuad.MaterialInfo material;
        @Nullable
        private       Direction              cullDirection;
        @Nullable
        private       BakedQuad              sourceQuad;

        private Builder() {}

        public static Builder create()
        {
            return new Builder();
        }

        public Builder sourceQuad(BakedQuad sourceQuad)
        {
            this.sourceQuad = sourceQuad;
            return this;
        }

        @Override
        public Builder from(final BakedQuad quad)
        {
            return sourceQuad(sourceQuad)
                .from(quad);
        }

        public Builder vertex(final Consumer<VertexData.Builder> vertexDataConsumer)
        {
            final VertexData.Builder vertexDataBuilder = VertexData.Builder.create();
            vertexDataConsumer.accept(vertexDataBuilder);
            manualVertexData.add(vertexDataBuilder.build());
            return this;
        }

        @Override
        public Builder vertex(final VertexData data)
        {
            this.vertexData.add(data);
            return this;
        }

        @Override
        public Builder cullDirection(@Nullable Direction orientation)
        {
            this.cullDirection = orientation;
            return this;
        }

        @Override
        public Builder material(@NotNull BakedQuad.MaterialInfo material)
        {
            this.material = material;
            return this;
        }

        public ModelQuadLayer build()
        {
            final BakedQuad sourceQuad = this.sourceQuad == null ? buildSourceQuad() : this.sourceQuad;

            Collection<VertexData> vertexData = !manualVertexData.isEmpty() ? manualVertexData : this.vertexData;
            vertexData = vertexData.stream().sorted(Comparator.comparing(VertexData::vertexIndex)).toList();

            return new ModelQuadLayer(vertexData.toArray(VertexData[]::new), cullDirection, sourceQuad, material);
        }

        private BakedQuad buildSourceQuad()
        {
            if (manualVertexData.size() != 4)
            {
                throw new IllegalStateException("Cannot build a source quad without 4 vertex data");
            }

            if (material == null) {
                throw new IllegalStateException("Can not build a source quad without a material provided!");
            }

            final BakedQuadBuilder builder = new BakedQuadBuilder(material);
            builder.cullDirection(cullDirection);
            manualVertexData.stream().sorted(Comparator.comparing(VertexData::vertexIndex)).forEach(builder::vertex);
            builder.complete();
            return builder.build();
        }
    }
}