package nosferatu.eldergrove;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TaintedTotemFeature extends Feature<NoneFeatureConfiguration> {
    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST
    };

    public TaintedTotemFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        if (!context.level().getBiome(origin).is(EldergroveBiomes.TAINTED_GROVE)
                || !canSustainTotem(context.level().getBlockState(origin.below()))) {
            return false;
        }

        Direction armDirection = context.random().nextBoolean() ? Direction.EAST : Direction.NORTH;
        BlockPos cap = origin.above(2);
        BlockPos top = origin.above(3);
        BlockPos growth = origin.above(4);
        if (!canReplace(context.level(), origin)
                || !canReplace(context.level(), origin.above())
                || !canReplace(context.level(), cap)
                || !canReplace(context.level(), cap.relative(armDirection))
                || !canReplace(context.level(), cap.relative(armDirection.getOpposite()))
                || !canReplace(context.level(), top)
                || !canReplace(context.level(), growth)) {
            return false;
        }

        corruptGround(context.level(), origin, context.random());
        context.level().setBlock(origin, EldergroveBlocks.TAINTED_ROCK.get().defaultBlockState(), 3);
        context.level().setBlock(origin.above(), EldergroveBlocks.TAINTED_HEART.get().defaultBlockState(), 3);
        context.level().setBlock(cap, EldergroveBlocks.TAINTED_ROCK.get().defaultBlockState(), 3);
        context.level().setBlock(cap.relative(armDirection), EldergroveBlocks.TAINTED_ROCK.get().defaultBlockState(), 3);
        context.level().setBlock(cap.relative(armDirection.getOpposite()), EldergroveBlocks.TAINTED_ROCK.get().defaultBlockState(), 3);
        context.level().setBlock(top, EldergroveBlocks.TAINTED_CRUST.get().defaultBlockState(), 3);

        BlockState crown = EldergroveBlocks.TAINTED_GROWTH.get().defaultBlockState();
        if (crown.canSurvive(context.level(), growth)) {
            context.level().setBlock(growth, crown, 2);
        }

        return true;
    }

    private static void corruptGround(LevelAccessor level, BlockPos origin, RandomSource random) {
        BlockPos centerGround = origin.below();
        level.setBlock(centerGround, EldergroveBlocks.TAINTED_CRUST.get().defaultBlockState(), 3);

        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            BlockPos ground = findGround(level, origin.relative(direction));
            if (ground == null) {
                continue;
            }

            BlockState replacement = random.nextBoolean()
                    ? EldergroveBlocks.TAINTED_SOIL.get().defaultBlockState()
                    : EldergroveBlocks.TAINTED_CRUST.get().defaultBlockState();
            level.setBlock(ground, replacement, 3);

            BlockPos plantPos = ground.above();
            BlockState fibre = EldergroveBlocks.TAINTED_FIBRE.get().defaultBlockState();
            if (fibre.canSurvive(level, plantPos)) {
                level.setBlock(plantPos, fibre, 2);
            }
        }
    }

    private static BlockPos findGround(LevelAccessor level, BlockPos around) {
        for (int dy = 1; dy >= -1; dy--) {
            BlockPos ground = around.offset(0, dy - 1, 0);
            BlockState above = level.getBlockState(ground.above());
            if (canSustainTotem(level.getBlockState(ground))
                    && above.getFluidState().isEmpty()
                    && (above.isAir() || above.canBeReplaced())) {
                return ground;
            }
        }
        return null;
    }

    private static boolean canSustainTotem(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(EldergroveBlocks.TAINTED_SOIL.get())
                || state.is(EldergroveBlocks.TAINTED_CRUST.get())
                || state.is(EldergroveBlocks.TAINTED_ROCK.get());
    }

    private static boolean canReplace(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES) || state.is(Blocks.SNOW);
    }
}
