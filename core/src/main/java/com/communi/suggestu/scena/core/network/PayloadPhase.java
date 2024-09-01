package com.communi.suggestu.scena.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Consumer;

public final class PayloadPhase<T extends FriendlyByteBuf> {

    public static PayloadPhase<FriendlyByteBuf> CONFIG = new PayloadPhase<>();
    public static PayloadPhase<RegistryFriendlyByteBuf> PLAY = new PayloadPhase<>();

    private PayloadPhase() {
        throw new IllegalStateException("PayloadPhase should not be instantiated");
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == this.getClass();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "PayloadPhase[]";
    }

}
