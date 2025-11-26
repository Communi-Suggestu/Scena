package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;

public interface IRegisterItemModelEvent extends IEvent
{
    void handle(Registrar registrar);

    interface Registrar {
        <T extends ItemModel.Unbaked> void registerModel(Identifier location, MapCodec<T> codec);
    }
}
