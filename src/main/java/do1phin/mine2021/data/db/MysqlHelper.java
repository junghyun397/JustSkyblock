package do1phin.mine2021.data.db;

import do1phin.mine2021.data.Config;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlHelper extends RDBSHelper {

    public MysqlHelper(Config config) {
        super(config);
    }

    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.config.dbHost + ":" + this.config.dbPort + "/mine24?serverTimezone=UTC&autoReconnection=true",
                    this.config.dbUser, this.config.dbPassword);
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

}
