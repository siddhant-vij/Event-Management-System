package com.postgres.models;

import java.sql.Date;

public class Registration {
  private int event_id;
  private int attendee_id;
  private Date registered_at;

  public Registration(int event_id, int attendee_id, Date registered_at) {
    this.event_id = event_id;
    this.attendee_id = attendee_id;
    this.registered_at = registered_at;
  }

  public int getEventId() {
    return event_id;
  }

  public int getAttendeeId() {
    return attendee_id;
  }

  public Date getRegisteredAt() {
    return registered_at;
  }

  public String getInsertRegistrationQuery() {
    return "INSERT INTO registrations (event_id, attendee_id, registered_at) VALUES (" +
        event_id + ", " +
        attendee_id + ", '" +
        registered_at.toString() + "');";
  }
}
