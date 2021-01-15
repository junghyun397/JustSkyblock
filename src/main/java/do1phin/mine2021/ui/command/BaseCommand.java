package do1phin.mine2021.ui.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;

public abstract class BaseCommand extends Command {

    protected final ServerAgent serverAgent;
    protected final MessageAgent messageAgent;
    protected final Config config;

    public BaseCommand(String name, String description, String usageMessage, CommandParameter[] commandParameters, ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        super(name, description, usageMessage);
        this.commandParameters.clear();
        this.commandParameters.put("default", commandParameters);

        this.serverAgent = serverAgent;
        this.messageAgent = messageAgent;
        this.config = config;
    }

    protected boolean checkExecutable(CommandSender commandSender) {
        return commandSender instanceof Player && commandSender.hasPermission(this.getPermission());
    }

}
