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

        refreshSoil(level, origin);
        placeCrown(level, origin, height, random);
        placeTrunk(level, origin, height, random);

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

        for (int i = 0; i < 16; i++) {
            int x = random.nextInt(11) - 5;
            int z = random.nextInt(11) - 5;
            int y = height - 4 + random.nextInt(8);
            BlockPos leafPos = origin.offset(x, y, z);
            if (canReplaceWithLeaves(level, leafPos)) {
                setLeaves(level, leafPos);
            }
        }
    }

    private static void refreshSoil(ServerLevel level, BlockPos origin) {
        for (int x = -1; x <= 2; x++) {
            for (int z = -1; z <= 2; z++) {
                BlockPos soilPos = origin.offset(x, -1, z);
                BlockState soil = level.getBlockState(soilPos);
                level.sendBlockUpdated(soilPos, soil, soil, 2);
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
        return state.is(BlockTags.DIRT);
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
            level.setBlock(pos, state, 2);
        }
    }

    private static void setLeaves(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, EldergroveBlocks.ELDERWOOD_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 2);
    }
}
