package do1phin.mine2021.skyblock;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;

public class SkyBlockEventListener implements Listener {

    SkyBlockAgent skyBlockAgent;

    SkyBlockEventListener(SkyBlockAgent skyBlockAgent) {
        this.skyBlockAgent = skyBlockAgent;
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {

    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {

    }

}
