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
        // Eldergrove should feel like a Thaumcraft-style magical pocket inside forest climates:
        // easier to find than before, but still kept away from deserts/badlands by requiring decent humidity.
        // Continentalness starts at near-inland/mid-inland so it can appear near normal forests, not only far inland.
        addEldergrove(mapper, 0.05F, 0.62F, 0.42F, 1.00F, 0.34F, 1.00F, -0.62F, 0.32F, -0.90F, 0.90F);
        addEldergrove(mapper, 0.22F, 0.78F, 0.50F, 1.00F, 0.28F, 0.92F, -0.52F, 0.38F, -0.76F, 0.76F);
        addEldergrove(mapper, 0.00F, 0.42F, 0.56F, 1.00F, 0.42F, 1.00F, -0.72F, 0.18F, -0.72F, 0.72F);
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
