package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class TaintedDeadTreeGenerator {
    private static final EnumProperty<Direction.Axis> AXIS = RotatedPillarBlock.AXIS;
    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST
    };

    private TaintedDeadTreeGenerator() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 4 + random.nextInt(4);
        if (!canGrow(level, origin, height)) {
            return false;
        }

        BlockState verticalLog = Blocks.DARK_OAK_LOG.defaultBlockState().setValue(AXIS, Direction.Axis.Y);
        for (int y = 0; y <= height; y++) {
            setBlock(level, origin.above(y), verticalLog);
        }

        int branchY = Math.max(2, height - 1);
        int branchCount = 2 + random.nextInt(3);
        for (int i = 0; i < branchCount; i++) {
            Direction dir = HORIZONTAL_DIRECTIONS[(i + random.nextInt(HORIZONTAL_DIRECTIONS.length)) % HORIZONTAL_DIRECTIONS.length];
            int length = 2 + random.nextInt(3);
            placeBranch(level, origin.above(branchY - random.nextInt(2)), dir, length, random);
        }

        if (random.nextBoolean()) {
            setBlock(level, origin.above(height + 1), EldergroveBlocks.TAINTED_GROWTH.get().defaultBlockState());
        }

        placeGroundGrowth(level, origin, random);
        return true;
    }

    private static void placeBranch(LevelAccessor level, BlockPos start, Direction dir, int length, RandomSource random) {
        BlockState log = Blocks.DARK_OAK_LOG.defaultBlockState().setValue(AXIS, dir.getAxis());
        BlockPos last = start;
        for (int step = 1; step <= length; step++) {
            int y = step > 2 && random.nextBoolean() ? 1 : 0;
            last = start.relative(dir, step).above(y);
            setBlock(level, last, log);
        }

        if (random.nextInt(3) != 0) {
            setBlock(level, last.above(), Blocks.OAK_LEAVES.defaultBlockState());
        }
    }

    private static void placeGroundGrowth(LevelAccessor level, BlockPos origin, RandomSource random) {
        for (int i = 0; i < 18; i++) {
            int x = random.nextInt(9) - 4;
            int z = random.nextInt(9) - 4;
            if (x * x + z * z > 18) {
                continue;
            }

            BlockPos surface = findSurface(level, origin.offset(x, 0, z));
            if (surface == null || !level.getBlockState(surface).isAir()) {
                continue;
            }

            BlockState plant = random.nextInt(3) == 0
                    ? EldergroveBlocks.TAINTED_GROWTH.get().defaultBlockState()
                    : EldergroveBlocks.TAINTED_FIBRE.get().defaultBlockState();
            if (plant.canSurvive(level, surface)) {
                level.setBlock(surface, plant, 2);
            }
        }
    }

    private static BlockPos findSurface(LevelAccessor level, BlockPos origin) {
        for (int dy = 4; dy >= -3; dy--) {
            BlockPos pos = origin.above(dy);
            if (canSustainTree(level.getBlockState(pos.below())) && level.getBlockState(pos).isAir()) {
                return pos;
            }
        }
        return null;
    }

    private static boolean canGrow(LevelAccessor level, BlockPos origin, int height) {
        if (!canSustainTree(level.getBlockState(origin.below()))) {
            return false;
        }
        for (int y = 0; y <= height + 2; y++) {
            int radius = y < height - 2 ? 1 : 4;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (!canReplace(level, origin.offset(x, y, z))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean canSustainTree(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.PODZOL)
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get())
                || state.is(EldergroveBlocks.TAINTED_SOIL.get())
                || state.is(EldergroveBlocks.TAINTED_CRUST.get());
    }

    private static boolean canReplace(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES);
    }

    private static void setBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        if (canReplace(level, pos)) {
            level.setBlock(pos, state, 3);
        }
    }
}