package com.communi.suggestu.scena.core.client.rendering;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface ExtendedBlockStateModelPart
{
    /**
     * {@return the blockstate which describes the appearance of this part the best. Null if not available. Used by shader mods (Iris) to handle shader specific material properties}
     */
    @Nullable
    BlockState getBlockAppearance();

    /**
     * Controls the AO behavior for all quads of this model. The default behavior is to use AO unless the block emits light,
     * {@link TriState#TRUE} and {@link TriState#FALSE} force AO to be enabled and disabled respectively, regardless of
     * the block emitting light or not.
     * <p>
     * This method cannot force AO if the global smooth lighting video setting is disabled.
     *
     * @return {@link TriState#TRUE} to force-enable AO, {@link TriState#FALSE} to force-disable AO or {@link TriState#DEFAULT} to use vanilla AO behavior
     */
    default TriState ambientOcclusion() {
        return ((BlockStateModelPart) this).useAmbientOcclusion() ? TriState.DEFAULT : TriState.FALSE;
    }

}
