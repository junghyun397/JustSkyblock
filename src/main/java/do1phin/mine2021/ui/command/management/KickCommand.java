package do1phin.mine2021.ui.command.management;

import cn.nukkit.command.CommandSender;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;

public class KickCommand extends ManagementCommand {

    public KickCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        super(config.getPluginConfig().getString("command.management.kick-command.command"),
                config.getPluginConfig().getString("command.management.kick-command.description"),
                config.getPluginConfig().getString("command.management.kick-command.usage"),
                serverAgent, messageAgent, config);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }

}
