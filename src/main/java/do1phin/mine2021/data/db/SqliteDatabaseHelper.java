package do1phin.mine2021.data.db;

import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.*;

public class SqliteDatabaseHelper extends DatabaseHelper {

    private final File dbFile;

    public SqliteDatabaseHelper(ServerAgent serverAgent, Config config) {
        this.dbFile = new File(config.getDataFolder() + "/" + config.getDatabaseConfig().getString("database.sqlite.db-file"));
    }

    @Override
    public boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig sqLiteConfig = new SQLiteConfig();
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile.getPath(), sqLiteConfig.toProperties());
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void initDatabase() {
        try {
            final Statement stmt = this.connection.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS user_info ( " +
                    "uuid text NOT NULL, " +
                    "name text NOT NULL, " +
                    "category INT NOT NULL DEFAULT 0, " +
                    "ip text NOT NULL, " +
                    "date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "register_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "section INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "island_setting mediumtext NOT NULL, " +
                    "ban_date timestamp NULL DEFAULT NULL, " +
                    "ban_reason mediumtext " +
                    ");"
            );
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("SqlResolve")
    @Override
    public int getNextAutoIncrement() {
        try {
            final PreparedStatement pstmt = this.connection.prepareStatement("SELECT MAX(section) AS section FROM user_info");
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) return resultSet.getInt("section");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

}