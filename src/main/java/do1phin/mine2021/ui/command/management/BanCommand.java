package do1phin.mine2021.ui.command.management;

import cn.nukkit.command.CommandSender;
import do1phin.mine2021.ServerAgent;

public class BanCommand extends ManagementCommand {

    public BanCommand(ServerAgent serverAgent) {
        super("k", "유저를 차단합니다.", "/b [닉네임]", serverAgent);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }
}
