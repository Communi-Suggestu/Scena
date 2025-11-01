package com.communi.suggestu.scena.fabric.platform.client.model.unbaked;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record UnbakedCustomModelWrapper<T extends BlockStateModel.Unbaked>(
    MapCodec<T> mapCodec,
    @Nullable
    T model
) implements CustomUnbakedBlockStateModel
{
    public UnbakedCustomModelWrapper(final MapCodec<T> mapCodec)
    {
        this(mapCodec, null);
    }

    @Override
    public @NotNull MapCodec<UnbakedCustomModelWrapper<?>> codec()
    {
        return mapCodec().xmap(
            unbaked -> new UnbakedCustomModelWrapper<>(mapCodec(), unbaked),
            customUnbakedBlockStateModel -> model()
        );
    }

    @Override
    public @NotNull BlockStateModel bake(final @NotNull ModelBaker baker)
    {
        if (model() == null)
            throw new IllegalStateException("Data registration mode can not be used for baking!");

        return model().bake(baker);
    }

    @Override
    public void resolveDependencies(final @NotNull ResolvableModel.Resolver resolver)
    {
        if (model() == null)
            throw new IllegalStateException("Data registration mode can not be used for baking!");

        model().resolveDependencies(resolver);
    }
}
