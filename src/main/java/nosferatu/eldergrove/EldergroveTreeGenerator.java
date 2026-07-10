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

public final class EldergroveTreeGenerator {
    private static final EnumProperty<Direction.Axis> AXIS = RotatedPillarBlock.AXIS;

    private EldergroveTreeGenerator() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 8 + random.nextInt(5);

        if (!canGrow(level, origin, height)) {
            return false;
        }

        placeTrunk(level, origin, height, random);
        placeCrown(level, origin, height, random);
        EldergroveGroundPlants.placeNearTree(level, origin, random, 8, 5);

        return true;
    }

    private static void placeTrunk(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        BlockState verticalLog = EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState().setValue(AXIS, Direction.Axis.Y);
        BlockState xLog = EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState().setValue(AXIS, Direction.Axis.X);
        BlockState zLog = EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState().setValue(AXIS, Direction.Axis.Z);
        BlockState heart = EldergroveBlocks.GROVE_HEART.get().defaultBlockState();
        boolean placedHeart = false;

        // Silverwood-like trunk from Thaumcraft: one core with four vertical side ribs, not a plain 2x2 pillar.
        for (int y = 0; y < height; y++) {
            boolean canPlaceHeart = y > 2 && y < height - 3 && !placedHeart && random.nextInt(12) == 0;
            setTrunkBlock(level, origin.offset(0, y, 0), canPlaceHeart ? heart : verticalLog);
            setTrunkBlock(level, origin.offset(-1, y, 0), verticalLog);
            setTrunkBlock(level, origin.offset(1, y, 0), verticalLog);
            setTrunkBlock(level, origin.offset(0, y, -1), verticalLog);
            setTrunkBlock(level, origin.offset(0, y, 1), verticalLog);
            if (canPlaceHeart) {
                placedHeart = true;
            }
        }

        // Low buttress roots: squat base blocks plus short horizontal roots, closer to the original silverwood silhouette.
        setTrunkBlock(level, origin.offset(-1, 0, -1), verticalLog);
        setTrunkBlock(level, origin.offset(1, 0, 1), verticalLog);
        setTrunkBlock(level, origin.offset(-1, 0, 1), verticalLog);
        setTrunkBlock(level, origin.offset(1, 0, -1), verticalLog);

        if (random.nextInt(3) != 0) setTrunkBlock(level, origin.offset(-1, 1, -1), verticalLog);
        if (random.nextInt(3) != 0) setTrunkBlock(level, origin.offset(1, 1, 1), verticalLog);
        if (random.nextInt(3) != 0) setTrunkBlock(level, origin.offset(-1, 1, 1), verticalLog);
        if (random.nextInt(3) != 0) setTrunkBlock(level, origin.offset(1, 1, -1), verticalLog);

        setTrunkBlock(level, origin.offset(-2, 0, 0), xLog);
        setTrunkBlock(level, origin.offset(2, 0, 0), xLog);
        setTrunkBlock(level, origin.offset(0, 0, -2), zLog);
        setTrunkBlock(level, origin.offset(0, 0, 2), zLog);
        if (random.nextBoolean()) setTrunkBlock(level, origin.offset(-3, 0, 0), xLog);
        if (random.nextBoolean()) setTrunkBlock(level, origin.offset(3, 0, 0), xLog);
        if (random.nextBoolean()) setTrunkBlock(level, origin.offset(0, 0, -3), zLog);
        if (random.nextBoolean()) setTrunkBlock(level, origin.offset(0, 0, 3), zLog);

        int shoulderY = height - 4;
        setTrunkBlock(level, origin.offset(-1, shoulderY, -1), verticalLog);
        setTrunkBlock(level, origin.offset(1, shoulderY, 1), verticalLog);
        setTrunkBlock(level, origin.offset(-1, shoulderY, 1), verticalLog);
        setTrunkBlock(level, origin.offset(1, shoulderY, -1), verticalLog);
        if (random.nextInt(3) == 0) setTrunkBlock(level, origin.offset(-1, shoulderY - 1, -1), verticalLog);
        if (random.nextInt(3) == 0) setTrunkBlock(level, origin.offset(1, shoulderY - 1, 1), verticalLog);
        if (random.nextInt(3) == 0) setTrunkBlock(level, origin.offset(-1, shoulderY - 1, 1), verticalLog);
        if (random.nextInt(3) == 0) setTrunkBlock(level, origin.offset(1, shoulderY - 1, -1), verticalLog);

        setTrunkBlock(level, origin.offset(-2, shoulderY, 0), xLog);
        setTrunkBlock(level, origin.offset(2, shoulderY, 0), xLog);
        setTrunkBlock(level, origin.offset(0, shoulderY, -2), zLog);
        setTrunkBlock(level, origin.offset(0, shoulderY, 2), zLog);

        // Hidden canopy supports keep natural leaf decay working after the tree is chopped.
        placeCanopyBranch(level, origin.offset(0, height - 2, 0), 1, 0, 2 + random.nextInt(2), xLog);
        placeCanopyBranch(level, origin.offset(0, height - 2, 0), -1, 0, 2 + random.nextInt(2), xLog);
        placeCanopyBranch(level, origin.offset(0, height - 2, 0), 0, 1, 2 + random.nextInt(2), zLog);
        placeCanopyBranch(level, origin.offset(0, height - 2, 0), 0, -1, 2 + random.nextInt(2), zLog);
    }

    private static void placeCanopyBranch(LevelAccessor level, BlockPos start, int dx, int dz, int length, BlockState log) {
        for (int step = 1; step <= length; step++) {
            int y = step == length ? 1 : 0;
            setTrunkBlock(level, start.offset(dx * step, y, dz * step), log);
        }
    }

    private static void placeCrown(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        // Closer to Thaumcraft Silverwood: irregular clustered crown, not a smooth ball.
        int start = height - 5;
        int end = height + 3 + random.nextInt(3);

        for (int y = start; y <= end; y++) {
            int crownCenterY = Math.min(Math.max(y, height - 3), height);
            for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    double dx = x;
                    double dy = y - crownCenterY;
                    double dz = z;
                    double distance = dx * dx + dy * dy + dz * dz;

                    if (distance < 10 + random.nextInt(8)) {
                        BlockPos leafPos = origin.offset(x, y, z);
                        if (canReplaceWithLeaves(level, leafPos)) {
                            setLeaves(level, leafPos);
                        }
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
            int radius = y < 2 ? 3 : y >= height - 5 ? 6 : 3;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (!canReplaceTreeSpace(level, pos)) {
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

    private static boolean canReplaceTreeSpace(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES) || state.is(EldergroveBlocks.ELDERWOOD_SAPLING.get());
    }

    private static boolean canReplaceWithLeaves(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES);
    }

    private static void setTrunkBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockState current = level.getBlockState(pos);
        if (current.isAir() || current.canBeReplaced() || current.is(BlockTags.LEAVES) || current.is(EldergroveBlocks.ELDERWOOD_SAPLING.get())) {
            level.setBlock(pos, state, 3);
        }
    }

    private static void setLeaves(LevelAccessor level, BlockPos pos) {
        int distance = distanceToNearestLog(level, pos);
        level.setBlock(
                pos,
                EldergroveBlocks.ELDERWOOD_LEAVES.get().defaultBlockState()
                        .setValue(LeavesBlock.PERSISTENT, false)
                        .setValue(LeavesBlock.DISTANCE, distance),
                3
        );
    }

    private static int distanceToNearestLog(LevelAccessor level, BlockPos pos) {
        int best = 7;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int y = -6; y <= 6; y++) {
            for (int x = -6; x <= 6; x++) {
                for (int z = -6; z <= 6; z++) {
                    int distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (distance >= best || distance > 6) {
                        continue;
                    }
                    mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (level.getBlockState(mutable).is(BlockTags.LOGS)) {
                        best = Math.max(1, distance);
                    }
                }
            }
        }
        return best;
    }
}
