package com.communi.suggestu.scena.core.client.models;

import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

/**
 * A set of functionally equivalent shaders. One using {@link com.mojang.blaze3d.vertex.DefaultVertexFormat#BLOCK},
 * and the other two using {@link com.mojang.blaze3d.vertex.DefaultVertexFormat#NEW_ENTITY}.
 * {@code entityFabulous} may support custom render targets and other aspects of the fabulous pipeline, or can otherwise
 * be the same as {@code entity}.
 */
public record RenderTypeGroup(ChunkSectionLayer block, RenderType entity)
{
    public static RenderTypeGroup EMPTY = new RenderTypeGroup(null, null);

    public RenderTypeGroup
    {
        if ((block == null) != (entity == null))
            throw new IllegalArgumentException("The render types in a group must either be all null, or all non-null.");
    }

    /**
     * {@return true if this group has render types or not. It either has all, or none}
     */
    public boolean isEmpty()
    {
        // We throw an exception in the constructor if nullability doesn't match, so checking this is enough
        return block == null;
    }
}
