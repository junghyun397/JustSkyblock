package do1phin.mine2021.ui.command.management;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.utils.ExceptionWrapper;
import do1phin.mine2021.ux.UXAgent;

import java.util.Arrays;
import java.util.Optional;

public class DebugCommand extends ManagementCommand {

    private final UXAgent uxAgent;

    private enum DebugCommandType {
        GUIDE_BOOK, DEFAULT_ITEM, WELCOME_FORM, NONE
    }

    public DebugCommand(ServerAgent serverAgent, MessageAgent messageAgent, Config config, UXAgent uxAgent) {
        super("debug",
                "debug command",
                "/debug <debug type>",
                "debug",
                new CommandParameter[]{
                        CommandParameter.newEnum("debug type", false,
                                Arrays.stream(DebugCommandType.values()).map(DebugCommandType::toString).toArray(String[]::new)),
                        CommandParameter.newType("target", false, CommandParamType.TARGET)
                },
                serverAgent, messageAgent);

        this.uxAgent = uxAgent;
    }

    @Override
    public boolean execute(CommandSender commandSender, String ignored, String[] args) {
        if (!this.checkExecutable(commandSender)) return false;

        if (args.length < 2) return false;

        if (commandSender instanceof Player) {
            final Player player = Optional.ofNullable(this.serverAgent.getServer().getPlayer(args[1])).orElseGet(() -> (Player) commandSender);
            final DebugCommandType commandType = ExceptionWrapper.getOrDefault(() -> DebugCommandType.valueOf(args[0]), DebugCommandType.NONE);
            switch (commandType) {
                case GUIDE_BOOK:
                    this.uxAgent.giveGuideBook(player, 5);
                    break;
                case DEFAULT_ITEM:
                    this.uxAgent.giveDefaultItems(player);
                    break;
                case WELCOME_FORM:
                    this.messageAgent.sendSimpleForm(player, "form.welcome-form.title", "form.welcome-form.content");
                    break;
            }

            Command.broadcastCommandMessage(commandSender, "debug " + commandType.toString() + " >> " + player.getName());
        }

        return false;
    }
}
