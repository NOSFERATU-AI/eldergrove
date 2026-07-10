package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class EldergroveTreeGenerator {
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
        BlockState log = EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState();
        BlockState heart = EldergroveBlocks.GROVE_HEART.get().defaultBlockState();
        boolean placedHeart = false;

        for (int y = 0; y < height; y++) {
            boolean canPlaceHeart = y > 2 && y < height - 3 && !placedHeart && random.nextInt(10) == 0;
            setTrunkBlock(level, origin.offset(0, y, 0), canPlaceHeart ? heart : log);
            setTrunkBlock(level, origin.offset(1, y, 0), log);
            setTrunkBlock(level, origin.offset(0, y, 1), log);
            setTrunkBlock(level, origin.offset(1, y, 1), log);
            if (canPlaceHeart) {
                placedHeart = true;
            }
        }

        // Thaumcraft-like buttress roots around the silver trunk.
        setTrunkBlock(level, origin.offset(-1, 0, 0), log);
        setTrunkBlock(level, origin.offset(-1, 1, 0), log);
        setTrunkBlock(level, origin.offset(2, 0, 1), log);
        setTrunkBlock(level, origin.offset(2, 1, 1), log);
        setTrunkBlock(level, origin.offset(0, 0, -1), log);
        setTrunkBlock(level, origin.offset(0, 1, -1), log);
        setTrunkBlock(level, origin.offset(1, 0, 2), log);
        setTrunkBlock(level, origin.offset(1, 1, 2), log);

        // Hidden canopy supports keep natural leaf decay working after the trunk is chopped.
        placeCanopyBranch(level, origin.offset(0, height - 2, 0), 1, 0, 2 + random.nextInt(2), log);
        placeCanopyBranch(level, origin.offset(1, height - 2, 1), -1, 0, 2 + random.nextInt(2), log);
        placeCanopyBranch(level, origin.offset(0, height - 2, 1), 0, 1, 2 + random.nextInt(2), log);
        placeCanopyBranch(level, origin.offset(1, height - 2, 0), 0, -1, 2 + random.nextInt(2), log);

        if (random.nextBoolean()) {
            placeCanopyBranch(level, origin.offset(0, height - 1, 0), 1, 1, 2, log);
            placeCanopyBranch(level, origin.offset(1, height - 1, 1), -1, -1, 2, log);
        } else {
            placeCanopyBranch(level, origin.offset(1, height - 1, 0), -1, 1, 2, log);
            placeCanopyBranch(level, origin.offset(0, height - 1, 1), 1, -1, 2, log);
        }
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
            int radius = y < 2 ? 2 : y >= height - 5 ? 6 : 3;
            for (int x = -radius; x <= radius + 1; x++) {
                for (int z = -radius; z <= radius + 1; z++) {
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
