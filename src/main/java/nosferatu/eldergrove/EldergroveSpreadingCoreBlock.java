package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EldergroveSpreadingCoreBlock extends Block {
    private static final int SPREAD_RADIUS = 10;
    private static final int SPREAD_ATTEMPTS = 6;
    private static final int TICK_DELAY = 60;

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
            BlockPos targetPos = pos.offset(
                    random.nextInt(SPREAD_RADIUS * 2 + 1) - SPREAD_RADIUS,
                    random.nextInt(5) - 2,
                    random.nextInt(SPREAD_RADIUS * 2 + 1) - SPREAD_RADIUS
            );

            BlockState targetState = level.getBlockState(targetPos);
            BlockState nextState = getNextEldergroveGrassStage(level, targetPos, targetState);
            if (nextState != null) {
                level.setBlock(targetPos, nextState, 2);
            }
        }
    }

    private static BlockState getNextEldergroveGrassStage(ServerLevel level, BlockPos pos, BlockState state) {
        if (level.getBlockState(pos.above()).isSolidRender(level, pos.above())) {
            return null;
        }

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
}
