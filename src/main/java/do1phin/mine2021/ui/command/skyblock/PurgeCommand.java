package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.skyblock.data.SkyblockData;
import do1phin.mine2021.ui.MessageAgent;

import java.util.Optional;
import java.util.UUID;

public class PurgeCommand extends SkyblockCommand {

    private final DatabaseAgent databaseAgent;

    public PurgeCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, SkyBlockAgent skyBlockAgent, DatabaseAgent databaseAgent) {
        super(config.getString("command.skyblock.purge-command.command"),
                config.getString("command.skyblock.purge-command.description"),
                config.getString("command.skyblock.purge-command.usage"),
                new CommandParameter[]{
                        CommandParameter.newType("player", false, CommandParamType.TARGET)
                },
                serverAgent, messageAgent, config, skyBlockAgent);

        this.databaseAgent = databaseAgent;
    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!(commandSender instanceof Player) || !commandSender.hasPermission(this.getPermission())) return false;

        if (args.length == 0) {
            this.messageAgent.sendMessage((Player) commandSender, "command.skyblock.purge-command.format-error");
            return false;
        }

        final Player targetPlayer = commandSender.getServer().getPlayer(args[0]);
        final UUID targetUUID;
        if (targetPlayer == null) {
            Optional<UUID> tTargetUUID = this.databaseAgent.getUUIDByPlayerName(args[0]);
            if (tTargetUUID.isPresent()) targetUUID = tTargetUUID.get();
            else {
                this.messageAgent.sendMessage((Player) commandSender, "command.skyblock.purge-command.purge-failed-playernotfound",
                        new String[]{"%player"}, new String[]{args[0]});
                return false;
            }
        } else targetUUID = targetPlayer.getUniqueId();

        final SkyblockData skyblockData = this.skyBlockAgent.getSkyblockData((Player) commandSender);
        if (!skyblockData.getCollaborators().contains(targetUUID)) {
            this.messageAgent.sendMessage((Player) commandSender, "command.skyblock.purge-command.purge-failed-playernotinvited",
                    new String[]{"%player"}, new String[]{args[0]});
            return false;
        }

        this.skyBlockAgent.purgeCollaborator((Player) commandSender, targetUUID);

        this.messageAgent.sendMessage((Player) commandSender, "message.skyblock.purge-succeed",
                new String[]{"%player"}, new String[]{args[0]});

        return true;
    }

}
