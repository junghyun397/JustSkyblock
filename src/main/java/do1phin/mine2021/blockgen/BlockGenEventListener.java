package do1phin.mine2021.blockgen;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.item.Item;

import java.util.Timer;
import java.util.TimerTask;

public class BlockGenEventListener implements Listener {

    private final BlockGenAgent blockGenAgent;

    public BlockGenEventListener(BlockGenAgent blockGenAgent) {
        this.blockGenAgent = blockGenAgent;
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (this.blockGenAgent.isBlockGenSource(event.getBlock().getFullId())
                && this.blockGenAgent.registerBlockGenSource(event.getPlayer(), event.getBlock())) {
            Item heldItem = event.getPlayer().getInventory().getItemInHand();
            heldItem.setCount(heldItem.getCount() - 1);
            event.setCancelled();
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        int sourceBlockID = this.blockGenAgent.getMainLevel().getBlock(
                (int) event.getBlock().x,
                (int) event.getBlock().y - 1,
                (int) event.getBlock().z).getFullId();

        if (this.blockGenAgent.isBlockGenSource(sourceBlockID)) {
            int x = (int) event.getBlock().x;
            int y = (int) event.getBlock().y;
            int z = (int) event.getBlock().z;

            final Timer genTimer = new Timer();
            TimerTask genTask = new TimerTask() {
                @Override
                public void run() {
                    blockGenAgent.getMainLevel().setBlockFullIdAt(x, y, z,
                            blockGenAgent.getReGenBlock(blockGenAgent.getBlockGenSourceLevel(sourceBlockID)));
                }
            };
            genTimer.schedule(genTask, 250);
        }
    }

    @EventHandler
    public void onEntityInventoryChange(final EntityInventoryChangeEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getNewItem() == null) return;

        int fullID = (event.getNewItem().getId() << 4) + event.getNewItem().getDamage();
        if (this.blockGenAgent.isBlockGenSource(fullID))
            event.getNewItem().setCustomName(this.blockGenAgent.getBlockGenSourceTag(fullID));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFromTo(final BlockFromToEvent event) {
        if (this.blockGenAgent.isBlockGenSource(event.getBlock().getFullId())) event.setCancelled();
    }

}
