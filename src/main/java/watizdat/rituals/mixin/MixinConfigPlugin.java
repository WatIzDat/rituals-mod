package watizdat.rituals.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (!mixinClassName.endsWith(".AmbientEntityDummyMixin")) {
            return;
        }

        String mobEntity = mapInternalName("net.minecraft.class_1308");
        if (!targetClass.superName.equals(mobEntity)) {
            throw new IllegalStateException();
        }

        String pathAwareEntity = mapInternalName("net.minecraft.class_1314");
        targetClass.superName = pathAwareEntity;

        for (MethodNode method : targetClass.methods) {
            for (ListIterator<AbstractInsnNode> iterator = method.instructions.iterator(); iterator.hasNext(); ) {
                AbstractInsnNode insn = iterator.next();

                if (insn instanceof MethodInsnNode call && insn.getOpcode() == Opcodes.INVOKESPECIAL && call.owner.equals(mobEntity)) {
                    call.owner = pathAwareEntity;
                }
            }
        }
    }

    private String mapInternalName(String intermediary) {
        return FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", intermediary).replace('.', '/');
    }
}
