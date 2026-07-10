package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class EldergroveGroundPlants {
    private EldergroveGroundPlants() {
    }

    public static void placeNearTree(LevelAccessor level, BlockPos origin, RandomSource random, int tries, int radius) {
        placeNearTree(level, origin, random, tries, radius, true);
    }

    public static void placeVishroomsNearTree(LevelAccessor level, BlockPos origin, RandomSource random, int tries, int radius) {
        placeNearTree(level, origin, random, tries, radius, false);
    }

    private static void placeNearTree(LevelAccessor level, BlockPos origin, RandomSource random, int tries, int radius, boolean allowShimmerleaf) {
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

            BlockState plant = allowShimmerleaf && random.nextInt(5) != 0
                    ? EldergroveBlocks.SHIMMERLEAF.get().defaultBlockState()
                    : EldergroveBlocks.VISHROOM.get().defaultBlockState();

            if (plant.canSurvive(level, surface)) {
                level.setBlock(surface, plant, 2);
            }
        }
    }

    private static BlockPos findPlantSurface(LevelAccessor level, BlockPos origin) {
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
