package do1phin.mine2021.blockgen;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.level.LevelLoadEvent;

import java.util.Timer;
import java.util.TimerTask;

public class BlockGenEventListener implements Listener {

    private final BlockGenAgent blockGenAgent;

    public BlockGenEventListener(BlockGenAgent blockGenAgent) {
        this.blockGenAgent = blockGenAgent;
    }

    @EventHandler
    public void onLevelLoad(final LevelLoadEvent event) {
        if (event.getLevel().getName().equals("world")) this.blockGenAgent.setMainLevel(event.getLevel());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        if (this.blockGenAgent.isBlockGenSource(event.getBlock().getFullId()))
            event.setCancelled(!this.blockGenAgent.registerBlockGenSource(event.getBlock()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled()) return;

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

}
