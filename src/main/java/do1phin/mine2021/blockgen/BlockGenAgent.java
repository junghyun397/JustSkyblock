package do1phin.mine2021.blockgen;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.blockgen.blocks.*;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.utils.Pair;
import do1phin.mine2021.utils.TimerWrapper;
import do1phin.mine2021.utils.Tuple;

import java.util.List;
import java.util.Optional;

public class BlockGenAgent {

    private final ServerAgent serverAgent;
    private final MessageAgent messageAgent;

    private final List<Integer> blockGenSource;
    private final List<Integer> blockGenDelay;
    private final List<List<Pair<Double, Block>>> blockGenDict;

    public BlockGenAgent(ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        this.serverAgent = serverAgent;
        this.messageAgent = messageAgent;

        final Tuple<List<Integer>, List<Integer>, List<List<Pair<Double, Block>>>> blockGenData = config.parseBlockGenData();
        this.blockGenSource = blockGenData.a;
        this.blockGenDelay = blockGenData.b;
        this.blockGenDict = blockGenData.c;

        this.registerBlockGenSource();
    }

    @SuppressWarnings("unchecked")
    private void registerBlockGenSource() {
        BlockGenSource.blockGenIds = new int[this.blockGenSource.size()];
        BlockGenSource.blockGenNames = new String[this.blockGenSource.size()];
        BlockGenSource.customNames = new String[this.blockGenSource.size()];

        for (int i = 0; i < this.blockGenSource.size(); i++) {
            final int id = this.blockGenSource.get(i);
            final String blockName = "blockgen_source_" + (i + 1);
            final String customName = this.messageAgent.getText("blockgen.item-tag") + (i + 1);

            BlockGenSource.blockGenIds[i] = id;
            BlockGenSource.blockGenNames[i] = blockName;
            BlockGenSource.customNames[i] = customName;

            final Class<? extends BlockGenSource> blockClass;
            try {
                blockClass = (Class<? extends BlockGenSource>) Class.forName("do1phin.mine2021.blockgen.blocks.BlockGenSource" + (i + 1));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                this.serverAgent.loggerWarning(e.getMessage());
                return;
            }

            Block.registerBlockImplementation(id, blockClass, blockName, false);
        }
    }

    public Item getBasicBlockGenSource() {
        final Item item = Item.get(this.blockGenSource.get(0), 0, 1);
        item.setCustomName(this.messageAgent.getText("blockgen.item-tag") + "1");
        return item;
    }

    public boolean isBlockGenSource(int id) {
        return this.blockGenSource.contains(id);
    }

    public int getBlockGenSourceLevel(int id) {
        return this.blockGenSource.indexOf(id);
    }

    public int getBlockGenDelay(int blockGenSourceLevel) {
        return this.blockGenDelay.get(blockGenSourceLevel);
    }

    public Block getReGenBlock(int blockGenSourceLevel) {
        final double rand = Math.random();
        for (Pair<Double, Block> entry: this.blockGenDict.get(blockGenSourceLevel)) {
            if (rand < entry.a) return entry.b.clone();
        }

        return BlockState.of(1, 0).getBlock().clone();
    }

    public boolean registerBlockGenSource(Player player, Block block) {
        final int blockGenSourceLevel = this.getBlockGenSourceLevel(block.getId());
        this.spawnNextBlock((int) block.x, (int) block.y + 1, (int) block.z, blockGenSourceLevel);

        if (blockGenSourceLevel + 2 > this.blockGenSource.size()) return false;

        return this.canUpgradeBlockGen(block).map(position -> {
            this.upgradeBlockGen(blockGenSourceLevel, position);
            this.messageAgent.sendMessage(player, "message.blockgen.blockgen-upgrade-succeed",
                    new String[]{"%level"}, new String[]{String.valueOf(blockGenSourceLevel + 2)});
            return true;
        }).orElse(false);
    }

    void spawnNextBlock(int x, int y, int z, int sourceLevel) {
        TimerWrapper.schedule(() -> {
            if (this.getBlockGenSourceLevel(this.getMainLevel().getBlock(x, y - 1, z).getId()) == sourceLevel
                    && this.getMainLevel().getBlock(x, y, z).getId() == 0) {
                final Block reGenBlock;
                if (Math.random() < 1 / (float) ((Math.pow(6, sourceLevel + 1)) * 4))
                    reGenBlock = BlockState.of(this.blockGenSource.get(sourceLevel)).getBlock().clone();
                else reGenBlock = this.getReGenBlock(sourceLevel);

                this.getMainLevel().setBlock(new Vector3(x, y, z), reGenBlock);
            }}, this.getBlockGenDelay(sourceLevel)
        );
    }

    private Optional<Position> canUpgradeBlockGen(Block block) {
        final boolean[][] map = new boolean[5][5];
        int count = 1;
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                map[x][z] = this.getMainLevel().getBlock((int) block.x + x - 2, (int) block.y, (int) block.z + z - 2).getId()
                        == block.getId();
                if (map[x][z]) count++;
            }
        }
        map[2][2] = true;

        if (count < 9) return Optional.empty();

        for (int x = 0; x < 3; x++)
            for (int z = 0; z < 3; z++)
                if(map[x][z] && map[x + 1][z] && map[x + 2][z] &&
                        map[x][z + 1] && map[x + 1][z + 1] && map[x + 2][z + 1] &&
                        map[x][z + 2] && map[x + 1][z + 2] && map[x + 2][z + 2])
                    return Optional.of(new Position(block.x + x - 1, block.y, block.z + z - 1, this.getMainLevel()));

        return Optional.empty();
    }

    private void upgradeBlockGen(int prvSourceLevel, Position core) {
        for (int y = 0; y < 2; y++)
            for (int x = 0; x < 3; x++)
                for (int z = 0; z < 3; z++)
                    this.getMainLevel().setBlockAt((int) core.x + x - 1, (int) core.y + y, (int) core.z + z - 1, 0);

        this.getMainLevel().setBlockIdAt((int) core.x, (int) core.y, (int) core.z,
                this.blockGenSource.get(prvSourceLevel + 1));

        this.spawnNextBlock((int) core.x , (int) core.y + 1, (int) core.z, prvSourceLevel + 1);

        for (int x = 0; x < 9; x++)
            for (int z = 0; z < 9; z++)
                this.getMainLevel().addParticleEffect(new Position(core.x - (x - 5.5) / 3, core.y + 1, core.z - (z - 5.5) / 3),
                        ParticleEffect.BASIC_SMOKE);
    }

    Level getMainLevel() {
        return this.serverAgent.getMainLevel();
    }

}
