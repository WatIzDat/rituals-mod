package watizdat.rituals.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import watizdat.rituals.block.RitualPoleBlock;
import watizdat.rituals.enums.RitualPoleType;
import watizdat.rituals.feature.config.RitualPoleFeatureConfig;
import watizdat.rituals.init.ModBlocks;
import watizdat.rituals.init.ModTags;

public class RitualPoleFeature extends Feature<RitualPoleFeatureConfig> {
    public RitualPoleFeature(Codec<RitualPoleFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<RitualPoleFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        RitualPoleFeatureConfig config = context.getConfig();

        System.out.println(config.type());

        BlockState blockState = ModBlocks.RITUAL_POLE.getDefaultState();

        BlockPos testPos = new BlockPos(origin);

        if (!world.getBlockState(testPos.down()).isOf(Blocks.BEDROCK) &&
            !world.getBlockState(testPos.down()).isAir() &&
            !world.getBlockState(testPos.down()).isTransparent(world, testPos.down()) &&
            (world.getBlockState(testPos).isOf(Blocks.AIR) || world.getBlockState(testPos).isOf(Blocks.WATER)) &&
            (world.getBlockState(testPos.up()).isOf(Blocks.AIR) || world.getBlockState(testPos.up()).isOf(Blocks.WATER)) &&
            testPos.up().getY() <= world.getTopY()) {

            if ((config.type() == RitualPoleType.NETHER && world.getBlockState(testPos.down()).isIn(ModTags.NETHER_RITUAL_POLE_NOT_PLACEABLE)) ||
                 config.type() == RitualPoleType.OVERWORLD && !world.getBlockState(testPos.down()).getFluidState().isEmpty()) {

                return false;
            }

            BlockState bottomBlockState = blockState.with(RitualPoleBlock.TYPE, config.type());
            BlockState topBlockState = blockState.with(RitualPoleBlock.HALF, DoubleBlockHalf.UPPER)
                    .with(RitualPoleBlock.TYPE, config.type());

//            if (config.type() == RitualPoleType.AQUATIC) {
//                world.setBlockState(testPos,
//                        bottomBlockState.with(RitualPoleBlock.WATERLOGGED, true), Block.NOTIFY_ALL);
//
//                world.setBlockState(testPos.up(),
//                        topBlockState.with(RitualPoleBlock.WATERLOGGED, true), Block.NOTIFY_ALL);
//            } else {
//                world.setBlockState(testPos, bottomBlockState, Block.NOTIFY_ALL);
//
//                world.setBlockState(testPos.up(), topBlockState, Block.NOTIFY_ALL);
//            }

            if (world.getBlockState(testPos).isOf(Blocks.WATER)) {
                world.setBlockState(testPos,
                        bottomBlockState.with(RitualPoleBlock.WATERLOGGED, true), Block.NOTIFY_ALL);
            } else {
                world.setBlockState(testPos, bottomBlockState, Block.NOTIFY_ALL);
            }

            if (world.getBlockState(testPos.up()).isOf(Blocks.WATER)) {
                world.setBlockState(testPos.up(),
                        topBlockState.with(RitualPoleBlock.WATERLOGGED, true), Block.NOTIFY_ALL);
            } else {
                world.setBlockState(testPos.up(), topBlockState, Block.NOTIFY_ALL);
            }

            return true;
        }

        return false;
    }
}
