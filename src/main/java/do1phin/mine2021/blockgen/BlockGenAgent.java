package do1phin.mine2021.blockgen;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import do1phin.mine2021.ServerAgent;
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

    public String getBlockGenSourceTag(int id) {
        return this.messageAgent.getText("blockgen.item-tag") + (this.getBlockGenSourceLevel(id) + 1);
    }

    public boolean registerBlockGenSource(Player player, Block block) {
        final int blockGenSourceLevel = this.getBlockGenSourceLevel(block.getId());
        this.spawnNextBlock((int) block.x, (int) block.y + 1, (int) block.z, blockGenSourceLevel);

        if (blockGenSourceLevel + 2 > this.blockGenSource.size()) return false;

        final Optional<Position> core = this.canUpgradeBlockGen(block);
        if (core.isPresent()) {
            this.upgradeBlockGen(blockGenSourceLevel, core.get());
            this.messageAgent.sendMessage(player, "message.blockgen.blockgen-upgrade-succeed",
                    new String[]{"%level"}, new String[]{String.valueOf(blockGenSourceLevel + 2)});
            return true;
        } else return false;
    }

    void spawnNextBlock(int x, int y, int z, int sourceLevel) {
        TimerWrapper.schedule(() -> {
            if (this.getBlockGenSourceLevel(this.getMainLevel().getBlock(x, y - 1, z).getId()) == sourceLevel) {
                final Block reGenBlock;
                if (Math.random() < 1 / (float) ((sourceLevel + 1) * 8 * 8))
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
