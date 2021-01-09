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
    public void onPlayerChat(final PlayerChatEvent event) {
        event.setCancelled();
        event.getPlayer().getServer().broadcastMessage(this.playerCategoryAgent.getPlayerChatFormat(event.getPlayer(), event.getMessage()));
    }

}
