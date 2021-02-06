package do1phin.mine2021.skyblock;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.db.DatabaseAgent;
import do1phin.mine2021.skyblock.data.ProtectionType;
import do1phin.mine2021.skyblock.data.SkyblockData;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.utils.TimerWrapper;

import java.util.*;

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

        this.sectionDistance = config.getSkyblockConfig().getInt("skyblock.distance");

        this.defaultIslandShape = config.parseSkyblockDefaultIslandShape();
    }

    public int getSkyblockSectionByUUID(UUID uuid) {
        return this.serverAgent.getPlayerData(uuid).map(data -> data.getSkyblockData().getSection())
                .orElseGet(() -> this.databaseAgent.getPlayerData(uuid).getSkyblockData().getSection());
    }

    public int getSkyblockSectionByX(int x) {
        return (int) Math.floor((x + 0.001) / (this.sectionDistance * 16)) + 1;
    }

    public Position getSkyblockSpawn(int section) {
        return new Position((this.sectionDistance * 16 * section) - (this.sectionDistance * 8), 65,
                (double) this.sectionDistance * 8, this.getMainLevel());
    }

    public void registerSkyblockData(SkyblockData skyblockData) {
        this.skyblockDataMap.put(skyblockData.getSection(), skyblockData);
    }

    public SkyblockData getSkyblockData(Player player) {
        return this.serverAgent.getPlayerData(player).getSkyblockData();
    }

    public Optional<SkyblockData> getSkyblockData(int section) {
        return Optional.ofNullable(this.skyblockDataMap.get(section));
    }

    public void purgeSkyblockData(int section) {
        this.skyblockDataMap.remove(section);
    }

    public void registerNewSkyblock(PlayerData playerData, boolean enableTeleport2Island) {
        final Position islandSpawnPosition = this.getSkyblockSpawn(playerData.getSkyblockData().getSection());

        this.loadIslandDefaultChunk(playerData.getSkyblockData().getSection());
        this.generateDefaultIsland(islandSpawnPosition);

        if (enableTeleport2Island)
            TimerWrapper.schedule(() -> playerData.getPlayer().teleport(islandSpawnPosition), 500);
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

    int findFirstRespawnSection(Player player) {
        if (this.databaseAgent.checkPlayerData(player.getUniqueId()))
            return this.getSkyblockSectionByUUID(player.getUniqueId());
        else
            return this.databaseAgent.getNextSection();
    }

    public void teleportPlayerToIsland(Player player, SkyblockData skyblockData) {
        if (!this.getSkyblockData(skyblockData.getSection()).isPresent())
            this.registerSkyblockData(this.databaseAgent.getPlayerData(skyblockData.getOwnerUUID()).getSkyblockData());

        this.loadIslandDefaultChunk(skyblockData.getSection());
        player.teleport(this.findSafeSpawn(this.getSkyblockSpawn(skyblockData.getSection())).orElseGet(() ->
                this.generateSafeSpawn(skyblockData.getSection())));

        if (player.getUniqueId().equals(skyblockData.getOwnerUUID()))
            this.messageAgent.sendMessage(player, "message.skyblock.teleport-succeed-self");
        else {
            this.messageAgent.sendMessage(player, "message.skyblock.teleport-succeed",
                    new String[]{"%player"}, new String[]{skyblockData.getOwnerName()});

            this.serverAgent.getPlayerData(skyblockData.getOwnerUUID()).ifPresent(playerData ->
                    this.messageAgent.sendMessage(playerData.getPlayer(), "message.skyblock.teleport-incoming",
                            new String[]{"%player"}, new String[]{player.getName()}));
        }
    }

    Optional<Position> findSafeSpawn(Position position) {
        final int x = (int) position.x;
        final int y = (int) position.y;
        final int z = (int) position.z;

        if (this.getMainLevel().getBlock(x, y - 1, z).getId() != 0)
            return Optional.of(position);

        for (int dy = 255; dy > 0; dy--)
            if (this.getMainLevel().getBlock(x, dy, (int) position.z).getId() != 0)
                return Optional.of(new Position(x, dy + 1, z, this.getMainLevel()));

        return Optional.empty();
    }

    Position generateSafeSpawn(int section) {
        final Position safeSpawn = this.getSkyblockSpawn(section);

        this.getMainLevel().setBlockIdAt((int) safeSpawn.x, (int) safeSpawn.y - 1, (int) safeSpawn.z, 1);
        this.getMainLevel().setBlockIdAt((int) safeSpawn.x, (int) safeSpawn.y - 1, (int) safeSpawn.z - 1, 1);
        this.getMainLevel().setBlockIdAt((int) safeSpawn.x - 1, (int) safeSpawn.y - 1, (int) safeSpawn.z - 1, 1);
        this.getMainLevel().setBlockIdAt((int) safeSpawn.x - 1, (int) safeSpawn.y - 1, (int) safeSpawn.z, 1);

        return safeSpawn;
    }

    boolean onPlayerModifyBlock(Player player, int blockX) {
        final int section = this.getSkyblockSectionByX(blockX);

        final SkyblockData skyblockData = this.getSkyblockData(section)
                .orElseGet(() -> this.databaseAgent.getSkyblockDataBySection(section).map(skyblockData1 -> {
                    this.registerSkyblockData(skyblockData1);
                    return skyblockData1;
                }).orElseGet(() -> SkyblockData.getErrorDummy(section)));

        if (player.getUniqueId().equals(skyblockData.getOwnerUUID())) return true;

        if (skyblockData.getProtectionType() == ProtectionType.ALLOW_ALL) return true;
        else if (skyblockData.getProtectionType() == ProtectionType.ALLOW_INVITED) {
            if (skyblockData.getCollaborators().contains(player.getUniqueId())) return true;
            this.messageAgent.sendPopup(player, "popup.skyblock.protection-type-warning",
                    new String[]{"%player", "%protection-type"},
                    new String[]{skyblockData.getOwnerName(), this.messageAgent.getText("skyblock.protection-type.allow-invited")});
            return false;
        }
        else {
            this.messageAgent.sendPopup(player, "popup.skyblock.protection-type-warning",
                    new String[]{"%player", "%protection-type"},
                    new String[]{skyblockData.getOwnerName(), this.messageAgent.getText("skyblock.protection-type.allow-only-owner")});
            return false;
        }
    }

    public ProtectionType getSkyblockProtectionType(int section) {
        return this.skyblockDataMap.get(section).getProtectionType();
    }

    public void updateProtectionType(Player player, ProtectionType protectionType) {
        final PlayerData playerData = this.serverAgent.getPlayerData(player);
        playerData.getSkyblockData().setProtectionType(protectionType);
        this.databaseAgent.updatePlayerData(playerData);
    }

    public ProtectionType getSkyblockLockType(int section) {
        return this.getSkyblockData(section).map(SkyblockData::getLockType).orElseGet(() ->
                        this.databaseAgent.getSkyblockDataBySection(section)
                                .map(SkyblockData::getLockType).orElse(ProtectionType.ALLOW_ONLY_OWNER));
    }

    public void updateLockType(Player player, ProtectionType lockType) {
        final PlayerData playerData = this.serverAgent.getPlayerData(player);
        playerData.getSkyblockData().setLockType(lockType);
        this.databaseAgent.updatePlayerData(playerData);
    }

    public void addCollaborator(Player player, UUID collaborator) {
        final PlayerData playerData = this.serverAgent.getPlayerData(player);
        final SkyblockData skyblockData = playerData.getSkyblockData();
        final List<UUID> collaborators = skyblockData.getCollaborators();
        collaborators.add(collaborator);
        this.databaseAgent.updatePlayerData(playerData);
    }

    public void purgeCollaborator(Player player, UUID collaborator) {
        final PlayerData playerData = this.serverAgent.getPlayerData(player);
        final SkyblockData skyblockData = playerData.getSkyblockData();
        final List<UUID> collaborators = skyblockData.getCollaborators();
        collaborators.remove(collaborator);
        this.databaseAgent.updatePlayerData(playerData);
    }

    boolean canPlayerLoadChunk(Player player, int chunkX, int chunkZ) {
        final int section = this.getSkyblockSectionByX((int) Math.round(player.getPosition().x));
        return this.sectionDistance > chunkZ && chunkZ >= 0
                && section * this.sectionDistance > chunkX && chunkX >= (section - 1) * this.sectionDistance;
    }

    private void loadIslandDefaultChunk(int section) {
        final int chunkX = (section * this.sectionDistance) - (this.sectionDistance / 2);
        final int chunkY = this.sectionDistance / 2;
        this.getMainLevel().loadChunk(chunkX, chunkY);
        this.getMainLevel().loadChunk(chunkX - 1, chunkY);
        this.getMainLevel().loadChunk(chunkX, chunkY - 1);
        this.getMainLevel().loadChunk(chunkX - 1, chunkY + 1);
    }

    private int unloadIslandChunk(int section) {
        section = section - 1;
        int unloadedChunks = 0;
        for (int x = 0; x < this.sectionDistance; x++)
            for (int z = 0; z < this.sectionDistance; z++)
                if (this.getMainLevel().unloadChunk(section * this.sectionDistance +  x + x, z)) unloadedChunks++;

        return unloadedChunks;
    }

    Level getMainLevel() {
        return this.serverAgent.getMainLevel();
    }

}
