#!/bin/bash

# Database connection details
export PGUSER='postgres'
export PGPASSWORD='admin'
PSQL="psql --dbname=eventmgmt -t --no-align -c"

# CSV File Paths
EVENTS_CSV="Problem/events.csv"
ATTENDEES_CSV="Problem/attendees.csv"
REGISTRATIONS_CSV="Problem/registrations.csv"

# Truncate DB Tables if data already exists
$PSQL "TRUNCATE events, attendees, registrations"

# Inserting Events data
cat $EVENTS_CSV | while IFS="," read -r event_name event_date event_location
do
  if [[ $event_name != "event_name" ]]
  then
    event_name=$(echo $event_name | xargs)
    event_date=$(echo $event_date | xargs)
    event_location=$(echo $event_location | xargs)
    
    $PSQL "INSERT INTO events(event_name, event_date, event_location) VALUES('$event_name', '$event_date', '$event_location')"
  fi
done

# Inserting Attendees data
cat $ATTENDEES_CSV | while IFS="," read -r attendee_name attendee_email
do
  if [[ $attendee_name != "attendee_name" ]]
  then
    attendee_name=$(echo $attendee_name | xargs)
    attendee_email=$(echo $attendee_email | xargs)

    $PSQL "INSERT INTO attendees(attendee_name, attendee_email) VALUES('$attendee_name', '$attendee_email')"
  fi
done

# Inserting Registrations data
cat $REGISTRATIONS_CSV | while IFS="," read -r event_id attendee_id registration_date
do
  if [[ $event_id != "event_id" ]]
  then
    event_id=$(echo $event_id | xargs)
    attendee_id=$(echo $attendee_id | xargs)
    registration_date=$(echo $registration_date | xargs)

    # Check if event_id exists
    event_exists=$($PSQL "SELECT EXISTS(SELECT 1 FROM events WHERE event_id = '$event_id');")

    # Check if attendee_id exists
    attendee_exists=$($PSQL "SELECT EXISTS(SELECT 1 FROM attendees WHERE attendee_id = '$attendee_id');")

    if [[ $event_exists =~ "t" ]]; then
        if [[ $attendee_exists =~ "t" ]]; then
            # Insert the registration
            $PSQL "INSERT INTO registrations(event_id, attendee_id, registered_at) VALUES($event_id, $attendee_id, '$registration_date')"
        else
            echo "Error: Attendee ID $attendee_id does not exist."
        fi
    else
        echo "Error: Event ID $event_id does not exist."
    fi
  fi
done

echo "Data inserted successfully."




# Testing `event_id` and `attendee_id` before inserting each registration can be seen as redundant if the database is already enforcing referential integrity through foreign key constraints. These constraints ensure that any `event_id` or `attendee_id` inserted into the `registrations` table must already exist in the `events` and `attendees` tables, respectively. If a foreign key constraint is violated (i.e., if the ID does not exist), the database will reject the insertion with an error.

# However, there might be cases where additional logic is needed beyond simple existence checks.