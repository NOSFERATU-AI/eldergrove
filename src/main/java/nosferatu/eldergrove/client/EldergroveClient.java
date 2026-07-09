package nosferatu.eldergrove.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import nosferatu.eldergrove.Eldergrove;
import nosferatu.eldergrove.EldergroveBlocks;
import nosferatu.eldergrove.EldergroveItems;

@EventBusSubscriber(modid = Eldergrove.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EldergroveClient {
    private static final int MAGICAL_FOREST_COLOR = 0x55FF81;
    private static final int MAGICAL_FOREST_EDGE_COLOR = 0x66F4AB;
    private static final int MAGICAL_FOREST_DEEP_COLOR = 0x66FFC5;

    private EldergroveClient() {
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, level, pos, tintIndex) -> MAGICAL_FOREST_EDGE_COLOR,
                EldergroveBlocks.ELDERGROVE_GRASS_FAINT.get()
        );
        event.register(
                (state, level, pos, tintIndex) -> MAGICAL_FOREST_COLOR,
                EldergroveBlocks.ELDERGROVE_GRASS.get()
        );
        event.register(
                (state, level, pos, tintIndex) -> MAGICAL_FOREST_DEEP_COLOR,
                EldergroveBlocks.ELDERGROVE_GRASS_DEEP.get(),
                EldergroveBlocks.ELDERWOOD_LEAVES.get()
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> MAGICAL_FOREST_EDGE_COLOR,
                EldergroveItems.ELDERGROVE_GRASS_FAINT.get()
        );
        event.register(
                (stack, tintIndex) -> MAGICAL_FOREST_COLOR,
                EldergroveItems.ELDERGROVE_GRASS.get()
        );
        event.register(
                (stack, tintIndex) -> MAGICAL_FOREST_DEEP_COLOR,
                EldergroveItems.ELDERGROVE_GRASS_DEEP.get(),
                EldergroveItems.ELDERWOOD_LEAVES.get()
        );
    }
}
