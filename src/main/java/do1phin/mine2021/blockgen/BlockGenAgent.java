package do1phin.mine2021.blockgen;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSponge;
import cn.nukkit.level.Position;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.utils.Pair;

import java.util.List;
import java.util.Map;

public class BlockGenAgent {

    private final ServerAgent serverAgent;
    private final MessageAgent messageAgent;

    private final Config config;

    private List<Pair<Integer, Integer>> blockGenSourceList;
    private List<Map<Block, Double>> blockGenDict;

    public BlockGenAgent(ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        this.serverAgent = serverAgent;
        this.messageAgent = messageAgent;

        this.config = config;
    }

    public boolean isBlockGenSource(Block block) {
        return false;
    }

    private void upgradeBlockGen(Player player, Position position) {
        // TODO:
    }

}
