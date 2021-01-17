package do1phin.mine2021.data.db;

import do1phin.mine2021.data.Config;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlHelper extends RDBSHelper {

    private final String host;
    private final String database;
    private final int port;
    private final String user;
    private final String password;

    public MysqlHelper(Config config) {
        this.host = config.getDatabaseConfig().getString("database.mysql.host");
        this.database = config.getDatabaseConfig().getString("database.mysql.database");
        this.port = config.getDatabaseConfig().getInt("database.mysql.port");
        this.user = config.getDatabaseConfig().getString("database.mysql.user");
        this.password = config.getDatabaseConfig().getString("database.mysql.password");
    }

    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?serverTimezone=UTC&autoReconnection=true",
                    this.user, this.password);
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

}
