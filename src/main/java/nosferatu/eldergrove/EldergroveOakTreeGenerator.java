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
        int height = 10 + random.nextInt(5);
        if (!canGrow(level, origin, height)) {
            return false;
        }

        BlockState verticalLog = Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, Direction.Axis.Y);
        for (int y = 0; y < height; y++) {
            setBlock(level, origin.above(y), verticalLog);
        }

        int branchBase = height - 5;
        int branches = 4 + random.nextInt(3);
        for (int i = 0; i < branches; i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int startY = branchBase + random.nextInt(4);
            int length = 3 + random.nextInt(3);
            BlockPos end = placeFancyBranch(level, origin.above(startY), dir, length);
            placeLeafBlob(level, end.above(), 2 + random.nextInt(2), random);
        }

        placeTopCrown(level, origin.above(height - 2), random);
        return true;
    }

    private static BlockPos placeFancyBranch(LevelAccessor level, BlockPos start, Direction dir, int length) {
        Direction.Axis axis = dir.getAxis();
        BlockState log = Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, axis);
        BlockPos last = start;
        for (int step = 1; step <= length; step++) {
            int y = step > 2 ? 1 : 0;
            last = start.relative(dir, step).above(y);
            setBlock(level, last, log);
        }
        return last;
    }

    private static void placeTopCrown(LevelAccessor level, BlockPos center, RandomSource random) {
        // Fancy-oak style: layered, irregular, with side lobes instead of one cube of leaves.
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
    }

    private static void placeLeafBlob(LevelAccessor level, BlockPos center, int radius, RandomSource random) {
        for (int y = -1; y <= 1; y++) {
            int layerRadius = y == 0 ? radius : Math.max(1, radius - 1);
            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    int edge = Math.abs(x) + Math.abs(z);
                    if (edge > layerRadius + 1 || (edge == layerRadius + 1 && random.nextBoolean())) {
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
            int radius = y < height - 5 ? 1 : 6;
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
