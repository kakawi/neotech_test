package com.hlebon.repository;

import com.hlebon.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {

    private static final Logger log = LoggerFactory.getLogger(ConnectionManager.class);
    private static final int GET_CONNECTION_DELAY = 5000;

    private final DataSource dataSource;
    private Connection connection;

    public ConnectionManager(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws SQLException {
        while (connection == null || connection.isClosed()) {
            try {
                connection = dataSource.getConnection();
            } catch (final Exception e) {
                log.error("Can't get connection");
                Utils.delay(GET_CONNECTION_DELAY);
            }
        }
        return connection;
    }
}
