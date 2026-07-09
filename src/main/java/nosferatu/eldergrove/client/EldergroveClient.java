package nosferatu.eldergrove.client;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import nosferatu.eldergrove.Eldergrove;
import nosferatu.eldergrove.EldergroveBlocks;
import nosferatu.eldergrove.EldergroveItems;
import nosferatu.eldergrove.EldergroveSpreadingCoreBlock;

@EventBusSubscriber(modid = Eldergrove.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EldergroveClient {
    private static final int MAGICAL_FOREST_EDGE_COLOR = 0x7ACB65;
    private static final int MAGICAL_FOREST_COLOR = 0x55FF81;
    private static final int MAGICAL_FOREST_DEEP_COLOR = 0x66FFC5;
    private static final int MAGICAL_FOREST_LEAF_COLOR = 0x62D6B9;

    private EldergroveClient() {
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                EldergroveClient::grassColor,
                Blocks.GRASS_BLOCK,
                Blocks.SHORT_GRASS,
                Blocks.TALL_GRASS,
                Blocks.FERN,
                Blocks.LARGE_FERN,
                EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get(),
                EldergroveBlocks.ELDERGROVE_GRASS.get(),
                EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get()
        );

        event.register(
                EldergroveClient::leafColor,
                Blocks.OAK_LEAVES,
                Blocks.BIRCH_LEAVES,
                Blocks.SPRUCE_LEAVES,
                Blocks.JUNGLE_LEAVES,
                Blocks.ACACIA_LEAVES,
                Blocks.DARK_OAK_LEAVES,
                EldergroveBlocks.ELDERWOOD_LEAVES.get()
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> MAGICAL_FOREST_COLOR,
                EldergroveItems.ELDERGROVE_GRASS_FAINT.get(),
                EldergroveItems.ELDERGROVE_GRASS.get(),
                EldergroveItems.ELDERGROVE_GRASS_DEEP.get()
        );

        event.register(
                (stack, tintIndex) -> MAGICAL_FOREST_LEAF_COLOR,
                EldergroveItems.ELDERWOOD_LEAVES.get()
        );
    }

    private static int grassColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (level == null || pos == null) {
            return MAGICAL_FOREST_COLOR;
        }

        int baseColor = BiomeColors.getAverageGrassColor(level, pos);
        double influence = findAuraInfluence(level, pos);
        if (influence <= 0.0D) {
            return baseColor;
        }

        return blend(baseColor, magicalGrassTarget(pos), influence);
    }

    private static int leafColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (level == null || pos == null) {
            return MAGICAL_FOREST_LEAF_COLOR;
        }

        int baseColor = BiomeColors.getAverageFoliageColor(level, pos);
        double influence = state.is(EldergroveBlocks.ELDERWOOD_LEAVES.get()) ? 1.0D : findAuraInfluence(level, pos);
        if (influence <= 0.0D) {
            return baseColor;
        }

        return blend(baseColor, MAGICAL_FOREST_LEAF_COLOR, influence);
    }

    private static double findAuraInfluence(BlockAndTintGetter level, BlockPos pos) {
        int radius = EldergroveSpreadingCoreBlock.AURA_RADIUS;
        double strongest = 0.0D;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int y = -8; y <= 8; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distanceSq = x * x + y * y + z * z;
                    if (distanceSq > radius * radius) {
                        continue;
                    }

                    mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (level.getBlockState(mutable).is(EldergroveBlocks.GROVE_HEART.get())) {
                        double distance = Math.sqrt(distanceSq);
                        strongest = Math.max(strongest, 1.0D - distance / radius);
                    }
                }
            }
        }

        return strongest;
    }

    private static int magicalGrassTarget(BlockPos pos) {
        int variant = Math.floorMod(pos.getX() * 734287 + pos.getY() * 159733 + pos.getZ() * 912271, 3);
        if (variant == 0) {
            return MAGICAL_FOREST_EDGE_COLOR;
        }
        if (variant == 1) {
            return MAGICAL_FOREST_COLOR;
        }
        return MAGICAL_FOREST_DEEP_COLOR;
    }

    private static int blend(int from, int to, double amount) {
        double clamped = Math.max(0.0D, Math.min(1.0D, amount));
        int fromR = (from >> 16) & 255;
        int fromG = (from >> 8) & 255;
        int fromB = from & 255;
        int toR = (to >> 16) & 255;
        int toG = (to >> 8) & 255;
        int toB = to & 255;
        int r = (int) (fromR + (toR - fromR) * clamped);
        int g = (int) (fromG + (toG - fromG) * clamped);
        int b = (int) (fromB + (toB - fromB) * clamped);
        return (r << 16) | (g << 8) | b;
    }
}
