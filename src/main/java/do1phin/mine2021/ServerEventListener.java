package do1phin.mine2021;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.utils.NukkitUtility;
import do1phin.mine2021.utils.Pair;

public class ServerEventListener implements Listener {

    private final ServerAgent serverAgent;

    ServerEventListener(ServerAgent serverAgent) {
        this.serverAgent = serverAgent;
    }

    @EventHandler
    public void onPlayerPreLogin(final PlayerPreLoginEvent event) {
        PlayerData playerData = this.serverAgent.registerPlayerData(event.getPlayer());

        Pair<String, String> namePair = MessageAgent.getNamePair(event.getPlayer().getName(), playerData.getPlayerGroup());
        event.getPlayer().setDisplayName(namePair.a);
        event.getPlayer().setNameTag(namePair.b);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        event.setJoinMessage("");
        NukkitUtility.broadcastPopUP(event.getPlayer().getServer(), "§7+" + event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage("");
        NukkitUtility.broadcastPopUP(event.getPlayer().getServer(), "§7-" + event.getPlayer().getName());
        this.serverAgent.purgePlayerData(event.getPlayer().getUniqueId().toString());
    }

}
