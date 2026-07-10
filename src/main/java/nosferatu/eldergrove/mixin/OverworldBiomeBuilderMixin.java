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
        // Moist inland forest band: keeps the main Eldergrove identity close to old magical forests.
        addEldergrove(mapper, 0.05F, 0.72F, 0.50F, 1.00F, 0.34F, 1.00F, -0.86F, 0.12F, -1.00F, 1.00F);

        // Secondary forest pockets: improves findability without pushing too hard into dry/coastal climates.
        addEldergrove(mapper, 0.16F, 0.78F, 0.44F, 1.00F, 0.30F, 0.88F, -0.58F, 0.30F, -0.82F, 0.82F);

        // Plains/meadow-friendly band: lets Eldergrove appear as a magical grove in open land, not only forests.
        addEldergrove(mapper, 0.18F, 0.88F, 0.28F, 0.76F, 0.34F, 1.00F, -0.70F, 0.48F, -1.00F, 1.00F);
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