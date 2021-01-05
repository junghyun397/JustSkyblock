package do1phin.mine2021.data;

import java.util.List;
import java.util.Map;

public class Config {

    public final int skyblockDistance;

    public final List<Map<Integer, Float>> itemGenDict;

    public final String dbHost;
    public final int dbPort;
    public final String dbUser;
    public final String dbPassword;

    public Config(cn.nukkit.utils.Config pluginConfig) {
        this.skyblockDistance = pluginConfig.getInt("skyblock.distance");

        this.itemGenDict = null;

        this.dbHost = pluginConfig.getString("db.host");
        this.dbPort = pluginConfig.getInt("db.port");
        this.dbUser = pluginConfig.getString("db.user");
        this.dbPassword = pluginConfig.getString("db.password");
    }

}
