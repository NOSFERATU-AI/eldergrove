package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class EldergroveGroundPlants {
    private EldergroveGroundPlants() {
    }

    public static void placeNearTree(WorldGenLevel level, BlockPos origin, RandomSource random, int tries, int radius) {
        for (int i = 0; i < tries; i++) {
            int x = random.nextInt(radius * 2 + 1) - radius;
            int z = random.nextInt(radius * 2 + 1) - radius;
            if (x * x + z * z > radius * radius) {
                continue;
            }

            BlockPos surface = findPlantSurface(level, origin.offset(x, 0, z));
            if (surface == null || !level.getBlockState(surface).isAir()) {
                continue;
            }

            BlockState plant = random.nextInt(5) == 0
                    ? EldergroveBlocks.VISHROOM.get().defaultBlockState()
                    : EldergroveBlocks.SHIMMERLEAF.get().defaultBlockState();

            if (plant.canSurvive(level, surface)) {
                level.setBlock(surface, plant, 2);
            }
        }
    }

    private static BlockPos findPlantSurface(WorldGenLevel level, BlockPos origin) {
        for (int dy = 5; dy >= -4; dy--) {
            BlockPos pos = origin.above(dy);
            if (canPlantOn(level.getBlockState(pos.below())) && level.getBlockState(pos).isAir()) {
                return pos;
            }
        }
        return null;
    }

    private static boolean canPlantOn(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MOSS_BLOCK)
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get());
    }
}
