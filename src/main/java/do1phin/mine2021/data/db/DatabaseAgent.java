package do1phin.mine2021.data.db;

import cn.nukkit.Player;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.skyblock.data.SkyblockData;

import java.sql.*;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Optional;
import java.util.TimeZone;

public class DatabaseAgent {

    private final ServerAgent serverAgent;
    private final RDBSHelper RDBSHelper;

    public DatabaseAgent(ServerAgent serverAgent, RDBSHelper RDBSHelper) {
        this.serverAgent = serverAgent;
        this.RDBSHelper = RDBSHelper;
    }

    public Optional<PlayerData> getPlayerData(String uuid) {
        return this.getPlayerData(null, uuid);
    }

    public Optional<PlayerData> getPlayerData(Player player, String uuid) {
        try {
            final PreparedStatement pstmt = this.RDBSHelper.getConnection().prepareStatement(
                    "SELECT * FROM user_info WHERE uuid=?;");
            pstmt.setString(1, uuid);

            ResultSet rs = pstmt.executeQuery();
            pstmt.clearParameters();

            if (rs.next())
                return Optional.of(new PlayerData(player, uuid, rs.getString("name"), rs.getString("ip"),
                        rs.getInt("category"),
                        SkyblockData.fromJSON(rs.getInt("section"),
                                rs.getString("island_setting"),
                                uuid, rs.getString("name")),
                        rs.getTimestamp("ban_date")));
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }

        return Optional.empty();
    }

    public void registerPlayerData(PlayerData playerData) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));

        try {
            final PreparedStatement pstmt = this.RDBSHelper.getConnection().prepareStatement(
                    "INSERT INTO user_info(uuid, name, ip, register_date, island_setting) VALUES (?, ?, ?, ?, ?);");

            pstmt.setString(1, playerData.getUuid());
            pstmt.setString(2, playerData.getName());
            pstmt.setString(3, playerData.getIp());
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()), calendar);
            pstmt.setString(5, playerData.getSkyblockData().toJSON());

            pstmt.execute();
            pstmt.clearParameters();
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }
    }

    public void updatePlayerData(PlayerData playerData) {
        try {
            final PreparedStatement pstmt = this.RDBSHelper.getConnection().prepareStatement(
                            "UPDATE user_info SET name=?, category=?, ip=?, island_setting=?, ban_date=? WHERE uuid=?;");

            pstmt.setString(1, playerData.getName());
            pstmt.setInt(2, playerData.getPlayerCategory());
            pstmt.setString(3, playerData.getIp());
            pstmt.setString(4, playerData.getSkyblockData().toJSON());
            pstmt.setTimestamp(5, playerData.getBanDate());

            pstmt.setString(6, playerData.getUuid());

            pstmt.executeUpdate();
            pstmt.clearParameters();
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }
    }

    public Optional<SkyblockData> getSkyblockDataBySection(int section) {
        try {
            final PreparedStatement pstmt = this.RDBSHelper.getConnection().prepareStatement(
                    "SELECT island_setting, uuid FROM user_info WHERE section=?;");
            pstmt.setInt(1, section);

            ResultSet rs = pstmt.executeQuery();
            pstmt.clearParameters();

            if (rs.next()) return Optional.of(SkyblockData.fromJSON(section,
                    rs.getString("island_setting"),
                    rs.getString("uuid"),
                    rs.getString("name"))
            );
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<String> getUUIDByPlayerName(String name) {
        try {
            final PreparedStatement pstmt = this.RDBSHelper.getConnection().prepareStatement(
                    "SELECT uuid FROM user_info WHERE name=?;");
            pstmt.setString(1, name);

            ResultSet rs = pstmt.executeQuery();
            pstmt.clearParameters();

            if (rs.next()) return Optional.of(rs.getString("uuid"));
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<String> getPlayerNameByUUID(String uuid) {
        try {
            final PreparedStatement pstmt = this.RDBSHelper.getConnection().prepareStatement(
                    "SELECT name FROM user_info WHERE uuid=?;");
            pstmt.setString(1, uuid);

            ResultSet rs = pstmt.executeQuery();
            pstmt.clearParameters();

            if (rs.next()) return Optional.of(rs.getString("name"));
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }

        return Optional.empty();
    }

    public int getNextSection() {
        try {
            final Statement stmt = this.RDBSHelper.getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery("SHOW TABLE STATUS LIKE 'user_info'");

            if (resultSet.next()) return resultSet.getInt("Auto_increment");
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }

        return 0;
    }

}
