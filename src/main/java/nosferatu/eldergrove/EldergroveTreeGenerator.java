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
        int height = 8 + random.nextInt(7);

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
            boolean canPlaceHeart = y > 2 && y < height - 3 && !placedHeart && random.nextInt(10) == 0;
            setTrunkBlock(level, origin.offset(0, y, 0), canPlaceHeart ? heart : log);
            setTrunkBlock(level, origin.offset(1, y, 0), log);
            setTrunkBlock(level, origin.offset(0, y, 1), log);
            setTrunkBlock(level, origin.offset(1, y, 1), log);
            if (canPlaceHeart) {
                placedHeart = true;
            }
        }

        // Silverwood-style buttress roots: visible near the ground, not a full ugly cross up the trunk.
        setTrunkBlock(level, origin.offset(-1, 0, 0), log);
        setTrunkBlock(level, origin.offset(-1, 1, 0), log);
        setTrunkBlock(level, origin.offset(2, 0, 1), log);
        setTrunkBlock(level, origin.offset(2, 1, 1), log);
        setTrunkBlock(level, origin.offset(0, 0, -1), log);
        setTrunkBlock(level, origin.offset(0, 1, -1), log);
        setTrunkBlock(level, origin.offset(1, 0, 2), log);
        setTrunkBlock(level, origin.offset(1, 1, 2), log);

        // A few short hidden supports inside the crown, so leaves decay naturally only when the tree is chopped.
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

    private static void placeCanopyBranch(ServerLevel level, BlockPos start, int dx, int dz, int length, BlockState log) {
        for (int step = 1; step <= length; step++) {
            int y = step == length ? 1 : 0;
            setTrunkBlock(level, start.offset(dx * step, y, dz * step), log);
        }
    }

    private static void placeCrown(ServerLevel level, BlockPos origin, int height, RandomSource random) {
        // Center is offset half a block because the trunk is 2x2. This keeps the crown round around the trunk.
        double centerX = origin.getX() + 0.5D;
        double centerY = origin.getY() + height - 1.0D;
        double centerZ = origin.getZ() + 0.5D;

        int bottom = height - 5;
        int top = height + 3;

        for (int y = bottom; y <= top; y++) {
            double dy = (origin.getY() + y - centerY) / 3.8D;
            double vertical = Math.abs(dy);
            double radius = 4.9D * (1.0D - vertical * 0.42D);

            if (y <= height - 5) {
                radius = 1.6D;
            } else if (y == height - 4) {
                radius = 3.0D;
            } else if (y == height + 3) {
                radius = 1.8D;
            }

            for (int x = -5; x <= 6; x++) {
                for (int z = -5; z <= 6; z++) {
                    BlockPos leafPos = origin.offset(x, y, z);
                    double dx = leafPos.getX() + 0.5D - centerX;
                    double dz = leafPos.getZ() + 0.5D - centerZ;
                    double distance = Math.sqrt(dx * dx + dz * dz);

                    double edgeNoise = stableNoise(leafPos) * 0.45D;
                    if (distance <= radius + edgeNoise && !isCornerCut(distance, radius, leafPos, random)) {
                        if (canReplaceWithLeaves(level, leafPos)) {
                            setLeaves(level, leafPos);
                        }
                    }
                }
            }
        }

        // Small cap on top, typical rounded silverwood silhouette.
        for (int x = -2; x <= 3; x++) {
            for (int z = -2; z <= 3; z++) {
                BlockPos leafPos = origin.offset(x, height + 4, z);
                double dx = x - 0.5D;
                double dz = z - 0.5D;
                if (dx * dx + dz * dz <= 4.6D && canReplaceWithLeaves(level, leafPos)) {
                    setLeaves(level, leafPos);
                }
            }
        }
    }

    private static boolean isCornerCut(double distance, double radius, BlockPos pos, RandomSource random) {
        if (distance < radius - 0.6D) {
            return false;
        }
        return Math.floorMod(pos.getX() * 31 + pos.getY() * 17 + pos.getZ() * 47, 7) == 0 && random.nextInt(4) != 0;
    }

    private static double stableNoise(BlockPos pos) {
        int value = Math.floorMod(pos.getX() * 734287 + pos.getY() * 912271 + pos.getZ() * 438289, 1000);
        return value / 1000.0D;
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
