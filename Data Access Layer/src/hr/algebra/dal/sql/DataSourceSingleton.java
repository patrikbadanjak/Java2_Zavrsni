package hr.algebra.dal.sql;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;

public class DataSourceSingleton {
    private static final String SERVER_NAME = "localhost";
    private static final String DATABASE_NAME = "Java2_Zavrsni";
    private static final String UID = "sa";
    private static final String PWD = "SQL";

    private DataSourceSingleton() {
    }

    private static DataSource instance;

    public static DataSource getInstance() {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    private static DataSource createInstance() {
        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setServerName(SERVER_NAME);
        dataSource.setDatabaseName(DATABASE_NAME);
        dataSource.setUser(UID);
        dataSource.setPassword(PWD);
        return dataSource;
    }
}
