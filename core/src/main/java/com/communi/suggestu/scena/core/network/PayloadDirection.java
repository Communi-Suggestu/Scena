package com.communi.suggestu.scena.core.network;

public enum PayloadDirection {
    SERVERBOUND,
    CLIENTBOUND,
    BOTH;

    public boolean requiresClientHandler() {
        return this == CLIENTBOUND || this == BOTH;
    }
}
