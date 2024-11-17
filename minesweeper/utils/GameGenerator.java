package dk.martinersej.minesweeper.utils;

import dk.martinersej.minesweeper.Minesweeper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
        ChunkData chunk = this.createChunkData(world);

        // Skaber en Random med verdens seed + chunk-koordinater for at gøre det unikt pr. chunk
        random = new Random(world.getSeed() + chunkX * 31 + chunkZ * 17);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Set biome to PLAINS
                biomeGrid.setBiome(x, z, Biome.PLAINS);

                // sponge is pure dark in our texture pack
                chunk.setBlock(x, 0, z, Material.SPONGE.getId());

                // if x and z is even, hence 2x2 block pattern
                if (x % 2 == 0 && z % 2 == 0) {
                    // the chance of adding a bomb is 1/6
                    if (random.nextInt(6) == 0) {
                        Location location = new Location(world, chunkX * 16 + x, 0, chunkZ * 16 + z);
                        Minesweeper.get().addBomb(location);
                    }
                }
            }
        }

        return chunk;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<>();
    }
}
