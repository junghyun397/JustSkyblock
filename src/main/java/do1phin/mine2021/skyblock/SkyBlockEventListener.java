package do1phin.mine2021.skyblock;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.player.PlayerChunkRequestEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;

public class SkyBlockEventListener implements Listener {

    private final SkyBlockAgent skyBlockAgent;

    public SkyBlockEventListener(SkyBlockAgent skyBlockAgent) {
        this.skyBlockAgent = skyBlockAgent;
    }

    @EventHandler
    public void onLevelLoad(final LevelLoadEvent event) {
        if (event.getLevel().getName().equals("world")) this.skyBlockAgent.setMainLevel(event.getLevel());
    }

    @EventHandler
    public void onChunkRequestEvent(final PlayerChunkRequestEvent event) {
        event.setCancelled(!this.skyBlockAgent.canPlayerLoadChunk(event.getPlayer(), event.getChunkX(), event.getChunkZ()));
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        // event.setRespawnPosition(this.skyBlockAgent.getSkyblockSpawnPosition(this.skyBlockAgent.getSkyblockSectionByPosition(event.getPlayer().getPosition())));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        // event.setCancelled(!this.skyBlockAgent.canPlayerModifyBlock(event.getPlayer(), event.getBlock().getX(), event.getBlock().getZ()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        // event.setCancelled(!this.skyBlockAgent.canPlayerModifyBlock(event.getPlayer(), event.getBlock().getX(), event.getBlock().getZ()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        // event.setCancelled(!this.skyBlockAgent.canPlayerModifyBlock(event.getPlayer(), event.getBlock().getX(), event.getBlock().getZ()));
    }

}
