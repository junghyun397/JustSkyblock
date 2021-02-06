package do1phin.mine2021.ui.command.skyblock;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.skyblock.SkyBlockAgent;
import do1phin.mine2021.skyblock.data.ProtectionType;
import do1phin.mine2021.ui.MessageAgent;

public class LockTypeCommand extends SkyblockCommand {

    public LockTypeCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, SkyBlockAgent skyBlockAgent) {
        super(config.getUIString("command.skyblock.lock-type-command.command"),
                config.getUIString("command.skyblock.lock-type-command.description"),
                config.getUIString("command.skyblock.lock-type-command.usage"),
                new CommandParameter[]{
                        CommandParameter.newEnum(config.getUIString("command.skyblock.lock-type-command.parameter.lock-type"),
                                false, new String[]{messageAgent.getText("skyblock.lock-type.allow-only-owner"),
                                        messageAgent.getText("skyblock.lock-type.allow-invited"),
                                        messageAgent.getText("skyblock.lock-type.allow-all")})
                },
                serverAgent, messageAgent, skyBlockAgent);
    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!this.checkExecutable(commandSender)) return false;

        if (args.length == 0) {
            this.messageAgent.sendMessage(commandSender, "command.skyblock.lock-type-command.format-error");
            return false;
        }

        final ProtectionType lockType;
        if (args[0].equals(this.messageAgent.getText("skyblock.protection-type.allow-only-owner")))
            lockType = ProtectionType.ALLOW_ONLY_OWNER;
        else if (args[0].equals(this.messageAgent.getText("skyblock.protection-type.allow-invited")))
            lockType = ProtectionType.ALLOW_INVITED;
        else if (args[0].equals(this.messageAgent.getText("skyblock.protection-type.allow-all")))
            lockType = ProtectionType.ALLOW_ALL;
        else {
            this.messageAgent.sendMessage(commandSender, "command.skyblock.lock-type-command.format-error");
            return false;
        }

        this.skyBlockAgent.updateLockType((Player) commandSender, lockType);

        this.messageAgent.sendMessage(commandSender, "message.skyblock.lock-type-updated",
                new String[]{"%lock-type"}, new String[]{args[0]});

        return true;
    }

}
