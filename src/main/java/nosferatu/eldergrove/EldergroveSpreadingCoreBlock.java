package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EldergroveSpreadingCoreBlock extends Block {
    private static final int SPREAD_RADIUS = 12;
    private static final int SPREAD_ATTEMPTS = 24;
    private static final int TICK_DELAY = 40;

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
                    random.nextInt(7) - 3,
                    random.nextInt(SPREAD_RADIUS * 2 + 1) - SPREAD_RADIUS
            );

            BlockState targetState = level.getBlockState(targetPos);
            if (canBecomeEldergroveMoss(targetState)) {
                level.setBlock(targetPos, EldergroveBlocks.ELDERGROVE_MOSS.get().defaultBlockState(), 3);
            } else if (canBecomeElderwoodLeaves(targetState)) {
                level.setBlock(targetPos, EldergroveBlocks.ELDERWOOD_LEAVES.get().defaultBlockState(), 3);
            }
        }
    }

    private static boolean canBecomeEldergroveMoss(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.MOSS_BLOCK);
    }

    private static boolean canBecomeElderwoodLeaves(BlockState state) {
        return state.is(Blocks.OAK_LEAVES)
                || state.is(Blocks.BIRCH_LEAVES)
                || state.is(Blocks.SPRUCE_LEAVES)
                || state.is(Blocks.JUNGLE_LEAVES)
                || state.is(Blocks.ACACIA_LEAVES)
                || state.is(Blocks.DARK_OAK_LEAVES)
                || state.is(Blocks.AZALEA_LEAVES)
                || state.is(Blocks.FLOWERING_AZALEA_LEAVES);
    }
}
