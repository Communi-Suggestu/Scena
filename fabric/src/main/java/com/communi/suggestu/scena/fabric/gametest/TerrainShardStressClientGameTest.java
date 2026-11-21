package com.communi.suggestu.scena.fabric.gametest;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainShardGate;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainShardStats;
import com.communi.suggestu.scena.fabric.platform.client.rendering.TerrainStressModels;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;

/**
 * Fills one section with a stress model cube and asserts the sharded upload path completes.
 * Runs only when {@code scena.terrainStress} and {@code scena.experimentalTerrainSharding} are set.
 */
public final class TerrainShardStressClientGameTest implements FabricClientGameTest
{
    private static final BlockPos ORIGIN = new BlockPos(0, 64, 0);

    @Override
    public void runTest(final ClientGameTestContext context)
    {
        if (!TerrainStressModels.enabled() || !Boolean.getBoolean("scena.experimentalTerrainSharding"))
        {
            return;
        }

        context.runOnClient(client -> client.options.pauseOnLostFocus = false);

        try (final TestSingleplayerContext world = context.worldBuilder()
            .adjustSettings(settings -> settings.setName("Scena terrain sharding check"))
            .create())
        {
            world.getServer().runCommand("gamemode spectator @a");
            world.getServer().runCommand(String.format(
                "fill %d %d %d %d %d %d minecraft:netherite_block",
                ORIGIN.getX(), ORIGIN.getY(), ORIGIN.getZ(),
                ORIGIN.getX() + 15, ORIGIN.getY() + 15, ORIGIN.getZ() + 15
            ));
            world.getServer().runCommand("tp @a 8 82 24 facing 8 72 8");

            context.waitFor(client -> client.level != null, 600);
            if (!TerrainShardGate.isEnabled())
            {
                throw new AssertionError("The sharding mixins are not active; the gate did not accept scena.experimentalTerrainSharding");
            }

            world.getConnection().waitForChunksRender(true, 600);
            int ticks = 0;
            TerrainShardStats.Snapshot live = TerrainShardStats.snapshot();
            while (ticks < 600 && !(live.compiles() > 0 && TerrainStressModels.emissions() >= 4096L * TerrainStressModels.repeats()
                && live.shardsUploaded() >= 2 && live.drawsAppended() >= 1))
            {
                context.waitTick();
                ticks++;
                live = TerrainShardStats.snapshot();
            }

            final TerrainShardStats.Snapshot stats = live;
            if (stats.compiles() == 0)
            {
                throw new AssertionError("No section compile ran: " + stats);
            }
            if (TerrainStressModels.emissions() < 4096L * TerrainStressModels.repeats())
            {
                throw new AssertionError("The stress model did not emit the whole section: emissions="
                    + " expected=" + (4096L * TerrainStressModels.repeats()) + " got=" + TerrainStressModels.emissions() + " " + stats);
            }
            if (stats.builderRotations() == 0)
            {
                throw new AssertionError("The section compiler never rotated a builder at the 32 MiB vertex limit: " + stats);
            }
            if (stats.layersSharded() == 0)
            {
                throw new AssertionError("No section layer was sharded: " + stats);
            }
            if (stats.shardsUploaded() < stats.shardsStaged())
            {
                throw new AssertionError("Staged shards did not finish uploading: " + stats);
            }
            if (stats.drawsAppended() == 0)
            {
                throw new AssertionError("The render pass did not append any shard draws: " + stats);
            }
        }
    }
}
