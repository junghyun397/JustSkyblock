package do1phin.mine2021;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
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
import do1phin.mine2021.ui.command.management.BanCommand;
import do1phin.mine2021.ui.command.management.GroupCommand;
import do1phin.mine2021.ui.command.management.KickCommand;
import do1phin.mine2021.ui.command.management.UnBanCommand;
import do1phin.mine2021.ui.command.skyblock.*;
import do1phin.mine2021.utils.CalendarHelper;
import do1phin.mine2021.utils.EmptyGenerator;

import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
public class ServerAgent extends PluginBase {

    private DatabaseAgent databaseAgent;
    private PlayerGroupAgent playerGroupAgent;
    private MessageAgent messageAgent;
    private SkyBlockAgent skyBlockAgent;
    private BlockGenAgent blockGenAgent;

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    private Level mainLevel;

    private final List<UUID> pendingRegisterPlayerList = new ArrayList<>();

    private Config.SystemConfig systemConfig;

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
        this.skyBlockAgent = new SkyBlockAgent(this, this.databaseAgent, this.messageAgent, config);
        this.blockGenAgent = new BlockGenAgent(this, this.messageAgent, config);
        this.playerGroupAgent = new PlayerGroupAgent(this, this.databaseAgent, config);

        this.getServer().getPluginManager().registerEvents(new ServerEventListener(this, config.getSkyblockConfig().getString("skyblock.main-level")), this);
        this.getServer().getPluginManager().registerEvents(new SkyBlockEventListener(this.skyBlockAgent), this);
        this.getServer().getPluginManager().registerEvents(new BlockGenEventListener(this.blockGenAgent), this);
        this.getServer().getPluginManager().registerEvents(new PlayerGroupEventListener(this.playerGroupAgent), this);

        this.getServer().getCommandMap().register("mine2021", new TeleportCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new InviteCommand(this, this.messageAgent, config, this.skyBlockAgent));
        this.getServer().getCommandMap().register("mine2021", new InviteListCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new PurgeCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new ProtectionTypeCommand(this, this.messageAgent, config, this.skyBlockAgent));

        this.getServer().getCommandMap().register("mine2021", new BanCommand(this, this.messageAgent, config, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new UnBanCommand(this, this.messageAgent, config, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new KickCommand(this, this.messageAgent, config));
        this.getServer().getCommandMap().register("mine2021", new GroupCommand(this, this.messageAgent, config, this.playerGroupAgent));

        config.parseAdditionalRecipes().forEach(recipe -> getServer().getCraftingManager().registerRecipe(recipe));
        this.getServer().getCraftingManager().rebuildPacket();

        this.loggerInfo("§eloading succeed.");
    }

    public void registerPlayer(Player player) {
        final UUID uuid = player.getUniqueId();
        final String name = player.getName();
        final String ip = player.getAddress();

        Optional<PlayerData> playerData = this.databaseAgent.getPlayerData(player, uuid);
        if (playerData.isPresent()) {
            if (!playerData.get().getName().equals(name) | !playerData.get().getIp().equals(ip))
                this.databaseAgent.updatePlayerData(playerData.get());

            if (playerData.get().getBanDate() != null
                    && !this.processBannedPlayer(playerData.get())) return;

            this.messageAgent.sendBroadcast("message.general.on-player-join",
                    new String[]{"%player"}, new String[]{player.getName()});
        } else {
            int section = this.databaseAgent.getNextSection();
            playerData = Optional.of(new PlayerData(player, uuid, name, ip, 0, SkyblockData.getDefault(section, uuid, name), null, null));
            this.registerNewPlayer(playerData.get());
        }

        this.playerDataMap.put(uuid, playerData.get());
        this.skyBlockAgent.registerSkyblockData(playerData.get().getSkyblockData());

        this.playerGroupAgent.setPlayerNameTag(playerData.get());
        if (this.systemConfig.disableDefaultCommands && !player.isOp()) this.removeDefaultCommandPermission(player);
    }

    public PlayerData getPlayerData(Player player) {
        return this.playerDataMap.get(player.getUniqueId());
    }

    public Optional<PlayerData> getPlayerData(UUID uuid) {
        final PlayerData playerData = this.playerDataMap.get(uuid);
        if (playerData != null) return Optional.of(playerData);
        else return Optional.empty();
    }

    public void purgePlayer(Player player) {
        if (!this.getPlayerData(player.getUniqueId()).isPresent()) return;

        this.messageAgent.sendBroadcast("message.general.on-player-quit",
                new String[]{"%player"}, new String[]{player.getName()});
        this.messageAgent.sendBroadcastPopup("popup.general.on-player-quit",
                new String[]{"%player"}, new String[]{player.getName()});

        this.playerDataMap.remove(player.getUniqueId());
    }

    private void registerNewPlayer(PlayerData playerData) {
        this.databaseAgent.registerPlayerData(playerData);
        this.skyBlockAgent.registerNewSkyblock(playerData, this.systemConfig.enableTeleportToIsland);

        if (this.systemConfig.enableDefaultItems) this.giveDefaultItems(playerData.getPlayer());
        this.addPendingRegisterNewPlayer(playerData.getUuid());

        this.messageAgent.sendBroadcast("message.general.on-player-first-join",
                new String[]{"%player"}, new String[]{playerData.getName()});
    }

    private void addPendingRegisterNewPlayer(UUID uuid) {
        this.pendingRegisterPlayerList.add(uuid);
    }

    boolean isPendingRegisterNewPlayer(UUID uuid) {
        return this.pendingRegisterPlayerList.contains(uuid);
    }

    void continueRegisterNewPlayer(Player player) {
        this.pendingRegisterPlayerList.remove(player.getUniqueId());

        if (this.systemConfig.enableWelcomeForm)
            this.messageAgent.sendSimpleForm(player, "form.welcome-form.title", "form.welcome-form.content");
    }

    private boolean processBannedPlayer(PlayerData playerData) {
        if (playerData.getBanDate().getTime() < System.currentTimeMillis()) return true;

        final String[] ymdhm = CalendarHelper.getYMDHMFromTimestamp(playerData.getBanDate());
        playerData.getPlayer().kick(this.messageAgent.getMessage("message.management.player-banned",
                new String[]{"%player", "%year", "%month", "%day", "%hour", "%minute", "%reason"},
                new String[]{playerData.getName(), ymdhm[0], ymdhm[1], ymdhm[2], ymdhm[3], ymdhm[4], playerData.getBanReason()}
        ), false);

        return false;
    }

    private void giveDefaultItems(Player player) {
        this.systemConfig.defaultItemCollection.forEach(item ->
                player.getInventory().addItem(Item.get(item.a, item.b, item.c).clone()));

        if (this.systemConfig.enableGuideBook) {
            final ItemBookWritten book = (ItemBookWritten) Item.get(387, 0, 1);
            final String bookName = this.messageAgent.getText("general.guidebook") + " v" + this.systemConfig.guideBookVersion;
            book.writeBook(this.systemConfig.guideBookAuthor, bookName, Arrays.stream(this.systemConfig.guideBookPages).map(s ->
                    s.replaceAll("%player", player.getName())).toArray(String[]::new));
            book.setCustomName(bookName);

            player.getInventory().addItem(book);
        }
    }

    private void removeDefaultCommandPermission(Player player) {
        player.addAttachment(this, "nukkit.command", false);
        player.recalculatePermissions();
    }

    public boolean isEnableInventorySave() {
        return this.systemConfig.enableInventorySave;
    }

    void setMainLevel(Level level) {
        this.mainLevel = level;
    }

    public Level getMainLevel() {
        return this.mainLevel;
    }

}
