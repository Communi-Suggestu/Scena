package com.communi.suggestu.scena.core.client.rendering.sharding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerrainShardSetTest {

    private static TerrainShardSet threeSlices() {
        return new TerrainShardSet(TerrainShardPlan.create(3L * 128, 32, 128));
    }

    @Test
    void stagingMustFollowPlanOrder() {
        TerrainShardSet set = threeSlices();

        assertThrows(IllegalStateException.class, () -> set.markStaged(1));
        set.markStaged(0);
        assertEquals(1, set.stagedCount());
        assertEquals(1, set.nextStageIndex());
        assertFalse(set.isStagingComplete());
    }

    @Test
    void uploadsCompleteOutOfOrderAndDuplicatesAreIgnored() {
        TerrainShardSet set = threeSlices();
        set.markStaged(0);
        set.markStaged(1);
        set.markStaged(2);
        assertTrue(set.isStagingComplete());

        assertFalse(set.markUploaded(2));
        assertFalse(set.markUploaded(2));
        assertFalse(set.markUploaded(0));
        assertFalse(set.isReady());
        assertThrows(IllegalArgumentException.class, () -> set.markUploaded(-1));
        assertThrows(IllegalArgumentException.class, () -> set.markUploaded(3));
        assertTrue(set.markUploaded(1));
        assertTrue(set.isReady());
        assertEquals(3, set.uploadedCount());
    }

    @Test
    void readyTransitionReportedOnceAndOnlyWhenAllUploaded() {
        TerrainShardSet set = new TerrainShardSet(TerrainShardPlan.create(256, 32, 128));
        set.markStaged(0);
        set.markStaged(1);

        assertFalse(set.markUploaded(0));
        assertTrue(set.markUploaded(1));
        assertFalse(set.markUploaded(1));
    }

    @Test
    void releaseIsIdempotentAndBlocksFurtherRecording() {
        TerrainShardSet set = threeSlices();
        set.markStaged(0);

        assertTrue(set.release());
        assertFalse(set.release());
        assertTrue(set.isReleased());
        assertThrows(IllegalStateException.class, () -> set.markStaged(1));
        assertFalse(set.markUploaded(0));
        assertEquals(1, set.stagedCount());
        assertEquals(0, set.uploadedCount());
    }

    @Test
    void cancelAfterCompleteStagingReleasesWithoutUploads() {
        TerrainShardSet set = threeSlices();
        set.markStaged(0);
        set.markStaged(1);
        set.markStaged(2);

        assertTrue(set.release());
        assertFalse(set.isReady());
        assertFalse(set.markUploaded(0));
    }

    @Test
    void cannotUploadBeforeStagingOrStagePastThePlan() {
        TerrainShardSet set = threeSlices();
        assertThrows(IllegalStateException.class, () -> set.markUploaded(0));
        set.markStaged(0);
        set.markStaged(1);
        set.markStaged(2);
        assertThrows(IllegalStateException.class, () -> set.markStaged(3));
    }

    @Test
    void releasedReadySetIsNoLongerReady() {
        TerrainShardSet set = new TerrainShardSet(TerrainShardPlan.create(128, 32, 128));
        set.markStaged(0);
        assertTrue(set.markUploaded(0));
        assertTrue(set.isReady());
        set.release();
        assertFalse(set.isReady());
    }
}
