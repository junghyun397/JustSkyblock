package do1phin.mine2021.ui.command;

import cn.nukkit.command.Command;
import do1phin.mine2021.ServerAgent;

public abstract class BaseCommand extends Command {

    protected ServerAgent serverAgent;

    public BaseCommand(String name, String description, String usageMessage, ServerAgent serverAgent) {
        super(name, description, usageMessage);
        this.serverAgent = serverAgent;
    }

}
