package do1phin.mine2021.skyblock.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class SkyblockData {

    private final int section;

    private ProtectionType protectionType;
    private ProtectionType lockType;

    private final List<UUID> collaborators;

    private final String ownerName;
    private final UUID ownerUUID;

    private SkyblockData(int section, ProtectionType protectionType, ProtectionType lockType, List<UUID> collaborators, UUID ownerUUID, String ownerName) {
        this.section = section;
        this.protectionType = protectionType;
        this.lockType = lockType;
        this.collaborators = collaborators;

        this.ownerName = ownerName;
        this.ownerUUID = ownerUUID;
    }

    public int getSection() {
        return this.section;
    }

    public ProtectionType getProtectionType() {
        return protectionType;
    }

    public void setProtectionType(ProtectionType protectionType) {
        this.protectionType = protectionType;
    }

    public ProtectionType getLockType() {
        return this.lockType;
    }

    public void setLockType(ProtectionType lockType) {
        this.lockType = lockType;
    }

    public List<UUID> getCollaborators() {
        return collaborators;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("protection-type", this.protectionType.value);
        jsonObject.put("lock-type", this.lockType.value);
        jsonObject.put("collaborators", this.collaborators.stream().map(UUID::toString).collect(Collectors.toList()));
        return jsonObject.toString();
    }

    public static SkyblockData fromJSON(int section, String json, UUID uuid, String owner) {
        final JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
            return new SkyblockData(section,
                    ProtectionType.valueOf(((Long) jsonObject.getOrDefault("protection-type", (long) ProtectionType.ALLOW_INVITED.value)).intValue()),
                    ProtectionType.valueOf(((Long) jsonObject.getOrDefault("lock-type", (long) ProtectionType.ALLOW_ALL.value)).intValue()),
                    ((List<String>) jsonObject.get("collaborators")).stream().map(UUID::fromString).collect(Collectors.toList()),
                    uuid, owner);
        } catch (ParseException e) {
            return SkyblockData.getDefault(section, uuid, owner);
        }
    }

    public static SkyblockData getDefault(int section, UUID uuid, String owner) {
        return new SkyblockData(section, ProtectionType.ALLOW_INVITED, ProtectionType.ALLOW_ALL, new ArrayList<>(), uuid, owner);
    }

    public static SkyblockData getErrorDummy(int section) {
        return new SkyblockData(section, ProtectionType.ALLOW_ONLY_OWNER, ProtectionType.ALLOW_ALL, Collections.EMPTY_LIST, new UUID(0, 0), "ERROR");
    }

}
