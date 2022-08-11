package com.communi.suggestu.scena.core.client.models;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IDataAwareBakedModel extends BakedModel {


    @NotNull List<BakedQuad> getQuads(
            @Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull IBlockModelData extraData);
}
