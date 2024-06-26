package watizdat.rituals.block;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import watizdat.rituals.network.ModNetworkConstants;
import watizdat.rituals.state.ModPersistentState;
import watizdat.rituals.state.ModPlayerData;

public class RitualPoleBlock extends Block {
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

        ModPlayerData playerState = ModPersistentState.getPlayerState(player);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeCollection(playerState.entityTypesKilled.stream().map(EntityType::getId).toList(), PacketByteBuf::writeIdentifier);

        ServerPlayNetworking.send((ServerPlayerEntity) player, ModNetworkConstants.OPEN_RITUAL_POLE_GUI_PACKET_ID, buf);

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }
}
