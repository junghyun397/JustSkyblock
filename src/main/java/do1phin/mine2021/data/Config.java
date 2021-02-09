package do1phin.mine2021.data;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.utils.ConfigSection;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.utils.Pair;
import do1phin.mine2021.utils.Tuple;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Config {

    private final cn.nukkit.utils.Config serverConfig;
    private final cn.nukkit.utils.Config databaseConfig;
    private final cn.nukkit.utils.Config skyblockConfig;
    private final cn.nukkit.utils.Config blockGenConfig;
    private final cn.nukkit.utils.Config userInterfaceConfig;
    private final cn.nukkit.utils.Config userGroupsConfig;

    private final File dataFolder;

    private final static String defaultLanguage = "eng";

    public Config(ServerAgent serverAgent) {
        this.dataFolder = serverAgent.getDataFolder();

        this.serverConfig = this.initConfigFile(serverAgent, "server-config.yml");
        this.databaseConfig = this.initConfigFile(serverAgent, "database-config.yml");
        this.skyblockConfig = this.initConfigFile(serverAgent, "skyblock-config.yml");
        this.blockGenConfig = this.initConfigFile(serverAgent, "blockgen-config.yml");
        this.userGroupsConfig = this.initConfigFile(serverAgent, "user-groups.yml");

        String language = serverAgent.getServer().getLanguage().getLang();
        if (serverAgent.getResource("user-interface-config-" + language + ".yml") == null)
            language = defaultLanguage;

        this.userInterfaceConfig = this.initConfigFile(serverAgent, "user-interface-config-" + language + ".yml");
    }

    private cn.nukkit.utils.Config initConfigFile(ServerAgent serverAgent, String fileName) {
        serverAgent.saveResource(fileName);
        return new cn.nukkit.utils.Config(new File(this.dataFolder + "/" + fileName));
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

    public File getDataFolder() {
        return this.dataFolder;
    }

    // SYSTEM CONFIG

    public SystemConfig parseSystemConfig() {
        return new SystemConfig(this.serverConfig.getBoolean("system.rule.disable-nukkit-commands"),
                this.serverConfig.getBoolean("system.rule.enable-inventory-save"),

                this.parseDefaultItems(),

                this.userInterfaceConfig.getInt("guidebook.version"),
                this.userInterfaceConfig.getString("guidebook.author"),
                this.parseGuideBookPages(),

                this.serverConfig.getBoolean("system.ux.enable-teleport-to-island"),
                this.serverConfig.getBoolean("system.ux.enable-welcome-form"),
                this.serverConfig.getBoolean("system.ux.enable-default-items"),
                this.serverConfig.getBoolean("system.ux.enable-guidebook"));
    }

    // SERVER CONFIG

    public Collection<ShapedRecipe> parseAdditionalRecipes() {
        return ((List<?>) this.serverConfig.getList("crafting.additional-recipes")).stream().map(o -> {
            final ConfigSection configSection = (ConfigSection) o;
            return new ShapedRecipe(
                    configSection.getString("name"), 1,
                    Item.get(configSection.getInt("id"), configSection.getInt("meta"), configSection.getInt("amount")),
                    configSection.getString("shape").split(","),
                    Arrays.stream(configSection.getString("ingredients").split(","))
                            .map(s -> s.split("="))
                            .collect(Collectors.toMap(e -> e[0].toCharArray()[0], e -> {
                                final String[] sliced = e[1].split(":");
                                return Item.get(Integer.parseInt(sliced[0]), Integer.parseInt(sliced[1]));
                            })),
                    new ArrayList<>()
            );
        }).collect(Collectors.toList());
    }

    public Collection<Item> parseBannedRecipes() {
        return ((List<?>) this.serverConfig.getList("crafting.banned-recipes")).stream().map(o -> {
            final ConfigSection configSection = (ConfigSection) o;
            return Item.get(configSection.getInt("id"), configSection.getInt("meta"), configSection.getInt("amount"));
        }).collect(Collectors.toList());
    }

    public Collection<Tuple<Integer, Integer, Integer>> parseDefaultItems() {
        return ((List<?>) this.serverConfig.getList("default-items")).stream().map(o -> {
            final ConfigSection configSection = (ConfigSection) o;
            return new Tuple<>(configSection.getInt("id"), configSection.getInt("meta"), configSection.getInt("amount"));
        }).collect(Collectors.toList());
    }

    // USER GROUPS CONFIG

    public Map<Integer, Tuple<String, String, String>> parsePlayerGroupMap() {
        return this.userGroupsConfig.getSection("groups").getAllMap().entrySet().stream().map(stringObjectEntry -> {
            ConfigSection section = ((ConfigSection) stringObjectEntry.getValue());
            return new Pair<>(
                    section.getInt("id", 0),
                    new Tuple<>(stringObjectEntry.getKey(), section.getString("display-name") + " ", section.getString("nametag"))
            );
        }).collect(Collectors.toMap(e -> e.a, e -> e.b));
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
        final List<Integer> blockGenSources = new ArrayList<>();
        final List<Integer> blockGenDelay = new ArrayList<>();
        final List<List<Tuple<Double, Double, Block>>> rawBlockGenDictionary = new ArrayList<>();

        ((List<?>) this.blockGenConfig.getList("dictionary")).forEach(o -> {
            final ConfigSection blocksSection = (ConfigSection) o;
            final List<Tuple<Double, Double, Block>> rawBlocks = new ArrayList<>();
            final List<?> blocksList = blocksSection.getList("blocks");

            blocksList.forEach(o1 -> {
                final ConfigSection blockSection = (ConfigSection) o1;
                rawBlocks.add(new Tuple<>(
                        blockSection.getDouble("percentage"),
                        blockSection.getDouble("factor"),
                        BlockState.of(blockSection.getInt("id"), blockSection.getInt("meta")).getBlock()
                ));
            });

            blockGenSources.add(blocksSection.getInt("id"));
            blockGenDelay.add(blocksSection.getInt("delay"));
            rawBlockGenDictionary.add(rawBlocks);
        });

        final List<List<Pair<Double, Block>>> blockGenDictionary = new ArrayList<>();

        for (int i = 0; i < rawBlockGenDictionary.size(); i++) {
            final List<Pair<Double, Block>> blocks = new ArrayList<>();

            final List<Pair<Double, Block>> reducedList = new ArrayList<>();
            for (int j = 0; j < i + 1; j++) {
                final int finalJ = i - j;
                reducedList.addAll(rawBlockGenDictionary.get(j).stream().map(o ->
                        new Pair<>(o.a * Math.pow(o.b, finalJ), o.c)).collect(Collectors.toList()));
            }

            final double[] total = {0};
            final double[] acc = {0};

            reducedList.forEach(o -> total[0] = total[0] + o.a);
            reducedList.forEach(o -> {
                acc[0] = acc[0] + o.a / total[0];
                blocks.add(new Pair<>(acc[0], o.b));
            });

            blockGenDictionary.add(blocks);
        }

        return new Tuple<>(blockGenSources, blockGenDelay, blockGenDictionary);
    }

    // USER INTERFACE CONFIG

    public String getUIString(String key) {
        return this.userInterfaceConfig.getString(key);
    }

    public String[] parseGuideBookPages() {
        return this.userInterfaceConfig.getStringList("guidebook.content").toArray(new String[0]);
    }

    public static class SystemConfig {
        public final boolean disableDefaultCommands;
        public final boolean enableInventorySave;

        public final Collection<Tuple<Integer, Integer, Integer>> defaultItemCollection;

        public final int guideBookVersion;
        public final String guideBookAuthor;
        public final String[] guideBookPages;

        public final boolean enableTeleportToIsland;
        public final boolean enableWelcomeForm;
        public final boolean enableDefaultItems;
        public final boolean enableGuideBook;

        public SystemConfig(boolean disableDefaultCommands,
                            boolean enableInventorySave,

                            Collection<Tuple<Integer, Integer, Integer>> defaultItemCollection,

                            int guideBookVersion,
                            String guideBookAuthor,
                            String[] guideBookPages,

                            boolean enableTeleportToIsland,
                            boolean enableWelcomeForm,
                            boolean enableDefaultItems,
                            boolean enableGuideBook) {
            this.disableDefaultCommands = disableDefaultCommands;
            this.enableInventorySave = enableInventorySave;

            this.defaultItemCollection = defaultItemCollection;

            this.guideBookVersion = guideBookVersion;
            this.guideBookAuthor = guideBookAuthor;
            this.guideBookPages = guideBookPages;

            this.enableTeleportToIsland = enableTeleportToIsland;
            this.enableWelcomeForm = enableWelcomeForm;
            this.enableDefaultItems = enableDefaultItems;
            this.enableGuideBook = enableGuideBook;
        }
    }

}
