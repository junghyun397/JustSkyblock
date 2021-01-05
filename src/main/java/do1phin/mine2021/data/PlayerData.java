package do1phin.mine2021.data;

public class PlayerData {

    private final String name;
    private final String ip;
    private final long uuid;

    private final PlayerGroup playerGroup;
    private final int section;

    private PlayerStatus playerStatus;

    public PlayerData(String name, String ip, long uuid, PlayerGroup playerGroup, int section) {
        this.name = name;
        this.ip = ip;
        this.uuid = uuid;

        this.playerGroup = playerGroup;
        this.section = section;

        this.playerStatus = PlayerStatus.NEED_REGISTER;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public long getUuid() {
        return uuid;
    }

    public PlayerGroup getPlayerGroup() {
        return playerGroup;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public int getSection() {
        return section;
    }

}
