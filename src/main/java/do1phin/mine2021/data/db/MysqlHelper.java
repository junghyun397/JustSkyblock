package do1phin.mine2021.data.db;

import do1phin.mine2021.data.Config;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlHelper extends RDBSHelper {

    private final String host;
    private final int port;
    private final String user;
    private final String password;

    public MysqlHelper(Config config) {
        this.host = config.getString("db.mysql.host");
        this.port = config.getInt("db.mysql.port");
        this.user = config.getString("db.mysql.user");
        this.password = config.getString("db.mysql.password");
    }

    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + ":" + this.port + "/mine24?serverTimezone=UTC&autoReconnection=true",
                    this.user, this.password);
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

}
