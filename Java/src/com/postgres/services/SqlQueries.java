package com.postgres.services;

import java.sql.Connection;
import java.sql.ResultSet;

import com.postgres.utils.CrudOperations;

public class SqlQueries {
  private final Connection con;
  private final String dbName = "eventmgmt";
  private final String user = "postgres";
  private final String password = "admin";
  private final String outputFilePath = "output.txt";
  private String queryStr = null;

  public SqlQueries() {
    // Connect to DB
    this.con = CrudOperations.connectToDB(
        dbName, user, password);

    queryStr = "List all events: ";
    listAllEvents(con);

    queryStr = "List all events in a particular city: \"Lalganj\": ";
    listEventsByCity(con, "Lalganj");

    queryStr = "List all events in a particular year and month: \"December, 2022\": ";
    listEventsByYearAndMonth(con);

    queryStr = "List all events in a particular date range: from \"2022-04-01\" to \"2022-06-30\": ";
    listEventsByDateRange(con, "2022-04-01", "2022-06-30");

    queryStr = "Find attendees registered for a specific event: event_name = \"eget nunc donec\": ";
    findAttendeesForEvent(con, "eget nunc donec");

    queryStr = "Find attendees registered for a specific event before a certain date: event_name = \"eget nunc donec\" and date = \"2022-12-01\": ";
    findAttendeesForEventBeforeDate(con, "eget nunc donec", "2022-12-01");

    queryStr = "Count the number of attendees for each event: ";
    countAttendeesForEachEvent(con);

    queryStr = "Update attendee information (e.g., email address) for a specific event: event_name = \"faucibus accumsan\": ";
    updateAttendeeInfoForEvent(con, "faucibus accumsan", "test@example.com");

    queryStr = "Delete an event and its associated registrations: event_name = \"duis bibendum\": ";
    deleteEventAndRegistrations(con, "duis bibendum");

    queryStr = "Delete an attendee and its associated registrations: attendee_name = \"Roch Batters\": ";
    deleteAttendeeAndRegistrations(con, "Roch Batters");

    queryStr = "List all attendees not registered for a specific event: event_name = \"eget nunc donec\": ";
    listAttendeesNotRegisteredForEvent(con, "eget nunc donec");

    queryStr = "Find events without any registrations: ";
    findEventsWithoutRegistrations(con);

    queryStr = "List events that have more than 20 attendees registered: ";
    listEventsWithMoreThanNAttendees(con, 20);

    queryStr = "Find events with no attendees registered after a certain date: date = \"2022-12-01\": ";
    findEventsWithNoAttendeesAfterDate(con, "2022-12-01");

    queryStr = "Retrieve the latest 3 events based on registration date for a particular city: city = \"Kanayannur\": ";
    listLatestEventsByCity(con, "Kanayannur", 3);

    queryStr = "Generate a list of attendees who have attended more than 4 events: ";
    listAttendeesWithMoreThanNEvents(con, 4);

    queryStr = "Find the year & month combination with the highest number of events scheduled and list all events for that month: ";
    findMonthWithHighestNumberOfEvents(con);

    queryStr = "Find the city with the highest number of events scheduled and list all events for that city: ";
    findCityWithHighestNumberOfEvents(con);

    queryStr = "Find the city with the highest number of events scheduled and list all the attendees who'll be attending these events with their dates of registration: ";
    listAttendeesForCityWithMostEvents(con);

    // Close database connection
    CrudOperations.closeConnection(con);
  }

  private void performAction(Connection con, String queryStr, String query) {
    if (query.trim().toUpperCase().startsWith("SELECT") || query.trim().toUpperCase().startsWith("WITH")) {
      ResultSet rs = CrudOperations.executeQuery(con, query);
      CrudOperations.printQueryResultToFile(queryStr, rs, outputFilePath);
    } else {
      int count = CrudOperations.executeUpdateQuery(con, query);
      CrudOperations.printQueryResultToFile(queryStr, count, outputFilePath);
    }
  }

  private void listAllEvents(Connection con) {
    String query = "SELECT *\r\n" + //
        "  FROM events";
    performAction(con, queryStr, query);
  }

  private void listEventsByCity(Connection con, String city) {
    String query = "SELECT *\r\n" + //
        "  FROM events\r\n" + //
        "  WHERE event_location = '" + city + "'";
    performAction(con, queryStr, query);
  }

  private void listEventsByYearAndMonth(Connection con) {
    String query = "SELECT *\r\n" + //
        "  FROM events\r\n" + //
        "  WHERE event_date BETWEEN '2022-12-01' AND '2022-12-31'";
    performAction(con, queryStr, query);
  }

