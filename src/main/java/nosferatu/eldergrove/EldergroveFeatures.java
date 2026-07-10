package nosferatu.eldergrove;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EldergroveFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Eldergrove.MODID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> ELDERWOOD_TREE = FEATURES.register(
            "elderwood_tree",
            () -> new ElderwoodTreeFeature(NoneFeatureConfiguration.CODEC)
    );

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> ELDERGROVE_OAK_TREE = FEATURES.register(
            "eldergrove_oak_tree",
            () -> new EldergroveOakTreeFeature(NoneFeatureConfiguration.CODEC)
    );

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> GREATWOOD_TREE = FEATURES.register(
            "greatwood_tree",
            () -> new GreatwoodTreeFeature(NoneFeatureConfiguration.CODEC)
    );

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> TAINTED_SURFACE = FEATURES.register(
            "tainted_surface",
            () -> new TaintedSurfaceFeature(NoneFeatureConfiguration.CODEC)
    );

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> TAINTED_DEAD_TREE = FEATURES.register(
            "tainted_dead_tree",
            () -> new TaintedDeadTreeFeature(NoneFeatureConfiguration.CODEC)
    );

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> FALLEN_LOG = FEATURES.register(
            "fallen_log",
            () -> new FallenLogFeature(NoneFeatureConfiguration.CODEC)
    );

    private EldergroveFeatures() {
    }

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }
}