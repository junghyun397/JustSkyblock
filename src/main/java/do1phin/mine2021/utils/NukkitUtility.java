package do1phin.mine2021.utils;

import cn.nukkit.Server;

public class NukkitUtility {

    public static void broadcastPopUP(Server server, String message) {
        server.getOnlinePlayers().forEach((ignored, player) -> player.sendPopup(message));
    }

}
