package com.postgres.models;

public class Attendee {
  private String attendee_name;
  private String attendee_email;

  public Attendee(String attendee_name, String attendee_email) {
    this.attendee_name = attendee_name;
    this.attendee_email = attendee_email;
  }

  public String getAttendeeName() {
    return attendee_name;
  }

  public String getAttendeeEmail() {
    return attendee_email;
  }

  public String getInsertAttendeeQuery() {
    return "INSERT INTO attendees (attendee_name, attendee_email) VALUES ('" +
        attendee_name + "', '" +
        attendee_email + "');";
  }
}
