package do1phin.mine2021.ui.command.management;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.utils.CalendarHelper;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public class BanCommand extends ManagementCommand {

    private final DatabaseAgent databaseAgent;

    public BanCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, DatabaseAgent databaseAgent) {
        super(config.getUIString("command.management.ban-command.command"),
                config.getUIString("command.management.ban-command.description"),
                config.getUIString("command.management.ban-command.usage"),
                new CommandParameter[]{
                        CommandParameter.newType(config.getUIString("command.management.ban-command.parameter.player"),
                                false, CommandParamType.TARGET),
                        CommandParameter.newType(config.getUIString("command.management.ban-command.parameter.reason"),
                                false, CommandParamType.TEXT),
                        CommandParameter.newType(config.getUIString("command.management.ban-command.parameter.duration"),
                                false, CommandParamType.INT)
                },
                serverAgent, messageAgent);

        this.databaseAgent = databaseAgent;
    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!this.checkExecutable(commandSender)) return false;

        final int duration;
        if (args.length < 3) {
            this.messageAgent.sendMessage(commandSender, "command.management.ban-command.format-error");
            return false;
        }

        try {
            duration = Integer.parseInt(args[2]);
        } catch (NumberFormatException ignored_) {
            this.messageAgent.sendMessage(commandSender, "command.management.ban-command.format-error");
            return false;
        }

        final PlayerData targetPlayerData;
        final Player targetPlayer = this.serverAgent.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            Optional<UUID> uuidByName = this.databaseAgent.getUUIDByPlayerName(args[0]);
            if (uuidByName.isPresent())
                targetPlayerData = this.databaseAgent.getPlayerData(uuidByName.get());
            else {
                this.messageAgent.sendMessage(commandSender, "command.management.ban-command.ban-failed-playernotfound",
                        new String[]{"%player"}, new String[]{args[0]});
                return false;
            }
        } else targetPlayerData = this.serverAgent.getPlayerData(targetPlayer);

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * duration);
        targetPlayerData.setBanDate(timestamp);
        targetPlayerData.setBanReason(args[1]);
        this.databaseAgent.updatePlayerData(targetPlayerData);

        if (targetPlayer != null) {
            final String[] ymdhm = CalendarHelper.getYMDHMFromTimestamp(timestamp);
            targetPlayer.kick(this.messageAgent.getMessage("message.management.on-player-banned",
                    new String[]{"%player", "%year", "%month", "%day", "%hour", "%minute", "%reason"},
                    new String[]{targetPlayer.getName(), ymdhm[0], ymdhm[1], ymdhm[2], ymdhm[3], ymdhm[4], args[1]}), false);
        }

        this.messageAgent.sendMessage(commandSender, "command.management.ban-command.ban-succeed",
                new String[]{"%player", "%reason", "%duration"}, new String[]{targetPlayerData.getName(), args[1], args[2]});

        return true;
    }

}
