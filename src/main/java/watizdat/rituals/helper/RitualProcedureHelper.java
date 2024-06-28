package watizdat.rituals.helper;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import watizdat.rituals.state.ModPersistentState;
import watizdat.rituals.state.ModPlayerData;

import java.util.*;

public class RitualProcedureHelper {
    public static void startRitualProcedure(BlockPos ritualPolePos, PlayerEntity player) {
        int radius = 10;

        ModPlayerData playerState = ModPersistentState.getPlayerState(player);

        ServerWorld serverWorld = player.getServer().getWorld(player.getWorld().getRegistryKey());

        ((ParticleTimerAccess) serverWorld).rituals$setTimer(20L);

        for (int t = 0; t < 360; t += 10) {
            float x = radius * MathHelper.cos(t) + ritualPolePos.getX();
            float z = radius * MathHelper.sin(t) + ritualPolePos.getZ();

            for (int y = ritualPolePos.getY() - 50; y < ritualPolePos.getY() + 50; y += 2) {
                ((ParticleTimerAccess) serverWorld).rituals$addPosition(x, y, z);
            }
        }

        for (EntityType<?> entityType : playerState.entityTypesKilled) {
            List<BlockPos> validSpawnPositions = new ArrayList<>();
            Set<Pair<Integer, Integer>> duplicates = new HashSet<>();

            for (int r = 0; r < radius; r++) {
                for (int t = 0; t < 360; t++) {
                    int x = Math.round(r * MathHelper.cos(t) + ritualPolePos.getX());
                    int z = Math.round(r * MathHelper.sin(t) + ritualPolePos.getZ());

                    if (duplicates.contains(new Pair<>(x, z))) {
                        continue;
                    }

                    duplicates.add(new Pair<>(x, z));

                    BlockPos spawnPosDown = canSpawn(
                            new Vec3d(x, ritualPolePos.getY(), z),
                            new Vec3d(x, player.getWorld().getBottomY(), z),
                            player,
                            entityType
                    );

                    if (spawnPosDown != null && !validSpawnPositions.contains(spawnPosDown)) {
                        validSpawnPositions.add(spawnPosDown);

                        continue;
                    }

                    BlockPos spawnPosUp = canSpawn(
                            new Vec3d(x, ritualPolePos.getY(), z),
                            new Vec3d(x, player.getWorld().getTopY() - 1, z),
                            player,
                            entityType
                    );

                    if (spawnPosUp != null && !validSpawnPositions.contains(spawnPosUp)) {
                        validSpawnPositions.add(spawnPosUp);
                    }
                }
            }

            if (validSpawnPositions.isEmpty()) {
                continue;
            }

            Random random = new Random();

            BlockPos pos = validSpawnPositions.get(random.nextInt(validSpawnPositions.size()));

            entityType.spawn(serverWorld, pos, SpawnReason.MOB_SUMMONED);
        }
    }

    private static BlockPos canSpawn(Vec3d raycastStart, Vec3d raycastEnd, PlayerEntity player, EntityType<?> entityType) {
        RaycastContext raycastContext = new RaycastContext(
                raycastStart,
                raycastEnd,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.ANY,
                player
        );

        BlockHitResult blockHitResult = player.getWorld().raycast(raycastContext);

        BlockPos pos = blockHitResult.getBlockPos();

        int height = MathHelper.ceil(entityType.getHeight());

        boolean canSpawn = true;

        for (int i = 0; i < height; i++) {
            int distance = i + 1;

            if (!player.getWorld().getBlockState(pos.up(distance)).isTransparent(player.getWorld(), pos.up(distance))) {
                canSpawn = false;
            }
        }

        if (canSpawn) {
            return pos.up();
        } else {
            return null;
        }
    }
}
