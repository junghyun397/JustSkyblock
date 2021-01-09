package do1phin.mine2021.skyblock;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.skyblock.data.ProtectionType;
import do1phin.mine2021.skyblock.data.SkyblockData;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.utils.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SkyBlockAgent {

    private final ServerAgent serverAgent;
    private final MessageAgent messageAgent;
    private final DatabaseAgent databaseAgent;

    private final int sectionDistance;

    private final int[][][] defaultIslandShape;
    private final List<Tuple<Integer, Integer, Integer>> defaultItemList;

    private final Map<Integer, SkyblockData> skyblockDataMap = new HashMap<>();

    private Level mainLevel = null;

    public SkyBlockAgent(ServerAgent serverAgent, DatabaseAgent databaseAgent, MessageAgent messageAgent, Config config) {
        this.serverAgent = serverAgent;
        this.messageAgent = messageAgent;
        this.databaseAgent = databaseAgent;

        this.sectionDistance = config.getPluginConfig().getInt("skyblock.distance");

        this.defaultIslandShape = config.parseSkyblockDefaultIslandShape();
        this.defaultItemList = config.parseSkyblockDefaultItemList();
    }

    public int getSkyblockSectionByUUID(String uuid) {
        Optional<PlayerData> playerData = this.serverAgent.getPlayerData(uuid);
        if (playerData.isPresent()) return playerData.get().getSkyblockData().getSection();

        playerData = this.databaseAgent.getPlayerData(uuid);
        return playerData.map(data -> data.getSkyblockData().getSection()).orElse(0);
    }

    public int getSkyblockSectionByPosition(Position position) {
        return 0;
    }

    public void registerSkyblockData(PlayerData playerData) {
        this.skyblockDataMap.put(playerData.getSkyblockData().getSection(), playerData.getSkyblockData());
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
        Position islandSpawnPosition = this.getSkyblockSpawnPosition(playerData.getSkyblockData().getSection());

        playerData.getPlayer().setSpawn(islandSpawnPosition);
        this.generateNewSkyblock(playerData);
        this.giveDefaultSkyblockItem(playerData);
        playerData.getPlayer().teleport(islandSpawnPosition);
    }

    private void generateNewSkyblock(PlayerData playerData) {
        // TODO: 스카이블록 생성 코드 작업
    }

    private void giveDefaultSkyblockItem(PlayerData playerData) {
        for (Tuple<Integer, Integer, Integer> item: this.defaultItemList)
            playerData.getPlayer().getInventory().addItem(new Item(item.a, item.b, item.c));
    }

    public void teleportPlayerToSkyblock(Player player, String destinationName, String destinationUUID) {
        int section = this.getSkyblockSectionByUUID(destinationUUID);
        Optional<SkyblockData> skyblockData = this.getSkyblockData(section);
        if (!skyblockData.isPresent()) {
            Optional<PlayerData> playerData = this.databaseAgent.getPlayerData(destinationUUID);
            playerData.ifPresent(this::registerSkyblockData);
        }

        player.teleport(this.getSkyblockSpawnPosition(section));
        this.messageAgent.sendMessage(player, "message.skyblock.teleport-succeed",
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

    public boolean canPlayerModifyBlock(Player player, double blockX, double blockZ) {
        // TODO: 블록 보호 수준 관리
        return false;
    }

    public Position getSkyblockSpawnPosition(int section) {
        return new Position(this.sectionDistance * 16 * section, 65, 0);
    }

    void setMainLevel(Level level) {
        this.mainLevel = level;
    }

    Level getMainLevel() {
        return this.mainLevel;
    }

}
