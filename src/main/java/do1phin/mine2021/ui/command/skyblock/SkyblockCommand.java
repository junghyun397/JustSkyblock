package do1phin.mine2021.ui.command.skyblock;

import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.ui.command.BaseCommand;

public abstract class SkyblockCommand extends BaseCommand {

    protected SkyBlockAgent skyBlockAgent;

    public SkyblockCommand(String name, String description, String usageMessage, ServerAgent serverAgent, SkyBlockAgent skyBlockAgent) {
        super(name, description, usageMessage, serverAgent);
        this.skyBlockAgent = skyBlockAgent;

        this.setPermission("mine2021.skyblock");
    }

}
