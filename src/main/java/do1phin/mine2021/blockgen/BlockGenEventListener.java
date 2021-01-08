package do1phin.mine2021.blockgen;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;

public class BlockGenEventListener implements Listener {

    private final BlockGenAgent blockGenAgent;

    public BlockGenEventListener(BlockGenAgent blockGenAgent) {
        this.blockGenAgent = blockGenAgent;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(final BlockBreakEvent event) {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        this.blockGenAgent.isBlockGenSource(event.getBlock());
    }

}
