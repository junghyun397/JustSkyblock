package do1phin.mine2021.ui.command.management;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;

public class CategoryCommand extends ManagementCommand {

    public CategoryCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        super(config.getString("command.management.category-command.command"),
                config.getString("command.management.category-command.description"),
                config.getString("command.management.category-command.usage"),
                new CommandParameter[]{
                        CommandParameter.newType("player", false, CommandParamType.TARGET),
                        CommandParameter.newType("category-id", false, CommandParamType.INT)
                },
                serverAgent, messageAgent, config);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }

}
