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
        int heightLimit = 11 + random.nextInt(8);
        int trunkHeight = Math.max(7, (int) (heightLimit * 0.618D));
        BlockPos root = findGrowRoot(level, origin, heightLimit + trunkHeight + 5);
        if (root == null) {
            return false;
        }

        placeButtressRoots(level, root, random);
        generateGreatwoodPart(level, root, heightLimit, 1.12D, 0.90D, random);

        // Original-style Greatwood is two overlapping old-tree crowns, but the branches must stay readable.
        BlockPos upperOrigin = root.above(trunkHeight);
        generateGreatwoodPart(level, upperOrigin, heightLimit, 1.52D, 1.05D, random);
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
                BlockPos branchBase = new BlockPos(base.getX(), node.branchBaseY(), base.getZ());
                List<BlockPos> branch = placeBlockLine(level, branchBase, node.pos(), true);
                decorateBranch(level, branch, random);
                coverBranchWithLeaves(level, branch, random);
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
        for (int y = 0; y < 5; y++) {
            float radius = (y == 0 || y == 4) ? 2.0F : 3.0F;
            if (y == 2 && random.nextBoolean()) {
                radius = 3.25F;
            }
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

                if (radius >= 3.0F && random.nextInt(7) == 0) {
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

    private static List<BlockPos> placeBlockLine(LevelAccessor level, BlockPos from, BlockPos to, boolean thinBranch) {
        int[] start = new int[]{from.getX(), from.getY(), from.getZ()};
        int[] end = new int[]{to.getX(), to.getY(), to.getZ()};
        int[] delta = new int[]{end[0] - start[0], end[1] - start[1], end[2] - start[2]};
        List<BlockPos> placed = new ArrayList<>();

        byte dominant = 0;
        for (byte axis = 0; axis < 3; axis++) {
            if (Math.abs(delta[axis]) > Math.abs(delta[dominant])) {
                dominant = axis;
            }
        }

        if (delta[dominant] == 0) {
            return placed;
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
            placed.add(pos);

            if (!thinBranch && axis != Direction.Axis.Y && Math.abs(pos.getY() - from.getY()) < 2) {
                setLog(level, pos.below(), log(axis));
                placed.add(pos.below());
            }

            last = pos;
        }
        return placed;
    }

    private static void decorateBranch(LevelAccessor level, List<BlockPos> branch, RandomSource random) {
        if (branch.size() < 4) {
            return;
        }
        for (int i = 2; i < branch.size() - 1; i++) {
            if (random.nextInt(5) != 0) {
                continue;
            }
            BlockPos pos = branch.get(i);
            Direction side = random.nextBoolean() ? Direction.NORTH : Direction.EAST;
            if (random.nextBoolean()) {
                side = side.getOpposite();
            }
            BlockPos knot = pos.relative(side);
            if (canReplace(level, knot)) {
                setLog(level, knot, log(side.getAxis()));
            }
        }
    }

    private static void coverBranchWithLeaves(LevelAccessor level, List<BlockPos> branch, RandomSource random) {
        for (int i = 2; i < branch.size(); i += 2) {
            BlockPos pos = branch.get(i);
            if (random.nextInt(3) == 0) {
                placeLeafCluster(level, pos.above(), 1 + random.nextInt(2), random);
            }
        }
    }

    private static void placeLeafCluster(LevelAccessor level, BlockPos center, int radius, RandomSource random) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    int distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (distance > radius + 1 || random.nextInt(8) == 0) {
                        continue;
                    }
                    BlockPos pos = center.offset(x, y, z);
                    if (canReplace(level, pos)) {
                        setLeaves(level, pos);
                    }
                }
            }
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

    private static BlockPos findGrowRoot(LevelAccessor level, BlockPos origin, int maxHeight) {
        for (int xOffset = -2; xOffset <= 1; xOffset++) {
            for (int zOffset = -2; zOffset <= 1; zOffset++) {
                BlockPos candidate = origin.offset(xOffset, 0, zOffset);
                if (canGrow(level, candidate, maxHeight)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private static boolean canGrow(LevelAccessor level, BlockPos origin, int maxHeight) {
        if (!level.getBlockState(origin.below()).is(BlockTags.DIRT)
                || !level.getBlockState(origin.east().below()).is(BlockTags.DIRT)
                || !level.getBlockState(origin.south().below()).is(BlockTags.DIRT)
                || !level.getBlockState(origin.east().south().below()).is(BlockTags.DIRT)) {
            return false;
        }

        for (int y = 0; y <= maxHeight; y++) {
            int radius = y < 4 ? 4 : y > maxHeight - 12 ? 11 : 6;
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
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES);
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