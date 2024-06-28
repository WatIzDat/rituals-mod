package watizdat.rituals.mixin;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.helper.ParticleTimerAccess;
import watizdat.rituals.init.ModEvents;
import watizdat.rituals.state.ModPersistentState;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ParticleTimerAccess {
    @Shadow public abstract <T extends ParticleEffect> int spawnParticles(T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed);

    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Unique
    private long maxTicks = 20;

    @Inject(at = @At("TAIL"), method = "tick")
    private void rituals$tick(CallbackInfo info) {
        ModEvents.ticks--;

        if (ModEvents.ticks == 0L) {
            for (Vector3d position : ModEvents.positions) {
                spawnParticles(new DustParticleEffect(DustParticleEffect.RED, 4f), position.x, position.y, position.z, 1, 0d, 0d, 0d, 0d);
            }

            ModEvents.ticks = maxTicks;
        }
    }

    @Override
    public void rituals$setTimer(long ticks) {
        maxTicks = ticks;
        ModEvents.ticks = ticks;
    }

    @Override
    public void rituals$addPosition(double x, double y, double z) {
        Vector3d vector3d = new Vector3d(x, y, z);

        ModEvents.positions.add(vector3d);

        ModPersistentState state = ModPersistentState.getServerState(getServer());

        state.particlePositions.add(vector3d);
    }
}
