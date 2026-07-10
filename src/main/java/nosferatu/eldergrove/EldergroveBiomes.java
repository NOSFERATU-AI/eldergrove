package nosferatu.eldergrove;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class EldergroveBiomes {
    public static final ResourceKey<Biome> ELDERGROVE = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(Eldergrove.MODID, "eldergrove")
    );

    private EldergroveBiomes() {
    }
}
