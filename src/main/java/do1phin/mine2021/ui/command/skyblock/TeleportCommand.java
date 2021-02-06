package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.skyblock.data.ProtectionType;
import do1phin.mine2021.skyblock.data.SkyblockData;
import do1phin.mine2021.ui.MessageAgent;

import java.util.Optional;
import java.util.Random;
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

        if (args.length == 0 || args[0].equals("@s")) {
            this.skyBlockAgent.teleportPlayerToIsland((Player) commandSender, this.skyBlockAgent.getSkyblockData((Player) commandSender));
            return true;
        }

        final SkyblockData skyblockData;
        if (args[0].equals("@r")) {
            final Optional<SkyblockData> randomSkyblockData = this.findRandomAllowedIsland();
            if (!randomSkyblockData.isPresent()) return false;
            skyblockData = randomSkyblockData.get();
        } else {
            final Optional<UUID> maybeUUID = this.databaseAgent.getUUIDByPlayerName(args[0]);
            if (!maybeUUID.isPresent()) {
                this.messageAgent.sendMessage(commandSender, "command.skyblock.teleport-command.teleport-failed-playernotfound",
                        new String[]{"%player"}, new String[]{args[0]});
                return false;
            }

            skyblockData = this.databaseAgent.getPlayerData(maybeUUID.get()).getSkyblockData();

            if (skyblockData.getLockType() == ProtectionType.ALLOW_ONLY_OWNER) {
                this.messageAgent.sendMessage(commandSender, "command.skyblock.teleport-command.teleport-failed-locked",
                        new String[]{"%player", "%lock-type"},
                        new String[]{args[0], this.messageAgent.getText("skyblock.lock-type.allow-only-owner")});
                return false;
            } else if (skyblockData.getLockType() == ProtectionType.ALLOW_INVITED
                    && !skyblockData.getCollaborators().contains(((Player) commandSender).getUniqueId())) {
                this.messageAgent.sendMessage(commandSender, "command.skyblock.teleport-command.teleport-failed-locked",
                        new String[]{"%player", "%lock-type"},
                        new String[]{args[0], this.messageAgent.getText("skyblock.lock-type.allow-invited")});
                return false;
            }
        }

        this.skyBlockAgent.teleportPlayerToIsland((Player) commandSender, skyblockData);

        return true;
    }

    private Optional<SkyblockData> findRandomAllowedIsland() {
        int counter = 0;
        while (counter < 1000) {
            final Optional<SkyblockData> targetSkyblockData
                    = this.databaseAgent.getSkyblockDataBySection(new Random().nextInt(this.databaseAgent.getNextSection()))
                    .filter(skyblockData -> skyblockData.getLockType() != ProtectionType.ALLOW_ALL);
            if (targetSkyblockData.isPresent()) return targetSkyblockData;
            counter++;
        }
        return Optional.empty();
    }

}
