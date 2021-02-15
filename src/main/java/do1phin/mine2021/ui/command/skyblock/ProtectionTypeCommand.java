package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.skyblock.data.ProtectionType;
import do1phin.mine2021.ui.MessageAgent;

public class ProtectionTypeCommand extends SkyblockCommand {

    public ProtectionTypeCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, SkyBlockAgent skyBlockAgent) {
        super(config.getUIString("command.skyblock.protection-type-command.command"),
                config.getUIString("command.skyblock.protection-type-command.description"),
                config.getUIString("command.skyblock.protection-type-command.usage"),
                "protection",
                new CommandParameter[]{
                        CommandParameter.newEnum(config.getUIString("command.skyblock.protection-type-command.parameter.protection-type"),
                                false, new String[]{messageAgent.getText("skyblock.protection-type.allow-only-owner"),
                                        messageAgent.getText("skyblock.protection-type.allow-invited"),
                                        messageAgent.getText("skyblock.protection-type.allow-all")})
                },
                serverAgent, messageAgent, skyBlockAgent);
    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!this.checkExecutable(commandSender)) return false;

        if (args.length == 0) {
            this.messageAgent.sendMessage(commandSender, "command.skyblock.protection-type-command.format-error");
            return false;
        }

        final ProtectionType protectionType;
        if (args[0].equals(this.messageAgent.getText("skyblock.protection-type.allow-only-owner")))
            protectionType = ProtectionType.ALLOW_ONLY_OWNER;
        else if (args[0].equals(this.messageAgent.getText("skyblock.protection-type.allow-invited")))
            protectionType = ProtectionType.ALLOW_INVITED;
        else if (args[0].equals(this.messageAgent.getText("skyblock.protection-type.allow-all")))
            protectionType = ProtectionType.ALLOW_ALL;
        else {
            this.messageAgent.sendMessage(commandSender, "command.skyblock.protection-type-command.format-error");
            return false;
        }

        this.skyBlockAgent.updateProtectionType((Player) commandSender, protectionType);

        this.messageAgent.sendMessage(commandSender, "message.skyblock.protection-type-updated",
                new String[]{"%protection-type"}, new String[]{args[0]});

        return true;
    }

}
