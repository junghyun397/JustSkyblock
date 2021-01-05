package do1phin.mine2021.ui;

import do1phin.mine2021.data.PlayerGroup;
import do1phin.mine2021.utils.Pair;

public class PrefixTool {

    public static Pair<String, String> getNamePair(String name, PlayerGroup playerGroup) {
        if (playerGroup == PlayerGroup.STAFF) return new Pair<>("§b[Staff]§f " + name, "§7<<§l§f" + name + "§r§7>>");
        else return new Pair<>("§2[유저]§f " + name, "§7<<§l§f" + name + "§r§7>>");
    }

}
