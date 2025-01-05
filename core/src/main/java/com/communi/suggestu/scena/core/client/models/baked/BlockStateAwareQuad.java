package com.communi.suggestu.scena.core.client.models.baked;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Represents a baked quad that is aware of the block state it is part of.
 */
public class BlockStateAwareQuad extends BakedQuad {

    private final BlockState blockState;

    public BlockStateAwareQuad(int[] vertexData, int tintIndex, Direction direction, TextureAtlasSprite sprite, boolean shade, BlockState blockState) {
        super(vertexData, tintIndex, direction, sprite, shade);
        this.blockState = blockState;
    }

    public BlockStateAwareQuad(BakedQuad quad, BlockState blockState) {
        super(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        return blockState;
    }
}
