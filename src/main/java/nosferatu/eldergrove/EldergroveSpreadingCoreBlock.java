package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EldergroveSpreadingCoreBlock extends Block {
    private static final int SPREAD_RADIUS = 8;
    private static final int SPREAD_ATTEMPTS = 2;
    private static final int TICK_DELAY = 50;
    private static final int SURFACE_SEARCH_RANGE = 8;

    public EldergroveSpreadingCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(oldState.getBlock())) {
            level.scheduleTick(pos, this, TICK_DELAY);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        spread(level, pos, random);
        level.scheduleTick(pos, this, TICK_DELAY);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        spread(level, pos, random);
    }

    private static void spread(ServerLevel level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < SPREAD_ATTEMPTS; i++) {
            BlockPos targetColumn = pos.offset(
                    random.nextInt(SPREAD_RADIUS) - random.nextInt(SPREAD_RADIUS),
                    0,
                    random.nextInt(SPREAD_RADIUS) - random.nextInt(SPREAD_RADIUS)
            );

            BlockPos targetPos = findSurfaceGrass(level, targetColumn, pos.getY());
            if (targetPos == null) {
                continue;
            }

            BlockState targetState = level.getBlockState(targetPos);
            BlockState nextState = getNextEldergroveGrassStage(targetState);
            if (nextState != null) {
                level.setBlock(targetPos, nextState, 2);
            }
        }
    }

    private static BlockPos findSurfaceGrass(ServerLevel level, BlockPos columnPos, int centerY) {
        int minY = Math.max(level.getMinBuildHeight(), centerY - SURFACE_SEARCH_RANGE);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, centerY + SURFACE_SEARCH_RANGE);

        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = new BlockPos(columnPos.getX(), y, columnPos.getZ());
            BlockState state = level.getBlockState(pos);
            if (canAdvanceGrass(state) && !level.getBlockState(pos.above()).isSolidRender(level, pos.above())) {
                return pos;
            }
        }

        return null;
    }

    private static BlockState getNextEldergroveGrassStage(BlockState state) {
        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.PODZOL) || state.is(Blocks.MYCELIUM)) {
            return EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get().defaultBlockState();
        }

        if (state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())) {
            return EldergroveBlocks.ELDERGROVE_GRASS.get().defaultBlockState();
        }

        if (state.is(EldergroveBlocks.ELDERGROVE_GRASS.get())) {
            return EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get().defaultBlockState();
        }

        return null;
    }

    private static boolean canAdvanceGrass(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MYCELIUM)
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS.get());
    }
}
