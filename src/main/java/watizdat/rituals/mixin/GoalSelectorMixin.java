//package watizdat.rituals.mixin;
//
//import net.minecraft.entity.ai.goal.GoalSelector;
//import net.minecraft.entity.ai.goal.MeleeAttackGoal;
//import net.minecraft.entity.ai.goal.PrioritizedGoal;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.Set;
//
//@Mixin(GoalSelector.class)
//public class GoalSelectorMixin {
//    @Shadow @Final private Set<PrioritizedGoal> goals;
//    @Unique
//    private boolean attackGoalAdded;
//
//    @Inject(at = @At("TAIL"), method = "add")
//    private void rituals$add(CallbackInfo info) {
//        goals.add(new PrioritizedGoal(100, new MeleeAttackGoal()))
//    }
//}
