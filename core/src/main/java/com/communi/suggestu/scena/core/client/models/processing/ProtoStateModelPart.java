package com.communi.suggestu.scena.core.client.models.processing;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.TriState;

/**
 * Represents the prototype state of a {@link net.minecraft.client.renderer.block.dispatch.BlockStateModelPart}
 * with information that we need to further process and manipulate the quads inside the part.
 *
 * @param ambientOcclusion The ambient occlusion state.
 * @param particleMaterial The material of the particle that spawns from this part.
 * @param materialFlags The material flags for the part, indicates for example if this is a partially transparent part or not.
 */
public record ProtoStateModelPart(TriState ambientOcclusion,
                                  Material.Baked particleMaterial,
                                  @BakedQuad.MaterialFlags int materialFlags)
{
    /**
     * Converts a vanilla {@link BlockStateModelPart part} to a prototype with its information.
     *
     * @param part The part to convert.
     */
    public ProtoStateModelPart(final BlockStateModelPart part)
    {
        this(TriState.from(part.useAmbientOcclusion()), part.particleMaterial(), part.materialFlags());
    }
}
