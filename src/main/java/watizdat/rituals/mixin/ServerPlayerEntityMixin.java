package watizdat.rituals.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.state.ModDataAttachments;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void rituals$onDeath(CallbackInfo info) {
        if (hasAttached(ModDataAttachments.getRitualPolePosPersistent())) {
            BlockPos ritualPolePos = getAttached(ModDataAttachments.getRitualPolePosPersistent());

            ((RitualPoleBlockEntity) getWorld().getBlockEntity(ritualPolePos)).failRitual();
        }
    }
}
