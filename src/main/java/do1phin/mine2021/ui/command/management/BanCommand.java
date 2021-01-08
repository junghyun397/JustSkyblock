package do1phin.mine2021.ui.command.management;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;

public class BanCommand extends ManagementCommand {

    public BanCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        super(config.getString("command.management.ban-command.command"),
                config.getString("command.management.ban-command.description"),
                config.getString("command.management.ban-command.usage"),
                new CommandParameter[]{
                        CommandParameter.newType("player", false, CommandParamType.TARGET),
                        CommandParameter.newType("reason", false, CommandParamType.TEXT),
                        CommandParameter.newType("duration", false, CommandParamType.INT)
                },
                serverAgent, messageAgent, config);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }

}
