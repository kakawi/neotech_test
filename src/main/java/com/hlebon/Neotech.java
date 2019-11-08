package com.hlebon;

import com.hlebon.repository.RepositoryQueue;
import com.hlebon.repository.RepositoryQueueImpl;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Timestamp;
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
        final DataSource dataSource = getDataSource();
        final RepositoryQueue repositoryQueue = getRepositoryQueue(dataSource);

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

    private static RepositoryQueueImpl getRepositoryQueue(final DataSource dataSource) {
        final RepositoryQueueImpl repositoryQueue = new RepositoryQueueImpl(dataSource);
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(repositoryQueue);
        return repositoryQueue;
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
