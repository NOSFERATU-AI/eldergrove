package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class EldergroveGrassBlock extends GrassBlock {
    public EldergroveGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Keep this grass stable for now. Vanilla GrassBlock can decay into dirt in dark places,
        // but Eldergrove spread should not randomly destroy the magical surface layer during tests.
    }
}
