package do1phin.mine2021.skyblock;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerChunkRequestEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;

public class SkyBlockEventListener implements Listener {

    SkyBlockAgent skyBlockAgent;

    SkyBlockEventListener(SkyBlockAgent skyBlockAgent) {
        this.skyBlockAgent = skyBlockAgent;
    }

    @EventHandler
    public void onChunkRequestEvent(final PlayerChunkRequestEvent event) {
        event.setCancelled(!this.skyBlockAgent.canPlayerLoadChunk(event.getPlayer(), event.getChunkX(), event.getChunkZ()));
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        event.setRespawnPosition(this.skyBlockAgent.getIslandSpawnPosition(this.skyBlockAgent.getIslandSection(event.getPlayer().getPosition())));
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        event.setCancelled(!this.skyBlockAgent.canPlayerModifyBlock(event.getPlayer(), event.getBlock().getX(), event.getBlock().getZ()));
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        event.setCancelled(!this.skyBlockAgent.canPlayerModifyBlock(event.getPlayer(), event.getBlock().getX(), event.getBlock().getZ()));
    }

}
