package watizdat.rituals.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3d;
import watizdat.rituals.Rituals;
import watizdat.rituals.block.RitualPoleBlock;
import watizdat.rituals.entity.ModEntityHelper;
import watizdat.rituals.enums.RitualPoleType;
import watizdat.rituals.enums.RitualState;
import watizdat.rituals.init.ModBlockEntityTypes;
import watizdat.rituals.init.ModTags;
import watizdat.rituals.state.ModComponents;

import java.util.*;

public class RitualPoleBlockEntity extends BlockEntity {
    public static final HashMap<EntityType<?>, List<ItemStack>> ENTITY_TYPE_LOOT_MAP = new HashMap<>(Map.ofEntries(
            Map.entry(EntityType.ALLAY, List.of(new ItemStack(Items.DIAMOND, 16))),
            Map.entry(EntityType.AXOLOTL, List.of(new ItemStack(Items.GLOW_BERRIES, 64), new ItemStack(Items.MOSS_BLOCK, 7))),
            Map.entry(EntityType.BAT, List.of()),
            Map.entry(EntityType.PIGLIN, List.of(new ItemStack(Items.GOLD_INGOT, 32))),
            Map.entry(EntityType.SHULKER, List.of(new ItemStack(Items.SHULKER_SHELL, 32)))
    ));

    private RitualState ritualState = RitualState.NOT_STARTED;
    private List<UUID> entityUuids = new ArrayList<>();
    private List<Map.Entry<EntityType<?>, Integer>> entityTypesKilled;
    private UUID playerUuid = new UUID(0L, 0L);
    private List<Vector3d> particlePositions = new ArrayList<>();

    private static final long TIMER_MAX_TICKS = 20L;
    private boolean timerStarted;
    private long timerTicks;

    private static final int RADIUS = 10;

    public RitualPoleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_POLE, pos, state);
    }

    public void addEntityUuid(UUID uuid) {
        entityUuids.add(uuid);

        markDirty();
    }

    public void removeEntityUuid(UUID uuid, boolean hasChildren) {
        entityUuids.remove(uuid);

        markDirty();

        if (entityUuids.isEmpty() && !hasChildren) {
            successRitual();
        }
    }

    public void startRitual(PlayerEntity player, List<Map.Entry<Identifier, Integer>> entityTypesKilled) {
        Rituals.LOGGER.info("Ritual started");

        playerUuid = player.getUuid();
        ModComponents.RITUAL_POLE_POS_COMPONENT.get(player).set(getPos());

        ritualState = RitualState.IN_PROGRESS;

        this.entityTypesKilled = entityTypesKilled.stream().<Map.Entry<EntityType<?>, Integer>>map(entry -> Map.entry(Registries.ENTITY_TYPE.get(entry.getKey()), entry.getValue())).toList();

        timerStarted = true;
        timerTicks = TIMER_MAX_TICKS;

        markDirty();

        if (particlePositions.isEmpty()) {
            setParticlePositions();
        }

        for (Map.Entry<EntityType<?>, Integer> entry : this.entityTypesKilled) {
            EntityType<?> entityType = entry.getKey();
            int count = entry.getValue();

            if (!isEntityValidForRitualPoleType(entityType, getWorld().getBlockState(getPos()).get(RitualPoleBlock.TYPE))) {
                continue;
            }

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

            for (int i = 0; i < count; i++) {
                BlockPos pos = validSpawnPositions.get(random.nextInt(validSpawnPositions.size()));

                ServerWorld serverWorld = player.getServer().getWorld(player.getWorld().getRegistryKey());

                Entity entity = entityType.spawn(serverWorld, pos, SpawnReason.MOB_SUMMONED);

                ModEntityHelper.setAsRitualMob(entity, getPos());
            }
        }
    }

    public void successRitual() {
        PlayerEntity player = getWorld().getPlayerByUuid(playerUuid);

        if (player == null ||
            player.isDead()) {

            return;
        }

        ritualState = RitualState.SUCCESS;

        for (Map.Entry<EntityType<?>, Integer> entry : entityTypesKilled) {
            EntityType<?> entityType = entry.getKey();
            int count = entry.getValue();

            if (!isEntityValidForRitualPoleType(entityType, getWorld().getBlockState(getPos()).get(RitualPoleBlock.TYPE))) {
                continue;
            }

            for (int i = 0; i < count; i++) {
                for (ItemStack itemStack : ENTITY_TYPE_LOOT_MAP.get(entityType)) {
                    ItemEntity itemEntity = new ItemEntity(
                            getWorld(),
                            getPos().getX() + 0.5,
                            getPos().getY() + 1.5,
                            getPos().getZ() + 0.5,
                            itemStack.copy(),
                            0d,
                            0d,
                            0d);

                    itemEntity.setToDefaultPickupDelay();
                    itemEntity.setCovetedItem();

                    getWorld().spawnEntity(itemEntity);
                }
            }
        }

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

    public static boolean isEntityValidForRitualPoleType(EntityType<?> entityType, RitualPoleType ritualPoleType) {
        if (ModEntityHelper.isWaterCreature(entityType) && ritualPoleType != RitualPoleType.AQUATIC) {
            return false;
        }

        if ((entityType.isIn(ModTags.LIVES_IN_OVERWORLD_LAND) || entityType.isIn(ModTags.LIVES_IN_OVERWORLD_FLYING) &&
                ritualPoleType != RitualPoleType.OVERWORLD)) {
            return false;
        }

        if (entityType.isIn(ModTags.LIVES_IN_NETHER) && ritualPoleType != RitualPoleType.NETHER) {
            return false;
        }

        if (entityType.isIn(ModTags.LIVES_IN_END) && ritualPoleType != RitualPoleType.END) {
            return false;
        }

        return true;
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

        boolean isWaterCreature = ModEntityHelper.isWaterCreature(entityType);

        for (int i = 0; i < height; i++) {
            int distance = i + 1;

            // Water creatures spawn in the water rather than on top of it
            if (isWaterCreature) {
                distance = i;
            }

//            if (!player.getWorld().getBlockState(pos.up(distance)).isTransparent(player.getWorld(), pos.up(distance))) {
//                canSpawn = false;
//            }

            if ((!isWaterCreature && !player.getWorld().getBlockState(pos.up(distance)).isAir()) ||
                    (isWaterCreature && !player.getWorld().getBlockState(pos.up(distance)).isOf(Blocks.WATER))) {

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
