package nosferatu.eldergrove;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
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

    public static final DeferredBlock<Block> TAINTED_SOIL = BLOCKS.registerSimpleBlock(
            "tainted_soil",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_SOIL)
                    .mapColor(MapColor.COLOR_PURPLE)
                    .sound(SoundType.SOUL_SOIL)
    );

    public static final DeferredBlock<Block> TAINTED_CRUST = BLOCKS.registerSimpleBlock(
            "tainted_crust",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SCULK)
                    .mapColor(MapColor.COLOR_PURPLE)
                    .sound(SoundType.SCULK)
                    .lightLevel(state -> 1)
    );

    public static final DeferredBlock<Block> TAINTED_ROCK = BLOCKS.registerSimpleBlock(
            "tainted_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)
                    .mapColor(MapColor.COLOR_PURPLE)
                    .sound(SoundType.DEEPSLATE)
                    .lightLevel(state -> 1)
    );

    public static final DeferredBlock<Block> TAINTED_HEART = BLOCKS.register(
            "tainted_heart",
            () -> new TaintedHeartBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.CRYING_OBSIDIAN)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .sound(SoundType.SCULK)
                            .strength(4.0F, 12.0F)
                            .lightLevel(state -> 8)
                            .randomTicks()
            )
    );

    public static final DeferredBlock<Block> TAINTED_GROWTH = BLOCKS.register(
            "tainted_growth",
            () -> new EldergrovePlantBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_ROOTS)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .noCollission()
                            .instabreak()
                            .lightLevel(state -> 3)
                            .sound(SoundType.ROOTS)
            )
    );

    public static final DeferredBlock<Block> TAINTED_TENDRIL = BLOCKS.register(
            "tainted_tendril",
            () -> new EldergrovePlantBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_ROOTS)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .noCollission()
                            .instabreak()
                            .lightLevel(state -> 2)
                            .sound(SoundType.ROOTS)
            )
    );

    public static final DeferredBlock<Block> TAINTED_FIBRE = BLOCKS.register(
            "tainted_fibre",
            () -> new EldergrovePlantBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_ROOTS)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .noCollission()
                            .instabreak()
                            .lightLevel(state -> 2)
                            .sound(SoundType.ROOTS)
            )
    );

    public static final DeferredBlock<Block> ELDERWOOD_PLANKS = BLOCKS.registerSimpleBlock(
            "elderwood_planks",
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .mapColor(MapColor.WOOD)
                    .sound(SoundType.WOOD)
    );

    public static final DeferredBlock<StairBlock> ELDERWOOD_STAIRS = BLOCKS.register(
            "elderwood_stairs",
            () -> new StairBlock(
                    ELDERWOOD_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
                            .mapColor(MapColor.WOOD)
                            .sound(SoundType.WOOD)
            )
    );

    public static final DeferredBlock<SlabBlock> ELDERWOOD_SLAB = BLOCKS.register(
            "elderwood_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)
                            .mapColor(MapColor.WOOD)
                            .sound(SoundType.WOOD)
            )
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

    public static final DeferredBlock<SaplingBlock> ELDERWOOD_SAPLING = BLOCKS.register(
            "elderwood_sapling",
            () -> new ElderwoodSaplingBlock(
                    TreeGrower.OAK,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
                            .mapColor(MapColor.COLOR_CYAN)
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<Block> GREATWOOD_PLANKS = BLOCKS.registerSimpleBlock(
            "greatwood_planks",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_PLANKS)
                    .mapColor(MapColor.WOOD)
                    .sound(SoundType.WOOD)
    );

    public static final DeferredBlock<StairBlock> GREATWOOD_STAIRS = BLOCKS.register(
            "greatwood_stairs",
            () -> new StairBlock(
                    GREATWOOD_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_STAIRS)
                            .mapColor(MapColor.WOOD)
                            .sound(SoundType.WOOD)
            )
    );

    public static final DeferredBlock<SlabBlock> GREATWOOD_SLAB = BLOCKS.register(
            "greatwood_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_SLAB)
                            .mapColor(MapColor.WOOD)
                            .sound(SoundType.WOOD)
            )
    );

    public static final DeferredBlock<RotatedPillarBlock> GREATWOOD_LOG = BLOCKS.register(
            "greatwood_log",
            () -> new RotatedPillarBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_LOG)
                            .mapColor(MapColor.WOOD)
                            .sound(SoundType.WOOD)
            )
    );

    public static final DeferredBlock<LeavesBlock> GREATWOOD_LEAVES = BLOCKS.register(
            "greatwood_leaves",
            () -> new LeavesBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_LEAVES)
                            .mapColor(MapColor.PLANT)
                            .sound(SoundType.AZALEA_LEAVES)
            )
    );

    public static final DeferredBlock<SaplingBlock> GREATWOOD_SAPLING = BLOCKS.register(
            "greatwood_sapling",
            () -> new GreatwoodSaplingBlock(
                    TreeGrower.DARK_OAK,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_SAPLING)
                            .mapColor(MapColor.PLANT)
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<Block> SHIMMERLEAF = BLOCKS.register(
            "shimmerleaf",
            () -> new EldergrovePlantBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.LILY_OF_THE_VALLEY)
                            .mapColor(MapColor.COLOR_CYAN)
                            .noCollission()
                            .instabreak()
                            .lightLevel(state -> 7)
                            .sound(SoundType.GRASS)
            )
    );

    public static final DeferredBlock<Block> VISHROOM = BLOCKS.register(
            "vishroom",
            () -> new VishroomBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.RED_MUSHROOM)
                            .mapColor(MapColor.PLANT)
                            .noCollission()
                            .instabreak()
                            .lightLevel(state -> 4)
                            .sound(SoundType.GRASS)
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
