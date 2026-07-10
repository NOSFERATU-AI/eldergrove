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

    public static final DeferredItem<BlockItem> TAINTED_SOIL = ITEMS.registerSimpleBlockItem(
            "tainted_soil",
            EldergroveBlocks.TAINTED_SOIL
    );

    public static final DeferredItem<BlockItem> TAINTED_CRUST = ITEMS.registerSimpleBlockItem(
            "tainted_crust",
            EldergroveBlocks.TAINTED_CRUST
    );

    public static final DeferredItem<BlockItem> TAINTED_ROCK = ITEMS.registerSimpleBlockItem(
            "tainted_rock",
            EldergroveBlocks.TAINTED_ROCK
    );

    public static final DeferredItem<BlockItem> TAINTED_HEART = ITEMS.registerSimpleBlockItem(
            "tainted_heart",
            EldergroveBlocks.TAINTED_HEART
    );

    public static final DeferredItem<BlockItem> TAINTED_GROWTH = ITEMS.registerSimpleBlockItem(
            "tainted_growth",
            EldergroveBlocks.TAINTED_GROWTH
    );

    public static final DeferredItem<BlockItem> TAINTED_TENDRIL = ITEMS.registerSimpleBlockItem(
            "tainted_tendril",
            EldergroveBlocks.TAINTED_TENDRIL
    );

    public static final DeferredItem<BlockItem> TAINTED_FIBRE = ITEMS.registerSimpleBlockItem(
            "tainted_fibre",
            EldergroveBlocks.TAINTED_FIBRE
    );

    public static final DeferredItem<BlockItem> ELDERWOOD_PLANKS = ITEMS.registerSimpleBlockItem(
            "elderwood_planks",
            EldergroveBlocks.ELDERWOOD_PLANKS
    );

    public static final DeferredItem<BlockItem> ELDERWOOD_STAIRS = ITEMS.registerSimpleBlockItem(
            "elderwood_stairs",
            EldergroveBlocks.ELDERWOOD_STAIRS
    );

    public static final DeferredItem<BlockItem> ELDERWOOD_SLAB = ITEMS.registerSimpleBlockItem(
            "elderwood_slab",
            EldergroveBlocks.ELDERWOOD_SLAB
    );

    public static final DeferredItem<BlockItem> ELDERWOOD_LOG = ITEMS.registerSimpleBlockItem(
            "elderwood_log",
            EldergroveBlocks.ELDERWOOD_LOG
    );

    public static final DeferredItem<BlockItem> ELDERWOOD_LEAVES = ITEMS.registerSimpleBlockItem(
            "elderwood_leaves",
            EldergroveBlocks.ELDERWOOD_LEAVES
    );

    public static final DeferredItem<BlockItem> ELDERWOOD_SAPLING = ITEMS.registerSimpleBlockItem(
            "elderwood_sapling",
            EldergroveBlocks.ELDERWOOD_SAPLING
    );

    public static final DeferredItem<BlockItem> GREATWOOD_PLANKS = ITEMS.registerSimpleBlockItem(
            "greatwood_planks",
            EldergroveBlocks.GREATWOOD_PLANKS
    );

    public static final DeferredItem<BlockItem> GREATWOOD_STAIRS = ITEMS.registerSimpleBlockItem(
            "greatwood_stairs",
            EldergroveBlocks.GREATWOOD_STAIRS
    );

    public static final DeferredItem<BlockItem> GREATWOOD_SLAB = ITEMS.registerSimpleBlockItem(
            "greatwood_slab",
            EldergroveBlocks.GREATWOOD_SLAB
    );

    public static final DeferredItem<BlockItem> GREATWOOD_LOG = ITEMS.registerSimpleBlockItem(
            "greatwood_log",
            EldergroveBlocks.GREATWOOD_LOG
    );

    public static final DeferredItem<BlockItem> GREATWOOD_LEAVES = ITEMS.registerSimpleBlockItem(
            "greatwood_leaves",
            EldergroveBlocks.GREATWOOD_LEAVES
    );

    public static final DeferredItem<BlockItem> GREATWOOD_SAPLING = ITEMS.registerSimpleBlockItem(
            "greatwood_sapling",
            EldergroveBlocks.GREATWOOD_SAPLING
    );

    public static final DeferredItem<BlockItem> SHIMMERLEAF = ITEMS.registerSimpleBlockItem(
            "shimmerleaf",
            EldergroveBlocks.SHIMMERLEAF
    );

    public static final DeferredItem<BlockItem> VISHROOM = ITEMS.registerSimpleBlockItem(
            "vishroom",
            EldergroveBlocks.VISHROOM
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
