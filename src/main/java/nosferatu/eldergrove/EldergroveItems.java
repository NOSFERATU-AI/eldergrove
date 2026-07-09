package nosferatu.eldergrove;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EldergroveItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Eldergrove.MODID);

    public static final DeferredItem<Item> HEART_OF_THE_GROVE = ITEMS.registerSimpleItem(
            "heart_of_the_grove",
            new Item.Properties()
    );

    private EldergroveItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
