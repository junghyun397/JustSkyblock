package do1phin.mine2021.blockgen;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.utils.Pair;

import java.util.List;
import java.util.Optional;

public class BlockGenAgent {

    private final ServerAgent serverAgent;
    private final MessageAgent messageAgent;

    private final List<Integer> blockGenSource;
    private final List<List<Pair<Double, Integer>>> blockGenDict;

    private Level mainLevel = null;

    public BlockGenAgent(ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        this.serverAgent = serverAgent;
        this.messageAgent = messageAgent;

        Pair<List<Integer>, List<List<Pair<Double, Integer>>>> blockGenData = config.parseBlockGenData();
        this.blockGenSource = blockGenData.a;
        this.blockGenDict = blockGenData.b;
    }

    public boolean isBlockGenSource(int id) {
        return this.blockGenSource.contains(id);
    }

    public int getBlockGenSourceLevel(int id) {
        return this.blockGenSource.indexOf(id);
    }

    public int getReGenBlock(int blockGenSourceLevel) {
        double rand = Math.random();
        for (Pair<Double, Integer> entry: this.blockGenDict.get(blockGenSourceLevel)) if (rand < entry.a) return entry.b;
        return 0;
    }

    public boolean registerBlockGenSource(Block block) {
        this.mainLevel.setBlockFullIdAt((int) block.x, (int) block.y + 1, (int) block.z,
                this.getReGenBlock(this.getBlockGenSourceLevel(block.getFullId())));
        Optional<Position> core = this.canUpgradeBlockGen(block);
        if (core.isPresent()) {
            this.upgradeBlockGen(block, core.get());
            return false;
        } else return true;
    }

    private Optional<Position> canUpgradeBlockGen(Block block) {
        boolean[][] map = new boolean[5][5];
        int count = 1;
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                map[x][z] = this.mainLevel.getBlock((int) block.x + x - 2, (int) block.y, (int) block.z + z - 2).getFullId()
                        == block.getFullId();
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
                    return Optional.of(new Position(block.x + x - 1, block.y, block.z + z - 1));

        return Optional.empty();
    }

    private void upgradeBlockGen(Block prvBlock, Position core) {
        for (int y = 0; y < 2; y++)
            for (int x = 0; x < 3; x++)
                for (int z = 0; z < 3; z++)
                    this.mainLevel.setBlockAt((int) core.x + x - 1, (int) core.y + y, (int) core.z + z - 1, 0);

        this.mainLevel.setBlockFullIdAt((int) core.x, (int) core.y, (int) core.z,
                this.blockGenSource.get(this.blockGenSource.indexOf(prvBlock.getFullId()) + 1));
        this.mainLevel.setBlockFullIdAt((int) core.x, (int) core.y + 1, (int) core.z,
                this.getReGenBlock(this.getBlockGenSourceLevel(prvBlock.getFullId())));

        for (int x = 0; x < 9; x++)
            for (int z = 0; z < 9; z++)
                this.mainLevel.addParticleEffect(new Position(core.x - (x - 5.5) / 3, core.y + 1, core.z - (z - 5.5) / 3),
                        ParticleEffect.BLUE_FLAME);
    }

    void setMainLevel(Level level) {
        this.mainLevel = level;
    }

    Level getMainLevel() {
        return this.mainLevel;
    }

}
