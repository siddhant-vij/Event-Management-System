package com.postgres.utils;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.postgres.models.Attendee;
import com.postgres.models.Event;
import com.postgres.models.Registration;

public class CsvReader {
  private final List<Event> events;
  private final List<Attendee> attendees;
  private final List<Registration> registrations;

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/uuuu H:m");

  private static final String EVENTS_CSV_FILE = "Problem/events.csv";
  private static final String ATTENDEES_CSV_FILE = "Problem/attendees.csv";
  private static final String REGISTRATIONS_CSV_FILE = "Problem/registrations.csv";

  public CsvReader() {
    this.events = new ArrayList<>();
    this.attendees = new ArrayList<>();
    this.registrations = new ArrayList<>();
  }

  public List<Event> getEvents() {
    return Collections.unmodifiableList(this.events);
  }

  public List<Attendee> getAttendees() {
    return Collections.unmodifiableList(this.attendees);
  }

  public List<Registration> getRegistrations() {
    return Collections.unmodifiableList(this.registrations);
  }

  public void readDataFromCSV(String tableName) {
    switch (tableName) {
      case "events":
        readFromCSV(EVENTS_CSV_FILE, t -> {
          try {
            readEventsFromCSV(t);
          } catch (IOException e) {
            System.out.println("Error reading Events from CSV file: " + e.getMessage());
          }
        });
        break;
      case "attendees":
        readFromCSV(ATTENDEES_CSV_FILE, t -> {
          try {
            readAttendeesFromCSV(t);
          } catch (IOException e) {
            System.out.println("Error reading Attendees from CSV file: " + e.getMessage());
          }
        });
        break;
      case "registrations":
        readFromCSV(REGISTRATIONS_CSV_FILE, t -> {
          try {
            readRegistrationsFromCSV(t);
          } catch (IOException e) {
            System.out.println("Error reading Registrations from CSV file: " + e.getMessage());
          }
        });
        break;
      default:
        System.out.println("Invalid table name: " + tableName);
    }
  }

  private void readFromCSV(String csvFilePath, Consumer<BufferedReader> reader) {
    try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
      reader.accept(br);
    } catch (IOException e) {
      System.out.println("Error reading from CSV file: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void readEventsFromCSV(BufferedReader br) throws IOException {
    String line;
    while ((line = br.readLine()) != null) {
      if (line.isBlank() || line.startsWith("event_name")) {
        continue;
      }
      String[] values = line.split(",");
      Event event = new Event(
          values[0].strip(),
          Date.valueOf(LocalDate.parse(
              values[1].strip(), formatter)),
          values[2].strip());
      this.events.add(event);
    }
  }

  private void readAttendeesFromCSV(BufferedReader br) throws IOException {
    String line;
    while ((line = br.readLine()) != null) {
      if (line.isBlank() || line.startsWith("attendee_name")) {
        continue;
      }
      String[] values = line.split(",");
      Attendee attendee = new Attendee(
          values[0].strip(),
          values[1].strip());
      this.attendees.add(attendee);
    }
  }

  private void readRegistrationsFromCSV(BufferedReader br) throws IOException {
    String line;
    while ((line = br.readLine()) != null) {
      if (line.isBlank() || line.startsWith("event_id")) {
        continue;
      }
      String[] values = line.split(",");
      Registration registration = new Registration(
          Integer.parseInt(values[0].strip()),
          Integer.parseInt(values[1].strip()),
          Date.valueOf(LocalDate.parse(
              values[2].strip(), formatter)));
      this.registrations.add(registration);
    }
  }
}
