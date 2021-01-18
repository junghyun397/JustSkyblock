package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.skyblock.data.SkyblockData;
import do1phin.mine2021.ui.MessageAgent;

public class InviteCommand extends SkyblockCommand {

    public InviteCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, SkyBlockAgent skyBlockAgent) {
        super(config.getUIString("command.skyblock.invite-command.command"),
                config.getUIString("command.skyblock.invite-command.description"),
                config.getUIString("command.skyblock.invite-command.usage"),
                new CommandParameter[]{
                        CommandParameter.newType(config.getUIString("command.skyblock.invite-command.parameter.player"),
                                false, CommandParamType.TARGET)
                },
                serverAgent, messageAgent, skyBlockAgent);

    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!this.checkExecutable(commandSender)) return false;

        if (args.length == 0) {
            this.messageAgent.sendMessage(commandSender, "command.skyblock.invite-command.format-error");
            return false;
        }

        final Player targetPlayer = commandSender.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            this.messageAgent.sendMessage(commandSender, "command.skyblock.invite-command.invite-failed-playernotfound",
                    new String[]{"%player"}, new String[]{args[0]});
            return false;
        }

        final SkyblockData skyblockData = this.skyBlockAgent.getSkyblockData((Player) commandSender);
        if (skyblockData.getCollaborators().contains(targetPlayer.getUniqueId())) {
            this.messageAgent.sendMessage(commandSender, "command.skyblock.invite-command.invite-failed-playeralradyinvited");
            return false;
        }
        this.skyBlockAgent.addCollaborator((Player) commandSender, targetPlayer.getUniqueId());

        this.messageAgent.sendMessage(targetPlayer, "message.skyblock.invite-received",
                new String[]{"%owner"}, new String[]{commandSender.getName()});
        this.messageAgent.sendMessage(commandSender, "message.skyblock.invite-succeed",
                new String[]{"%player"}, new String[]{targetPlayer.getName()});

        return true;
    }

}
