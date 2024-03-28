package services

import (
	"fmt"
	"strconv"
	"strings"

	"github.com/jackc/pgx/v5"
	"github.com/siddhant-vij/Event-Management-System/utils"
)

func SqlQueries() {
	// Connect to database
	connection, err := utils.ConnectToDB(dbName, user, password, host, port)
	if err != nil {
		fmt.Println(err)
	}

	queryStr := ""
	outputFilePath := "output.txt"

	queryStr = "List all events: "
	listAllEvents(connection, queryStr, outputFilePath)

	queryStr = "List all events in a particular city: \"Lalganj\": "
	listEventsByCity(connection, queryStr, outputFilePath, "Lalganj")

	queryStr = "List all events in a particular year and month: \"December, 2022\": "
	listEventsByYearAndMonth(connection, queryStr, outputFilePath)

	queryStr = "List all events in a particular date range: from \"2022-04-01\" to \"2022-06-30\": "
	listEventsByDateRange(connection, queryStr, outputFilePath, "2022-04-01", "2022-06-30")

	queryStr = "Find attendees registered for a specific event: event_name = \"eget nunc donec\": "
	findAttendeesForEvent(connection, queryStr, outputFilePath, "eget nunc donec")

	queryStr = "Find attendees registered for a specific event before a certain date: event_name = \"eget nunc donec\" and date = \"2022-12-01\": "
	findAttendeesForEventBeforeDate(connection, queryStr, outputFilePath, "eget nunc donec", "2022-12-01")

	queryStr = "Count the number of attendees for each event: "
	countAttendeesForEachEvent(connection, queryStr, outputFilePath)

	queryStr = "Update attendee information (e.g., email address) for a specific event: event_name = \"faucibus accumsan\": "
	updateAttendeeInfoForEvent(connection, queryStr, outputFilePath, "faucibus accumsan", "test@example.com")

	queryStr = "Delete an event and its associated registrations: event_name = \"duis bibendum\": "
	deleteEventAndRegistrations(connection, queryStr, outputFilePath, "duis bibendum")

	queryStr = "Delete an attendee and its associated registrations: attendee_name = \"Roch Batters\": "
	deleteAttendeeAndRegistrations(connection, queryStr, outputFilePath, "Roch Batters")

	queryStr = "List all attendees not registered for a specific event: event_name = \"eget nunc donec\": "
	listAttendeesNotRegisteredForEvent(connection, queryStr, outputFilePath, "eget nunc donec")

	queryStr = "Find events without any registrations: "
	findEventsWithoutRegistrations(connection, queryStr, outputFilePath)

	queryStr = "List events that have more than 20 attendees registered: "
	listEventsWithMoreThanNAttendees(connection, queryStr, outputFilePath, 20)

	queryStr = "Find events with no attendees registered after a certain date: date = \"2022-12-01\": "
	findEventsWithNoAttendeesAfterDate(connection, queryStr, outputFilePath, "2022-12-01")

	queryStr = "Retrieve the latest 3 events based on registration date for a particular city: city = \"Kanayannur\": "
	listLatestEventsByCity(connection, queryStr, outputFilePath, "Kanayannur", 3)

	queryStr = "Generate a list of attendees who have attended more than 4 events: "
	listAttendeesWithMoreThanNEvents(connection, queryStr, outputFilePath, 4)

	queryStr = "Find the year & month combination with the highest number of events scheduled and list all events for that month: "
	findMonthWithHighestNumberOfEvents(connection, queryStr, outputFilePath)

	queryStr = "Find the city with the highest number of events scheduled and list all events for that city: "
	findCityWithHighestNumberOfEvents(connection, queryStr, outputFilePath)

	queryStr = "Find the city with the highest number of events scheduled and list all the attendees who'll be attending these events with their dates of registration: "
	listAttendeesForCityWithMostEvents(connection, queryStr, outputFilePath)

	// Close database connection
	utils.CloseDB(connection)
}

