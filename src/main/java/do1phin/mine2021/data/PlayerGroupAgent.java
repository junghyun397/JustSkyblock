package do1phin.mine2021.data;

import cn.nukkit.Player;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.utils.Tuple;

import java.util.Map;

public class PlayerGroupAgent {

    private final ServerAgent serverAgent;
    private final DatabaseAgent databaseAgent;

    private final Map<Integer, Tuple<String, String, String>> playerGroupMap;

    public PlayerGroupAgent(ServerAgent serverAgent, DatabaseAgent databaseAgent, Config config) {
        this.serverAgent = serverAgent;
        this.databaseAgent = databaseAgent;

        this.playerGroupMap = config.parsePlayerGroupMap();
    }

    public Map<Integer, Tuple<String, String, String>> getPlayerGroupMap() {
        return this.playerGroupMap;
    }

    public boolean isValidPlayerGroupID(int groupID) {
        return this.playerGroupMap.containsKey(groupID);
    }

    public void setPlayerGroup(Player player, int groupID) {
        final PlayerData playerData = this.serverAgent.getPlayerData(player);
        playerData.setPlayerGroup(groupID);
        this.databaseAgent.updatePlayerData(playerData);
    }

    String getFormattedChatMessage(Player player, String message) {
        return this.playerGroupMap.get(this.serverAgent.getPlayerData(player).getPlayerGroup()).b
                .replaceFirst("%name", player.getName()) + message;
    }

    public void setPlayerNameTag(PlayerData playerData) {
        playerData.getPlayer().setNameTag(this.playerGroupMap.get(
                playerData.getPlayerGroup()).c.replaceFirst("%name", playerData.getName()));
    }

}
