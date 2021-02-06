package do1phin.mine2021;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import do1phin.mine2021.blockgen.BlockGenAgent;
import do1phin.mine2021.blockgen.BlockGenEventListener;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.PlayerGroupAgent;
import do1phin.mine2021.data.PlayerGroupEventListener;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.data.db.DatabaseHelper;
import do1phin.mine2021.data.db.MysqlDatabaseHelper;
import do1phin.mine2021.data.db.SqliteDatabaseHelper;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.skyblock.SkyBlockEventListener;
import do1phin.mine2021.skyblock.data.SkyblockData;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.ui.UXAgent;
import do1phin.mine2021.ui.UXEventListener;
import do1phin.mine2021.ui.command.management.BanCommand;
import do1phin.mine2021.ui.command.management.GroupCommand;
import do1phin.mine2021.ui.command.management.KickCommand;
import do1phin.mine2021.ui.command.management.UnBanCommand;
import do1phin.mine2021.ui.command.skyblock.*;
import do1phin.mine2021.utils.EmptyGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("FieldCanBeLocal")
public class ServerAgent extends PluginBase {

    private static ServerAgent instance;
    public static ServerAgent getInstance() {
        return instance;
    }

    private DatabaseAgent databaseAgent;
    private MessageAgent messageAgent;
    private UXAgent uxAgent;
    private SkyBlockAgent skyBlockAgent;
    private BlockGenAgent blockGenAgent;
    private PlayerGroupAgent playerGroupAgent;

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    private Config.SystemConfig systemConfig;

    private Level mainLevel;

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
        Generator.addGenerator(EmptyGenerator.class, "EMPTY", Generator.TYPE_INFINITE);
    }

    @Override
    public void onEnable() {
        this.loggerInfo("§estart loading...");

        instance = this;

        final Config config = new Config(this);
        this.systemConfig = config.parseSystemConfig();

        this.loggerInfo("loading rdbms...");

        final DatabaseHelper databaseHelper;
        if (config.getDatabaseConfig().getString("database.type").equalsIgnoreCase("mysql"))
            databaseHelper = new MysqlDatabaseHelper(this, config);
        else
            databaseHelper = new SqliteDatabaseHelper(this, config);

        if (!databaseHelper.connect()) {
            this.loggerCritical("loading rdbms failed.");
            this.getServer().shutdown();
            return;
        }

        databaseHelper.initDatabase();

        this.loggerInfo("rdbms connected.");

        this.databaseAgent = new DatabaseAgent(this, databaseHelper);
        this.messageAgent = new MessageAgent(this, config);
        this.uxAgent = new UXAgent(this, this.messageAgent, config);
        this.skyBlockAgent = new SkyBlockAgent(this, this.databaseAgent, this.messageAgent, config);
        this.blockGenAgent = new BlockGenAgent(this, this.messageAgent, config);
        this.playerGroupAgent = new PlayerGroupAgent(this, this.databaseAgent, config);

        this.getServer().getPluginManager().registerEvents(new ServerEventListener(this, config.getSkyblockConfig().getString("skyblock.main-level")), this);
        this.getServer().getPluginManager().registerEvents(new UXEventListener(this.uxAgent), this);
        this.getServer().getPluginManager().registerEvents(new SkyBlockEventListener(this.skyBlockAgent), this);
        this.getServer().getPluginManager().registerEvents(new BlockGenEventListener(this.blockGenAgent), this);
        this.getServer().getPluginManager().registerEvents(new PlayerGroupEventListener(this.playerGroupAgent), this);

        this.getServer().getCommandMap().register("mine2021", new TeleportCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new InviteCommand(this, this.messageAgent, config, this.skyBlockAgent));
        this.getServer().getCommandMap().register("mine2021", new InviteListCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new PurgeCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new ProtectionTypeCommand(this, this.messageAgent, config, this.skyBlockAgent));
        this.getServer().getCommandMap().register("mine2021", new LockTypeCommand(this, this.messageAgent, config, this.skyBlockAgent));

        this.getServer().getCommandMap().register("mine2021", new BanCommand(this, this.messageAgent, config, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new UnBanCommand(this, this.messageAgent, config, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new KickCommand(this, this.messageAgent, config));
        this.getServer().getCommandMap().register("mine2021", new GroupCommand(this, this.messageAgent, config, this.playerGroupAgent));

        this.getServer().getOnlinePlayers().forEach((key, value) -> this.registerPlayer(value));

        this.loggerInfo("§eloading succeed.");
    }

    @Override
    public void onDisable() {
        this.databaseAgent.disconnect();
    }

    void registerPlayer(Player player) {
        final UUID uuid = player.getUniqueId();
        final String name = player.getName();
        final String ip = player.getAddress();

        final PlayerData playerData;
        if (this.databaseAgent.checkPlayerData(uuid)) {
            playerData = this.databaseAgent.getPlayerData(player, uuid);
            if (!playerData.getName().equals(name) || !playerData.getIp().equals(ip))
                this.databaseAgent.updatePlayerData(playerData);

            this.uxAgent.resolvePlayerJoin(playerData);
        } else {
            final int section = this.databaseAgent.getNextSection();
            playerData = new PlayerData(player, uuid, name, ip, 0, SkyblockData.getDefault(section, uuid, name), null, null);
            this.registerNewPlayer(playerData);

            this.uxAgent.resolvePlayerFirstJoin(player);
        }

        this.playerDataMap.put(uuid, playerData);
        this.skyBlockAgent.registerSkyblockData(playerData.getSkyblockData());
        this.playerGroupAgent.setPlayerNameTag(playerData);

        this.uxAgent.resolvePermissions(player);
    }

    public PlayerData getPlayerData(Player player) {
        return this.playerDataMap.get(player.getUniqueId());
    }

    public Optional<PlayerData> getPlayerData(UUID uuid) {
        return Optional.ofNullable(this.playerDataMap.get(uuid));
    }

    void purgePlayer(Player player) {
        this.getPlayerData(player.getUniqueId()).ifPresent(playerData -> {
            this.playerDataMap.remove(playerData.getUuid());
            this.uxAgent.resolvePlayerQuit(player);
        });
    }

    private void registerNewPlayer(PlayerData playerData) {
        this.databaseAgent.registerPlayerData(playerData);
        this.skyBlockAgent.registerNewSkyblock(playerData, this.systemConfig.enableTeleportToIsland);
    }

    public Level getMainLevel() {
        return this.mainLevel;
    }

    public SkyBlockAgent getSkyBlockAgent() {
        return this.skyBlockAgent;
    }

    public BlockGenAgent getBlockGenAgent() {
        return this.blockGenAgent;
    }

    public PlayerGroupAgent getPlayerGroupAgent() {
        return this.playerGroupAgent;
    }

    void setMainLevel(Level level) {
        this.mainLevel = level;
    }

}
