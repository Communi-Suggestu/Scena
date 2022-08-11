package com.communi.suggestu.scena.core.client.rendering.type;

import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * Manager for handling the different render types which are available to
 * fluids, items and blocks.
 */
public interface IRenderTypeManager
{

    /**
     * The current render type manager.
     * Deals with the render types which are available on different platforms.
     *
     * @return The render type manager.
     */
    static IRenderTypeManager getInstance() {
        return IRenderingManager.getInstance().getRenderTypeManager();
    }

    /**
     * Indicates if the blockstate needs to be rendered in the render type.
     *
     * @param blockState The block state in question.
     * @param renderType The render type.
     * @return True when rendering in the given render type is required, false when not.
     */
    boolean canRenderInType(final BlockState blockState, final RenderType renderType);

    /**
     * Indicates if the fluidState needs to be rendered in the render type.
     *
     * @param fluidState The fluid state in question.
     * @param renderType The render type.
     * @return True when rendering in the given render type is required, false when not.
     */
    boolean canRenderInType(final FluidState fluidState, final RenderType renderType);
}
