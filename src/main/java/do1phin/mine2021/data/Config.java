package do1phin.mine2021.data;

import cn.nukkit.utils.ConfigSection;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class Config {

    private final cn.nukkit.utils.Config pluginConfig;

    public final int skyblockDistance;
    public final int[][][] defaultIslandShape;

    public final Map<Integer, Float>[] blockGenDict;

    public final String databaseType;

    public final String dbFileName;

    public final String dbHost;
    public final int dbPort;
    public final String dbUser;
    public final String dbPassword;

    public Config(cn.nukkit.utils.Config config) {
        this.pluginConfig = config;

        this.skyblockDistance = config.getInt("skyblock.distance");
        this.defaultIslandShape = this.parseDefaultIslandShape(config.getList("skyblock.default-island-shape"));

        this.blockGenDict = this.parseBlockGenDict(config.getSection("blockgen"));

        this.databaseType = config.getString("db.type").toUpperCase();

        this.dbFileName = config.getString("db.sqlite.db-file");

        this.dbHost = config.getString("db.mysql.host");
        this.dbPort = config.getInt("db.mysql.port");
        this.dbUser = config.getString("db.mysql.user");
        this.dbPassword = config.getString("db.mysql.password");
    }

    private int[][][] parseDefaultIslandShape(List value) {
        return null;
    }

    private Map<Integer, Float>[] parseBlockGenDict(ConfigSection value) {
        return null;
    }

    public String getString(String key) {
        return this.pluginConfig.getString(key);
    }

    public cn.nukkit.utils.Config getPluginConfig() {
        return this.pluginConfig;
    }

}
