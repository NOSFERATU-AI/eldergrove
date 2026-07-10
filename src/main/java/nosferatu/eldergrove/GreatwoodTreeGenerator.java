package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.ArrayList;
import java.util.List;

public final class GreatwoodTreeGenerator {
    private static final EnumProperty<Direction.Axis> AXIS = RotatedPillarBlock.AXIS;
    private static final byte[] OTHER_COORD_PAIRS = new byte[]{2, 0, 0, 1, 2, 1};

    private GreatwoodTreeGenerator() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin, RandomSource random) {
        int heightLimit = 12 + random.nextInt(9);
        int trunkHeight = Math.max(7, (int) (heightLimit * 0.618D));
        if (!canGrow(level, origin, heightLimit + trunkHeight + 5)) {
            return false;
        }

        placeButtressRoots(level, origin, random);
        generateGreatwoodPart(level, origin, heightLimit, 1.20D, 0.95D, random);

        // Thaumcraft Greatwood is basically two old-tree crowns stacked through the same 2x2 body.
        // The upper part gives it that huge, uneven umbrella silhouette instead of a simple leaf ball.
        BlockPos upperOrigin = origin.above(trunkHeight);
        generateGreatwoodPart(level, upperOrigin, heightLimit, 1.66D, 1.15D, random);

        EldergroveGroundPlants.placeVishroomsNearTree(level, origin, random, 8, 9);
        return true;
    }

    private static void generateGreatwoodPart(LevelAccessor level, BlockPos base, int heightLimit, double scaleWidth, double leafDensity, RandomSource random) {
        int height = Math.min(heightLimit - 1, Math.max(6, (int) (heightLimit * 0.618D)));
        List<LeafNode> leafNodes = generateLeafNodes(level, base, heightLimit, height, scaleWidth, leafDensity, random);

        for (LeafNode node : leafNodes) {
            generateLeafNode(level, node.pos(), random);
        }

        for (LeafNode node : leafNodes) {
            int nodeBaseHeight = node.branchBaseY() - base.getY();
            if (nodeBaseHeight >= heightLimit * 0.2D) {
                placeBlockLine(level, new BlockPos(base.getX(), node.branchBaseY(), base.getZ()), node.pos(), true);
                placeBlockLine(level, new BlockPos(base.getX() + 1, node.branchBaseY(), base.getZ() + 1), node.pos(), false);
            }
        }

        generateTrunk(level, base, height);
    }

    private static List<LeafNode> generateLeafNodes(LevelAccessor level, BlockPos base, int heightLimit, int height, double scaleWidth, double leafDensity, RandomSource random) {
        int nodesPerLayer = (int) (1.382D + Math.pow(leafDensity * heightLimit / 13.0D, 2.0D));
        nodesPerLayer = Math.max(1, nodesPerLayer);

        List<LeafNode> leafNodes = new ArrayList<>();
        int y = base.getY() + heightLimit - 4;
        int trunkTopY = base.getY() + height;
        int layerFromTop = y - base.getY();

        leafNodes.add(new LeafNode(new BlockPos(base.getX(), y, base.getZ()), trunkTopY));
        y--;
        layerFromTop--;

        while (layerFromTop >= 0) {
            float layerSize = layerSize(heightLimit, layerFromTop);
            if (layerSize < 0.0F) {
                y--;
                layerFromTop--;
                continue;
            }

            for (int nodeIndex = 0; nodeIndex < nodesPerLayer; nodeIndex++) {
                double distance = scaleWidth * layerSize * (random.nextFloat() + 0.328D);
                double angle = random.nextFloat() * 2.0D * Math.PI;
                int nodeX = Mth.floor(distance * Math.sin(angle) + base.getX() + 0.5D);
                int nodeZ = Mth.floor(distance * Math.cos(angle) + base.getZ() + 0.5D);
                BlockPos node = new BlockPos(nodeX, y, nodeZ);
                BlockPos top = node.above(4);

                if (checkBlockLine(level, node, top) != -1) {
                    continue;
                }

                double horizontalDistance = Math.sqrt(Math.pow(Math.abs(base.getX() - nodeX), 2.0D) + Math.pow(Math.abs(base.getZ() - nodeZ), 2.0D));
                int branchBaseY = (int) (node.getY() - horizontalDistance * 0.38D);
                if (branchBaseY > trunkTopY) {
                    branchBaseY = trunkTopY;
                }

                BlockPos branchBase = new BlockPos(base.getX(), branchBaseY, base.getZ());
                if (checkBlockLine(level, branchBase, node) == -1) {
                    leafNodes.add(new LeafNode(node, branchBaseY));
                }
            }

            y--;
            layerFromTop--;
        }

        return leafNodes;
    }

    private static float layerSize(int heightLimit, int layer) {
        if (layer < heightLimit * 0.3D) {
            return -1.618F;
        }

        float half = heightLimit / 2.0F;
        float offset = heightLimit / 2.0F - layer;
        float size;
        if (offset == 0.0F) {
            size = half;
        } else if (Math.abs(offset) >= half) {
            size = 0.0F;
        } else {
            size = (float) Math.sqrt(Math.pow(Math.abs(half), 2.0D) - Math.pow(Math.abs(offset), 2.0D));
        }

        return size * 0.5F;
    }

    private static void generateLeafNode(LevelAccessor level, BlockPos node, RandomSource random) {
        for (int y = 0; y < 4; y++) {
            float radius = (y == 0 || y == 3) ? 2.0F : 3.0F;
            generateLeafLayer(level, node.above(y), radius, random);
        }
    }

    private static void generateLeafLayer(LevelAccessor level, BlockPos center, float radius, RandomSource random) {
        int blockRadius = (int) (radius + 0.618D);
        for (int x = -blockRadius; x <= blockRadius; x++) {
            for (int z = -blockRadius; z <= blockRadius; z++) {
                double distance = Math.pow(Math.abs(x) + 0.5D, 2.0D) + Math.pow(Math.abs(z) + 0.5D, 2.0D);
                if (distance > radius * radius) {
                    continue;
                }

                BlockPos pos = center.offset(x, 0, z);
                if (canReplace(level, pos)) {
                    setLeaves(level, pos);
                }

                // A few lower hanging leaves, but not enough to become a giant unsupported blob.
                if (radius >= 3.0F && random.nextInt(10) == 0) {
                    BlockPos hanging = pos.below();
                    if (canReplace(level, hanging)) {
                        setLeaves(level, hanging);
                    }
                }
            }
        }
    }

    private static void generateTrunk(LevelAccessor level, BlockPos base, int height) {
        placeBlockLine(level, base, base.above(height), false);
        placeBlockLine(level, base.east(), base.east().above(height), false);
        placeBlockLine(level, base.south(), base.south().above(height), false);
        placeBlockLine(level, base.east().south(), base.east().south().above(height), false);
    }

    private static void placeButtressRoots(LevelAccessor level, BlockPos origin, RandomSource random) {
        placeRoot(level, origin.offset(0, 1, 0), Direction.WEST, 5, random);
        placeRoot(level, origin.offset(1, 1, 1), Direction.EAST, 5, random);
        placeRoot(level, origin.offset(0, 1, 1), Direction.SOUTH, 4 + random.nextInt(2), random);
        placeRoot(level, origin.offset(1, 1, 0), Direction.NORTH, 4 + random.nextInt(2), random);
    }

    private static void placeRoot(LevelAccessor level, BlockPos start, Direction direction, int length, RandomSource random) {
        BlockState horizontalLog = log(direction.getAxis());
        for (int i = 1; i <= length; i++) {
            BlockPos pos = start.relative(direction, i).below(i > 2 ? 1 : 0);
            setLog(level, pos, horizontalLog);
            if (i < length && random.nextBoolean()) {
                setLog(level, pos.below(), horizontalLog);
            }
        }
    }

    private static void placeBlockLine(LevelAccessor level, BlockPos from, BlockPos to, boolean thinBranch) {
        int[] start = new int[]{from.getX(), from.getY(), from.getZ()};
        int[] end = new int[]{to.getX(), to.getY(), to.getZ()};
        int[] delta = new int[]{end[0] - start[0], end[1] - start[1], end[2] - start[2]};

        byte dominant = 0;
        for (byte axis = 0; axis < 3; axis++) {
            if (Math.abs(delta[axis]) > Math.abs(delta[dominant])) {
                dominant = axis;
            }
        }

        if (delta[dominant] == 0) {
            return;
        }

        byte firstOther = OTHER_COORD_PAIRS[dominant];
        byte secondOther = OTHER_COORD_PAIRS[dominant + 3];
        int step = delta[dominant] > 0 ? 1 : -1;
        double firstRatio = (double) delta[firstOther] / (double) delta[dominant];
        double secondRatio = (double) delta[secondOther] / (double) delta[dominant];

        BlockPos last = from;
        for (int offset = 0; offset != delta[dominant] + step; offset += step) {
            int[] current = new int[]{0, 0, 0};
            current[dominant] = Mth.floor(start[dominant] + offset + 0.5D);
            current[firstOther] = Mth.floor(start[firstOther] + offset * firstRatio + 0.5D);
            current[secondOther] = Mth.floor(start[secondOther] + offset * secondRatio + 0.5D);

            Direction.Axis axis = axisForLine(current[0] - last.getX(), current[1] - last.getY(), current[2] - last.getZ());
            BlockPos pos = new BlockPos(current[0], current[1], current[2]);
            setLog(level, pos, log(axis));

            if (!thinBranch && axis != Direction.Axis.Y) {
                if (Math.abs(pos.getY() - from.getY()) < 3) {
                    setLog(level, pos.below(), log(axis));
                }
            }

            last = pos;
        }
    }

    private static int checkBlockLine(LevelAccessor level, BlockPos from, BlockPos to) {
        int[] start = new int[]{from.getX(), from.getY(), from.getZ()};
        int[] end = new int[]{to.getX(), to.getY(), to.getZ()};
        int[] delta = new int[]{end[0] - start[0], end[1] - start[1], end[2] - start[2]};

        byte dominant = 0;
        for (byte axis = 0; axis < 3; axis++) {
            if (Math.abs(delta[axis]) > Math.abs(delta[dominant])) {
                dominant = axis;
            }
        }

        if (delta[dominant] == 0) {
            return -1;
        }

        byte firstOther = OTHER_COORD_PAIRS[dominant];
        byte secondOther = OTHER_COORD_PAIRS[dominant + 3];
        int step = delta[dominant] > 0 ? 1 : -1;
        double firstRatio = (double) delta[firstOther] / (double) delta[dominant];
        double secondRatio = (double) delta[secondOther] / (double) delta[dominant];

        int offset = 0;
        int target = delta[dominant] + step;
        for (; offset != target; offset += step) {
            int[] current = new int[]{0, 0, 0};
            current[dominant] = Mth.floor(start[dominant] + offset);
            current[firstOther] = Mth.floor(start[firstOther] + offset * firstRatio);
            current[secondOther] = Mth.floor(start[secondOther] + offset * secondRatio);

            BlockPos pos = new BlockPos(current[0], current[1], current[2]);
            if (!canReplace(level, pos)) {
                break;
            }
        }

        return offset == target ? -1 : Math.abs(offset);
    }

    private static Direction.Axis axisForLine(int dx, int dy, int dz) {
        if (Math.abs(dy) >= Math.abs(dx) && Math.abs(dy) >= Math.abs(dz)) {
            return Direction.Axis.Y;
        }
        return Math.abs(dx) >= Math.abs(dz) ? Direction.Axis.X : Direction.Axis.Z;
    }

    private static boolean canGrow(LevelAccessor level, BlockPos origin, int maxHeight) {
        if (!level.getBlockState(origin.below()).is(BlockTags.DIRT)
                || !level.getBlockState(origin.east().below()).is(BlockTags.DIRT)
                || !level.getBlockState(origin.south().below()).is(BlockTags.DIRT)
                || !level.getBlockState(origin.east().south().below()).is(BlockTags.DIRT)) {
            return false;
        }

        for (int y = 0; y <= maxHeight; y++) {
            int radius = y < 5 ? 4 : y > maxHeight - 12 ? 12 : 7;
            for (int x = -radius; x <= radius + 1; x++) {
                for (int z = -radius; z <= radius + 1; z++) {
                    if (!canReplace(level, origin.offset(x, y, z))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean canReplace(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir()
                || state.canBeReplaced()
                || state.is(BlockTags.LEAVES)
                || state.is(EldergroveBlocks.GREATWOOD_LOG.get())
                || state.is(EldergroveBlocks.GREATWOOD_LEAVES.get());
    }

    private static BlockState log(Direction.Axis axis) {
        return EldergroveBlocks.GREATWOOD_LOG.get().defaultBlockState().setValue(AXIS, axis);
    }

    private static void setLog(LevelAccessor level, BlockPos pos, BlockState state) {
        if (canReplace(level, pos)) {
            level.setBlock(pos, state, 3);
        }
    }

    private static void setLeaves(LevelAccessor level, BlockPos pos) {
        level.setBlock(
                pos,
                EldergroveBlocks.GREATWOOD_LEAVES.get().defaultBlockState()
                        .setValue(LeavesBlock.PERSISTENT, false)
                        .setValue(LeavesBlock.DISTANCE, 2),
                3
        );
    }

    private record LeafNode(BlockPos pos, int branchBaseY) {
    }
}