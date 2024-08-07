package watizdat.rituals.block;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.enums.RitualPoleType;
import watizdat.rituals.enums.RitualState;
import watizdat.rituals.init.ModBlockEntityTypes;
import watizdat.rituals.network.ModNetworkConstants;
import watizdat.rituals.state.ModComponents;
import watizdat.rituals.state.component.EntityTypesKilledComponent;

public class RitualPoleBlock extends BlockWithEntity {
    public static final EnumProperty<DoubleBlockHalf> HALF = EnumProperty.of("half", DoubleBlockHalf.class);
    public static final EnumProperty<RitualPoleType> TYPE = EnumProperty.of("type", RitualPoleType.class);

    public RitualPoleBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(HALF, DoubleBlockHalf.LOWER));
        setDefaultState(getDefaultState().with(TYPE, RitualPoleType.OVERWORLD));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

//        ModPlayerData playerState = ModPersistentState.getPlayerState(player);

        EntityTypesKilledComponent entityTypesKilled = ModComponents.ENTITY_TYPES_KILLED_COMPONENT.get(player);

        PacketByteBuf buf = PacketByteBufs.create();
//        buf.writeCollection(entityTypesKilled.getValue().stream().map(EntityType::getId).toList(), PacketByteBuf::writeIdentifier);
        buf.writeCollection(
                entityTypesKilled.getMap().entrySet().stream().toList(),
                (packetByteBuf, entry) -> {
                    packetByteBuf.writeIdentifier(EntityType.getId(entry.getKey()));
                    packetByteBuf.writeInt(entry.getValue());
                });

        buf.writeBlockPos(pos);

        RitualState ritualState = ((RitualPoleBlockEntity) world.getBlockEntity(pos)).getRitualState();
        buf.writeEnumConstant(ritualState);

        RitualPoleType ritualPoleType = world.getBlockState(pos).get(RitualPoleBlock.TYPE);
        buf.writeEnumConstant(ritualPoleType);

        ServerPlayNetworking.send((ServerPlayerEntity) player, ModNetworkConstants.OPEN_RITUAL_POLE_GUI_PACKET_ID, buf);

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF).add(TYPE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape platform;

        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            platform = VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.125f, 1f);
        } else {
            platform = VoxelShapes.cuboid(0f, 0.875f, 0f, 1f, 1f, 1f);
        }

        VoxelShape pole = VoxelShapes.cuboid(0.125f, 0f, 0.125f, 0.875f, 1f, 0.875f);

        return VoxelShapes.combineAndSimplify(platform, pole, BooleanBiFunction.OR);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitualPoleBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntityTypes.RITUAL_POLE, RitualPoleBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
