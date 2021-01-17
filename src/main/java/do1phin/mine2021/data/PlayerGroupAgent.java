package do1phin.mine2021.data;

import cn.nukkit.Player;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.utils.Tuple;

import java.util.Map;

public class PlayerGroupAgent {

    private final ServerAgent serverAgent;

    private final Map<Integer, Tuple<String, String, String>> playerCategoryMap;

    public PlayerGroupAgent(ServerAgent serverAgent, Config config) {
        this.serverAgent = serverAgent;

        this.playerCategoryMap = config.parsePlayerGroupMap();
    }

    public Map<Integer, Tuple<String, String, String>> getPlayerCategoryMap() {
        return this.playerCategoryMap;
    }

    public String getFormattedChatMessage(Player player, String message) {
        return this.playerCategoryMap.get(
                this.serverAgent.getPlayerData(player).getPlayerCategory()).b.replaceFirst("%name", player.getName()) + message;
    }

    public void setPlayerNameTag(PlayerData playerData) {
        playerData.getPlayer().setNameTag(this.playerCategoryMap.get(
                playerData.getPlayerCategory()).c.replaceFirst("%name", playerData.getName()));
    }

}
