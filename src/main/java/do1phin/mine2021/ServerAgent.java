package do1phin.mine2021;

import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.data.db.MysqlHelper;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.ui.command.skyblock.TeleportCommand;
import do1phin.mine2021.ui.command.management.BanCommand;
import do1phin.mine2021.ui.command.management.KickCommand;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.utils.EmptyGenerator;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.PlayerGroup;

import java.util.HashMap;

public class ServerAgent extends PluginBase {

    private static ServerAgent instance;
    public static ServerAgent getInstance() {
        return instance;
    }

    private Config config;

    private final HashMap<String, PlayerData> playerDataMap = new HashMap<>();

    public PlayerData registerPlayer(String name, String ip, long uuid) {
        PlayerData playerData = new PlayerData(name, ip, uuid, PlayerGroup.USER, 0);
        return this.playerDataMap.put(name, playerData);
    }

    public void purgePlayer(String name) {
        this.playerDataMap.remove(name);
    }

    @Override
    public void onLoad() {
        instance = this;
        Generator.addGenerator(EmptyGenerator.class, "EMPTY", Generator.TYPE_INFINITE);
    }

    @Override
    public void onEnable() {
        this.getLogger().info("start loading...");
        this.getServer().getPluginManager().registerEvents(new ServerEventListener(this), this);

        this.getLogger().info("start loading config...");
        this.config = new Config(this.getConfig());
        this.getLogger().info("succeed loading config.");

        this.getLogger().info("start mysql-db connection...");
        if (new MysqlHelper(this.config).connect()) this.getLogger().info("mysql-db connected.");
        else {
            this.getLogger().critical("mysql-db connect failed.");
            this.getServer().shutdown();
        }
        this.getLogger().info("mysql-db connected.");

        DatabaseAgent databaseAgent = new DatabaseAgent(this, new MysqlHelper(this.config));
        SkyBlockAgent skyBlockAgent = new SkyBlockAgent(this, databaseAgent);

        this.getServer().getCommandMap().register("", new TeleportCommand(this, skyBlockAgent));

        this.getServer().getCommandMap().register("", new BanCommand(this));
        this.getServer().getCommandMap().register("", new KickCommand(this));

        this.getLogger().info("succeed loading.");
    }

}
