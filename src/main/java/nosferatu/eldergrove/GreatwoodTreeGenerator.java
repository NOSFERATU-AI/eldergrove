package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class GreatwoodTreeGenerator {
    private GreatwoodTreeGenerator() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 15 + random.nextInt(7);
        if (!canGrow(level, origin, height)) {
            return false;
        }

        placeTrunk(level, origin, height, random);
        placeBranches(level, origin, height, random);
        placeCrown(level, origin, height, random);
        EldergroveGroundPlants.placeNearTree(level, origin, random, 12, 7);
        return true;
    }

    private static void placeTrunk(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        BlockState log = EldergroveBlocks.GREATWOOD_LOG.get().defaultBlockState();
        for (int y = 0; y < height; y++) {
            setLog(level, origin.offset(0, y, 0), log);
            setLog(level, origin.offset(1, y, 0), log);
            setLog(level, origin.offset(0, y, 1), log);
            setLog(level, origin.offset(1, y, 1), log);

            if (y < 4) {
                int r = 2 - y / 2;
                setLog(level, origin.offset(-r, y, 0), log);
                setLog(level, origin.offset(1 + r, y, 1), log);
                setLog(level, origin.offset(0, y, -r), log);
                setLog(level, origin.offset(1, y, 1 + r), log);
            }
        }
    }

    private static void placeBranches(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        BlockState log = EldergroveBlocks.GREATWOOD_LOG.get().defaultBlockState();
        int branchY = height - 5;
        branch(level, origin.offset(0, branchY, 0), -1, 0, 4 + random.nextInt(2), log);
        branch(level, origin.offset(1, branchY + 1, 1), 1, 0, 4 + random.nextInt(2), log);
        branch(level, origin.offset(0, branchY + 1, 1), 0, 1, 4 + random.nextInt(2), log);
        branch(level, origin.offset(1, branchY, 0), 0, -1, 4 + random.nextInt(2), log);

        if (random.nextBoolean()) {
            branch(level, origin.offset(0, branchY + 2, 0), -1, -1, 3, log);
            branch(level, origin.offset(1, branchY + 2, 1), 1, 1, 3, log);
        }
    }

    private static void branch(LevelAccessor level, BlockPos start, int dx, int dz, int length, BlockState log) {
        for (int i = 1; i <= length; i++) {
            int y = i / 2;
            setLog(level, start.offset(dx * i, y, dz * i), log);
        }
    }

    private static void placeCrown(LevelAccessor level, BlockPos origin, int height, RandomSource random) {
        double centerX = origin.getX() + 0.5D;
        double centerY = origin.getY() + height - 2.0D;
        double centerZ = origin.getZ() + 0.5D;

        for (int y = height - 7; y <= height + 4; y++) {
            double dy = Math.abs((origin.getY() + y - centerY) / 5.2D);
            double radius = 6.6D * (1.0D - dy * 0.42D);
            if (y <= height - 6) {
                radius = 3.0D;
            }
            if (y >= height + 3) {
                radius = 3.0D;
            }

            for (int x = -7; x <= 8; x++) {
                for (int z = -7; z <= 8; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    double dx = pos.getX() + 0.5D - centerX;
                    double dz = pos.getZ() + 0.5D - centerZ;
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    boolean raggedEdge = Math.floorMod(pos.getX() * 13 + pos.getY() * 17 + pos.getZ() * 19, 9) == 0;
                    if (dist <= radius && !(raggedEdge && dist > radius - 1.0D && random.nextBoolean())) {
                        if (canReplace(level, pos)) {
                            setLeaves(level, pos);
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
        for (int y = 0; y <= height + 5; y++) {
            int radius = y < 4 ? 3 : y > height - 8 ? 8 : 2;
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
