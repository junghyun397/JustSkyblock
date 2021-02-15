package do1phin.mine2021.ui.command.management;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;

public class KickCommand extends ManagementCommand {

    public KickCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        super(config.getUIString("command.management.kick-command.command"),
                config.getUIString("command.management.kick-command.description"),
                config.getUIString("command.management.kick-command.usage"),
                new CommandParameter[]{
                        CommandParameter.newType(config.getUIString("command.management.kick-command.parameter.player"),
                                false, CommandParamType.TARGET),
                        CommandParameter.newType(config.getUIString("command.management.kick-command.parameter.reason"),
                                false, CommandParamType.TEXT)
                },
                serverAgent, messageAgent);
    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!this.checkExecutable(commandSender)) return false;

        if (args.length < 2) {
            this.messageAgent.sendMessage(commandSender, "command.management.kick-command.format-error");
            return false;
        }

        final Player targetPlayer = this.serverAgent.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            this.messageAgent.sendMessage(commandSender, "command.management.kick-command.kick-failed-playernotfound",
                    new String[]{"%player"}, new String[]{args[0]});
            return false;
        }

        targetPlayer.kick(this.messageAgent.getMessage("message.management.on-player-kicked",
                new String[]{"%reason"}, new String[]{args[1]}), false);

        this.messageAgent.sendMessage(commandSender, "command.management.kick-command.kick-succeed",
                new String[]{"%player", "%reason"}, new String[]{targetPlayer.getName(), args[1]});

        Command.broadcastCommandMessage(commandSender,
                "kick " + targetPlayer.getName() + " reason " + args[1]);

        return true;
    }

}
