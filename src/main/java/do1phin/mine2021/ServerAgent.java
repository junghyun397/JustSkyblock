package do1phin.mine2021;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import do1phin.mine2021.blockgen.BlockGenEventListener;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerCategoryAgent;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.data.db.MysqlHelper;
import do1phin.mine2021.data.db.RDBSHelper;
import do1phin.mine2021.data.db.Sqlite3Helper;
import do1phin.mine2021.blockgen.BlockGenAgent;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.skyblock.SkyBlockEventListener;
import do1phin.mine2021.skyblock.data.SkyblockData;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.ui.command.management.BanCommand;
import do1phin.mine2021.ui.command.management.CategoryCommand;
import do1phin.mine2021.ui.command.management.KickCommand;
import do1phin.mine2021.ui.command.skyblock.*;
import do1phin.mine2021.utils.EmptyGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ServerAgent extends PluginBase {

    private static ServerAgent instance;
    public static ServerAgent getInstance() {
        return instance;
    }

    private DatabaseAgent databaseAgent;
    private PlayerCategoryAgent playerCategoryAgent;
    private MessageAgent messageAgent;
    private SkyBlockAgent skyBlockAgent;
    private BlockGenAgent blockGenAgent;

    private final Map<String, PlayerData> playerDataMap = new HashMap<>();

    private Level mainLevel = null;

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
        this.loggerInfo("loading config...");

        this.saveDefaultConfig();
        Config config = new Config(this.getConfig());

        this.loggerInfo("loading rdbms...");

        RDBSHelper rdbsHelper;

        if (config.databaseType.equals("MYSQL")) rdbsHelper = new MysqlHelper(config);
        else rdbsHelper = new Sqlite3Helper(config);

        if (!rdbsHelper.connect()) {
            this.loggerCritical("loading rdbms failed.");
            this.getServer().shutdown();
            return;
        }

        this.loggerInfo("rdbms connected.");

        this.playerCategoryAgent = new PlayerCategoryAgent(config);
        this.databaseAgent = new DatabaseAgent(this, rdbsHelper);
        this.messageAgent = new MessageAgent(this, config);
        this.skyBlockAgent = new SkyBlockAgent(this, this.databaseAgent, this.messageAgent, config);
        this.blockGenAgent = new BlockGenAgent(this, this.messageAgent, config);

        this.getServer().getPluginManager().registerEvents(new ServerEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SkyBlockEventListener(this.skyBlockAgent), this);
        this.getServer().getPluginManager().registerEvents(new BlockGenEventListener(this.blockGenAgent), this);

        this.getServer().getCommandMap().register("mine2021", new TeleportCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new InviteCommand(this, this.messageAgent, config, this.skyBlockAgent));
        this.getServer().getCommandMap().register("mine2021", new InviteListCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new PurgeCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new ProtectionTypeCommand(this, this.messageAgent, config, this.skyBlockAgent));

        this.getServer().getCommandMap().register("mine2021", new BanCommand(this, this.messageAgent, config));
        this.getServer().getCommandMap().register("mine2021", new KickCommand(this, this.messageAgent, config));
        this.getServer().getCommandMap().register("mine2021", new CategoryCommand(this, this.messageAgent, config));

        this.loggerInfo("§eloading succeed.");
    }

    public void registerPlayerData(Player player) {
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String ip = player.getAddress();

        Optional<PlayerData> playerData = this.databaseAgent.getPlayerData(player, uuid);
        if (playerData.isPresent()) {
            if (!playerData.get().getName().equals(name) | !playerData.get().getIp().equals(ip))
                this.databaseAgent.updatePlayerData(playerData.get());
        } else {
            int section = this.databaseAgent.getCurrentSection()+1;
            playerData = Optional.of(new PlayerData(player, uuid, name, ip, 0, section, SkyblockData.getDefault(section), null));
            this.registerNewPlayer(playerData.get());
        }

        this.playerDataMap.put(uuid, playerData.get());

        this.skyBlockAgent.registerSkyblockData(playerData.get());
        this.playerCategoryAgent.setPlayerNameTag(playerData.get());
    }

    public PlayerData getPlayerData(Player player) {
        return this.playerDataMap.get(player.getUniqueId().toString());
    }

    public Optional<PlayerData> getPlayerData(String uuid) {
        PlayerData playerData = this.playerDataMap.get(uuid);
        if (playerData != null) return Optional.of(playerData);
        else return Optional.empty();
    }

    public void purgePlayerData(String uuid) {
        this.playerDataMap.remove(uuid);
    }

    private void registerNewPlayer(PlayerData playerData) {
        this.databaseAgent.registerPlayerData(playerData);

        this.messageAgent.sendPlayerFirstJoinBroadcast(playerData);
        this.messageAgent.sendPlayerFirstJoinMessage(playerData);

        this.skyBlockAgent.registerNewSkyblock(playerData);
    }

    public void setMainLevel(Level level) {
        this.mainLevel = level;
    }

    public Level getMainLevel() {
        return this.mainLevel;
    }

}
