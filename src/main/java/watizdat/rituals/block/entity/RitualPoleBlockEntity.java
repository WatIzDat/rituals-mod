package watizdat.rituals.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3d;
import watizdat.rituals.Rituals;
import watizdat.rituals.access.*;
import watizdat.rituals.enums.RitualState;
import watizdat.rituals.event.ComponentEvents;
import watizdat.rituals.init.ModBlockEntityTypes;
import watizdat.rituals.state.ModComponents;
import watizdat.rituals.state.component.EntityTypesKilledComponent;

import java.util.*;

public class RitualPoleBlockEntity extends BlockEntity {
    private RitualState ritualState = RitualState.NOT_STARTED;
    private List<UUID> entityUuids = new ArrayList<>();
    private UUID playerUuid = new UUID(0L, 0L);
    private List<Vector3d> particlePositions = new ArrayList<>();

    private static final long TIMER_MAX_TICKS = 20L;
    private boolean timerStarted;
    private long timerTicks;

    private static final int RADIUS = 10;

    public RitualPoleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_POLE, pos, state);
    }

    public void removeEntityUuid(UUID uuid) {
        entityUuids.remove(uuid);

        markDirty();

        if (entityUuids.isEmpty()) {
            successRitual();
        }
    }

    public void startRitual(PlayerEntity player) {
        Rituals.LOGGER.info("Ritual started");

        playerUuid = player.getUuid();
//        player.setAttached(ModDataAttachments.getRitualPolePosPersistent(), getPos());
        ModComponents.RITUAL_POLE_POS_COMPONENT.get(player).set(getPos());

        ritualState = RitualState.IN_PROGRESS;

        timerStarted = true;
        timerTicks = TIMER_MAX_TICKS;

        markDirty();

        if (particlePositions.isEmpty()) {
            setParticlePositions();
        }

        EntityTypesKilledComponent entityTypesKilled = ModComponents.ENTITY_TYPES_KILLED_COMPONENT.get(player);

        for (EntityType<?> entityType : entityTypesKilled.getValue()) {
            List<BlockPos> validSpawnPositions = new ArrayList<>();
            Set<Pair<Integer, Integer>> duplicates = new HashSet<>();

            for (int r = 0; r < RADIUS; r++) {
                for (int t = 0; t < 360; t++) {
                    int x = Math.round(r * MathHelper.cos(t) + getPos().getX());
                    int z = Math.round(r * MathHelper.sin(t) + getPos().getZ());

                    if (duplicates.contains(new Pair<>(x, z))) {
                        continue;
                    }

                    duplicates.add(new Pair<>(x, z));

                    BlockPos spawnPosDown = canSpawn(
                            new Vec3d(x, getPos().getY(), z),
                            new Vec3d(x, player.getWorld().getBottomY(), z),
                            player,
                            entityType
                    );

                    if (spawnPosDown != null && !validSpawnPositions.contains(spawnPosDown)) {
                        validSpawnPositions.add(spawnPosDown);

                        continue;
                    }

                    BlockPos spawnPosUp = canSpawn(
                            new Vec3d(x, getPos().getY(), z),
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

            ServerWorld serverWorld = player.getServer().getWorld(player.getWorld().getRegistryKey());

            Entity entity = entityType.spawn(serverWorld, pos, SpawnReason.MOB_SUMMONED);

            ModComponents.RITUAL_POLE_POS_COMPONENT.get(entity).set(getPos());

            StatusEffectInstance statusEffectInstance = new StatusEffectInstance(
                    StatusEffects.GLOWING, StatusEffectInstance.INFINITE, 0, false, false);
            ((LivingEntity) entity).addStatusEffect(statusEffectInstance);

            entityUuids.add(entity.getUuid());

            markDirty();
        }
    }

    public void successRitual() {
        if (getWorld().getPlayerByUuid(playerUuid) == null ||
            getWorld().getPlayerByUuid(playerUuid).isDead()) {

            return;
        }

        ritualState = RitualState.SUCCESS;

        stopRitual();
    }

    public RitualState getRitualState() {
        return ritualState;
    }

    public void resetRitualState() {
        ritualState = RitualState.NOT_STARTED;
    }

    public void failRitual() {
        ritualState = RitualState.FAIL;

        List<UUID> entityUuidsCopy = new ArrayList<>(entityUuids);

        entityUuids.clear();

        for (UUID uuid : entityUuidsCopy) {
            getWorld().getServer().getWorld(getWorld().getRegistryKey()).getEntity(uuid).remove(Entity.RemovalReason.DISCARDED);
        }

        stopRitual();
    }

    private void stopRitual() {
//        getWorld().getPlayerByUuid(playerUuid).removeAttached(ModDataAttachments.getRitualPolePosPersistent());
        ModComponents.RITUAL_POLE_POS_COMPONENT.get(getWorld().getPlayerByUuid(playerUuid)).remove();
        playerUuid = new UUID(0L, 0L);

        timerStarted = false;

        markDirty();
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

    public static void tick(World world, BlockPos pos, BlockState state, RitualPoleBlockEntity blockEntity) {
        if (blockEntity.timerStarted) {
            blockEntity.timerTicks--;

            if (blockEntity.timerTicks == 0L) {
                for (Vector3d position : blockEntity.particlePositions) {
                    world.getServer().getWorld(world.getRegistryKey()).spawnParticles(new DustParticleEffect(DustParticleEffect.RED, 4f), position.x, position.y, position.z, 1, 0d, 0d, 0d, 0d);
                }

                blockEntity.timerTicks = TIMER_MAX_TICKS;
            }
        }
    }

    private void setParticlePositions() {
        for (int t = 0; t < 360; t += 10) {
            float x = RADIUS * MathHelper.cos(t) + getPos().getX();
            float z = RADIUS * MathHelper.sin(t) + getPos().getZ();

            for (int y = getPos().getY() - 50; y < getPos().getY() + 50; y += 2) {
                particlePositions.add(new Vector3d(x, y, z));
            }
        }
    }

    public boolean isOutsideCircle(double x, double z) {
        return (x - getPos().getX())*(x - getPos().getX()) + (z - getPos().getZ())*(z - getPos().getZ()) > RADIUS*RADIUS;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        timerStarted = nbt.getBoolean("timerStarted");
        timerTicks = TIMER_MAX_TICKS;

        setParticlePositions();

        NbtCompound entityUuidsNbt = nbt.getCompound("entityUuids");

        entityUuidsNbt.getKeys().forEach(key -> {
            entityUuids.add(entityUuidsNbt.getUuid(key));
        });

        ritualState = Enum.valueOf(RitualState.class, nbt.getString("ritualState"));

        playerUuid = nbt.getUuid("playerUuid");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("timerStarted", timerStarted);

        NbtCompound entityUuidsNbt = new NbtCompound();

        for (int i = 0; i < entityUuids.size(); i++) {
            entityUuidsNbt.putUuid(String.valueOf(i), entityUuids.get(i));
        }

        nbt.put("entityUuids", entityUuidsNbt);

        nbt.putString("ritualState", ritualState.toString());

        nbt.putUuid("playerUuid", playerUuid);

        super.writeNbt(nbt);
    }
}
