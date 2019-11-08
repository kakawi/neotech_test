package com.hlebon.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowRepository {

    private static final Logger log = LoggerFactory.getLogger(ShowRepository.class);

    private final ConnectionManager connectionManager;

    public ShowRepository(final ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public List<Timestamp> getData() {
        try {
            final Connection connection = connectionManager.getConnection();
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT time FROM neotech_time ORDER BY id");
            List<Timestamp> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(resultSet.getTimestamp("time"));
            }
            return result;
        } catch (final Exception e) {
            log.error("Something goes wrong");
            return Collections.emptyList();
        }
    }
}
