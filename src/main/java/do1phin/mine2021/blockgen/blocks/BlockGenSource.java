package do1phin.mine2021.blockgen.blocks;

import cn.nukkit.block.BlockSolid;
import cn.nukkit.item.Item;

public abstract class BlockGenSource extends BlockSolid {

    public static int[] blockGenIds;
    public static String[] blockGenNames;
    public static String[] customNames;

    private final int level;

    protected BlockGenSource(int level) {
        this.level = level;
    }

    @Override
    public int getId() {
        return blockGenIds[this.level];
    }

    @Override
    public String getName() {
        return blockGenNames[this.level];
    }

    @Override
    public int getDropExp() {
        return 2;
    }

    @Override
    public Item[] getDrops(Item item) {
        final Item[] dropItems = super.getDrops(item);
        dropItems[0].setCustomName(customNames[this.level]);
        return new Item[]{dropItems[0]};
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return !item.isNull();
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
