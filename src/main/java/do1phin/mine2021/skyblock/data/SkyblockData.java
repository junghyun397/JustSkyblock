package do1phin.mine2021.skyblock.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class SkyblockData {

    private final int section;
    private final String owner;

    private ProtectionType protectionType;

    private List<String> collaborators;

    public SkyblockData(String owner, int section, ProtectionType protectionType, List<String> collaborators) {
        this.owner = owner;
        this.section = section;
        this.protectionType = protectionType;
        this.collaborators = collaborators;
    }

    public ProtectionType getProtectionType() {
        return protectionType;
    }

    public int getSection() {
        return this.section;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setProtectionType(ProtectionType protectionType) {
        this.protectionType = protectionType;
    }

    public List<String> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<String> collaborators) {
        this.collaborators = collaborators;
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("protection-type", this.protectionType.value);
        jsonObject.put("collaborators", this.collaborators);
        return jsonObject.toString();
    }

    public static SkyblockData fromJSON(String owner, int section, String json) {
        final JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
            return new SkyblockData(owner, section, ProtectionType.valueOf(((Long) jsonObject.get("protection-type")).intValue()), (List<String>) jsonObject.get("collaborators"));
        } catch (ParseException e) {
            return SkyblockData.getDefault(owner, section);
        }
    }

    public static SkyblockData getDefault(String owner, int section) {
        return new SkyblockData(owner, section, ProtectionType.ALLOW_INVITED, new ArrayList<>());
    }

}
