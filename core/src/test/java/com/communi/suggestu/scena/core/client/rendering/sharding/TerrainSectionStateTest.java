package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TerrainSectionStateTest
{
    @Test
    void supersedingCompileReleasesPendingButKeepsPublishedMesh()
    {
        final TerrainSectionState<String> state = new TerrainSectionState<>();
        final List<String> released = new ArrayList<>();
        state.add("visible");
        state.published("visible");
        state.add("staged replacement");
        final long oldGeneration = state.generation();

        state.cancel(released::add);

        assertEquals(List.of("staged replacement"), released);
        assertNotEquals(oldGeneration, state.generation());
        state.add("new replacement");
        state.published("new replacement");
        state.cancel(released::add);
        assertEquals(List.of("staged replacement"), released);
    }

    @Test
    void releaseCanRemoveItselfAndRepeatedCancellationDoesNotReleaseTwice()
    {
        final TerrainSectionState<String> state = new TerrainSectionState<>();
        final List<String> released = new ArrayList<>();
        state.add("a");
        state.add("b");
        state.cancel(mesh -> {
            released.add(mesh);
            state.published(mesh);
        });
        state.cancel(released::add);
        assertEquals(List.of("a", "b"), released);
    }
}
