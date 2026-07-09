package nosferatu.eldergrove;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EldergroveItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Eldergrove.MODID);

    public static final DeferredItem<BlockItem> ELDERGROVE_MOSS = ITEMS.registerSimpleBlockItem(
            "eldergrove_moss",
            EldergroveBlocks.ELDERGROVE_MOSS
    );

    public static final DeferredItem<BlockItem> ELDERGROVE_PLANKS = ITEMS.registerSimpleBlockItem(
            "eldergrove_planks",
            EldergroveBlocks.ELDERGROVE_PLANKS
    );

    public static final DeferredItem<BlockItem> ELDERGROVE_LEAVES = ITEMS.registerSimpleBlockItem(
            "eldergrove_leaves",
            EldergroveBlocks.ELDERGROVE_LEAVES
    );

    private EldergroveItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
