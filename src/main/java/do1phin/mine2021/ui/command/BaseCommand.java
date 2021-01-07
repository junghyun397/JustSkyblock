package do1phin.mine2021.ui.command;

import cn.nukkit.command.PluginCommand;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;

public abstract class BaseCommand extends PluginCommand<ServerAgent> {

    protected final ServerAgent serverAgent;
    protected final MessageAgent messageAgent;
    protected final Config config;

    public BaseCommand(String name, String description, String usageMessage, ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        super(name, serverAgent);
        this.setDescription(description);
        this.setUsage(usageMessage);

        this.serverAgent = serverAgent;
        this.messageAgent = messageAgent;
        this.config = config;
    }

}
