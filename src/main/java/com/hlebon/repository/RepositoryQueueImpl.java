package com.hlebon.repository;

import com.hlebon.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RepositoryQueueImpl implements Runnable, RepositoryQueue {

    private static final Logger log = LoggerFactory.getLogger(RepositoryQueueImpl.class);

    private static final int CAPACITY = 25;
    private static final String INSERT_STATEMENT = "INSERT INTO neotech_time VALUES (null, ?)";
    private static final int GET_CONNECTION_DELAY = 5000;

    private final BlockingQueue<Timestamp> entities = new ArrayBlockingQueue<>(CAPACITY);
    private final DataSource dataSource;
    private Connection connection;

    public RepositoryQueueImpl(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean add(final Timestamp timestamp) {
        return entities.offer(timestamp);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Utils.delay(500);
                final Timestamp timestamp = entities.peek();
                if (timestamp == null) {
                    continue;
                }
                final Connection connection = getConnection();
                insertTimestamp(timestamp, connection);
                entities.remove();
            } catch (final Exception e) {
                log.error("Something goes wrong");
            }
        }
    }

    private void insertTimestamp(final Timestamp timestamp, final Connection connection) throws SQLException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT)) {
            preparedStatement.setTimestamp(1, timestamp);
            preparedStatement.execute();
        }
    }

    private Connection getConnection() throws SQLException {
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
