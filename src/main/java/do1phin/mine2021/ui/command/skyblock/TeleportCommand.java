package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.skyblock.SkyBlockAgent;

public class TeleportCommand extends SkyblockCommand {

    public TeleportCommand(ServerAgent serverAgent, SkyBlockAgent skyBlockAgent) {
        super("이동", "다른 유저의 구역으로 이동합니다.", "/이동 [닉네임]", serverAgent, skyBlockAgent);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }

}
