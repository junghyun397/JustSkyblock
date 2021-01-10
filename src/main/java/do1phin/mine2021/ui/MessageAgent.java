package do1phin.mine2021.ui;

import cn.nukkit.Player;
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

        this.prefixInfo = config.getString("prefix.info") + " ";
        this.prefixNotice = config.getString("prefix.notice") + " ";
    }

    public void sendMessage(Player player, String key) {
        this.sendMessage(player, key, null, null);
    }

    public void sendMessage(Player player, String key, String[] params, String[] values) {
        String message = this.config.getString(key);
        if (params != null && values != null && params.length == values.length)
            for (int i = 0; i < params.length; i++)
                message = message.replaceAll(params[i], values[i]);
        player.sendMessage(this.prefixInfo + message);
    }

    public void sendBroadcast(String key) {
        this.sendBroadcast(key, null, null);
    }

    public void sendBroadcast(String key, String[] params, String[] values) {
        String message = this.config.getString(key);
        if (params != null && values != null && params.length == values.length)
            for (int i = 0; i < params.length; i++)
                message = message.replaceAll(params[i], values[i]);
        this.serverAgent.getServer().broadcastMessage(this.prefixNotice + message);
    }

    public void sendPopup(Player player, String key) {
        this.sendMessage(player, key, null, null);
    }

    public void sendPopup(Player player, String key, String[] params, String[] values) {
        String message = this.config.getString(key);
        if (params != null && values != null && params.length == values.length)
            for (int i = 0; i < params.length; i++)
                message = message.replaceAll(params[i], values[i]);
        player.sendPopup(message);
    }

    public void sendBroadcastPopup(String key) {
        this.sendBroadcast(key, null, null);
    }

    public void sendBroadcastPopup(String key, String[] params, String[] values) {
        String message = this.config.getString(key);
        if (params != null && values != null && params.length == values.length)
            for (int i = 0; i < params.length; i++)
                message = message.replaceAll(params[i], values[i]);

        for (Player player: this.serverAgent.getServer().getOnlinePlayers().values())
            player.sendPopup(message);
    }

    public String getText(String key) {
        return this.config.getString("text." + key);
    }

}
