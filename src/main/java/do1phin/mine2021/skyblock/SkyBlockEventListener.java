package do1phin.mine2021.skyblock;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onChunkRequestEvent(PlayerChunkRequestEvent event) {
        if (this.skyBlockAgent.getMainLevel().getId() != event.getPlayer().getPosition().getLevel().getId()) return;

        event.setCancelled(!this.skyBlockAgent.canPlayerLoadChunk(event.getPlayer(), event.getChunkX(), event.getChunkZ()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (this.skyBlockAgent.getMainLevel().getId() != event.getRespawnPosition().getLevel().getId()) return;

        final int section;
        if (event.isFirstSpawn())
            section = this.skyBlockAgent.findFirstRespawnSection(event.getPlayer());
        else
            section = this.skyBlockAgent.getSkyblockSectionByX((int) Math.round(event.getPlayer().getPosition().getX()));

        event.setRespawnPosition(this.skyBlockAgent.findSafeSpawn(this.skyBlockAgent.getSkyblockSpawn(section))
                .orElseGet(() -> this.skyBlockAgent.generateSafeSpawn(section)));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.checkCancelInteractEvent(event.getPlayer(), event.getBlock()))
            event.setCancelled();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.checkCancelInteractEvent(event.getPlayer(), event.getBlock()))
            event.setCancelled();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR
                || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
                && this.checkCancelInteractEvent(event.getPlayer(), event.getBlock()))
            event.setCancelled();
    }

    private boolean checkCancelInteractEvent(Player player, Block block) {
        return !player.isOp()
                && (this.skyBlockAgent.getMainLevel().getId() != block.getLevel().getId()
                || !this.skyBlockAgent.onPlayerModifyBlock(player, (int) block.getX()));
    }

}
