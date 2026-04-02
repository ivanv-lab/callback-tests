package ru.git.ivanv_lab.db;

import ru.git.ivanv_lab.model.general.Transport;

import java.sql.PreparedStatement;
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

    public long getAccountId(String accountName, long partnerId){
        long accountId=0;
        try(PreparedStatement statement=workerThreadLocal.get().getConnection()
                .prepareStatement("SELECT id from accounts where name = ? and partner_id = ?")){

            statement.setString(1, accountName);
            statement.setLong(2, partnerId);

            ResultSet rs=statement.executeQuery();
            if(rs.next()) accountId=rs.getLong(1);
            rs.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        return accountId;
    }

    public int getProtocolId(String protocolName){
        int protocolId=0;
        try(PreparedStatement statement=workerThreadLocal.get().getConnection()
                .prepareStatement("SELECT id from protocols where name = ?")){

            statement.setString(1, protocolName);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) protocolId = resultSet.getInt(1);
            resultSet.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        return protocolId;
    }

    public long getMessageTypeId(String messageTypeName){
        long messageTypeId=0;
        try(PreparedStatement statement=workerThreadLocal.get().getConnection()
                .prepareStatement("SELECT id from message_types where name = ?")){

            statement.setString(1, messageTypeName);
            ResultSet rs=statement.executeQuery();

            if(rs.next()) messageTypeId=rs.getLong(1);
            rs.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        return messageTypeId;
    }

    public long getTariffId(Transport transport, long partnerId){
        long tariffId=0;
        int transportId=getTransportId(transport.getDbName());

        try(PreparedStatement statement=workerThreadLocal.get().getConnection()
                .prepareStatement("SELECT id from tariffs where transport_id = ? " +
                                  "and partner_id = ?")){

            statement.setInt(1, transportId);
            statement.setLong(2, partnerId);

            ResultSet rs=statement.executeQuery();

            if(rs.next()) tariffId=rs.getLong(1);
            rs.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        return tariffId;
    }

    public long getTemplateId(String templateName, Transport transport){
        long templateId=0;
        int transportId=getTransportId(transport.getDbName());

        try(PreparedStatement statement=workerThreadLocal.get().getConnection()
                .prepareStatement("SELECT id from bulk_distr_templates " +
                                  "where name = ? and is_visible = 1 and transport_id = ?")){

            statement.setString(1, templateName);
            statement.setInt(2, transportId);

            ResultSet rs=statement.executeQuery();

            if(rs.next()) templateId=rs.getLong(1);
            rs.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        return templateId;
    }

    public long getTemplateId(String templateName){
        long templateId=0;

        try(PreparedStatement statement=workerThreadLocal.get().getConnection()
                .prepareStatement("SELECT id from bulk_distr_templates " +
                                  "where name = ? and is_visible = 1")){

            statement.setString(1, templateName);

            ResultSet rs=statement.executeQuery();

            if(rs.next()) templateId=rs.getLong(1);
            rs.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        return templateId;
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