func performAction(connection *pgx.Conn, queryStr string, query string, outputFilePath string) {
	query = strings.TrimSpace(query)
	if strings.HasPrefix(query, "SELECT") || strings.HasPrefix(query, "WITH") {
		rows, err := utils.ExecuteQuery(connection, query)
		if err != nil {
			fmt.Println(err)
			return
		}
		err = utils.PrintQueryResultToFile(queryStr, rows, outputFilePath)
		if err != nil {
			fmt.Println(err)
			return
		}
	} else {
		count, err := utils.ExecuteUpdateQuery(connection, query)
		if err != nil {
			fmt.Println(err)
			return
		}
		err = utils.PrintRowsAffectedToFile(queryStr, count, outputFilePath)
		if err != nil {
			fmt.Println(err)
			return
		}
	}
}

func listAllEvents(connection *pgx.Conn, queryStr, outputFilePath string) {
	query := `
	SELECT *
	FROM events`
	performAction(connection, queryStr, query, outputFilePath)
}

func listEventsByCity(connection *pgx.Conn, queryStr, outputFilePath, city string) {
	query := `
	SELECT *
	FROM events
	WHERE event_location = '` + city + `'`
	performAction(connection, queryStr, query, outputFilePath)
}

func listEventsByYearAndMonth(connection *pgx.Conn, queryStr, outputFilePath string) {
	query := `
	SELECT *
  FROM events
  WHERE event_date BETWEEN '2022-12-01' AND '2022-12-31'`
	performAction(connection, queryStr, query, outputFilePath)
}

func listEventsByDateRange(connection *pgx.Conn, queryStr, outputFilePath, startDate, endDate string) {
	query := `
	SELECT *
  FROM events
  WHERE event_date BETWEEN '` + startDate + `' AND '` + endDate + `'`
	performAction(connection, queryStr, query, outputFilePath)
}

func findAttendeesForEvent(connection *pgx.Conn, queryStr, outputFilePath, eventName string) {
	query := `
	SELECT attendees.attendee_name
  FROM attendees
  JOIN registrations
  ON attendees.attendee_id = registrations.attendee_id
  WHERE registrations.event_id = 
  (
    SELECT event_id
    FROM events
    WHERE event_name = '` + eventName + `'
  )`
	performAction(connection, queryStr, query, outputFilePath)
}

func findAttendeesForEventBeforeDate(connection *pgx.Conn, queryStr, outputFilePath, eventName, beforeDate string) {
	query := `
	SELECT attendees.attendee_name
  FROM attendees
  JOIN registrations
  ON attendees.attendee_id = registrations.attendee_id
  JOIN events
  ON registrations.event_id = events.event_id
  WHERE events.event_name = '` + eventName + `'
  AND registrations.registered_at < '` + beforeDate + `'`
	performAction(connection, queryStr, query, outputFilePath)
}

func countAttendeesForEachEvent(connection *pgx.Conn, queryStr, outputFilePath string) {
	query := `
	SELECT events.event_name,
  COUNT(registrations.attendee_id)
  FROM events
  LEFT JOIN registrations
  ON events.event_id = registrations.event_id
  GROUP BY events.event_name`
	performAction(connection, queryStr, query, outputFilePath)
}

func updateAttendeeInfoForEvent(connection *pgx.Conn, queryStr, outputFilePath, eventName, newEmail string) {
	query := `
	UPDATE attendees
  SET attendee_email = '` + newEmail + `'
  WHERE attendee_id IN
  (
    SELECT attendee_id
    FROM registrations
    WHERE event_id IN
    (
      SELECT event_id
      FROM events
      WHERE event_name = '` + eventName + `'
    )
  )`
	performAction(connection, queryStr, query, outputFilePath)
}

func deleteEventAndRegistrations(connection *pgx.Conn, queryStr, outputFilePath, eventName string) {
	query1 := `
	DELETE FROM registrations
  WHERE event_id =
  (
    SELECT event_id
    FROM events
    WHERE event_name = '` + eventName + `'
  )`
	query2 := `
	DELETE FROM events
  WHERE event_name = '` + eventName + `'`
	performAction(connection, queryStr, query1, outputFilePath)
	performAction(connection, queryStr, query2, outputFilePath)
}

func deleteAttendeeAndRegistrations(connection *pgx.Conn, queryStr, outputFilePath, attendeeName string) {
	query1 := `
	DELETE FROM registrations
  WHERE attendee_id =
  (
    SELECT attendee_id
    FROM attendees
    WHERE attendee_name = '` + attendeeName + `'
  )`
	query2 := `
	DELETE FROM attendees
  WHERE attendee_name = '` + attendeeName + `'`
	performAction(connection, queryStr, query1, outputFilePath)
	performAction(connection, queryStr, query2, outputFilePath)
}

