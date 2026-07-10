package nosferatu.eldergrove;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;

import java.util.List;

public final class EldergroveBiomeSpreader {
    private EldergroveBiomeSpreader() {
    }

    public static boolean setEldergroveBiome(ServerLevel level, BlockPos pos) {
        if (pos.getY() < level.getMinBuildHeight() || pos.getY() >= level.getMaxBuildHeight()) {
            return false;
        }

        LevelChunk chunk = level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        Holder<Biome> eldergrove = level.registryAccess()
                .lookupOrThrow(Registries.BIOME)
                .getOrThrow(EldergroveBiomes.ELDERGROVE);

        int quartX = pos.getX() >> 2;
        int quartZ = pos.getZ() >> 2;
        int localQuartX = quartX & 3;
        int localQuartZ = quartZ & 3;
        boolean changed = false;

        for (int sectionIndex = 0; sectionIndex < chunk.getSections().length; sectionIndex++) {
            PalettedContainer<Holder<Biome>> biomes = (PalettedContainer<Holder<Biome>>) chunk
                    .getSection(sectionIndex)
                    .getBiomes();

            for (int localQuartY = 0; localQuartY < 4; localQuartY++) {
                Holder<Biome> current = biomes.get(localQuartX, localQuartY, localQuartZ);
                if (!current.is(EldergroveBiomes.ELDERGROVE)) {
                    biomes.getAndSetUnchecked(localQuartX, localQuartY, localQuartZ, eldergrove);
                    changed = true;
                }
            }
        }

        if (!changed) {
            return false;
        }

        chunk.setUnsaved(true);
        sendBiomeUpdate(level, chunk, pos);
        refreshSurface(level, pos);
        return true;
    }

    private static void sendBiomeUpdate(ServerLevel level, LevelChunk chunk, BlockPos pos) {
        ClientboundChunksBiomesPacket packet = ClientboundChunksBiomesPacket.forChunks(List.of(chunk));
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 160.0D * 160.0D) {
                player.connection.send(packet);
            }
        }
    }

    private static void refreshSurface(ServerLevel level, BlockPos pos) {
        for (int y = -1; y <= 2; y++) {
            BlockPos refreshPos = pos.above(y);
            level.sendBlockUpdated(refreshPos, level.getBlockState(refreshPos), level.getBlockState(refreshPos), 2);
        }
    }

    public static boolean isNaturalSurface(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(BlockTags.DIRT)
                && !level.getBlockState(pos.above()).isSolidRender(level, pos.above());
    }
}
