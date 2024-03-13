package com.postgres.services;

import java.sql.Connection;

import com.postgres.utils.CrudOperations;
import com.postgres.utils.CsvReader;

public class InsertData {
  private final CsvReader csvReader;
  private final Connection con;
  private String query = "";

  public InsertData() {
    this.csvReader = new CsvReader();
    readDataAndGeneratorQuery();
    this.con = getDbConnection();
    insertData();
    closeConnection();
  }

  private Connection getDbConnection() {
    String dbName = "eventmgmt";
    String user = "postgres";
    String password = "admin";
    return CrudOperations.connectToDB(dbName, user, password);
  }

  private void readDataAndGeneratorQuery() {
    this.csvReader.readDataFromCSV("events");
    this.csvReader.readDataFromCSV("attendees");
    this.csvReader.readDataFromCSV("registrations");

    for (var event : this.csvReader.getEvents()) {
      this.query += event.getInsertEventQuery();
    }
    for (var attendee : this.csvReader.getAttendees()) {
      this.query += attendee.getInsertAttendeeQuery();
    }
    for (var registration : this.csvReader.getRegistrations()) {
      this.query += registration.getInsertRegistrationQuery();
    }
  }

  private void insertData() {
    int count = CrudOperations.executeUpdateQuery(this.con, this.query);
    if (count != -1) {
      System.out.println("Data inserted successfully");
    }
  }

  private void closeConnection() {
    CrudOperations.closeConnection(this.con);
  }
}
