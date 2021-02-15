package do1phin.mine2021.ui.command.general;

import cn.nukkit.command.CommandSender;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;

public class MailCommand extends GeneralCommand {

    public MailCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        super("", "", "", "mail", null, serverAgent, messageAgent, config);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!this.checkExecutable(commandSender)) return false;
        return false;
    }

}
