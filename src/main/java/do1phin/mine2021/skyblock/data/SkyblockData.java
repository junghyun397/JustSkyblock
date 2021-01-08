package do1phin.mine2021.skyblock.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class SkyblockData {

    private int section;

    private ProtectionType protectionType;

    private List<String> collaborators;

    public SkyblockData(int section, ProtectionType protectionType, List<String> collaborators) {
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

    public static SkyblockData fromJSON(int section, String json) {
        final JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
            return new SkyblockData(section, ProtectionType.valueOf(((Long) jsonObject.get("protection-type")).intValue()), (List<String>) jsonObject.get("collaborators"));
        } catch (ParseException e) {
            return SkyblockData.getDefault(section);
        }
    }

    public static SkyblockData getDefault(int section) {
        return new SkyblockData(section, ProtectionType.ALLOW_ONLY_OWNER, new ArrayList<>());
    }

}
