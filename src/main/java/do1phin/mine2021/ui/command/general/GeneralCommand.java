package do1phin.mine2021.ui.command.general;

import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.ui.command.BaseCommand;

public abstract class GeneralCommand extends BaseCommand {

    protected ServerAgent serverAgent;

    public GeneralCommand(String name, String description, String usageMessage, CommandParameter[] commandParameters, ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        super(name, description, usageMessage, commandParameters, serverAgent, messageAgent);

        this.setPermission("justskyblock.general");
    }
}
