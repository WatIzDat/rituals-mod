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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.enums.RitualState;
import watizdat.rituals.init.ModBlockEntityTypes;
import watizdat.rituals.network.ModNetworkConstants;
import watizdat.rituals.state.ModComponents;
import watizdat.rituals.state.component.EntityTypesKilledComponent;

public class RitualPoleBlock extends BlockWithEntity {
    public static final EnumProperty<DoubleBlockHalf> HALF = EnumProperty.of("half", DoubleBlockHalf.class);

    public RitualPoleBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

//        ModPlayerData playerState = ModPersistentState.getPlayerState(player);

        EntityTypesKilledComponent entityTypesKilled = ModComponents.ENTITY_TYPES_KILLED_COMPONENT.get(player);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeCollection(entityTypesKilled.getValue().stream().map(EntityType::getId).toList(), PacketByteBuf::writeIdentifier);
        buf.writeBlockPos(pos);

        RitualState ritualState = ((RitualPoleBlockEntity) world.getBlockEntity(pos)).getRitualState();
        buf.writeEnumConstant(ritualState);

        ServerPlayNetworking.send((ServerPlayerEntity) player, ModNetworkConstants.OPEN_RITUAL_POLE_GUI_PACKET_ID, buf);

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF);
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
