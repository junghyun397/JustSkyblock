package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.command.CommandSender;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.ui.MessageAgent;

public class PurgeCommand extends SkyblockCommand {

    public PurgeCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, SkyBlockAgent skyBlockAgent) {
        super(config.getPluginConfig().getString("command.skyblock.purge-command.command"),
                config.getPluginConfig().getString("command.skyblock.purge-command.description"),
                config.getPluginConfig().getString("command.skyblock.purge-command.usage"),
                serverAgent, messageAgent, config, skyBlockAgent);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }

}
