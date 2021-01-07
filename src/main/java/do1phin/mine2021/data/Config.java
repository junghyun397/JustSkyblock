package do1phin.mine2021.data;

import cn.nukkit.utils.ConfigSection;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class Config {

    private final cn.nukkit.utils.Config pluginConfig;

    public final int skyblockDistance;
    public final int[][][] defaultIslandShape;

    public final Map<Integer, Float>[] itemGenDict;

    public final String databaseType;

    public final String dbHost;
    public final int dbPort;
    public final String dbUser;
    public final String dbPassword;

    public Config(cn.nukkit.utils.Config config) {
        this.pluginConfig = config;

        this.skyblockDistance = config.getInt("skyblock.distance");
        this.defaultIslandShape = this.parseDefaultIslandShape(config.getList("skyblock.default-island-shape"));

        this.itemGenDict = this.parseOreGenDict(config.getSection("oregen"));

        this.databaseType = config.getString("db.type").toUpperCase();

        this.dbHost = config.getString("db.mysql.host");
        this.dbPort = config.getInt("db.mysql.port");
        this.dbUser = config.getString("db.mysql.user");
        this.dbPassword = config.getString("db.mysql.password");
    }

    private int[][][] parseDefaultIslandShape(List value) {
        return null;
    }

    private Map<Integer, Float>[] parseOreGenDict(ConfigSection value) {
        return null;
    }

    public cn.nukkit.utils.Config getPluginConfig() {
        return this.pluginConfig;
    }

}
