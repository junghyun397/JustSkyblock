package do1phin.mine2021.data;

import cn.nukkit.Player;
import cn.nukkit.utils.ConfigSection;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.utils.Tuple;

import java.util.HashMap;
import java.util.Map;

public class PlayerCategoryAgent {

    private final ServerAgent serverAgent;

    private final Map<Integer, Tuple<String, String, String>> playerCategoryMap;

    public PlayerCategoryAgent(ServerAgent serverAgent, Config config) {
        this.serverAgent = serverAgent;

        this.playerCategoryMap = this.buildPlayerCategoryMap(config.getPluginConfig().getSection("category"));
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

    private Map<Integer, Tuple<String, String, String>> buildPlayerCategoryMap(ConfigSection configSection) {
        final Map<Integer, Tuple<String, String, String>> categoryMap = new HashMap<>();
        configSection.forEach((s, o) -> {
            ConfigSection section = ((ConfigSection) o);
            categoryMap.put(section.getInt("id", 0),
                    new Tuple<>(s, section.getString("display-name") + " ", section.getString("nametag")));
        });
        return categoryMap;
    }

}
