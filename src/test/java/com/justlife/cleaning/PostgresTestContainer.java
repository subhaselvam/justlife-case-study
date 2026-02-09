package com.justlife.cleaning;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;


public class PostgresTestContainer {

  private static PostgreSQLContainer<?> container;

  public static PostgreSQLContainer<?> getInstance() {
    if (container == null) {
      container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"))
          .withDatabaseName("justlife_db")
          .withUsername("test")
          .withPassword("test")
          .withReuse(false);
      container.start();
    }
    return container;
  }
}

