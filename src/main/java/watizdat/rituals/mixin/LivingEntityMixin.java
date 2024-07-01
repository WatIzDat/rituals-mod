package watizdat.rituals.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.network.ModNetworkConstants;
import watizdat.rituals.state.ModDataAttachments;
import watizdat.rituals.state.ModPersistentState;
import watizdat.rituals.state.ModPlayerData;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow @Nullable public abstract LivingEntity getAttacker();

//	@Shadow @Final private AttributeContainer attributes;

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;getAttacker()Lnet/minecraft/entity/Entity;"), method = "onDeath")
	private void rituals$onDeath(CallbackInfo info) {
		if (!getWorld().isClient) {
			if (hasAttached(ModDataAttachments.getRitualPolePosPersistent())) {
				BlockPos ritualPolePos = getAttached(ModDataAttachments.getRitualPolePosPersistent());

				((RitualPoleBlockEntity) getWorld().getBlockEntity(ritualPolePos)).removeEntityUuid(getUuid());
			} else if (getAttacker() != null && getAttacker().isPlayer()) {
				ModPlayerData playerState = ModPersistentState.getPlayerState(getAttacker());
				playerState.entityTypesKilled.add(getType());

				MinecraftServer server = getWorld().getServer();
				PacketByteBuf buf = PacketByteBufs.create();
				buf.writeCollection(playerState.entityTypesKilled.stream().map(EntityType::getId).toList(), PacketByteBuf::writeIdentifier);

				ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(getAttacker().getUuid());
				server.execute(() -> {
					ServerPlayNetworking.send(playerEntity, ModNetworkConstants.KILLED_ENTITY_PACKET_ID, buf);
				});
			}
		}
	}

//	@Inject(at = @At("HEAD"), method = "getAttributes")
//	private void rituals$getAttributes(CallbackInfoReturnable<AttributeContainer> info) {
//	}
}