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
        super(config.getString("command.skyblock.invite-command.command"),
                config.getString("command.skyblock.invite-command.description"),
                config.getString("command.skyblock.invite-command.usage"),
                new CommandParameter[]{
                        CommandParameter.newType("player", false, CommandParamType.TARGET)
                },
                serverAgent, messageAgent, config, skyBlockAgent);

    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!(commandSender instanceof Player) || !commandSender.hasPermission(this.getPermission())) return false;

        if (args.length == 0) {
            this.messageAgent.sendMessage((Player) commandSender, "command.skyblock.invite-command.format-error");
            return false;
        }

        Player targetPlayer = commandSender.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            this.messageAgent.sendMessage((Player) commandSender, "command.skyblock.invite-command.invite-failed-playernotfound",
                    new String[]{"%player"}, new String[]{args[0]});
            return false;
        }

        SkyblockData skyblockData = this.skyBlockAgent.getSkyblockData((Player) commandSender);
        if (skyblockData.getCollaborators().contains(targetPlayer.getUniqueId().toString())) {
            this.messageAgent.sendMessage((Player) commandSender, "command.skyblock.invite-command.invite-failed-playeralradyinvited");
            return false;
        }
        this.skyBlockAgent.addCollaborator((Player) commandSender, targetPlayer.getUniqueId().toString());

        this.messageAgent.sendMessage(targetPlayer, "message.skyblock.invite-command.invite-received",
                new String[]{"%owner"}, new String[]{commandSender.getName()});
        this.messageAgent.sendMessage((Player) commandSender, "message.skyblock.invite-command.invite-succeed",
                new String[]{"%player"}, new String[]{targetPlayer.getName()});

        return true;
    }

}
