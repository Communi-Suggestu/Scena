package com.communi.suggestu.scena.core.client.rendering.sharding;

/**
 * Experimental opt-in for terrain sharding. Disabled unless the JVM is started with
 * {@code -Dscena.experimentalTerrainSharding=true}.
 */
public final class TerrainShardGate {

    public static final String PROPERTY = "scena.experimentalTerrainSharding";

    private static final boolean ENABLED = Boolean.getBoolean(PROPERTY);

    private TerrainShardGate() {
    }

    public static boolean isEnabled() {
        return ENABLED;
    }
}
