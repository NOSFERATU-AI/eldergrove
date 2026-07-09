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
                    .icon(() -> EldergroveItems.ELDERGROVE_MOSS.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(EldergroveItems.ELDERGROVE_MOSS.get());
                        output.accept(EldergroveItems.ELDERGROVE_PLANKS.get());
                        output.accept(EldergroveItems.ELDERGROVE_LEAVES.get());
                    })
                    .build()
    );

    private EldergroveCreativeTabs() {
    }

    public static void register(IEventBus modBus) {
        CREATIVE_TABS.register(modBus);
    }
}
