package do1phin.mine2021.skyblock;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.*;

public class SkyBlockEventListener implements Listener {

    private final SkyBlockAgent skyBlockAgent;

    public SkyBlockEventListener(SkyBlockAgent skyBlockAgent) {
        this.skyBlockAgent = skyBlockAgent;
    }

    @EventHandler
    public void onChunkRequestEvent(PlayerChunkRequestEvent event) {
        event.setCancelled(!this.skyBlockAgent.canPlayerLoadChunk(event.getPlayer(), event.getChunkX(), event.getChunkZ()));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnPosition(this.skyBlockAgent.findSafeSpawn(this.skyBlockAgent.getSkyblockSpawn(
                this.skyBlockAgent.getSkyblockSectionByX((int) Math.round(event.getPlayer().getPosition().getX()))
         )));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp() && !this.skyBlockAgent.onPlayerModifyBlock(event.getPlayer(), (int) event.getBlock().getX()))
            event.setCancelled();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().isOp() && !this.skyBlockAgent.onPlayerModifyBlock(event.getPlayer(), (int) event.getBlock().getX()))
            event.setCancelled();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
                && !event.getPlayer().isOp()
                && !this.skyBlockAgent.onPlayerModifyBlock(event.getPlayer(), (int) event.getBlock().getX()))
            event.setCancelled();
    }

}
