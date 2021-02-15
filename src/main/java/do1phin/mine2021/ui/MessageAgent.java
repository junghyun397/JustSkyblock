package do1phin.mine2021.ui;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.window.FormWindowSimple;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;

public class MessageAgent {

    private final ServerAgent serverAgent;

    private final String prefixInfo;
    private final String prefixNotice;

    private final Config config;

    public MessageAgent(ServerAgent serverAgent, Config config) {
        this.serverAgent = serverAgent;
        this.config = config;

        this.prefixInfo = config.getUIString("prefix.info") + " ";
        this.prefixNotice = config.getUIString("prefix.notice") + " ";
    }

    public void sendMessage(CommandSender player, String key) {
        this.sendMessage(player, key, null, null);
    }

    public void sendMessage(CommandSender player, String key, String[] params, String[] values) {
        player.sendMessage(this.prefixInfo + this.getMessage(key, params, values));
    }

    public void sendCommandMessage(CommandSender player, String key) {
        this.sendCommandMessage(player, key, null, null);
    }

    public void sendCommandMessage(CommandSender player, String key, String[] params, String[] values) {
        Command.broadcastCommandMessage(player, this.getMessage(key, params, values));
    }

    public void sendBroadcast(String key) {
        this.sendBroadcast(key, null, null);
    }

    public void sendBroadcast(String key, String[] params, String[] values) {
        this.serverAgent.getServer().broadcastMessage(this.prefixNotice + this.getMessage(key, params, values));
    }

    public void sendPopup(Player player, String key) {
        this.sendMessage(player, key, null, null);
    }

    public void sendPopup(Player player, String key, String[] params, String[] values) {
        player.sendPopup(this.getMessage(key, params, values));
    }

    public void sendBroadcastPopup(String key) {
        this.sendBroadcast(key, null, null);
    }

    public void sendBroadcastPopup(String key, String[] params, String[] values) {
        String message = this.getMessage(key, params, values);
        for (Player player: this.serverAgent.getServer().getOnlinePlayers().values())
            player.sendPopup(message);
    }

    public String getMessage(String key, String[] params, String[] values) {
        String message = this.config.getUIString(key);
        if (params != null && values != null && params.length == values.length)
            for (int i = 0; i < params.length; i++)
                message = message.replaceAll(params[i], values[i]);

        return message;
    }

    public void sendSimpleForm(Player player, String titleKey, String contentKey) {
        final FormWindowSimple form = new FormWindowSimple(this.config.getUIString(titleKey), this.config.getUIString(contentKey));
        player.showFormWindow(form);
    }

    public String getText(String key) {
        return this.config.getUIString("text." + key);
    }

}
