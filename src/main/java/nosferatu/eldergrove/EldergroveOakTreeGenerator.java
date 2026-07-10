package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class EldergroveOakTreeGenerator {
    private EldergroveOakTreeGenerator() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 4 + random.nextInt(3);
        if (!canGrow(level, origin, height)) {
            return false;
        }

        BlockState log = Blocks.OAK_LOG.defaultBlockState();
        for (int y = 0; y < height; y++) {
            setBlock(level, origin.above(y), log);
        }

        placeOakCrown(level, origin.above(height), random);
        return true;
    }

    private static void placeOakCrown(LevelAccessor level, BlockPos center, RandomSource random) {
        for (int y = -2; y <= 2; y++) {
            int radius = y == -2 ? 2 : (y <= 0 ? 3 : 2);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int edge = Math.abs(x) + Math.abs(z);
                    if (edge > radius + 1 || (edge == radius + 1 && random.nextBoolean())) {
                        continue;
                    }
                    BlockPos pos = center.offset(x, y, z);
                    if (canReplace(level, pos)) {
                        setLeaves(level, pos);
                    }
                }
            }
        }
    }

    private static boolean canGrow(LevelAccessor level, BlockPos origin, int height) {
        if (!level.getBlockState(origin.below()).is(BlockTags.DIRT)) {
            return false;
        }
        for (int y = 0; y <= height + 3; y++) {
            int radius = y < height - 1 ? 1 : 4;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (!canReplace(level, origin.offset(x, y, z))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean canReplace(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES);
    }

    private static void setBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        if (canReplace(level, pos)) {
            level.setBlock(pos, state, 3);
        }
    }

    private static void setLeaves(LevelAccessor level, BlockPos pos) {
        level.setBlock(
                pos,
                Blocks.OAK_LEAVES.defaultBlockState()
                        .setValue(LeavesBlock.PERSISTENT, false)
                        .setValue(LeavesBlock.DISTANCE, 3),
                3
        );
    }
}