  private void listEventsByDateRange(Connection con, String startDate, String endDate) {
    String query = "SELECT *\r\n" + //
        "  FROM events\r\n" + //
        "  WHERE event_date BETWEEN '" + startDate + "' AND '" + endDate + "'";
    performAction(con, queryStr, query);
  }

  private void findAttendeesForEvent(Connection con, String eventName) {
    String query = "SELECT attendees.attendee_name\r\n" + //
        "  FROM attendees\r\n" + //
        "  JOIN registrations\r\n" + //
        "  ON attendees.attendee_id = registrations.attendee_id\r\n" + //
        "  WHERE registrations.event_id = \r\n" + //
        "  (\r\n" + //
        "    SELECT event_id\r\n" + //
        "    FROM events\r\n" + //
        "    WHERE event_name = '" + eventName + "'\r\n" + //
        "  )";
    performAction(con, queryStr, query);
  }

  private void findAttendeesForEventBeforeDate(Connection con, String eventName, String beforeDate) {
    String query = "SELECT attendees.attendee_name\r\n" + //
        "  FROM attendees\r\n" + //
        "  JOIN registrations\r\n" + //
        "  ON attendees.attendee_id = registrations.attendee_id\r\n" + //
        "  JOIN events\r\n" + //
        "  ON registrations.event_id = events.event_id\r\n" + //
        "  WHERE events.event_name = '" + eventName + "'\r\n" + //
        "  AND registrations.registered_at < '" + beforeDate + "'";
    performAction(con, queryStr, query);
  }

  private void countAttendeesForEachEvent(Connection con) {
    String query = "SELECT events.event_name,\r\n" + //
        "  COUNT(registrations.attendee_id)\r\n" + //
        "  FROM events\r\n" + //
        "  LEFT JOIN registrations\r\n" + //
        "  ON events.event_id = registrations.event_id\r\n" + //
        "  GROUP BY events.event_name";
    performAction(con, queryStr, query);
  }

  private void updateAttendeeInfoForEvent(Connection con, String eventName, String newEmail) {
    String query = "UPDATE attendees\r\n" + //
        "  SET attendee_email = '" + newEmail + "'\r\n" + //
        "  WHERE attendee_id IN\r\n" + //
        "  (\r\n" + //
        "    SELECT attendee_id\r\n" + //
        "    FROM registrations\r\n" + //
        "    WHERE event_id IN\r\n" + //
        "    (\r\n" + //
        "      SELECT event_id\r\n" + //
        "      FROM events\r\n" + //
        "      WHERE event_name = '" + eventName + "'\r\n" + //
        "    )\r\n" + //
        "  )";
    performAction(con, queryStr, query);
  }

  private void deleteEventAndRegistrations(Connection con, String eventName) {
    String query1 = "DELETE FROM registrations\r\n" + //
        "  WHERE event_id =\r\n" + //
        "  (\r\n" + //
        "    SELECT event_id\r\n" + //
        "    FROM events\r\n" + //
        "    WHERE event_name = '" + eventName + "'\r\n" + //
        "  )";
    String query2 = "DELETE FROM events\r\n" + //
        "  WHERE event_name = '" + eventName + "'";
    performAction(con, queryStr, query1);
    performAction(con, queryStr, query2);
  }

  private void deleteAttendeeAndRegistrations(Connection con, String attendeeName) {
    String query1 = "DELETE FROM registrations\r\n" + //
        "  WHERE attendee_id =\r\n" + //
        "  (\r\n" + //
        "    SELECT attendee_id\r\n" + //
        "    FROM attendees\r\n" + //
        "    WHERE attendee_name = '" + attendeeName + "'\r\n" + //
        "  )";
    String query2 = "DELETE FROM attendees\r\n" + //
        "  WHERE attendee_name = '" + attendeeName + "'";
    performAction(con, queryStr, query1);
    performAction(con, queryStr, query2);
  }

  private void listAttendeesNotRegisteredForEvent(Connection con, String eventName) {
    String query = "SELECT attendee_name\r\n" + //
        "  FROM attendees\r\n" + //
        "  WHERE attendee_id NOT IN\r\n" + //
        "  (\r\n" + //
        "    SELECT attendee_id\r\n" + //
        "    FROM registrations\r\n" + //
        "    WHERE event_id =\r\n" + //
        "    (\r\n" + //
        "      SELECT event_id\r\n" + //
        "      FROM events\r\n" + //
        "      WHERE event_name = '" + eventName + "'\r\n" + //
        "    )\r\n" + //
        "  )";
    performAction(con, queryStr, query);
  }

