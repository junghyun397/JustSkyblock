package do1phin.mine2021.data;

import cn.nukkit.Player;
import do1phin.mine2021.skyblock.data.SkyblockData;

public class PlayerData {

    private final Player player;
    private final boolean online;

    private final String uuid;
    private final String name;
    private final String ip;

    private final int playerCategory;
    private final int section;
    private final SkyblockData skyblockData;

    private final long banDate;

    public PlayerData(Player player, String uuid, String name, String ip, int playerCategory, int section, SkyblockData skyblockData, long banDate) {
        this.player = player;
        this.online = player != null;

        this.uuid = uuid;
        this.name = name;
        this.ip = ip;

        this.playerCategory = playerCategory;
        this.section = section;
        this.skyblockData = skyblockData;

        this.banDate = banDate;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isOnline() {
        return this.online;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPlayerCategory() {
        return this.playerCategory;
    }

    public int getSection() {
        return this.section;
    }

    public SkyblockData getSkyblockData() {
        return this.skyblockData;
    }

    public long getBanDate() {
        return this.banDate;
    }

}
