package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EldergroveSpreadingCoreBlock extends Block {
    public static final int AURA_RADIUS = 10;
    private static final int SPREAD_ATTEMPTS = 2;
    private static final int TICK_DELAY = 80;
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
        spreadAura(level, pos, random);
        level.scheduleTick(pos, this, TICK_DELAY);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        spreadAura(level, pos, random);
    }

    private static void spreadAura(ServerLevel level, BlockPos heartPos, RandomSource random) {
        for (int i = 0; i < SPREAD_ATTEMPTS; i++) {
            BlockPos targetColumn = heartPos.offset(
                    random.nextInt(AURA_RADIUS * 2 + 1) - AURA_RADIUS,
                    0,
                    random.nextInt(AURA_RADIUS * 2 + 1) - AURA_RADIUS
            );

            if (targetColumn.distSqr(heartPos.getX(), targetColumn.getY(), heartPos.getZ()) > AURA_RADIUS * AURA_RADIUS) {
                continue;
            }

            BlockPos targetPos = findSurfaceGrass(level, targetColumn, heartPos.getY());
            if (targetPos == null) {
                continue;
            }

            level.setBlock(targetPos, EldergroveBlocks.ELDERGROVE_GRASS.get().defaultBlockState(), 3);
        }
    }

    private static BlockPos findSurfaceGrass(ServerLevel level, BlockPos columnPos, int centerY) {
        int minY = Math.max(level.getMinBuildHeight(), centerY - SURFACE_SEARCH_RANGE);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, centerY + SURFACE_SEARCH_RANGE);

        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = new BlockPos(columnPos.getX(), y, columnPos.getZ());
            BlockState state = level.getBlockState(pos);
            if (canBecomeEldergroveGrass(state) && !level.getBlockState(pos.above()).isSolidRender(level, pos.above())) {
                return pos;
            }
        }

        return null;
    }

    private static boolean canBecomeEldergroveGrass(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MYCELIUM);
    }
}
