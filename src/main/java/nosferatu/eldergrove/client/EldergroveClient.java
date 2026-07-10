package nosferatu.eldergrove.client;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import nosferatu.eldergrove.Eldergrove;
import nosferatu.eldergrove.EldergroveBlocks;
import nosferatu.eldergrove.EldergroveItems;

@EventBusSubscriber(modid = Eldergrove.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EldergroveClient {
    private static final int MAGICAL_FOREST_EDGE_COLOR = 0x74B85E;
    private static final int MAGICAL_FOREST_COLOR = 0x55D978;
    private static final int MAGICAL_FOREST_DEEP_COLOR = 0x43C99D;
    private static final int MAGICAL_FOREST_LEAF_COLOR = 0x62D6B9;

    private EldergroveClient() {
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                EldergroveClient::eldergroveGrassColor,
                EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get(),
                EldergroveBlocks.ELDERGROVE_GRASS.get(),
                EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get()
        );

        event.register(
                EldergroveClient::plantColor,
                net.minecraft.world.level.block.Blocks.SHORT_GRASS,
                net.minecraft.world.level.block.Blocks.TALL_GRASS,
                net.minecraft.world.level.block.Blocks.FERN,
                net.minecraft.world.level.block.Blocks.LARGE_FERN
        );

        event.register(
                (state, level, pos, tintIndex) -> MAGICAL_FOREST_LEAF_COLOR,
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

    private static int eldergroveGrassColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        int target = magicalGrassTarget(state, pos);
        if (level == null || pos == null) {
            return target;
        }

        int vanilla = BiomeColors.getAverageGrassColor(level, pos);
        double amount = state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get()) ? 0.45D
                : state.is(EldergroveBlocks.ELDERGROVE_GRASS.get()) ? 0.72D
                : 0.92D;
        return blend(vanilla, target, amount);
    }

    private static int plantColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (level == null || pos == null) {
            return MAGICAL_FOREST_COLOR;
        }

        BlockState below = getBelowSafely(level, pos);
        if (below == null) {
            return BiomeColors.getAverageGrassColor(level, pos);
        }

        if (below.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get())) {
            return blend(BiomeColors.getAverageGrassColor(level, pos), MAGICAL_FOREST_EDGE_COLOR, 0.35D);
        }
        if (below.is(EldergroveBlocks.ELDERGROVE_GRASS.get())) {
            return blend(BiomeColors.getAverageGrassColor(level, pos), MAGICAL_FOREST_COLOR, 0.6D);
        }
        if (below.is(EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get())) {
            return blend(BiomeColors.getAverageGrassColor(level, pos), MAGICAL_FOREST_DEEP_COLOR, 0.8D);
        }

        return BiomeColors.getAverageGrassColor(level, pos);
    }

    private static BlockState getBelowSafely(BlockAndTintGetter level, BlockPos pos) {
        try {
            return level.getBlockState(pos.below());
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static int magicalGrassTarget(BlockState state, BlockPos pos) {
        int base = state.is(EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get()) ? MAGICAL_FOREST_EDGE_COLOR
                : state.is(EldergroveBlocks.ELDERGROVE_GRASS.get()) ? MAGICAL_FOREST_COLOR
                : MAGICAL_FOREST_DEEP_COLOR;

        if (pos == null) {
            return base;
        }

        int variant = Math.floorMod(pos.getX() * 734287 + pos.getY() * 159733 + pos.getZ() * 912271, 3);
        if (variant == 0) {
            return blend(base, 0x8ABE5F, 0.18D);
        }
        if (variant == 1) {
            return blend(base, 0x52E890, 0.16D);
        }
        return blend(base, 0x45C8B1, 0.14D);
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
