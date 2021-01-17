package do1phin.mine2021.blockgen;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.item.Item;

public class BlockGenEventListener implements Listener {

    private final BlockGenAgent blockGenAgent;

    public BlockGenEventListener(BlockGenAgent blockGenAgent) {
        this.blockGenAgent = blockGenAgent;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled() && this.blockGenAgent.isBlockGenSource(event.getBlock().getId())
                && this.blockGenAgent.registerBlockGenSource(event.getPlayer(), event.getBlock())) {
            final Item heldItem = event.getPlayer().getInventory().getItemInHand();
            heldItem.setCount(heldItem.getCount() - 1);
            event.setCancelled();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        int sourceBlockID = this.blockGenAgent.getMainLevel().getBlock(
                (int) event.getBlock().x,
                (int) event.getBlock().y - 1,
                (int) event.getBlock().z).getId();

        if (this.blockGenAgent.isBlockGenSource(sourceBlockID)) {
            final int x = (int) event.getBlock().x;
            final int y = (int) event.getBlock().y;
            final int z = (int) event.getBlock().z;

            this.blockGenAgent.spawnNextBlock(x, y, z, blockGenAgent.getBlockGenSourceLevel(sourceBlockID));
        }
    }

    @EventHandler
    public void onEntityInventoryChange(EntityInventoryChangeEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getNewItem() == null) return;

        if (this.blockGenAgent.isBlockGenSource(event.getNewItem().getBlockId()))
            event.getNewItem().setCustomName(this.blockGenAgent.getBlockGenSourceTag(event.getNewItem().getBlockId()));
    }

    @EventHandler
    public void onBlockFromTo(BlockUpdateEvent event) {
        if (this.blockGenAgent.isBlockGenSource(event.getBlock().getId())) event.setCancelled();
    }

}
