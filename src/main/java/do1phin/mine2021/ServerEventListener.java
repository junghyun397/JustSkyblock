package do1phin.mine2021;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.player.*;

public class ServerEventListener implements Listener {

    private final ServerAgent serverAgent;
    private final String skyblockLevel;

    public ServerEventListener(ServerAgent serverAgent, String skyblockLevel) {
        this.serverAgent = serverAgent;
        this.skyblockLevel = skyblockLevel;
    }

    @EventHandler
    public void onLevelLoad(LevelLoadEvent event) {
        if (event.getLevel().getName().equals(skyblockLevel)) this.serverAgent.setMainLevel(event.getLevel());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.serverAgent.registerPlayer(event.getPlayer());
        event.setJoinMessage("");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.serverAgent.unregisterPlayer(event.getPlayer());
        event.setQuitMessage("");
    }

}
