package ru.git.ivanv_lab.db;

import ru.git.ivanv_lab.model.Transport;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlDataFabric {
    private final ThreadLocal<SqlWorker> workerThreadLocal;

    public SqlDataFabric(SqlWorker worker){
        workerThreadLocal=ThreadLocal.withInitial(()->worker);
    }

    public int getTransportId(String transportName){
        int transportId=0;
        try (ResultSet resultSet = workerThreadLocal.get()
                .query(String.format("SELECT id from transports where name='%s'", transportName))) {

            assertTrue(resultSet.next());
            transportId=resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return transportId;
    }

    public long getPartnerId(String partnerName){
        long partnerId=0;
        try(ResultSet resultSet = workerThreadLocal.get()
                .query(String.format("SELECT id from partners where name = '%s'",partnerName))){

            if(resultSet.next())
                partnerId = resultSet.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return partnerId;
    }
}
