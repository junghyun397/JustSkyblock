package do1phin.mine2021.ui;

import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;

public class MessageAgent {

    private final ServerAgent serverAgent;

    private final String prefixInfo;
    private final String prefixNotice;

    public MessageAgent(ServerAgent serverAgent, Config config) {
        this.serverAgent = serverAgent;

        this.prefixInfo = config.getPluginConfig().getString("prefix.info") + " ";
        this.prefixNotice = config.getPluginConfig().getString("prefix.notice") + " ";
    }

    public void sendPlayerFirstJoinBroadcast(PlayerData playerData) {
        this.serverAgent.getServer().broadcastMessage(this.prefixNotice + playerData.getName() + "님이 서버에 처음 접속하셨습니다.");
    }

    public void sendPlayerFirstJoinMessage(PlayerData playerData) {

    }

    public void sendTeleportSucceedMessage(PlayerData playerData) {

    }

    public void sendTeleportFailedMessage(PlayerData playerData) {

    }

    public void sendOregenUpgradeSucceedMessage(PlayerData playerData) {

    }

}
