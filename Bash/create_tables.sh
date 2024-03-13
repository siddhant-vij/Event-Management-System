#!/bin/bash

# Database connection details
export PGUSER='postgres'
export PGPASSWORD='admin'
PSQL="psql --dbname=eventmgmt -t --no-align -c"

# Create tables in the PostgreSQL database
$PSQL "
DROP TABLE IF EXISTS registrations CASCADE;
DROP TABLE IF EXISTS attendees CASCADE;
DROP TABLE IF EXISTS events CASCADE;

CREATE TABLE IF NOT EXISTS events (
    event_id SERIAL PRIMARY KEY,
    event_name VARCHAR(255) NOT NULL,
    event_date DATE NOT NULL,
    event_location VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS attendees (
    attendee_id SERIAL PRIMARY KEY,
    attendee_name VARCHAR(255) NOT NULL,
    attendee_email VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS registrations (
    registration_id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL REFERENCES events(event_id),
    attendee_id INTEGER NOT NULL REFERENCES attendees(attendee_id),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(event_id, attendee_id)
);
"

echo "Tables created successfully."
