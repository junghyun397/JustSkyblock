package do1phin.mine2021.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseHelper {

    protected Connection connection;

    public abstract boolean connect();

    public void disconnect() {
        try {
            this.connection.close();
        } catch (SQLException ignored) {}
    }

    public abstract void initDatabase();

    public abstract int getNextAutoIncrement();

    public Connection getConnection() {
        return this.connection;
    }

}
