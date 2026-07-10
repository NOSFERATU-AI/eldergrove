package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class GreatwoodSaplingBlock extends SaplingBlock {
    public GreatwoodSaplingBlock(TreeGrower treeGrower, BlockBehaviour.Properties properties) {
        super(treeGrower, properties);
    }

    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        BlockPos root = findGreatwoodSquare(level, pos);
        if (root == null) {
            return;
        }

        BlockPos[] saplings = new BlockPos[]{
                root,
                root.east(),
                root.south(),
                root.east().south()
        };
        BlockState[] oldStates = new BlockState[saplings.length];

        for (int i = 0; i < saplings.length; i++) {
            oldStates[i] = level.getBlockState(saplings[i]);
            level.setBlock(saplings[i], Blocks.AIR.defaultBlockState(), 4);
        }

        if (!GreatwoodTreeGenerator.grow(level, root, random)) {
            for (int i = 0; i < saplings.length; i++) {
                level.setBlock(saplings[i], oldStates[i], 4);
            }
        }
    }

    private static BlockPos findGreatwoodSquare(ServerLevel level, BlockPos pos) {
        for (int xOffset = 0; xOffset >= -1; xOffset--) {
            for (int zOffset = 0; zOffset >= -1; zOffset--) {
                BlockPos root = pos.offset(xOffset, 0, zOffset);
                if (isGreatwoodSapling(level, root)
                        && isGreatwoodSapling(level, root.east())
                        && isGreatwoodSapling(level, root.south())
                        && isGreatwoodSapling(level, root.east().south())) {
                    return root;
                }
            }
        }
        return null;
    }

    private static boolean isGreatwoodSapling(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(EldergroveBlocks.GREATWOOD_SAPLING.get());
    }
}