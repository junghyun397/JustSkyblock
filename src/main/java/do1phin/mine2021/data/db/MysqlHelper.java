package do1phin.mine2021.data.db;

import do1phin.mine2021.data.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlHelper {

    private final String host;
    private final int port;
    private final String user;
    private final String password;

    private Connection connection;

    public MysqlHelper(Config config) {
        this.host = config.dbHost;
        this.port = config.dbPort;
        this.user = config.dbUser;
        this.password = config.dbPassword;
    }

    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/mine24?serverTimezone=UTC",
                    user, password);
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {
        try {
            this.connection.close();
        } catch (SQLException ignored) {}
    }

    public Connection getConnection() {
        return connection;
    }
}
