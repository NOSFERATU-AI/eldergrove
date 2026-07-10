package nosferatu.eldergrove;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EldergroveCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            Eldergrove.MODID
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ELDERGROVE_TAB = CREATIVE_TABS.register(
            "eldergrove",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.eldergrove"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> EldergroveItems.GROVE_HEART.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(EldergroveItems.ELDERWOOD_LOG.get());
                        output.accept(EldergroveItems.ELDERWOOD_PLANKS.get());
                        output.accept(EldergroveItems.ELDERWOOD_STAIRS.get());
                        output.accept(EldergroveItems.ELDERWOOD_SLAB.get());
                        output.accept(EldergroveItems.ELDERWOOD_LEAVES.get());
                        output.accept(EldergroveItems.ELDERWOOD_SAPLING.get());
                        output.accept(EldergroveItems.GREATWOOD_LOG.get());
                        output.accept(EldergroveItems.GREATWOOD_PLANKS.get());
                        output.accept(EldergroveItems.GREATWOOD_STAIRS.get());
                        output.accept(EldergroveItems.GREATWOOD_SLAB.get());
                        output.accept(EldergroveItems.GREATWOOD_LEAVES.get());
                        output.accept(EldergroveItems.SHIMMERLEAF.get());
                        output.accept(EldergroveItems.VISHROOM.get());
                        output.accept(EldergroveItems.GROVE_HEART.get());
                    })
                    .build()
    );

    private EldergroveCreativeTabs() {
    }

    public static void register(IEventBus modBus) {
        CREATIVE_TABS.register(modBus);
    }
}