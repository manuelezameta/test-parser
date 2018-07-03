package com.ef.persistence;

import com.ef.domain.IpBlockLine;
import com.ef.domain.LogLine;
import com.ef.utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ParserPersistence {

    public void saveIpBlockInDb(List<IpBlockLine> ipBlockLines) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = null;
        String query = "insert into ip_block_tb (ip, block_message) " +
                " values (?,?)";

        try {
            String truncateTable = "TRUNCATE TABLE ip_block_tb";
            statement = connection.prepareStatement(truncateTable);
            statement.executeUpdate();

            statement = connection.prepareStatement(query);

            for (int i = 0; i < ipBlockLines.size(); i++) {
                IpBlockLine ipBlockLine = ipBlockLines.get(i);
                statement.setString(1, ipBlockLine.getIp());
                statement.setString(2, ipBlockLine.getBlockMessage());

                statement.addBatch();

                if (i % 1000 == 0) statement.executeBatch();
            }

            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void saveLogInDb(List<LogLine> logLines) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = null;
        String query = "insert into log_tb (log_date, ip, request, status, user_agent) " +
                " values (?,?,?,?,?)";

        try {
            String truncateTable = "TRUNCATE TABLE log_tb";
            statement = connection.prepareStatement(truncateTable);
            statement.executeUpdate();

            statement = connection.prepareStatement(query);

            for (int i = 0; i < logLines.size(); i++) {
                LogLine logLine = logLines.get(i);
                statement.setTimestamp(1, Timestamp.valueOf(logLine.getDate()));
                statement.setString(2, logLine.getIp());
                statement.setString(3, logLine.getRequest());
                statement.setInt(4, logLine.getStatus());
                statement.setString(5, logLine.getUserAgent());

                statement.addBatch();

                if (i % 1000 == 0) statement.executeBatch();
            }

            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
