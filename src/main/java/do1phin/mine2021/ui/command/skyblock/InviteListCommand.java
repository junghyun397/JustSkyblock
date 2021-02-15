package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.database.DatabaseAgent;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.ui.MessageAgent;

import java.util.List;
import java.util.UUID;

public class InviteListCommand extends SkyblockCommand {

    private final DatabaseAgent databaseAgent;

    public InviteListCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, SkyBlockAgent skyBlockAgent, DatabaseAgent databaseAgent) {
        super(config.getUIString("command.skyblock.invite-list-command.command"),
                config.getUIString("command.skyblock.invite-list-command.description"),
                config.getUIString("command.skyblock.invite-list-command.usage"),
                new CommandParameter[]{},
                serverAgent, messageAgent, skyBlockAgent);

        this.databaseAgent = databaseAgent;
    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] ignored_) {
        if (!this.checkExecutable(commandSender)) return false;

        final List<UUID> collaborators = this.skyBlockAgent.getSkyblockData((Player) commandSender).getCollaborators();

        if (collaborators.size() == 0)
            this.messageAgent.sendMessage(commandSender, "command.skyblock.invite-list-command.show-invited-list-empty");
        else {
            final StringBuffer invitedList = new StringBuffer();
            collaborators.forEach(uuid -> invitedList.append(this.databaseAgent.getPlayerNameByUUID(uuid)).append(" "));
            this.messageAgent.sendMessage(commandSender, "command.skyblock.invite-list-command.show-invited-list",
                    new String[]{"%invited-list"}, new String[]{invitedList.toString()});
        }

        return true;
    }

}
