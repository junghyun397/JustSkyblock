package do1phin.mine2021.utils;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.Map;

public class EmptyGenerator extends Generator {

    private ChunkManager chunkManager;

    public EmptyGenerator(Map ignored) {}

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void init(ChunkManager chunkManager, NukkitRandom nukkitRandom) {
        this.chunkManager = chunkManager;
    }

    @Override
    public void generateChunk(int i, int i1) { }

    @Override
    public void populateChunk(int i, int i1) { }

    @Override
    public Map<String, Object> getSettings() {
        return null;
    }

    @Override
    public String getName() {
        return "EMPTY";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(128.0, 65.0, 128.0);
    }

    @Override
    public ChunkManager getChunkManager() {
        return chunkManager;
    }
}
