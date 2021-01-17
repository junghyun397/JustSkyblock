package do1phin.mine2021.data;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;

public class PlayerGroupEventListener implements Listener {

    private final PlayerGroupAgent playerGroupAgent;

    public PlayerGroupEventListener(PlayerGroupAgent playerGroupAgent) {
        this.playerGroupAgent = playerGroupAgent;
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        event.getPlayer().getServer().broadcastMessage(this.playerGroupAgent.getFormattedChatMessage(event.getPlayer(), event.getMessage()));
        event.setCancelled();
    }

}
