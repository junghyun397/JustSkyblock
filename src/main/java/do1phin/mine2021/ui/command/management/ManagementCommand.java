package do1phin.mine2021.ui.command.management;

import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.ui.command.BaseCommand;

public abstract class ManagementCommand extends BaseCommand {

    public ManagementCommand(String name, String description, String usageMessage, ServerAgent serverAgent) {
        super(name, description, usageMessage, serverAgent);

        this.setPermission("mine2021.management");
    }

}
