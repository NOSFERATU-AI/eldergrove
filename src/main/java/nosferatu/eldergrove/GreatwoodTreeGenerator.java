package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class GreatwoodTreeGenerator {
    private static final EnumProperty<Direction.Axis> AXIS = RotatedPillarBlock.AXIS;
    private static final Direction[] CARDINALS = new Direction[]{
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST
    };

    private GreatwoodTreeGenerator() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 18 + random.nextInt(8);
        if (!canGrow(level, origin, height)) {
            return false;
        }

        placeTrunk(level, origin, height, random);
        placeMainBranches(level, origin, height, random);
        placeLayeredCanopy(level, origin, height, random);
        EldergroveGroundPlants.placeVishroomsNearTree(level, origin, random, 8, 9);
        return true;
    }

    private static void placeTrunk(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        BlockState verticalLog = log(Direction.Axis.Y);

        // Greatwood is a huge old trunk: 2x2 core, heavier base, then tapering shoulder blocks.
        for (int y = 0; y < height; y++) {
            setLog(level, origin.offset(0, y, 0), verticalLog);
            setLog(level, origin.offset(1, y, 0), verticalLog);
            setLog(level, origin.offset(0, y, 1), verticalLog);
            setLog(level, origin.offset(1, y, 1), verticalLog);

            if (y < 5) {
                setLog(level, origin.offset(-1, y, 0), verticalLog);
                setLog(level, origin.offset(2, y, 1), verticalLog);
                setLog(level, origin.offset(0, y, -1), verticalLog);
                setLog(level, origin.offset(1, y, 2), verticalLog);
            } else if (y > height - 7 && y < height - 2 && random.nextInt(3) != 0) {
                setLog(level, origin.offset(-1, y, 0), verticalLog);
                setLog(level, origin.offset(2, y, 1), verticalLog);
            }
        }

        // Big root feet at ground level, like the heavy base of Thaumcraft greatwood.
        placeRoot(level, origin.offset(0, 1, 0), Direction.WEST, 4, random);
        placeRoot(level, origin.offset(1, 1, 1), Direction.EAST, 4, random);
        placeRoot(level, origin.offset(0, 1, 1), Direction.SOUTH, 3 + random.nextInt(2), random);
        placeRoot(level, origin.offset(1, 1, 0), Direction.NORTH, 3 + random.nextInt(2), random);
    }

    private static void placeRoot(LevelAccessor level, BlockPos start, Direction direction, int length, RandomSource random) {
        BlockState horizontalLog = log(direction.getAxis());
        BlockPos pos = start;
        for (int i = 1; i <= length; i++) {
            pos = start.relative(direction, i).below(i > 2 ? 1 : 0);
            setLog(level, pos, horizontalLog);
            if (i < length && random.nextBoolean()) {
                setLog(level, pos.below(), horizontalLog);
            }
        }
    }

    private static void placeMainBranches(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        int firstY = height - 10;
        int secondY = height - 7;
        int thirdY = height - 4;

        for (Direction direction : CARDINALS) {
            int y = firstY + random.nextInt(3);
            int length = 5 + random.nextInt(4);
            BlockPos end = placeBranch(level, origin.offset(direction.getStepX() > 0 ? 1 : 0, y, direction.getStepZ() > 0 ? 1 : 0), direction, length, random);
            placeBranchCanopy(level, end.above(1 + random.nextInt(2)), 3 + random.nextInt(2), random);
        }

        for (Direction direction : CARDINALS) {
            if (random.nextInt(3) == 0) {
                continue;
            }
            int y = secondY + random.nextInt(3);
            int length = 4 + random.nextInt(4);
            BlockPos end = placeBranch(level, origin.offset(direction.getStepX() > 0 ? 1 : 0, y, direction.getStepZ() > 0 ? 1 : 0), direction, length, random);
            placeBranchCanopy(level, end.above(1), 3, random);
        }

        Direction topDirection = CARDINALS[random.nextInt(CARDINALS.length)];
        BlockPos topEnd = placeBranch(level, origin.offset(0, thirdY, 0), topDirection, 6 + random.nextInt(3), random);
        placeBranchCanopy(level, topEnd.above(1), 3 + random.nextInt(2), random);

        if (random.nextBoolean()) {
            Direction diagonalA = random.nextBoolean() ? Direction.NORTH : Direction.SOUTH;
            Direction diagonalB = random.nextBoolean() ? Direction.EAST : Direction.WEST;
            BlockPos diagonalEnd = placeDiagonalBranch(level, origin.offset(0, secondY + 1, 0), diagonalA, diagonalB, 4 + random.nextInt(3), random);
            placeBranchCanopy(level, diagonalEnd.above(1), 3, random);
        }
    }

    private static BlockPos placeBranch(LevelAccessor level, BlockPos start, Direction direction, int length, RandomSource random) {
        BlockState horizontalLog = log(direction.getAxis());
        BlockPos last = start;
        for (int i = 1; i <= length; i++) {
            int rise = i / 3;
            last = start.relative(direction, i).above(rise);
            setLog(level, last, horizontalLog);
            if (i > 2 && random.nextInt(3) == 0) {
                setLog(level, last.below(), horizontalLog);
            }
        }
        return last;
    }

    private static BlockPos placeDiagonalBranch(LevelAccessor level, BlockPos start, Direction first, Direction second, int length, RandomSource random) {
        BlockPos last = start;
        for (int i = 1; i <= length; i++) {
            int rise = i / 3;
            last = start.relative(first, i).relative(second, i).above(rise);
            setLog(level, last, log(i % 2 == 0 ? first.getAxis() : second.getAxis()));
            if (random.nextInt(4) == 0) {
                setLog(level, last.below(), log(first.getAxis()));
            }
        }
        return last;
    }

    private static void placeLayeredCanopy(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        // Greatwood should read as a huge, tiered old crown, not a single sphere.
        placeCanopyLayer(level, origin.above(height - 8), 5, random);
        placeCanopyLayer(level, origin.above(height - 6), 7, random);
        placeCanopyLayer(level, origin.above(height - 4), 8, random);
        placeCanopyLayer(level, origin.above(height - 2), 7, random);
        placeCanopyLayer(level, origin.above(height), 5, random);
        placeCanopyLayer(level, origin.above(height + 2), 3, random);
    }

    private static void placeCanopyLayer(LevelAccessor level, BlockPos center, int radius, RandomSource random) {
        for (int y = -1; y <= 1; y++) {
            int layerRadius = y == 0 ? radius : Math.max(2, radius - 2);
            for (int x = -layerRadius; x <= layerRadius + 1; x++) {
                for (int z = -layerRadius; z <= layerRadius + 1; z++) {
                    int manhattan = Math.abs(x) + Math.abs(z);
                    boolean hardCorner = Math.abs(x) > layerRadius - 1 && Math.abs(z) > layerRadius - 1;
                    boolean raggedEdge = manhattan > layerRadius + 2 && random.nextBoolean();
                    if (hardCorner || raggedEdge || random.nextInt(32) == 0) {
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

    private static void placeBranchCanopy(LevelAccessor level, BlockPos center, int radius, RandomSource random) {
        for (int y = -2; y <= 2; y++) {
            int layerRadius = y == 0 ? radius : Math.max(1, radius - Math.abs(y));
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
        if (!level.getBlockState(origin.below()).is(BlockTags.DIRT)) {
            return false;
        }
        for (int y = 0; y <= height + 5; y++) {
            int radius = y < 5 ? 5 : y > height - 10 ? 10 : 3;
            for (int x = -radius; x <= radius + 1; x++) {
                for (int z = -radius; z <= radius + 1; z++) {
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

    private static BlockState log(Direction.Axis axis) {
        return EldergroveBlocks.GREATWOOD_LOG.get().defaultBlockState().setValue(AXIS, axis);
    }

    private static void setLog(LevelAccessor level, BlockPos pos, BlockState state) {
        if (canReplace(level, pos)) {
            level.setBlock(pos, state, 3);
        }
    }

    private static void setLeaves(LevelAccessor level, BlockPos pos) {
        level.setBlock(
                pos,
                EldergroveBlocks.GREATWOOD_LEAVES.get().defaultBlockState()
                        .setValue(LeavesBlock.PERSISTENT, false)
                        .setValue(LeavesBlock.DISTANCE, 4),
                3
        );
    }
}
