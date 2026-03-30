package ru.git.ivanv_lab.db;

import org.postgresql.ds.PGConnectionPoolDataSource;
import ru.git.ivanv_lab.data.PropertyProvider;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.sql.*;

public class SqlWorker {

    private final ConnectionPoolDataSource dataSource;

    public SqlWorker(String username, String password, String dbName) {
        PGConnectionPoolDataSource source = new PGConnectionPoolDataSource();
        source.setServerNames(new String[]{PropertyProvider.getBaseUrl()});
        source.setDatabaseName(dbName);
        source.setUser(username);
        source.setPassword(password);
        source.setPortNumbers(new int[]{5432});

        dataSource = source;
    }

    private Connection getConnection() {
        try {
            PooledConnection pooledConnection = dataSource.getPooledConnection();
            return pooledConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet query(String query) {
        try {
            Statement statement = getConnection().createStatement();
            try {
                return statement.executeQuery(query);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void executeQueryNonResult(String query) {
        try (Statement statement = getConnection().createStatement()) {
            int res = statement.executeUpdate(query);
            if (res == 0) throw new SQLException("Запрос выполнен неудачно");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
