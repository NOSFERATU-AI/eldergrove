package nosferatu.eldergrove;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EldergroveBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Eldergrove.MODID);

    public static final DeferredBlock<Block> ELDERGROVE_GRASS_FAINT = BLOCKS.register(
            "eldergrove_grass_faint",
            () -> new EldergroveGrassBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)
                            .mapColor(MapColor.COLOR_CYAN)
                            .sound(SoundType.GRASS)
            )
    );

    public static final DeferredBlock<Block> ELDERGROVE_GRASS = BLOCKS.register(
            "eldergrove_grass",
            () -> new EldergroveGrassBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)
                            .mapColor(MapColor.COLOR_CYAN)
                            .sound(SoundType.GRASS)
            )
    );

    public static final DeferredBlock<Block> ELDERGROVE_GRASS_DEEP = BLOCKS.register(
            "eldergrove_grass_deep",
            () -> new EldergroveGrassBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)
                            .mapColor(MapColor.COLOR_CYAN)
                            .sound(SoundType.GRASS)
            )
    );

    public static final DeferredBlock<Block> ELDERGROVE_MOSS = BLOCKS.registerSimpleBlock(
            "eldergrove_moss",
            BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK)
                    .mapColor(MapColor.COLOR_CYAN)
                    .sound(SoundType.MOSS)
    );

    public static final DeferredBlock<Block> ELDERWOOD_PLANKS = BLOCKS.registerSimpleBlock(
            "elderwood_planks",
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .mapColor(MapColor.WOOD)
                    .sound(SoundType.WOOD)
    );

    public static final DeferredBlock<RotatedPillarBlock> ELDERWOOD_LOG = BLOCKS.register(
            "elderwood_log",
            () -> new RotatedPillarBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_LOG)
                            .mapColor(MapColor.WOOD)
                            .sound(SoundType.WOOD)
            )
    );

    public static final DeferredBlock<LeavesBlock> ELDERWOOD_LEAVES = BLOCKS.register(
            "elderwood_leaves",
            () -> new LeavesBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                            .mapColor(MapColor.COLOR_CYAN)
                            .sound(SoundType.AZALEA_LEAVES)
            )
    );

    public static final DeferredBlock<Block> GROVE_HEART = BLOCKS.register(
            "grove_heart",
            () -> new EldergroveSpreadingCoreBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ROOTED_DIRT)
                            .mapColor(MapColor.COLOR_CYAN)
                            .sound(SoundType.AMETHYST)
                            .lightLevel(state -> 11)
                            .randomTicks()
            )
    );

    private EldergroveBlocks() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
