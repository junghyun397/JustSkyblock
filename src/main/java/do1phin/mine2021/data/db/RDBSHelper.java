package do1phin.mine2021.data.db;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class RDBSHelper {

    protected Connection connection;

    public boolean connect() {
        return false;
    }

    public void disconnect() {
        try {
            this.connection.close();
        } catch (SQLException ignored) {}
    }

    public Connection getConnection() {
        return this.connection;
    }

}
