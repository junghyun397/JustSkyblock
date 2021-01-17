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
import do1phin.mine2021.data.PlayerGroupAgent;
import do1phin.mine2021.data.PlayerGroupEventListener;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.data.db.MysqlHelper;
import do1phin.mine2021.data.db.RDBSHelper;
import do1phin.mine2021.data.db.Sqlite3Helper;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.skyblock.SkyBlockEventListener;
import do1phin.mine2021.skyblock.data.SkyblockData;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.ui.command.management.BanCommand;
import do1phin.mine2021.ui.command.management.GroupCommand;
import do1phin.mine2021.ui.command.management.KickCommand;
import do1phin.mine2021.ui.command.skyblock.*;
import do1phin.mine2021.utils.EmptyGenerator;
import do1phin.mine2021.utils.Tuple;

import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
public class ServerAgent extends PluginBase {

    private DatabaseAgent databaseAgent;
    private PlayerGroupAgent playerGroupAgent;
    private MessageAgent messageAgent;
    private SkyBlockAgent skyBlockAgent;
    private BlockGenAgent blockGenAgent;

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    private Level mainLevel = null;

    private final List<UUID> pendingResisterNewPlayerList = new ArrayList<>();

    private boolean disableDefaultCommands;
    private boolean enableInventorySave;
    private Collection<Tuple<Integer, Integer, Integer>> defaultItemCollection;
    private int guideBookVersion = 0;
    private String[] guideBookPages = null;

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
        this.loggerInfo("loading config...");

        this.saveDefaultConfig();
        final Config config = new Config(this);

        this.loggerInfo("loading rdbms...");

        final RDBSHelper rdbsHelper;

        if (config.getDatabaseConfig().getString("database.type").equalsIgnoreCase("mysql"))
            rdbsHelper = new MysqlHelper(config);
        else
            rdbsHelper = new Sqlite3Helper(config);

        if (!rdbsHelper.connect()) {
            this.loggerCritical("loading rdbms failed.");
            this.getServer().shutdown();
            return;
        }

        this.loggerInfo("rdbms connected.");

        this.databaseAgent = new DatabaseAgent(this, rdbsHelper);
        this.messageAgent = new MessageAgent(this, config);
        this.skyBlockAgent = new SkyBlockAgent(this, this.databaseAgent, this.messageAgent, config);
        this.blockGenAgent = new BlockGenAgent(this, this.messageAgent, config);
        this.playerGroupAgent = new PlayerGroupAgent(this, this.databaseAgent, config);

        this.getServer().getPluginManager().registerEvents(new ServerEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockGenEventListener(this.blockGenAgent), this);
        this.getServer().getPluginManager().registerEvents(new SkyBlockEventListener(this.skyBlockAgent), this);
        this.getServer().getPluginManager().registerEvents(new PlayerGroupEventListener(this.playerGroupAgent), this);

        this.getServer().getCommandMap().register("mine2021", new TeleportCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new InviteCommand(this, this.messageAgent, config, this.skyBlockAgent));
        this.getServer().getCommandMap().register("mine2021", new InviteListCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new PurgeCommand(this, this.messageAgent, config, this.skyBlockAgent, this.databaseAgent));
        this.getServer().getCommandMap().register("mine2021", new ProtectionTypeCommand(this, this.messageAgent, config, this.skyBlockAgent));

        this.getServer().getCommandMap().register("mine2021", new BanCommand(this, this.messageAgent, config));
        this.getServer().getCommandMap().register("mine2021", new KickCommand(this, this.messageAgent, config));
        this.getServer().getCommandMap().register("mine2021", new GroupCommand(this, this.messageAgent, config));

        config.parseAdditionalRecipes().forEach(recipe -> getServer().getCraftingManager().registerRecipe(recipe));
        this.getServer().getCraftingManager().rebuildPacket();

        this.disableDefaultCommands = config.getServerConfig().getBoolean("system.disable-nukkit-commands");
        this.enableInventorySave = config.getServerConfig().getBoolean("system.enable-inventory-save");

        this.defaultItemCollection = config.parseDefaultItems();

        this.guideBookVersion = config.getUserInterfaceConfig().getInt("guidebook.version");
        this.guideBookPages = config.parseGuideBookPages();

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

            this.messageAgent.sendBroadcast("message.general.on-player-join",
                    new String[]{"%player"}, new String[]{player.getName()});
        } else {
            int section = this.databaseAgent.getNextSection();
            playerData = Optional.of(new PlayerData(player, uuid, name, ip, 0, SkyblockData.getDefault(section, uuid, name), null));
            this.registerNewPlayer(playerData.get());
        }

        this.playerDataMap.put(uuid, playerData.get());
        this.skyBlockAgent.registerSkyblockData(playerData.get().getSkyblockData());

        this.playerGroupAgent.setPlayerNameTag(playerData.get());
        if (this.disableDefaultCommands && !player.isOp()) this.removeDefaultCommandPermission(player);
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
        this.skyBlockAgent.registerNewSkyblock(playerData);
        this.giveDefaultItems(playerData.getPlayer());

        this.addPendingRegisterNewPlayer(playerData.getUuid());

        this.messageAgent.sendBroadcast("message.general.on-player-first-join",
                new String[]{"%player"}, new String[]{playerData.getName()});
    }

    private void giveDefaultItems(Player player) {
        this.defaultItemCollection.forEach(item -> player.getInventory().addItem(Item.get(item.a, item.b, item.c).clone()));

        final ItemBookWritten book = (ItemBookWritten) Item.get(387, 0, 1);
        final String bookName = this.messageAgent.getText("general.guidebook") + " v" + this.guideBookVersion;
        book.writeBook("§e§lMine24 2021", bookName, Arrays.stream(this.guideBookPages).map(s ->
                s.replaceAll("%player", player.getName())).toArray(String[]::new));
        book.setCustomName(bookName);

        player.getInventory().addItem(book);
    }

    private void removeDefaultCommandPermission(Player player) {
        player.addAttachment(this, "nukkit.command", false);
        player.recalculatePermissions();
    }

    private void addPendingRegisterNewPlayer(UUID uuid) {
        this.pendingResisterNewPlayerList.add(uuid);
    }

    boolean isPendingRegisterNewPlayer(UUID uuid) {
        return this.pendingResisterNewPlayerList.contains(uuid);
    }

    void continueRegisterNewPlayer(Player player) {
        this.pendingResisterNewPlayerList.remove(player.getUniqueId());

        this.messageAgent.sendSimpleForm(player, "form.welcome-form.title", "form.welcome-form.content");
    }

    public boolean isEnableInventorySave() {
        return this.enableInventorySave;
    }

    void setMainLevel(Level level) {
        this.mainLevel = level;
    }

    public Level getMainLevel() {
        return this.mainLevel;
    }

}
