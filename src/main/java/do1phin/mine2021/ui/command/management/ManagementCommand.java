package do1phin.mine2021.ui.command.management;

import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.ui.command.BaseCommand;

public abstract class ManagementCommand extends BaseCommand {

    public ManagementCommand(String name, String description, String usageMessage, CommandParameter[] commandParameters, ServerAgent serverAgent, MessageAgent messageAgent) {
        super(name, description, usageMessage, commandParameters, serverAgent, messageAgent);

        this.setPermission("mine2021.management");
    }

}
