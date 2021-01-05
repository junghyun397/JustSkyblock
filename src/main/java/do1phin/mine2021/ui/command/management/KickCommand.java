package do1phin.mine2021.ui.command.management;

import cn.nukkit.command.CommandSender;
import do1phin.mine2021.ServerAgent;

public class KickCommand extends ManagementCommand {

    public KickCommand(ServerAgent serverAgent) {
        super("k", "유저를 추방합니다.", "/k [닉네임]", serverAgent);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }

}
