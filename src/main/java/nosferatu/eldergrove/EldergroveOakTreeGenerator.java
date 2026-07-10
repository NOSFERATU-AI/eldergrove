package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class EldergroveOakTreeGenerator {
    private static final EnumProperty<Direction.Axis> AXIS = RotatedPillarBlock.AXIS;

    private EldergroveOakTreeGenerator() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 8 + random.nextInt(5);
        if (!canGrow(level, origin, height)) {
            return false;
        }

        BlockState verticalLog = Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, Direction.Axis.Y);
        for (int y = 0; y < height; y++) {
            setBlock(level, origin.above(y), verticalLog);
        }

        placeLargeOakBranches(level, origin, height, random);
        placeLargeOakCrown(level, origin.above(height - 2), random);
        return true;
    }

    private static void placeLargeOakBranches(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        int baseY = height - 4;
        placeBranch(level, origin.above(baseY), 1, 0, 3 + random.nextInt(2), Direction.Axis.X);
        placeBranch(level, origin.above(baseY + 1), -1, 0, 2 + random.nextInt(2), Direction.Axis.X);
        placeBranch(level, origin.above(baseY), 0, 1, 3 + random.nextInt(2), Direction.Axis.Z);
        placeBranch(level, origin.above(baseY + 1), 0, -1, 2 + random.nextInt(2), Direction.Axis.Z);
    }

    private static void placeBranch(LevelAccessor level, BlockPos start, int dx, int dz, int length, Direction.Axis axis) {
        BlockState log = Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, axis);
        for (int step = 1; step <= length; step++) {
            int y = step > 2 ? 1 : 0;
            setBlock(level, start.offset(dx * step, y, dz * step), log);
        }
    }

    private static void placeLargeOakCrown(LevelAccessor level, BlockPos center, RandomSource random) {
        for (int y = -3; y <= 3; y++) {
            int radius;
            if (y <= -2) {
                radius = 3;
            } else if (y <= 1) {
                radius = 4;
            } else {
                radius = 2;
            }

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int edge = Math.abs(x) + Math.abs(z);
                    boolean corner = Math.abs(x) == radius && Math.abs(z) == radius;
                    if (edge > radius + 2 || (corner && random.nextBoolean())) {
                        continue;
                    }
                    BlockPos pos = center.offset(x, y, z);
                    if (canReplace(level, pos)) {
                        setLeaves(level, pos);
                    }
                }
            }
        }

        // Extra side clusters give the oak the large Magical Forest silhouette instead of the tiny vanilla oak shape.
        placeLeafCluster(level, center.offset(3, -1, 0), random);
        placeLeafCluster(level, center.offset(-3, 0, 0), random);
        placeLeafCluster(level, center.offset(0, -1, 3), random);
        placeLeafCluster(level, center.offset(0, 0, -3), random);
    }

    private static void placeLeafCluster(LevelAccessor level, BlockPos center, RandomSource random) {
        for (int y = -1; y <= 1; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (Math.abs(x) + Math.abs(z) > 3 || random.nextInt(7) == 0) {
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
        if (!canSustainTree(level.getBlockState(origin.below()))) {
            return false;
        }
        for (int y = 0; y <= height + 4; y++) {
            int radius = y < height - 4 ? 1 : 5;
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

    private static boolean canSustainTree(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get());
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
