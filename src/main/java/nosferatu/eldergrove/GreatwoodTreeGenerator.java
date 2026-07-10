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
        int height = 16 + random.nextInt(5);
        if (!canGrow(level, origin, height)) {
            return false;
        }

        placeMassiveTrunk(level, origin, height, random);
        placeGreatBranches(level, origin, height, random);
        placeGreatwoodCrown(level, origin, height, random);
        EldergroveGroundPlants.placeVishroomsNearTree(level, origin, random, 8, 9);
        return true;
    }

    private static void placeMassiveTrunk(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        BlockState verticalLog = log(Direction.Axis.Y);

        // Old Thaumcraft-like greatwood: a heavy 2x2 core with buttress roots, not a thin pole.
        for (int y = 0; y < height; y++) {
            setLog(level, origin.offset(0, y, 0), verticalLog);
            setLog(level, origin.offset(1, y, 0), verticalLog);
            setLog(level, origin.offset(0, y, 1), verticalLog);
            setLog(level, origin.offset(1, y, 1), verticalLog);

            if (y < 4) {
                setLog(level, origin.offset(-1, y, 0), verticalLog);
                setLog(level, origin.offset(2, y, 1), verticalLog);
                setLog(level, origin.offset(0, y, -1), verticalLog);
                setLog(level, origin.offset(1, y, 2), verticalLog);
            }

            if (y > 6 && y < height - 3 && random.nextInt(4) == 0) {
                Direction side = CARDINALS[random.nextInt(CARDINALS.length)];
                setLog(level, origin.offset(1, y, 1).relative(side), verticalLog);
            }
        }

        placeRoot(level, origin.offset(0, 1, 0), Direction.WEST, 5, random);
        placeRoot(level, origin.offset(1, 1, 1), Direction.EAST, 5, random);
        placeRoot(level, origin.offset(0, 1, 1), Direction.SOUTH, 4 + random.nextInt(2), random);
        placeRoot(level, origin.offset(1, 1, 0), Direction.NORTH, 4 + random.nextInt(2), random);
    }

    private static void placeRoot(LevelAccessor level, BlockPos start, Direction direction, int length, RandomSource random) {
        BlockState horizontalLog = log(direction.getAxis());
        for (int i = 1; i <= length; i++) {
            BlockPos pos = start.relative(direction, i).below(i > 2 ? 1 : 0);
            setLog(level, pos, horizontalLog);
            if (i < length && random.nextBoolean()) {
                setLog(level, pos.below(), horizontalLog);
            }
        }
    }

    private static void placeGreatBranches(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        int lowY = height - 10;
        int midY = height - 7;
        int highY = height - 4;

        // The lower ring creates the wide, visible wooden arms seen in the old greatwood silhouette.
        for (Direction direction : CARDINALS) {
            int y = lowY + random.nextInt(3);
            int length = 8 + random.nextInt(4);
            BlockPos start = branchStart(origin, direction, y);
            BlockPos end = placeThickBranch(level, start, direction, length, random, true);
            placeFlatCrownBlob(level, end.above(1), 5 + random.nextInt(2), 4 + random.nextInt(2), random);
        }

        // A second, uneven ring overlaps the lower one so the canopy becomes one huge irregular mass.
        for (Direction direction : CARDINALS) {
            if (random.nextInt(4) == 0) {
                continue;
            }
            int y = midY + random.nextInt(3);
            int length = 6 + random.nextInt(4);
            BlockPos start = branchStart(origin, direction, y);
            BlockPos end = placeThickBranch(level, start, direction, length, random, false);
            placeFlatCrownBlob(level, end.above(1), 4 + random.nextInt(2), 3 + random.nextInt(2), random);
        }

        // Diagonal limbs break the cross shape and make it feel more natural.
        for (int i = 0; i < 3; i++) {
            Direction first = random.nextBoolean() ? Direction.NORTH : Direction.SOUTH;
            Direction second = random.nextBoolean() ? Direction.EAST : Direction.WEST;
            BlockPos end = placeDiagonalBranch(level, origin.offset(0, midY + random.nextInt(4), 0), first, second, 5 + random.nextInt(4), random);
            placeFlatCrownBlob(level, end.above(1), 4, 3 + random.nextInt(2), random);
        }

        Direction topDirection = CARDINALS[random.nextInt(CARDINALS.length)];
        BlockPos topEnd = placeThickBranch(level, origin.offset(0, highY, 0), topDirection, 6 + random.nextInt(3), random, false);
        placeFlatCrownBlob(level, topEnd.above(1), 4 + random.nextInt(2), 3, random);
    }

    private static BlockPos branchStart(BlockPos origin, Direction direction, int y) {
        return origin.offset(direction.getStepX() > 0 ? 1 : 0, y, direction.getStepZ() > 0 ? 1 : 0);
    }

    private static BlockPos placeThickBranch(LevelAccessor level, BlockPos start, Direction direction, int length, RandomSource random, boolean heavy) {
        BlockState horizontalLog = log(direction.getAxis());
        BlockPos last = start;
        for (int i = 1; i <= length; i++) {
            int rise = i / 4;
            last = start.relative(direction, i).above(rise);
            setLog(level, last, horizontalLog);

            if (heavy && i < length - 1) {
                setLog(level, last.below(), horizontalLog);
            }
            if (i < 4) {
                setLog(level, last.relative(direction.getClockWise()), horizontalLog);
                setLog(level, last.relative(direction.getCounterClockWise()), horizontalLog);
            }
            if (i > 3 && i < length - 2 && random.nextInt(4) == 0) {
                Direction side = random.nextBoolean() ? direction.getClockWise() : direction.getCounterClockWise();
                placeSmallSideBranch(level, last, side, 2 + random.nextInt(3), random);
            }
        }
        return last;
    }

    private static void placeSmallSideBranch(LevelAccessor level, BlockPos start, Direction direction, int length, RandomSource random) {
        BlockState horizontalLog = log(direction.getAxis());
        for (int i = 1; i <= length; i++) {
            BlockPos pos = start.relative(direction, i).above(i / 3);
            setLog(level, pos, horizontalLog);
            if (i == length) {
                placeFlatCrownBlob(level, pos.above(), 3, 2, random);
            }
        }
    }

    private static BlockPos placeDiagonalBranch(LevelAccessor level, BlockPos start, Direction first, Direction second, int length, RandomSource random) {
        BlockPos last = start;
        for (int i = 1; i <= length; i++) {
            int rise = i / 4;
            last = start.relative(first, i).relative(second, i / 2).above(rise);
            setLog(level, last, log(i % 2 == 0 ? first.getAxis() : second.getAxis()));
            if (i < 4) {
                setLog(level, last.below(), log(first.getAxis()));
            }
        }
        return last;
    }

    private static void placeGreatwoodCrown(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        // Several overlapping, flattened masses. This avoids the ugly single sphere and matches the old greatwood canopy better.
        int baseY = height - 9;
        placeFlatCrownBlob(level, origin.offset(0, baseY, 0), 6, 4, random);
        placeFlatCrownBlob(level, origin.offset(3, baseY + 1, 3), 6, 4, random);
        placeFlatCrownBlob(level, origin.offset(-4, baseY + 1, 2), 6, 4, random);
        placeFlatCrownBlob(level, origin.offset(2, baseY + 2, -4), 6, 4, random);
        placeFlatCrownBlob(level, origin.offset(-3, baseY + 2, -3), 5, 4, random);

        placeFlatCrownBlob(level, origin.offset(0, height - 4, 0), 7, 4, random);
        placeFlatCrownBlob(level, origin.offset(5, height - 3, 0), 5, 3, random);
        placeFlatCrownBlob(level, origin.offset(-5, height - 3, 1), 5, 3, random);
        placeFlatCrownBlob(level, origin.offset(1, height - 2, 5), 5, 3, random);
        placeFlatCrownBlob(level, origin.offset(0, height, 0), 4, 3, random);
    }

    private static void placeFlatCrownBlob(LevelAccessor level, BlockPos center, int radius, int verticalRadius, RandomSource random) {
        for (int y = -verticalRadius; y <= verticalRadius; y++) {
            int layerRadius = Math.max(2, radius - Math.abs(y));
            for (int x = -layerRadius; x <= layerRadius + 1; x++) {
                for (int z = -layerRadius; z <= layerRadius + 1; z++) {
                    int distance = Math.abs(x) + Math.abs(z);
                    boolean farCorner = Math.abs(x) > layerRadius - 1 && Math.abs(z) > layerRadius - 1;
                    boolean openGap = distance > layerRadius + 2 && random.nextBoolean();
                    boolean raggedHole = distance > 2 && random.nextInt(42) == 0;
                    if (farCorner || openGap || raggedHole) {
                        continue;
                    }

                    BlockPos pos = center.offset(x, y, z);
                    if (canReplace(level, pos)) {
                        setLeaves(level, pos);
                    }

                    if (y < 0 && distance > layerRadius - 2 && random.nextInt(8) == 0) {
                        BlockPos hanging = pos.below();
                        if (canReplace(level, hanging)) {
                            setLeaves(level, hanging);
                        }
                    }
                }
            }
        }
    }

    private static boolean canGrow(LevelAccessor level, BlockPos origin, int height) {
        if (!level.getBlockState(origin.below()).is(BlockTags.DIRT)) {
            return false;
        }
        for (int y = 0; y <= height + 4; y++) {
            int radius = y < 5 ? 6 : y > height - 11 ? 12 : 5;
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