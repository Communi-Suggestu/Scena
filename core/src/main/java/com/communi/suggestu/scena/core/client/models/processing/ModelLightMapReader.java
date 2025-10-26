package com.communi.suggestu.scena.core.client.models.processing;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.jetbrains.annotations.NotNull;

public class ModelLightMapReader extends BaseModelReader {
    private int lv = 0;
    private VertexFormat format = DefaultVertexFormat.BLOCK;
    private boolean hasLightMap = false;

    public ModelLightMapReader() {
    }

    public static int block(int p_109884_) {
        return (p_109884_ & 0xFFFF) >> 4; // From-Forge: Fix fullbright quads showing dark artifacts. Reported as MC-169806
    }

    public static int sky(int p_109895_) {
        return p_109895_ >> 20 & '\uffff';
    }

    public static int pack(int p_109886_, int p_109887_) {
        return p_109886_ << 4 | p_109887_ << 20;
    }

    public int getLv() {
        return lv;
    }

    @NotNull
    @Override
    public VertexFormat getVertexFormat() {
        return format;
    }

    public void setVertexFormat(
            VertexFormat format) {
        hasLightMap = false;

        int eCount = format.getElements().size();
        for (int x = 0; x < eCount; x++) {
            VertexFormatElement e = format.getElements().get(x);
            if (e.usage() == VertexFormatElement.Usage.UV && e.index() == 2 && e.type() == VertexFormatElement.Type.SHORT) {
                hasLightMap = true;
            }
        }

        this.format = format;
    }

    @Override
    public void put(
            final int vertNum,
            final int element,
            final float @NotNull ... data) {
        final VertexFormatElement e = getVertexFormat().getElements().get(element);

        if (e.usage() == VertexFormatElement.Usage.UV && e.index() == 2 && e.type() == VertexFormatElement.Type.SHORT && data.length >= 2 && hasLightMap) {
            final float maxLightmap = 32.0f / 0xffff;
            final int lvFromData_sky = sky((int) (data[0] / maxLightmap));
            final int lvFromData_block = block((int) (data[1] / maxLightmap) & 0xf);

            lv = pack(lvFromData_block, lvFromData_sky);
        }
    }
}