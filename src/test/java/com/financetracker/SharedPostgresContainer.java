package com.financetracker;

import org.testcontainers.containers.PostgreSQLContainer;

public final class SharedPostgresContainer {

    public static final PostgreSQLContainer<?> INSTANCE =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        INSTANCE.start();
    }

    private SharedPostgresContainer() {}
}
