package do1phin.mine2021.ui.command.management;

import cn.nukkit.command.CommandSender;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;

public class CategoryCommand extends ManagementCommand {

    public CategoryCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        super(config.getPluginConfig().getString("command.management.category-command.command"),
                config.getPluginConfig().getString("command.management.category-command.description"),
                config.getPluginConfig().getString("command.management.category-command.usage"),
                serverAgent, messageAgent, config);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }

}
