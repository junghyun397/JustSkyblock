package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.ui.command.BaseCommand;

public abstract class SkyblockCommand extends BaseCommand {

    protected SkyBlockAgent skyBlockAgent;

    public SkyblockCommand(String name, String description, String usageMessage, String permission, CommandParameter[] commandParameters, ServerAgent serverAgent, MessageAgent messageAgent, SkyBlockAgent skyBlockAgent) {
        super(name, description, usageMessage, "skyblock", commandParameters, serverAgent, messageAgent);
        this.skyBlockAgent = skyBlockAgent;
    }

}
