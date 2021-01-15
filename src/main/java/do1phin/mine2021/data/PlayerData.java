package do1phin.mine2021.data;

import cn.nukkit.Player;
import do1phin.mine2021.skyblock.data.SkyblockData;

import java.sql.Timestamp;
import java.util.UUID;

public class PlayerData {

    private final Player player;
    private final boolean online;

    private final UUID uuid;
    private final String name;
    private final String ip;

    private final int playerCategory;
    private final SkyblockData skyblockData;

    private final Timestamp banDate;

    public PlayerData(Player player, UUID uuid, String name, String ip, int playerCategory, SkyblockData skyblockData, Timestamp banDate) {
        this.player = player;
        this.online = player != null;

        this.uuid = uuid;
        this.name = name;
        this.ip = ip;

        this.playerCategory = playerCategory;
        this.skyblockData = skyblockData;

        this.banDate = banDate;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isOnline() {
        return this.online;
    }

    public UUID getUuid() {
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

    public SkyblockData getSkyblockData() {
        return this.skyblockData;
    }

    public Timestamp getBanDate() {
        return this.banDate;
    }

}
