package do1phin.mine2021;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.*;
import do1phin.mine2021.utils.NukkitUtility;

public class ServerEventListener implements Listener {

    private final ServerAgent serverAgent;

    ServerEventListener(ServerAgent serverAgent) {
        this.serverAgent = serverAgent;
    }

    @EventHandler
    public void onPlayerPreLogin(final PlayerPreLoginEvent event) {
        this.serverAgent.registerPlayerData(event.getPlayer());
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

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        event.setDeathMessage("");
    }

    @EventHandler
    public void on(final PlayerEvent event) {
    }

}
