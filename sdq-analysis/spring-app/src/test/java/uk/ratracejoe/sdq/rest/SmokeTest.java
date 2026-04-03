package uk.ratracejoe.sdq.rest;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

class SmokeTest {

  @Test
  void canStartContainer() {
    PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16");
    pg.start();
    System.out.println("JDBC URL = " + pg.getJdbcUrl());
  }
}
