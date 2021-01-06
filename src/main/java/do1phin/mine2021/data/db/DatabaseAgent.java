package do1phin.mine2021.data.db;

import cn.nukkit.Player;
import com.sun.istack.internal.Nullable;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.data.PlayerCategory;
import do1phin.mine2021.skyblock.data.SkyblockData;

import java.sql.*;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class DatabaseAgent {

    private final ServerAgent serverAgent;
    private final RDBSHelper RDBSHelper;

    public DatabaseAgent(ServerAgent serverAgent, RDBSHelper RDBSHelper) {
        this.serverAgent = serverAgent;
        this.RDBSHelper = RDBSHelper;
    }

    public PlayerData getPlayerData(String uuid) {
        return this.getPlayerData(null, uuid);
    }

    public PlayerData getPlayerData(@Nullable Player player, String uuid) {
        try {
            final PreparedStatement pstmt = this.RDBSHelper.getConnection().prepareStatement(
                    "SELECT * FROM user_info WHERE uuid=?;");
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();
            pstmt.clearParameters();

            if (rs.next())
                return new PlayerData(player, uuid, rs.getString("name"), rs.getString("ip"),
                        PlayerCategory.values()[rs.getInt("category")],
                        rs.getInt("section"), SkyblockData.fromJSON(rs.getString("island_setting")));
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }
        return null;
    }

    public void registerPlayerData(PlayerData playerData) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));

        try {
            final PreparedStatement pstmt = this.RDBSHelper.getConnection().prepareStatement(
                    "INSERT INTO user_info(uuid, name, ip, register_date) VALUES (?, ?, ?, ?);");

            pstmt.setString(1, playerData.getUuid());
            pstmt.setString(2, playerData.getName());
            pstmt.setString(3, playerData.getIp());
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()), calendar);

            pstmt.execute();
            pstmt.clearParameters();
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }
    }

    public void updatePlayerData(PlayerData playerData) {
        try {
            final PreparedStatement pstmt = this.RDBSHelper.getConnection().prepareStatement(
                            "UPDATE user_info SET name=?, category=?, ip=?, island_setting=?, WHERE uuid=?;");

            pstmt.setString(1, playerData.getName());
            pstmt.setInt(2, playerData.getPlayerGroup().value);
            pstmt.setString(3, playerData.getIp());
            pstmt.setString(4, playerData.getSkyblockData().toJSON());
            pstmt.setString(5, playerData.getUuid());

            pstmt.executeUpdate();
            pstmt.clearParameters();
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }
    }

    public int getCurrentSection() {
        try {
            final Statement stmt = this.RDBSHelper.getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT MAX(section) FROM user_info");

            if (resultSet.next()) return resultSet.getInt("MAX(section)");
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }
        return 0;
    }

}
