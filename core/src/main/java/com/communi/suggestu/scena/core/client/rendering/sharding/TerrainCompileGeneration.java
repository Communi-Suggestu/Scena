package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.util.function.Supplier;

public final class TerrainCompileGeneration
{
    private record Token(TerrainSectionAccess owner, long generation)
    {
    }

    private static final ThreadLocal<Token> CURRENT = new ThreadLocal<>();

    private TerrainCompileGeneration()
    {
    }

    public static <T> T run(final TerrainSectionAccess section, final Supplier<T> operation)
    {
        if (!TerrainShardGate.isEnabled())
        {
            return operation.get();
        }
        section.scena$dispatcher().lock();
        try
        {
            CURRENT.set(new Token(section, section.scena$pending().generation()));
        }
        finally
        {
            section.scena$dispatcher().unlock();
        }
        try
        {
            return operation.get();
        }
        finally
        {
            CURRENT.remove();
        }
    }

    public static void attach(final TerrainMeshState state)
    {
        final Token token = CURRENT.get();
        if (token != null)
        {
            state.generation(token.owner(), token.generation());
        }
    }
}
