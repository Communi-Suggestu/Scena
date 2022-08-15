package com.communi.suggestu.scena.core.entity.block;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Manager which handles block entities.
 */
public interface IBlockEntityManager
{

    /**
     * The current instance of the block entity manager for the current platform.
     *
     * @return The current instance of the block entity manager.
     */
    static IBlockEntityManager getInstance() {
        return IScenaPlatform.getInstance().getBlockEntityManager();
    }

    /**
     * Creates a new block entity type for the given block.
     *
     * @param configurator The configurator for the block entity type builder
     * @return The block entity type.
     * @param <T> The type of the block entity.
     */
    default <T extends BlockEntity> Supplier<BlockEntityType<T>> createType(final BlockEntityBuilder<T> blockEntityBuilder, Consumer<BlockEntityTypeBuilder<T>> configurator) {
        final BlockEntityTypeBuilder<T> builder = new BlockEntityTypeBuilder<>(blockEntityBuilder);
        configurator.accept(builder);
        return builder.build();
    }
}
