package watizdat.rituals.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.LookAtMobTask;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.Task;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import watizdat.rituals.access.BrainMixinAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(Brain.class)
public abstract class BrainMixin implements BrainMixinAccess {
    @Shadow public abstract void setTaskList(Activity activity, int begin, ImmutableList<Task<?>> tasks, MemoryModuleType<?> memoryType);

    @Override
    public void rituals$setRitualsTaskList() {
        setTaskList(Activity.CORE, 0, ImmutableList.of(
                LookAtMobTask.create(Entity::isPlayer, 10f),
                RangedApproachTask.create(1.2f),
                MeleeAttackTask.create(18)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    @ModifyArgs(method = "createProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain$Profile;<init>(Ljava/util/Collection;Ljava/util/Collection;)V"))
    private static void rituals$createProfileInit(Args args) {
        ImmutableList<MemoryModuleType<?>> memoryModulesImmutable = args.get(0);
        ImmutableList<SensorType<?>> sensorsImmutable = args.get(1);

        List<MemoryModuleType<?>> memoryModules = new ArrayList<>(memoryModulesImmutable);
        List<SensorType<?>> sensors = new ArrayList<>(sensorsImmutable);

        memoryModules.add(MemoryModuleType.VISIBLE_MOBS);
        memoryModules.add(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        memoryModules.add(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
        memoryModules.add(MemoryModuleType.LOOK_TARGET);
        memoryModules.add(MemoryModuleType.WALK_TARGET);
        memoryModules.add(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        memoryModules.add(MemoryModuleType.PATH);
        memoryModules.add(MemoryModuleType.ATTACK_TARGET);
        memoryModules.add(MemoryModuleType.ATTACK_COOLING_DOWN);

        sensors.add(SensorType.NEAREST_PLAYERS);

        args.set(0, memoryModules);
        args.set(1, sensors);
    }

//    @Inject(method = "<init>", at = @At("TAIL"))
//    private void rituals$init(CallbackInfo info) {
//        setTaskList(Activity.CORE, 0, ImmutableList.of(
//                LookAtMobTask.create(Entity::isPlayer, 10f),
//                RangedApproachTask.create(1.2f),
//                MeleeAttackTask.create(18)
//        ), MemoryModuleType.ATTACK_TARGET);
//    }

//    @ModifyVariable(method = "setTaskList(Lnet/minecraft/entity/ai/brain/Activity;Lcom/google/common/collect/ImmutableList;Ljava/util/Set;Ljava/util/Set;)V", at = @At("HEAD"), index = 2, argsOnly = true)
//    private ImmutableList<?> test(ImmutableList value) {
//        System.out.println("test");
//        return ImmutableList.of(
//                Pair.of(0, MeleeAttackTask.create(10))
//        );
//    }

//    @ModifyVariable(method = "setTaskList(Lnet/minecraft/entity/ai/brain/Activity;ILcom/google/common/collect/ImmutableList;)V", at = @At("HEAD"), index = 3, argsOnly = true)
//    private ImmutableList test(ImmutableList value) {
//        System.out.println("test");
//        return ImmutableList.of(LookAtMobTask.create(Entity::isPlayer, 10f), MeleeAttackTask.create(10));
//    }

//    @Shadow public abstract void setTaskList(Activity activity, int begin, ImmutableList tasks, MemoryModuleType<?> memoryType);
//
//    @Overwrite
//    public void setTaskList(Activity activity, int begin, ImmutableList list) {
//        System.out.println("test");
////        return ImmutableList.of(LookAtMobTask.create(Entity::isPlayer, 10f), MeleeAttackTask.create(10));
//        setTaskList(activity, begin, ImmutableList.of(LookAtMobTask.create(Entity::isPlayer, 10f), RangedApproachTask.create(1.1f), MeleeAttackTask.create(10)), MemoryModuleType.ATTACK_TARGET);
//    }
}
