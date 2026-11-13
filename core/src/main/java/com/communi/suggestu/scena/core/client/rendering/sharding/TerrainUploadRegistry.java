package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Dispatcher-local ownership; no world or section is retained in a static registry.
 */
public final class TerrainUploadRegistry
{
    private final Set<TerrainMeshState> meshes = new LinkedHashSet<>();
    private boolean closed;

    public void add(final TerrainMeshState mesh)
    {
        if (closed)
        {
            throw new IllegalStateException("Terrain dispatcher has been disposed");
        }
        meshes.add(mesh);
    }

    public void remove(final TerrainMeshState mesh)
    {
        meshes.remove(mesh);
    }

    public boolean isClosed()
    {
        return closed;
    }

    public void close()
    {
        closed = true;
        for (final TerrainMeshState mesh : new ArrayList<>(meshes))
        {
            mesh.owner().scena$cancelTasks();
            mesh.release();
        }
        meshes.clear();
    }
}
