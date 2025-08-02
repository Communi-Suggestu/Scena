package com.communi.suggestu.scena.core.entity.block;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Set;
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
            return new BlockEntityType<>(builder::create, Set.of(blocks));
        };
    }
}
