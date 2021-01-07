package do1phin.mine2021.data;

import cn.nukkit.utils.ConfigSection;
import do1phin.mine2021.utils.Pair;
import do1phin.mine2021.utils.Tuple;

import java.util.HashMap;
import java.util.Map;

public class PlayerCategoryAgent {

    private final Map<Integer, Tuple<String, String, String>> playerCategoryMap;

    public PlayerCategoryAgent(Config config) {
        this.playerCategoryMap = this.buildPlayerCategoryMap(config.getPluginConfig().getSection("category"));
    }

    public Map<Integer, Tuple<String, String, String>> getPlayerCategoryMap() {
        return playerCategoryMap;
    }

    public Pair<String, String> getPrefixPair(String name, int playerCategory) {
        return new Pair<>(this.playerCategoryMap.get(playerCategory).b + name, this.playerCategoryMap.get(playerCategory).c);
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
