package do1phin.mine2021.data;

import cn.nukkit.utils.ConfigSection;
import do1phin.mine2021.utils.NukkitUtility;
import do1phin.mine2021.utils.Pair;
import do1phin.mine2021.utils.Tuple;

import java.util.ArrayList;
import java.util.List;

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
        final List<?> dim1 = this.pluginConfig.getList("skyblock.default-island-shape");
        final List<List<List<Integer>>> dist1 = new ArrayList<>();
        final int shapeY = dim1.size();
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

        final int[][][] islandShape = new int[shapeY][shapeZ][shapeX];
        for (int y = 0; y < shapeY; y++)
            for (int z = 0; z < shapeZ; z++)
                for (int x = 0; x < shapeX; x++)
                    islandShape[y][z][x] = dist1.get(y).get(z).get(x);

        return islandShape;
    }

    public List<Tuple<Integer, Integer, Integer>> parseDefaultItemList() {
        final List<?> defaultItemSection = this.pluginConfig.getList("default-item-list");

        final List<Tuple<Integer, Integer, Integer>> defaultItemList = new ArrayList<>();
        defaultItemSection.forEach(o -> {
            final ConfigSection section = (ConfigSection) o;
            defaultItemList.add(new Tuple<>(section.getInt("id"), section.getInt("meta"), section.getInt("count")));
        });

        return defaultItemList;
    }

    public Tuple<List<Integer>, List<Integer>, List<List<Pair<Double, Integer>>>> parseBlockGenData() {
        final List<?> blockGenSection = this.pluginConfig.getList("blockgen");

        final List<Integer> blockGenSource = new ArrayList<>();
        final List<Integer> blockGenDelay = new ArrayList<>();
        final List<List<Pair<Double, Integer>>> blockGenDict = new ArrayList<>();
        blockGenSection.forEach(o -> {
            ConfigSection section = (ConfigSection) o;
            blockGenSource.add(NukkitUtility.getFullID(section.getInt("id"), section.getInt("meta")));
            blockGenDelay.add(section.getInt("delay"));

            final List<Pair<Double, Integer>> blockGenCell = new ArrayList<>();
            final List<?> blocksList = section.getList("blocks");
            final double[] total = {0};
            final double[] acc = {0};
            blocksList.forEach(o1 -> total[0] = total[0] + ((ConfigSection) o1).getDouble("percentage"));
            blocksList.forEach(o1 -> {
                ConfigSection blockSection  = (ConfigSection) o1;
                acc[0] = acc[0] + blockSection.getDouble("percentage") / total[0];
                blockGenCell.add(new Pair<>(
                        acc[0],
                        NukkitUtility.getFullID(blockSection.getInt("id"), blockSection.getInt("meta"))
                ));
            });
            blockGenDict.add(blockGenCell);
        });

        return new Tuple<>(blockGenSource, blockGenDelay, blockGenDict);
    }

    public String[] parseGuideBookPages() {
        return this.pluginConfig.getStringList("guidebook").toArray(new String[0]);
    }

}