func listAttendeesNotRegisteredForEvent(connection *pgx.Conn, queryStr, outputFilePath, eventName string) {
	query := `
	SELECT attendee_name
  FROM attendees
  WHERE attendee_id NOT IN
  (
    SELECT attendee_id
    FROM registrations
    WHERE event_id =
    (
      SELECT event_id
      FROM events
      WHERE event_name = '` + eventName + `'
    )
  )`
	performAction(connection, queryStr, query, outputFilePath)
}

func findEventsWithoutRegistrations(connection *pgx.Conn, queryStr, outputFilePath string) {
	query := `
	SELECT event_name
  FROM events
  WHERE event_id
  NOT IN
  (
    SELECT event_id
    FROM registrations
  )`
	performAction(connection, queryStr, query, outputFilePath)
}

func listEventsWithMoreThanNAttendees(connection *pgx.Conn, queryStr, outputFilePath string, attendeeThreshold int) {
	query := `
	SELECT event_name
  FROM events
  WHERE event_id IN
  (
    SELECT event_id
    FROM registrations
    GROUP BY event_id
    HAVING COUNT(*) > ` + strconv.Itoa(attendeeThreshold) + `
  )`
	performAction(connection, queryStr, query, outputFilePath)
}

func findEventsWithNoAttendeesAfterDate(connection *pgx.Conn, queryStr, outputFilePath, afterDate string) {
	query := `
	SELECT event_name
  FROM events
  WHERE event_id
  NOT IN
  (
    SELECT event_id
    FROM registrations
    WHERE registered_at > '` + afterDate + `'
  )`
	performAction(connection, queryStr, query, outputFilePath)
}

func listLatestEventsByCity(connection *pgx.Conn, queryStr, outputFilePath, city string, numberOfEvents int) {
	query := `
	SELECT DISTINCT events.event_name,
  registrations.registered_at
  FROM events
  JOIN registrations
  ON events.event_id = registrations.event_id
  WHERE events.event_location = '` + city + `'
  ORDER BY registrations.registered_at
  DESC LIMIT ` + strconv.Itoa(numberOfEvents)
	performAction(connection, queryStr, query, outputFilePath)
}

func listAttendeesWithMoreThanNEvents(connection *pgx.Conn, queryStr, outputFilePath string, eventCount int) {
	query := `
	SELECT attendees.attendee_name
  FROM attendees
  JOIN registrations
  ON attendees.attendee_id = registrations.attendee_id
  GROUP BY attendees.attendee_name
  HAVING COUNT(*) > ` + strconv.Itoa(eventCount)
	performAction(connection, queryStr, query, outputFilePath)
}

func findMonthWithHighestNumberOfEvents(connection *pgx.Conn, queryStr, outputFilePath string) {
	query := `
	SELECT to_char(event_date, 'YYYY-MM') AS month_year,
  array_agg(event_name)
  FROM events
  GROUP BY month_year
  ORDER BY COUNT(*)
  DESC LIMIT 1`
	performAction(connection, queryStr, query, outputFilePath)
}

func findCityWithHighestNumberOfEvents(connection *pgx.Conn, queryStr, outputFilePath string) {
	query := `
	SELECT event_location,
  array_agg(event_name)
  FROM events
  GROUP BY event_location
  ORDER BY COUNT(*)
  DESC LIMIT 1`
	performAction(connection, queryStr, query, outputFilePath)
}

func listAttendeesForCityWithMostEvents(connection *pgx.Conn, queryStr, outputFilePath string) {
	query := `
	WITH CityRank AS (
    SELECT event_location, COUNT(*) AS event_count
    FROM events
    GROUP BY event_location
    ORDER BY event_count DESC
    LIMIT 1
  )
  SELECT e.event_location, a.attendee_name, r.registered_at
  FROM events e
  JOIN registrations r
  ON e.event_id = r.event_id
  JOIN attendees a
  ON r.attendee_id = a.attendee_id
  WHERE e.event_location =
  (
    SELECT event_location
    FROM CityRank
  )
  ORDER BY r.registered_at`
	performAction(connection, queryStr, query, outputFilePath)
}
