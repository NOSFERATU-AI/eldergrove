package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EldergroveSpreadingCoreBlock extends Block {
    public static final int AURA_RADIUS = 14;
    private static final int SPREAD_ATTEMPTS = 3;
    private static final int TICK_DELAY = 80;
    private static final int SURFACE_SEARCH_RANGE = 10;

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
        spreadBiomeAura(level, pos, random);
        level.scheduleTick(pos, this, TICK_DELAY);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        spreadBiomeAura(level, pos, random);
    }

    private static void spreadBiomeAura(ServerLevel level, BlockPos heartPos, RandomSource random) {
        for (int i = 0; i < SPREAD_ATTEMPTS; i++) {
            int offsetX = random.nextInt(AURA_RADIUS * 2 + 1) - AURA_RADIUS;
            int offsetZ = random.nextInt(AURA_RADIUS * 2 + 1) - AURA_RADIUS;
            if (offsetX * offsetX + offsetZ * offsetZ > AURA_RADIUS * AURA_RADIUS) {
                continue;
            }

            BlockPos targetColumn = heartPos.offset(offsetX, 0, offsetZ);
            BlockPos surface = findNaturalSurface(level, targetColumn, heartPos.getY());
            if (surface != null) {
                EldergroveBiomeSpreader.setEldergroveBiome(level, surface);
            }
        }
    }

    private static BlockPos findNaturalSurface(ServerLevel level, BlockPos columnPos, int centerY) {
        int minY = Math.max(level.getMinBuildHeight(), centerY - SURFACE_SEARCH_RANGE);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, centerY + SURFACE_SEARCH_RANGE);

        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = new BlockPos(columnPos.getX(), y, columnPos.getZ());
            if (EldergroveBiomeSpreader.isNaturalSurface(level, pos)) {
                return pos;
            }
        }

        return null;
    }
}
