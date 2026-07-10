package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class EldergroveTreeGenerator {
    private EldergroveTreeGenerator() {
    }

    public static boolean grow(ServerLevel level, BlockPos origin, RandomSource random) {
        int height = 9 + random.nextInt(4);

        if (!canGrow(level, origin, height)) {
            return false;
        }

        placeTrunk(level, origin, height, random);
        placeCrown(level, origin, height, random);

        return true;
    }

    private static void placeTrunk(ServerLevel level, BlockPos origin, int height, RandomSource random) {
        BlockState log = EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState();
        BlockState heart = EldergroveBlocks.GROVE_HEART.get().defaultBlockState();
        boolean placedHeart = false;

        for (int y = 0; y < height; y++) {
            boolean canPlaceHeart = y > 2 && y < height - 3 && !placedHeart && random.nextInt(12) == 0;
            setTrunkBlock(level, origin.offset(0, y, 0), canPlaceHeart ? heart : log);
            setTrunkBlock(level, origin.offset(1, y, 0), log);
            setTrunkBlock(level, origin.offset(0, y, 1), log);
            setTrunkBlock(level, origin.offset(1, y, 1), log);
            if (canPlaceHeart) {
                placedHeart = true;
            }
        }

        int shoulderY = 2 + random.nextInt(2);
        setTrunkBlock(level, origin.offset(-1, 0, 0), log);
        setTrunkBlock(level, origin.offset(-1, 1, 0), log);
        setTrunkBlock(level, origin.offset(2, 0, 1), log);
        setTrunkBlock(level, origin.offset(2, 1, 1), log);
        setTrunkBlock(level, origin.offset(0, 0, -1), log);
        setTrunkBlock(level, origin.offset(0, 1, -1), log);
        setTrunkBlock(level, origin.offset(1, 0, 2), log);
        setTrunkBlock(level, origin.offset(1, 1, 2), log);

        setTrunkBlock(level, origin.offset(-1, shoulderY, 0), log);
        setTrunkBlock(level, origin.offset(2, shoulderY, 1), log);
        setTrunkBlock(level, origin.offset(0, shoulderY, -1), log);
        setTrunkBlock(level, origin.offset(1, shoulderY, 2), log);

        int topY = height;
        setTrunkBlock(level, origin.offset(0, topY, 0), log);
        setTrunkBlock(level, origin.offset(1, topY, 0), log);
        setTrunkBlock(level, origin.offset(0, topY, 1), log);
        setTrunkBlock(level, origin.offset(1, topY, 1), log);

        placeCanopyBranches(level, origin, height, random, log);
    }

    private static void placeCanopyBranches(ServerLevel level, BlockPos origin, int height, RandomSource random, BlockState log) {
        int y = height - 2;
        int[][] directions = new int[][]{
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {-1, 1}, {1, -1}, {-1, -1}
        };

        for (int[] direction : directions) {
            int length = 2 + random.nextInt(3);
            int dx = direction[0];
            int dz = direction[1];
            for (int step = 1; step <= length; step++) {
                int branchY = y + step / 3;
                BlockPos branchPos = origin.offset(0, branchY, 0).offset(dx * step, 0, dz * step);
                setTrunkBlock(level, branchPos, log);
            }
        }
    }

    private static void placeCrown(ServerLevel level, BlockPos origin, int height, RandomSource random) {
        BlockPos center = origin.offset(0, height - 1, 0);
        int bottom = height - 5;
        int top = height + 4;

        for (int y = bottom; y <= top; y++) {
            int relY = y - height;
            double yShape = Math.abs(relY + 1) * 0.55D;
            double radius = 4.8D - yShape;
            if (relY > 1) {
                radius -= relY * 0.35D;
            }
            if (relY < -3) {
                radius -= 0.6D;
            }

            for (int x = -5; x <= 6; x++) {
                for (int z = -5; z <= 6; z++) {
                    double dx = x - 0.5D;
                    double dz = z - 0.5D;
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    double ragged = random.nextDouble() * 0.75D;
                    if (distance <= radius + ragged) {
                        BlockPos leafPos = center.offset(x, relY, z);
                        if (canReplaceWithLeaves(level, leafPos)) {
                            setLeaves(level, leafPos);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 14; i++) {
            int x = random.nextInt(11) - 5;
            int z = random.nextInt(11) - 5;
            int y = height - 4 + random.nextInt(8);
            BlockPos leafPos = origin.offset(x, y, z);
            if (canReplaceWithLeaves(level, leafPos)) {
                setLeaves(level, leafPos);
            }
        }
    }

    private static boolean canGrow(ServerLevel level, BlockPos origin, int height) {
        if (!canSustainTree(level.getBlockState(origin.below()))) {
            return false;
        }

        for (int y = 0; y <= height + 4; y++) {
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

    private static boolean canReplaceTreeSpace(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES) || state.is(EldergroveBlocks.ELDERWOOD_SAPLING.get());
    }

    private static boolean canReplaceWithLeaves(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES);
    }

    private static void setTrunkBlock(ServerLevel level, BlockPos pos, BlockState state) {
        BlockState current = level.getBlockState(pos);
        if (current.isAir() || current.canBeReplaced() || current.is(BlockTags.LEAVES) || current.is(EldergroveBlocks.ELDERWOOD_SAPLING.get())) {
            level.setBlock(pos, state, 3);
        }
    }

    private static void setLeaves(ServerLevel level, BlockPos pos) {
        int distance = distanceToNearestLog(level, pos);
        level.setBlock(
                pos,
                EldergroveBlocks.ELDERWOOD_LEAVES.get().defaultBlockState()
                        .setValue(LeavesBlock.PERSISTENT, false)
                        .setValue(LeavesBlock.DISTANCE, distance),
                3
        );
    }

    private static int distanceToNearestLog(ServerLevel level, BlockPos pos) {
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
