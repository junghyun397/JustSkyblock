package do1phin.mine2021.blockgen.blocks;

import cn.nukkit.block.BlockSolid;
import cn.nukkit.item.Item;

public abstract class BlockGenSource extends BlockSolid {

    public static int[] blockGenIds;
    public static String[] blockGenNames;
    public static String[] customNames;

    public static int blockGenLevel;

    @Override
    public int getId() {
        return blockGenIds[blockGenLevel];
    }

    @Override
    public String getName() {
        return blockGenNames[blockGenLevel];
    }

    @Override
    public int getDropExp() {
        return 2;
    }

    @Override
    public Item[] getDrops(Item item) {
        final Item[] dropItems = super.getDrops(item);
        dropItems[0].setCustomName(customNames[blockGenLevel]);
        return dropItems;
    }

    @Override
    public double getHardness() {
        return 4;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
