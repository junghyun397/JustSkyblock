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
    public void onChunkRequestEvent(PlayerChunkRequestEvent event) {
        event.setCancelled(!this.skyBlockAgent.canPlayerLoadChunk(event.getPlayer(), event.getChunkX(), event.getChunkZ()));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final int section;
        if (event.isFirstSpawn())
            section = this.skyBlockAgent.getSkyblockSectionByUUID(event.getPlayer().getUniqueId());
        else
            section = this.skyBlockAgent.getSkyblockSectionByX((int) Math.round(event.getPlayer().getPosition().getX()));

        event.setRespawnPosition(this.skyBlockAgent.findSafeSpawn(this.skyBlockAgent.getSkyblockSpawn(section))
                .orElseGet(() -> this.skyBlockAgent.generateSafeSpawn(section)));
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
        if (!(event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR
                || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
                && !event.getPlayer().isOp()
                && !this.skyBlockAgent.onPlayerModifyBlock(event.getPlayer(), (int) event.getBlock().getX()))
            event.setCancelled();
    }

}
