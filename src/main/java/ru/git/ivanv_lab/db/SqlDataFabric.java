package ru.git.ivanv_lab.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlDataFabric {
    private final ThreadLocal<SqlWorker> workerThreadLocal;

    public SqlDataFabric(SqlWorker worker){
        workerThreadLocal=ThreadLocal.withInitial(()->worker);
    }

    public int getTransportId(String transportName){
        int transportId=0;
        try (ResultSet resultSet = query(String.format("SELECT id from transports where name='%s'", transportName))) {

            assertTrue(resultSet.next());
            transportId=resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return transportId;
    }

    public long getPartnerId(String partnerName){
        long partnerId=0;
        try(ResultSet resultSet = query(String.format("SELECT id from partners where name = '%s'",partnerName))){

            if(resultSet.next())
                partnerId = resultSet.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return partnerId;
    }

    public ResultSet query(String query) {
        try {
            Statement statement = workerThreadLocal
                    .get().getConnection().createStatement();
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
        try (Statement statement = workerThreadLocal
                .get().getConnection().createStatement()) {
            int res = statement.executeUpdate(query);
            if (res == 0) throw new SQLException("Запрос выполнен неудачно");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
