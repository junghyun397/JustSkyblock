package do1phin.mine2021.data;

import java.util.List;
import java.util.Map;

public class Config {

    public final int skyblockDistance;
    public final List<List<Integer>> defaultIslandShape;

    public final List<Map<Integer, Float>> itemGenDict;

    public final String databaseType;

    public final String dbHost;
    public final int dbPort;
    public final String dbUser;
    public final String dbPassword;

    public Config(cn.nukkit.utils.Config config) {
        this.skyblockDistance = config.getInt("skyblock.distance");
        this.defaultIslandShape = null;

        this.itemGenDict = null;

        this.databaseType = "Mysql";

        this.dbHost = config.getString("db.host");
        this.dbPort = config.getInt("db.port");
        this.dbUser = config.getString("db.user");
        this.dbPassword = config.getString("db.password");
    }

}
