package do1phin.mine2021;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import do1phin.mine2021.event.CommandListener;
import do1phin.mine2021.event.EventListener;

public class ServerManager extends PluginBase {

    private static ServerManager instance;

    public static ServerManager getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.getLogger().info("start loading...");
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getLogger().info("succeed loading.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final CommandListener commandListener = new CommandListener();
        return commandListener.onCommand(sender, cmd, label, args);
    }

}
