package do1phin.mine2021.data;

import cn.nukkit.block.Block;
import cn.nukkit.utils.ConfigSection;
import do1phin.mine2021.utils.Pair;
import do1phin.mine2021.utils.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {

    private final cn.nukkit.utils.Config pluginConfig;

    public Config(cn.nukkit.utils.Config config) {
        this.pluginConfig = config;
    }

    public String getString(String key) {
        return this.pluginConfig.getString(key);
    }

    public int getInt(String key) {
        return this.pluginConfig.getInt(key);
    }

    public cn.nukkit.utils.Config getPluginConfig() {
        return this.pluginConfig;
    }

    public int[][][] parseSkyblockDefaultIslandShape() {
        List<?> dim1 = this.pluginConfig.getList("skyblock.default-island-shape");
        List<List<List<Integer>>> dist1 = new ArrayList<>();
        int shapeY;
        int shapeX = 0;
        int shapeZ = 0;
        for (Object dim2 : dim1) {
            List<List<Integer>> dist2 = new ArrayList<>();
            for (Object dim3 : (List<?>) dim2) {
                List<Integer> dist3 = new ArrayList<>();
                for (Object block : (List<?>) dim3) {
                    dist3.add((int) block);
                }
                dist2.add(dist3);
                shapeX = ((List<?>) dim3).size();
            }
            dist1.add(dist2);
            shapeZ = ((List<?>) dim2).size();
        }
        shapeY = dim1.size();

        int[][][] islandShape = new int[shapeY][shapeZ][shapeX];
        for (int y = 0; y < shapeY; y++)
            for (int z = 0; z < shapeZ; z++)
                for (int x = 0; x < shapeX; x++)
                    islandShape[y][z][x] = dist1.get(y).get(z).get(x);

        return islandShape;
    }

    public List<Tuple<Integer, Integer, Integer>> parseSkyblockDefaultItemList() {
        ConfigSection configSection = this.pluginConfig.getSection("default-item-list");
        List<Tuple<Integer, Integer, Integer>> defaultItemList = new ArrayList<>();
        for (Map.Entry<String, Object> entry: configSection.getAllMap().entrySet())
            defaultItemList.add(new Tuple<>(
                    Integer.parseInt(entry.getKey().split("-")[0]),
                    Integer.parseInt(entry.getKey().split("-")[1]),
                    (int) entry.getValue())
            );
        return defaultItemList;
    }

    public Pair<List<Integer>, List<List<Pair<Double, Integer>>>> parseBlockGenData() {
        ConfigSection configSection = this.pluginConfig.getSection("blockgen");
        List<Integer> blockGenSource = new ArrayList<>();
        List<List<Pair<Double, Integer>>> blockGenDict = new ArrayList<>();
        for (int i = 0; i < configSection.getInt("levels"); i++) {
            ConfigSection dist = configSection.getSection("l" + (i + 1));
            blockGenSource.add(Block.get(dist.getInt("id"), dist.getInt("meta")).getFullId());
            ConfigSection blocks = dist.getSection("blocks");
            List<Pair<Double, Integer>> lBlockGenDict = new ArrayList<>();
            blockGenDict.add(lBlockGenDict);
            double acc = 0;
            for (Map.Entry<String, Object> entry: blocks.getAllMap().entrySet()) {
                acc = acc + (Double) entry.getValue();
                lBlockGenDict.add(new Pair<>(
                        acc, Block.get(Integer.parseInt(entry.getKey().split("-")[0]),
                        Integer.parseInt(entry.getKey().split("-")[1])).getFullId())
                );
            }
        }
        return new Pair<>(blockGenSource, blockGenDict);
    }

    public String[] parseGuideBookPages() {
        return this.pluginConfig.getStringList("guidebook").toArray(new String[0]);
    }

}
