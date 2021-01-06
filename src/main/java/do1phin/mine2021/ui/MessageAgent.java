package do1phin.mine2021.ui;

import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.PlayerCategory;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.utils.Pair;

public class MessageAgent {

    private ServerAgent serverAgent;

    public MessageAgent(ServerAgent serverAgent) {

    }

    public void sendPlayerFirstJoinBroadcast(PlayerData playerData) {
        this.serverAgent.getServer().broadcastMessage("<§e안내§f> " + playerData.getName() + "님이 서버에 처음 접속하셨습니다.");
    }

    public void sendPlayerFirstJoinMessage(PlayerData playerData) {

    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public static Pair<String, String> getNamePair(String name, PlayerCategory playerCategory) {
        switch (playerCategory) {
            case STAFF:
                return new Pair<>("§b[Staff]§f " + name, "§7<<§l§f" + name + "§r§7>>");
            default:
                return new Pair<>("§2[유저]§f " + name, "§7<<§l§f" + name + "§r§7>>");
        }
    }

}
