package com.postgres.models;

import java.sql.Date;

public class Event {
  private String event_name;
  private Date event_date;
  private String event_location;

  public Event(String event_name, Date event_date, String event_location) {
    this.event_name = event_name;
    this.event_date = event_date;
    this.event_location = event_location;
  }

  public String getEventName() {
    return event_name;
  }

  public Date getEventDate() {
    return event_date;
  }

  public String getEventLocation() {
    return event_location;
  }

  public String getInsertEventQuery() {
    return "INSERT INTO events (event_name, event_date, event_location) VALUES ('" +
        event_name + "', '" +
        event_date.toString() + "', '" +
        event_location + "');";
  }
}
