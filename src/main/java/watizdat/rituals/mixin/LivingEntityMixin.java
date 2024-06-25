package watizdat.rituals.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.Rituals;
import watizdat.rituals.network.ModNetworkConstants;
import watizdat.rituals.state.ModPersistentState;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow @Nullable public abstract LivingEntity getAttacker();

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;getAttacker()Lnet/minecraft/entity/Entity;"), method = "onDeath")
	private void rituals$onDeath(CallbackInfo info) {
		if (!getWorld().isClient && getAttacker() != null) {
			Rituals.LOGGER.info(EntityType.getId(getType()).toString());

			ModPersistentState state = ModPersistentState.getServerState(getWorld().getServer());

			state.entityTypesKilled.add(getType());

			MinecraftServer server = getWorld().getServer();
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeCollection(state.entityTypesKilled.stream().map(EntityType::getId).toList(), PacketByteBuf::writeIdentifier);

			ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(getAttacker().getUuid());
			server.execute(() -> {
				ServerPlayNetworking.send(playerEntity, ModNetworkConstants.KILLED_ENTITY_PACKET_ID, buf);
			});
		}
	}
}