package watizdat.rituals.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BatEntity;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import watizdat.rituals.access.MobEntityMixinAccess;
import watizdat.rituals.network.ModNetworkConstants;
import watizdat.rituals.state.ModComponents;
import watizdat.rituals.state.component.EntityTypesKilledComponent;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow @Nullable public abstract LivingEntity getAttacker();

	@Shadow public abstract float getMaxHealth();

	@Shadow public abstract AttributeContainer getAttributes();

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;getAttacker()Lnet/minecraft/entity/Entity;"), method = "onDeath")
	private void rituals$onDeath(CallbackInfo info) {
		if (!getWorld().isClient) {
			if (ModComponents.RITUAL_POLE_POS_COMPONENT.get(this).isPresent()) {
                ModComponents.RITUAL_POLE_POS_COMPONENT.get(this).getBlockEntity(getWorld()).removeEntityUuid(getUuid());
			} else if (getAttacker() != null && getAttacker().isPlayer()) {
                EntityTypesKilledComponent entityTypesKilled = ModComponents.ENTITY_TYPES_KILLED_COMPONENT.get(getAttacker());

				entityTypesKilled.add(getType());

				MinecraftServer server = getWorld().getServer();
				PacketByteBuf buf = PacketByteBufs.create();
				buf.writeCollection(entityTypesKilled.getValue().stream().map(EntityType::getId).toList(), PacketByteBuf::writeIdentifier);

				ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(getAttacker().getUuid());
				server.execute(() -> {
					ServerPlayNetworking.send(playerEntity, ModNetworkConstants.KILLED_ENTITY_PACKET_ID, buf);
				});
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D", cancellable = true)
	private void rituals$returnRitualAttributesForRitualMobs(EntityAttribute attribute, CallbackInfoReturnable<Double> info) {
		if (((LivingEntity) (Object) this) instanceof MobEntity
				&& ((MobEntityMixinAccess) this).rituals$isRitualMob()) {

			if (attribute == EntityAttributes.GENERIC_ATTACK_DAMAGE &&
					!getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {

				info.setReturnValue((double) getMaxHealth());
			}

			if (attribute == EntityAttributes.GENERIC_FOLLOW_RANGE) {
				info.setReturnValue(2048d);
			}

			if (((LivingEntity) (Object) this) instanceof BatEntity) {
				if (attribute == EntityAttributes.GENERIC_FLYING_SPEED) {
					info.setReturnValue(0.6d);
				} else if (attribute == EntityAttributes.GENERIC_MOVEMENT_SPEED) {
					info.setReturnValue(0.3d);
				}
			}
		}
	}
}