package com.postgres.services;

import java.sql.Connection;

import com.postgres.utils.CrudOperations;

public class CreateTables {
  private final String query;
  private final Connection con;

  public CreateTables() {
    this.query = queryGenerator();
    this.con = getDbConnection();
    createTables();
    closeConnection();
  }

  private String queryGenerator() {
    return "DROP TABLE IF EXISTS registrations CASCADE;\n" +
        "DROP TABLE IF EXISTS attendees CASCADE;\n" +
        "DROP TABLE IF EXISTS events CASCADE;\n" +
        "\n" +
        "CREATE TABLE IF NOT EXISTS events (\n" +
        " event_id SERIAL PRIMARY KEY,\n" +
        " event_name VARCHAR(255) NOT NULL,\n" +
        " event_date DATE NOT NULL,\n" +
        " event_location VARCHAR(255) NOT NULL\n" +
        ");\n" +
        "\n" +
        "CREATE TABLE IF NOT EXISTS attendees (\n" +
        " attendee_id SERIAL PRIMARY KEY,\n" +
        " attendee_name VARCHAR(255) NOT NULL,\n" +
        " attendee_email VARCHAR(255) NOT NULL\n" +
        ");\n" +
        "\n" +
        "CREATE TABLE IF NOT EXISTS registrations (\n" +
        " registration_id SERIAL PRIMARY KEY,\n" +
        " event_id INTEGER NOT NULL REFERENCES events(event_id),\n" +
        " attendee_id INTEGER NOT NULL REFERENCES attendees(attendee_id),\n" +
        " registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
        " UNIQUE(event_id, attendee_id)\n" +
        ");\n";
  }

  private Connection getDbConnection() {
    String dbName = "eventmgmt";
    String user = "postgres";
    String password = "admin";
    return CrudOperations.connectToDB(dbName, user, password);
  }

  private void createTables() {
    int count = CrudOperations.executeUpdateQuery(this.con, this.query);
    if (count != -1) {
      System.out.println("Tables created successfully");
    }
  }

  private void closeConnection() {
    CrudOperations.closeConnection(this.con);
  }
}
