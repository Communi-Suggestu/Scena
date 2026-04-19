package com.communi.suggestu.scena.core.client.utils;

import com.communi.suggestu.scena.core.client.models.processing.VertexData;
import net.minecraft.client.resources.model.geometry.BakedQuad;

import java.util.function.Consumer;

public class BakedQuadUtils
{
    private BakedQuadUtils() {

    }

    public static void forEachVertex(BakedQuad quad, Consumer<VertexData> forEach) {
        for (int i = 0; i < 4; i++)
        {
            forEach.accept(VertexData.from(quad, i));
        }
    }
}
