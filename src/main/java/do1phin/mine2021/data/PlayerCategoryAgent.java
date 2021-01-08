package do1phin.mine2021.data;

import cn.nukkit.utils.ConfigSection;
import do1phin.mine2021.utils.Tuple;

import java.util.HashMap;
import java.util.Map;

public class PlayerCategoryAgent {

    private final Map<Integer, Tuple<String, String, String>> playerCategoryMap;

    public PlayerCategoryAgent(Config config) {
        this.playerCategoryMap = this.buildPlayerCategoryMap(config.getPluginConfig().getSection("category"));
    }

    public Map<Integer, Tuple<String, String, String>> getPlayerCategoryMap() {
        return this.playerCategoryMap;
    }

    public void setPlayerNameTag(PlayerData playerData) {
        playerData.getPlayer().setDisplayName(this.playerCategoryMap.get(
                playerData.getPlayerCategory()).b.replaceFirst("%name", playerData.getName()));
        playerData.getPlayer().setNameTag(this.playerCategoryMap.get(
                playerData.getPlayerCategory()).c.replaceFirst("%name", playerData.getName()));
    }

    private Map<Integer, Tuple<String, String, String>> buildPlayerCategoryMap(ConfigSection configSection) {
        Map<Integer, Tuple<String, String, String>> categoryMap = new HashMap<>();
        configSection.forEach((s, o) -> {
            ConfigSection section = ((ConfigSection) o);
            categoryMap.put(section.getInt("id", 0),
                    new Tuple<>(s, section.getString("display-name"), section.getString("nametag")));
        });
        return categoryMap;
    }

}
