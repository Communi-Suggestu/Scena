package com.communi.suggestu.scena.core.client.models.processing;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class BakedQuadAdapter extends BakedQuadBuilder {

    private final Int2ObjectMap<VertexData> indexToVertexData = new Int2ObjectOpenHashMap<>();
    private final int colorOverride;

    public BakedQuadAdapter(Collection<VertexData> adaptionData, int colorOverride) {
        super();
        adaptionData.forEach(data -> indexToVertexData.put(data.vertexIndex(), data));
        this.colorOverride = colorOverride;
    }

    @Override
    public void put(final int vertexIndex,
                    final int elementIndex,
                    final float @NotNull ... data) {
        if (indexToVertexData.containsKey(vertexIndex)) {
            final VertexFormat format = getVertexFormat();
            final VertexFormatElement element = format.getElements().get(elementIndex);

            if (element == VertexFormatElement.POSITION) {
                final VertexData vertexData = indexToVertexData.get(vertexIndex);
                final float[] positionData = vertexData.positionData();
                super.put(vertexIndex, elementIndex, positionData);
            } else if (element.usage() == VertexFormatElement.Usage.UV && element.index() == 0) {
                final VertexData vertexData = indexToVertexData.get(vertexIndex);
                final float[] uvData = vertexData.uvData();
                super.put(vertexIndex, elementIndex, uvData);
            } else if (element.usage() == VertexFormatElement.Usage.COLOR && this.colorOverride != -1) {
                final float[] colorData = new float[4];
                colorData[0] = ((this.colorOverride >> 16) & 0xFF) / 255.0F;
                colorData[1] = ((this.colorOverride >> 8) & 0xFF) / 255.0F;
                colorData[2] = (this.colorOverride & 0xFF) / 255.0F;
                colorData[3] = ((this.colorOverride >> 24) & 0xFF) / 255.0F;
                super.put(vertexIndex, elementIndex, colorData);
            } else {
                super.put(vertexIndex, elementIndex, data);
            }
        }
        else
        {
            super.put(vertexIndex, elementIndex, data);
        }
    }
}
