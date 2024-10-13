package com.jw.integration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;

@ApplicationScoped
public class ContainerITConfiguration implements QuarkusTestResourceLifecycleManager {

    PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

    @Override
    public Map<String, String> start() {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(("postgres"));
        POSTGRESQL_CONTAINER.start();
        Map<String, String> conf = new HashMap<>();
        conf.put("quarkus.datasource.jdbc.url", POSTGRESQL_CONTAINER.getJdbcUrl());
        conf.put("quarkus.datasource.username", POSTGRESQL_CONTAINER.getUsername());
        conf.put("quarkus.datasource.password", POSTGRESQL_CONTAINER.getPassword());
        return conf;
    }

    @Override
    public void stop() {
        POSTGRESQL_CONTAINER.stop();
    }

    @Container
    public static RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq:3.13-management")
                    .withReuse(true)
                    .withQueue("unprocessed-products")
                    .withQueue("finalized-products")
                    .withQueue("update-products")
                    .withExchange("unprocessed-orders", "topic")
                    .withExchange("unprocessed-products", "topic")
                    .withExchange("finalized-products", "topic")
                    .withBinding("unprocessed-products", "unprocessed-products")
                    .withBinding("finalized-products", "finalized-products");

    static {
        rabbitMQContainer.start();
    }
}
