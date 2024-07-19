package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
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
				boolean hasChildren = ((LivingEntity) (Object) this) instanceof SlimeEntity &&
						((SlimeEntity) (Object) this).getSize() > 1;

                ModComponents.RITUAL_POLE_POS_COMPONENT.get(this).getBlockEntity(getWorld()).removeEntityUuid(getUuid(), hasChildren);
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
					info.setReturnValue(0.96d);
				} else if (attribute == EntityAttributes.GENERIC_MOVEMENT_SPEED) {
					info.setReturnValue(0.48d);
				}
			}
		}
	}

	@ModifyReturnValue(method = "canSee", at = @At("RETURN"))
	private boolean rituals$allowRitualGuardianToSeeThroughOneBlock(boolean original, @Local(argsOnly = true) Entity target) {
		if (!(((LivingEntity) (Object) this) instanceof GuardianEntity)) {
			return original;
		}

		if (!((MobEntityMixinAccess) this).rituals$isRitualMob()) {
			return original;
		}

		if (original) {
			return true;
		}

		boolean twoBlocksInTheWay = false;

		Vec3d start = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
		Vec3d end = new Vec3d(target.getX(), target.getEyeY(), target.getZ());

		HitResult hitResult1 = this.getWorld().raycast(new RaycastContext(
				start,
				end,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				this));

		if (hitResult1.getType() == HitResult.Type.BLOCK) {
			Vec3d startWithOffset = hitResult1.getPos().add(hitResult1.getPos().subtract(start).normalize().multiply(1.5d));

			HitResult hitResult2 = this.getWorld().raycast(new RaycastContext(
					startWithOffset,
					end,
					RaycastContext.ShapeType.COLLIDER,
					RaycastContext.FluidHandling.NONE,
					this));

			if (hitResult2.getType() == HitResult.Type.BLOCK) {
				twoBlocksInTheWay = true;

				System.out.println("two blocks in the way");
			}
		}

		return !original && !twoBlocksInTheWay;
	}
}