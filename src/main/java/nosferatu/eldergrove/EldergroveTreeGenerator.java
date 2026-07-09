package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class EldergroveTreeGenerator {
    private EldergroveTreeGenerator() {
    }

    public static boolean grow(ServerLevel level, BlockPos origin, RandomSource random) {
        int height = 8 + random.nextInt(5);

        if (!canGrow(level, origin, height)) {
            return false;
        }

        BlockState soil = level.getBlockState(origin.below());
        if (soil.is(Blocks.GRASS_BLOCK) || soil.is(Blocks.DIRT) || soil.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())
                || soil.is(EldergroveBlocks.ELDERGROVE_GRASS.get()) || soil.is(EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get())) {
            level.setBlock(origin.below(), EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get().defaultBlockState(), 2);
        }

        int leafBottom = origin.getY() + height - 5;
        int leafTop = origin.getY() + height + 3 + random.nextInt(3);
        for (int y = leafBottom; y <= leafTop; y++) {
            int centerY = clamp(y, origin.getY() + height - 3, origin.getY() + height);
            for (int x = origin.getX() - 5; x <= origin.getX() + 5; x++) {
                for (int z = origin.getZ() - 5; z <= origin.getZ() + 5; z++) {
                    double dx = x - origin.getX();
                    double dy = y - centerY;
                    double dz = z - origin.getZ();
                    double distance = dx * dx + dy * dy + dz * dz;
                    if (distance < 10 + random.nextInt(8)) {
                        BlockPos leafPos = new BlockPos(x, y, z);
                        if (canReplaceWithLeaves(level, leafPos)) {
                            level.setBlock(leafPos, EldergroveBlocks.ELDERWOOD_LEAVES.get().defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        boolean placedHeart = false;
        int heartChance = Math.max(10, (int) (height * 1.5D));
        for (int dy = 0; dy < height; dy++) {
            BlockPos trunkPos = origin.above(dy);
            if (dy > 0 && !placedHeart && random.nextInt(heartChance) == 0) {
                setLog(level, trunkPos, EldergroveBlocks.GROVE_HEART.get().defaultBlockState());
                placedHeart = true;
            } else {
                setLog(level, trunkPos, EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
            }

            setLog(level, trunkPos.west(), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
            setLog(level, trunkPos.east(), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
            setLog(level, trunkPos.north(), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
            setLog(level, trunkPos.south(), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
        }

        BlockPos top = origin.above(height);
        setLog(level, top, EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
        setLog(level, origin.offset(-1, 0, -1), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
        setLog(level, origin.offset(1, 0, 1), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
        setLog(level, origin.offset(-1, 0, 1), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
        setLog(level, origin.offset(1, 0, -1), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());

        if (random.nextInt(3) != 0) setLog(level, origin.offset(-1, 1, -1), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
        if (random.nextInt(3) != 0) setLog(level, origin.offset(1, 1, 1), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
        if (random.nextInt(3) != 0) setLog(level, origin.offset(-1, 1, 1), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());
        if (random.nextInt(3) != 0) setLog(level, origin.offset(1, 1, -1), EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState());

        return true;
    }

    private static boolean canGrow(ServerLevel level, BlockPos origin, int height) {
        if (!canSustainTree(level.getBlockState(origin.below()))) {
            return false;
        }

        for (int y = 0; y <= height + 4; y++) {
            int radius = y == 0 ? 1 : y >= height - 2 ? 5 : 2;
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
        return state.is(BlockTags.DIRT) || state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS.get()) || state.is(EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get());
    }

    private static boolean canReplaceTreeSpace(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES) || state.is(EldergroveBlocks.ELDERWOOD_SAPLING.get());
    }

    private static boolean canReplaceWithLeaves(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES);
    }

    private static void setLog(ServerLevel level, BlockPos pos, BlockState state) {
        BlockState current = level.getBlockState(pos);
        if (current.isAir() || current.canBeReplaced() || current.is(BlockTags.LEAVES) || current.is(EldergroveBlocks.ELDERWOOD_SAPLING.get())) {
            level.setBlock(pos, state, 2);
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
