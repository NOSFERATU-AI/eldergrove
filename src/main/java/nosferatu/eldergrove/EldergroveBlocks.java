package nosferatu.eldergrove;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EldergroveBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Eldergrove.MODID);

    public static final DeferredBlock<Block> ELDERGROVE_MOSS = BLOCKS.registerSimpleBlock(
            "eldergrove_moss",
            BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK)
                    .mapColor(MapColor.COLOR_PURPLE)
                    .sound(SoundType.MOSS)
    );

    public static final DeferredBlock<Block> ELDERGROVE_PLANKS = BLOCKS.registerSimpleBlock(
            "eldergrove_planks",
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .mapColor(MapColor.WOOD)
                    .sound(SoundType.WOOD)
    );

    public static final DeferredBlock<LeavesBlock> ELDERGROVE_LEAVES = BLOCKS.register(
            "eldergrove_leaves",
            () -> new LeavesBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .sound(SoundType.AZALEA_LEAVES)
            )
    );

    private EldergroveBlocks() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
