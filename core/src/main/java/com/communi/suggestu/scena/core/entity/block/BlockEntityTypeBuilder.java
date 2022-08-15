package com.communi.suggestu.scena.core.entity.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.Supplier;

public final class BlockEntityTypeBuilder<T extends BlockEntity>
{
    private final BlockEntityBuilder<T> builder;
    private final List<Supplier<Block>> validBlockSuppliers;

    public BlockEntityTypeBuilder(final BlockEntityBuilder<T> builder) {
        this.builder = builder;
        this.validBlockSuppliers = Lists.newArrayList();
    }

    public BlockEntityTypeBuilder<T> withValidBlock(final Supplier<Block> blockSupplier) {
        this.validBlockSuppliers.add(blockSupplier);
        return this;
    }

    @SuppressWarnings("ConstantConditions")
    public Supplier<BlockEntityType<T>> build() {
        return () -> {
            final Block[] blocks = this.validBlockSuppliers.stream().map(Supplier::get).toArray(Block[]::new);
            return BlockEntityType.Builder.of(this.builder::create, blocks).build(null);
        };
    }
}
