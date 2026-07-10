package nosferatu.eldergrove.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import nosferatu.eldergrove.EldergroveBiomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(OverworldBiomeBuilder.class)
public abstract class OverworldBiomeBuilderMixin {
    @Inject(method = "addBiomes", at = @At("TAIL"))
    private void eldergrove$addEldergroveBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, CallbackInfo ci) {
        // Thaumcraft-style Magical Forest should be a real inland forest region: uncommon, but not absurdly rare.
        // These bands stay away from deserts/badlands by requiring humidity, and avoid shore/ocean/lake-basin noise
        // by keeping continentalness high and erosion mostly in rougher inland terrain.
        addEldergrove(mapper, 0.06F, 0.60F, 0.48F, 0.98F, 0.56F, 1.00F, -0.58F, 0.14F, -0.86F, 0.86F);
        addEldergrove(mapper, 0.22F, 0.72F, 0.56F, 1.00F, 0.50F, 1.00F, -0.52F, 0.18F, -0.72F, 0.72F);
    }

    private static void addEldergrove(
            Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper,
            float minTemperature,
            float maxTemperature,
            float minHumidity,
            float maxHumidity,
            float minContinentalness,
            float maxContinentalness,
            float minErosion,
            float maxErosion,
            float minWeirdness,
            float maxWeirdness
    ) {
        mapper.accept(Pair.of(
                Climate.parameters(
                        Climate.Parameter.span(minTemperature, maxTemperature),
                        Climate.Parameter.span(minHumidity, maxHumidity),
                        Climate.Parameter.span(minContinentalness, maxContinentalness),
                        Climate.Parameter.span(minErosion, maxErosion),
                        Climate.Parameter.point(0.0F),
                        Climate.Parameter.span(minWeirdness, maxWeirdness),
                        0.0F
                ),
                EldergroveBiomes.ELDERGROVE
        ));
    }
}
