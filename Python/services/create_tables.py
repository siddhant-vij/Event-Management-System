from typing import Final

from psycopg2.extensions import connection

from utils.crud_operations import CrudOperations


class CreateTables:
    def __init__(self) -> None:
        self.query: Final[str] = self.query_generator()
        self.con: Final[connection] = self.get_db_connection()
        self.create_tables()
        self.close_connection()

    def query_generator(self) -> str:
        return """
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
    """

    def get_db_connection(self) -> connection:
        return CrudOperations.connect_to_db(
            db_name="eventmgmt",
            user="postgres",
            password="admin"
        )

    def create_tables(self) -> None:
        count = CrudOperations.execute_query(
            con=self.con,
            query=self.query
        )
        self.con.commit()
        if count != -1:
            print("Tables created successfully")

    def close_connection(self) -> None:
        self.con.close()
