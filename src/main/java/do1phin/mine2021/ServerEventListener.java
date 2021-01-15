package do1phin.mine2021;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.player.*;

public class ServerEventListener implements Listener {

    private final ServerAgent serverAgent;

    ServerEventListener(ServerAgent serverAgent) {
        this.serverAgent = serverAgent;
    }

    @EventHandler
    public void onLevelLoad(LevelLoadEvent event) {
        if (event.getLevel().getName().equals("world")) this.serverAgent.setMainLevel(event.getLevel());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.serverAgent.registerPlayer(event.getPlayer());
        event.setJoinMessage("");
    }

    @EventHandler
    public void onPlayerLocallyInitialized(PlayerLocallyInitializedEvent event) {
        if (this.serverAgent.isPendingRegisterNewPlayer(event.getPlayer().getUniqueId()))
            this.serverAgent.continueRegisterNewPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.serverAgent.purgePlayer(event.getPlayer());
        event.setQuitMessage("");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setKeepInventory(true);
        event.setDeathMessage("");
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled();
    }

}
