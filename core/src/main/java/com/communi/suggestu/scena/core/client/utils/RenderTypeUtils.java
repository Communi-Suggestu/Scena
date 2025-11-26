package com.communi.suggestu.scena.core.client.utils;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.jetbrains.annotations.Nullable;

public class RenderTypeUtils
{
    public static @Nullable RenderType renderTypeFor(@Nullable final ChunkSectionLayer chunkSectionLayer)
    {
        if (chunkSectionLayer == null)
            return null;

        return switch (chunkSectionLayer)
        {
            case SOLID -> RenderTypes.solidMovingBlock();
            case CUTOUT -> RenderTypes.cutoutMovingBlock();
            case TRANSLUCENT -> RenderTypes.translucentMovingBlock();
            case TRIPWIRE -> RenderTypes.tripwireMovingBlock();
        };
    }
}
