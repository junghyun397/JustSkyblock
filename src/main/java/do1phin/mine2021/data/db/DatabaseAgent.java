package do1phin.mine2021.data.db;

import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.PlayerData;

public class DatabaseAgent {

    private final ServerAgent serverAgent;
    private final MysqlHelper mysqlHelper;

    public DatabaseAgent(ServerAgent serverAgent, MysqlHelper mysqlHelper) {
        this.serverAgent = serverAgent;
        this.mysqlHelper = mysqlHelper;
    }

    public PlayerData getPlayerData() {
        return null;
    }

    public void registerPlayerData(PlayerData playerData) {

    }

    public void updatePlayerData(PlayerData playerData) {

    }

    public int getCurrentSector() {
        return 0;
    }

}
