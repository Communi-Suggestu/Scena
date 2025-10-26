package com.communi.suggestu.scena.core.client.models.processing;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ModelVertexDataReader extends BaseModelReader {

    private int vertexIndex = 0;
    private VertexData.Builder vertexDataBuilder = VertexData.Builder.create();

    private final Collection<VertexData> vertexData = new ArrayList<>();

    public ModelVertexDataReader() {
    }

    public Collection<VertexData> getVertexData() {
        return vertexData;
    }

    @Override
    public void put(
            final int vertexIndex,
            final int element,
            final float @NotNull ... data) {
        final VertexFormat format = getVertexFormat();
        final VertexFormatElement ele = format.getElements().get(element);

        if (vertexIndex != this.vertexIndex) {
            this.vertexIndex = vertexIndex;
            vertexData.add(vertexDataBuilder.build());
            vertexDataBuilder = VertexData.Builder.create();
        }

        if (ele.usage() == VertexFormatElement.Usage.UV && ele.index() == 0) {
            vertexDataBuilder.withU(data[0]);
            vertexDataBuilder.withV(data[1]);
        } else if (ele.usage() == VertexFormatElement.Usage.POSITION) {
            vertexDataBuilder.withX(data[0]);
            vertexDataBuilder.withY(data[1]);
            vertexDataBuilder.withZ(data[2]);
        }

        vertexDataBuilder.withVertexIndex(vertexIndex);
    }

    @Override
    public void onComplete() {
        vertexData.add(vertexDataBuilder.build());
        vertexDataBuilder = VertexData.Builder.create();
    }
}