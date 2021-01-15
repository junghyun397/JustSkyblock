package do1phin.mine2021.data;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;

public class PlayerCategoryEventListener implements Listener {

    private final PlayerCategoryAgent playerCategoryAgent;

    public PlayerCategoryEventListener(PlayerCategoryAgent playerCategoryAgent) {
        this.playerCategoryAgent = playerCategoryAgent;
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        event.getPlayer().getServer().broadcastMessage(this.playerCategoryAgent.getFormattedChatMessage(event.getPlayer(), event.getMessage()));
        event.setCancelled();
    }

}
