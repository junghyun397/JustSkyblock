package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.ui.MessageAgent;

import java.util.List;

public class InviteListCommand extends SkyblockCommand {

    private final DatabaseAgent databaseAgent;

    public InviteListCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, SkyBlockAgent skyBlockAgent, DatabaseAgent databaseAgent) {
        super(config.getString("command.skyblock.invite-list-command.command"),
                config.getString("command.skyblock.invite-list-command.description"),
                config.getString("command.skyblock.invite-list-command.usage"),
                new CommandParameter[]{},
                serverAgent, messageAgent, config, skyBlockAgent);

        this.databaseAgent = databaseAgent;
    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] ignored_) {
        if (!(commandSender instanceof Player) || !commandSender.hasPermission(this.getPermission())) return false;

        List<String> collaborators = this.skyBlockAgent.getSkyblockData((Player) commandSender).getCollaborators();

        if (collaborators.size() == 0)
            this.messageAgent.sendMessage((Player) commandSender, "command.skyblock.invite-list-command.show-invited-list-empty");
        else {
            StringBuffer invitedList = new StringBuffer();
            collaborators.forEach(uuid -> invitedList.append(this.databaseAgent.getPlayerNameByUUID(uuid).orElse("ERROR")));
            this.messageAgent.sendMessage((Player) commandSender, "command.skyblock.invite-list-command.show-invited-list",
                    new String[]{"%invited-list"}, new String[]{invitedList.toString()});
        }

        return true;
    }

}