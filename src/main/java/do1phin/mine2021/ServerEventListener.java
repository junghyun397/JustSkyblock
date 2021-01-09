package do1phin.mine2021;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.event.player.*;
import do1phin.mine2021.utils.NukkitUtility;

public class ServerEventListener implements Listener {

    private final ServerAgent serverAgent;

    ServerEventListener(ServerAgent serverAgent) {
        this.serverAgent = serverAgent;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.serverAgent.registerPlayerData(event.getPlayer());
        NukkitUtility.broadcastPopUP(event.getPlayer().getServer(), "§7+" + event.getPlayer().getName());
        event.setJoinMessage("");
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.serverAgent.purgePlayerData(event.getPlayer());
        NukkitUtility.broadcastPopUP(event.getPlayer().getServer(), "§7-" + event.getPlayer().getName());
        event.setQuitMessage("");
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        event.setKeepInventory(true);
        event.setDeathMessage("");
    }

    @EventHandler
    public void onPlayerAchievementAwarded(final PlayerAchievementAwardedEvent event) {
        event.setCancelled();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(final BlockFromToEvent event) {
        event.setCancelled();
    }

}
