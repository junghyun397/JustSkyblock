package do1phin.mine2021.skyblock;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.skyblock.data.ProtectionType;
import do1phin.mine2021.skyblock.data.SkyblockData;
import do1phin.mine2021.ui.MessageAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SkyBlockAgent {

    private final ServerAgent serverAgent;
    private final MessageAgent messageAgent;
    private final DatabaseAgent databaseAgent;

    private final Map<Integer, SkyblockData> skyblockDataMap = new HashMap<>();

    private final int sectionDistance;
    private final int[][][] defaultIslandShape;

    public SkyBlockAgent(ServerAgent serverAgent, DatabaseAgent databaseAgent, MessageAgent messageAgent, Config config) {
        this.serverAgent = serverAgent;
        this.messageAgent = messageAgent;
        this.databaseAgent = databaseAgent;

        this.sectionDistance = config.getPluginConfig().getInt("skyblock.distance");

        this.defaultIslandShape = config.parseSkyblockDefaultIslandShape();
    }

    public int getSkyblockSectionByUUID(String uuid) {
        Optional<PlayerData> playerData = this.serverAgent.getPlayerData(uuid);
        if (playerData.isPresent()) return playerData.get().getSkyblockData().getSection();

        playerData = this.databaseAgent.getPlayerData(uuid);
        return playerData.map(data -> data.getSkyblockData().getSection()).orElse(0);
    }

    public int getSkyblockSectionByX(int x) {
        return (int) Math.floor((x + 0.001) / (this.sectionDistance * 16)) + 1;
    }

    public Position getSkyblockSpawn(int section) {
        return new Position((this.sectionDistance * 16 * section) - (this.sectionDistance * 8), 65,
                (double) this.sectionDistance * 8);
    }

    public void registerSkyblockData(SkyblockData skyblockData) {
        this.skyblockDataMap.put(skyblockData.getSection(), skyblockData);
    }

    public SkyblockData getSkyblockData(Player player) {
        return this.serverAgent.getPlayerData(player).getSkyblockData();
    }

    public Optional<SkyblockData> getSkyblockData(int section) {
        SkyblockData skyblockData = this.skyblockDataMap.get(section);
        if (skyblockData != null) return Optional.of(skyblockData);
        else return Optional.empty();
    }

    public void purgeSkyblockData(int section) {
        this.skyblockDataMap.remove(section);
    }

    public void registerNewSkyblock(PlayerData playerData) {
        Position islandSpawnPosition = this.getSkyblockSpawn(playerData.getSkyblockData().getSection());

        this.loadIslandDefaultChunk(playerData.getSkyblockData().getSection());
        this.generateDefaultIsland(islandSpawnPosition);
        playerData.getPlayer().setSpawn(islandSpawnPosition);
        playerData.getPlayer().teleport(new Location(islandSpawnPosition.x, islandSpawnPosition.y, islandSpawnPosition.z,
                90, 90, this.getMainLevel()), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    private void generateDefaultIsland(Position islandSpawnPosition) {
        for (int y = 0; y < this.defaultIslandShape.length; y++)
            for (int z = 0; z < this.defaultIslandShape[0].length; z++)
                for (int x = 0; x < this.defaultIslandShape[0][0].length; x++)
                    this.getMainLevel().setBlockIdAt(
                            (int) islandSpawnPosition.x + x - 3,
                            (int) islandSpawnPosition.y - y,
                            (int) islandSpawnPosition.z + z - 3,
                            this.defaultIslandShape[y][z][x]
                    );
    }

    public void teleportPlayerToIsland(Player player, String destinationUUID, String destinationName) {
        int section = this.getSkyblockSectionByUUID(destinationUUID);
        Optional<SkyblockData> skyblockData = this.getSkyblockData(section);
        if (!skyblockData.isPresent()) {
            Optional<PlayerData> playerData = this.databaseAgent.getPlayerData(destinationUUID);
            playerData.ifPresent(playerData1 -> this.registerSkyblockData(playerData1.getSkyblockData()));
        }

        this.loadIslandDefaultChunk(section);
        player.teleport(this.getSkyblockSpawn(section));
        if (player.getUniqueId().toString().equals(destinationUUID))
            this.messageAgent.sendMessage(player, "message.skyblock.teleport-succeed-self");
        else this.messageAgent.sendMessage(player, "message.skyblock.teleport-succeed",
                new String[]{"%player"}, new String[]{destinationName});
    }

    public ProtectionType getSkyblockProtectionType(int section) {
        return this.skyblockDataMap.get(section).getProtectionType();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void updateProtectionType(Player player, ProtectionType protectionType) {
        this.serverAgent.getPlayerData(player.getUniqueId().toString()).get().getSkyblockData().setProtectionType(protectionType);
        this.databaseAgent.updatePlayerData(this.serverAgent.getPlayerData(player.getUniqueId().toString()).get());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void addCollaborator(Player player, String collaborator) {
        PlayerData playerData = this.serverAgent.getPlayerData(player.getUniqueId().toString()).get();
        SkyblockData skyblockData = playerData.getSkyblockData();
        List<String> collaborators = skyblockData.getCollaborators();
        collaborators.add(collaborator);
        skyblockData.setCollaborators(collaborators);
        this.databaseAgent.updatePlayerData(playerData);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void purgeCollaborator(Player player, String collaborator) {
        PlayerData playerData = this.serverAgent.getPlayerData(player.getUniqueId().toString()).get();
        SkyblockData skyblockData = playerData.getSkyblockData();
        List<String> collaborators = skyblockData.getCollaborators();
        collaborators.remove(collaborator);
        skyblockData.setCollaborators(collaborators);
        this.databaseAgent.updatePlayerData(playerData);
    }

    public boolean canPlayerLoadChunk(Player player, int chunkX, int chunkZ) {
        // TODO: 청크 로딩 관리
        return true;
    }

    private void loadIslandDefaultChunk(int section) {
        int chunkX = (section * this.sectionDistance) - (this.sectionDistance / 2);
        int chunkY = this.sectionDistance / 2;
        this.getMainLevel().loadChunk(chunkX, chunkY);
        this.getMainLevel().loadChunk(chunkX - 1, chunkY);
        this.getMainLevel().loadChunk(chunkX, chunkY - 1);
        this.getMainLevel().loadChunk(chunkX - 1, chunkY + 1);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean onPlayerModifyBlock(Player player, int blockX) {
        int section = this.getSkyblockSectionByX(blockX);
        Optional<SkyblockData> skyblockData = this.getSkyblockData(section);
        if (!skyblockData.isPresent()) {
            skyblockData = this.databaseAgent.getSkyblockDataBySection(section);
            if (!skyblockData.isPresent()) return false;
            this.registerSkyblockData(skyblockData.get());
        }

        if (player.getUniqueId().toString().equals(skyblockData.get().getOwner())) return true;

        if (skyblockData.get().getProtectionType() == ProtectionType.ALLOW_ALL) return true;
        else if (skyblockData.get().getProtectionType() == ProtectionType.ALLOW_INVITED) {
            if (skyblockData.get().getCollaborators().contains(player.getUniqueId().toString())) return true;
            this.messageAgent.sendPopup(player, "popup.skyblock.protection-type-warning",
                    new String[]{"%protection-type"},
                    new String[]{this.messageAgent.getText("skyblock.protection-type.allow-invited")});
            return false;
        }
        else {
            this.messageAgent.sendPopup(player, "popup.skyblock.protection-type-warning",
                    new String[]{"%protection-type"},
                    new String[]{this.messageAgent.getText("skyblock.protection-type.allow-only-owner")});
            return false;
        }
    }

    private Level getMainLevel() {
        return this.serverAgent.getMainLevel();
    }

}
