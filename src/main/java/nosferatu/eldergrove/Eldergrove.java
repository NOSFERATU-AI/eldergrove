package nosferatu.eldergrove;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Eldergrove.MODID)
public final class Eldergrove {
    public static final String MODID = "eldergrove";

    public Eldergrove(IEventBus modBus, ModContainer modContainer) {
        EldergroveBlocks.register(modBus);
        EldergroveItems.register(modBus);
        EldergroveCreativeTabs.register(modBus);
        EldergroveFeatures.register(modBus);
    }
}
