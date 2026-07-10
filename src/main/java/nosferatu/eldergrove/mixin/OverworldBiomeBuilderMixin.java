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
        // Thaumcraft-style Magical Forest should be an inland forest pocket, not a desert/badlands repaint.
        // Keep it out of the hot temperature band, out of low-continentalness lake/coast bands,
        // and away from highly eroded flat basins that often become big water bowls.
        addEldergrove(mapper, 0.05F, 0.35F, 0.48F, 0.82F, 0.62F, 1.00F, -0.28F, 0.08F, -0.42F, -0.08F);
        addEldergrove(mapper, 0.05F, 0.35F, 0.48F, 0.82F, 0.62F, 1.00F, -0.28F, 0.08F, 0.08F, 0.34F);
        addEldergrove(mapper, 0.22F, 0.52F, 0.55F, 0.88F, 0.68F, 1.00F, -0.24F, 0.06F, -0.38F, -0.10F);
        addEldergrove(mapper, 0.22F, 0.52F, 0.55F, 0.88F, 0.68F, 1.00F, -0.24F, 0.06F, 0.10F, 0.30F);
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
