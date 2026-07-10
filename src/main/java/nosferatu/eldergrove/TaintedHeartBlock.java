package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class TaintedHeartBlock extends Block {
    public static final int SPREAD_RADIUS = 12;
    private static final int SPREAD_ATTEMPTS = 6;
    private static final int TICK_DELAY = 60;
    private static final int MAX_SURFACE_DELTA = 12;

    public TaintedHeartBlock(Properties properties) {
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
        spreadTaint(level, pos, random);
        level.scheduleTick(pos, this, TICK_DELAY);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        spreadTaint(level, pos, random);
    }

    private static void spreadTaint(ServerLevel level, BlockPos heartPos, RandomSource random) {
        for (int attempt = 0; attempt < SPREAD_ATTEMPTS; attempt++) {
            int offsetX = random.nextInt(SPREAD_RADIUS * 2 + 1) - SPREAD_RADIUS;
            int offsetZ = random.nextInt(SPREAD_RADIUS * 2 + 1) - SPREAD_RADIUS;
            if (offsetX * offsetX + offsetZ * offsetZ > SPREAD_RADIUS * SPREAD_RADIUS) {
                continue;
            }

            BlockPos column = heartPos.offset(offsetX, 0, offsetZ);
            if (!level.hasChunkAt(column)) {
                continue;
            }

            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column.getX(), column.getZ()) - 1;
            if (Math.abs(surfaceY - heartPos.getY()) > MAX_SURFACE_DELTA) {
                continue;
            }

            BlockPos surface = new BlockPos(column.getX(), surfaceY, column.getZ());
            BlockState current = level.getBlockState(surface);
            if (!isConvertibleSurface(current)) {
                continue;
            }

            BlockState replacement = current.is(BlockTags.BASE_STONE_OVERWORLD)
                    ? EldergroveBlocks.TAINTED_ROCK.get().defaultBlockState()
                    : random.nextInt(5) == 0
                            ? EldergroveBlocks.TAINTED_CRUST.get().defaultBlockState()
                            : EldergroveBlocks.TAINTED_SOIL.get().defaultBlockState();

            level.setBlock(surface, replacement, 3);
            EldergroveBiomeSpreader.setTaintedBiome(level, surface);
            placeTaintedGrowth(level, surface.above(), random);
            return;
        }
    }

    private static boolean isConvertibleSurface(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.CLAY)
                || state.is(Blocks.MUD)
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get());
    }

    private static void placeTaintedGrowth(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState current = level.getBlockState(pos);
        if (!current.getFluidState().isEmpty() || (!current.isAir() && !current.canBeReplaced())) {
            return;
        }

        BlockState growth = switch (random.nextInt(6)) {
            case 0 -> EldergroveBlocks.TAINTED_GROWTH.get().defaultBlockState();
            case 1 -> EldergroveBlocks.TAINTED_TENDRIL.get().defaultBlockState();
            default -> EldergroveBlocks.TAINTED_FIBRE.get().defaultBlockState();
        };
        if (growth.canSurvive(level, pos)) {
            level.setBlock(pos, growth, 2);
        }
    }
}
