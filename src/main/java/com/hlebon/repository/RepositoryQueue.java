package com.hlebon.repository;

import java.sql.Timestamp;

public interface RepositoryQueue {

    boolean add(Timestamp timestamp);
}
