package do1phin.mine2021.skyblock.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class SkyblockData {

    private final int section;

    private ProtectionType protectionType;

    private List<UUID> collaborators;

    private final String owner;
    private final UUID uuid;

    public SkyblockData(int section, ProtectionType protectionType, List<UUID> collaborators, UUID uuid, String owner) {
        this.section = section;
        this.protectionType = protectionType;
        this.collaborators = collaborators;

        this.owner = owner;
        this.uuid = uuid;
    }

    public ProtectionType getProtectionType() {
        return protectionType;
    }

    public int getSection() {
        return this.section;
    }

    public void setProtectionType(ProtectionType protectionType) {
        this.protectionType = protectionType;
    }

    public List<UUID> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<UUID> collaborators) {
        this.collaborators = collaborators;
    }

    public String getOwner() {
        return this.owner;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("protection-type", this.protectionType.value);
        jsonObject.put("collaborators", this.collaborators);
        return jsonObject.toString();
    }

    public static SkyblockData fromJSON(int section, String json, UUID uuid, String owner) {
        final JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
            return new SkyblockData(section,
                    ProtectionType.valueOf(((Long) jsonObject.get("protection-type")).intValue()),
                    ((List<String>) jsonObject.get("collaborators")).stream().map(UUID::fromString).collect(Collectors.toList()),
                    uuid, owner);
        } catch (ParseException e) {
            return SkyblockData.getDefault(section, uuid, owner);
        }
    }

    public static SkyblockData getDefault(int section, UUID uuid, String owner) {
        return new SkyblockData(section, ProtectionType.ALLOW_INVITED, new ArrayList<>(), uuid, owner);
    }

}
