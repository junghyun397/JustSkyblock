package do1phin.mine2021.database;

import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;

import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MysqlDatabaseHelper extends DatabaseHelper {

    private final String host;
    private final String database;
    private final int port;
    private final String user;
    private final String password;

    public MysqlDatabaseHelper(ServerAgent serverAgent, Config config) {
        this.host = config.getDatabaseConfig().getString("database.mysql.host");
        this.database = config.getDatabaseConfig().getString("database.mysql.database");
        this.port = config.getDatabaseConfig().getInt("database.mysql.port");
        this.user = config.getDatabaseConfig().getString("database.mysql.user");
        this.password = config.getDatabaseConfig().getString("database.mysql.password");
    }

    @Override
    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?serverTimezone=UTC" +
                            "&autoReconnection=true" +
                            "&useUnicode=true" +
                            "&characterEncoding=UTF-8",
                    this.user, this.password);
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("SqlResolve")
    @Override
    public void initDatabase() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::validateConnection, 1, 1, TimeUnit.MINUTES);

        try {
            final Statement stmt = this.connection.createStatement();

            if (stmt.executeQuery("SHOW TABLES LIKE 'user_info';").next()) return;

            stmt.execute("CREATE TABLE user_info ( " +
                    "uuid text NOT NULL, " +
                    "name text NOT NULL, " +
                    "category int(11) NOT NULL DEFAULT '0', " +
                    "ip text NOT NULL, " +
                    "date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "register_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "section int(11) NOT NULL, " +
                    "island_setting mediumtext NOT NULL, " +
                    "ban_date timestamp NULL DEFAULT NULL, " +
                    "ban_reason mediumtext " +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

            stmt.execute("ALTER TABLE `user_info` " +
                    "ADD PRIMARY KEY (`uuid`(32)), " +
                    "ADD KEY `section` (`section`);");

            stmt.execute("ALTER TABLE `user_info` " +
                    "MODIFY `section` int(11) NOT NULL AUTO_INCREMENT;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("SqlResolve")
    @Override
    public int getNextAutoIncrement() {
        try {
            final PreparedStatement pstmt = this.connection.prepareStatement("SHOW TABLE STATUS LIKE 'user_info'");
            final ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) return resultSet.getInt("Auto_increment");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void validateConnection() {
        try {
            final PreparedStatement pstmt = this.connection.prepareStatement("SELECT 1");
            if (pstmt.executeQuery().next()) return;
        } catch (Exception ignored) {}
        this.disconnect();
        this.connect();
    }

}
