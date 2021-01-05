package do1phin.mine2021.skyblock;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.db.DatabaseAgent;

public class SkyBlockAgent {

    private final ServerAgent serverAgent;
    private final DatabaseAgent databaseAgent;

    public SkyBlockAgent(ServerAgent serverAgent, DatabaseAgent databaseAgent) {
        this.serverAgent = serverAgent;
        this.databaseAgent = databaseAgent;

        serverAgent.getServer().getPluginManager().registerEvents(new SkyBlockEventListener(this), serverAgent);
    }

    public boolean canPlayerModifyBlock(Player player, PlayerData playerData) {
        return false;
    }

    public boolean onBlockBreak(PlayerData playerData, Position position) {
        return false;
    }

}
