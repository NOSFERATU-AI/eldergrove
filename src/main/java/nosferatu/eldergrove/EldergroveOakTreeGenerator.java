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
        int height = 9 + random.nextInt(4);
        if (!canGrow(level, origin, height)) {
            return false;
        }

        BlockState verticalLog = Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, Direction.Axis.Y);
        for (int y = 0; y <= height; y++) {
            setBlock(level, origin.above(y), verticalLog);
        }

        // Low buttress roots keep the tree grounded and stop it looking like a pole with a ball on top.
        if (random.nextBoolean()) {
            placeLowRoot(level, origin.above(1), Direction.NORTH, 2 + random.nextInt(2));
        }
        if (random.nextBoolean()) {
            placeLowRoot(level, origin.above(1), Direction.SOUTH, 2 + random.nextInt(2));
        }
        if (random.nextBoolean()) {
            placeLowRoot(level, origin.above(1), Direction.WEST, 2 + random.nextInt(2));
        }
        if (random.nextBoolean()) {
            placeLowRoot(level, origin.above(1), Direction.EAST, 2 + random.nextInt(2));
        }

        // Lower wide tier: broad side branches with their own leaf masses.
        for (int i = 0; i < HORIZONTAL_DIRECTIONS.length; i++) {
            Direction dir = HORIZONTAL_DIRECTIONS[(i + random.nextInt(HORIZONTAL_DIRECTIONS.length)) % HORIZONTAL_DIRECTIONS.length];
            int startY = height - 5 + random.nextInt(2);
            int length = 4 + random.nextInt(3);
            BlockPos end = placeBranch(level, origin.above(startY), dir.getStepX() * length, 1, dir.getStepZ() * length, random);
            placeOakLeafNode(level, end.above(), 3, random);
            placeBranchLeaves(level, origin.above(startY), end, random);
        }

        // Diagonal tier: breaks symmetry and gives the crown different sides/levels.
        placeOptionalDiagonalBranch(level, origin, height - 4, Direction.NORTH, Direction.EAST, random);
        placeOptionalDiagonalBranch(level, origin, height - 4, Direction.NORTH, Direction.WEST, random);
        placeOptionalDiagonalBranch(level, origin, height - 3, Direction.SOUTH, Direction.EAST, random);
        placeOptionalDiagonalBranch(level, origin, height - 3, Direction.SOUTH, Direction.WEST, random);

        // Upper tier: shorter branches tucked into the crown.
        for (int i = 0; i < 3; i++) {
            Direction dir = HORIZONTAL_DIRECTIONS[random.nextInt(HORIZONTAL_DIRECTIONS.length)];
            int length = 2 + random.nextInt(3);
            BlockPos end = placeBranch(level, origin.above(height - 1 + random.nextInt(2)), dir.getStepX() * length, 1, dir.getStepZ() * length, random);
            placeOakLeafNode(level, end.above(), 2, random);
        }

        placeLayeredTopCrown(level, origin.above(height), random);
        return true;
    }

    private static void placeLowRoot(LevelAccessor level, BlockPos start, Direction dir, int length) {
        BlockState log = Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, dir.getAxis());
        for (int step = 1; step <= length; step++) {
            BlockPos pos = start.relative(dir, step).below(step > 2 ? 1 : 0);
            setBlock(level, pos, log);
        }
    }

    private static void placeOptionalDiagonalBranch(LevelAccessor level, BlockPos origin, int y, Direction first, Direction second, RandomSource random) {
        if (random.nextInt(3) == 0) {
            return;
        }
        int length = 3 + random.nextInt(3);
        BlockPos end = placeBranch(
                level,
                origin.above(y),
                first.getStepX() * length + second.getStepX() * (length - 1),
                1 + random.nextInt(2),
                first.getStepZ() * length + second.getStepZ() * (length - 1),
                random
        );
        placeOakLeafNode(level, end.above(), 2 + random.nextInt(2), random);
        placeBranchLeaves(level, origin.above(y), end, random);
    }

    private static BlockPos placeBranch(LevelAccessor level, BlockPos start, int dx, int dy, int dz, RandomSource random) {
        int steps = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
        BlockPos last = start;
        for (int step = 1; step <= steps; step++) {
            int x = Math.round(dx * (step / (float) steps));
            int y = Math.round(dy * (step / (float) steps));
            int z = Math.round(dz * (step / (float) steps));
            BlockPos pos = start.offset(x, y, z);
            Direction.Axis axis = Math.abs(dx) >= Math.abs(dz) ? Direction.Axis.X : Direction.Axis.Z;
            if (Math.abs(y) > Math.abs(x) + Math.abs(z) && random.nextBoolean()) {
                axis = Direction.Axis.Y;
            }
            setBlock(level, pos, Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, axis));
            last = pos;
        }
        return last;
    }

    private static void placeLayeredTopCrown(LevelAccessor level, BlockPos center, RandomSource random) {
        // Several overlapping layers instead of one round blob.
        placeOakLeafNode(level, center.offset(0, 0, 0), 3, random);
        placeOakLeafNode(level, center.offset(2, -1, 1), 2, random);
        placeOakLeafNode(level, center.offset(-2, -1, -1), 2, random);
        placeOakLeafNode(level, center.offset(1, 1, -2), 2, random);
        placeOakLeafNode(level, center.offset(-1, 1, 2), 2, random);

        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            if (random.nextBoolean()) {
                placeOakLeafNode(level, center.relative(dir, 3).below(), 2, random);
            }
        }
    }

    private static void placeOakLeafNode(LevelAccessor level, BlockPos center, int radius, RandomSource random) {
        for (int y = -2; y <= 2; y++) {
            int layerRadius;
            if (y == -2) {
                layerRadius = Math.max(1, radius - 1);
            } else if (y == -1 || y == 0) {
                layerRadius = radius;
            } else if (y == 1) {
                layerRadius = Math.max(1, radius - 1);
            } else {
                layerRadius = Math.max(1, radius - 2);
            }

            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    int edge = Math.abs(x) + Math.abs(z);
                    boolean corner = Math.abs(x) == layerRadius && Math.abs(z) == layerRadius;
                    boolean openEdge = edge > layerRadius + 1;
                    if (corner || (openEdge && random.nextBoolean())) {
                        continue;
                    }
                    if (y > 0 && edge > layerRadius && random.nextBoolean()) {
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

    private static void placeBranchLeaves(LevelAccessor level, BlockPos start, BlockPos end, RandomSource random) {
        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        int dz = end.getZ() - start.getZ();
        int steps = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
        for (int step = 1; step <= steps; step++) {
            if (step % 2 == 0 || random.nextBoolean()) {
                int x = Math.round(dx * (step / (float) steps));
                int y = Math.round(dy * (step / (float) steps));
                int z = Math.round(dz * (step / (float) steps));
                BlockPos center = start.offset(x, y, z).above();
                placeSmallLeafPatch(level, center, random);
            }
        }
    }

    private static void placeSmallLeafPatch(LevelAccessor level, BlockPos center, RandomSource random) {
        for (int y = -1; y <= 1; y++) {
            int radius = y == 0 ? 2 : 1;
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
        if (!canSustainTree(level.getBlockState(origin.below()))) {
            return false;
        }
        for (int y = 0; y <= height + 5; y++) {
            int radius = y < height - 5 ? 2 : 7;
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
                        .setValue(LeavesBlock.DISTANCE, 2),
                3
        );
    }
}