package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EldergroveSpreadingCoreBlock extends Block {
    public static final int AURA_RADIUS = 18;
    private static final int REFRESH_ATTEMPTS = 18;
    private static final int TICK_DELAY = 50;
    private static final int SURFACE_SEARCH_RANGE = 8;

    public EldergroveSpreadingCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(oldState.getBlock())) {
            refreshAura((ServerLevel) level, pos, level.random, REFRESH_ATTEMPTS * 4);
            level.scheduleTick(pos, this, TICK_DELAY);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        refreshAura(level, pos, random, REFRESH_ATTEMPTS);
        level.scheduleTick(pos, this, TICK_DELAY);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        refreshAura(level, pos, random, REFRESH_ATTEMPTS);
    }

    private static void refreshAura(ServerLevel level, BlockPos heartPos, RandomSource random, int attempts) {
        for (int i = 0; i < attempts; i++) {
            BlockPos targetColumn = heartPos.offset(
                    random.nextInt(AURA_RADIUS) - random.nextInt(AURA_RADIUS),
                    0,
                    random.nextInt(AURA_RADIUS) - random.nextInt(AURA_RADIUS)
            );

            BlockPos targetPos = findSurfaceVegetation(level, targetColumn, heartPos.getY());
            if (targetPos == null) {
                continue;
            }

            BlockState state = level.getBlockState(targetPos);
            level.sendBlockUpdated(targetPos, state, state, 2);

            BlockPos above = targetPos.above();
            BlockState aboveState = level.getBlockState(above);
            if (canTintLikeMagicalForest(aboveState)) {
                level.sendBlockUpdated(above, aboveState, aboveState, 2);
            }
        }
    }

    private static BlockPos findSurfaceVegetation(ServerLevel level, BlockPos columnPos, int centerY) {
        int minY = Math.max(level.getMinBuildHeight(), centerY - SURFACE_SEARCH_RANGE);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, centerY + SURFACE_SEARCH_RANGE);

        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = new BlockPos(columnPos.getX(), y, columnPos.getZ());
            BlockState state = level.getBlockState(pos);
            if (canTintLikeMagicalForest(state) && !level.getBlockState(pos.above()).isSolidRender(level, pos.above())) {
                return pos;
            }
        }

        return null;
    }

    private static boolean canTintLikeMagicalForest(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.SHORT_GRASS)
                || state.is(Blocks.TALL_GRASS)
                || state.is(Blocks.FERN)
                || state.is(Blocks.LARGE_FERN)
                || state.is(Blocks.OAK_LEAVES)
                || state.is(Blocks.BIRCH_LEAVES)
                || state.is(Blocks.SPRUCE_LEAVES)
                || state.is(Blocks.JUNGLE_LEAVES)
                || state.is(Blocks.ACACIA_LEAVES)
                || state.is(Blocks.DARK_OAK_LEAVES)
                || state.is(EldergroveBlocks.ELDERWOOD_LEAVES.get());
    }
}
