package do1phin.mine2021.ui.command.management;

import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.ui.command.BaseCommand;

public abstract class ManagementCommand extends BaseCommand {

    public ManagementCommand(String name, String description, String usageMessage, ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        super(name, description, usageMessage, serverAgent, messageAgent, config);

        this.setPermission("mine2021.management");
    }

}
