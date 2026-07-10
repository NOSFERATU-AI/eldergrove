package nosferatu.eldergrove;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TaintedSurfaceFeature extends Feature<NoneFeatureConfiguration> {
    private static final int SURFACE_DEPTH = 2;

    public TaintedSurfaceFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        int baseX = context.origin().getX() & ~15;
        int baseZ = context.origin().getZ() & ~15;
        boolean changed = false;

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int x = baseX + localX;
                int z = baseZ + localZ;
                int y = context.level().getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
                BlockPos surface = new BlockPos(x, y, z);

                if (!context.level().getBiome(surface).is(EldergroveBiomes.TAINTED_GROVE)) {
                    continue;
                }

                BlockState current = context.level().getBlockState(surface);
                if (isDirtSurface(current)) {
                    BlockState top = context.random().nextInt(5) == 0
                            ? EldergroveBlocks.TAINTED_CRUST.get().defaultBlockState()
                            : EldergroveBlocks.TAINTED_SOIL.get().defaultBlockState();
                    context.level().setBlock(surface, top, 2);
                    changed = true;

                    for (int depth = 1; depth <= SURFACE_DEPTH; depth++) {
                        BlockPos below = surface.below(depth);
                        if (isDirtSurface(context.level().getBlockState(below))) {
                            context.level().setBlock(
                                    below,
                                    EldergroveBlocks.TAINTED_SOIL.get().defaultBlockState(),
                                    2
                            );
                        }
                    }
                } else if (current.is(BlockTags.BASE_STONE_OVERWORLD)) {
                    context.level().setBlock(
                            surface,
                            EldergroveBlocks.TAINTED_ROCK.get().defaultBlockState(),
                            2
                    );
                    changed = true;
                }
            }
        }

        return changed;
    }

    private static boolean isDirtSurface(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get());
    }
}
