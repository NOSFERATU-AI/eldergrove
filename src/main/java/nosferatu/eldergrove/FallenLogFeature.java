package nosferatu.eldergrove;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FallenLogFeature extends Feature<NoneFeatureConfiguration> {
    private static final EnumProperty<Direction.Axis> AXIS = RotatedPillarBlock.AXIS;
    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST
    };

    public FallenLogFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource random = context.random();
        LevelAccessor level = context.level();
        Direction direction = HORIZONTAL_DIRECTIONS[random.nextInt(HORIZONTAL_DIRECTIONS.length)];
        Direction.Axis axis = direction.getAxis();
        int length = 3 + random.nextInt(4);
        BlockPos origin = context.origin();

        // More oak than silver wood: these are forest-floor fallen logs, not only magical tree remains.
        BlockState log = random.nextInt(3) == 0
                ? EldergroveBlocks.ELDERWOOD_LOG.get().defaultBlockState().setValue(AXIS, axis)
                : Blocks.OAK_LOG.defaultBlockState().setValue(AXIS, axis);

        for (int i = 0; i < length; i++) {
            BlockPos pos = origin.relative(direction, i);
            if (!canSupport(level.getBlockState(pos.below())) || !canReplace(level.getBlockState(pos))) {
                return false;
            }
        }

        for (int i = 0; i < length; i++) {
            BlockPos pos = origin.relative(direction, i);
            level.setBlock(pos, log, 3);

            if (random.nextInt(3) == 0) {
                BlockPos mossPos = pos.above();
                if (level.getBlockState(mossPos).isAir()) {
                    level.setBlock(mossPos, Blocks.MOSS_CARPET.defaultBlockState(), 3);
                }
            }
        }

        return true;
    }

    private static boolean canSupport(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MOSS_BLOCK)
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS.get())
                || state.is(EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get());
    }

    private static boolean canReplace(BlockState state) {
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES);
    }
}
