package ru.git.ivanv_lab.db;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import org.postgresql.ds.PGConnectionPoolDataSource;
import ru.git.ivanv_lab.data.PropertyProvider;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.sql.*;

public class SqlWorker {

    private final ConnectionPoolDataSource dataSource;

    public SqlWorker(String username, String password, String dbName) {
        if(tryOracleConnection(username, password)){
            try {
                OracleConnectionPoolDataSource source = new OracleConnectionPoolDataSource();
                source.setURL(String.format("jdbc:oracle:thin:@%s:1521:wsoft",PropertyProvider.getDbUrl()));
                source.setDatabaseName("WSOFT");
                source.setUser(username);
                source.setPassword(password);
                source.setPortNumber(1521);

                dataSource = source;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
        } else if (tryPostgresConnection(username, password, dbName)){
            PGConnectionPoolDataSource source = new PGConnectionPoolDataSource();
            source.setServerNames(new String[]{PropertyProvider.getDbUrl()});
            source.setDatabaseName(dbName);
            source.setUser(username);
            source.setPassword(password);
            source.setPortNumbers(new int[]{5432});

            dataSource = source;
        } else {
            throw new RuntimeException("Не удалось подключиться к БД");
        }
    }

    private boolean tryOracleConnection(String username, String password){
        try {
            String url = String.format("jdbc:oracle:thin:@%s:1521:wsoft", PropertyProvider.getDbUrl());
            return DriverManager.getConnection(url, username, password).isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean tryPostgresConnection(String username, String password, String dbName){
        try{
            String url = String.format("jdbc:postgresql://%s:5432/", PropertyProvider.getDbUrl()) + dbName;
            return DriverManager.getConnection(url, username, password).isValid(5);
        } catch (SQLException e){
            return false;
        }
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
