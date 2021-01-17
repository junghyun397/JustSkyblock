package do1phin.mine2021.data;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.utils.ConfigSection;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.utils.Pair;
import do1phin.mine2021.utils.Tuple;

import java.util.*;

public class Config {

    private final cn.nukkit.utils.Config serverConfig;
    private final cn.nukkit.utils.Config databaseConfig;
    private final cn.nukkit.utils.Config skyblockConfig;
    private final cn.nukkit.utils.Config blockGenConfig;
    private final cn.nukkit.utils.Config userInterfaceConfig;
    private final cn.nukkit.utils.Config userGroupsConfig;

    public Config(ServerAgent serverAgent) {
        this.serverConfig = this.initConfigFile(serverAgent, "server-config.yml");
        this.databaseConfig = this.initConfigFile(serverAgent, "database-config.yml");
        this.skyblockConfig = this.initConfigFile(serverAgent, "skyblock-config.yml");
        this.blockGenConfig = this.initConfigFile(serverAgent, "blockgen-config.yml");
        this.userInterfaceConfig = this.initConfigFile(serverAgent, "user-interface-config.yml");
        this.userGroupsConfig = this.initConfigFile(serverAgent, "user-groups.yml");
    }

    private cn.nukkit.utils.Config initConfigFile(ServerAgent serverAgent, String fileName) {
        serverAgent.saveResource(fileName);
        return new cn.nukkit.utils.Config(serverAgent.getDataFolder() + "/" + fileName);
    }

    public cn.nukkit.utils.Config getServerConfig() {
        return this.serverConfig;
    }

    public cn.nukkit.utils.Config getDatabaseConfig() {
        return this.databaseConfig;
    }

    public cn.nukkit.utils.Config getSkyblockConfig() {
        return this.skyblockConfig;
    }

    public cn.nukkit.utils.Config getBlockGenConfig() {
        return this.blockGenConfig;
    }

    public cn.nukkit.utils.Config getUserInterfaceConfig() {
        return this.userInterfaceConfig;
    }

    public cn.nukkit.utils.Config getUserGroupsConfig() {
        return this.userGroupsConfig;
    }

    // SERVER CONFIG

    public Collection<Recipe> parseAdditionalRecipes() {
        return new ArrayList<>();
    }

    public Collection<Tuple<Integer, Integer, Integer>> parseDefaultItemCollection() {
        final List<?> defaultItemSection = this.serverConfig.getList("default-item-list");

        final Collection<Tuple<Integer, Integer, Integer>> defaultItemList = new ArrayList<>();
        defaultItemSection.forEach(o -> {
            final ConfigSection section = (ConfigSection) o;
            defaultItemList.add(new Tuple<>(section.getInt("id"), section.getInt("meta"), section.getInt("count")));
        });

        return defaultItemList;
    }

    // DATABASE CONFIG

    // SKYBLOCK CONFIG

    public int[][][] parseSkyblockDefaultIslandShape() {
        final List<?> dim1 = this.skyblockConfig.getList("skyblock.default-island-shape");
        final List<List<List<Integer>>> dist1 = new ArrayList<>();
        final int shapeY = dim1.size();
        int shapeX = 0;
        int shapeZ = 0;
        for (Object dim2 : dim1) {
            List<List<Integer>> dist2 = new ArrayList<>();
            for (Object dim3 : (List<?>) dim2) {
                List<Integer> dist3 = new ArrayList<>();
                for (Object block : (List<?>) dim3)
                    dist3.add((int) block);
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

    // BLOCKGEN CONFIG

    public Tuple<List<Integer>, List<Integer>, List<List<Pair<Double, Block>>>> parseBlockGenData() {
        final List<?> blockGenSection = this.blockGenConfig.getList("dictionary");

        final List<Integer> blockGenSource = new ArrayList<>();
        final List<Integer> blockGenDelay = new ArrayList<>();
        final List<List<Pair<Double, Block>>> blockGenDict = new ArrayList<>();
        blockGenSection.forEach(o -> {
            ConfigSection section = (ConfigSection) o;
            blockGenSource.add(section.getInt("id"));
            blockGenDelay.add(section.getInt("delay"));

            final List<Pair<Double, Block>> blockGenCell = new ArrayList<>();
            final List<?> blocksList = section.getList("blocks");
            final double[] total = {0};
            final double[] acc = {0};
            blocksList.forEach(o1 -> total[0] = total[0] + ((ConfigSection) o1).getDouble("percentage"));
            blocksList.forEach(o1 -> {
                ConfigSection blockSection  = (ConfigSection) o1;
                acc[0] = acc[0] + blockSection.getDouble("percentage") / total[0];
                blockGenCell.add(new Pair<>(
                        acc[0],
                        BlockState.of(blockSection.getInt("id"), blockSection.getInt("meta")).getBlock()
                ));
            });
            blockGenDict.add(blockGenCell);
        });

        return new Tuple<>(blockGenSource, blockGenDelay, blockGenDict);
    }

    // USER INTERFACE CONFIG

    public String getUIString(String key) {
        return this.userInterfaceConfig.getString(key);
    }

    public String[] parseGuideBookPages() {
        return this.userInterfaceConfig.getStringList("guidebook.content").toArray(new String[0]);
    }

    // USER GROUPS CONFIG

    public Map<Integer, Tuple<String, String, String>> parsePlayerGroupMap() {
        final Map<Integer, Tuple<String, String, String>> categoryMap = new HashMap<>();
        this.userGroupsConfig.getSection("groups").forEach((s, o) -> {
            ConfigSection section = ((ConfigSection) o);
            categoryMap.put(section.getInt("id", 0),
                    new Tuple<>(s, section.getString("display-name") + " ", section.getString("nametag")));
        });
        return categoryMap;
    }

}
