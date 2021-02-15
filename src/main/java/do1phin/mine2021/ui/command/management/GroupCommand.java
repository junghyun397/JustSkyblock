package do1phin.mine2021.ui.command.management;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.PlayerGroupAgent;
import do1phin.mine2021.ui.MessageAgent;

public class GroupCommand extends ManagementCommand {

    private final PlayerGroupAgent playerGroupAgent;

    public GroupCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, PlayerGroupAgent playerGroupAgent) {
        super(config.getUIString("command.management.group-command.command"),
                config.getUIString("command.management.group-command.description"),
                config.getUIString("command.management.group-command.usage"),
                new CommandParameter[]{
                        CommandParameter.newType(config.getUIString("command.management.group-command.parameter.player"),
                                false, CommandParamType.TARGET),
                        CommandParameter.newType(config.getUIString("command.management.group-command.parameter.id"),
                                false, CommandParamType.INT)
                },
                serverAgent, messageAgent);

        this.playerGroupAgent = playerGroupAgent;
    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!this.checkExecutable(commandSender)) return false;

        if (args.length < 2) {
            this.messageAgent.sendMessage(commandSender, "command.management.group-command.format-error");
            return false;
        }

        final int groupID = Integer.parseInt(args[1]);
        if (!this.playerGroupAgent.isValidPlayerGroupID(groupID)) {
            this.messageAgent.sendMessage(commandSender, "command.management.group-command.update-failed-idnotfound",
                    new String[]{"%id"}, new String[]{String.valueOf(groupID)});
            return false;
        }

        final Player targetPlayer = this.serverAgent.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            this.messageAgent.sendMessage(commandSender, "command.management.group-command.update-failed-playernotfound",
                    new String[]{"%player"}, new String[]{String.valueOf(args[0])});
            return false;
        }

        final PlayerData targetPlayerData = this.serverAgent.getPlayerData(targetPlayer);

        this.playerGroupAgent.setPlayerGroup(targetPlayer, groupID);
        this.playerGroupAgent.setPlayerNameTag(targetPlayerData);

        final String groupName = this.playerGroupAgent.getPlayerGroupMap().get(groupID).a;

        this.messageAgent.sendMessage(commandSender, "command.management.group-command.update-succeed",
                new String[]{"%player", "%id", "%group-name"}, new String[]{
                        targetPlayer.getName(),
                        String.valueOf(groupID),
                        groupName
        });

        Command.broadcastCommandMessage(commandSender,
                "group " + targetPlayerData.getName() + " code " + groupID + " name " + groupName);

        return true;
    }

}
