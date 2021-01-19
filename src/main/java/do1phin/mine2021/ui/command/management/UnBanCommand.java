package do1phin.mine2021.ui.command.management;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.ui.MessageAgent;

import java.util.Optional;
import java.util.UUID;

public class UnBanCommand extends ManagementCommand {

    private final DatabaseAgent databaseAgent;

    public UnBanCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, DatabaseAgent databaseAgent) {
        super(config.getUIString("command.management.unban-command.command"),
                config.getUIString("command.management.unban-command.description"),
                config.getUIString("command.management.unban-command.usage"),
                new CommandParameter[]{
                        CommandParameter.newType(config.getUIString("command.management.unban-command.parameter.player"),
                                false, CommandParamType.TEXT),
                },
                serverAgent, messageAgent);

        this.databaseAgent = databaseAgent;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!this.checkExecutable(commandSender)) return false;

        if (args.length < 1) {
            this.messageAgent.sendMessage(commandSender, "command.management.unban-command.format-error");
            return false;
        }

        final Optional<UUID> targetUUID = this.databaseAgent.getUUIDByPlayerName(args[0]);
        if (targetUUID.isPresent()) {
            final PlayerData playerData = this.databaseAgent.getPlayerData(targetUUID.get()).get();
            if (playerData.getBanDate() != null) {
                playerData.setBanDate(null);
                playerData.setBanReason(null);
                this.databaseAgent.updatePlayerData(playerData);

                this.messageAgent.sendMessage(commandSender, "command.management.unban-command.unban-succeed",
                        new String[]{"%player"}, new String[]{playerData.getName()});
                return true;
            }
        }
        this.messageAgent.sendMessage(commandSender, "command.management.unban-command.unban-failed",
                new String[]{"%player"}, new String[]{args[0]});
        return false;
    }

}
