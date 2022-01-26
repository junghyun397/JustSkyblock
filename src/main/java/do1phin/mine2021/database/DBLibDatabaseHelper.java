package do1phin.mine2021.database;

import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;

public class DBLibDatabaseHelper extends MysqlDatabaseHelper {

    public DBLibDatabaseHelper(ServerAgent serverAgent, Config config) {
        super(serverAgent, config);
    }

    @Override
    public boolean connect() {
        // TODO: implement
        return true;
    }

}