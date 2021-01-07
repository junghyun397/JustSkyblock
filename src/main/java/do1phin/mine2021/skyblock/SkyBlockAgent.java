package do1phin.mine2021.skyblock;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.skyblock.data.ProtectionType;
import do1phin.mine2021.ui.MessageAgent;

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

    public void updateIslandProtectionType(int section, ProtectionType protectionType) {

    }

    public boolean canPlayerLoadChunk(Player player, int chunkX, int chunkZ) {
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canPlayerModifyBlock(Player player, double blockX, double blockZ) {
        return false;
    }

    public boolean onOreGenBlockBreak(Player player, Position position) {
        return false;
    }

    public void generateNewIsland(PlayerData playerData) {

    }

    public int getIslandSection(Position position) {
        return 0;
    }

    public Position getIslandSpawnPosition(int section) {
        return new Position(128.0, 65, this.config.skyblockDistance * section * 1.0);
    }

}
