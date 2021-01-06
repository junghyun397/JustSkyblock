package do1phin.mine2021;

import cn.nukkit.Player;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import do1phin.mine2021.data.PlayerCategory;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.data.db.MysqlHelper;
import do1phin.mine2021.data.db.RDBSHelper;
import do1phin.mine2021.data.db.Sqlite3Helper;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.skyblock.data.SkyblockData;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.ui.command.skyblock.TeleportCommand;
import do1phin.mine2021.ui.command.management.BanCommand;
import do1phin.mine2021.ui.command.management.KickCommand;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.utils.EmptyGenerator;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.utils.Pair;

import java.util.HashMap;

public class ServerAgent extends PluginBase {

    private static ServerAgent instance;
    public static ServerAgent getInstance() {
        return instance;
    }

    private DatabaseAgent databaseAgent;
    private MessageAgent messageAgent;
    private SkyBlockAgent skyBlockAgent;

    private final HashMap<String, PlayerData> playerDataMap = new HashMap<>();

    public void loggerInfo(String message) {
        this.getLogger().info(message);
    }

    public void loggerAlert(String message) {
        this.getLogger().alert(message);
    }

    public void loggerWarning(String message) {
        this.getLogger().warning(message);
    }

    public void loggerCritical(String message) {
        this.getLogger().critical(message);
    }

    @Override
    public void onLoad() {
        instance = this;
        Generator.addGenerator(EmptyGenerator.class, "EMPTY", Generator.TYPE_INFINITE);
    }

    @Override
    public void onEnable() {
        this.loggerInfo("§estart loading...");
        this.getServer().getPluginManager().registerEvents(new ServerEventListener(this), this);

        this.loggerInfo("start loading config...");

        Config config = new Config(this.getConfig());

        this.loggerInfo("succeed loading config.");

        this.loggerInfo("start rdbms connection...");

        RDBSHelper rdbsHelper;

        if (config.databaseType.equals("Mysql")) rdbsHelper = new MysqlHelper(config);
        else rdbsHelper = new Sqlite3Helper(config);

        if (rdbsHelper.connect()) this.loggerInfo("mysql-db connected.");
        else {
            this.loggerCritical(" connect failed.");
            this.getServer().shutdown();
        }
        this.loggerInfo("rdbms connected.");

        this.databaseAgent = new DatabaseAgent(this, rdbsHelper);
        this.messageAgent = new MessageAgent(this);
        this.skyBlockAgent = new SkyBlockAgent(this, this.databaseAgent, this.messageAgent, config);

        this.getServer().getCommandMap().register("", new TeleportCommand(this, this.skyBlockAgent));

        this.getServer().getCommandMap().register("", new BanCommand(this));
        this.getServer().getCommandMap().register("", new KickCommand(this));

        this.loggerInfo("§esucceed loading.");
    }

    public PlayerData registerPlayerData(Player player) {
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String ip = player.getAddress();

        PlayerData playerData = this.databaseAgent.getPlayerData(uuid);
        if (playerData == null) {
            playerData = new PlayerData(player, uuid, name, ip, PlayerCategory.USER,
                    this.databaseAgent.getCurrentSection()+1, SkyblockData.getDefault());
            this.registerNewPlayer(playerData);
        } else {
            if (!playerData.getName().equals(name) | !playerData.getIp().equals(ip))
                this.databaseAgent.updatePlayerData(playerData);
        }

        this.playerDataMap.put(uuid, playerData);
        return playerData;
    }

    public PlayerData getPlayerData(String uuid) {
        return this.playerDataMap.get(uuid);
    }

    public void purgePlayerData(String uuid) {
        this.playerDataMap.remove(uuid);
    }

    private void registerNewPlayer(PlayerData playerData) {
        this.databaseAgent.registerPlayerData(playerData);
        this.skyBlockAgent.generateNewIsland(playerData);

        Pair<Double, Double> islandSpawnPosition = this.skyBlockAgent.getIslandSpawnPosition(playerData.getSection());
        Vector3 playerSpawn = new Vector3(islandSpawnPosition.a, 65.0, islandSpawnPosition.b);
        playerData.getPlayer().setSpawn(playerSpawn);
        playerData.getPlayer().teleport(playerSpawn);

    }

}
