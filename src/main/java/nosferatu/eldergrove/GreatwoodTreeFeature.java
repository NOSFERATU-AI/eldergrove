package nosferatu.eldergrove;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GreatwoodTreeFeature extends Feature<NoneFeatureConfiguration> {
    public GreatwoodTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return GreatwoodTreeGenerator.grow(context.level(), context.origin(), context.random());
    }
}
