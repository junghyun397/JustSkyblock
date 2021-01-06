package do1phin.mine2021.skyblock;

import cn.nukkit.level.Position;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.skyblock.data.ProtectionType;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.utils.Pair;

public class SkyBlockAgent {

    private final ServerAgent serverAgent;
    private final MessageAgent messageAgent;
    private final DatabaseAgent databaseAgent;

    private final Config config;

    public SkyBlockAgent(ServerAgent serverAgent, DatabaseAgent databaseAgent, MessageAgent messageAgent, Config config) {
        this.serverAgent = serverAgent;
        this.messageAgent = messageAgent;
        this.databaseAgent = databaseAgent;

        this.config = config;

        serverAgent.getServer().getPluginManager().registerEvents(new SkyBlockEventListener(this), serverAgent);
    }

    public ProtectionType getIslandProtectionType(int section) {
        return ProtectionType.ALLOW_ONLY_OWNER;
    }

    public boolean canPlayerModifyBlock(PlayerData playerData, Position position) {
        return false;
    }

    public boolean onOreBreak(PlayerData playerData, Position position) {
        return false;
    }

    public boolean onOreGenBlockBreak(PlayerData playerData, Position position) {
        return false;
    }

    public void generateNewIsland(PlayerData playerData) {

    }

    public int getIslandSection(Position position) {
        return 0;
    }

    public Pair<Double, Double> getIslandSpawnPosition(int section) {
        return new Pair<>(128.0, this.config.skyblockDistance * section * 1.0);
    }

}
