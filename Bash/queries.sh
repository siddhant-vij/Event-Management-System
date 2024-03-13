#!/bin/bash

# Database connection details
export PGUSER='postgres'
export PGPASSWORD='admin'
PSQL="psql --dbname=eventmgmt -t --no-align -c"

# Query Operations
echo -e "List all events:" > output.txt
echo "$(
  $PSQL "SELECT *
  FROM events"
)" >> output.txt



echo -e "\n\n\n\n\nList all events in a particular city: 'Lalganj'" >> output.txt
echo "$(
  $PSQL "SELECT *
  FROM events
  WHERE event_location = 'Lalganj'"
)" >> output.txt



echo -e "\n\n\n\n\nList all events in a particular year and month: 'December, 2022'" >> output.txt
echo "$(
  $PSQL "SELECT *
  FROM events
  WHERE event_date BETWEEN '2022-12-01' AND '2022-12-31'"
)" >> output.txt



echo -e "\n\n\n\n\nList all events in a particular date range: from '2022-04-01' to '2022-06-30'" >> output.txt
echo "$(
  $PSQL "SELECT *
  FROM events
  WHERE event_date BETWEEN '2022-04-01' AND '2022-06-30'"
)" >> output.txt



echo -e "\n\n\n\n\nFind attendees registered for a specific event: event_name = 'eget nunc donec'" >> output.txt
echo "$(
  $PSQL "SELECT attendees.attendee_name
  FROM attendees
  JOIN registrations
  ON attendees.attendee_id = registrations.attendee_id
  WHERE registrations.event_id = 
  (
    SELECT event_id
    FROM events
    WHERE event_name = 'eget nunc donec'
  )"
)" >> output.txt



echo -e "\n\n\n\n\nFind attendees registered for a specific event before a certain date: event_name = 'eget nunc donec' and date = '2022-12-01'" >> output.txt
echo "$(
  $PSQL "SELECT attendees.attendee_name
  FROM attendees
  JOIN registrations
  ON attendees.attendee_id = registrations.attendee_id
  JOIN events
  ON registrations.event_id = events.event_id
  WHERE events.event_name = 'eget nunc donec'
  AND registrations.registered_at < '2022-12-01'"
)" >> output.txt



echo -e "\n\n\n\n\nCount the number of attendees for each event:" >> output.txt
echo "$(
  $PSQL "SELECT events.event_name,
  COUNT(registrations.attendee_id)
  FROM events
  LEFT JOIN registrations
  ON events.event_id = registrations.event_id
  GROUP BY events.event_name"
)" >> output.txt



echo -e "\n\n\n\n\nUpdate all attendees information (e.g., email address) for a specific event: event_name = 'faucibus accumsan'" >> output.txt
echo "$(
  $PSQL "UPDATE attendees
  SET attendee_email = 'test@example.com'
  WHERE attendee_id IN
  (
    SELECT attendee_id
    FROM registrations
    WHERE event_id IN
    (
      SELECT event_id
      FROM events
      WHERE event_name = 'faucibus accumsan'
    )
  )"
)" >> output.txt



echo -e "\n\n\n\n\nDelete an event and its associated registrations: event_name = 'duis bibendum'" >> output.txt
echo "$(
  $PSQL "DELETE FROM registrations
  WHERE event_id =
  (
    SELECT event_id
    FROM events
    WHERE event_name = 'duis bibendum'
  )"
)" >> output.txt
echo "$(
  $PSQL "DELETE FROM events
  WHERE event_name = 'duis bibendum'"
)" >> output.txt


echo -e "\n\n\n\n\nDelete an attendee and its associated registrations: attendee_name = 'Roch Batters'" >> output.txt
echo "$(
  $PSQL "DELETE FROM registrations
  WHERE attendee_id =
  (
    SELECT attendee_id
    FROM attendees
    WHERE attendee_name = 'Roch Batters'
  )"
)" >> output.txt
echo "$(
  $PSQL "DELETE FROM attendees
  WHERE attendee_name = 'Roch Batters'"
)" >> output.txt



echo -e "\n\n\n\n\nList all attendees not registered for a specific event: event_name = 'eget nunc donec'" >> output.txt
echo "$(
  $PSQL "SELECT attendee_name
  FROM attendees
  WHERE attendee_id NOT IN
  (
    SELECT attendee_id
    FROM registrations
    WHERE event_id =
    (
      SELECT event_id
      FROM events
      WHERE event_name = 'eget nunc donec'
    )
  )"
)" >> output.txt



echo -e "\n\n\n\n\nFind events without any registrations:" >> output.txt
echo "$(
  $PSQL "SELECT event_name
  FROM events
  WHERE event_id
  NOT IN
  (
    SELECT event_id
    FROM registrations
  )"
)" >> output.txt



echo -e "\n\n\n\n\nList events that have more than 20 attendees registered:" >> output.txt
echo "$(
  $PSQL "SELECT event_name
  FROM events
  WHERE event_id IN
  (
    SELECT event_id
    FROM registrations
    GROUP BY event_id
    HAVING COUNT(*) > 20
  )"
)" >> output.txt



echo -e "\n\n\n\n\nFind events with no attendees registered after a certain date: date = '2022-12-01'" >> output.txt
echo "$(
  $PSQL "SELECT event_name
  FROM events
  WHERE event_id
  NOT IN
  (
    SELECT event_id
    FROM registrations
    WHERE registered_at > '2022-12-01'
  )"
)" >> output.txt



echo -e "\n\n\n\n\nRetrieve the latest 3 events based on registration date for a particular city: city = 'Kanayannur'" >> output.txt
echo "$(
  $PSQL "SELECT DISTINCT events.event_name,
  registrations.registered_at
  FROM events
  JOIN registrations
  ON events.event_id = registrations.event_id
  WHERE events.event_location = 'Kanayannur'
  ORDER BY registrations.registered_at
  DESC LIMIT 3"
)" >> output.txt


echo -e "\n\n\n\n\nGenerate a list of attendees who have attended more than 4 events:" >> output.txt
echo "$(
  $PSQL "SELECT attendees.attendee_name
  FROM attendees
  JOIN registrations
  ON attendees.attendee_id = registrations.attendee_id
  GROUP BY attendees.attendee_name
  HAVING COUNT(*) > 4"
)" >> output.txt


echo -e "\n\n\n\n\nFind the year & month combination with the highest number of events scheduled and list all events for that month:" >> output.txt
echo "$(
  $PSQL "SELECT to_char(event_date, 'YYYY-MM') AS month_year,
  array_agg(event_name)
  FROM events
  GROUP BY month_year
  ORDER BY COUNT(*)
  DESC LIMIT 1"
)" >> output.txt


echo -e "\n\n\n\n\nFind the city with the highest number of events scheduled and list all events for that city:" >> output.txt
echo "$(
  $PSQL "SELECT event_location,
  array_agg(event_name)
  FROM events
  GROUP BY event_location
  ORDER BY COUNT(*)
  DESC LIMIT 1"
)" >> output.txt


echo -e "\n\n\n\n\nFind the city with the highest number of events scheduled and list all the attendees who'll be attending these events with their dates of registration:" >> output.txt
echo "$(
  $PSQL "WITH CityRank AS (
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
  ORDER BY r.registered_at"
)" >> output.txt
