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
import java.util.UUID;

public class DatabaseAgent {

    private final ServerAgent serverAgent;
    private final DatabaseHelper databaseHelper;

    public DatabaseAgent(ServerAgent serverAgent, DatabaseHelper databaseHelper) {
        this.serverAgent = serverAgent;
        this.databaseHelper = databaseHelper;
    }

    public void disconnect() {
        this.databaseHelper.disconnect();
    }

    public boolean checkPlayerData(UUID uuid) {
        try {
            final PreparedStatement pstmt = this.databaseHelper.getConnection().prepareStatement(
                    "SELECT 0 FROM user_info WHERE uuid=?;");
            pstmt.setString(1, uuid.toString());

            final ResultSet rs = pstmt.executeQuery();
            pstmt.clearParameters();

            if (rs.next()) return true;
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }

        return false;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.getPlayerData(null, uuid);
    }

    public PlayerData getPlayerData(Player player, UUID uuid) {
        try {
            final PreparedStatement pstmt = this.databaseHelper.getConnection().prepareStatement(
                    "SELECT * FROM user_info WHERE uuid=?;");
            pstmt.setString(1, uuid.toString());

            final ResultSet rs = pstmt.executeQuery();
            pstmt.clearParameters();

            if (rs.next())
                return new PlayerData(player, uuid, rs.getString("name"), rs.getString("ip"),
                        rs.getInt("category"),
                        SkyblockData.fromJSON(rs.getInt("section"),
                                rs.getString("island_setting"),
                                uuid, rs.getString("name")),
                        rs.getTimestamp("ban_date"), rs.getString("ban_reason"));
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }

        return null;
    }

    public void registerPlayerData(PlayerData playerData) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));

        try {
            final PreparedStatement pstmt = this.databaseHelper.getConnection().prepareStatement(
                    "INSERT INTO user_info(uuid, name, ip, register_date, island_setting) VALUES (?, ?, ?, ?, ?);");

            pstmt.setString(1, playerData.getUuid().toString());
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
            final PreparedStatement pstmt = this.databaseHelper.getConnection().prepareStatement(
                            "UPDATE user_info SET name=?, category=?, ip=?, island_setting=?, ban_date=?, ban_reason=? WHERE uuid=?;");

            pstmt.setString(1, playerData.getName());
            pstmt.setInt(2, playerData.getPlayerGroup());
            pstmt.setString(3, playerData.getIp());
            pstmt.setString(4, playerData.getSkyblockData().toJSON());
            pstmt.setTimestamp(5, playerData.getBanDate());
            pstmt.setString(6, playerData.getBanReason());

            pstmt.setString(7, playerData.getUuid().toString());

            pstmt.executeUpdate();
            pstmt.clearParameters();
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }
    }

    public Optional<SkyblockData> getSkyblockDataBySection(int section) {
        try {
            final PreparedStatement pstmt = this.databaseHelper.getConnection().prepareStatement(
                    "SELECT island_setting, name, uuid FROM user_info WHERE section=?;");
            pstmt.setInt(1, section);

            final ResultSet rs = pstmt.executeQuery();
            pstmt.clearParameters();

            if (rs.next()) return Optional.of(SkyblockData.fromJSON(section,
                    rs.getString("island_setting"),
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"))
            );
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<UUID> getUUIDByPlayerName(String name) {
        try {
            final PreparedStatement pstmt = this.databaseHelper.getConnection().prepareStatement(
                    "SELECT uuid FROM user_info WHERE name=?;");
            pstmt.setString(1, name);

            final ResultSet rs = pstmt.executeQuery();
            pstmt.clearParameters();

            if (rs.next()) return Optional.of(UUID.fromString(rs.getString("uuid")));
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }

        return Optional.empty();
    }

    public String getPlayerNameByUUID(UUID uuid) {
        try {
            final PreparedStatement pstmt = this.databaseHelper.getConnection().prepareStatement(
                    "SELECT name FROM user_info WHERE uuid=?;");
            pstmt.setString(1, uuid.toString());

            final ResultSet rs = pstmt.executeQuery();
            pstmt.clearParameters();

            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) {
            this.serverAgent.loggerCritical(e.getMessage());
        }

        return null;
    }

    public int getNextSection() {
        return this.databaseHelper.getNextAutoIncrement();
    }

}
