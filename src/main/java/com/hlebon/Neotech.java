package com.hlebon;

import com.hlebon.repository.ConnectionManager;
import com.hlebon.repository.RepositoryQueue;
import com.hlebon.repository.RepositoryQueueImpl;
import com.hlebon.repository.ShowRepository;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Neotech {

    private static final Logger log = LoggerFactory.getLogger(Neotech.class);

    private static final String MYSQL_DATABASE = "neotech";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3308/" + MYSQL_DATABASE;
    private static final String MYSQL_USER = "neotech_user";
    private static final String MYSQL_PASSWORD = "neotech_pass";
    private static final int DELAY_BETWEEN_MESSAGES = 1000;

    public static void main(final String[] args) {
        if (args.length == 0) {
            saveStrategy();
        } else if (args.length == 1) {
            if ("-p".equals(args[0])) {
                showStrategy();
            } else {
                log.error("Unknown parameter " + args[0]);
            }
        } else {
            log.error("Unknown parameters");
        }
    }

    private static void showStrategy() {
        final ConnectionManager connectionManager = getConnectionManager(getDataSource());
        final ShowRepository showRepository = getShowRepository(connectionManager);

        final List<Timestamp> data = showRepository.getData();
        for (final Timestamp timestamp : data) {
            log.info(timestamp.toString());
        }
    }

    private static ShowRepository getShowRepository(final ConnectionManager connectionManager) {
        return new ShowRepository(connectionManager);
    }

    private static void saveStrategy() {
        final ConnectionManager connectionManager = getConnectionManager(getDataSource());
        final RepositoryQueue repositoryQueue = getRepositoryQueue(connectionManager);

        while (true) {
            final boolean isAdded = repositoryQueue.add(new Timestamp(System.currentTimeMillis()));
            if (isAdded) {
                log.info("Added in Queue Successfully");
            } else {
                log.error("The queue is FULL");
            }
            Utils.delay(DELAY_BETWEEN_MESSAGES);
        }
    }

    private static RepositoryQueueImpl getRepositoryQueue(final ConnectionManager connectionManager) {
        final RepositoryQueueImpl repositoryQueue = new RepositoryQueueImpl(connectionManager);
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(repositoryQueue);
        return repositoryQueue;
    }

    private static ConnectionManager getConnectionManager(final DataSource dataSource) {
        return new ConnectionManager(dataSource);
    }

    private static MysqlDataSource getDataSource() {
        final MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(MYSQL_URL);
        mysqlDataSource.setUser(MYSQL_USER);
        mysqlDataSource.setPassword(MYSQL_PASSWORD);
        mysqlDataSource.setDatabaseName(MYSQL_DATABASE);
        return mysqlDataSource;
    }
}