  private void findEventsWithoutRegistrations(Connection con) {
    String query = "SELECT event_name\r\n" + //
        "  FROM events\r\n" + //
        "  WHERE event_id\r\n" + //
        "  NOT IN\r\n" + //
        "  (\r\n" + //
        "    SELECT event_id\r\n" + //
        "    FROM registrations\r\n" + //
        "  )";
    performAction(con, queryStr, query);
  }

  private void listEventsWithMoreThanNAttendees(Connection con, int attendeeThreshold) {
    String query = "SELECT event_name\r\n" + //
        "  FROM events\r\n" + //
        "  WHERE event_id IN\r\n" + //
        "  (\r\n" + //
        "    SELECT event_id\r\n" + //
        "    FROM registrations\r\n" + //
        "    GROUP BY event_id\r\n" + //
        "    HAVING COUNT(*) > " + attendeeThreshold + "\r\n" + //
        "  )";
    performAction(con, queryStr, query);
  }

  private void findEventsWithNoAttendeesAfterDate(Connection con, String afterDate) {
    String query = "SELECT event_name\r\n" + //
        "  FROM events\r\n" + //
        "  WHERE event_id\r\n" + //
        "  NOT IN\r\n" + //
        "  (\r\n" + //
        "    SELECT event_id\r\n" + //
        "    FROM registrations\r\n" + //
        "    WHERE registered_at > '" + afterDate + "'\r\n" + //
        "  )";
    performAction(con, queryStr, query);
  }

  private void listLatestEventsByCity(Connection con, String city, int numberOfEvents) {
    String query = "SELECT DISTINCT events.event_name,\r\n" + //
        "  registrations.registered_at\r\n" + //
        "  FROM events\r\n" + //
        "  JOIN registrations\r\n" + //
        "  ON events.event_id = registrations.event_id\r\n" + //
        "  WHERE events.event_location = '" + city + "'\r\n" + //
        "  ORDER BY registrations.registered_at\r\n" + //
        "  DESC LIMIT " + numberOfEvents;
    performAction(con, queryStr, query);
  }

  private void listAttendeesWithMoreThanNEvents(Connection con, int eventCount) {
    String query = "SELECT attendees.attendee_name\r\n" + //
        "  FROM attendees\r\n" + //
        "  JOIN registrations\r\n" + //
        "  ON attendees.attendee_id = registrations.attendee_id\r\n" + //
        "  GROUP BY attendees.attendee_name\r\n" + //
        "  HAVING COUNT(*) > " + eventCount;
    performAction(con, queryStr, query);
  }

  private void findMonthWithHighestNumberOfEvents(Connection con) {
    String query = "SELECT to_char(event_date, 'YYYY-MM') AS month_year,\r\n" + //
        "  array_agg(event_name)\r\n" + //
        "  FROM events\r\n" + //
        "  GROUP BY month_year\r\n" + //
        "  ORDER BY COUNT(*)\r\n" + //
        "  DESC LIMIT 1";
    performAction(con, queryStr, query);
  }

  private void findCityWithHighestNumberOfEvents(Connection con) {
    String query = "SELECT event_location,\r\n" + //
        "  array_agg(event_name)\r\n" + //
        "  FROM events\r\n" + //
        "  GROUP BY event_location\r\n" + //
        "  ORDER BY COUNT(*)\r\n" + //
        "  DESC LIMIT 1";
    performAction(con, queryStr, query);
  }

  private void listAttendeesForCityWithMostEvents(Connection con) {
    String query = "WITH CityRank AS (\r\n" + //
        "    SELECT event_location, COUNT(*) AS event_count\r\n" + //
        "    FROM events\r\n" + //
        "    GROUP BY event_location\r\n" + //
        "    ORDER BY event_count DESC\r\n" + //
        "    LIMIT 1\r\n" + //
        "  )\r\n" + //
        "  SELECT e.event_location, a.attendee_name, r.registered_at\r\n" + //
        "  FROM events e\r\n" + //
        "  JOIN registrations r\r\n" + //
        "  ON e.event_id = r.event_id\r\n" + //
        "  JOIN attendees a\r\n" + //
        "  ON r.attendee_id = a.attendee_id\r\n" + //
        "  WHERE e.event_location =\r\n" + //
        "  (\r\n" + //
        "    SELECT event_location\r\n" + //
        "    FROM CityRank\r\n" + //
        "  )\r\n" + //
        "  ORDER BY r.registered_at";
    performAction(con, queryStr, query);
  }
}
