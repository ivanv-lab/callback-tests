package ru.git.ivanv_lab.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.git.ivanv_lab.data.PropertyProvider;

import java.sql.*;

public class SqlWorker {
    private static final Logger log = LoggerFactory.getLogger(SqlWorker.class);
    private static final String ORACLE_JDBC_URL = "jdbc:oracle:thin:@%s:1521:wsoft";
    private static final String POSTGRES_JDBC_URL = "jdbc:postgresql://%s:5432/";

    private final Connection connection;

    public SqlWorker(String username, String password, String dbName) {
        Connection tempConn;

        try {
            tempConn = tryOracleConnection(username, password, dbName);
        } catch (SQLException oracleEx) {
            try {
                tempConn = tryPostgresConnection(username, password, dbName);
            } catch (SQLException postgresEx) {
                throw new RuntimeException("Ошибка подключения к БД", postgresEx);
            }
        }

        this.connection = tempConn;
    }

    private Connection tryOracleConnection(String username, String password, String dbName) throws SQLException {
        String url = String.format(ORACLE_JDBC_URL, PropertyProvider.getBaseUrl());
        return DriverManager.getConnection(url, username, password);
    }

    private Connection tryPostgresConnection(String username, String password, String dbName) throws SQLException {
        log.warn(dbName + username + password);
        String url = String.format(POSTGRES_JDBC_URL, PropertyProvider.getBaseUrl()) + dbName;
        return DriverManager.getConnection(url, username, password);
    }

    public ResultSet query(String query) {
        try {
            Statement statement = connection.createStatement();
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
        try (Statement statement = connection.createStatement()) {
            int res = statement.executeUpdate(query);
            if (res == 0) throw new SQLException("Запрос выполнен неудачно");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
