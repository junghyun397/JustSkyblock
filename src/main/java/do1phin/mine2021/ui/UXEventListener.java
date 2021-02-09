package do1phin.mine2021.ui;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerAchievementAwardedEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;

public class UXEventListener implements Listener {

    private final UXAgent uxAgent;

    public UXEventListener(UXAgent uxAgent) {
        this.uxAgent = uxAgent;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setKeepInventory(this.uxAgent.isEnableInventorySave());
        event.getEntity().setExperience(event.getEntity().getExperience(), Math.max(event.getEntity().getExperienceLevel() - 1, 0));
        event.setKeepExperience(this.uxAgent.isEnableInventorySave());
        event.setDeathMessage("");
    }

    @EventHandler
    public void onPlayerLocallyInitialized(PlayerLocallyInitializedEvent event) {
        if (this.uxAgent.isPendingRegisterNewPlayer(event.getPlayer().getUniqueId()))
            this.uxAgent.continueRegisterNewPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled();
    }
}
