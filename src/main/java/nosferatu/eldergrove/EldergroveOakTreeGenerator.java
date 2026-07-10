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
    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST
    };

    private EldergroveOakTreeGenerator() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 7 + random.nextInt(4);
        if (!canGrow(level, origin, height)) {
            return false;
        }

        BlockState verticalLog = Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, Direction.Axis.Y);
        for (int y = 0; y < height; y++) {
            setBlock(level, origin.above(y), verticalLog);
        }

        int branchBase = Math.max(3, height - 4);
        int branchCount = 3 + random.nextInt(3);
        for (int i = 0; i < branchCount; i++) {
            Direction dir = HORIZONTAL_DIRECTIONS[(i + random.nextInt(HORIZONTAL_DIRECTIONS.length)) % HORIZONTAL_DIRECTIONS.length];
            int startY = branchBase + random.nextInt(3);
            int length = 2 + random.nextInt(3);
            BlockPos branchEnd = placeBigOakBranch(level, origin.above(startY), dir, length);
            placeLeafBlob(level, branchEnd.above(), 2 + random.nextInt(2), random);
        }

        if (random.nextBoolean()) {
            Direction diagonalA = random.nextBoolean() ? Direction.NORTH : Direction.SOUTH;
            Direction diagonalB = random.nextBoolean() ? Direction.EAST : Direction.WEST;
            BlockPos branchEnd = placeDiagonalBranch(level, origin.above(branchBase + 1), diagonalA, diagonalB, 3);
            placeLeafBlob(level, branchEnd.above(), 2, random);
        }

        placeBigOakCrown(level, origin.above(height - 2), random);
        return true;
    }

    private static BlockPos placeBigOakBranch(LevelAccessor level, BlockPos start, Direction dir, int length) {
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

    private static BlockPos placeDiagonalBranch(LevelAccessor level, BlockPos start, Direction first, Direction second, int length) {
        BlockState firstAxisLog = Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, first.getAxis());
        BlockState secondAxisLog = Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, second.getAxis());
        BlockPos last = start;
        for (int step = 1; step <= length; step++) {
            last = start.relative(first, step).relative(second, step).above(step > 1 ? 1 : 0);
            setBlock(level, last, step % 2 == 0 ? firstAxisLog : secondAxisLog);
        }
        return last;
    }

    private static void placeBigOakCrown(LevelAccessor level, BlockPos center, RandomSource random) {
        // Vanilla big-oak feel: compact irregular crown wrapped around the upper trunk, not a flat mushroom cap.
        for (int y = -3; y <= 3; y++) {
            int radius;
            if (y == -3) {
                radius = 2;
            } else if (y == -2 || y == 2) {
                radius = 3;
            } else if (y == -1 || y == 0 || y == 1) {
                radius = 4;
            } else {
                radius = 2;
            }

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int edge = Math.abs(x) + Math.abs(z);
                    boolean hardCorner = Math.abs(x) == radius && Math.abs(z) == radius;
                    boolean upperSparseEdge = y > 1 && edge > radius + 1;
                    if (edge > radius + 2 || hardCorner || (upperSparseEdge && random.nextBoolean())) {
                        continue;
                    }
                    if (random.nextInt(18) == 0 && edge > radius) {
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
