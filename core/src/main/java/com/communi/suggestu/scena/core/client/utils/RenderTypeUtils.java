package com.communi.suggestu.scena.core.client.utils;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import org.jetbrains.annotations.Nullable;

public class RenderTypeUtils
{
    public static @Nullable RenderType renderTypeFor(@Nullable final ChunkSectionLayer chunkSectionLayer)
    {
        if (chunkSectionLayer == null)
            return null;

        return switch (chunkSectionLayer)
        {
            case SOLID -> RenderType.solid();
            case CUTOUT_MIPPED -> RenderType.cutoutMipped();
            case CUTOUT -> RenderType.cutout();
            case TRANSLUCENT -> RenderType.translucentMovingBlock();
            case TRIPWIRE -> RenderType.tripwire();
        };
    }
}
