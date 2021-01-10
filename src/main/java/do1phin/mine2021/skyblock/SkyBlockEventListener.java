package do1phin.mine2021.skyblock;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerChunkRequestEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;

public class SkyBlockEventListener implements Listener {

    private final SkyBlockAgent skyBlockAgent;

    public SkyBlockEventListener(SkyBlockAgent skyBlockAgent) {
        this.skyBlockAgent = skyBlockAgent;
    }

    @EventHandler
    public void onChunkRequestEvent(final PlayerChunkRequestEvent event) {
        event.setCancelled(!this.skyBlockAgent.canPlayerLoadChunk(event.getPlayer(), event.getChunkX(), event.getChunkZ()));
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        if (!event.isFirstSpawn()) {
            event.getPlayer().setSpawn(this.skyBlockAgent.getSkyblockSpawn(
                    this.skyBlockAgent.getSkyblockSectionByX((int) Math.round(event.getPlayer().getPosition().x)))
            );
        }
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!this.skyBlockAgent.onPlayerModifyBlock(event.getPlayer(), (int) event.getBlock().getX())) event.setCancelled();
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!this.skyBlockAgent.onPlayerModifyBlock(event.getPlayer(), (int) event.getBlock().getX())) event.setCancelled();
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!this.skyBlockAgent.onPlayerModifyBlock(event.getPlayer(), (int) event.getBlock().getX())) event.setCancelled();
    }

}
