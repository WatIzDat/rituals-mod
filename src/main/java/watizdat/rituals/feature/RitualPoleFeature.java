package watizdat.rituals.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import watizdat.rituals.block.RitualPoleBlock;
import watizdat.rituals.init.ModBlocks;

public class RitualPoleFeature extends Feature<DefaultFeatureConfig> {
    public RitualPoleFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();

        BlockState blockState = ModBlocks.RITUAL_POLE.getDefaultState();

        BlockPos testPos = new BlockPos(origin);

        if (world.getBlockState(testPos.down()).isIn(BlockTags.DIRT) &&
            world.getBlockState(testPos).isOf(Blocks.AIR) &&
            world.getBlockState(testPos.up()).isOf(Blocks.AIR) &&
            testPos.up().getY() <= world.getTopY()) {

            world.setBlockState(testPos, blockState, Block.NOTIFY_ALL);
            world.setBlockState(testPos.up(), blockState.with(RitualPoleBlock.HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);

            return true;
        }

        return false;
    }
}
