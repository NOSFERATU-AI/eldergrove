package nosferatu.eldergrove;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EldergroveItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Eldergrove.MODID);

    public static final DeferredItem<BlockItem> ELDERGROVE_GRASS_FAINT = ITEMS.registerSimpleBlockItem(
            "eldergrove_grass_faint",
            EldergroveBlocks.ELDERGROVE_GRASS_FAINT
    );

    public static final DeferredItem<BlockItem> ELDERGROVE_GRASS = ITEMS.registerSimpleBlockItem(
            "eldergrove_grass",
            EldergroveBlocks.ELDERGROVE_GRASS
    );

    public static final DeferredItem<BlockItem> ELDERGROVE_GRASS_DEEP = ITEMS.registerSimpleBlockItem(
            "eldergrove_grass_deep",
            EldergroveBlocks.ELDERGROVE_GRASS_DEEP
    );

    public static final DeferredItem<BlockItem> ELDERGROVE_MOSS = ITEMS.registerSimpleBlockItem(
            "eldergrove_moss",
            EldergroveBlocks.ELDERGROVE_MOSS
    );

    public static final DeferredItem<BlockItem> ELDERWOOD_PLANKS = ITEMS.registerSimpleBlockItem(
            "elderwood_planks",
            EldergroveBlocks.ELDERWOOD_PLANKS
    );

    public static final DeferredItem<BlockItem> ELDERWOOD_LOG = ITEMS.registerSimpleBlockItem(
            "elderwood_log",
            EldergroveBlocks.ELDERWOOD_LOG
    );

    public static final DeferredItem<BlockItem> ELDERWOOD_LEAVES = ITEMS.registerSimpleBlockItem(
            "elderwood_leaves",
            EldergroveBlocks.ELDERWOOD_LEAVES
    );

    public static final DeferredItem<BlockItem> GROVE_HEART = ITEMS.registerSimpleBlockItem(
            "grove_heart",
            EldergroveBlocks.GROVE_HEART
    );

    private EldergroveItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
