package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.ui.MessageAgent;

import java.util.Optional;
import java.util.UUID;

public class TeleportCommand extends SkyblockCommand {

    private final DatabaseAgent databaseAgent;

    public TeleportCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, SkyBlockAgent skyBlockAgent, DatabaseAgent databaseAgent) {
        super(config.getUIString("command.skyblock.teleport-command.command"),
                config.getUIString("command.skyblock.teleport-command.description"),
                config.getUIString("command.skyblock.teleport-command.usage"),
                new CommandParameter[]{CommandParameter.newType(config.getUIString("command.skyblock.teleport-command.parameter.player"),
                        true, CommandParamType.TARGET)
                },
                serverAgent, messageAgent, skyBlockAgent);

        this.databaseAgent = databaseAgent;
    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!this.checkExecutable(commandSender)) return false;

        if (args.length == 0) {
            this.skyBlockAgent.teleportPlayerToIsland((Player) commandSender, ((Player) commandSender).getUniqueId(), commandSender.getName());
        } else {
            final Optional<UUID> targetUUID = this.databaseAgent.getUUIDByPlayerName(args[0]);
            if (!targetUUID.isPresent()) {
                this.messageAgent.sendMessage(commandSender, "command.skyblock.teleport-command.teleport-failed-playernotfound",
                        new String[]{"%player"}, new String[]{args[0]});
                return false;
            }

            this.skyBlockAgent.teleportPlayerToIsland((Player) commandSender, targetUUID.get(), args[0]);
        }

        return true;
    }

}
