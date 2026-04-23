package com.communi.suggestu.scena.core.client.models.processing;

import com.communi.suggestu.scena.core.client.models.vertices.QuadProcessor;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

public record DeconstructedModelPartComponent(
    ProtoStateModelPart part,
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
        private       BakedQuad              quad;
        @Nullable
        private       ProtoStateModelPart    part;
        @Nullable
        private       Integer                tintIndex        = null;

        private Builder() {}

        public static Builder create()
        {
            return new Builder();
        }

        public static Builder create(ProtoStateModelPart part, BakedQuad quad)
        {
            return create().part(part)
                .from(quad);
        }

        public static Builder create(BlockStateModelPart part, BakedQuad quad)
        {
            return create().part(new ProtoStateModelPart(part))
                .from(quad);
        }

        public Builder part(ProtoStateModelPart part)
        {
            this.part = part;
            return this;
        }

        @Override
        public Builder from(final BakedQuad quad)
        {
            this.quad = quad;
            return QuadProcessor.super.from(quad);
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

        public Builder tintIndex(int tintIndex)
        {
            this.tintIndex = tintIndex;
            return this;
        }

        public DeconstructedModelPartComponent build()
        {
            if (this.material == null)
            {
                if (this.quad == null)
                {
                    throw new IllegalStateException("Either a material, or a source quad has to be provided!");
                }

                this.material = this.quad.materialInfo();
            }

            if (this.tintIndex != null)
            {
                this.material = new BakedQuad.MaterialInfo(
                    this.material.sprite(),
                    this.material.layer(),
                    this.material.itemRenderType(),
                    tintIndex,
                    this.material.shade(),
                    this.material.lightEmission()
                );
            }

            final BakedQuad sourceQuad = this.quad == null ? buildSourceQuad() : this.quad;

            Collection<VertexData> vertexData = !manualVertexData.isEmpty() ? manualVertexData : this.vertexData;
            vertexData = vertexData.stream().sorted(Comparator.comparing(VertexData::vertexIndex)).toList();

            return new DeconstructedModelPartComponent(part, vertexData.toArray(VertexData[]::new), cullDirection, sourceQuad, material);
        }

        private BakedQuad buildSourceQuad()
        {
            if (manualVertexData.size() != 4)
            {
                throw new IllegalStateException("Cannot build a source quad without 4 vertex data");
            }

            if (material == null)
            {
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