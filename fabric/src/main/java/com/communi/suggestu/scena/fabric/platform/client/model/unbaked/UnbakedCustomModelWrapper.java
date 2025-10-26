package com.communi.suggestu.scena.fabric.platform.client.model.unbaked;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import org.jetbrains.annotations.NotNull;

public record UnbakedCustomModelWrapper<T extends BlockStateModel.Unbaked>(
    MapCodec<T> mapCodec,
    T model
) implements CustomUnbakedBlockStateModel
{
    @Override
    public @NotNull MapCodec<UnbakedCustomModelWrapper<T>> codec()
    {
        return mapCodec().xmap(
            unbaked -> this,
            customUnbakedBlockStateModel -> model()
        );
    }

    @Override
    public @NotNull BlockStateModel bake(final @NotNull ModelBaker baker)
    {
        return model().bake(baker);
    }

    @Override
    public void resolveDependencies(final @NotNull ResolvableModel.Resolver resolver)
    {
        model().resolveDependencies(resolver);
    }
}
