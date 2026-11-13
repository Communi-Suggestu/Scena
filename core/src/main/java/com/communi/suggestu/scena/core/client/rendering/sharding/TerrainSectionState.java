package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Accessed only under the dispatcher's copy lock. Published meshes leave the pending set.
 */
public final class TerrainSectionState<M>
{
    private long generation;
    private final Set<M> pending = new LinkedHashSet<>();

    public long generation()
    {
        return generation;
    }

    public void add(final M mesh)
    {
        pending.add(mesh);
    }

    public void published(final M mesh)
    {
        pending.remove(mesh);
    }

    public void cancel(final Consumer<M> release)
    {
        generation++;
        for (final M mesh : new ArrayList<>(pending))
        {
            release.accept(mesh);
        }
        pending.clear();
    }
}
